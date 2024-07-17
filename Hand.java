import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
public class Hand {

    //constants
    private String[] threePlayer = {"sb", "bb", "d"};
    private String[] fourPlayer = {"sb", "bb", "co", "d"};
    private String[] fivePlayer = {"sb", "bb", "utg", "co", "d"};
    private String[] sixPlayer = {"sb", "bb", "utg", "lj", "co", "d"};
    private String[] sevenPlayer = {"sb", "bb", "utg", "lj", "hj", "co", "d"};
    private String[] cards = {
            "2-s", "3-s", "4-s", "5-s", "6-s", "7-s", "8-s", "9-s", "10-s", "J-s", "Q-s", "K-s", "A-s",
            "2-h", "3-h", "4-h", "5-h", "6-h", "7-h", "8-h", "9-h", "10-h", "J-h", "Q-h", "K-h", "A-h",
            "2-d", "3-d", "4-d", "5-d", "6-d", "7-d", "8-d", "9-d", "10-d", "J-d", "Q-d", "K-d", "A-d",
            "2-c", "3-c", "4-c", "5-c", "6-c", "7-c", "8-c", "9-c", "10-c", "J-c", "Q-c", "K-c", "A-c"
    };

    //fields
    private ArrayList<Player> players = new ArrayList<>();
    private int playerCount;
    private ArrayList<Integer> numbers = new ArrayList<>();
    private double pot;
    public Hand(ArrayList<Player> possiblePlayers) {
        ArrayList<Player> finalPlayers = new ArrayList<>();
        for (int i = 0; i < possiblePlayers.size(); i++) {
            if (possiblePlayers.get(i).getBalance() == 0) {
                //do nothing
            } else {
                finalPlayers.add(possiblePlayers.get(i));
            }
        }
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < 52; i++) {
            numbers.add(i);
        }
        this.players = finalPlayers;
        this.numbers = numbers;
        this.playerCount = finalPlayers.size();
        this.pot = 0.0;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Integer> getNumbers() { return numbers; }

    public void setNumbers(ArrayList<Integer> numbers) { this.numbers = numbers; }

    public double getPot() { return pot; }

    public void setPot(double pot) { this.pot = pot; }

