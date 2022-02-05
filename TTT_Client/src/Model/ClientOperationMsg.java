package Model;

import java.util.Vector;

public class ClientOperationMsg {

    private String typeOfOperation = null; //operations like signin signup
    private Boolean operationResult;       // resulted operation from server
    public Vector<Player> players = null;    //vector to hold all players
    private Player player = null;          //object from player class
    private Game game = null;              //object from game class
    private int fieldPosition;             //number of XO Quarter
    private char signPlayed; //X or O Sign
    public Vector<Record> records;

    //constructors
    public ClientOperationMsg() {
        players = new Vector<>();
        records = new Vector<>();
    }

    public ClientOperationMsg(String _typeOfOperation) {
        typeOfOperation = _typeOfOperation;
    }

    public ClientOperationMsg(String _typeOfOperation, Player _player) {
        typeOfOperation = _typeOfOperation;
        player = _player;
    }

    public ClientOperationMsg(String _typeOfOperation, Game _game) {
        typeOfOperation = _typeOfOperation;
        game = _game;
    }

    public ClientOperationMsg(String _typeOfOperation, Player _player, Game _game) {
        typeOfOperation = _typeOfOperation;
        player = _player;
        game = _game;
    }

    public ClientOperationMsg(String _typeOfOperation, Game _game, int _fieldPosition, char _signPlayed) {
        typeOfOperation = _typeOfOperation;
        game = _game;
        fieldPosition = _fieldPosition;
        signPlayed = _signPlayed;
    }

    //setters
    public void setTypeOfOperation(String _typeOfOperation) {
        typeOfOperation = _typeOfOperation;
    }

    public void setOperationResult(boolean _operationResult) {
        operationResult = _operationResult;
    }

    public void setFieldNumber(int _fieldNumber) {
        fieldPosition = _fieldNumber;
    }

    public void setSignPlayed(char _signPlayed) {
        signPlayed = _signPlayed;
    }

    //getters
    public String getOperationType() {
        return typeOfOperation;
    }

    public Boolean getOperationResults() {
        return operationResult;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public int getFieldPosition() {
        return fieldPosition;
    }

    public char getSignPlayed() {
        return signPlayed;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
