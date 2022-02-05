package Model;

import Controller.ServerGui;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class ServerThread extends Thread {

  private final Socket socket;
  private DataInputStream dis;
  private PrintStream ps;
  private Player newPlayer;
  private Game game = new Game();
  static Vector<ServerThread> playersVector = new Vector<>(); //sock
  static HashMap<Integer, ServerThread> onlinePlayers = new HashMap<>();
  static HashMap<String, Integer> usernameToId = new HashMap<>();
  Gson g = new Gson();

  public ServerThread(Socket s) {
    this.socket = s;
  }

  @Override
  public void run() {
    try {
      dis = new DataInputStream(socket.getInputStream());
      ps = new PrintStream(socket.getOutputStream(), true);
      newPlayer = new Player();
      //playersVector.add(this);
      String message;
      while (true) {
        message = dis.readLine();
        System.out.println("message:" + message);
        if (!message.isEmpty()) {
          try {
            JSONParser parser = new JSONParser();
            System.out.println("in check msg" + message);
            Object file;
            file = parser.parse(message);
            JSONObject jsonObjectdecode = (JSONObject) file;
            String request = (String) jsonObjectdecode.get("type");
            if (request == null) {
              request = "i donot care";
            }
            System.out.println("Request = " + request);
            if (request.equals("register")) {
              String username = (String) jsonObjectdecode.get("username");
              String password = (String) jsonObjectdecode.get("password");
              String email = (String) jsonObjectdecode.get("email");
              String[] arr = { username, email, password };
              Gson g = new Gson();
              InsideXOGame objMsg = new InsideXOGame();
              if (Database.register(arr) == 1) {
                refreshList();
                objMsg.setOperationResult(true);
                objMsg.setTypeOfOperation("signUpAccepted");
                ps.println(g.toJson(objMsg));
                System.out.println("accepted");
                //ps.println("signUpAccepted");
              }  else {
                objMsg.setOperationResult(false);
                objMsg.setTypeOfOperation("signUpRejected");
                ps.println(g.toJson(objMsg));
                System.out.println("rejected");
              }
            } else if (request.equals("RecordVsComp")) {
              String player1 = (String) jsonObjectdecode.get("player1");
              String player2 = (String) jsonObjectdecode.get("player2");
              String Record = (String) jsonObjectdecode.get("Record");
              String time = (String) jsonObjectdecode.get("time");
              Database.Record(player1, player2, Record, time);
              System.out.println(Record + "===============");
            }
              else if (request.equals("viewRecords")) {
                Vector<Record> records = new Vector<>();
                String player = (String) jsonObjectdecode.get("player");
                records = Database.getRecords(player);
                InsideXOGame objMsg = new InsideXOGame();
                objMsg.setTypeOfOperation("viewRecordsfromServer");
                objMsg.setRecords(records);
                ps.println(g.toJson(objMsg));              
              }
             else {
              jsonMessageHandler(message);
            }
          } catch (ParseException ex) {
            ex.getStackTrace();
            System.out.println("error while call json message hundler ");
          } catch (org.json.simple.parser.ParseException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          } catch (SQLException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          } catch (ClassNotFoundException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          } catch (InstantiationException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          } catch (IllegalAccessException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
      }
    } catch (IOException ex) { //in catch exception here means client logged out - release resources , remove player from players vector , update its status
      ex.getStackTrace();
      System.out.println("server can not connect with client");
      try {
        socket.close();
        dis.close();
        ps.close();
        newPlayer.setStatus(false);
        onlinePlayers.remove(newPlayer.getPlayerId());
        usernameToId.remove(newPlayer.getUserName());
        try {
          Database.updatePlayerStatus(newPlayer.getUserName(), 0); //update status of player to be offline
        } catch (SQLException ex1) {
          Logger
            .getLogger(ServerThread.class.getName())
            .log(Level.SEVERE, null, ex1);
        }
        System.out.println("player is leaved and become offline");
      } catch (IOException e) {
        System.out.println("Error while closing socket connection from server");
        e.getStackTrace();
      }
    }
  }

  public Player getNewPlayer() {
    return newPlayer;
  }

  /**
   *
   * @param data
   * @throws ParseException
   *  method to switch on the type of player's request
   */
  private void jsonMessageHandler(String data) throws ParseException {
    Gson gson = new Gson();
    InsideXOGame msgObject = gson.fromJson(data, InsideXOGame.class);

    switch (msgObject.getTypeOfOperation()) {
      case "login":
        {
          try {
            handelLogInRequest(msgObject);
          } catch (SQLException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "playingSingleMode":
        {
          try {
            handelPlayingSingleModeRequest(msgObject);
          } catch (SQLException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "singleModeGameFinished":
        {
          try {
            handelSingleGameFinishedRequest(msgObject);
          } catch (SQLException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "retrivePlayers":
        handelRetrivePlayersRequest(msgObject);
        break;
      case "invite":
        handelInviteRequest(msgObject);
        break;
      case "invitationAccepted":
        {
          try {
            handelInvitationAcceptedRequest(msgObject);
          } catch (SQLException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "invitationRejected":
        handelInvitationRejectedRequest(msgObject);
        break;
      case "GamePlayMove":
        {
          try {
            handelGamePlayMoveRequest(msgObject);
          } catch (Exception ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "gameGotFinished":
        {
          try {
            handelGameGotFinishedRequest(msgObject);
          } catch (SQLException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "chatMessage":
        handelChatRequest(msgObject);
        break;
      case "back":
        handelBackRequest(msgObject);
        break;
      case "backFromonline":
        {
          try {
            handelBackFromOnlineRequest(msgObject);
          } catch (SQLException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          } catch (IndexOutOfBoundsException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          } catch (IllegalAccessException ex) {
            Logger
              .getLogger(ServerThread.class.getName())
              .log(Level.SEVERE, null, ex);
          }
        }
        break;
      case "logout":
        {
          handelLogoutRequest(msgObject);
        }
        break;
      case "newPlayerLoggedInPopUp":
        handelPopUpMessage(msgObject);
        break;
    }
  }

  private void handelLogInRequest(InsideXOGame objMsg) throws SQLException {
    Gson g = new Gson();
    Player player;
    String userName, password;
    int playerId = 0;
    player = objMsg.getPlayer();
    userName = player.getUserName();
    password = player.getPassword();
    playerId = Database.login(userName, password); //this function will return -1 if login faild
    if (playerId != -1) {
      refreshList();
      Database.updatePlayerStatus(playerId, 1);
      newPlayer.setPlayerId(playerId);
      newPlayer.setStatus(true);
      newPlayer.setUserName(userName);
      newPlayer.setPassword(password);
      newPlayer.setIsPlaying(false);
      newPlayer.setScore(Database.getPoints(playerId));
      System.out.println(newPlayer.getScore());
      ServerThread.onlinePlayers.put(playerId, this);
      ServerThread.usernameToId.put(userName, playerId);
      objMsg.getPlayer().setScore(newPlayer.getScore());
      objMsg.getPlayer().setStatus(true);
      objMsg.setOperationResult(true);
      objMsg.setTypeOfOperation("loginAccepted");
      ps.println(g.toJson(objMsg));
      handelPopUpMessage(objMsg); //to notify all online users with new player logged in
    } else {
      objMsg.setTypeOfOperation("logInRejected");
      objMsg.setOperationResult(false);
    }
  }

  private void handelPlayingSingleModeRequest(InsideXOGame objMsg)
    throws SQLException {
    Gson g = new Gson();
    Player player;
    String userName;
    player = objMsg.getPlayer();
    userName = player.getUserName();
    Database.updatePlayerStatus(userName, 2);
    objMsg.setOperationResult(true);
    objMsg.getPlayer().setIsPlaying(true);
    objMsg.getPlayer().setStatus(true); ///////////////
    objMsg.setTypeOfOperation("playingSingleMode");
    ps.println(g.toJson(objMsg));
  }

  private void handelSingleGameFinishedRequest(InsideXOGame msgObject)
    throws SQLException {
    Gson g = new Gson();
    Player player;
    String userName;
    player = msgObject.getPlayer();
    userName = player.getUserName();
    Database.updatePlayerScore(userName, 5);
    refreshList();
    newPlayer.setScore(newPlayer.getScore() + 5);
    msgObject.setOperationResult(true);
    msgObject.getPlayer().setScore(newPlayer.getScore());
    msgObject.setTypeOfOperation("singleModePlayerScoreUpdated");
    ps.println(g.toJson(msgObject));
    System.out.print(msgObject.getPlayer().getScore());
  }

  private void handelRetrivePlayersRequest(InsideXOGame msgObject) {
    Vector<Player> players = new Vector<>();
    for (Map.Entry<Integer, ServerThread> handler : onlinePlayers.entrySet()) {
      Player player = handler.getValue().getNewPlayer();
      if (player.getStatus() && !player.getIsPlaying()) { // add he is not busy
        players.add(player);
      }
    }
    msgObject.setOperationResult(true);
    msgObject.setTypeOfOperation("retrevingPlayersList");
    msgObject.players = players;
    ps.println(g.toJson(msgObject));
    //System.out.println(g.toJson(msgObject));
  }


   private void handelInviteRequest(InsideXOGame msgObject) {
        int opponentUserId = usernameToId.get(msgObject.getGame().getAwayPlayer());
        if(onlinePlayers.containsKey(opponentUserId)){//add he is not busy
            Player opponentPlayer = onlinePlayers.get(opponentUserId).getNewPlayer(); 
            if(opponentPlayer.getStatus() && !opponentPlayer.getIsPlaying()){
                msgObject.setOperationResult(true);
                msgObject.setTypeOfOperation("receivingInvitation");
                onlinePlayers.get(opponentUserId).getPs().println(g.toJson(msgObject));//print in json format
                return;
            }
        }
        msgObject.setOperationResult(false);
        msgObject.setTypeOfOperation("receivingInvitation");
        ps.println(g.toJson(msgObject));
    }

 
  
      private void handelInvitationAcceptedRequest(InsideXOGame msgObject) throws SQLException {
        int opponentUserId = usernameToId.get(msgObject.getGame().getHomeplayer());
        if(onlinePlayers.containsKey(opponentUserId)){
            Player opponentPlayer = onlinePlayers.get(opponentUserId).getNewPlayer(); 
            if(opponentPlayer.getStatus() && !opponentPlayer.getIsPlaying()){
                System.out.println(newPlayer.getPlayerId());
                System.out.println(opponentUserId);
                
                int gameId = Database.addPlayersGame(newPlayer.getPlayerId(), opponentUserId);
                game.setGameId(gameId);
                onlinePlayers.get(opponentUserId).getGame().setGameId(gameId);
                msgObject.setOperationResult(true);
                msgObject.setTypeOfOperation("inviationAcceptedFromServer");
                msgObject.getGame().setGameId(gameId);
                newPlayer.setOpponentId(opponentUserId);
                newPlayer.setIsPlaying(true);
                onlinePlayers.get(opponentUserId).getNewPlayer().setIsPlaying(true);
                onlinePlayers.get(opponentUserId).getNewPlayer().setOpponentId(newPlayer.getPlayerId());
                onlinePlayers.get(opponentUserId).getPs().println(g.toJson(msgObject));// json
                String home = msgObject.getGame().getHomeplayer();
                msgObject.getGame().setHomePlayer(msgObject.getGame().getAwayPlayer());
                msgObject.getGame().setAwayPlayer(home);                
                ps.println(g.toJson(msgObject));
                System.out.println(g.toJson(msgObject));
                return;
            }
        }
    }
  


  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  private void handelInvitationRejectedRequest(InsideXOGame msgObject) {
    int opponentUserId = usernameToId.get(msgObject.getGame().getHomeplayer());
    if (
      onlinePlayers.containsKey(opponentUserId) &&
      onlinePlayers.get(opponentUserId).getNewPlayer().getStatus()
    ) {
      msgObject.setOperationResult(true);
      msgObject.setTypeOfOperation("inviationRejectedFromServer");
      onlinePlayers.get(opponentUserId).getPs().println(g.toJson(msgObject)); // json
    } else {} //what if the inviter went off
  }

  private void handelGamePlayMoveRequest(InsideXOGame msgObject)
    throws SQLException, IndexOutOfBoundsException, IllegalAccessException {
    if (newPlayer.getOpponentId() == 0) {
      return;
    }
    char[] maz = game.getSavedGame();
    maz[msgObject.getFieldPosition() - 1] = msgObject.getSignPlayed();
    game.setSavedGame(maz);
    if (onlinePlayers.containsKey(newPlayer.getOpponentId())) {
      Player opponentPlayer = onlinePlayers
        .get(newPlayer.getOpponentId())
        .getNewPlayer();
      if (opponentPlayer.getStatus() && opponentPlayer.getIsPlaying()) {
        onlinePlayers
          .get(newPlayer.getOpponentId())
          .getGame()
          .setSavedGame(maz);
        msgObject.setOperationResult(true);
        msgObject.setTypeOfOperation("incomingMove");
        onlinePlayers
          .get(newPlayer.getOpponentId())
          .getPs()
          .println(g.toJson(msgObject)); // json
      }
    } else { // other player went off line
      msgObject
        .getGame()
        .setMessage("sorry the opponent left your game is saved!");
      msgObject.getGame().setAwayPlayer("Server");
      msgObject.getGame().setHomePlayer("Server");
      msgObject.setTypeOfOperation("chatMessageFromServer");
      ps.println(g.toJson(msgObject));
      newPlayer.setOpponentId(0);
    }
    System.out.println(game.getSavedGame());
  }

  private void handelGameGotFinishedRequest(InsideXOGame msgObject)
    throws SQLException {
    msgObject.setOperationResult(true);
    msgObject.setTypeOfOperation("gameGotFinishedSuccessfully");
    Database.setWinner(
      game.getGameId(),
      newPlayer.getPlayerId(),
      newPlayer.getOpponentId(),
      newPlayer.getPlayerId()
    );
    Database.setWinner(
      game.getGameId(),
      newPlayer.getOpponentId(),
      newPlayer.getPlayerId(),
      newPlayer.getPlayerId()
    );
    //Database.removeSavedGame(game.getGameId());
    newPlayer.setIsPlaying(false);
    onlinePlayers
      .get(newPlayer.getOpponentId())
      .getNewPlayer()
      .setIsPlaying(false);
    onlinePlayers
      .get(newPlayer.getOpponentId())
      .getNewPlayer()
      .setOpponentId(0);
    newPlayer.setOpponentId(0);
    Database.updatePlayerScore(newPlayer.getPlayerId(), 10);
    refreshList();
    newPlayer.setScore(Database.getPoints(newPlayer.getPlayerId()));
    msgObject.getPlayer().setScore(Database.getPoints(newPlayer.getPlayerId()));
    msgObject.setPlayer(newPlayer);
    ps.println(g.toJson(msgObject));
    System.out.print(msgObject.getPlayer().getScore());
  }

  private void handelChatRequest(InsideXOGame msgObject) {
    if (
      onlinePlayers.containsKey(newPlayer.getOpponentId()) &&
      onlinePlayers.get(newPlayer.getOpponentId()).getNewPlayer().getStatus()
    ) {
      msgObject.setOperationResult(true);
      msgObject.setTypeOfOperation("chatMessageFromServer");
      onlinePlayers
        .get(newPlayer.getOpponentId())
        .getPs()
        .println(g.toJson(msgObject)); //json
    }
  }

  private void handelBackRequest(InsideXOGame msgObject) {
    newPlayer.setIsPlaying(false);
    msgObject.setPlayer(newPlayer);
    msgObject.setOperationResult(true);
    msgObject.setTypeOfOperation("backFromServer");
    ps.println(g.toJson(msgObject)); //json
  }

  public PrintStream getPs() {
    return ps;
  }

  public void setPs(PrintStream ps) {
    this.ps = ps;
  }

  private void handelBackFromOnlineRequest(InsideXOGame msgObject)
    throws SQLException, IndexOutOfBoundsException, IllegalAccessException {
    newPlayer.setIsPlaying(false);
    System.out.println(g.toJson(msgObject));
    if (
      !msgObject.getGame().getIsFinished() && newPlayer.getOpponentId() != 0
    ) {
      System.out.println(game.getSavedGame().toString());
     
      msgObject
        .getGame()
        .setMessage("sorry the opponent left your game is saved!");
      msgObject.getGame().setAwayPlayer("Server");
      msgObject.getGame().setHomePlayer("Server");
      handelChatRequest(msgObject);
    }
    msgObject.setTypeOfOperation("backFromServer");
    if (newPlayer.getOpponentId() != 0) {
      onlinePlayers
        .get(newPlayer.getOpponentId())
        .getNewPlayer()
        .setOpponentId(0);
      newPlayer.setOpponentId(0);
    }
    ps.println(g.toJson(msgObject));
  }

  private void handelLogoutRequest(InsideXOGame msgObject) {
    try {
      socket.close();
      dis.close();
      ps.close();
    } catch (IOException ex) {
      Logger
        .getLogger(ServerThread.class.getName())
        .log(Level.SEVERE, null, ex);
    }

    newPlayer.setStatus(false);
    onlinePlayers.remove(newPlayer.getPlayerId());
    usernameToId.remove(newPlayer.getUserName());
    try {
      Database.logout(newPlayer.getPlayerId());
      //Database.updatePlayerStatus(newPlayer.getUserName(),0); //update status of player to be offline
      refreshList();
    } catch (SQLException ex1) {
      Logger
        .getLogger(ServerThread.class.getName())
        .log(Level.SEVERE, null, ex1);
    }

    System.out.println("player is leaved and become offline");
  }

  public void refreshList() {
    //System.out.println("hello world");
    Platform.runLater(
      () -> {
        ServerGui.test.listPlayers();
      }
    );
  }

  private void handelPopUpMessage(InsideXOGame msgObject) {
    for (Map.Entry<Integer, ServerThread> onlinePlayer : onlinePlayers.entrySet()) { //show pop up to all players of new online player
      if (onlinePlayer.getKey() != msgObject.getPlayer().getPlayerId()) {
        msgObject.setTypeOfOperation("newPlayerLoggedInPopUp");

        onlinePlayer.getValue().getPs().println(g.toJson(msgObject));
      }
    }
  }
}
