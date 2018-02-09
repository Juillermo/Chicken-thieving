package clean;


public class Score {
	private int score;
	Tracker tracker;
	int numOfTurns;        //Score was collected over so many turns

	public Score(int initialScore, int initialNumOfTurns, Tracker tracker) {

		score = 0;
		this.tracker = tracker;
		numOfTurns = 0;

	}

	public void update(boolean result) {
		numOfTurns++;
		score = result?score+1:score-1;
		tracker.update(result);
		
	}

	public int getScore() {
		return score;
	}

	public boolean isConfident() {
		return tracker.isConfident();
	}
}