
public class ModelTime {
 
	int counter;
    double avgtime;
    int store;
	double[] TT;
	double lasttime;
	double maxtime;
	
	public ModelTime(){
		counter = 0;
	    avgtime = 0.0;
	    store = 5;
		TT = new double[store];
		lasttime = 0.0;
	    maxtime = 0.0;
	}
	public void model(double time){
		
		double sum = 0;
		int rm = counter%store;
		counter++;
		TT[rm] = time-lasttime;
		maxtime = 0;
	
		for(int i=0; i<store; i++){
			sum += TT[i];
			if(TT[i]>maxtime)
				maxtime=TT[i];
		}
		avgtime = (counter<store)?sum/counter:sum/store;
		lasttime = time;
	}
	
	public int getRemRounds(double time){
		int remRounds = (int)Math.floor((1-time)/maxtime);
		//System.out.println("\nBOA: Rounds remaining: "+ remRounds);
		return remRounds;
	}
	

}

	


