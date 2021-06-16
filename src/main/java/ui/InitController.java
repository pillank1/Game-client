package ui;

import dto.AuthInfo;
import dto.ClientMessage;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Player;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class InitController extends AbstractParentController implements Initializable, GuiInteractor {
    @FXML
    AnchorPane anchorPane;
    @FXML
    Pane statusPane;
    int rows = 8;
    int columns = 8;
    @FXML
    private Label accountLabel, playerNames, statusLabel;
    @FXML
    private GridPane field;
    private Player player;
    private BlockingQueue<AuthInfo> authInfoBlockingQueue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Initializer.initializeWindowControllers(this.getClass(), stage, controllerMap);
        authInfoBlockingQueue = new ArrayBlockingQueue<>(1);
        field.getStyleClass().add("game-grid");
        statusPane.getStyleClass().add("status-pane");
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Pane pane = new Pane();
                int finalI = i;
                int finalJ = j;
                pane.setOnMouseReleased(mouseEvent -> {
                    if (pane.getChildren().isEmpty() && player.isMoveAllowed()) {
                        pane.getChildren().add(getCircle(player.getPlayerColor()));
                        player.sendMessage(new ClientMessage(player.getPlayerName(), finalI, finalJ, false));
                        player.setMoveAllowed(false);
                    }
                });
                pane.getStyleClass().add("game-grid-cell");
                if (i == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (j == 0) {
                    pane.getStyleClass().add("first-row");
                }
                field.add(pane, i, j);
            }
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setResizable(false);
        stage.setOnShown(windowEvent -> {
            stage.getScene().getStylesheets().add("css/main.css");
            auth();
        });
        stage.setOnHidden(windowEvent -> {
            if (!Objects.isNull(player)) {
                player.close();
            }
        });
    }

    private void auth() {
        AuthController controller = (AuthController) getController("auth");
        Stage authStage = controller.getStage();
        authStage.setOnShown(windowEvent -> player = new Player(this, authInfoBlockingQueue));
        EventHandler<ActionEvent> eventHandler = actionEvent -> {
            try {
                authInfoBlockingQueue.put(getAuthInfo());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        controller.setEventHandlerForOkButton(eventHandler);
        //authStage.setOnCloseRequest(windowEvent -> initPlayer());
        authStage.setResizable(false);
        authStage.show();
    }

    private AuthInfo getAuthInfo(){
        AuthController controller = (AuthController) getController("auth");
        return new AuthInfo(controller.getName().getText(),
                controller.getColorPicker().getValue().getRed(),
                controller.getColorPicker().getValue().getGreen(),
                controller.getColorPicker().getValue().getBlue(),
                controller.getColorPicker().getValue().getOpacity());
    }


    @Override
    public void closeAuthWindow() {
        getController("auth").getStage().close();
    }

    @Override
    public void addCircle(final int row, final int column, Color color) {
        for (Node node : field.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column && node instanceof Pane pane && pane.getChildren().isEmpty()) {
                pane.getChildren().add(getCircle(color));
                break;
            }
        }
    }

    @Override
    public void clearField() {
        for (Node node : field.getChildren()) {
            if (node instanceof Pane pane) {
                pane.getChildren().clear();
            }
        }
    }

    @Override
    public void setPlayerNames(String names) {
        playerNames.setText(names);
    }

    @Override
    public void setStatusLabel(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void opponentLeftTheGame() {
        OpponentLeftTheGameController opponentLeftTheGameWindow = (OpponentLeftTheGameController)getController("opponentLeftTheGame");
        opponentLeftTheGameWindow.setEventHandlerForButtonToLeft(actionEvent -> {
            opponentLeftTheGameWindow.getStage().close();
            if (getController("draw").getStage().isShowing()) {
                getController("draw").getStage().close();
            }
            if (getController("youAreAWinner").getStage().isShowing()) {
                getController("youAreAWinner").getStage().close();
            }
            if (getController("youAreALoser").getStage().isShowing()) {
                getController("youAreALoser").getStage().close();
            }
            stage.close();
        });
        focusStage(opponentLeftTheGameWindow.getStage());
        opponentLeftTheGameWindow.getStage().show();
    }

    @Override
    public void showWaitForAPlayerWindow() {
        WaitForAPlayerController WaitForAPlayerWindow = (WaitForAPlayerController)getController("waitForAPlayer");
        WaitForAPlayerWindow.getStage().setOnCloseRequest(Event::consume);
        WaitForAPlayerWindow.setEventHandlerForButtonToLeft(actionEvent -> {
            WaitForAPlayerWindow.getStage().close();
            stage.close();
        });
        focusStage(WaitForAPlayerWindow.getStage());
        WaitForAPlayerWindow.getStage().show();
    }

    @Override
    public void closeWaitForAPlayerWindow() {
        getController("waitForAPlayer").getStage().close();
    }

    @Override
    public void youAreAWinner() {
        aWindow aWindow = getController("youAreAWinner");
        focusStage(aWindow.getStage());
        aWindow.getStage().show();
    }

    @Override
    public void youAreALoser() {
        aWindow aWindow = getController("youAreALoser");
        focusStage(aWindow.getStage());
        aWindow.getStage().show();
    }

    @Override
    public void draw() {
        aWindow aWindow = getController("draw");
        focusStage(aWindow.getStage());
        aWindow.getStage().show();
    }

    @Override
    public void colorHasTaken() {
        aWindow aWindow = getController("colorHasTaken");
        focusStage(aWindow.getStage());
        aWindow.getStage().show();
    }

    @Override
    public void setAccount(String account) {
        accountLabel.setText(account);
    }

    private void focusStage(Stage stage1) {
        double centerXPosition = stage.getX() + stage.getWidth() / 2d;
        double centerYPosition = stage.getY() + stage.getHeight() / 2d;
        stage1.setOnShowing(ev -> stage1.hide());
        stage1.setOnShown(ev -> {
            stage1.setX(centerXPosition - stage1.getWidth() / 2d);
            stage1.setY(centerYPosition - stage1.getHeight() / 2d);
            stage1.show();
        });
    }

    public Node getCircle(Color color) {
        Circle circle = new Circle(30, 30, 15);
        circle.setFill(color);
        Group group = new Group();
        group.getChildren().add(circle);
        return group;
    }
}
