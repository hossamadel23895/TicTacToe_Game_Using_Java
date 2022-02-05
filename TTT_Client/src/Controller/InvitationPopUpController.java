package Controller;

import Model.ClientOperationMsg;
import Model.Operation;
import com.google.gson.Gson;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import Model.ClientHandler;
import javafx.scene.control.Label;

public class InvitationPopUpController implements Initializable {

    @FXML
    private Label playername;
    PrintStream PSFromController;
    String homeplayer = loginController.username;
    String opponentPlayer;
    ClientOperationMsg xoMessage;
    Stage stage;

    @FXML

    public void getAwayplayerName(ClientOperationMsg xoMessage, Stage stage) {

        this.xoMessage = xoMessage;
        this.opponentPlayer = xoMessage.getGame().getHomeplayer();
        this.stage = stage;
        playername.setText(opponentPlayer + ": invites you to a game");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromController = ClientHandler.ps;

    }

    @FXML
    private void accept(ActionEvent event) {
        xoMessage.setTypeOfOperation(Operation.INVITATION_ACCEPTED);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
        stage.hide();

    }

    @FXML
    private void decline(ActionEvent event) {
        xoMessage.getGame().setAwayPlayer(homeplayer);
        xoMessage.setTypeOfOperation(Operation.INVITATION_REJECTED);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
        stage.hide();
    }
}
