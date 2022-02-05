package Model;

import static com.mysql.jdbc.StringUtils.isNullOrEmpty;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Database {

  private PreparedStatement pst;
  private ResultSet rs;
  static Connection con = null;
  static String db_name = "ttt_database";
  static String url = "jdbc:mysql://localhost:3306/" + db_name;
  static String username = "root"; //your name
  static String password = "4424392Yahz"; //your password

  //this function created to connect to the database
  public static void dbConnect()
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    con = DriverManager.getConnection(url, username, password);
  }

  //this function created to disconnect to the database
  public static void dbDisconnect() throws SQLException {
    con.close();
  }

  // Login Methode
  public static int login(String username, String password)
    throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement(
        "Select id from player where username = ? and password = ?"
      );
    statement.setString(1, username);
    statement.setString(2, password);

    ResultSet rs = statement.executeQuery();
    int id = -1;
    if (rs.next()) {
      id = rs.getInt("id");
      updatePlayerStatus(id, 1);
    }

    return id;
  }

  // Register Methode
  //this function take array of strings that represent player data and save this data into the database
  public static int register(String[] arr)
    throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    int success_register = 1;
    Database database = new Database();
    int userName_exist = database.checkUserExistance(arr[0]);

    if (userName_exist == 0 ) { // username is not taken
      PreparedStatement statement;
      statement =
        con.prepareStatement(
          "insert into player(`username`,`email`,`password`)values(?,?,?)"
        );
      statement.setString(1, arr[0]);
      statement.setString(2, arr[1]);
      statement.setString(3, arr[2]);
      statement.executeUpdate();
      System.out.println("username is not taken");
    } else {
      success_register = 0;
    }
    return success_register;
  }

  public int checkUserExistance(String user) {
      PreparedStatement statement;
      ResultSet rs;
    try {
      statement =
        con.prepareStatement(
          "select username from player where username=?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY
        );
      statement.setString(1, user);
      rs = statement.executeQuery();
      if (!rs.next()) {
        statement.close();
        rs.close();
        return 0;
      }
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return 1;
  }

 

  //this function take player id and return the status of this player
  public static int getPlayerStatus(int playerId)
    throws SQLException, IndexOutOfBoundsException {
    PreparedStatement statement;
    statement =
      con.prepareStatement("Select player_status from player where id = ?");
    statement.setInt(1, playerId);
    ResultSet rs = statement.executeQuery();
    int status = 0;
    if (rs.next()) {
      status = rs.getInt("player_status");
    }

    return status;
  }

  //this function take player id and score value and update in the database the score of this player by increase it with the new value
  public static void updatePlayerScore(int playerId, int value)
    throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement(
        "update player " +
        "set player_points = (player_points + ? )" +
        "where id = ?"
      );
    statement.setInt(1, value);
    statement.setInt(2, playerId);
    statement.executeUpdate();
  }

  //this function return all players in the database in observable list to put it in table view in GUI
  public static ObservableList<Player> getPlayers()
      throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    ObservableList<Player> players = FXCollections.observableArrayList();
    Statement stmt = con.createStatement();
    String queryString = new String(
        "select * from player order by  player_points desc");
    ResultSet rs = stmt.executeQuery(queryString);
    while (rs.next()) {
      int id = rs.getInt(1);
      String name = rs.getString(2);
      String email = rs.getString(3);
      String password = rs.getString(4);
      int points = rs.getInt(5);
      int status = rs.getInt(6);

      Player p = new Player(name, password, email);
      p.setStringStatus(status);
      p.setScore(points);
      players.add(p);
    }
    return players;
  }
  public static Vector<Record> getRecords(String player)
      throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    Vector<Record> records = new Vector<>();
    PreparedStatement statement = con.prepareStatement(
        "select * from Replay where player1=? or player2=? ");//
    statement.setString(1, player);
    statement.setString(2, player);
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      String player1 = rs.getString(1);
      String player2 = rs.getString(2);
      String Record = rs.getString(3);
      String time = rs.getString(4);

      Record R = new Record(player1, player2, Record, time);
      R.setPlayer1(player1);
      R.setPlayer2(player2);
      R.setReplay(Record);
      R.setTime(time);
      records.add(R);
    }
    return records;
  }
      //this function created to add game between two players in data base
    public static int addPlayersGame(int player1Id , int player2Id) throws SQLException{
        int id = 0;
        PreparedStatement statement;         
        statement=con.prepareStatement("insert into game(`player1_id`,`player2_id`)values(?,?)",Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, player1Id);
        statement.setInt(2, player2Id);			
        statement.executeUpdate();
        ResultSet rs=statement.getGeneratedKeys();
        if(rs.next()){
            id=rs.getInt(1);
        }
        return id;
    }

  

  // this function is created to change the status of player
  public static void updatePlayerStatus(int playerId, int statusValue)
    throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement("update player set player_status= ? where id = ? ");
    
    statement.setInt(1, statusValue);
    statement.setInt(2, playerId);
    statement.executeUpdate();
  }




  //this function take player userName and return the status of this player
  public static int getPlayerStatus(String playerUsername)
    throws SQLException, IndexOutOfBoundsException {
    PreparedStatement statement;
    statement =
      con.prepareStatement(
        "Select player_status from player where username = ?"
      );
    statement.setString(1, playerUsername);
    ResultSet rs = statement.executeQuery();
    int status = 0;
    if (rs.next()) {
      status = rs.getInt("player_status");
    }

    return status;
  }

  // this function is created to change the status of player here tale playerUser name as a parameter
  public static void updatePlayerStatus(String playerUsername, int statusValue)
    throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement(
        "update player set player_status= ? where username = ? "
      );
    statement.setInt(1, statusValue);
    statement.setString(2, playerUsername);
    statement.executeUpdate();
  }

  //this function take player username and score value and update in the database the score of this player by increase it with the new value
  public static void updatePlayerScore(String playerUsername, int value)
    throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement(
        "update player " +
        "set player_points = (player_points + ? )" +
        "where username = ?"
      );
    statement.setInt(1, value);
    statement.setString(2, playerUsername);
    statement.executeUpdate();
  }

  public static int getPoints(int player_id) throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement("Select player_points from player where id = ?");
    statement.setInt(1, player_id);
    ResultSet rs = statement.executeQuery();
    int points = 0;
    if (rs.next()) {
      points = rs.getInt("player_points");
    }

    return points;
  }

  public static void setWinner(
    int game_id,
    int player1_id,
    int player2_id,
    int winner_id
  )
    throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement(
        "update game set winner_id= ? where id = ? and player1_id = ? and player2_id = ? "
      );
    statement.setInt(1, winner_id);
    statement.setInt(2, game_id);
    statement.setInt(3, player1_id);
    statement.setInt(4, player2_id);
    statement.executeUpdate();
  }

  public static void logout(int player_id) throws SQLException {
    PreparedStatement statement;
    statement =
      con.prepareStatement("update player set player_status= ? where id = ?");
    statement.setInt(1, 0);
    statement.setInt(2, player_id);
    statement.executeUpdate();
  }

  public static void changeAllStatus() throws SQLException {
    PreparedStatement statement;
    statement = con.prepareStatement("update player set player_status= ?");
    statement.setInt(1, 0);
    statement.executeUpdate();
  }

  public static void removeGame(int gameId) throws SQLException {
    PreparedStatement statement;
    statement = con.prepareStatement("delete from game where id = ?");
    statement.setInt(1, gameId);
    statement.executeUpdate();
  }


  
  public static void Record(String player1,String player2,String Record,String time) throws SQLException{
         PreparedStatement statement;
            statement = con.prepareStatement("insert into replay(`player1`,`player2`,`Record`,`time`)values(?,?,?,?)");
            statement.setString(1, player1);
            statement.setString(2, player2);
            statement.setString(3, Record);
            statement.setString(4, time);
            statement.executeUpdate();

    }
}
