import java.util.*;

public class GoFish {

    private final String[] SUITS = { "C", "D", "H", "S" };
    private final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

    private char whoseTurn;
    private char whoseTurnMulti;
    private final Player player;
    private final Player player2;
    private final Player computer;
    private List<Card> deck;
    private final Scanner in;

    public GoFish() {
        this.whoseTurn = 'P';
        this.whoseTurnMulti = 'P';
        this.player = new Player();
        this.player2 = new Player();
        this.computer = new Player();

        boolean reask = true;

        this.in = new Scanner(System.in);

        while(reask){
            System.out.println("\none player or two players?(respond with \"one\" or \"two\")");
            String response = in.nextLine();
            if(response != null){
                response = response.toLowerCase();
                if(response.equals("one")){
                    reask = false;
                    play();
                } else if(response.equals("two")){
                    reask = false;
                    multiplayer();
                }
            }
        }

    }

    //////////PLAYER VS PLAYER /////////////////////////////////////////////////////

    public void multiplayer() {
        shuffleAndDealMulti();

        // play the game until someone wins

        while (true) {
            if (whoseTurnMulti == 'P') {
                whoseTurnMulti = takeTurnMulti(false);

                if (player.findAndRemoveBooks()) {
                    System.out.println("PLAYER: Oh, that's a book!");
                    showBooksMulti(false);
                }
            } else if (whoseTurnMulti == '2') {
                whoseTurnMulti = takeTurnMulti(true);

                if (player2.findAndRemoveBooks()) {
                    System.out.println("PLAYER 2: Oh, that's a book!");
                    showBooksMulti(true);
                }
            }

            // the games doesn't end until all 13 books are completed, or there are
            // no more cards left in the deck. the player with the ,ost books at the
            // end of the game wins.

            int playerBooks = player.getBooks().size();
            int player2Books = player2.getBooks().size();

            String winMessage = "Congratulations, you win! " + playerBooks + " books to " + player2Books + ".";
            String loseMessage = "Maybe next time. You lose " + player2Books + " books to " + playerBooks + ".";
            String tieMessage = "Looks like it's a tie, " + playerBooks + " to " + player2Books + ".";

            if (playerBooks + player2Books == 13) {
                if (player.getBooks().size() > player2.getBooks().size()) {
                    System.out.println("\n" + winMessage);
                } else {
                    System.out.println("\n" + loseMessage);
                }
                break;
            } else if (deck.size() == 0) {
                System.out.println("\nOh no, there are no more cards in the deck!");

                if (playerBooks > player2Books) {
                    System.out.println(winMessage);
                } else if (player2Books > playerBooks) {
                    System.out.println(loseMessage);
                } else {
                    System.out.println(tieMessage);
                }
                break;
            }
        }
    }

    public void shuffleAndDealMulti() {
        if (deck == null) {
            initializeDeck();
        }
        Collections.shuffle(deck);  // shuffles the deck

        while (player.getHand().size() < 7) {
            player.takeCard(deck.remove(0));    // deal 7 cards to the
            player2.takeCard(deck.remove(0));  // player and the computer
        }
    }

