import com.fasterxml.jackson.databind.ObjectMapper;
public class Card {

    public enum Typ { KREUZ, PIK, HERZ, KARO};
    public enum Rang { ASS, ZWEI, DREI, VIER, FUENF, SECHS, SIEBEN, ACHT, NEUN, ZEHN, BUBE, DAME, KOENIG};
    private Typ type;
    private Rang rang;
    private String toStringTyp;
    private String toStringRang;
    private int rangNumber;
    private static final ObjectMapper serializer = new ObjectMapper();

    public Card (Typ type, Rang rang ) {
        this.type = type;
        this.rang = rang;
        switch (type) {
            case KREUZ:
                toStringTyp = "Kreuz ";
                break;
            case PIK:
                toStringTyp = "Pik ";
                break;
            case HERZ:
                toStringTyp = "Herz ";
                break;
            case KARO:
                toStringTyp = "Karo ";
                break;
        }
        switch (rang) {
            case ASS:
                toStringRang += "Ass";
                rangNumber = 1;
                break;
            case ZWEI:
                toStringRang += "2";
                rangNumber = 2;
                break;
            case DREI:
                toStringRang += "3";
                rangNumber = 3;
                break;
            case VIER:
                toStringRang += "4";
                rangNumber = 4;
                break;
            case FUENF:
                toStringRang += "5";
                rangNumber = 5;
                break;
            case SECHS:
                toStringRang += "6";
                rangNumber = 6;
                break;
            case SIEBEN:
                toStringRang += "7";
                rangNumber = 7;
                break;
            case ACHT:
                toStringRang += "8";
                rangNumber = 8;
                break;
            case NEUN:
                toStringRang += "9";
                rangNumber = 9;
                break;
            case ZEHN:
                toStringRang += "10";
                rangNumber = 10;
                break;
            case BUBE:
                toStringRang += "Bube";
                rangNumber = 10;
                break;
            case DAME:
                toStringRang += "Dame";
                rangNumber = 10;
                break;
            case KOENIG:
                toStringRang += "Koenig";
                rangNumber = 10;
                break;
        }
    }

    public static Card fromJSON(String received) {
        return null;
    }

    public Typ getType() {
        return type;
    }

    public Rang getRang() {
        return rang;
    }

}