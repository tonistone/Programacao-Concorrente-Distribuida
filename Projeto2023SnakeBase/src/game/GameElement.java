package game;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import environment.Cell;

public abstract class GameElement {
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
