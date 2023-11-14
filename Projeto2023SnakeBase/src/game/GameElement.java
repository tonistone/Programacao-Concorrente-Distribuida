package game;

import environment.Cell;

public abstract class GameElement {
    private Cell originalCell;
    private Cell nextCell;

    public Cell getOriginalCell(){
		return originalCell;
	}

    public void setOriginalCell(Cell newCell){
		originalCell = newCell;
	}

    public Cell getnextCell(){
		return nextCell;
	}

    public void setnextCell(Cell newCell){
		nextCell = newCell;
	}
}
