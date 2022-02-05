package Model;

public class Record {
    String player1;
    String player2;
    String replay;
    String time;


     public Record (String _player1,String _player2,String _replay,String _time)
    {
        player1 = _player1;
        player2  = _player2;
        replay = _replay;
        time = _time;
    };
    
    public String getPlayer1() {
        return player1;
    }
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }
    public String getPlayer2() {
        return player2;
    }
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }
    public String getReplay() {
        return replay;
    }
    public void setReplay(String replay) {
        this.replay = replay;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
  
}
