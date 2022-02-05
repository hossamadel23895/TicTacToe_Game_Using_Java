/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import Model.Database;
import Model.Player;
import Model.Server;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Dina Alaa
 */
public class ServerSceneController 
implements Initializable {

@FXML
private Button turnONBtn;
@FXML
private Button turnOFFBtn;

Server myServer;
    @FXML
    private TableColumn<Player, String>userName;
    @FXML
    private TableColumn<Player, Integer>score;
    @FXML
    private TableColumn<Player, String>statusString;
    @FXML
    private TableView<Player> dataTable;
   
@Override
public void initialize(URL url, ResourceBundle rb) {
     turnOFFBtn.setDisable(true);

        }
    
@FXML

private void startServerConnection(ActionEvent event) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Database.dbConnect();
        Database.changeAllStatus();
        listPlayers();
        myServer=new Server(7015);

turnONBtn.setDisable(true);
turnOFFBtn.setDisable(false);
}

@FXML
private void stopServerConnection(ActionEvent event) throws SQLException {
      
    resetTable();
    Database.changeAllStatus();
    myServer.closeServer();
    Database.dbDisconnect();
 turnOFFBtn.setDisable(true);
 turnONBtn.setDisable(false);

    }
    public void listPlayers(){
        try {
            ObservableList players=Database.getPlayers();
            dataTable.setItems(players);
            userName.setCellValueFactory(new PropertyValueFactory<Player, String>("userName"));
            score.setCellValueFactory(new PropertyValueFactory<Player, Integer>("score"));
            statusString.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStringStatus()));
            userName.setStyle("-fx-alignment: CENTER;");
            score.setStyle("-fx-alignment: CENTER;");
            statusString.setStyle("-fx-alignment: CENTER;");
            dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
    } catch (SQLException ex) {
        Logger.getLogger(ServerSceneController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
        Logger.getLogger(ServerSceneController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
        Logger.getLogger(ServerSceneController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
        Logger.getLogger(ServerSceneController.class.getName()).log(Level.SEVERE, null, ex);
    }


    }

    public void resetTable(){
        for( int i = 0; i<dataTable.getItems().size(); i++) {
            dataTable.getItems().clear();
        }   
    }
    
}
