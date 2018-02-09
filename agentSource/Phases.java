package clean;

import negotiator.Bid;

public class Phases {
	
	double phase2atTime;
	double phase3AatTime;
	double phase3atTime;
	double phase4atTime;
	int phase4atRound;

	
public Phases(double phase2atTime,double phase3AatTime,double phase3atTime,double phase4atTime, int phase4atRound){
	
	this.phase2atTime = phase2atTime;
	this.phase3AatTime = phase3AatTime;
	this.phase3atTime = phase3atTime;
	this.phase4atTime = phase4atTime;
	this.phase4atRound = phase4atRound;
	
}

public int getPhase(double time, int roundsRemaining, boolean backupExists, long domainSize, boolean areOMsConfident) {
	int phase = 0;

	if (roundsRemaining <= 1 && time > phase3atTime)
		phase = 4;
	else if ((time > phase4atTime || roundsRemaining < phase4atRound) && backupExists)
		phase = 3;
	else if ((time > phase3atTime || roundsRemaining <domainSize/8))
		phase = 2;
	else if ((time > phase3AatTime || roundsRemaining <domainSize/8) && !backupExists)
		phase = 2;
	else if (time > phase2atTime && areOMsConfident)
		phase = 1;
	else
		phase = 0;

	return phase;

}


}
