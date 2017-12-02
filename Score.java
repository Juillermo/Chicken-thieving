

import java.util.ArrayList;

public class Score{
	private
	int score;
	ArrayList<Integer> tracker;
	int mistakes;
	int correct;
	int turn;
	
	public Score(){
		
		
		score=0;
		mistakes=0;
		tracker=new ArrayList<Integer>();
		turn=0;
		
	}
	
	public void update(boolean res){
		turn++;
		if(res){
			correct=correct+1;
			score=score+1;
			tracker.add(1);
		}
		else{
			mistakes=mistakes+1;
			score=score-1;
			tracker.add(-1);
		}
	}
	
	public int score(){
		return score;
	}
	
	public boolean trackRec(int window,int overlook){
		int neg=0;
		for(int i=0;i<window;i++)
			{
			neg=(tracker.get(tracker.size()-i)==-1)?neg+1:neg;
			if(neg>overlook)
				return false;
			}
		return true;
	}
	
}