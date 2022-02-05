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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class loginController implements Initializable {

    //why username static?
    //to be accesed by the class name to get username at any other scene
    public static String username;
    public static boolean myTurn = false;
    private String password;
    public boolean accepted;
    Stage window;
    PrintStream PSFromController;

    @FXML
    private TextField loginPassword;

    @FXML
    private TextField loginUserName;
    @FXML
    public Label error;

    //to intialize the login controller with printstream refer to main class printStream
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromController = ClientHandler.ps;

    }

    //login button function to validate the inputs from user
    //also to send message to server to say person want to log in check it's credintional
    @FXML
    private void login(ActionEvent event) {
        username = loginUserName.getText();
        password = loginPassword.getText();
        if (username.equals("") || password.equals("")) {
            if (username.equals("")) {
                error.setText("invalid login info !");
            }

            if (password.equals("")) {
                error.setText("invalid login info !");
            }
        } else {
            Player player = new Player();
            player.setUserName(username);
            player.setPassword(password);
            ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.LOGIN, player);
            Gson g = new Gson();
            String s = g.toJson(xoMessage);
            PSFromController.println(s);
        }

        if (!accepted) {
            System.out.println("invaild login info !");
            error.setText("invalid login info !");
        }
    }

    //function will be called if the server respond with login rejected
    public void displayErrorMessage() {
        System.out.println("not found");
    }

    //to move to sign up scene if the user press on sign up button
    @FXML
    void signup(ActionEvent event) throws IOException {
        Parent Login = FXMLLoader.load(getClass().getResource("/View/SignUp.fxml"));
        Scene loginScene = new Scene(Login);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }
}
