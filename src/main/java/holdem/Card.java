package holdem;

import java.util.stream.Stream;

public class Card implements Comparable<Card> {
    private Suit suit;
    private Rank rank;

    public Card(String cardString) {
        String rankString = cardString.substring(0, 1);
        String suitString = cardString.substring(1);
        // can use a map to avoid iterating over all values for each card
        // https://www.baeldung.com/java-enum-values
        // think about helpful exceptions for erroneously specified cards
        this.rank = Stream.of(Rank.values()).filter(x -> x.toString().equals(rankString)).findFirst().get();
        this.suit = Stream.of(Suit.values()).filter(x -> x.toString().equals(suitString)).findFirst().get();
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank.toString() + suit.toString();
    }

    @Override
    public int compareTo(Card other) {
        if (this.rank == other.rank) {
            return 0;
        }
        return this.rank.ordinal() > other.rank.ordinal() ? 1 : -1;
    }
}
