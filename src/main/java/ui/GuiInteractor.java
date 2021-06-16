package ui;

import dto.AuthInfo;
import javafx.scene.paint.Color;

import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

public interface GuiInteractor {
    void addCircle(final int row, final int column, Color color);

    void youAreAWinner();

    void youAreALoser();

    void draw();

    void setAccount(String account);

    void clearField();

    void setPlayerNames(String names);

    void setStatusLabel(String status);

    void opponentLeftTheGame();

    void showWaitForAPlayerWindow();

    void closeWaitForAPlayerWindow();

    void closeAuthWindow();

    void colorHasTaken();
}
