package Controller;

import Model.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PlayerListController implements Initializable {

    PrintStream PSFromController;
    Player player;
    boolean inviteName = false;
    String homePlayer = loginController.username;
    String AwayPlayer;
    Vector<Player> holdAllPlayers;

    @FXML
    private TableView<Player> playersListTable;
    @FXML
    private TableColumn<Player, String> userNameColumn;
    @FXML
    private TableColumn<Player, Integer> scoreColoumn;
    @FXML
    private TableColumn<Player, String> statusColoumn;
    @FXML
    private TableColumn<Player, String> isPlayingColoumn;

    //use the coming object from server to set the online players in this scene
    public void setAllPlayers(ClientOperationMsg xoMessage) {
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        scoreColoumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        statusColoumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        isPlayingColoumn.setCellValueFactory(new PropertyValueFactory<>("IsPlaying"));
        userNameColumn.setStyle("-fx-alignment: CENTER;");
        scoreColoumn.setStyle("-fx-alignment: CENTER;");
        statusColoumn.setStyle("-fx-alignment: CENTER;");
        isPlayingColoumn.setStyle("-fx-alignment: CENTER;");
        holdAllPlayers = xoMessage.players;

        //to put vector in observerlist in order to put it in table
        ObservableList<Player> _allPlayers = FXCollections.observableList(holdAllPlayers);
        playersListTable.setItems(_allPlayers);
        playersListTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromController = ClientHandler.ps;
    }

    //back will return back to the selection scene 
    @FXML
    private void back(ActionEvent event) {
        try {
            FXMLLoader selectionmodeloader = new FXMLLoader();
            selectionmodeloader.setLocation(getClass().getResource("/View/SelectionMode.fxml"));
            Parent selectionmodeRoot = selectionmodeloader.load();
            Scene selectionmodeScene = new Scene(selectionmodeRoot);
            Stage selectionmodeStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            selectionmodeStage.hide();
            selectionmodeStage.setScene(selectionmodeScene);
            selectionmodeStage.show();
        } catch (IOException ex) {
            Logger.getLogger(PlayerListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void MouseClicked(MouseEvent event) {
        player = playersListTable.getSelectionModel().getSelectedItem();
    }

    //if the user press refresh button it will reopen this page with the updated data from DB
    //by sending retrive players ang recive retriving players list
    @FXML
    private void refresh(ActionEvent event) {
        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xointerface = new ClientOperationMsg(Operation.RETRIVE_PLAYERS, player);
        Gson g = new Gson();
        String s = g.toJson(xointerface);
        PSFromController.println(s);
    }

    //used to select the away player that user want to play with him
    //and send an invitation to him
    private void minimize(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    private void exit(ActionEvent event) {
        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.LOGOUT, player);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
        Platform.exit();
    }

    @FXML
    private void Invite(ActionEvent event) {
        if (!player.getUserName().equals(loginController.username)) {
            if (player.getStatus() && !player.getIsPlaying()) {
                AwayPlayer = player.getUserName();
                Game offlineGameCreation = new Game();
                offlineGameCreation.setHomePlayer(homePlayer);
                offlineGameCreation.setAwayPlayer(AwayPlayer);
                ClientOperationMsg xointerface = new ClientOperationMsg(Operation.INVITE, offlineGameCreation);
                loginController.myTurn = true;
                Gson g = new Gson();
                String s = g.toJson(xointerface);
                System.out.println(s);
                PSFromController.println(s);
            }
        }

    }
}
