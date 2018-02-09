package clean;

import negotiator.Bid;
import negotiator.timeline.TimeLineInfo;

public class PhasewiseStrategy extends BiddingStrategy{
	
	Phases phases;
	BiddingStrategy[] strategies;
	TimeLineInfo timeline;
	ModelRounds roundsModel;
	ModelDomain domainModel;
	ModelHistory history;
	ModelOpponents oppModels;
	
	
	public PhasewiseStrategy(Phases phases,BiddingStrategy[] strategies, TimeLineInfo timeline, ModelRounds roundsModel, ModelDomain domainModel, ModelHistory history, ModelOpponents oppModels){
		this.phases = phases;
		this.strategies = strategies;
		this.timeline=timeline;
		this.roundsModel=roundsModel;
		this.domainModel = domainModel;
		this.history = history;
		this.oppModels = oppModels;
	}
	
	public Bid getBid() throws Exception{
		double time = timeline.getTime();
		int numRoundsRemaining = roundsModel.getRemRounds(time);
		boolean backupExists = !(history.getBackupOffer() == null);
		long domainSize = domainModel.getSize();
		boolean confident = oppModels.areOMsConfident();
		
		int phase = phases.getPhase(time, numRoundsRemaining, backupExists, domainSize, confident);
		Bid bid = strategies[phase].getBid();
		return bid;
	}
}