    private char takeTurnMulti(boolean p2) {
        showHandMulti(p2);
        showBooksMulti(p2);

        // if requestCard returns null, then the hand was empty and new card was drawn.
        // this restarts the turn, ensuring the updated hand is printed to the console.

        Card card = requestCardMulti(p2);
        if (card == null) {
            return p2 ? '2' : 'P';     // restart this turn with updated hand
        }

        // check if your opponent has the card you requested. it will be automatically
        // relinquished if you do. otherwise, draw from the deck. return the character
        // code for whose turn it should be next.

        if (!p2) {
            if (player2.hasCard(card)) {
                showHandMulti(true);
                System.out.print("Player 2, do you have this card (yes/no)? ");
                String response = in.nextLine();
                if(response.equalsIgnoreCase("yes")){
                    player2.relinquishCard(player, card);
                    System.out.println("PLAYER 2: Here you go! ");
                }
                return 'P';

            } else {
                showHandMulti(true);
                System.out.print("Player 2, do you have this card (yes/no)? ");
                String response = in.nextLine();
                if(response.equalsIgnoreCase("no")){
                    System.out.println("PLAYER 2: Nope, go fish!");
                    player.takeCard(deck.remove(0));//take card?
                }
                return '2';
            }
        } else {
            if (player.hasCard(card)) {
                showHandMulti(false);
                System.out.print("Player 1, do you have this card? ");
                String response = in.nextLine();
                if(response.equalsIgnoreCase("yes")){
                    player.relinquishCard(player2, card);
                    System.out.println("PLAYER 1: Here you go! ");
                }
                return '2';
            } else {
                showHandMulti(false);
                System.out.print("Player 1, do you have this card? ");
                String response = in.nextLine();
                if(response.equalsIgnoreCase("no")){
                    System.out.println("PLAYER 1: Nope, go fish!");
                    player2.takeCard(deck.remove(0));
                }
                return 'P';
            }
        }
    }

