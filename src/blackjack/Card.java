package blackjack;

public class Card {
    private final int faceValue;
    private final Suit suit;

    public Card(int c, Suit s) {
        faceValue = c;
        suit = s;
    }

    public int value() {
        return faceValue;
    }

    public Suit suit() {
        return suit;
    }
}
