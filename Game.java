import java.sql.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;

//has main method and where the game will run
public class Game {
    private static ArrayList<Player> players = new ArrayList<>();
    private static int playerCount;
    private static int handCount;
    private static String[] cards = {
            "2-s", "3-s", "4-s", "5-s", "6-s", "7-s", "8-s", "9-s", "10-s", "J-s", "Q-s", "K-s", "A-s",
            "2-h", "3-h", "4-h", "5-h", "6-h", "7-h", "8-h", "9-h", "10-h", "J-h", "Q-h", "K-h", "A-h",
            "2-d", "3-d", "4-d", "5-d", "6-d", "7-d", "8-d", "9-d", "10-d", "J-d", "Q-d", "K-d", "A-d",
            "2-c", "3-c", "4-c", "5-c", "6-c", "7-c", "8-c", "9-c", "10-c", "J-c", "Q-c", "K-c", "A-c"
    };

    public static void main (String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("How many players do you have?");
        playerCount = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < playerCount; i++) {
            System.out.println("What is your player name?");
            String playerName = scanner.nextLine();
            Player tempPlayer = new Player();
            tempPlayer.setName(playerName);
            tempPlayer.setBalance(100);
            players.add(tempPlayer);
        }

        boolean endGame = false;

        while (!endGame) {
            Hand nextHand = new Hand(players);
            handCount += 1;

            ArrayList<Player> preFlopPlayers = nextHand.preFlop(nextHand.getPlayers());

            ArrayList<Integer> possibleCards = nextHand.getNumbers();

            if (preFlopPlayers.size() == 1) {
                System.out.println(preFlopPlayers.get(0).getName() + " has won the pot!");
            } else {
                System.out.println("finish next turn");

                String flop = "";
                for (int i = 0; i < 3; i++) {
                    int randomCard = (int)(possibleCards.size() * Math.random());
                    flop += cards[possibleCards.get(randomCard)] + ",";
                    possibleCards.remove(randomCard);
                }
                System.out.println("flop: " + flop);
                Files.writeToFile("cardLedger.txt", "flop: " + flop);

                ArrayList<Player> postFlopPlayers = nextHand.streetAfterFlop(preFlopPlayers, "flop");
                if (postFlopPlayers.size() == 1) {
                    System.out.println(postFlopPlayers.get(0).getName() + " has won the pot!");
                } else {
                    System.out.println("finish next turn");

                    String turn = flop;
                    int randomCard = (int)(possibleCards.size() * Math.random());
                    turn += cards[possibleCards.get(randomCard)] + ",";
                    possibleCards.remove(randomCard);
                    System.out.println("turn: " + turn);
                    Files.writeToFile("cardLedger.txt", "turn: " + turn);

                    ArrayList<Player> turnPlayers = nextHand.streetAfterFlop(postFlopPlayers, "turn");
                    if (turnPlayers.size() == 1) {
                        System.out.println(turnPlayers.get(0).getName() + " has won the pot!");
                    } else {
                        System.out.println("finish next turn");

                        String river = turn;
                        int randomCardRiver = (int)(possibleCards.size() * Math.random());
                        river += cards[possibleCards.get(randomCardRiver)];
                        possibleCards.remove(randomCardRiver);
                        System.out.println("river: " + river);
                        Files.writeToFile("cardLedger.txt", "river: " + river);

                        ArrayList<Player> riverPlayers = nextHand.streetAfterFlop(turnPlayers, "river");
                        Files.writeToFile("cardLedger.txt", "\n");
                        Files.writeToFile("gameLedger.txt", "\n");
                        if (riverPlayers.size() == 1) {
                            System.out.println(riverPlayers.get(0).getName() + " has won the pot!");
                            riverPlayers.get(0).setBalance(riverPlayers.get(0).getBalance() + nextHand.getPot());
                        } else {
                            for (int i = 0; i < riverPlayers.size(); i++) {
                                riverPlayers.get(i).setHandValue(getHandRanking(river,
                                        riverPlayers.get(i).getCardOne(),
                                        riverPlayers.get(i).getCardTwo()));
                            }
                            double minRiverHand = 1000;
                            ArrayList<Player> winners = new ArrayList<>();
                            for (int i = 0; i < riverPlayers.size(); i++) {
                                if (riverPlayers.get(i).getHandValue() < minRiverHand) {
                                    winners.clear();
                                    winners.add(riverPlayers.get(i));
                                    minRiverHand = riverPlayers.get(i).getHandValue();
                                } else if (riverPlayers.get(i).getHandValue() == minRiverHand) {
                                    winners.add(riverPlayers.get(i));
                                }
                            }
                            if (winners.size() == 1) {
                                System.out.print("The winner of this pot is: ");
                                for (int i = 0; i < winners.size(); i++) {
                                    System.out.print(winners.get(i).getName() + " (" + winners.get(i).getPosition() + ") ");
                                }
                                System.out.print("with a hand value of " + winners.get(0).getHandValue());
                                System.out.println();
                                winners.get(0).setBalance(winners.get(0).getBalance() + nextHand.getPot());
                            } else {
                                ArrayList<Player> finalizedWinners = new ArrayList<>();
                                finalizedWinners = winners;
                                if ((winners.get(0).getHandValue() >= 180) && (winners.get(0).getHandValue() <= 187)) {
                                    finalizedWinners = chopFlush(river, winners);
                                }
                                if ((winners.get(0).getHandValue() >= 198) && (winners.get(0).getHandValue() <= 210)) {
                                    finalizedWinners = chopTrips(river, winners);
                                }
                                if ((winners.get(0).getHandValue() >= 289) && (winners.get(0).getHandValue() <= 301)) {
                                    finalizedWinners = chopOnePair(river, winners);
                                }
                                if ((winners.get(0).getHandValue() >= 302) && (winners.get(0).getHandValue() <= 309)) {
                                    finalizedWinners = chopHighCard(river, winners);
                                }
                                System.out.print("The winner of this pot is: ");
                                for (int i = 0; i < finalizedWinners.size(); i++) {
                                    System.out.print(finalizedWinners.get(i).getName() + " (" + finalizedWinners.get(i).getPosition() + ") ");
                                }
                                System.out.print("with a hand value of " + finalizedWinners.get(0).getHandValue());
                                System.out.println();
                                if (finalizedWinners.size() == 1) {
                                    finalizedWinners.get(0).setBalance(finalizedWinners.get(0).getBalance() + nextHand.getPot());
                                } else {
                                    for (int i = 0; i < finalizedWinners.size(); i++) {
                                        finalizedWinners.get(i).setBalance(finalizedWinners.get(i).getBalance() + (nextHand.getPot() / (double) finalizedWinners.size()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("Total pot is: " + nextHand.getPot());

            endGame = true;
        }
    }

    public static boolean contains(ArrayList<String> cards, String lookingFor) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).equals(lookingFor)) {
                return true;
            }
        }
        return false;
    }

    public static int howMany(ArrayList<String> cards, String lookingFor) {
        int counter = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).equals(lookingFor)) {
                counter += 1;
            }
        }
        return counter;
    }

    public static int cardToValue(String card) {
        if (card.contains("A")) {
            return 14;
        } else if (card.contains("K")) {
            return 13;
        } else if (card.contains("Q")) {
            return 12;
        } else if (card.contains("J")) {
            return 11;
        } else if (card.contains("10")) {
            return 10;
        } else if (card.contains("9")) {
            return 9;
        } else if (card.contains("8")) {
            return 8;
        } else if (card.contains("7")) {
            return 7;
        } else if (card.contains("6")) {
            return 6;
        } else if (card.contains("5")) {
            return 5;
        } else if (card.contains("4")) {
            return 4;
        } else if (card.contains("3")) {
            return 3;
        } else if (card.contains("2")) {
            return 2;
        }
        return 0;
    }

    public static double cardToDoubleValue(String card) {
        if (card.contains("A")) {
            return 2819178082162327154499022366029959843954512194276761760087463015.0;
        } else if (card.contains("K")) {
            return 1639518622529236077952144318816050685207.0;
        } else if (card.contains("Q")) {
            return 1719515742866809222961802.0;
        } else if (card.contains("J")) {
            return 953476947989903.0;
        } else if (card.contains("10")) {
            return 1803416167;
        } else if (card.contains("9")) {
            return 528706;
        } else if (card.contains("8")) {
            return 3411;
        } else if (card.contains("7")) {
            return 155;
        } else if (card.contains("6")) {
            return 22;
        } else if (card.contains("5")) {
            return 7;
        } else if (card.contains("4")) {
            return 3;
        } else if (card.contains("3")) {
            return 2;
        } else if (card.contains("2")) {
            return 1;
        }
        return 0;
    }

    public static String doubleToCardValue(double value) {
        if (value == 2819178082162327154499022366029959843954512194276761760087463015.0) {
            return "A";
        } else if (value == 1639518622529236077952144318816050685207.0) {
            return "K";
        } else if (value == 1719515742866809222961802.0) {
            return "Q";
        } else if (value == 953476947989903.0) {
            return "J";
        } else if (value == 1803416167) {
            return "10";
        } else if (value == 528706) {
            return "9";
        } else if (value == 3411) {
            return "8";
        } else if (value == 155) {
            return "7";
        } else if (value == 22) {
            return "6";
        } else if (value == 7) {
            return "5";
        } else if (value == 3) {
            return "4";
        } else if (value == 2) {
            return "3";
        } else if (value == 1) {
            return "2";
        }
        return "Unknown";
    }

    public static ArrayList<Player> chopFlush(String board, ArrayList<Player> players) {
        String flushType = "";
        int spadeCounter = 0;
        int diamondCounter = 0;
        int clubCounter = 0;
        int heartCounter = 0;
        String[] splitBoard = board.split(",");
        for (int i = 0; i < splitBoard.length; i++) {
            String[] splitSuit = splitBoard[i].split("-");
            if (splitSuit[1].equals("s")) {
                spadeCounter += 1;
            } else if (splitSuit[1].equals("d")) {
                diamondCounter += 1;
            } else if (splitSuit[1].equals("c")) {
                clubCounter += 1;
            } else if (splitSuit[1].equals("h")) {
                heartCounter += 1;
            }
        }
        if (spadeCounter >= 3) {
            int cardOneValue = 0;
            int cardTwoValue = 0;
            for (int j = 0; j < players.size(); j++) {
                String[] cardOneSplit = players.get(j).getCardOne().split("-");
                String[] cardTwoSplit = players.get(j).getCardTwo().split("-");
                if (players.get(j).getCardOne().contains("s")) {
                    cardOneValue = cardToValue(cardOneSplit[0]);
                }
                if (players.get(j).getCardTwo().contains("s")) {
                    cardTwoValue = cardToValue(cardTwoSplit[0]);
                }
                ArrayList<Integer> flushArray = new ArrayList<>();
                for (int i = 0; i < splitBoard.length; i++) {
                    if (splitBoard[i].contains("s")) {
                        String[] splitSuit = splitBoard[i].split("-");
                        flushArray.add(cardToValue(splitSuit[0]));
                    }
                }
                int maxCard = Math.max(cardOneValue, cardTwoValue);
                if (spadeCounter == 3) {
                    players.get(j).setFlushValue(maxCard);
                } else if (spadeCounter == 4) {
                    players.get(j).setFlushValue(maxCard);
                } else if (spadeCounter == 5) {
                    players.get(j).setFlushValue(0);
                    for (int i = 0; i < flushArray.size(); i++) {
                        if (flushArray.get(i) < maxCard) {
                            players.get(j).setFlushValue(maxCard);
                        }
                    }
                }
            }
        } else if (diamondCounter >= 3) {
            int cardOneValue = 0;
            int cardTwoValue = 0;
            for (int j = 0; j < players.size(); j++) {
                String[] cardOneSplit = players.get(j).getCardOne().split("-");
                String[] cardTwoSplit = players.get(j).getCardTwo().split("-");
                if (players.get(j).getCardOne().contains("d")) {
                    cardOneValue = cardToValue(cardOneSplit[0]);
                }
                if (players.get(j).getCardTwo().contains("d")) {
                    cardTwoValue = cardToValue(cardTwoSplit[0]);
                }
                ArrayList<Integer> flushArray = new ArrayList<>();
                for (int i = 0; i < splitBoard.length; i++) {
                    if (splitBoard[i].contains("d")) {
                        String[] splitSuit = splitBoard[i].split("-");
                        flushArray.add(cardToValue(splitSuit[0]));
                    }
                }
                int maxCard = Math.max(cardOneValue, cardTwoValue);
                if (diamondCounter == 3) {
                    players.get(j).setFlushValue(maxCard);
                } else if (diamondCounter == 4) {
                    players.get(j).setFlushValue(maxCard);
                } else if (diamondCounter == 5) {
                    players.get(j).setFlushValue(0);
                    for (int i = 0; i < flushArray.size(); i++) {
                        if (flushArray.get(i) < maxCard) {
                            players.get(j).setFlushValue(maxCard);
                        }
                    }
                }
            }
        } else if (clubCounter >= 3) {
            int cardOneValue = 0;
            int cardTwoValue = 0;
            for (int j = 0; j < players.size(); j++) {
                String[] cardOneSplit = players.get(j).getCardOne().split("-");
                String[] cardTwoSplit = players.get(j).getCardTwo().split("-");
                if (players.get(j).getCardOne().contains("c")) {
                    cardOneValue = cardToValue(cardOneSplit[0]);
                }
                if (players.get(j).getCardTwo().contains("c")) {
                    cardTwoValue = cardToValue(cardTwoSplit[0]);
                }
                ArrayList<Integer> flushArray = new ArrayList<>();
                for (int i = 0; i < splitBoard.length; i++) {
                    if (splitBoard[i].contains("c")) {
                        String[] splitSuit = splitBoard[i].split("-");
                        flushArray.add(cardToValue(splitSuit[0]));
                    }
                }
                int maxCard = Math.max(cardOneValue, cardTwoValue);
                if (clubCounter == 3) {
                    players.get(j).setFlushValue(maxCard);
                } else if (clubCounter == 4) {
                    players.get(j).setFlushValue(maxCard);
                } else if (clubCounter == 5) {
                    players.get(j).setFlushValue(0);
                    for (int i = 0; i < flushArray.size(); i++) {
                        if (flushArray.get(i) < maxCard) {
                            players.get(j).setFlushValue(maxCard);
                        }
                    }
                }
            }
        } else if (heartCounter >= 3) {
            int cardOneValue = 0;
            int cardTwoValue = 0;
            for (int j = 0; j < players.size(); j++) {
                String[] cardOneSplit = players.get(j).getCardOne().split("-");
                String[] cardTwoSplit = players.get(j).getCardTwo().split("-");
                if (players.get(j).getCardOne().contains("h")) {
                    cardOneValue = cardToValue(cardOneSplit[0]);
                }
                if (players.get(j).getCardTwo().contains("h")) {
                    cardTwoValue = cardToValue(cardTwoSplit[0]);
                }
                ArrayList<Integer> flushArray = new ArrayList<>();
                for (int i = 0; i < splitBoard.length; i++) {
                    if (splitBoard[i].contains("h")) {
                        String[] splitSuit = splitBoard[i].split("-");
                        flushArray.add(cardToValue(splitSuit[0]));
                    }
                }
                int maxCard = Math.max(cardOneValue, cardTwoValue);
                if (heartCounter == 3) {
                    players.get(j).setFlushValue(maxCard);
                } else if (heartCounter == 4) {
                    players.get(j).setFlushValue(maxCard);
                } else if (heartCounter == 5) {
                    players.get(j).setFlushValue(0);
                    for (int i = 0; i < flushArray.size(); i++) {
                        if (flushArray.get(i) < maxCard) {
                            players.get(j).setFlushValue(maxCard);
                        }
                    }
                }
            }
        }
        ArrayList<Player> winners = new ArrayList<>();
        int maxHand = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getFlushValue() > maxHand) {
                winners.clear();
                winners.add(players.get(i));
                maxHand = players.get(i).getFlushValue();
            } else if (players.get(i).getFlushValue() == maxHand) {
                winners.add(players.get(i));
            }
        }
        return winners;
    }

    public static ArrayList<Player> chopTrips(String board, ArrayList<Player> players) {
        String[] splitBoard = board.split(",");
        if (players.get(0).getHandValue() == 198) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("A")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("A")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("A")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 199) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("K")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("K")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("K")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 200) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                if (splitBoard[j].contains("Q")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("Q")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("Q")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 201) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("J")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("J")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("J")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 202) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("10")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("10")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("10")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 203) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("9")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("9")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("9")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 204) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("8")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("8")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("8")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 205) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("7")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("7")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("7")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 206) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("6")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("6")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("6")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 207) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("5")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("5")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("5")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 208) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("4")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("4")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("4")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 209) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("3")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("3")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("3")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        } else if (players.get(0).getHandValue() == 210) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("2")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("2")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("2")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                players.get(i).setTripsValue(restCardValues.get(restCardValues.size() - 1) * restCardValues.get(restCardValues.size() - 2));
            }
        }
        ArrayList<Player> winners = new ArrayList<>();
        double maxHand = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getTripsValue() > maxHand) {
                winners.clear();
                winners.add(players.get(i));
                maxHand = players.get(i).getTripsValue();
            } else if (players.get(i).getTripsValue() == maxHand) {
                winners.add(players.get(i));
            }
        }
        return winners;
    }
