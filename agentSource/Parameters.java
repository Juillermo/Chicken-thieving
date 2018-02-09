package clean;

public class Parameters{
	/******** for Tracker ************/
	static int trackerWindow = 100;
	static int trackerOverlook= 5;
	
	/*********** for ModelRounds *****/
	static int timeStoreSize = 20;
	static int startRoundModel = timeStoreSize+5;
	
	/********** for boulware **********/
	static double Pmax = 1;
	static double Pmin =0.8;
	static double e = 0.1;
	static double startingThresh = 1;
	
	/********** for nash_boulware **********/
	static double nPmax = 1;
	static double nPmin =0;
	static double ne = 0.2;
	
	/****** phase switch times *****/
	static double phase2 = 0.5;
	static double phase3A = 0.9;
	static double phase3 = 0.95;
	static double phase4 = 0.99;
	static int phase4R = 2;
	
	static double nashIteratorMin = 0;
	
	
	public static  int getNumOfOppModels(long domainSize){
		int num=0;
			if (domainSize > 20000)
				num = 2;
			else if (domainSize> 10000)
				num = 3;
			else if (domainSize > 5000)
				num = 4;
			else if (domainSize > 1000)
				num = 5;
			else
				num = 6;
			return num;
		}
	}
