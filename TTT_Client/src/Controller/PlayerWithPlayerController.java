package Controller;

import Model.*;

import com.google.gson.Gson;
import java.io.PrintStream;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Vector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

public class PlayerWithPlayerController implements Initializable {

    boolean gameEnded;
    @FXML
    private Button resume;
    @FXML
    private Button pos1;
    @FXML
    private Button pos2;
    @FXML
    private Button pos3;
    @FXML
    private Button pos6;
    @FXML
    private Button pos5;
    @FXML
    private Button pos4;
    @FXML
    private Button pos7;
    @FXML
    private Button pos8;
    @FXML
    private Button pos9;
    @FXML
    private Label playerSign;
    @FXML
    private Label opponenPlayerSign;
    @FXML
    private Label homeNameLabel;
    @FXML
    private Label opponentNameLabel;
    @FXML
    private Label gameResult;
    @FXML
    private TextArea textScreenMessanger;
    @FXML
    private Button sendButton;
    @FXML
    private TextField textAreaMessanger;
    @FXML
    private Button SaveRec;

    public String gameRecord1 = "";

    PrintStream PSFromController;
    boolean myturn;
    String myUserName;
    String opponentUserName;
    int gameID;
    char playerSymbol, opponentSymbol;
    Integer playerPos;
    Vector<Integer> playerMoves = new Vector<>();
    Vector<Integer> opponentMoves = new Vector<>();
    Vector<Integer> movesPool = new Vector<>();
    int numOfMoves;
    public static boolean turnOffNotification = true;

    boolean WinShape(Vector<Integer> moves) {
        boolean winFlag = false;
        Integer[] topRow = {1, 2, 3};
        Integer[] midRow = {4, 5, 6};
        Integer[] botRow = {7, 8, 9};
        Integer[] leftCol = {1, 4, 7};
        Integer[] midCol = {2, 5, 8};
        Integer[] rightCol = {3, 6, 9};
        Integer[] mainDiag = {1, 5, 9};
        Integer[] secondaryDiag = {3, 5, 7};
        Integer[][] winningCases = {
            topRow, midRow, botRow,
            leftCol, midCol, rightCol,
            mainDiag, secondaryDiag
        };

        int i = 0;
        while (!winFlag && i < winningCases.length) {
            if (moves.containsAll(Arrays.asList(winningCases[i]))) {
                winFlag = true;
            }
            i++;
        }
        return winFlag;
    }

    public void init() {
        playerMoves.clear();
        opponentMoves.clear();
        movesPool.clear();
        turnOffNotification = true;
        if (loginController.myTurn) {
            playerSymbol = 'X';
            opponentSymbol = 'O';
            gameResult.setText("Your Turn");
        } else {
            playerSymbol = 'O';
            opponentSymbol = 'X';
            gameResult.setText("Their Turn");
        }
        for (int i = 0; i < 9; i++) {
            movesPool.add(i + 1);
        }
        numOfMoves = 0;
        gameEnded = false;
    }

    public void displayMove(Integer position, char symbol) {
        switch (position) {
            case 1:
                pos1.setText(Character.toString(symbol));
                gameRecord1 += "1";
                break;
            case 2:
                pos2.setText(Character.toString(symbol));
                gameRecord1 += "2";
                break;
            case 3:
                pos3.setText(Character.toString(symbol));
                gameRecord1 += "3";
                break;
            case 4:
                pos4.setText(Character.toString(symbol));
                gameRecord1 += "4";
                break;
            case 5:
                pos5.setText(Character.toString(symbol));
                gameRecord1 += "5";
                break;
            case 6:
                pos6.setText(Character.toString(symbol));
                gameRecord1 += "6";
                break;
            case 7:
                pos7.setText(Character.toString(symbol));
                gameRecord1 += "7";
                break;
            case 8:
                pos8.setText(Character.toString(symbol));
                gameRecord1 += "8";
                break;
            case 9:
                pos9.setText(Character.toString(symbol));
                gameRecord1 += "9";
                break;
            default:
                break;
        }
    }

    @FXML
    void playMove(ActionEvent event) {
        if (myturn) {
            System.out.println("is myturn");
            if (!gameEnded) {
                // Player move
                System.out.println("game not ended");
                playerPos = Integer.parseInt(((Control) event.getSource()).getId());
                if (!movesPool.isEmpty() && movesPool.contains(playerPos)) {
                    System.out.println("I can play here");
                    displayMove(playerPos, playerSymbol);
                    movesPool.remove(playerPos);
                    playerMoves.add(playerPos);
                    sendMyMove();
                    numOfMoves++;
                    myturn = false;
                    gameResult.setText("Their Turn");
                    if (WinShape(playerMoves)) {
                        System.out.println("You win! ");
                        gameResult.setText("You Win! ");
                        gameEnded = true;
                        myturn = false;
                        reportGameEnding(true);
                        SaveRec.setDisable(false);
                        sendButton.setDisable(true);
                    }
                }
                if (!gameEnded && numOfMoves >= 9) {
                    System.out.println("It's a draw!");
                    gameResult.setText("It's a Draw! ");
                    gameEnded = true;
                    myturn = false;
                    SaveRec.setDisable(false);
                    sendButton.setDisable(true);
                    //reportGameEnding(false);                    
                }
            }
        }
    }

