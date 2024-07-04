import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Kaertenzaehler {
    private static final int packetSize = 4096;
    private static int port;
    private static String name;
    private static int anzahlGewinn;
    private static int anzahlBackjacks;
    private static List<Card> cards;
    private static Statistik stats;
    private static String empfehlung;
    private static int deckAnzahl;


    public class Statistik {
        private int anzahlGewinn;
        private int anzahlBackjacks;
        private List<Card> cards;

        public Statistik(int anzahlGewinn, int anzahlBackjacks, List<Card> cards) {
            this.anzahlGewinn = anzahlGewinn;
            this.anzahlBackjacks = anzahlBackjacks;
            this.cards = new ArrayList<>(cards);
        }

        public int getAnzahlGewinn() {
            return anzahlGewinn;
        }

        public void setAnzahlGewinn(int anzahlGewinn) {
            this.anzahlGewinn = anzahlGewinn;
        }

        public int getAnzahlBackjacks() {
            return anzahlBackjacks;
        }

        public void setAnzahlBackjacks(int anzahlBackjacks) {
            this.anzahlBackjacks = anzahlBackjacks;
        }

        public List<Card> getCards() {
            return new ArrayList<>(cards);
        }

        public void setCards(List<Card> cards) {
            this.cards = new ArrayList<>(cards);
        }
    }

    public static void sendStatsToCrouper(String serverIp, int serverPort, Statistik stats) {
        try (Socket socket = new Socket(serverIp, serverPort)) {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);

            // Serialisieren des Statistik-Objekts in eine Zeichenfolge
            String statsString = stats.getAnzahlGewinn() + "," + stats.getAnzahlBackjacks() + ",";
            for (Card card : stats.getCards()) {
                statsString += card.getType().toString() + card.getRang().toString() + ",";
            }

            // Senden Sie das serialisierte Statistik-Objekt an den Server
            writer.println(statsString);

            // Schließen Sie die Verbindung
            writer.close();
            outputStream.close();
            socket.close();

            System.out.println("Sent stats to Croupier: " + statsString);
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
    public static void sendStatsToSpieler(String serverIp, int serverPort, Statistik stats) {
        try (Socket socket = new Socket(serverIp, serverPort)) {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);

            // Serialize the Statistik object to a string
            String statsString = stats.getAnzahlGewinn() + "," + stats.getAnzahlBackjacks() + ",";
            for (Card card : stats.getCards()) {
                statsString += card.getType().toString() + card.getRang().toString() + ",";
            }

            // Send the serialized Statistik object to the server
            writer.println(statsString);

            // Close the connection
            writer.close();
            outputStream.close();
            socket.close();

            System.out.println("Sent stats to Spieler: " + statsString);
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
        public void main(String[] args) {
            if (args.length != 2) {
                System.err.println("Arguments: \"<port number> <client name>\"");
                System.exit(-1);
            }

            try {
                port = Integer.parseInt(args[0]);
                name = args[1];
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number");
                System.exit(-1);
            }

            System.out.println(name + " (Port: " + port + ") is here.");

            new Thread(() -> receiveLines(port)).start();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                String input;
                while (!(input = br.readLine()).equalsIgnoreCase("quit")) {
                    String[] parts = input.split(" ", 4);
                    if (parts.length == 4 && parts[0].equalsIgnoreCase("send")) {
                        String ip = parts[1];
                        int port = Integer.parseInt(parts[2]);
                        String message = parts[3];
                        sendLines(ip, port, message);
                    } else {
                        System.err.println("Unknown command.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

    private static void sendLines(String ip, int port, String message) {
    }

    private static void receiveLines(int port) {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                byte[] buffer = new byte[packetSize];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (true) {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                    // Handle the received message
                    if (message.startsWith("Kartenzähler an Croupier: numberOfDecks ")) {
                        String[] parts = message.split(" ", 4);
                        String SpielerName = parts[3];
                        int deckAnzahl = Integer.parseInt(parts[1]);

                        // Send a response to the client
                        String response = "Croupier an Kartenzähler: numberOfDecks " + deckAnzahl;
                        InetAddress address = packet.getAddress();
                        int clientPort = packet.getPort();
                        sendResponse(address, clientPort, response);
                    } else {
                        System.out.println("Received: " + message);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
        }

        private static void sendResponse(InetAddress address, int port, String message) {
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(packet);
                System.out.println("Sent response: " + message);
            } catch (IOException e) {
                System.err.println("Error sending response: " + e.getMessage());
            }
        }
}

