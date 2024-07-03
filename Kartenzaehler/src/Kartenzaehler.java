import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Kartenzaehler {

    private static String name;
    private static String serverIP;
    private static int serverPort;
    private int deckCount;
    private Deck deck;
    private int gewinn;
    private int backjacksan = 0;
    private Stats stats;

    public static Card requestCard(PrintWriter out, BufferedReader in, String playerName) throws IOException {
        out.println("requestCard " + playerName);
        String response = in.readLine();
        if (response.startsWith("card")) {

        } else {
            System.err.println("Ungültige Kartenantwort vom Croupier.");
            return null;
        }
    }
    public static int requestNumberOfDecks(PrintWriter out, BufferedReader in, String playerName) throws IOException {
        out.println("numberOfDecks " + playerName);
        String response = in.readLine();
        if (response.startsWith("numberOfDecks")) {
            return Integer.parseInt(response.substring("numberOfDecks ".length()));
        } else {
            System.err.println("Ungültige Antwort des Croupiers auf die Anzahl der Decks.");
            return -1;
        }
    }

    public Stats requestStats(PrintWriter out, BufferedReader in) throws IOException {
        out.println("requestStats");
        String response = in.readLine();
        if (response.startsWith("stats")) {
            String[] statsData = response.substring("stats ".length()).split(" ");
            int gewinn = Integer.parseInt(statsData[0]);
            int blackjacks = Integer.parseInt(statsData[1]);
            Deck deck = new Deck(statsData[2]);
            return new Stats(gewinn, blackjacks, deck);
        } else {
            System.err.println("Ungültige Statistik antwort vom Croupier.");
            return null;
        }
    }

    public static void fatal(String input) {
        System.err.println(input);
        System.exit(-1);
    }

    public void getDeckCount() {
        deckCount = Croupier.deckCount; // hinzufügen einer Variable, welche die decks zählt.
    }

    public void getDeck() {
        deck = Croupier.getDeck(); // nötig eine Methode getDeck() in Croupier hinzufügen.
    }

    public void setStats(int gewinn, int blackjacks, Deck deck) {
        stats = new Stats(gewinn, blackjacks, deck);
    }

    public void sendStats(String recipient, Stats stats) {
        sendMessage(recipient, "stats " + stats.getGewinn() + " " + stats.getBlackjacks() + " " + stats.getDeck().toString());
    }

    private void sendMessage(String recipient, String message) {
    }

    public void registerCounter(String ip, int port, String playerName) {
    }

    public String calculateOptimalAction(int playerScore, int dealerCard) {
        if (playerScore == 21) {
            return "Blackjack!";
        } else if (playerScore > 21) {
            return "Busted!";
        } else if (playerScore >= 17) {
            return "„Stand.";
        } else {
            if (dealerCard >= 7 && dealerCard <= 10) {
                return "Double down wenn möglich, ansonsten hit.";
            } else {
                return "Hit.";
            }
        }
    }

    public static boolean isIP(String ip) { // Checks if String is valid IPv4 address
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public class Stats {
        private int gewinn;
        private int blackjacks;
        private Deck deck;

        public Stats(int gewinn, int blackjacks, Deck deck) {
            this.gewinn = gewinn;
            this.blackjacks = blackjacks;
        }

        public int getGewinn() {
            return gewinn;
        }

        public void setGewinn(int gewinn) {
            this.gewinn = gewinn;
        }

        public int getBlackjacks() {
            return blackjacks;
        }

        public void setBlackjacks(int blackjacks) {
            this.blackjacks = blackjacks;
        }

        public Deck getDeck() {
            return deck;
        }

        public void setDeck(Deck deck) {
            this.deck = deck;
        }
    }

    public static void main(String[] args) {
        String serverIP = ""; // Define the server IP
        int serverPort = 8080; // Define the server port
        String name = "Player1"; // Define the player name

        try (Socket socket = new Socket(serverIP, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            // Register the client with the server
            out.println("register " + name);

            // Wait for registration response from the Croupier
            String fromServer = in.readLine();
            if (fromServer.startsWith("registration successfully")) {
                System.out.println("Registration successful.");
            } else if (fromServer.startsWith("registration declined")) {
                System.err.println("Registration declined: " + fromServer.substring("registration declined ".length()));
                System.exit(-1);
            } else {
                System.err.println("ungültige Registrierungsantwort vom Croupier.");
                System.exit(-1);
            }

            // Request statistics from the Croupier
            Stats stats = requestStats(out, in);
            if (stats != null) {
                System.out.println("Erhaltene Statistiken: Gewinn=" + stats.getGewinn() + ", Blackjacks=" + stats.getBlackjacks());
            }

            // Request the number of decks from the Croupier
            int numberOfDecks = requestNumberOfDecks(out, in, name);
            System.out.println("Anzahl der erhaltenen Decks:" + numberOfDecks);

            // Request a card from the Croupier
            Card card = requestCard(out, in, name);
            System.out.println("Karte erhalten: " + card);

        } catch (UnknownHostException e) {
            fatal("Unbekannter Server mit IP" + serverIP);
        } catch (IOException e) {
            fatal("Nachricht kann nicht gesendet werden..");
        }
        System.exit(0);
    }
}
