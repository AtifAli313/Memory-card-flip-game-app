package com.test3;

public class MemoryCard {
    private final int identifier;
    private boolean isFlipped;
    private boolean isMatched;

    public MemoryCard(int identifier) {
        this.identifier = identifier;
        this.isFlipped = false;
        this.isMatched = false;
    }

    public int getIdentifier() {
        return identifier;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
