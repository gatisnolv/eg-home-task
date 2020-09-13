package holdem;

import java.util.List;

public class Hand implements Comparable<Hand> {
    private Card[] handCards;
    private Card[] boardCards;
    private HandEvaluator evaluator;

    Hand(Card card1, Card card2, Card[] boardCards) {
        this.handCards = new Card[] { card1, card2 };
        this.boardCards = boardCards;
        evaluator = new HandEvaluator(this);
    }

    public Card[] getHandCards() {
        return handCards;
    }

    public Card[] getBoardCards() {
        return boardCards;
    }

    private HandRanking getHandRanking() {
        return evaluator.getHandRanking();
    }

    @Override
    public int compareTo(Hand other) {
        return compareTo(other, true);
    }

    public int compareTo(Hand other, boolean considerAlphabeticOrdering) {
        int rankingOrdinal = getHandRanking().ordinal();
        int otherRankingOrdinal = other.getHandRanking().ordinal();
        if (rankingOrdinal == otherRankingOrdinal) {
            int sameHandRankingComparison = compareToHandWithSameHandRanking(other);
            if (sameHandRankingComparison == 0 && considerAlphabeticOrdering) {
                return toString().compareTo(other.toString());
            }
            return sameHandRankingComparison;
        }
        return rankingOrdinal > otherRankingOrdinal ? 1 : -1;
    }

    private int compareToHandWithSameHandRanking(Hand other) {
        HandRanking ranking = getHandRanking();
        switch (ranking) {
            case STRAIGHT_FLUSH:
                return compareStraightFlushes(other);
            case FOUR_OF_A_KIND:
                return compareFourOfAKinds(other);
            case FULL_HOUSE:
                return compareFullHouses(other);
            case FLUSH:
                return compareFlushes(other);
            case STRAIGHT:
                return compareStraights(other);
            case THREE_OF_A_KIND:
                return compareThreeOfAKinds(other);
            case TWO_PAIRS:
                return compareTwoPairsHands(other);
            case PAIR:
                return comparePairs(other);
            default: // HIGH_CARD
                return compareHighCards(other);
        }
    }

    private int compareStraightFlushes(Hand other) {
        Rank topCard = evaluator.getStraightFlushTopCardRank();
        Rank otherTopCard = other.evaluator.getStraightFlushTopCardRank();
        return topCard.compareTo(otherTopCard);
    }

    private int compareFourOfAKinds(Hand other) {
        Rank four = evaluator.getFourOfAKindRank();
        Rank otherFour = other.evaluator.getFourOfAKindRank();
        Rank kicker = evaluator.getFourOfAKindKickerRank();
        Rank otherKicker = other.evaluator.getFourOfAKindKickerRank();
        int foursComparison = four.compareTo(otherFour);
        return foursComparison != 0 ? foursComparison : kicker.compareTo(otherKicker);
    }

    private int compareFullHouses(Hand other) {
        Rank three = evaluator.getThreeOfAKindRank();
        Rank otherThree = other.evaluator.getThreeOfAKindRank();
        Rank pair = evaluator.getHighestPairRank();
        Rank otherPair = other.evaluator.getHighestPairRank();
        int threesComparison = three.compareTo(otherThree);
        return threesComparison != 0 ? threesComparison : pair.compareTo(otherPair);
    }

    private int compareFlushes(Hand other) {
        List<Rank> ranks = evaluator.getFlushRankList();
        List<Rank> otherRanks = other.evaluator.getFlushRankList();
        return compareKickers(ranks, otherRanks);
    }

    private int compareKickers(List<Rank> ranks, List<Rank> otherRanks) {
        int sizeOfRankList = ranks.size();
        int rankComparison = 0;
        for (int i = 0; i < sizeOfRankList; i++) {
            rankComparison = ranks.get(i).compareTo(otherRanks.get(i));
            if (rankComparison != 0) {
                return rankComparison;
            }
        }
        return rankComparison;
    }

    private int compareStraights(Hand other) {
        Rank topCard = evaluator.getStraightTopCardRank();
        Rank otherTopCard = other.evaluator.getStraightTopCardRank();
        return topCard.compareTo(otherTopCard);
    }

    private int compareThreeOfAKinds(Hand other) {
        Rank three = evaluator.getThreeOfAKindRank();
        Rank otherThree = other.evaluator.getThreeOfAKindRank();
        List<Rank> kickers = evaluator.getThreeOfAKindKickerRanks();
        List<Rank> otherKickers = other.evaluator.getThreeOfAKindKickerRanks();
        int threesComparison = three.compareTo(otherThree);
        return threesComparison != 0 ? threesComparison : compareKickers(kickers, otherKickers);
    }

    private int compareTwoPairsHands(Hand other) {
        Rank highPair = evaluator.getHighestPairRank();
        Rank otherHighPair = other.evaluator.getHighestPairRank();
        Rank lowPair = evaluator.getSecondHighestPairRank();
        Rank otherLowPair = other.evaluator.getSecondHighestPairRank();
        Rank kicker = evaluator.getTwoPairsKickerRank();
        Rank otherKicker = other.evaluator.getTwoPairsKickerRank();
        int highPairsComparison = highPair.compareTo(otherHighPair);
        if (highPairsComparison != 0) {
            return highPairsComparison;
        }
        int lowPairsComparison = lowPair.compareTo(otherLowPair);
        return lowPairsComparison != 0 ? lowPairsComparison : kicker.compareTo(otherKicker);
    }

    private int comparePairs(Hand other) {
        Rank pair = evaluator.getHighestPairRank();
        Rank otherPair = other.evaluator.getHighestPairRank();
        List<Rank> kickers = evaluator.getPairKickerRanks();
        List<Rank> otherKickers = other.evaluator.getPairKickerRanks();
        int pairsComparison = pair.compareTo(otherPair);
        return pairsComparison != 0 ? pairsComparison : compareKickers(kickers, otherKickers);
    }

    private int compareHighCards(Hand other) {
        return compareKickers(evaluator.getHighCardKickerRanks(), other.evaluator.getHighCardKickerRanks());
    }

    @Override
    public String toString() {
        return handCards[0].toString() + handCards[1].toString();
    }

    public void evaluateHand() {
        evaluator.evaluateHand();
    }
}
