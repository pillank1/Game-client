package ui;

import javafx.stage.Stage;

public abstract class AbstractWindow implements aWindow {
    protected Stage stage;

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