//
//    public static Player chopTwoPair(String board, ArrayList<Player> players) {
//
//    }

    //edge case where if both high cards after the pair are on the board, then it will result in a chop (doesnt look
    // at fifth card, can fix this later but will be tedious but simple)
    public static ArrayList<Player> chopOnePair(String board, ArrayList<Player> players) {
        String[] splitBoard = board.split(",");
        if (players.get(0).getHandValue() == 289) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("A")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("A")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("A")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 290) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("K")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("K")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("K")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 291) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("Q")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("Q")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("Q")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 292) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("J")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("J")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("J")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 293) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("10")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("10")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("10")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 294) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("9")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("9")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("9")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 295) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("8")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("8")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("8")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 296) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("7")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("7")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("7")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 297) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("6")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("6")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("6")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 298) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("5")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("5")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("5")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 299) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("4")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("4")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("4")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 300) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("3")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("3")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("3")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        } else if (players.get(0).getHandValue() == 301) {
            for (int i = 0; i < players.size(); i++) {
                ArrayList<String> restCards = new ArrayList<>();
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains("2")) {
                        //do nothing
                    } else {
                        String[] splitSuit = splitBoard[j].split("-");
                        restCards.add(splitSuit[0]);
                    }
                }
                String[] splitCardOne = players.get(i).getCardOne().split("-");
                String[] splitCardTwo = players.get(i).getCardTwo().split("-");
                if (!splitCardOne[0].equals("2")) {
                    restCards.add(splitCardOne[0]);
                }
                if (!splitCardTwo[0].equals("2")) {
                    restCards.add(splitCardTwo[0]);
                }
                ArrayList<Double> restCardValues = new ArrayList<>();
                for (int j = 0; j < restCards.size(); j++) {
                    restCardValues.add(cardToDoubleValue(restCards.get(j)));
                }
                Collections.sort(restCardValues);
                ArrayList<String> organizedCards = new ArrayList<>();
                for (int j = 0; j < restCardValues.size(); j++) {
                    organizedCards.add(doubleToCardValue(restCardValues.get(j)));
                }
                boolean checker = false;
                for (int j = 0; j < splitBoard.length; j++) {
                    if (splitBoard[j].contains(organizedCards.get(0))) {
                        organizedCards.remove(0);
                        checker = true;
                    }
                }
                if (checker == true) {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(0))) {
                            organizedCards.remove(0);
                        }
                    }
                } else {
                    for (int j = 0; j < splitBoard.length; j++) {
                        if (splitBoard[j].contains(organizedCards.get(1))) {
                            organizedCards.remove(1);
                        }
                    }
                }
                players.get(i).setHighCardValue(cardToDoubleValue(organizedCards.get(organizedCards.size() - 1)) * cardToDoubleValue(organizedCards.get(organizedCards.size() - 2)));
            }
        }
        ArrayList<Player> winners = new ArrayList<>();
        double maxHand = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getOnePairValue() > maxHand) {
                winners.clear();
                winners.add(players.get(i));
                maxHand = players.get(i).getOnePairValue();
            } else if (players.get(i).getOnePairValue() == maxHand) {
                winners.add(players.get(i));
            }
        }
        return winners;
    }

    public static ArrayList<Player> chopHighCard(String board, ArrayList<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            ArrayList<String> allCards = new ArrayList<>();
            ArrayList<Double> allCardsValue = new ArrayList<>();
            String[] splitBoard = board.split(",");
            for (int j = 0; j < splitBoard.length; j++) {
                String[] splitSuit = splitBoard[j].split("-");
                allCards.add(splitSuit[0]);
            }
            String[] splitCardOne = players.get(i).getCardOne().split("-");
            String[] splitCardTwo = players.get(i).getCardTwo().split("-");
            allCards.add(splitCardOne[0]);
            allCards.add(splitCardTwo[0]);
            for (int j = 0; j < allCards.size(); j++) {
                allCardsValue.add(cardToDoubleValue(allCards.get(j)));
            }
            Collections.sort(allCardsValue);
            double highCardValue =
                    allCardsValue.get(allCardsValue.size() - 1) * allCardsValue.get(allCardsValue.size() - 2) *
                            allCardsValue.get(allCardsValue.size() - 3) * allCardsValue.get(allCardsValue.size() - 4) *
                            allCardsValue.get(allCardsValue.size() - 5);
            players.get(i).setHighCardValue(highCardValue);
        }
        ArrayList<Player> winners = new ArrayList<>();
        double maxHand = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getHighCardValue() > maxHand) {
                winners.clear();
                winners.add(players.get(i));
                maxHand = players.get(i).getHighCardValue();
            } else if (players.get(i).getHighCardValue() == maxHand) {
                winners.add(players.get(i));
            }
        }
        return winners;
    }

    public static int getHandRanking(String board, String cardOne, String cardTwo) {
        ArrayList<String> cardListWithSuit = new ArrayList<>();
        String[] boardCards = board.split(",");
        for (int i = 0; i < boardCards.length; i++) {
            cardListWithSuit.add(boardCards[i]);
        }
        cardListWithSuit.add(cardOne);
        cardListWithSuit.add(cardTwo);

        ArrayList<String> cardListWithoutSuit = new ArrayList<>();
        ArrayList<String> cardListOnlySuit = new ArrayList<>();
        for (int i = 0; i < boardCards.length; i++) {
            String[] splitSuit = boardCards[i].split("-");
            cardListWithoutSuit.add(splitSuit[0]);
            cardListOnlySuit.add(splitSuit[1]);
        }
        String[] firstCard = cardOne.split("-");
        String[] secondCard = cardTwo.split("-");
        cardListWithoutSuit.add(firstCard[0]);
        cardListWithoutSuit.add(secondCard[0]);
        cardListOnlySuit.add(firstCard[1]);
        cardListOnlySuit.add(secondCard[1]);

        //royal flush
        if ((contains(cardListWithSuit, "A-s")) && contains(cardListWithSuit, "K-s")
                && contains(cardListWithSuit, "Q-s") && contains(cardListWithSuit, "J-s") &&
                contains(cardListWithSuit, "10-s")) {
            return 1;
        } else if ((contains(cardListWithSuit, "A-d")) && contains(cardListWithSuit, "K-d")
                && contains(cardListWithSuit, "Q-d") && contains(cardListWithSuit, "J-d") &&
                contains(cardListWithSuit, "10-d")) {
            return 1;
        } else if ((contains(cardListWithSuit, "A-c")) && contains(cardListWithSuit, "K-c")
                && contains(cardListWithSuit, "Q-c") && contains(cardListWithSuit, "J-c") &&
                contains(cardListWithSuit, "10-c")) {
            return 1;
        } else if ((contains(cardListWithSuit, "A-h")) && contains(cardListWithSuit, "K-h")
                && contains(cardListWithSuit, "Q-h") && contains(cardListWithSuit, "J-h") &&
                contains(cardListWithSuit, "10-h")) {
            return 1;
        }

        //king high straight flush
        if (contains(cardListWithSuit, "K-s") && contains(cardListWithSuit, "Q-s")&& contains(cardListWithSuit, "J-s") &&
                contains(cardListWithSuit, "10-s" ) && (contains(cardListWithSuit, "9-s"))) {
            return 2;
        } else if (contains(cardListWithSuit, "K-d")
                && contains(cardListWithSuit, "Q-d") && contains(cardListWithSuit, "J-d") &&
                contains(cardListWithSuit, "10-d") && (contains(cardListWithSuit, "9-d"))) {
            return 2;
        } else if (contains(cardListWithSuit, "K-c")
                && contains(cardListWithSuit, "Q-c") && contains(cardListWithSuit, "J-c") &&
                contains(cardListWithSuit, "10-c") && (contains(cardListWithSuit, "9-c"))) {
            return 2;
        } else if (contains(cardListWithSuit, "K-h")
                && contains(cardListWithSuit, "Q-h") && contains(cardListWithSuit, "J-h") &&
                contains(cardListWithSuit, "10-h") && (contains(cardListWithSuit, "9-h"))) {
            return 2;
        }

        //queen high straight flush
        if (contains(cardListWithSuit, "Q-s") && contains(cardListWithSuit, "J-s") &&
                contains(cardListWithSuit, "10-s" ) && (contains(cardListWithSuit, "9-s"))
                && contains(cardListWithSuit, "8-s")) {
            return 3;
        } else if (contains(cardListWithSuit, "Q-d") && contains(cardListWithSuit, "J-d") &&
                contains(cardListWithSuit, "10-d") && (contains(cardListWithSuit, "9-d")) &&
                contains(cardListWithSuit, "8-d")) {
            return 3;
        } else if (contains(cardListWithSuit, "Q-c") && contains(cardListWithSuit, "J-c") &&
                contains(cardListWithSuit, "10-c") && (contains(cardListWithSuit, "9-c")) &&
                contains(cardListWithSuit, "8-c")) {
            return 3;
        } else if (contains(cardListWithSuit, "Q-h") && contains(cardListWithSuit, "J-h") &&
                contains(cardListWithSuit, "10-h") && (contains(cardListWithSuit, "9-h")) &&
                contains(cardListWithSuit, "8-h")) {
            return 3;
        }

        //jack high straight flush
        if (contains(cardListWithSuit, "J-s") &&
                contains(cardListWithSuit, "10-s" ) && (contains(cardListWithSuit, "9-s"))
                && contains(cardListWithSuit, "8-s") && contains(cardListWithSuit, "7-s")) {
            return 4;
        } else if (contains(cardListWithSuit, "J-d") &&
                contains(cardListWithSuit, "10-d") && (contains(cardListWithSuit, "9-d")) &&
                contains(cardListWithSuit, "8-d") && contains(cardListWithSuit, "7-d")) {
            return 4;
        } else if (contains(cardListWithSuit, "J-c") &&
                contains(cardListWithSuit, "10-c") && (contains(cardListWithSuit, "9-c")) &&
                contains(cardListWithSuit, "8-c") && contains(cardListWithSuit, "7-c")) {
            return 4;
        } else if (contains(cardListWithSuit, "J-h") &&
                contains(cardListWithSuit, "10-h") && (contains(cardListWithSuit, "9-h")) &&
                contains(cardListWithSuit, "8-h") && contains(cardListWithSuit, "7-h")) {
            return 4;
        }

        //ten high straight flush
        if (contains(cardListWithSuit, "10-s" ) && (contains(cardListWithSuit, "9-s"))
                && contains(cardListWithSuit, "8-s") && contains(cardListWithSuit, "7-s") &&
                contains(cardListWithSuit, "6-s")) {
            return 5;
        } else if (contains(cardListWithSuit, "10-d") && (contains(cardListWithSuit, "9-d")) &&
                contains(cardListWithSuit, "8-d") && contains(cardListWithSuit, "7-d") &&
                contains(cardListWithSuit, "6-d")) {
            return 5;
        } else if (contains(cardListWithSuit, "10-c") && (contains(cardListWithSuit, "9-c")) &&
                contains(cardListWithSuit, "8-c") && contains(cardListWithSuit, "7-c") &&
                contains(cardListWithSuit, "6-c")) {
            return 5;
        } else if (contains(cardListWithSuit, "10-h") && (contains(cardListWithSuit, "9-h")) &&
                contains(cardListWithSuit, "8-h") && contains(cardListWithSuit, "7-h") &&
                contains(cardListWithSuit, "6-h")) {
            return 5;
        }

        //nine high straight flush
        if (contains(cardListWithSuit, "9-s")
                && contains(cardListWithSuit, "8-s") && contains(cardListWithSuit, "7-s") &&
                contains(cardListWithSuit, "6-s") && contains(cardListWithSuit, "5-s" )) {
            return 6;
        } else if (contains(cardListWithSuit, "9-d") &&
                contains(cardListWithSuit, "8-d") && contains(cardListWithSuit, "7-d") &&
                contains(cardListWithSuit, "6-d") && contains(cardListWithSuit, "5-d")) {
            return 6;
        } else if (contains(cardListWithSuit, "9-c") &&
                contains(cardListWithSuit, "8-c") && contains(cardListWithSuit, "7-c") &&
                contains(cardListWithSuit, "6-c") && contains(cardListWithSuit, "5-c")) {
            return 6;
        } else if (contains(cardListWithSuit, "9-h") &&
                contains(cardListWithSuit, "8-h") && contains(cardListWithSuit, "7-h") &&
                contains(cardListWithSuit, "6-h") && contains(cardListWithSuit, "5-h")) {
            return 6;
        }

        //eight high straight flush
        if (contains(cardListWithSuit, "8-s") && contains(cardListWithSuit, "7-s") &&
                contains(cardListWithSuit, "6-s") && contains(cardListWithSuit, "5-s" ) &&
                contains(cardListWithSuit, "4-s")) {
            return 7;
        } else if (contains(cardListWithSuit, "8-d") && contains(cardListWithSuit, "7-d") &&
                contains(cardListWithSuit, "6-d") && contains(cardListWithSuit, "5-d") &&
                contains(cardListWithSuit, "4-d")) {
            return 7;
        } else if (contains(cardListWithSuit, "8-c") && contains(cardListWithSuit, "7-c") &&
                contains(cardListWithSuit, "6-c") && contains(cardListWithSuit, "5-c") &&
                contains(cardListWithSuit, "4-c")) {
            return 7;
        } else if (contains(cardListWithSuit, "8-h") && contains(cardListWithSuit, "7-h") &&
                contains(cardListWithSuit, "6-h") && contains(cardListWithSuit, "5-h") &&
                contains(cardListWithSuit, "4-h")) {
            return 7;
        }

        //seven high straight flush
        if (contains(cardListWithSuit, "7-s") &&
                contains(cardListWithSuit, "6-s") && contains(cardListWithSuit, "5-s" ) &&
                contains(cardListWithSuit, "4-s") && contains(cardListWithSuit, "3-s")) {
            return 8;
        } else if (contains(cardListWithSuit, "7-d") &&
                contains(cardListWithSuit, "6-d") && contains(cardListWithSuit, "5-d") &&
                contains(cardListWithSuit, "4-d") && contains(cardListWithSuit, "3-d")) {
            return 8;
        } else if (contains(cardListWithSuit, "7-c") &&
                contains(cardListWithSuit, "6-c") && contains(cardListWithSuit, "5-c") &&
                contains(cardListWithSuit, "4-c") && contains(cardListWithSuit, "3-c")) {
            return 8;
        } else if (contains(cardListWithSuit, "7-h") &&
                contains(cardListWithSuit, "6-h") && contains(cardListWithSuit, "5-h") &&
                contains(cardListWithSuit, "4-h") && contains(cardListWithSuit, "3-h")) {
            return 8;
        }

        //six high straight flush
        if (contains(cardListWithSuit, "6-s") && contains(cardListWithSuit, "5-s" ) &&
                contains(cardListWithSuit, "4-s") && contains(cardListWithSuit, "3-s") &&
                contains(cardListWithSuit, "2-s")) {
            return 9;
        } else if (contains(cardListWithSuit, "6-d") && contains(cardListWithSuit, "5-d") &&
                contains(cardListWithSuit, "4-d") && contains(cardListWithSuit, "3-d") &&
                contains(cardListWithSuit, "2-d")) {
            return 9;
        } else if (contains(cardListWithSuit, "6-c") && contains(cardListWithSuit, "5-c") &&
                contains(cardListWithSuit, "4-c") && contains(cardListWithSuit, "3-c") &&
                contains(cardListWithSuit, "2-c")) {
            return 9;
        } else if (contains(cardListWithSuit, "6-h") && contains(cardListWithSuit, "5-h") &&
                contains(cardListWithSuit, "4-h") && contains(cardListWithSuit, "3-h") &&
                contains(cardListWithSuit, "2-h")) {
            return 9;
        }

        //five high straight flush
        if (contains(cardListWithSuit, "5-s" ) &&
                contains(cardListWithSuit, "4-s") && contains(cardListWithSuit, "3-s") &&
                contains(cardListWithSuit, "2-s") && contains(cardListWithSuit, "A-s")) {
            return 10;
        } else if (contains(cardListWithSuit, "5-d") &&
                contains(cardListWithSuit, "4-d") && contains(cardListWithSuit, "3-d") &&
                contains(cardListWithSuit, "2-d") && contains(cardListWithSuit, "A-d")) {
            return 10;
        } else if (contains(cardListWithSuit, "5-c") &&
                contains(cardListWithSuit, "4-c") && contains(cardListWithSuit, "3-c") &&
                contains(cardListWithSuit, "2-c") && contains(cardListWithSuit, "A-c")) {
            return 10;
        } else if (contains(cardListWithSuit, "5-h") &&
                contains(cardListWithSuit, "4-h") && contains(cardListWithSuit, "3-h") &&
                contains(cardListWithSuit, "2-h") && contains(cardListWithSuit, "A-h")) {
            return 10;
        }

        //ace quads
        if (howMany(cardListWithoutSuit, "A") == 4) {
            return 11;
        }

        //king quads
        if (howMany(cardListWithoutSuit, "K") == 4) {
            return 12;
        }

        //queen quads
        if (howMany(cardListWithoutSuit, "Q") == 4) {
            return 13;
        }

        //jack quads
        if (howMany(cardListWithoutSuit, "J") == 4) {
            return 14;
        }

        //ten quads
        if (howMany(cardListWithoutSuit, "10") == 4) {
            return 15;
        }

        //nine quads
        if (howMany(cardListWithoutSuit, "9") == 4) {
            return 16;
        }

        //eight quads
        if (howMany(cardListWithoutSuit, "8") == 4) {
            return 17;
        }

        //seven quads
        if (howMany(cardListWithoutSuit, "7") == 4) {
            return 18;
        }

        //six quads
        if (howMany(cardListWithoutSuit, "6") == 4) {
            return 19;
        }

        //five quads
        if (howMany(cardListWithoutSuit, "5") == 4) {
            return 20;
        }

        //four quads
        if (howMany(cardListWithoutSuit, "4") == 4) {
            return 21;
        }

        //three quads
        if (howMany(cardListWithoutSuit, "3") == 4) {
            return 22;
        }

        //two quads
        if (howMany(cardListWithoutSuit, "2") == 4) {
            return 23;
        }

        //aces full of kings
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 24;
        }

        //aces full of queens
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 25;
        }

        //aces full of jacks
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 26;
        }

        //aces full of tens
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 27;
        }

        //aces full of nines
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 28;
        }

        //aces full of eights
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 29;
        }

        //aces full of sevens
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 30;
        }

        //aces full of sixes
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 31;
        }

        //aces full of fives
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 32;
        }

        //aces full of fours
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 33;
        }

        //aces full of threes
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 34;
        }

        //aces full of twos
        if ((howMany(cardListWithoutSuit, "A") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 35;
        }

        //kings full of aces
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 36;
        }

        //kings full of queens
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 37;
        }

        //kings full of jacks
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 38;
        }

        //kings full of tens
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 39;
        }

        //kings full of nines
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 40;
        }

        //kings full of eights
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 41;
        }

        //kings full of sevens
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 42;
        }

        //kings full of sixes
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 43;
        }

        //kings full of fives
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 44;
        }

        //kings full of fours
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 45;
        }

        //kings full of threes
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 46;
        }

        //kings full of twos
        if ((howMany(cardListWithoutSuit, "K") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 47;
        }

        //queens full of aces
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 48;
        }

        //queens full of kings
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 49;
        }

        //queens full of jacks
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 50;
        }

        //queens full of tens
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 51;
        }

        //queens full of nines
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 52;
        }

        //queens full of eights
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 53;
        }

        //queens full of sevens
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 54;
        }

        //queens full of sixes
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 55;
        }

        //queens full of fives
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 56;
        }

        //queens full of fours
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 57;
        }

        //queens full of threes
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 58;
        }

        //queens full of twos
        if ((howMany(cardListWithoutSuit, "Q") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 59;
        }

        //jacks full of aces
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 60;
        }

        //jacks full of kings
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 61;
        }

        //jacks full of queens
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 62;
        }

        //jacks full of tens
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 63;
        }

        //jacks full of nines
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 64;
        }

        //jacks full of eights
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 65;
        }

        //jacks full of sevens
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 66;
        }

        //jacks full of sixes
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 67;
        }

        //jacks full of fives
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 68;
        }

        //jacks full of fours
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 69;
        }

        //jacks full of threes
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 70;
        }

        //jacks full of twos
        if ((howMany(cardListWithoutSuit, "J") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 71;
        }

        //tens full of aces
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 72;
        }

        //tens full of kings
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 73;
        }

        //tens full of queens
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 74;
        }

        //tens full of jacks
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 75;
        }

        //tens full of nines
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 76;
        }

        //tens full of eights
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 77;
        }

        //tens full of sevens
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 78;
        }

        //tens full of sixes
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 79;
        }

        //tens full of fives
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 80;
        }

        //tens full of fours
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 81;
        }

        //tens full of threes
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 82;
        }

        //tens full of twos
        if ((howMany(cardListWithoutSuit, "10") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 83;
        }

        //nines full of aces
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 84;
        }

        //nines full of kings
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 85;
        }

        //nines full of queens
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 86;
        }

        //nines full of jacks
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 87;
        }

        //nines full of tens
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 88;
        }

        //nines full of eights
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 89;
        }

        //nines full of sevens
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 90;
        }

        //nines full of sixes
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 91;
        }

        //nines full of fives
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 92;
        }

        //nines full of fours
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 93;
        }

        //nines full of threes
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 94;
        }

        //nines full of twos
        if ((howMany(cardListWithoutSuit, "9") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 95;
        }

        //eights full of aces
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 96;
        }

        //eights full of kings
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 97;
        }

        //eights full of queens
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 98;
        }

        //eights full of jacks
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 99;
        }

        //eights full of tens
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 100;
        }

        //eights full of nines
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 101;
        }

        //eights full of sevens
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 102;
        }

        //eights full of sixes
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 103;
        }

        //eights full of fives
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 104;
        }

        //eights full of fours
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 105;
        }

        //eights full of threes
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 106;
        }

        //eights full of twos
        if ((howMany(cardListWithoutSuit, "8") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 107;
        }

        //sevens full of aces
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 108;
        }

        //sevens full of kings
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 109;
        }

        //sevens full of queens
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 110;
        }

        //sevens full of jacks
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 111;
        }

        //sevens full of tens
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 112;
        }

        //sevens full of nines
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 113;
        }

        //sevens full of eights
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 114;
        }

        //sevens full of sixes
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 115;
        }

        //sevens full of fives
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 116;
        }

        //sevens full of fours
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 117;
        }

        //sevens full of threes
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 118;
        }

        //sevens full of twos
        if ((howMany(cardListWithoutSuit, "7") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 119;
        }

        //sixes full of aces
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 120;
        }

        //sixes full of kings
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 121;
        }

        //sixes full of queens
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 122;
        }

        //sixes full of jacks
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 123;
        }

        //sixes full of tens
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 124;
        }

        //sixes full of nines
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 125;
        }

        //sixes full of eights
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 126;
        }

        //sixes full of sevens
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 127;
        }

        //sixes full of fives
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 128;
        }

        //sixes full of fours
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 129;
        }

        //sixes full of threes
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 130;
        }

        //sixes full of twos
        if ((howMany(cardListWithoutSuit, "6") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 131;
        }

        //fives full of aces
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 132;
        }

        //fives full of kings
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 133;
        }

        //fives full of queens
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 134;
        }

        //fives full of jacks
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 135;
        }

        //fives full of tens
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 136;
        }

        //fives full of nines
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 137;
        }

        //fives full of eights
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 138;
        }

        //fives full of sevens
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 139;
        }

        //fives full of sixes
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 140;
        }

        //fives full of fours
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 141;
        }

        //fives full of threes
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 142;
        }

        //fives full of twos
        if ((howMany(cardListWithoutSuit, "5") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 143;
        }

        //fours full of aces
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 144;
        }

        //fours full of kings
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 145;
        }

        //fours full of queens
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 146;
        }

        //fours full of jacks
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 147;
        }

        //fours full of tens
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 148;
        }

        //fours full of nines
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 149;
        }

        //fours full of eights
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 150;
        }

        //fours full of sevens
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 151;
        }

        //fours full of sixes
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 152;
        }

        //fours full of fives
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 153;
        }

        //fours full of threes
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 154;
        }

        //fours full of twos
        if ((howMany(cardListWithoutSuit, "4") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 155;
        }

        //threes full of aces
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 156;
        }

        //threes full of kings
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 157;
        }

        //threes full of queens
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 158;
        }

        //threes full of jacks
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 159;
        }

        //threes full of tens
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 160;
        }

        //threes full of nines
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 161;
        }

        //threes full of eights
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 162;
        }

        //threes full of sevens
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 163;
        }

        //threes full of sixes
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 164;
        }

        //threes full of fives
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 165;
        }

        //threes full of fours
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 166;
        }

        //threes full of twos
        if ((howMany(cardListWithoutSuit, "3") == 3) && (howMany(cardListWithoutSuit, "2") == 2)) {
            return 167;
        }

        //twos full of aces
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "A") == 2)) {
            return 168;
        }

        //twos full of kings
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "K") == 2)) {
            return 169;
        }

        //twos full of queens
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "Q") == 2)) {
            return 170;
        }

        //twos full of jacks
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "J") == 2)) {
            return 171;
        }

        //twos full of tens
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "10") == 2)) {
            return 172;
        }

        //twos full of nines
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "9") == 2)) {
            return 173;
        }

        //twos full of eights
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "8") == 2)) {
            return 174;
        }

        //twos full of sevens
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "7") == 2)) {
            return 175;
        }

        //twos full of sixes
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "6") == 2)) {
            return 176;
        }

        //twos full of fives
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "5") == 2)) {
            return 177;
        }

        //twos full of fours
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "4") == 2)) {
            return 178;
        }

        //twos full of threes
        if ((howMany(cardListWithoutSuit, "2") == 3) && (howMany(cardListWithoutSuit, "3") == 2)) {
            return 179;
        }

        //ace high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "A-s")) {
            return 180;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "A-d")) {
            return 180;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "A-c")) {
            return 180;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "A-h")) {
            return 180;
        }

        //king high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "K-s")) {
            return 181;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "K-d")) {
            return 181;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "K-c")) {
            return 181;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "K-h")) {
            return 181;
        }

        //queen high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "Q-s")) {
            return 182;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "Q-d")) {
            return 182;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "Q-c")) {
            return 182;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "Q-h")) {
            return 182;
        }

        //jack high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "J-s")) {
            return 183;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "J-d")) {
            return 183;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "J-c")) {
            return 183;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "J-h")) {
            return 183;
        }

        //ten high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "10-s")) {
            return 184;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "10-d")) {
            return 184;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "10-c")) {
            return 184;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "10-h")) {
            return 184;
        }

        //nine high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "9-s")) {
            return 185;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "9-d")) {
            return 185;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "9-c")) {
            return 185;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "9-h")) {
            return 185;
        }

        //eight high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "8-s")) {
            return 186;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "8-d")) {
            return 186;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "8-c")) {
            return 186;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "8-h")) {
            return 186;
        }

        //seven high flush
        if ((howMany(cardListOnlySuit, "s") >= 5) && contains(cardListWithSuit, "7-s")) {
            return 187;
        } else if ((howMany(cardListOnlySuit, "d") >= 5) && contains(cardListWithSuit, "7-d")) {
            return 187;
        } else if ((howMany(cardListOnlySuit, "c") >= 5) && contains(cardListWithSuit, "7-c")) {
            return 187;
        } else if ((howMany(cardListOnlySuit, "h") >= 5) && contains(cardListWithSuit, "7-h")) {
            return 187;
        }

        //ace high straight
        if (contains(cardListWithoutSuit, "A") && contains(cardListWithoutSuit, "K") && contains(cardListWithoutSuit, "Q") && contains(cardListWithoutSuit, "J") && contains(cardListWithoutSuit, "10")) {
            return 188;
        }

        //king high straight
        if (contains(cardListWithoutSuit, "K") && contains(cardListWithoutSuit, "Q") && contains(cardListWithoutSuit, "J") && contains(cardListWithoutSuit, "10") && contains(cardListWithoutSuit, "9")) {
            return 189;
        }

        //queen high straight
        if (contains(cardListWithoutSuit, "Q") && contains(cardListWithoutSuit, "J") && contains(cardListWithoutSuit, "10") && contains(cardListWithoutSuit, "9") && contains(cardListWithoutSuit, "8")) {
            return 190;
        }

        //jack high straight
        if (contains(cardListWithoutSuit, "J") && contains(cardListWithoutSuit, "10") && contains(cardListWithoutSuit, "9") && contains(cardListWithoutSuit, "8") && contains(cardListWithoutSuit, "7")) {
            return 191;
        }

        //ten high straight
        if (contains(cardListWithoutSuit, "10") && contains(cardListWithoutSuit, "9") && contains(cardListWithoutSuit, "8") && contains(cardListWithoutSuit, "7") && contains(cardListWithoutSuit, "6")) {
            return 192;
        }

        //nine high straight
        if (contains(cardListWithoutSuit, "9") && contains(cardListWithoutSuit, "8") && contains(cardListWithoutSuit, "7") && contains(cardListWithoutSuit, "6") && contains(cardListWithoutSuit, "5")) {
            return 193;
        }

        //eight high straight
        if (contains(cardListWithoutSuit, "8") && contains(cardListWithoutSuit, "7") && contains(cardListWithoutSuit, "6") && contains(cardListWithoutSuit, "5") && contains(cardListWithoutSuit, "4")) {
            return 194;
        }

        //seven high straight
        if (contains(cardListWithoutSuit, "7") && contains(cardListWithoutSuit, "6") && contains(cardListWithoutSuit, "5") && contains(cardListWithoutSuit, "4") && contains(cardListWithoutSuit, "3")) {
            return 195;
        }

        //six high straight
        if (contains(cardListWithoutSuit, "6") && contains(cardListWithoutSuit, "5") && contains(cardListWithoutSuit, "4") && contains(cardListWithoutSuit, "3") && contains(cardListWithoutSuit, "2")) {
            return 196;
        }

        //five high straight
        if (contains(cardListWithoutSuit, "5") && contains(cardListWithoutSuit, "4") && contains(cardListWithoutSuit, "3") && contains(cardListWithoutSuit, "2") && contains(cardListWithoutSuit, "A")) {
            return 197;
        }

        //ace trips
        if (howMany(cardListWithoutSuit, "A") == 3) {
            return 198;
        }

        //king trips
        if (howMany(cardListWithoutSuit, "K") == 3) {
            return 199;
        }

        //queen trips
        if (howMany(cardListWithoutSuit, "Q") == 3) {
            return 200;
        }

        //jack trips
        if (howMany(cardListWithoutSuit, "J") == 3) {
            return 201;
        }

        //ten trips
        if (howMany(cardListWithoutSuit, "10") == 3) {
            return 202;
        }

        //nine trips
        if (howMany(cardListWithoutSuit, "9") == 3) {
            return 203;
        }

        //eight trips
        if (howMany(cardListWithoutSuit, "8") == 3) {
            return 204;
        }

        //seven trips
        if (howMany(cardListWithoutSuit, "7") == 3) {
            return 205;
        }

        //six trips
        if (howMany(cardListWithoutSuit, "6") == 3) {
            return 206;
        }

        //five trips
        if (howMany(cardListWithoutSuit, "5") == 3) {
            return 207;
        }

        //four trips
        if (howMany(cardListWithoutSuit, "4") == 3) {
            return 208;
        }

        //three trips
        if (howMany(cardListWithoutSuit, "3") == 3) {
            return 209;
        }

        //two trips
        if (howMany(cardListWithoutSuit, "2") == 3) {
            return 210;
        }

        //ace king two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "K") == 2) {
            return 211;
        }

        //ace queen two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "Q") == 2) {
            return 212;
        }

        //ace jack two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "J") == 2) {
            return 213;
        }

        //ace ten two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "10") == 2) {
            return 214;
        }

        //ace nine two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "9") == 2) {
            return 215;
        }

        //ace eight two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "8") == 2) {
            return 216;
        }

        //ace seven two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "7") == 2) {
            return 217;
        }

        //ace six two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 218;
        }

        //ace five two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 219;
        }

        //ace four two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 220;
        }

        //ace three two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 221;
        }

        //ace two two pair
        if (howMany(cardListWithoutSuit, "A") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 222;
        }

        //king queen two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "Q") == 2) {
            return 223;
        }

        //king jack two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "J") == 2) {
            return 224;
        }

        //king ten two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "10") == 2) {
            return 225;
        }

        //king nine two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "9") == 2) {
            return 226;
        }

        //king eight two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "8") == 2) {
            return 227;
        }

        //king seven two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "7") == 2) {
            return 228;
        }

        //king six two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 229;
        }

        //king five two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 230;
        }

        //king four two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 231;
        }

        //king three two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 232;
        }

        //king two two pair
        if (howMany(cardListWithoutSuit, "K") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 233;
        }

        //queen jack two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "J") == 2) {
            return 234;
        }

        //queen ten two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "10") == 2) {
            return 235;
        }

        //queen nine two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "9") == 2) {
            return 236;
        }

        //queen eight two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "8") == 2) {
            return 237;
        }

        //queen seven two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "7") == 2) {
            return 238;
        }

        //queen six two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 239;
        }

        //queen five two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 240;
        }

        //queen four two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 241;
        }

        //queen three two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 242;
        }

        //queen two two pair
        if (howMany(cardListWithoutSuit, "Q") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 243;
        }

        //jack ten two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "10") == 2) {
            return 244;
        }

        //jack nine two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "9") == 2) {
            return 245;
        }

        //jack eight two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "8") == 2) {
            return 246;
        }

        //jack seven two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "7") == 2) {
            return 247;
        }

        //jack six two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 248;
        }

        //jack five two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 249;
        }

        //jack four two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 250;
        }

        //jack three two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 251;
        }

        //jack two two pair
        if (howMany(cardListWithoutSuit, "J") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 252;
        }

        //ten nine two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "9") == 2) {
            return 253;
        }

        //ten eight two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "8") == 2) {
            return 254;
        }

        //ten seven two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "7") == 2) {
            return 255;
        }

        //ten six two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 256;
        }

        //ten five two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 257;
        }

        //ten four two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 258;
        }

        //ten three two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 259;
        }

        //ten two two pair
        if (howMany(cardListWithoutSuit, "10") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 260;
        }

        //nine eight two pair
        if (howMany(cardListWithoutSuit, "9") == 2 && howMany(cardListWithoutSuit, "8") == 2) {
            return 261;
        }

        //nine seven two pair
        if (howMany(cardListWithoutSuit, "9") == 2 && howMany(cardListWithoutSuit, "7") == 2) {
            return 262;
        }

        //nine six two pair
        if (howMany(cardListWithoutSuit, "9") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 263;
        }

        //nine five two pair
        if (howMany(cardListWithoutSuit, "9") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 264;
        }

        //nine four two pair
        if (howMany(cardListWithoutSuit, "9") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 265;
        }

        //nine three two pair
        if (howMany(cardListWithoutSuit, "9") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 266;
        }

        //nine two two pair
        if (howMany(cardListWithoutSuit, "9") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 267;
        }

        //eight seven two pair
        if (howMany(cardListWithoutSuit, "8") == 2 && howMany(cardListWithoutSuit, "7") == 2) {
            return 268;
        }

        //eight six two pair
        if (howMany(cardListWithoutSuit, "8") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 269;
        }

        //eight five two pair
        if (howMany(cardListWithoutSuit, "8") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 270;
        }

        //eight four two pair
        if (howMany(cardListWithoutSuit, "8") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 271;
        }

        //eight three two pair
        if (howMany(cardListWithoutSuit, "8") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 272;
        }

        //eight two two pair
        if (howMany(cardListWithoutSuit, "8") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 273;
        }

        //seven six two pair
        if (howMany(cardListWithoutSuit, "7") == 2 && howMany(cardListWithoutSuit, "6") == 2) {
            return 274;
        }

        //seven five two pair
        if (howMany(cardListWithoutSuit, "7") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 275;
        }

        //seven four two pair
        if (howMany(cardListWithoutSuit, "7") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 276;
        }

        //seven three two pair
        if (howMany(cardListWithoutSuit, "7") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 277;
        }

        //seven two two pair
        if (howMany(cardListWithoutSuit, "7") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 278;
        }

        //six five two pair
        if (howMany(cardListWithoutSuit, "6") == 2 && howMany(cardListWithoutSuit, "5") == 2) {
            return 279;
        }

        //six four two pair
        if (howMany(cardListWithoutSuit, "6") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 280;
        }

        //six three two pair
        if (howMany(cardListWithoutSuit, "6") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 281;
        }

        //six two two pair
        if (howMany(cardListWithoutSuit, "6") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 282;
        }

        //five four two pair
        if (howMany(cardListWithoutSuit, "5") == 2 && howMany(cardListWithoutSuit, "4") == 2) {
            return 283;
        }

        //five three two pair
        if (howMany(cardListWithoutSuit, "5") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 284;
        }

        //five two two pair
        if (howMany(cardListWithoutSuit, "5") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 285;
        }

        //four three two pair
        if (howMany(cardListWithoutSuit, "4") == 2 && howMany(cardListWithoutSuit, "3") == 2) {
            return 286;
        }

        //four two two pair
        if (howMany(cardListWithoutSuit, "4") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 287;
        }

        //three two two pair
        if (howMany(cardListWithoutSuit, "3") == 2 && howMany(cardListWithoutSuit, "2") == 2) {
            return 288;
        }

        //ace pair
        if (howMany(cardListWithoutSuit, "A") == 2) {
            return 289;
        }

        //king pair
        if (howMany(cardListWithoutSuit, "K") == 2) {
            return 290;
        }

        //queen pair
        if (howMany(cardListWithoutSuit, "Q") == 2) {
            return 291;
        }

        //jack pair
        if (howMany(cardListWithoutSuit, "J") == 2) {
            return 292;
        }

        //ten pair
        if (howMany(cardListWithoutSuit, "T") == 2) {
            return 293;
        }

        //nine pair
        if (howMany(cardListWithoutSuit, "9") == 2) {
            return 294;
        }

        //eight pair
        if (howMany(cardListWithoutSuit, "8") == 2) {
            return 295;
        }

        //seven pair
        if (howMany(cardListWithoutSuit, "7") == 2) {
            return 296;
        }

        //six pair
        if (howMany(cardListWithoutSuit, "6") == 2) {
            return 297;
        }

        //five pair
        if (howMany(cardListWithoutSuit, "5") == 2) {
            return 298;
        }

        //four pair
        if (howMany(cardListWithoutSuit, "4") == 2) {
            return 299;
        }

        //three pair
        if (howMany(cardListWithoutSuit, "3") == 2) {
            return 300;
        }

        //two pair
        if (howMany(cardListWithoutSuit, "2") == 2) {
            return 301;
        }

        //ace high
        if (howMany(cardListWithoutSuit, "A") == 1) {
            return 302;
        }

        //king high
        if (howMany(cardListWithoutSuit, "K") == 1) {
            return 303;
        }

        //queen high
        if (howMany(cardListWithoutSuit, "Q") == 1) {
            return 304;
        }

        //jack high
        if (howMany(cardListWithoutSuit, "J") == 1) {
            return 305;
        }

        //ten high
        if (howMany(cardListWithoutSuit, "T") == 1) {
            return 306;
        }

        //nine high
        if (howMany(cardListWithoutSuit, "9") == 1) {
            return 307;
        }

        //eight high
        if (howMany(cardListWithoutSuit, "8") == 1) {
            return 308;
        }

        //seven high
        if (howMany(cardListWithoutSuit, "7") == 1) {
            return 309;
        }

        return 0;
    }
}
