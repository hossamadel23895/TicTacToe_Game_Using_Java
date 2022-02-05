package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javafx.application.Platform;
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

public class GameSceneReplayController implements Initializable {

    private String record;

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
    @FXML
    private Label away;

    @FXML
    private Label home;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String player1 = ReplaysListSceneController.chosenReplay.getPlayer1();
        String player2 = ReplaysListSceneController.chosenReplay.getPlayer2();
        home.setText(player1);
        away.setText(player2);

    }

    @FXML
    private Button pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, pos9, replayBtn;

    @FXML
    private Text winText, loseText, drawText;

    @FXML
    private void backHandler(ActionEvent event) throws IOException {
        Parent backParent = FXMLLoader.load(getClass().getResource("/View/SelectionMode.fxml"));
        Scene backScene = new Scene(backParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(backScene);
        window.show();
    }

    @FXML
    private void replayBtnHandler(ActionEvent ae) {
        replayBtn.setVisible(false);
        replayBtn.setDisable(true);
        replayGame();
    }

    public String checkWin() {
        String whoWon = "";
        String currentBoardStatus = readBoard();
        //Rows win check
        if (pos1.getText() == pos2.getText() && pos2.getText() == pos3.getText() && pos1.getText() != "") {
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

        } else if (pos4.getText() == pos5.getText() && pos5.getText() == pos6.getText() && pos4.getText() != "") {
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

        } else if (pos7.getText() == pos8.getText() && pos8.getText() == pos9.getText() && pos7.getText() != "") {
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
        } else if (pos1.getText() == pos4.getText() && pos4.getText() == pos7.getText() && pos7.getText() != "") {
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

        } else if (pos2.getText() == pos5.getText() && pos5.getText() == pos8.getText() && pos8.getText() != "") {
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

        } else if (pos3.getText() == pos6.getText() && pos6.getText() == pos9.getText() && pos9.getText() != "") {
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
        } else if (pos1.getText() == pos5.getText() && pos5.getText() == pos9.getText() && pos5.getText() != "") {
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

        } else if (pos3.getText() == pos5.getText() && pos5.getText() == pos7.getText() && pos5.getText() != "") {
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

    public void checkGameDone() {
        switch (checkWin()) {
            case "X":
                winText.setVisible(true);
                break;

            case "O":
                loseText.setVisible(true);
                break;

            case "D":
                drawText.setVisible(true);
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

    public void replayGame() {
        String gameRec = ReplaysListSceneController.chosenReplay.getReplay();

        System.out.println("game" + gameRec);
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                char[] gameRecChar = gameRec.toCharArray();
                int[] gameRecInt = new int[gameRecChar.length];
                for (int i = 0; i < gameRecChar.length; i++) {
                    gameRecInt[i] = Character.getNumericValue(gameRecChar[i]);
                    int counter = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            String nextPlay = counter % 2 == 0 ? "X" : "O";
                            switch (gameRecInt[counter]) {
                                case 1:
                                    pos1.setText(nextPlay);
                                    break;
                                case 2:
                                    pos2.setText(nextPlay);
                                    break;
                                case 3:
                                    pos3.setText(nextPlay);
                                    break;
                                case 4:
                                    pos4.setText(nextPlay);
                                    break;
                                case 5:
                                    pos5.setText(nextPlay);
                                    break;
                                case 6:
                                    pos6.setText(nextPlay);
                                    break;
                                case 7:
                                    pos7.setText(nextPlay);
                                    break;
                                case 8:
                                    pos8.setText(nextPlay);
                                    break;
                                case 9:
                                    pos9.setText(nextPlay);
                                    break;
                            }
                        }
                    });
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1000));
                }
                checkGameDone();
            }
        });

        th.start();
    }

}
