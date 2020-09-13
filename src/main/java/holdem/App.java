package holdem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public final class App {

    private static final int NUMBER_OF_CHARS_IN_HOLDEM_HAND = 4;
    private static final String EQUAL_VALUE_HAND_SEPARATOR = "=";
    private static final String DIFFERENT_VALUE_HAND_SEPARATOR = " ";
    private static final String TIME_EXECUTION_PARAMETER = "--timed";

    private App() {
    }

    public void processDeal(String dealString) {
        try {
            List<Hand> deal = parseDeal(dealString);
            evaluateAndSortDealHands(deal);
            printFormattedSortedHands(deal);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            System.out.println(message + ", skipping line: " + dealString);
        }
    }

    public List<Hand> parseDeal(String dealString) {
        Card[] boardCards = getBoardCards(dealString);
        return getHands(dealString, boardCards);
    }

    public Card[] getBoardCards(String dealString) {
        String boardCardsString = dealString.substring(0, 10);
        Card[] result = new Card[5];
        for (int i = 0; i < 5; i++) {
            int cardStart = i * 2;
            result[i] = new Card(boardCardsString.substring(cardStart, cardStart + 2));
        }
        return result;
    }

    public List<Hand> getHands(String dealString, Card[] boardCards) {
        String handsString = dealString.substring(10);
        Arrays.sort(boardCards); // sort board cards once for all hands
        Scanner handsScanner = new Scanner(handsString);
        List<Hand> hands = new ArrayList<>();
        while (handsScanner.hasNext()) {
            String handString = handsScanner.next();
            checkHandString(handString);
            Card card1 = new Card(handString.substring(0, 2));
            Card card2 = new Card(handString.substring(2));
            hands.add(new Hand(card1, card2, boardCards));
        }
        handsScanner.close();
        return hands;
    }

    private void checkHandString(String handString) {
        if (handString.length() != NUMBER_OF_CHARS_IN_HOLDEM_HAND) {
            throw new RuntimeException("Invalid hand string encountered: " + handString);
        }
    }

    private void evaluateAndSortDealHands(List<Hand> deal) {
        deal.stream().forEach(hand -> {
            hand.evaluateHand();
        });
        Collections.sort(deal);
    }

    private void printFormattedSortedHands(List<Hand> deal) {
        StringBuilder outputBuilder = new StringBuilder();
        String separator = "";
        Hand previousHand = null;
        for (Hand hand : deal) {
            if (previousHand != null && hand.compareTo(previousHand, false) == 0) {
                separator = EQUAL_VALUE_HAND_SEPARATOR;
            }
            outputBuilder.append(separator + hand);
            separator = DIFFERENT_VALUE_HAND_SEPARATOR;
            previousHand = hand;
        }
        System.out.println(outputBuilder);
    }

    public static void main(String[] args) throws IOException {
        List<String> arguments = Arrays.asList(args);
        boolean measureExecutionTime = arguments.contains(TIME_EXECUTION_PARAMETER);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        long start = measureExecutionTime ? System.currentTimeMillis() : 0;
        String line = reader.readLine();
        while (line != null) {
            new App().processDeal(line);
            line = reader.readLine();
        }
        if (measureExecutionTime) {
            long end = System.currentTimeMillis();
            System.out.println((double) (end - start) / 1000);
        }

    }
}
