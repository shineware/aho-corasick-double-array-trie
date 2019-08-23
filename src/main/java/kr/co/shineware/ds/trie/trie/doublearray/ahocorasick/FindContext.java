package kr.co.shineware.ds.trie.trie.doublearray.ahocorasick;

public class FindContext {
    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    private int currentState = 0;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position = 1;
}
