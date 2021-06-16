package ui;

import dto.AuthInfo;
import enums.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

@AutoInitializableController(title = "Авторизация", type = Item.CONTROLLER, pathFXML = "auth.fxml")
public class AuthController extends AbstractWindow implements Initializable {
    @FXML
    private TextField name;
    @FXML
    private Button okButton;
    //private AuthInfo authInfo;
    @FXML
    private ColorPicker colorPicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colorPicker.setValue(Color.RED);
    }

    public void setEventHandlerForOkButton(EventHandler<ActionEvent> eventHandler){
        okButton.setOnAction(eventHandler);
    }

//    @FXML
//    private void ok() {
//        authInfo = new AuthInfo(name.getText(),
//                colorPicker.getValue().getRed(),
//                colorPicker.getValue().getGreen(),
//                colorPicker.getValue().getBlue(),
//                colorPicker.getValue().getOpacity());
//        //stage.close();
//    }

//    public AuthInfo getAuthInfo() {
//        return authInfo;
//    }

    public TextField getName() {
        return name;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }
}
