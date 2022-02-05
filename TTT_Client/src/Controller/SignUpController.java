package Controller;

import Model.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

public class SignUpController implements Initializable {

    PrintStream PSFromController;
    public boolean accepted;
    @FXML
    private Label alertLabel;
    @FXML
    private TextField Email;

    @FXML
    private PasswordField Password;

    @FXML
    private PasswordField confirmationPassword;

    @FXML
    private TextField userName;

    //function to check the sign up Credentials
    public boolean checkCredentials() {
        boolean flag = true;

        if (userName.getText().equals("")) {
            alertLabel.setText("All Fields required!!");
            flag = false;
        }
        if (Password.getText().equals("")) {
            alertLabel.setText("All Fields required!!");
            flag = false;
        }
        if (Email.getText().equals("")) {
            alertLabel.setText("All Fields required!!");
            flag = false;
        }
        if (((confirmationPassword.getText()).equals(Password.getText())) == false) {
            alertLabel.setText("All Fields required!!");
            flag = false;
        }
        return flag;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromController = ClientHandler.ps;
        //errorMessage.setVisible(false);
    }

    //function to handle an action with server while pressing on signUp button
    @FXML
    private void register(ActionEvent event) throws IOException {
        //output in variable b because the previous function return boolean flag
        if (checkCredentials()) {
            Player player = new Player(userName.getText(), Password.getText(), Email.getText());
            ClientOperationMsg xointerface = new ClientOperationMsg(Operation.SIGNUP, player);
            Gson g = new Gson();
            String s = g.toJson(xointerface);
            // PSFromController.println(s);

            String msg;
            JSONObject map = new JSONObject();
            map.put("type", "register");
            map.put("username", userName.getText());
            map.put("email", Email.getText());
            map.put("password", Password.getText());
            msg = map.toString();
            System.out.println(msg);
            PSFromController.println(msg);
            if (!accepted) {
                System.out.println("username is already taken");
                alertLabel.setText("username is already taken");
            }
        }
    }

    //function will be called if the server respond with signup rejected
    public void displayErrorMessage() {
        System.out.println("");
    }

    //to return back to login scene while pressing register button
    @FXML
    private void back(ActionEvent event) throws IOException {
        FXMLLoader logInLoader = new FXMLLoader();
        logInLoader.setLocation(getClass().getResource("/View/LoginScene.fxml"));
        Parent logInRoot = logInLoader.load();
        ClientHandler.LI = logInLoader.getController();
        Scene logInScene = new Scene(logInRoot);
        Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        loginStage.hide();
        loginStage.setScene(logInScene);
        loginStage.show();
    }
}
