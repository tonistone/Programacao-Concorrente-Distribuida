package game;

import java.io.Serializable;

import environment.BoardPosition;

public class ClientObstacle implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private BoardPosition pos;
    private int remaining;

    public ClientObstacle(BoardPosition pos, int remaining) {
        this.pos = pos;
        this.remaining = remaining;
    }

    public BoardPosition getPos() {
        return pos;
    }

    public int getRemaining() {
        return remaining;
    }
}
