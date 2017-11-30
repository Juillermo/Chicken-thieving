
public class ModelTime {
 
	int counter;
    double avgtime;
    int store;
	double[] TT;
	double lasttime;
	
	public ModelTime(){
		counter=0;
	    avgtime=0.0;
	    store=5;
		TT=new double[store];
		lasttime=0.0;
		
	}
	public void model(double time){
		
		double sum=0;
		int rm = counter%store;
		counter++;
		TT[rm]=time-lasttime;
		for(int i=0;i<store;i++)
			{sum=sum+TT[i];}
		avgtime=(counter<store)?sum/counter:sum/store;
		lasttime=time;
		
	}
	
	public int getRemRounds(double time){
		
		double avgRoundTime = avgtime;
		int remRounds = (int)Math.floor((1-time)/avgRoundTime);
		System.out.println("\nBOA: Rounds remaining: "+ remRounds);
		
		 return remRounds;
	 }
	

}

	