    private Card requestCardMulti(boolean p2) {
        Card card = null;

        // request a card from your opponent, ensuring that the request is valid.
        // if your hand is empty, we return null to signal the calling method to
        // restart the turn. otherwise, we return the requested card.

        while (card == null) {
            if (!p2) {
                if (player.getHand().size() == 0) {
                    player.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            } else {
                if (player2.getHand().size() == 0) {
                    player2.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER 2: Got any...");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            }
        }

        return card;
    }

    private void showHandMulti(boolean p2) {
        if (!p2) {
            System.out.println("\nPLAYER hand: " + player.getHand());   // only show player's hand
        }
        if (p2) {
            System.out.println("\nPLAYER 2 hand: " + player2.getHand());   // only show player's hand
        }
    }

    private void showBooksMulti(boolean p2) {
        if (!p2) {
            System.out.println("PLAYER books: " + player.getBooks());   // shows the player's books
        }
        if (p2) {
            System.out.println("\nPLAYER 2 books: " + player2.getBooks());  // shows the player 2 books
        }
    }

    //////////PLAYER VS COMPUTER ///////////////////////////////////////////////////

    public void play() {
        shuffleAndDeal();

        // play the game until someone wins

        while (true) {
            if (whoseTurn == 'P') {
                whoseTurn = takeTurn(false);

                if (player.findAndRemoveBooks()) {
                    System.out.println("PLAYER: Oh, that's a book!");
                    showBooks(false);
                }
            } else if (whoseTurn == 'C') {
                whoseTurn = takeTurn(true);

                if (computer.findAndRemoveBooks()) {
                    System.out.println("CPU: Oh, that's a book!");
                    showBooks(true);
                }
            }

            // the games doesn't end until all 13 books are completed, or there are
            // no more cards left in the deck. the player with the ,ost books at the
            // end of the game wins.

            int playerBooks = player.getBooks().size();
            int computerBooks = computer.getBooks().size();

            String winMessage = "Congratulations, you win! " + playerBooks + " books to " + computerBooks + ".";
            String loseMessage = "Maybe next time. You lose " + computerBooks + " books to " + playerBooks + ".";
            String tieMessage = "Looks like it's a tie, " + playerBooks + " to " + computerBooks + ".";

            if (playerBooks + computerBooks == 13) {
                if (player.getBooks().size() > computer.getBooks().size()) {
                    System.out.println("\n" + winMessage);
                } else {
                    System.out.println("\n" + loseMessage);
                }
                break;
            } else if (deck.size() == 0) {
                System.out.println("\nOh no, there are no more cards in the deck!");

                if (playerBooks > computerBooks) {
                    System.out.println(winMessage);
                } else if (computerBooks > playerBooks) {
                    System.out.println(loseMessage);
                } else {
                    System.out.println(tieMessage);
                }
                break;
            }
        }
    }

    public void shuffleAndDeal() {
        if (deck == null) {
            initializeDeck();
        }
        Collections.shuffle(deck);  // shuffles the deck

        while (player.getHand().size() < 7) {
            player.takeCard(deck.remove(0));    // deal 7 cards to the
            computer.takeCard(deck.remove(0));  // player and the computer
        }
    }

    private char takeTurn(boolean cpu) {
        showHand(cpu);
        showBooks(cpu);

        // if requestCard returns null, then the hand was empty and new card was drawn.
        // this restarts the turn, ensuring the updated hand is printed to the console.

        Card card = requestCard(cpu);
        if (card == null) {
            return cpu ? 'C' : 'P';     // restart this turn with updated hand
        }

        // check if your opponent has the card you requested. it will be automatically
        // relinquished if you do. otherwise, draw from the deck. return the character
        // code for whose turn it should be next.

        if (!cpu) {
            if (computer.hasCard(card)) {
                System.out.println("CPU: Yup, here you go!");
                computer.relinquishCard(player, card);

                return 'P';
            } else {
                System.out.println("CPU: Nope, go fish!");
                player.takeCard(deck.remove(0));

                return 'C';
            }
        } else {
            if (player.hasCard(card)) {
                System.out.println("CPU: Oh, you do? Well, hand it over!");
                player.relinquishCard(computer, card);

                return 'C';
            } else {
                System.out.println("CPU: Ah, I guess I'll go fish...");
                computer.takeCard(deck.remove(0));


                return 'P';
            }
        }
    }

    private Card requestCard(boolean cpu) {
        Card card = null;

        // request a card from your opponent, ensuring that the request is valid.
        // if your hand is empty, we return null to signal the calling method to
        // restart the turn. otherwise, we return the requested card.

        while (card == null) {
            if (!cpu) {
                if (player.getHand().size() == 0) {
                    player.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            } else {
                if (computer.getHand().size() == 0) {
                    computer.takeCard(deck.remove(0));

                    return null;
                } else {
                    card = computer.getCardByNeed();
                    System.out.println("CPU: Got any... " + card.getRank());
                }
            }
        }

        return card;
    }

    private void showHand(boolean cpu) {
        if (!cpu) {
            System.out.println("\nPLAYER hand: " + player.getHand());   // only show player's hand
        }
    }

    private void showBooks(boolean cpu) {
        if (!cpu) {
            System.out.println("PLAYER books: " + player.getBooks());   // shows the player's books
        } else {
            System.out.println("\nCPU books: " + computer.getBooks());  // shows the computer's books
        }
    }


    ////////// PRIVATE METHODS /////////////////////////////////////////////////////

    private void initializeDeck() {
        deck = new ArrayList<>(52);

        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(new Card(rank, suit));     // adds 52 cards to the deck (13 ranks, 4 suits)
            }
        }
    }


    ////////// MAIN METHOD /////////////////////////////////////////////////////////

    public static void main(String[] args) {
        System.out.println("#########################################################");
        System.out.println("#                                                       #");
        System.out.println("#   ####### #######   ####### ####### ####### #     #   #");
        System.out.println("#   #       #     #   #          #    #       #     #   #");
        System.out.println("#   #  #### #     #   #####      #    ####### #######   #");
        System.out.println("#   #     # #     #   #          #          # #     #   #");
        System.out.println("#   ####### #######   #       ####### ####### #     #   #");
        System.out.println("#                                                       #");
        System.out.println("#   A human v. CPU rendition of the classic card game   #");
        System.out.println("#   Go Fish. Play the game, read and modify the code,   #");
        System.out.println("#   and make it your own!                               #");
        System.out.println("#                                                       #");
        System.out.println("#########################################################");

        new GoFish().play();
    }
}