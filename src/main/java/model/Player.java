package model;

import dto.AuthInfo;
import dto.ClientMessage;
import dto.ServerMessage;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import ui.GuiInteractor;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class Player extends Thread {
    private final ClientMessage CLOSE_REQUEST = new ClientMessage("", -1, -1, true);
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private AuthInfo authInfo;
    private Color color;
    private final GuiInteractor guiInteractor;
    private boolean isMoveAllowed;
    private BlockingQueue<AuthInfo> authInfoBlockingQueue;

    public Player(GuiInteractor guiInteractor, BlockingQueue<AuthInfo> authInfoBlockingQueue) {
        this.guiInteractor = guiInteractor;
        this.authInfoBlockingQueue = authInfoBlockingQueue;
        start();
    }

    @Override
    public void run() {
        boolean isThreadShouldBeClosedAheadOfSchedule = false;
        try {
            clientSocket = new Socket("localhost", 5635);
            isMoveAllowed = true;
            out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            MessageExchanger.send(out, getAuthInfo());
            in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            String str;
            do {
                str = MessageExchanger.receive(in, String.class);
                if (str.equals("Цвет занят")) {
                    Platform.runLater(guiInteractor::colorHasTaken);
                    MessageExchanger.send(out, getAuthInfo());
                }
            } while (str.equals("Цвет занят"));
            Platform.runLater(guiInteractor::closeAuthWindow);
            ServerMessage introduceMessage = MessageExchanger.receive(in, ServerMessage.class);
            Platform.runLater(() -> {
                if (introduceMessage.number() == 1) {
                    guiInteractor.showWaitForAPlayerWindow();
                    guiInteractor.setPlayerNames("(Вы) " + introduceMessage.status() + " : ?");
                } else {
                    guiInteractor.setStatusLabel("Ожидайте хода соперника");
                    guiInteractor.setPlayerNames(introduceMessage.status() + " : " + introduceMessage.name() + " (Вы)");
                }
            });
            if (introduceMessage.number() == 1) {
                ServerMessage introduceMessage2 = MessageExchanger.receive(in, ServerMessage.class);
                if (Objects.isNull(introduceMessage2)) {
                    isThreadShouldBeClosedAheadOfSchedule = true;
                } else {
                    Platform.runLater(() -> {
                        guiInteractor.closeWaitForAPlayerWindow();
                        guiInteractor.setStatusLabel("Ваш ход");
                        guiInteractor.setPlayerNames("(Вы) " + introduceMessage2.status() + " : " + introduceMessage2.name());
                    });
                }
            }
            if (!isThreadShouldBeClosedAheadOfSchedule) {
            while (true) {
                if (clientSocket.isClosed()) {
                    break;
                }
                ServerMessage serverMessage = MessageExchanger.receive(in, ServerMessage.class);
                if (clientSocket.isClosed()) {
                    break;
                }
                Platform.runLater(() -> {
                    if (serverMessage.status().equals("Соперник покинул игру")) {
                        guiInteractor.clearField();
                        guiInteractor.opponentLeftTheGame();
                    } else {
                        if (!serverMessage.name().equals(authInfo.name())) {
                            guiInteractor.addCircle(
                                    serverMessage.y(),
                                    serverMessage.x(), new Color(
                                            serverMessage.red(),
                                            serverMessage.green(),
                                            serverMessage.blue(),
                                            serverMessage.opacity()));
                            isMoveAllowed = true;
                            guiInteractor.setStatusLabel("Ваш ход");
                        } else {
                            guiInteractor.setStatusLabel("Ожидайте хода соперника");
                        }
                        if (serverMessage.status().equals("Ничья")) {
                            guiInteractor.draw();
                            guiInteractor.clearField();
                        }
                        if (serverMessage.status().equals(getPlayerName())) {
                            guiInteractor.youAreAWinner();
                            guiInteractor.clearField();
                        }
                        if (!(serverMessage.status().equals("Ничья") || serverMessage.status().isEmpty() || serverMessage.status().equals(getPlayerName()))) {
                            guiInteractor.youAreALoser();
                            guiInteractor.clearField();
                        }
                        guiInteractor.setAccount(serverMessage.account());
                    }
                });
                if (serverMessage.status().equals("Соперник покинул игру")) {
                    break;
                }
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AuthInfo getAuthInfo() {
        try {
            authInfo = authInfoBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        color = new Color(
                authInfo.red(),
                authInfo.green(),
                authInfo.blue(),
                authInfo.opacity());
        return authInfo;
    }

//    public <T> T receiveMessage(Class<T> clazz) {
//        if (in == null) return null;
//        return MessageExchanger.receive(in, clazz);
//    }

    public <T> void sendMessage(T obj) {
        try {
            MessageExchanger.send(out, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Color getPlayerColor() {
        return color;
    }

    public void close() {
        try {
            MessageExchanger.send(out, CLOSE_REQUEST);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return authInfo.name();
    }

    public boolean isMoveAllowed() {
        return isMoveAllowed;
    }

    public void setMoveAllowed(boolean moveAllowed) {
        isMoveAllowed = moveAllowed;
    }
}
