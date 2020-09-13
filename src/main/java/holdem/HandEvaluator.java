package holdem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandEvaluator {
    private ArrayList<Card> cards; // sorted card list
    private Map<Rank, List<Card>> rankMap;
    private Map<Suit, List<Card>> suitMap;
    private Rank straightFlushTopCardRank;
    private Rank straightTopCardRank;
    private Suit flushSuit;
    private Rank fourOfAKindRank;
    private List<Rank> threeOfAKindRanks; // in decreasing order of rank
    private List<Rank> pairRanks; // in decreasing order of rank
    private List<Rank> longestRankSequence; // longest consecutive rank sequence (increasing order)
    private List<Rank> rankList; // all present ranks in increasing order
    private HandRanking handRanking;

    HandEvaluator(Hand hand) {
        cards = new ArrayList<>();
        cards.addAll(Arrays.asList(hand.getHandCards()));
        cards.addAll(Arrays.asList(hand.getBoardCards()));
        Collections.sort(cards);
        rankMap = new HashMap<>();
        suitMap = new HashMap<>();
        pairRanks = new ArrayList<>();
        threeOfAKindRanks = new ArrayList<>();
        longestRankSequence = new ArrayList<>();
        rankList = new ArrayList<>();
    }

    private void preprocessHand() {
        List<Rank> rankSequence = new ArrayList<>();
        boolean hasTwo = cards.get(0).getRank() == Rank.TWO;
        Card highestRankCard = cards.get(cards.size() - 1);
        boolean hasAce = highestRankCard.getRank() == Rank.ACE;
        if (hasTwo && hasAce) { // Add ace to rank sequence if ace low straight possible
            rankSequence.add(Rank.ACE);
        }

        cards.stream().forEachOrdered(card -> {
            Rank rank = card.getRank();
            Suit suit = card.getSuit();
            List<Card> equalRankCards = getCardList(rankMap, rank);
            List<Card> equalSuitCards = getCardList(suitMap, suit);
            equalRankCards.add(card);
            equalSuitCards.add(card);

            populateLongestRankSequence(rankSequence, longestRankSequence, hasAce, card, highestRankCard);
            populateRankList(rank);
        });
    }

    private <T> List<Card> getCardList(Map<T, List<Card>> inputMap, T listKey) {
        List<Card> cardList = inputMap.get(listKey);
        if (cardList == null) {
            List<Card> newCardList = new ArrayList<>();
            inputMap.put(listKey, newCardList);
            cardList = newCardList;
        }
        return cardList;
    }

    private void populateLongestRankSequence(List<Rank> rankSequence, List<Rank> resultRankSequence, boolean hasAce,
            Card card, Card highestRankCard) {
        Rank rank = card.getRank();
        int numberOfSequentialRanks = rankSequence.size();
        Rank lastSequentialRank = rankSequence.isEmpty() ? null : rankSequence.get(numberOfSequentialRanks - 1);
        boolean extendsRankSequence = rankSequence.isEmpty() || rank.ordinal() == lastSequentialRank.ordinal() + 1
                || lastSequentialRank == Rank.ACE && rank == Rank.TWO;
        boolean isLastCard = card.equals(highestRankCard);

        if (extendsRankSequence) {
            rankSequence.add(rank);
            if (isLastCard) {
                replaceCurrentLongestRankSequenceIfALongerIsFound(rankSequence, resultRankSequence);
            }
        } else {
            replaceCurrentLongestRankSequenceIfALongerIsFound(rankSequence, resultRankSequence);
            if (rank != lastSequentialRank) { // skip cards of rank equal to current highest rank
                rankSequence.clear();
                rankSequence.add(rank);
            }
        }
    }

    private void replaceCurrentLongestRankSequenceIfALongerIsFound(List<Rank> rankSequence,
            List<Rank> resultRankSequence) {
        if (rankSequence.size() > resultRankSequence.size()) {
            resultRankSequence.clear();
            resultRankSequence.addAll(rankSequence);
        }
    }

    private void populateRankList(Rank rank) {
        int numberOfRanks = rankList.size();
        Rank previousRank = rankList.isEmpty() ? null : rankList.get(numberOfRanks - 1);
        if (rank != previousRank) {
            rankList.add(rank);
        }
    }

    private void determinePossibleHandRankings() {
        flushSuit = suitMap.values().stream().filter(suitList -> suitList.size() >= 5).map(x -> x.get(0).getSuit())
                .findAny().orElse(null);
        boolean hasFlush = flushSuit != null;
        int numberOfCardsInLongestRankSequence = longestRankSequence.size();
        straightTopCardRank = numberOfCardsInLongestRankSequence >= 5
                ? longestRankSequence.get(numberOfCardsInLongestRankSequence - 1)
                : null;
        boolean hasStraight = straightTopCardRank != null;

        // there can't be both a (straight or flush) and (a four of a kind or full
        // house) among 7 cards, also if there is either a straight or flush, we don't
        // care about three of a kind or lower rankings
        if (!hasFlush && !hasStraight) {
            rankList.stream().sorted(Comparator.reverseOrder()).forEachOrdered(rank -> {
                int numberOfCardsWithRank = rankMap.get(rank).size();
                switch (numberOfCardsWithRank) {
                    case 4:
                        fourOfAKindRank = rank;
                        break;
                    case 3:
                        // A full house could be made from 'two 3 of a kinds' among the 7 cards
                        threeOfAKindRanks.add(rank);
                        break;
                    case 2:
                        pairRanks.add(rank);
                        break;
                    default:
                        break;
                }
            });
        }

        if (hasFlush && hasStraight) {
            checkForStraightFlush();
        }
    }

    private void checkForStraightFlush() {
        List<Card> commonCards = new ArrayList<>();
        suitMap.get(flushSuit).stream().forEachOrdered(flushCard -> {
            if (longestRankSequence.contains(flushCard.getRank())) {
                commonCards.add(flushCard);
            }
        });

        if (commonCards.size() >= 5) {
            List<Rank> flushRankSequence = new ArrayList<>();
            List<Rank> longestFlushRankSequence = new ArrayList<>();
            boolean hasTwo = commonCards.get(0).getRank() == Rank.TWO;
            Card highestRankFlushCard = commonCards.get(commonCards.size() - 1);
            boolean hasAce = highestRankFlushCard.getRank() == Rank.ACE;
            if (hasTwo && hasAce) { // Add ace to flush rank sequence if ace low straight flush possible
                flushRankSequence.add(Rank.ACE);
            }

            commonCards.stream().forEachOrdered(card -> {
                populateLongestRankSequence(flushRankSequence, longestFlushRankSequence, hasAce, card,
                        highestRankFlushCard);
            });

            int numberOfCardsInFlushRankSequence = longestFlushRankSequence.size();
            if (numberOfCardsInFlushRankSequence >= 5) {
                straightFlushTopCardRank = longestFlushRankSequence.get(numberOfCardsInFlushRankSequence - 1);
            }
        }
    }

    private HandRanking decideOverallHandRanking() {
        if (straightFlushTopCardRank != null) {
            return HandRanking.STRAIGHT_FLUSH;
        } else if (fourOfAKindRank != null) {
            return HandRanking.FOUR_OF_A_KIND;
        } else if (threeOfAKindRanks.size() == 2 || !threeOfAKindRanks.isEmpty() && !pairRanks.isEmpty()) {
            return HandRanking.FULL_HOUSE;
        } else if (flushSuit != null) {
            return HandRanking.FLUSH;
        } else if (straightTopCardRank != null) {
            return HandRanking.STRAIGHT;
        } else if (!threeOfAKindRanks.isEmpty()) {
            return HandRanking.THREE_OF_A_KIND;
        } else if (pairRanks.size() >= 2) {
            return HandRanking.TWO_PAIRS;
        } else if (!pairRanks.isEmpty()) {
            return HandRanking.PAIR;
        } else {
            return HandRanking.HIGH_CARD;
        }
    }

    public HandRanking getHandRanking() {
        return handRanking;
    }

    public Rank getStraightFlushTopCardRank() {
        return straightFlushTopCardRank;
    }

    public Rank getFourOfAKindRank() {
        return fourOfAKindRank;
    }

    public Rank getFourOfAKindKickerRank() {
        return cards.stream().sorted(Comparator.reverseOrder()).filter(card -> card.getRank() != fourOfAKindRank)
                .map(card -> card.getRank()).findFirst().get();
    }

    public Rank getThreeOfAKindRank() {
        return threeOfAKindRanks.get(0);
    }

    public Rank getHighestPairRank() {
        return threeOfAKindRanks.size() > 1 ? threeOfAKindRanks.get(1) : pairRanks.get(0);
    }

    public List<Rank> getFlushRankList() { // flush rank list in decreasing rank order
        return suitMap.get(flushSuit).stream().sorted(Comparator.reverseOrder()).map(card -> card.getRank()).limit(5)
                .collect(Collectors.toList());
    }

    public Rank getStraightTopCardRank() {
        return straightTopCardRank;
    }

    public List<Rank> getThreeOfAKindKickerRanks() {
        Rank threeOfAKindRank = threeOfAKindRanks.get(0);
        return cards.stream().sorted(Comparator.reverseOrder()).filter(card -> card.getRank() != threeOfAKindRank)
                .limit(2).map(card -> card.getRank()).collect(Collectors.toList());
    }

    public Rank getSecondHighestPairRank() {
        return pairRanks.get(1);
    }

    public Rank getTwoPairsKickerRank() {
        Rank highestPairRank = pairRanks.get(0);
        Rank secondHighestPairRank = pairRanks.get(1);
        return cards.stream().sorted(Comparator.reverseOrder())
                .filter(card -> card.getRank() != highestPairRank && card.getRank() != secondHighestPairRank)
                .map(card -> card.getRank()).findFirst().get();
    }

    public List<Rank> getPairKickerRanks() {
        Rank pairRank = pairRanks.get(0);
        return cards.stream().sorted(Comparator.reverseOrder()).filter(card -> card.getRank() != pairRank).limit(3)
                .map(card -> card.getRank()).collect(Collectors.toList());
    }

    public List<Rank> getHighCardKickerRanks() {
        return cards.stream().sorted(Comparator.reverseOrder()).map(card -> card.getRank()).limit(5)
                .collect(Collectors.toList());
    }

    public void evaluateHand() {
        preprocessHand();
        determinePossibleHandRankings();
        handRanking = decideOverallHandRanking();
    }
}