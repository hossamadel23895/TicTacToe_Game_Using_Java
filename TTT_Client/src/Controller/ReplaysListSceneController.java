package Controller;

import Model.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ReplaysListSceneController implements Initializable {

    PrintStream PSFromController;
    public static Record chosenReplay = new Record();
    String currentPlayer = loginController.username;

    Vector<Record> holdAllReplays;

    @FXML
    private TableView<Record> replaysListTable;
    @FXML
    private TableColumn<Record, String> player1Column;
    @FXML
    private TableColumn<Record, String> player2Column;
    @FXML
    private TableColumn<Record, String> timeColumn;

    @FXML
    private void backBtnHandler(ActionEvent event) {
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
            Logger.getLogger(PlayWithComputerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //use the coming object from server to set the player replays in the scene
    public void setAllReplays(ClientOperationMsg xoMessage) {
        holdAllReplays = xoMessage.records;
        ObservableList<Record> _allReplays = FXCollections.observableList(holdAllReplays);
        System.out.println(_allReplays.getClass().getName());
        player1Column.setCellValueFactory(new PropertyValueFactory<>("player1"));
        player2Column.setCellValueFactory(new PropertyValueFactory<>("player2"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        player1Column.setStyle("-fx-alignment: CENTER;");
        player2Column.setStyle("-fx-alignment: CENTER;");
        timeColumn.setStyle("-fx-alignment: CENTER;");
        replaysListTable.setItems(_allReplays);

        //to put vector in observerlist in order to put it in table
        replaysListTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromController = ClientHandler.ps;

    }

    @FXML
    private void MouseClicked(MouseEvent event) {
        chosenReplay = replaysListTable.getSelectionModel().getSelectedItem();
    }

    //used to select the desired replay
    @FXML
    private void viewReplayBtnHandler(ActionEvent event) throws IOException {

        System.out.println(chosenReplay.getReplay());
        FXMLLoader replayloader = new FXMLLoader();
        replayloader.setLocation(getClass().getResource("/View/GameSceneReplay.fxml"));
        Parent replayloaderRoot = replayloader.load();

        Scene replayScene = new Scene(replayloaderRoot);
        Stage replayStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        replayStage.hide();
        replayStage.setScene(replayScene);
        replayStage.show();

    }
}
