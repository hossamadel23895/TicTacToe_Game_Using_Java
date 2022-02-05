package Model;

import Controller.*;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ClientHandler extends Application {

    PlayerWithPlayerController pwp;
    public static loginController LI;
    public static SignUpController SU;
    public static int score = 0;
    DataInputStream dis;
    public static PrintStream ps;
    Socket mySocket;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            mySocket = new Socket("127.0.0.1", 7015);
            dis = new DataInputStream(mySocket.getInputStream());
            ps = new PrintStream(mySocket.getOutputStream());
            new Thread(() -> {
                while (true) {
                    try {
                        String recivedMsg = dis.readLine();
                        System.out.println(recivedMsg);
                        Gson g = new Gson();
                        ClientOperationMsg serverMessage;
                        serverMessage = g.fromJson(recivedMsg, ClientOperationMsg.class);

                        //to switch to selection mode scene
                        if (serverMessage.getOperationType().equals(Operation.LOG_IN_ACCEPTED)) {
                            Platform.runLater(() -> {
                                try {
                                    moveToSelectionScene(stage, serverMessage);

                                } catch (IOException ex) {
                                    System.err.println("No switching");
                                    ex.printStackTrace();
                                }
                            });
                        }

                        if (serverMessage.getOperationType().equals(Operation.LOGIN_REJECTED)) {
                            SU = new SignUpController();
                            SU.accepted = false;

                            LI.accepted = false;
                        } //to switch to login scene
                        else if (serverMessage.getOperationType().equals(Operation.SIGN_UP_ACCEPTED)) {
                            System.out.println("Register here");
                            SU = new SignUpController();
                            SU.accepted = true;

                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loginInLoader = new FXMLLoader();
                                    loginInLoader.setLocation(getClass().getResource("/View/LoginScene.fxml"));
                                    Parent root = loginInLoader.load();
                                    LI = loginInLoader.getController();

                                    //main root scene which will start firstly 
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);
                                    stage.setTitle("Tic Tac Toe Game");
                                    stage.setResizable(false);
                                    // stage.getIcons().add(new Image("logo.png"));
                                    stage.show();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                // moveToLogInScene(stage);
                            });
                        } else if (serverMessage.getOperationType().equals(Operation.SIGN_UP_REJECTED)) {
                            SU = new SignUpController();
                            SU.accepted = false;
                        } //to switch to player with computer scene
                        else if (serverMessage.getOperationType().equals(Operation.PLAYING_SINGLE_MODE)) {
                            Platform.runLater(() -> {
                                try {
                                    moveToPlayWithComputerScene(stage);
                                } catch (IOException ex) {
                                    System.err.println("couldn't switch");
                                    ex.printStackTrace();
                                }
                            });
                        } //to switch to players list scene
                        else if (serverMessage.getOperationType().equals(Operation.RETREVING_PLAYERS_LIST)) {
                            Platform.runLater(() -> {
                                try {
                                    moveToPlayersListScene(stage, serverMessage);
                                } catch (IOException ex) {
                                    System.err.println("couldn't switch");
                                    ex.printStackTrace();
                                }
                            });
                        } //to switch to invitation to play a game
                        else if (serverMessage.getOperationType().equals(Operation.RECEIVING_INVITATION)) {
                            moveToInvitationPopUp(serverMessage);
                        } //to switch to player with player scene
                        else if (serverMessage.getOperationType().equals(Operation.INVITATION_ACCEPTED_FROM_SERVER)) {
                            Platform.runLater(() -> {
                                moveToPlayerToPlayerScene(stage, serverMessage);
                            });
                        } else if (serverMessage.getOperationType().equals(Operation.INVITATION_REJECTED_FROM_SERVER)) {

                            loginController.myTurn = false;
                        } //to switch to online pop up scene
                        else if (serverMessage.getOperationType().equals(Operation.NEW_PLAYER_LOGGEDIN_POPUP)) {
                            moveToOnlinePopUpScene(serverMessage);
                        } //to print the away players XO moves
                        else if (serverMessage.getOperationType().equals(Operation.INCOMING_MOVE)) {
                            Platform.runLater(() -> {
                                try {
                                    printGameMove(serverMessage);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                        } //to print the chat messages in the chat box
                        else if (serverMessage.getOperationType().equals(Operation.CHAT_PLAYERS_WITH_EACH_OTHERS_FROM_SERVER)) {
                            Platform.runLater(() -> {
                                PrintMessageOfChatRoom(serverMessage);
                            });
                        } //to display XO moves
                        //to indicate that game finished and show the results
                        else if (serverMessage.getOperationType().equals(Operation.GAME_GOT_FINISHED_SECCUSSFULLY)) {
                            pwp.recieveGameEnding();
                        } //to back to the selection scene if the user press back button
                        else if (serverMessage.getOperationType().equals(Operation.BACK_FROM_SERVER)) {

                            Platform.runLater(() -> {
                                try {
                                    moveToSelectionScene(stage, serverMessage);
                                } catch (IOException ex) {
                                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        } else if (serverMessage.getOperationType().equals("DinDgameIsNotSetted")) {
                            System.err.println("gameIsNotSetted");
                        } else if (serverMessage.getOperationType().equals("viewRecordsfromServer")) {
                            Platform.runLater(() -> {
                                try {
                                    moveToReplaysScene(stage, serverMessage);
                                } catch (IOException ex) {
                                    System.err.println("couldn't switch");
                                    ex.printStackTrace();
                                }
                            });
                        }
                    } catch (IOException ex) {
                        try {
                            this.dis.close();
                            ps.close();
                            this.mySocket.close();
                            break;
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException ex) {
            System.err.println("Server Is Off");
            ex.printStackTrace();
        }

        //loader to start login page
        FXMLLoader loginInLoader = new FXMLLoader();
        loginInLoader.setLocation(getClass().getResource("/View/LoginScene.fxml"));
        Parent root = loginInLoader.load();
        LI = loginInLoader.getController();

        //main root scene which will start firstly 
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe Game");
        stage.setResizable(false);
        // stage.getIcons().add(new Image("logo.png"));
        stage.show();
    }

    //**functions to move from GUI to another GUI**//
    //1st function to move to selection mode scene
    void moveToSelectionScene(Stage stage, ClientOperationMsg xoMessage) throws IOException {
        score = xoMessage.getPlayer().getScore();
        System.out.println(xoMessage.getPlayer().getScore());
        FXMLLoader selectionModeLoader = new FXMLLoader();
        selectionModeLoader.setLocation(getClass().getResource("/View/SelectionMode.fxml"));
        Parent selectionModeRoot = selectionModeLoader.load();
        Scene selectionModeScene = new Scene(selectionModeRoot);
        stage.hide();
        stage.setScene(selectionModeScene);
        stage.show();
    }

    //2nd function to move to log in scene
    void moveToLogInScene(Stage stage) {
        try {
            FXMLLoader logInLoader = new FXMLLoader();
            logInLoader.setLocation(getClass().getResource("/View/LoginScene.fxml"));
            Parent logInRoot = logInLoader.load();
            LI = logInLoader.getController();
            Scene logInScene = new Scene(logInRoot);
            stage.hide();
            stage.setScene(logInScene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //3rd function to move to play with computer scene
    void moveToPlayWithComputerScene(Stage stage) throws IOException {
        FXMLLoader playWithComputerLoader = new FXMLLoader();
        playWithComputerLoader.setLocation(getClass().getResource("/View/PlayWithComputer.fxml"));
        Parent playWithComputerRoot = playWithComputerLoader.load();
        Scene playWithComputerScene = new Scene(playWithComputerRoot);
        stage.hide();
        stage.setScene(playWithComputerScene);
        stage.show();
    }

    //4th function to move to online players list scene
    void moveToPlayersListScene(Stage stage, ClientOperationMsg xoMessage) throws IOException {
        FXMLLoader playersListLoader = new FXMLLoader();
        playersListLoader.setLocation(getClass().getResource("/View/PlayerList.fxml"));
        Parent playerListRoot = playersListLoader.load();

        //this is object from PlayersListControler used to call setAllPlayers function
        //to show all players on the table while loading this scene
        PlayerListController plc = playersListLoader.getController();
        plc.setAllPlayers(xoMessage);
        Scene playerListScene = new Scene(playerListRoot);
        stage.hide();
        stage.setScene(playerListScene);
        stage.show();
    }

    //5th function to move to invitation pop up
    void moveToInvitationPopUp(ClientOperationMsg xoMessage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader invitationPopUpLoader = new FXMLLoader();
                invitationPopUpLoader.setLocation(getClass().getResource("/View/InvitationPopUp.fxml"));
                Parent invitationPopUpRoot = invitationPopUpLoader.load();
                InvitationPopUpController popUpInvitation = invitationPopUpLoader.getController();
                Scene invitationPopUpScene = new Scene(invitationPopUpRoot);
                Stage invitationPopUpStage = new Stage();

                //this is object from invitation pop up Controller used to call getAwayPlayerName function
                //to get away player information
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                invitationPopUpStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 550);
                invitationPopUpStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 250);
                popUpInvitation.getAwayplayerName(xoMessage, invitationPopUpStage);
                invitationPopUpStage.hide();
                invitationPopUpStage.initStyle(StageStyle.UNDECORATED);
                invitationPopUpStage.setScene(invitationPopUpScene);
                invitationPopUpStage.show();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    //6th function to move to multiplayers scene
    void moveToPlayerToPlayerScene(Stage stage, ClientOperationMsg xoMessage) {
        try {
            FXMLLoader playerWithPlayerLoader = new FXMLLoader();
            playerWithPlayerLoader.setLocation(getClass().getResource("/View/PlayerWithPlayer.fxml"));
            Parent playerWithPlayerRoot = playerWithPlayerLoader.load();

            //this is object from player with player Controller used to call setIDs function
            //to set game IDs corresponding to their players
            pwp = playerWithPlayerLoader.getController();
            pwp.setIDs(xoMessage.getGame().getGameId(), loginController.username, xoMessage.getGame().getAwayPlayer());
            Scene playerWithPlayerScene = new Scene(playerWithPlayerRoot);
            stage.hide();
            stage.setScene(playerWithPlayerScene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //7th function to move to online pop up person scene
    void moveToOnlinePopUpScene(ClientOperationMsg xoMessage) {
        if (!xoMessage.getPlayer().getUserName().equals(loginController.username)) {
            Platform.runLater(() -> {
                try {
                    FXMLLoader onlinePopUpLoader = new FXMLLoader();
                    onlinePopUpLoader.setLocation(getClass().getResource("/View/OnlinePopUp.fxml"));
                    Parent onlinePopUpRoot = onlinePopUpLoader.load();

                    //this is object from OnlinePopUpController used to call getUserName function
                    //to show the player that become online
                    OnlinePopUpController popUp = onlinePopUpLoader.getController();
                    popUp.getUserName(xoMessage.getPlayer().getUserName());

                    Scene onlinePopUpScene = new Scene(onlinePopUpRoot);
                    Stage onlinePopUpStage = new Stage();
                    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

                    onlinePopUpStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 400);
                    onlinePopUpStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 200);
                    onlinePopUpStage.initStyle(StageStyle.UNDECORATED);
                    onlinePopUpStage.hide();
                    onlinePopUpStage.setScene(onlinePopUpScene);
                    onlinePopUpStage.show();
                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(event -> onlinePopUpStage.close());
                    delay.play();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        }
    }

    //8th go to view replays scene
    void moveToReplaysScene(Stage stage, ClientOperationMsg xoMessage) throws IOException {

        FXMLLoader replaysListLoader = new FXMLLoader();
        replaysListLoader.setLocation(getClass().getResource("/View/ReplaysListScene.fxml"));
        Parent replayListRoot = replaysListLoader.load();
        //this is object from PlayersListControler used to call setAllPlayers function
        //to show all players on the table while loading this scene
        ReplaysListSceneController rlc = replaysListLoader.getController();
        rlc.setAllReplays(xoMessage);
        Scene replayListScene = new Scene(replayListRoot);
        stage.hide();
        stage.setScene(replayListScene);
        stage.show();

    }

    //function to print the away player move
    void printGameMove(ClientOperationMsg xoMessage) {
        pwp.printOpponentMove(xoMessage.getFieldPosition(), true);
    }

    //function to print the message inside the chatbox
    void PrintMessageOfChatRoom(ClientOperationMsg xoMessage) {
        pwp.printMessage(xoMessage);
    }

    void cancelResume(boolean state) {
        pwp.cancelOrEnableResume(state);
    }

    @Override
    public void stop() {
        System.out.println("Stage is closing");
        Platform.exit();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
