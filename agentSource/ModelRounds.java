package clean;

public class ModelRounds {

	int roundsSoFar;							//keeps track of number of times update method was called
	double[] store;                         //records the difference in time between update calls aka time taken for a round
	double previousTime;                    //keeps track of the time of previous call to update
	
	public ModelRounds(int roundsSoFar,double[] store) {      //constructor needs an empty storage space of fixed size
		this.roundsSoFar=roundsSoFar;
		this.store = store;
	}

	public void updateActionChosen(double time) {     

		int index = roundsSoFar % store.length;
		roundsSoFar++;
		store[index] = time - previousTime;
		previousTime = time;
	}
	

	public int getRemRounds(double time) {          //returns estimated number of rounds remaining according to time model
		int remRounds = Integer.MAX_VALUE;
		if(roundsSoFar > Parameters.startRoundModel){         // We want to exclude the first few rounds that might include initializations
			double maxtime=getMax();
		    remRounds = (int) Math.floor((1 - time) / maxtime);
		}
		return remRounds;
	}
	
	public double getMax(){                        //returns max time recorded in model
		double maxtime = 0;
		for (int i = 0; i < store.length; i++) {
			if (store[i] > maxtime)
				maxtime = store[i];
		}
		return maxtime;
	}
	
	public double getAvg(){							//returns avg of times recorded in model
		double sum = 0; 
		for (int i = 0; i < store.length; i++) {
			sum += store[i];
			}
		double avgtime = (roundsSoFar < store.length) ? sum / roundsSoFar : sum / store.length;	
		return avgtime;
	}

}
