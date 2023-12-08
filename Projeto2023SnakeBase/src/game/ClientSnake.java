package game;

import java.util.LinkedList;

import environment.BoardPosition;
import environment.Cell;

public class ClientSnake {
    
    private LinkedList<BoardPosition> listPos = new LinkedList<>();
    private int id;
   
   
    public LinkedList<BoardPosition> getListPos() {
        return listPos;
    }
    public int getId() {
        return id;
    }
}
