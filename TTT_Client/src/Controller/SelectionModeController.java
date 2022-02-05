package Controller;

import Model.ClientOperationMsg;
import Model.Player;
import Model.Operation;
import com.google.gson.Gson;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Model.ClientHandler;

public class SelectionModeController implements Initializable {

    PrintStream PSFromController;
    String currentPlayer = loginController.username;
    @FXML
    private Text logedInUserName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromController = ClientHandler.ps;
        logedInUserName.setText(loginController.username);

        //why Integer.toString?
        //because setText required text not integer
        // logedInUserScore.setText(Integer.toString(ClientHandler.score));
    }

    //if the user press single player it will move to the select level scene
    @FXML
    private void singlePlayer(ActionEvent event) throws IOException {
        FXMLLoader levelSelectionlader = new FXMLLoader();
        levelSelectionlader.setLocation(getClass().getResource("/View/SelectLevel.fxml"));
        Parent levelSelectionRoot = levelSelectionlader.load();
        Scene levelSelectionScene = new Scene(levelSelectionRoot);
        Stage levelSelectionstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        levelSelectionstage.hide();
        levelSelectionstage.setScene(levelSelectionScene);
        levelSelectionstage.show();
    }

    //if the user press multiplayer it will send this message to server in order to return back
    //the data about the online users
    @FXML
    private void multiplayer(ActionEvent event) {
        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.RETRIVE_PLAYERS, player);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
    }

    @FXML
    private void replayBtnHandler(ActionEvent event) throws IOException {
        String msg;
        JSONObject map = new JSONObject();
        map.put("type", "viewRecords");
        map.put("player", currentPlayer);
        msg = map.toString();
        System.out.println(msg);
        PSFromController.println(msg);
    }

    //if the user press logout it will send logout to server
    //and it will return back to login scene
    @FXML
    private void logout(ActionEvent event) {

        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xointerface = new ClientOperationMsg(Operation.LOGOUT, player);
        Gson g = new Gson();
        String s = g.toJson(xointerface);
        PSFromController.println(s);
        Platform.exit();
    }

    private void minimize(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    private void exit(ActionEvent event) {

        Platform.exit();
    }
}
