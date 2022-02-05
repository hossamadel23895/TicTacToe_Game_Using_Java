package Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class OnlinePopUpController implements Initializable {

    @FXML
    private Text poptext;
    String username;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void getUserName(String username) {
        poptext.setText(username + " is active");
    }

}
