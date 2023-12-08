package game;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import environment.Board;
import environment.Cell;

public abstract class GameElement implements Serializable {
	//private Board board;
    private Cell myCell;
	private Lock lock = new ReentrantLock();

    public Cell getMyCell(){
		return myCell;
	}

    public void setMyCell(Cell newCell){
		myCell = newCell;
	}

	public void acquireLock() {
        lock.lock();
    }

    public void releaseLock() {
        lock.unlock();
    }
}