    void reportGameEnding(boolean state) {
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.GAME_GOT_FINISHED, new Player(myUserName), new Game(gameID, myUserName, opponentUserName));
        xoMessage.setOperationResult(state);
        Gson g = new Gson();
        String messageend = g.toJson(xoMessage);
        System.out.println(messageend);
        PSFromController.println(messageend);
    }

    public void recieveGameEnding() {
        gameEnded = true;
        myturn = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PSFromController = ClientHandler.ps;
        gameResult.setText("");
        init();
        myturn = loginController.myTurn;
    }

    void clearAll() {
        pos1.setText("");
        pos2.setText("");
        pos3.setText("");
        pos4.setText("");
        pos5.setText("");
        pos6.setText("");
        pos7.setText("");
        pos8.setText("");
        pos9.setText("");
        gameResult.setText("");
    }

    void sendMyMove() {
        Game game = new Game(gameID, myUserName, opponentUserName);
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.GAME_PLAY_MOVE, game, playerPos, playerSymbol);
        Gson g = new Gson();
        System.out.println(g.toJson(xoMessage));
        PSFromController.println(g.toJson(xoMessage));
    }

    public void setIDs(int gameID, String myUserName, String opponentUserName) {
        this.gameID = gameID;
        this.myUserName = myUserName;

        this.opponentUserName = opponentUserName;
        if (myturn) {
            Platform.runLater(() -> {
                homeNameLabel.setText("hello mr " + myUserName + "\n" + "you play with " + opponentUserName + "\n now it is :");
                //opponentNameLabel.setText(opponentUserName);
                gameResult.setText("Your Turn");
            });
        } else {
            Platform.runLater(() -> {
                homeNameLabel.setText("hello mr " + myUserName + "\n" + "you play with " + opponentUserName + "\n now it is :");
                //  opponentNameLabel.setText(opponentUserName);
                gameResult.setText("Their Turn");
            });
        }
    }

    public void printOpponentMove(Integer playerPos, boolean _myturn) {
        if (!movesPool.isEmpty() && movesPool.contains(playerPos)) {
            opponentMoves.add(playerPos);
            if (movesPool.contains(playerPos)) {
                movesPool.remove(playerPos);
            }
            numOfMoves++;
            if (!gameEnded) {
                displayMove(playerPos, opponentSymbol);
                myturn = _myturn;
                if (myturn) {
                    gameResult.setText("Your Turn");
                } else {
                    gameResult.setText("Their Turn");
                }
            }
        }
        if (WinShape(opponentMoves)) {
            System.out.println(" You Lose! ");
            gameResult.setText(" You Lose! ");
            gameEnded = true;
            myturn = false;
            SaveRec.setDisable(false);

            sendButton.setDisable(true);
        }
        if (!gameEnded && numOfMoves >= 9) {
            System.out.println("It's a draw!");
            gameResult.setText("It's a Draw! ");
            gameEnded = true;
            myturn = false;
            SaveRec.setDisable(false);

            sendButton.setDisable(true);
        }
    }

    @FXML
    private void back(ActionEvent event) {
        ClientOperationMsg xoMsg = new ClientOperationMsg(Operation.BACK_FROM_ONLINE, new Player(myUserName), new Game(myUserName, opponentUserName));
        xoMsg.getGame().setIsFinished(gameEnded);
        xoMsg.getPlayer().setIsMyTurn(myturn);
        Gson g = new Gson();
        PSFromController.println(g.toJson(xoMsg));
        turnOffNotification = false;
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        String chatingMessage = "[" + loginController.username + "]: " + textAreaMessanger.getText();//
        textAreaMessanger.setText("");
        textScreenMessanger.appendText("  " + chatingMessage + "\n");
        Game onlineGameChating = new Game(myUserName, opponentUserName, chatingMessage);
        ClientOperationMsg xointerface = new ClientOperationMsg(Operation.CHAT_PLAYERS_WITH_EACH_OTHERS, onlineGameChating);
        Gson g = new Gson();
        String message = g.toJson(xointerface);
        System.out.println(message);
        PSFromController.println(message);
    }

    @FXML
    private void end(ActionEvent event) {

        Player player = new Player();
        player.setUserName(loginController.username);
        ClientOperationMsg xoMessage = new ClientOperationMsg(Operation.LOGOUT, player);
        Gson g = new Gson();
        String s = g.toJson(xoMessage);
        PSFromController.println(s);
        Platform.exit();
    }

    @FXML
    void SaveRec(ActionEvent event) {
        String player1ingame, player2ingame;
        if (loginController.myTurn) {
            player1ingame = loginController.username;
            player2ingame = opponentUserName;
        } else {
            player1ingame = opponentUserName;
            player2ingame = loginController.username;

        }

        String SendRecordToDataBase = gameRecord1;
        String msg, username;
        String time = String.valueOf(Instant.now());
        System.out.print(time);
        username = loginController.username;
        JSONObject map = new JSONObject();
        map.put("type", "RecordVsComp");
        map.put("player1", player1ingame);
        map.put("player2", player2ingame);
        map.put("Record", SendRecordToDataBase);
        map.put("time", time);
        msg = map.toString();
        System.out.println(msg);
        PSFromController.println(msg);
        SaveRec.setDisable(true);
        SaveRec.setText("Replay Saved");

    }

    public void printMessage(ClientOperationMsg xo) {
        if (!xo.getGame().getMessage().equals("IS_LEFT")) {
            textScreenMessanger.appendText(xo.getGame().getMessage() + "\n");
            // System.out.println(textScreenMessanger.getText());
        } else {
            gameResult.setText("Opponent Left The Session");
        }
    }

    public void cancelOrEnableResume(boolean state) {
        resume.setDisable(state);
    }

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

}