    //returns the small blind player
    public Player getSmallBlind(ArrayList<Player> players) {
        Player smallBlind = new Player();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPosition() == null) {
                return null;
            }
            if (players.get(i).getPosition().equals("sb")) {
                smallBlind = players.get(i);
                break;
            }
        }
        return smallBlind;
    }

    //returns the big blind player
    public Player getBigBlind(ArrayList<Player> players) {
        Player bigBlind = new Player();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPosition() == null) {
                return null;
            }
            if (players.get(i).getPosition().equals("bb")) {
                bigBlind = players.get(i);
                break;
            }
        }
        return bigBlind;
    }

    //gets the index of the small blind player
    public int getSmallBlindIndex(ArrayList<Player> players) {
        int index = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPosition().equals("sb")) {
                index = i;
                break;
            }
        }
        return index;
    }

    //assigns positions to everybody if first hand or rotates positions if not first hand
    public void setPositions(ArrayList<Player> players) {
        Player prevSmallBlind = getSmallBlind(players);
        if (playerCount == 3) {
            if (prevSmallBlind == null) {
                for (int i = 0; i < playerCount; i++) {
                    players.get(i).setPosition(threePlayer[i]);
                }
            } else {
                for (int i = 0; i < playerCount; i++) {
                    players.get(((getSmallBlindIndex(players) + 1 + i) % playerCount)).setPosition(threePlayer[i]);
                }
            }
        } else if (playerCount == 4) {
            if (prevSmallBlind == null) {
                for (int i = 0; i < playerCount; i++) {
                    players.get(i).setPosition(fourPlayer[i]);
                }
            } else {
                for (int i = 0; i < playerCount; i++) {
                    players.get(((getSmallBlindIndex(players) + 1 + i) % playerCount)).setPosition(fourPlayer[i]);
                }
            }
        } else if (playerCount == 5) {
            if (prevSmallBlind == null) {
                for (int i = 0; i < playerCount; i++) {
                    players.get(i).setPosition(fivePlayer[i]);
                }
            } else {
                for (int i = 0; i < playerCount; i++) {
                    players.get(((getSmallBlindIndex(players) + 1 + i) % playerCount)).setPosition(fivePlayer[i]);
                }
            }
        } else if (playerCount == 6) {
            if (prevSmallBlind == null) {
                for (int i = 0; i < playerCount; i++) {
                    players.get(i).setPosition(sixPlayer[i]);
                }
            } else {
                for (int i = 0; i < playerCount; i++) {
                    players.get(((getSmallBlindIndex(players) + 1 + i) % playerCount)).setPosition(sixPlayer[i]);
                }
            }
        } else if (playerCount == 7) {
            if (prevSmallBlind == null) {
                for (int i = 0; i < playerCount; i++) {
                    players.get(i).setPosition(sevenPlayer[i]);
                }
            } else {
                for (int i = 0; i < playerCount; i++) {
                    players.get(((getSmallBlindIndex(players) + 1 + i) % playerCount)).setPosition(sevenPlayer[i]);
                }
            }
        }
    }

    //returns an array with small blind first and rest are ordered
    public ArrayList<Player> orderPlayers(ArrayList<Player> players) {
        ArrayList<Player> orderedPlayers = new ArrayList<Player>();
        int index = getSmallBlindIndex(players);
        for (int i = 0; i < players.size(); i++) {
            orderedPlayers.add(players.get(index + i));
        }
        return orderedPlayers;
    }

    //gives each player two cards
    public void assignCards(ArrayList<Player> players) {
        Files.writeToFile("cardLedger.txt", "Hand: ");
        for (int i = 0; i < players.size(); i++) {
            int randomOne = (int)(numbers.size() * Math.random());
            players.get(i).setCardOne(cards[numbers.get(randomOne)]);
            Files.writeToFile("cardLedger.txt",
                    players.get(i).getName() + ", card One: " + cards[numbers.get(randomOne)]);
            numbers.remove(randomOne);
            int randomTwo = (int)(numbers.size() * Math.random());
            players.get(i).setCardTwo(cards[numbers.get(randomTwo)]);
            Files.writeToFile("cardLedger.txt",
                    players.get(i).getName() + ", card Two: " + cards[numbers.get(randomTwo)]);
            numbers.remove(randomTwo);
        }
        //Files.writeToFile("cardLedger.txt", "\n");
    }

    //have to still incorporate changes to balance of each player as well as whether a person is all in, if a person
    //is all in then take them out of the orderedPlayers array but put them in a separate allIn array which will be
    //dealt with by the river
    public ArrayList<Player> preFlop(ArrayList<Player> players) {
        Scanner scanner = new Scanner(System.in);
        setPositions(players);
        assignCards(players);
        ArrayList<Player> orderedPlayers = orderPlayers(players);
        ArrayList<Player> foldedPlayers = new ArrayList<Player>();
        int startingIndex = 2;
        int indexOfLastRaiser = 1;
        double sizeOfMinRaise = 2;
        double sizeOfLastBet = 1;
        boolean actionComplete = false;
        boolean bigBlindFirstRoundEnding = false;
        int checkerForActionLimpedToBBAndCalls = 0;
        Files.writeToFile("gameLedger.txt", "Hand: ");
        Files.writeToFile("balanceLedger.txt", "Hand: ");
        Player sb = getSmallBlind(players);
        Player bb = getBigBlind(players);
        System.out.println(sb.getName() + " (sb) " + "bets 0.5");
        Files.writeToFile("gameLedger.txt", "Sb bet 0.5");
        sb.setBalance(sb.getBalance() - 0.5);
        Files.writeToFile("balanceLedger.txt", sb.getName() + " (sb) has " + sb.getBalance());
        sb.setPreflopBet(0.5);
        setPot(getPot() + 0.5);
        System.out.println(bb.getName() + " (bb) " + "bets 1");
        Files.writeToFile("gameLedger.txt", "Bb bet 1");
        bb.setBalance(bb.getBalance() - 1);
        Files.writeToFile("balanceLedger.txt", bb.getName() + " (bb) has " + bb.getBalance());
        bb.setPreflopBet(1);
        setPot(getPot() + 1);
        while ((!actionComplete || bigBlindFirstRoundEnding) && (checkerForActionLimpedToBBAndCalls == 0)) {
            Player tempPlayer = orderedPlayers.get(((startingIndex) % orderedPlayers.size()));
            if (!tempPlayer.getHasFolded()) {
                boolean typedWrite = false;
                while (!typedWrite) {
                    System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + ", would you like " +
                            "to " + "call, " + "raise, or fold?");
                    String response = scanner.nextLine();
                    if (response.equals("call")) {
                        typedWrite = true;
                        System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " calls " + sizeOfLastBet);
                        Files.writeToFile("gameLedger.txt", tempPlayer.getName() + " (" + tempPlayer.getPosition() +
                                ")" + " calls " + sizeOfLastBet);
                        tempPlayer.setPreflopAction(tempPlayer.getPreflopAction() + "calls " + sizeOfLastBet + ", ");
                        if (tempPlayer.getPreflopBet() == 0) {
                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                            Files.writeToFile("balanceLedger.txt",
                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                            setPot(getPot() + sizeOfLastBet);
                        } else {
                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getPreflopBet());
                            Files.writeToFile("balanceLedger.txt",
                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                            setPot(getPot() + sizeOfLastBet - tempPlayer.getPreflopBet());
                        }
                        tempPlayer.setPreflopBet(sizeOfLastBet);
                    } else if (response.equals("fold")) {
                        typedWrite = true;
                        System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " folds");
                        Files.writeToFile("gameLedger.txt", tempPlayer.getName() + " (" + tempPlayer.getPosition() +
                                        ")" + " folds");
                        tempPlayer.setPreflopAction(tempPlayer.getPreflopAction() + "folds " + sizeOfLastBet + ", ");
                        orderedPlayers.get((startingIndex % orderedPlayers.size())).setHasFolded(true);
                        foldedPlayers.add(orderedPlayers.get((startingIndex % orderedPlayers.size())));
                    } else if (response.equals("raise")) {
                        typedWrite = true;
                        indexOfLastRaiser = startingIndex;
                        boolean properRaise = false;
                        while (!properRaise) {
                            System.out.println("How much would you like to raise?");
                            double raiseAmt = scanner.nextDouble();
                            scanner.nextLine();
                            if (raiseAmt < sizeOfMinRaise) {
                                System.out.println("Not a proper raise, try again");
                            } else {
                                properRaise = true;
                                sizeOfLastBet = raiseAmt;
                                sizeOfMinRaise = 2 * raiseAmt;
                                System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " " +
                                        "raises " + raiseAmt);
                                Files.writeToFile("gameLedger.txt", tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " " +
                                                "raises " + raiseAmt);
                                tempPlayer.setPreflopAction(tempPlayer.getPreflopAction() + "raises " + sizeOfLastBet +
                                        ", ");
                                if (tempPlayer.getPreflopBet() == 0) {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet);
                                } else {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getPreflopBet());
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet - tempPlayer.getPreflopBet());
                                }
                                tempPlayer.setPreflopBet(sizeOfLastBet);
                            }
                        }
                    } else {
                        typedWrite = false;
                        System.out.println("Type correctly please");
                    }
                }
            }
            startingIndex += 1;
            System.out.println("Indexoflastraiser: " + indexOfLastRaiser % orderedPlayers.size() + ", starting index:" +
                    " " + startingIndex % orderedPlayers.size());
            if (bigBlindFirstRoundEnding) {
                checkerForActionLimpedToBBAndCalls += 1;
            }
            bigBlindFirstRoundEnding = false;
            if ((indexOfLastRaiser == 1 && (startingIndex % orderedPlayers.size() == 1))) {
                bigBlindFirstRoundEnding = true;
            } else if ((startingIndex % orderedPlayers.size()) == indexOfLastRaiser % orderedPlayers.size()) {
                actionComplete = true;
            }
        }
        if (foldedPlayers.size() == (orderedPlayers.size() - 1)) {
            for (int i = 0; i < orderedPlayers.size(); i++) {
                int checker = 0;
                for (int j = 0; j < foldedPlayers.size(); j++) {
                    if (foldedPlayers.get(j) == orderedPlayers.get(i)) {
                        checker += 1;
                    }
                }
                if (checker == 0) {
                    ArrayList<Player> onePlayer = new ArrayList<Player>();
                    onePlayer.add(orderedPlayers.get(i));
                    return onePlayer;
                }
            }
        } else {
            for (int i = 0; i < orderedPlayers.size(); i++) {
                for (int j = 0; j < foldedPlayers.size(); j++) {
                    if (orderedPlayers.get(i).getName().equals(foldedPlayers.get(j).getName())) {
                        orderedPlayers.remove(i);
                    }
                }
            }
        }
        return orderedPlayers;
    }

    public ArrayList<Player> streetAfterFlop(ArrayList<Player> players, String type) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Player> orderedPlayers = orderPlayers(players);
        ArrayList<Player> foldedPlayers = new ArrayList<Player>();
        int startingIndex = 0;
        int indexOfLastRaiser = 0;
        double sizeOfMinRaise = 0;
        double sizeOfLastBet = 0;
        boolean actionComplete = false;
        boolean firstAction = false;
        while (!actionComplete) {
            Player tempPlayer = orderedPlayers.get(((startingIndex) % orderedPlayers.size()));
            if (!tempPlayer.getHasFolded()) {
                if (!firstAction) {
                    boolean typedWrite = false;
                    while (!typedWrite) {
                        System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + ", would you like " +
                                "to " + "check, " + "raise, or fold?");
                        String response = scanner.nextLine();
                        if (response.equals("check")) {
                            typedWrite = true;
                            System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " check 0");
                            Files.writeToFile("gameLedger.txt",
                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " check 0");
                            if (type.equals("flop")) {
                                tempPlayer.setFlopAction(tempPlayer.getFlopAction() + "checks " + sizeOfLastBet +
                                        ", ");
                            } else if (type.equals("turn")) {
                                tempPlayer.setTurnAction(tempPlayer.getTurnAction() + "checks " + sizeOfLastBet +
                                        ", ");
                            } else if (type.equals("river")) {
                                tempPlayer.setRiverAction(tempPlayer.getRiverAction() + "checks " + sizeOfLastBet +
                                        ", ");
                            }
                        } else if (response.equals("fold")) {
                            typedWrite = true;
                            System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " folds");
                            Files.writeToFile("gameLedger.txt",
                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " folds");
                            orderedPlayers.get((startingIndex % orderedPlayers.size())).setHasFolded(true);
                            foldedPlayers.add(orderedPlayers.get((startingIndex % orderedPlayers.size())));
                            if (type.equals("flop")) {
                                tempPlayer.setFlopAction(tempPlayer.getFlopAction() + "folds " + sizeOfLastBet +
                                        ", ");
                            } else if (type.equals("turn")) {
                                tempPlayer.setTurnAction(tempPlayer.getTurnAction() + "folds " + sizeOfLastBet +
                                        ", ");
                            } else if (type.equals("river")) {
                                tempPlayer.setRiverAction(tempPlayer.getRiverAction() + "folds " + sizeOfLastBet +
                                        ", ");
                            }
                        } else if (response.equals("raise")) {
                            typedWrite = true;
                            indexOfLastRaiser = startingIndex;
                            boolean properRaise = false;
                            while (!properRaise) {
                                System.out.println("How much would you like to raise?");
                                double raiseAmt = scanner.nextDouble();
                                scanner.nextLine();
                                if (raiseAmt < sizeOfMinRaise) {
                                    System.out.println("Not a proper raise, try again");
                                } else {
                                    properRaise = true;
                                    sizeOfLastBet = raiseAmt;
                                    sizeOfMinRaise = 2 * raiseAmt;
                                    System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " " +
                                            "raises " + raiseAmt);
                                    Files.writeToFile("gameLedger.txt", tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " " +
                                            "raises " + raiseAmt);
                                    if (type.equals("flop")) {
                                        tempPlayer.setFlopAction(tempPlayer.getFlopAction() + "raises " + sizeOfLastBet +
                                                ", ");
                                        if (tempPlayer.getFlopBet() == 0) {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet);
                                        } else {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getFlopBet());
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet - tempPlayer.getFlopBet());
                                        }
                                        tempPlayer.setFlopBet(sizeOfLastBet);
                                    } else if (type.equals("turn")) {
                                        tempPlayer.setTurnAction(tempPlayer.getTurnAction() + "raises " + sizeOfLastBet +
                                                ", ");
                                        if (tempPlayer.getTurnBet() == 0) {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet);
                                        } else {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getTurnBet());
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet - tempPlayer.getTurnBet());
                                        }
                                        tempPlayer.setTurnBet(sizeOfLastBet);
                                    } else if (type.equals("river")) {
                                        tempPlayer.setRiverAction(tempPlayer.getRiverAction() + "raises " + sizeOfLastBet +
                                                ", ");
                                        if (tempPlayer.getRiverBet() == 0) {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet);
                                        } else {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getRiverBet());
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet - tempPlayer.getTurnBet());
                                        }
                                        tempPlayer.setRiverBet(sizeOfLastBet);
                                    }
                                }
                            }
                        } else {
                            typedWrite = false;
                            System.out.println("Type correctly please");
                        }
                    }
                    firstAction = true;
                } else {
                    boolean typedWrite = false;
                    while (!typedWrite) {
                        System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + ", would you like " +
                                "to " + "call, " + "raise, or fold?");
                        String response = scanner.nextLine();
                        if (response.equals("call")) {
                            typedWrite = true;
                            System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " calls " + sizeOfLastBet);
                            Files.writeToFile("gameLedger.txt",
                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " calls " + sizeOfLastBet);
                            if (type.equals("flop")) {
                                tempPlayer.setFlopAction(tempPlayer.getFlopAction() + "calls " + sizeOfLastBet +
                                        ", ");
                                if (tempPlayer.getFlopBet() == 0) {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet);
                                } else {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getFlopBet());
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet - tempPlayer.getFlopBet());
                                }
                                tempPlayer.setFlopBet(sizeOfLastBet);
                            } else if (type.equals("turn")) {
                                tempPlayer.setTurnAction(tempPlayer.getTurnAction() + "calls " + sizeOfLastBet +
                                        ", ");
                                if (tempPlayer.getTurnBet() == 0) {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet);
                                } else {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getTurnBet());
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet - tempPlayer.getTurnBet());
                                }
                                tempPlayer.setTurnBet(sizeOfLastBet);
                            } else if (type.equals("river")) {
                                tempPlayer.setRiverAction(tempPlayer.getRiverAction() + "calls " + sizeOfLastBet +
                                        ", ");
                                if (tempPlayer.getRiverBet() == 0) {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet);
                                } else {
                                    tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getRiverBet());
                                    Files.writeToFile("balanceLedger.txt",
                                            tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                    setPot(getPot() + sizeOfLastBet - tempPlayer.getRiverBet());
                                }
                                tempPlayer.setRiverBet(sizeOfLastBet);
                            }
                        } else if (response.equals("fold")) {
                            typedWrite = true;
                            System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " folds");
                            Files.writeToFile("gameLedger.txt",
                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " folds");
                            orderedPlayers.get((startingIndex % orderedPlayers.size())).setHasFolded(true);
                            foldedPlayers.add(orderedPlayers.get((startingIndex % orderedPlayers.size())));
                            if (type.equals("flop")) {
                                tempPlayer.setFlopAction(tempPlayer.getFlopAction() + "folds " + sizeOfLastBet +
                                        ", ");
                            } else if (type.equals("turn")) {
                                tempPlayer.setTurnAction(tempPlayer.getTurnAction() + "folds " + sizeOfLastBet +
                                        ", ");
                            } else if (type.equals("river")) {
                                tempPlayer.setRiverAction(tempPlayer.getRiverAction() + "folds " + sizeOfLastBet +
                                        ", ");
                            }
                        } else if (response.equals("raise")) {
                            typedWrite = true;
                            indexOfLastRaiser = startingIndex;
                            boolean properRaise = false;
                            while (!properRaise) {
                                System.out.println("How much would you like to raise?");
                                double raiseAmt = scanner.nextDouble();
                                scanner.nextLine();
                                if (raiseAmt < sizeOfMinRaise) {
                                    System.out.println("Not a proper raise, try again");
                                } else {
                                    properRaise = true;
                                    sizeOfLastBet = raiseAmt;
                                    sizeOfMinRaise = 2 * raiseAmt;
                                    System.out.println(tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " " +
                                            "raises " + raiseAmt);
                                    Files.writeToFile("gameLedger.txt", tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " " +
                                            "raises " + raiseAmt);
                                    if (type.equals("flop")) {
                                        tempPlayer.setFlopAction(tempPlayer.getFlopAction() + "raises " + sizeOfLastBet +
                                                ", ");
                                        if (tempPlayer.getFlopBet() == 0) {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet);
                                        } else {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getFlopBet());
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet - tempPlayer.getFlopBet());
                                        }
                                        tempPlayer.setFlopBet(sizeOfLastBet);
                                    } else if (type.equals("turn")) {
                                        tempPlayer.setTurnAction(tempPlayer.getTurnAction() + "raises " + sizeOfLastBet +
                                                ", ");
                                        if (tempPlayer.getTurnBet() == 0) {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet);
                                        } else {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getTurnBet());
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet - tempPlayer.getTurnBet());
                                        }
                                        tempPlayer.setTurnBet(sizeOfLastBet);
                                    } else if (type.equals("river")) {
                                        tempPlayer.setRiverAction(tempPlayer.getRiverAction() + "raises " + sizeOfLastBet +
                                                ", ");
                                        if (tempPlayer.getRiverBet() == 0) {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet);
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet);
                                        } else {
                                            tempPlayer.setBalance(tempPlayer.getBalance() - sizeOfLastBet + tempPlayer.getRiverBet());
                                            Files.writeToFile("balanceLedger.txt",
                                                    tempPlayer.getName() + " (" + tempPlayer.getPosition() + ")" + " has " + tempPlayer.getBalance());
                                            setPot(getPot() + sizeOfLastBet - tempPlayer.getRiverBet());
                                        }
                                        tempPlayer.setRiverBet(sizeOfLastBet);
                                    }
                                }
                            }
                        } else {
                            typedWrite = false;
                            System.out.println("Type correctly please");
                        }
                    }
                }
            }
            startingIndex += 1;
            if ((startingIndex % orderedPlayers.size()) == indexOfLastRaiser % orderedPlayers.size()) {
                actionComplete = true;
            }
        }
        for (int i = 0; i < orderedPlayers.size(); i++) {
            for (int j = 0; j < foldedPlayers.size(); j++) {
                if (orderedPlayers.get(i).getName().equals(foldedPlayers.get(j).getName())) {
                    orderedPlayers.remove(i);
                }
            }
        }
        return orderedPlayers;
    }
}
