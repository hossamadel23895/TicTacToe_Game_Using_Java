package Controller;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.lang.Exception;


public class ServerGui extends Application {


     public static ServerSceneController test;
    @Override
    public void start(Stage stage) throws Exception {
       FXMLLoader ServerPage=new FXMLLoader();
        ServerPage.setLocation(getClass().getResource("/View/ServerScene.fxml"));
        Parent  ServerPageroot = ServerPage.load();
        test=ServerPage.getController();
        Scene scene = new Scene(ServerPageroot);
        stage.setScene(scene);
        stage.setTitle("Server Page");
        stage.setResizable(false);
        stage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);
    }

}
