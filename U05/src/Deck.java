import java.util.*;

public class Deck {
    public enum Typ { KREUZ, PIK, HERZ, KARO};
    public enum Rang { ASS, ZWEI, DREI, VIER, FUENF, SECHS, SIEBEN, ACHT, NEUN, ZEHN, BUBE, DAME, KOENIG};
    private Typ type;
    private Rang rang;
    private List<Card> cards;
    int numberOfDecks = 0;

    public Deck(String statsDatum) {
        cards = new ArrayList<>();
        for (int i = 0; i < numberOfDecks; i++) {

        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        return cards.remove(cards.size() - 1);
    }
}