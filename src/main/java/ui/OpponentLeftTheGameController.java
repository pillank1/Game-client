package ui;

import enums.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@AutoInitializableController(title = "", type = Item.CONTROLLER, pathFXML = "opponentLeftTheGame.fxml")
public class OpponentLeftTheGameController extends AbstractWindow {
    @FXML
    private Button buttonToLeft;

    public void setEventHandlerForButtonToLeft(EventHandler<ActionEvent> eventHandler){
        buttonToLeft.setOnAction(eventHandler);
    }
}
