package Controller;

import Model.*;
import com.google.gson.Gson;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SelectLevelController implements Initializable {

    PrintStream PSFromController;
    public static int gameLevel = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromController = ClientHandler.ps;
    }

    @FXML
    private void hard(ActionEvent event) {
        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.PLAYING_SINGLE_MODE, player);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
        gameLevel = 1;
    }

    @FXML
    private void easy(ActionEvent event) {
        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.PLAYING_SINGLE_MODE, player);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
        gameLevel = 0;
    }

    @FXML
    private void medium(ActionEvent event) {
        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.PLAYING_SINGLE_MODE, player);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
        gameLevel = 0;
    }

    @FXML
    private void back(ActionEvent event) {
        try {
            FXMLLoader selectionModeLoader = new FXMLLoader();
            selectionModeLoader.setLocation(getClass().getResource("/View/SelectionMode.fxml"));
            Parent selectionModeRoot = selectionModeLoader.load();
            Scene selectionModeScene = new Scene(selectionModeRoot);
            Stage selectionModeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            selectionModeStage.hide();
            selectionModeStage.setScene(selectionModeScene);
            selectionModeStage.show();
        } catch (IOException ex) {
            Logger.getLogger(SelectLevelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
