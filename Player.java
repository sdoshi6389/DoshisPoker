
//prototype for each player on the table
public class Player {
    private double balance;
    private String name;
    private String position;
    private boolean hasFolded;
    private double preflopBet;
    private double flopBet;
    private double turnBet;
    private double riverBet;
    private String preflopAction;
    private String flopAction;
    private String turnAction;
    private String riverAction;
    private String playerMode; // easy, medium, hard
    private String playerType; // very tight, tight, loose, very loose
    private double vpip;
    private String cardOne;
    private String cardTwo;
    private double handValue;
    private int flushValue;
    private double tripsValue;
    private double highCardValue;
    private double onePairValue;
    public Player() {
        this.balance = 0.0;
        this.name = null;
        this.position = null;
        this.hasFolded = false;
        this.preflopBet = 0.0;
        this.flopBet = 0.0;
        this.turnBet = 0.0;
        this.riverBet = 0.0;
        this.preflopAction = null;
        this.flopAction = null;
        this.turnAction = null;
        this.riverAction = null;
        this.playerMode = null;
        this.playerType = null;
        this.vpip = 0.0;
        this.cardOne = null;
        this.cardTwo = null;
        this.handValue = 0;
        this.flushValue = 0;
        this.tripsValue = 0;
        this.highCardValue = 0;
        this.onePairValue = 0;
    }
    public Player(double balance, String name, String position, boolean hasFolded, double preflopBet, double flopBet,
                  double turnBet,
                  double riverBet
            , String preflopAction, String flopAction, String turnAction, String riverAction, String playerMode,
                  String playerType, double vpip, String cardOne, String cardTwo, double handValue,
                  int flushValue, double tripsValue, double highCardValue, double onePairValue) {
        this.balance = balance;
        this.name = name;
        this.position = position;
        this.hasFolded = hasFolded;
        this.preflopBet = preflopBet;
        this.flopBet = flopBet;
        this.turnBet = turnBet;
        this.riverBet = riverBet;
        this.preflopAction = preflopAction;
        this.flopAction = flopAction;
        this.turnAction = turnAction;
        this.riverAction = riverAction;
        this.playerMode = playerMode;
        this.playerType = playerType;
        this.vpip = vpip;
        this.cardOne = cardOne;
        this.cardTwo = cardTwo;
        this.handValue = handValue;
        this.flushValue = flushValue;
        this.tripsValue = tripsValue;
        this.highCardValue = highCardValue;
        this.onePairValue = onePairValue;
    }
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean getHasFolded() { return hasFolded; }

    public void setHasFolded(boolean hasFolded) { this.hasFolded = hasFolded; }

    public double getPreflopBet() { return preflopBet; }

    public void setPreflopBet(double preflopBet) {
        this.preflopBet = preflopBet;
    }

    public double getFlopBet() {
        return flopBet;
    }

    public void setFlopBet(double flopBet) {
        this.flopBet = flopBet;
    }

    public double getTurnBet() {
        return turnBet;
    }

    public void setTurnBet(double turnBet) {
        this.turnBet = turnBet;
    }

    public double getRiverBet() {
        return riverBet;
    }

    public void setRiverBet(double riverBet) {
        this.riverBet = riverBet;
    }

    public String getPreflopAction() {
        return preflopAction;
    }

    public void setPreflopAction(String preflopAction) {
        this.preflopAction = preflopAction;
    }

    public String getFlopAction() {
        return flopAction;
    }

    public void setFlopAction(String flopAction) {
        this.flopAction = flopAction;
    }

    public String getTurnAction() {
        return turnAction;
    }

    public void setTurnAction(String turnAction) {
        this.turnAction = turnAction;
    }

    public String getRiverAction() {
        return riverAction;
    }

    public void setRiverAction(String riverAction) { this.riverAction = riverAction; }

    public String getPlayerMode() {
        return playerMode;
    }

    public void setPlayerMode(String playerMode) {
        this.playerMode = playerMode;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public double getVpip() {
        return vpip;
    }

    public void setVpip(double vpip) {
        this.vpip = vpip;
    }

    public String getCardOne() { return cardOne; }

    public void setCardOne(String cardOne) { this.cardOne = cardOne; }

    public String getCardTwo() { return cardTwo; }

    public void setCardTwo(String cardTwo) { this.cardTwo = cardTwo; }

    public double getHandValue() { return handValue; }

    public void setHandValue(int handValue) { this.handValue = handValue; }

    public int getFlushValue() { return flushValue; }

    public void setFlushValue(int flushValue) { this.flushValue = flushValue; }

    public double getTripsValue() { return tripsValue; }

    public void setTripsValue(double tripsValue) { this.tripsValue = tripsValue; }

    public double getHighCardValue() { return highCardValue; }

    public void setHighCardValue(double highCardValue) { this.highCardValue = highCardValue; }

    public double getOnePairValue() { return onePairValue; }

    public void setOnePairValue(double onePairValue) { this.onePairValue = onePairValue; }

}

