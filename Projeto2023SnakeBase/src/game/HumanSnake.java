package game;

import environment.Board;
import environment.Cell;
 /** Class for a remote snake, controlled by a human 
  * 
  * @author luismota
  *
  */
public class HumanSnake extends Snake {
	
	public HumanSnake(int id,Board board) {
		super(id,board);
	}

  protected void move(Cell cell) throws InterruptedException {
	sharedLock.lock();
	try {
		Cell head= cell;
		if (cell.isOcupied() || (cell.getOcuppyingSnake() != this && cell.getOcuppyingSnake()!=null)){
			System.out.println("Entrei no if do cell iid out of bounds");
			return;
		}

		cells.addFirst(head);
		super.goalReachedCheck(head);

		if (cells.size() > size) {
	        cells.getLast().release();
			cells.removeLast();
		}
			super.snakeMoved.signalAll();
			super.board.setChanged();
		} finally {
			sharedLock.unlock();
		}
  }

}
