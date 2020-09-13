package holdem;

enum Suit {
    SPADE, CLUB, DIAMOND, HEART;

    @Override
    public String toString() {
        return this.name().substring(0, 1).toLowerCase();
    }
}
