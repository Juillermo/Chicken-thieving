package clean;

import negotiator.Bid;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;


public class LastRoundAcceptor extends AcceptStrategy{
	
	ModelHistory history;
	ModelRounds rounds;
	TimeLineInfo timeline;
	AbstractUtilitySpace utilitySpace;
	
	public LastRoundAcceptor(ModelHistory history, ModelRounds rounds, TimeLineInfo timeline, AbstractUtilitySpace utilitySpace){
		this.history = history;
		this.rounds = rounds;
		this.timeline= timeline;
		this.utilitySpace = utilitySpace;
	}
	
	public boolean isAcceptable(Bid bid){
		boolean acceptable = false;
		if(rounds.getRemRounds(timeline.getTime()) <= 1){
			acceptable = true;
		}
		else if(utilitySpace.getUtility(history.getLastReceivedOffer()) > utilitySpace.getUtility(bid)){
			acceptable = true;
		}
		return acceptable;
	}
}