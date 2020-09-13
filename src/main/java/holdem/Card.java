package holdem;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class Card implements Comparable<Card> {
    private Suit suit;
    private Rank rank;

    public Card(String cardString) {
        try {
            String rankString = cardString.substring(0, 1);
            String suitString = cardString.substring(1);
            this.rank = Stream.of(Rank.values()).filter(x -> x.toString().equals(rankString)).findFirst().get();
            this.suit = Stream.of(Suit.values()).filter(x -> x.toString().equals(suitString)).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Invalid card encountered: " + cardString);
        }
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
