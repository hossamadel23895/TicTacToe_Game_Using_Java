package Controller;

import Model.ClientOperationMsg;
import Model.Player;
import Model.Operation;
import Model.ClientHandler;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.time.Instant;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

public class PlayWithComputerController implements Initializable {

    @FXML
    private Label userNameLabel, gameResult;

    @FXML
    private Button pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, pos9, pAgainBtn, saveReplayBtn;

    @FXML
    private Text winText, loseText, drawText;

    PrintStream PSFromcontroller;
    String myUserName = loginController.username;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PSFromcontroller = ClientHandler.ps;
        userNameLabel.setText(myUserName);
    }

    @FXML
    private void back(ActionEvent event) {
        try {
            FXMLLoader selectionModeLoader = new FXMLLoader();
            selectionModeLoader.setLocation(
                    getClass().getResource("/View/SelectionMode.fxml")
            );
            Parent selectionModeRoot = selectionModeLoader.load();
            Scene selectionModeScene = new Scene(selectionModeRoot);
            Stage selectionModeStage = (Stage) ((Node) event.getSource()).getScene()
                    .getWindow();
            selectionModeStage.hide();
            selectionModeStage.setScene(selectionModeScene);
            selectionModeStage.show();
        } catch (IOException ex) {
            Logger
                    .getLogger(PlayWithComputerController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void PlayAgainBtnHandler(ActionEvent event) {
        try {
            Parent backParent = FXMLLoader.load(
                    getClass().getResource("/View/PlayWithComputer.fxml")
            );
            Scene backScene = new Scene(backParent);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(backScene);
            window.show();
        } catch (IOException ex) {
            Logger
                    .getLogger(PlayWithComputerController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void playMove(ActionEvent ae) {
        if (((Button) ae.getSource()).getText() == "") {
            doPlayerMove((Button) ae.getSource());

            checkGameDone();

            useHardAI1();

            checkGameDone();
        }
    }

    //Game record string
    public String gameRecord = "";

    public void doPlayerMove(Button btn) {
        if (btn.getText().equals("")) {
            btn.setText("X");

            //recording the player move in the record string
            switch (btn.getId()) {
                case "1":
                    gameRecord += "1";
                    break;
                case "2":
                    gameRecord += "2";
                    break;
                case "3":
                    gameRecord += "3";
                    break;
                case "4":
                    gameRecord += "4";
                    break;
                case "5":
                    gameRecord += "5";
                    break;
                case "6":
                    gameRecord += "6";
                    break;
                case "7":
                    gameRecord += "7";
                    break;
                case "8":
                    gameRecord += "8";
                    break;
                case "9":
                    gameRecord += "9";
                    break;
            }
        }
    }

    @FXML
    private void saveReplayBtnHandler(ActionEvent event) {
        //Specify what to do with the replay string.
        String SendRecordToDataBase = gameRecord;
        String msg, username;
        String time = String.valueOf(Instant.now());
        System.out.print(time);
        username = loginController.username;
        JSONObject map = new JSONObject();
        map.put("type", "RecordVsComp");
        map.put("player1", username);
        map.put("player2", "computer");
        map.put("Record", SendRecordToDataBase);
        map.put("time", time);
        msg = map.toString();
        System.out.println(msg);

        PSFromcontroller.println(msg);
        saveReplayBtn.setDisable(true);
        saveReplayBtn.setText("Replay Saved");
    }

    public String checkWin() {
        String whoWon = "";
        String currentBoardStatus = readBoard();
        //Rows win check
        if (pos1.getText() == pos2.getText()
                && pos2.getText() == pos3.getText()
                && pos1.getText() != "") {
            whoWon = pos1.getText() == "X" ? "X" : "O";
            pos1.getStyleClass().clear();
            pos2.getStyleClass().clear();
            pos3.getStyleClass().clear();
            if (whoWon == "X") {
                pos1.getStyleClass().add("game_grid_button_win");
                pos2.getStyleClass().add("game_grid_button_win");
                pos3.getStyleClass().add("game_grid_button_win");
            } else {
                pos1.getStyleClass().add("game_grid_button_lose");
                pos2.getStyleClass().add("game_grid_button_lose");
                pos3.getStyleClass().add("game_grid_button_lose");
            }
        } else if (pos4.getText() == pos5.getText()
                && pos5.getText() == pos6.getText()
                && pos4.getText() != "") {
            whoWon = pos4.getText() == "X" ? "X" : "O";
            pos4.getStyleClass().clear();
            pos5.getStyleClass().clear();
            pos6.getStyleClass().clear();
            if (whoWon == "X") {
                pos4.getStyleClass().add("game_grid_button_win");
                pos5.getStyleClass().add("game_grid_button_win");
                pos6.getStyleClass().add("game_grid_button_win");
            } else {
                pos4.getStyleClass().add("game_grid_button_lose");
                pos5.getStyleClass().add("game_grid_button_lose");
                pos6.getStyleClass().add("game_grid_button_lose");
            }
        } else if (pos7.getText() == pos8.getText()
                && pos8.getText() == pos9.getText()
                && pos7.getText() != "") {
            whoWon = pos7.getText() == "X" ? "X" : "O";
            pos7.getStyleClass().clear();
            pos8.getStyleClass().clear();
            pos9.getStyleClass().clear();
            if (whoWon == "X") {
                pos7.getStyleClass().add("game_grid_button_win");
                pos8.getStyleClass().add("game_grid_button_win");
                pos9.getStyleClass().add("game_grid_button_win");
            } else {
                pos7.getStyleClass().add("game_grid_button_lose");
                pos8.getStyleClass().add("game_grid_button_lose");
                pos9.getStyleClass().add("game_grid_button_lose");
            }
            //Columns win check
        } else if (pos1.getText() == pos4.getText()
                && pos4.getText() == pos7.getText()
                && pos7.getText() != "") {
            whoWon = pos7.getText() == "X" ? "X" : "O";
            pos1.getStyleClass().clear();
            pos4.getStyleClass().clear();
            pos7.getStyleClass().clear();
            if (whoWon == "X") {
                pos1.getStyleClass().add("game_grid_button_win");
                pos4.getStyleClass().add("game_grid_button_win");
                pos7.getStyleClass().add("game_grid_button_win");
            } else {
                pos1.getStyleClass().add("game_grid_button_lose");
                pos4.getStyleClass().add("game_grid_button_lose");
                pos7.getStyleClass().add("game_grid_button_lose");
            }
        } else if (pos2.getText() == pos5.getText()
                && pos5.getText() == pos8.getText()
                && pos8.getText() != "") {
            whoWon = pos8.getText() == "X" ? "X" : "O";
            pos2.getStyleClass().clear();
            pos5.getStyleClass().clear();
            pos8.getStyleClass().clear();
            if (whoWon == "X") {
                pos2.getStyleClass().add("game_grid_button_win");
                pos5.getStyleClass().add("game_grid_button_win");
                pos8.getStyleClass().add("game_grid_button_win");
            } else {
                pos2.getStyleClass().add("game_grid_button_lose");
                pos5.getStyleClass().add("game_grid_button_lose");
                pos8.getStyleClass().add("game_grid_button_lose");
            }
        } else if (pos3.getText() == pos6.getText()
                && pos6.getText() == pos9.getText()
                && pos9.getText() != "") {
            whoWon = pos9.getText() == "X" ? "X" : "O";
            pos3.getStyleClass().clear();
            pos6.getStyleClass().clear();
            pos9.getStyleClass().clear();
            if (whoWon == "X") {
                pos3.getStyleClass().add("game_grid_button_win");
                pos6.getStyleClass().add("game_grid_button_win");
                pos9.getStyleClass().add("game_grid_button_win");
            } else {
                pos3.getStyleClass().add("game_grid_button_lose");
                pos6.getStyleClass().add("game_grid_button_lose");
                pos9.getStyleClass().add("game_grid_button_lose");
            }
            //Diagonal win check
        } else if (pos1.getText() == pos5.getText()
                && pos5.getText() == pos9.getText()
                && pos5.getText() != "") {
            whoWon = pos5.getText() == "X" ? "X" : "O";
            pos1.getStyleClass().clear();
            pos5.getStyleClass().clear();
            pos9.getStyleClass().clear();
            if (whoWon == "X") {
                pos1.getStyleClass().add("game_grid_button_win");
                pos5.getStyleClass().add("game_grid_button_win");
                pos9.getStyleClass().add("game_grid_button_win");
            } else {
                pos1.getStyleClass().add("game_grid_button_lose");
                pos5.getStyleClass().add("game_grid_button_lose");
                pos9.getStyleClass().add("game_grid_button_lose");
            }
        } else if (pos3.getText() == pos5.getText()
                && pos5.getText() == pos7.getText()
                && pos5.getText() != "") {
            whoWon = pos5.getText() == "X" ? "X" : "O";
            pos3.getStyleClass().clear();
            pos5.getStyleClass().clear();
            pos7.getStyleClass().clear();
            if (whoWon == "X") {
                pos3.getStyleClass().add("game_grid_button_win");
                pos5.getStyleClass().add("game_grid_button_win");
                pos7.getStyleClass().add("game_grid_button_win");
            } else {
                pos3.getStyleClass().add("game_grid_button_lose");
                pos5.getStyleClass().add("game_grid_button_lose");
                pos7.getStyleClass().add("game_grid_button_lose");
            }
            //Draw check
        } else if (currentBoardStatus.equals("")) {
            whoWon = "D";
        }

        return whoWon;
    }

    public String readBoard() {
        String boardStatus = "";
        if (pos1.getText().equals("")) {
            boardStatus = boardStatus + "1";
        }
        if (pos2.getText().equals("")) {
            boardStatus = boardStatus + "2";
        }
        if (pos3.getText().equals("")) {
            boardStatus = boardStatus + "3";
        }
        if (pos4.getText().equals("")) {
            boardStatus = boardStatus + "4";
        }
        if (pos5.getText().equals("")) {
            boardStatus = boardStatus + "5";
        }
        if (pos6.getText().equals("")) {
            boardStatus = boardStatus + "6";
        }
        if (pos7.getText().equals("")) {
            boardStatus = boardStatus + "7";
        }
        if (pos8.getText().equals("")) {
            boardStatus = boardStatus + "8";
        }
        if (pos9.getText().equals("")) {
            boardStatus = boardStatus + "9";
        }
        return boardStatus;
    }

    public void useHardAI1() {
        if (checkWin() == "") {
            String currentBoardStatus = readBoard();
            int[] currentStatusArr = new int[currentBoardStatus.length()];

            for (int i = 0; i < currentBoardStatus.length(); i++) {
                currentStatusArr[i]
                        = Character.getNumericValue(currentBoardStatus.charAt(i));
            }

            Random r = new Random();
            int nextMoveIndex = r.nextInt(currentStatusArr.length);
            int nextMoveNum = currentStatusArr[nextMoveIndex];

            switch (nextMoveNum) {
                case 1:
                    pos1.setText("O");
                    break;
                case 2:
                    pos2.setText("O");
                    break;
                case 3:
                    pos3.setText("O");
                    break;
                case 4:
                    pos4.setText("O");
                    break;
                case 5:
                    pos5.setText("O");
                    break;
                case 6:
                    pos6.setText("O");
                    break;
                case 7:
                    pos7.setText("O");
                    break;
                case 8:
                    pos8.setText("O");
                    break;
                case 9:
                    pos9.setText("O");
                    break;
            }

            //AI recording its move in the record string
            String nextMoveString;
            nextMoveString = Integer.toString(nextMoveNum);
            gameRecord += nextMoveString;
        }
    }

    public void checkGameDone() {
        switch (checkWin()) {
            case "X":
                winText.setVisible(true);
                pAgainBtn.setVisible(true);
                pAgainBtn.setDisable(false);
                pAgainBtn.getStyleClass().add("main_button_win");
                saveReplayBtn.setVisible(true);
                saveReplayBtn.setDisable(false);
                reportGameEnding();
                break;
            case "O":
                loseText.setVisible(true);
                pAgainBtn.setVisible(true);
                pAgainBtn.setDisable(false);
                pAgainBtn.getStyleClass().add("main_button_lose");
                saveReplayBtn.setVisible(true);
                saveReplayBtn.setDisable(false);
                break;
            case "D":
                drawText.setVisible(true);
                pAgainBtn.setVisible(true);
                pAgainBtn.setDisable(false);
                pAgainBtn.getStyleClass().add("main_button");
                saveReplayBtn.setVisible(true);
                saveReplayBtn.setDisable(false);
                break;
            default:
                break;
        }

        if (checkWin() == "X" || checkWin() == "O" || checkWin() == "D") {
            pos1.setDisable(true);
            pos2.setDisable(true);
            pos3.setDisable(true);
            pos4.setDisable(true);
            pos5.setDisable(true);
            pos6.setDisable(true);
            pos7.setDisable(true);
            pos8.setDisable(true);
            pos9.setDisable(true);
        }
    }

    public void reportGameEnding() {
        Player player = new Player(myUserName);
        ClientOperationMsg xoMsg = new ClientOperationMsg(
                Operation.SINGLE_MODE_GAME_FINISHED,
                player
        );
        PSFromcontroller.println(new Gson().toJson(xoMsg));
    }
}
