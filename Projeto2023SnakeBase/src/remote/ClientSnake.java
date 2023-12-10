package remote;

import java.io.Serializable;
import java.util.LinkedList;
import environment.BoardPosition;

public class ClientSnake implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private LinkedList<BoardPosition> listPos = new LinkedList<>();
    private int id;
    private boolean isHuman;
    
    public ClientSnake(LinkedList<BoardPosition> listPos, int id) {
        this.listPos = listPos;
        this.id = id;
    }
   
    public LinkedList<BoardPosition> getListPos() {
        return listPos;
    }
    public int getId() {
        return id;
    }

    public int getLength() {
        return listPos.size();
    }

    public boolean isHumanSnake() {
        return isHuman;
    }

}
