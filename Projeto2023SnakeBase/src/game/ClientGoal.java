package game;

import environment.BoardPosition;

public class ClientGoal {
    
    private BoardPosition pos;
    private int value;
    
    public ClientGoal(BoardPosition pos, int value) {
        this.pos = pos;
        this.value = value;
    }

    public BoardPosition getPos() {
        return pos;
    }

    public int getValue() {
        return value;
    }
}
