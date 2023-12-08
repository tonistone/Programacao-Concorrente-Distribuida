package game;

import java.io.Serializable;

import environment.BoardPosition;

public class ClientGoal implements Serializable {
    
    private static final long serialVersionUID = 1L;
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
