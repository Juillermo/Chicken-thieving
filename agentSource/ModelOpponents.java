package clean;

import java.util.HashMap;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.boaframework.OpponentModel;

public class ModelOpponents{
	
	HashMap<AgentID, OMScorer> oppModels;

	public ModelOpponents(HashMap<AgentID, OMScorer> oppModels ){
		this.oppModels = oppModels;
	}
	
	public HashMap<AgentID, OpponentModel> getOMs(){
		HashMap<AgentID, OpponentModel> OMs = new HashMap<AgentID, OpponentModel>();
		OpponentModel OM = null;
		for(AgentID agent : oppModels.keySet()){
			OM = oppModels.get(agent).getBestOm();
			OMs.put(agent, OM);
		}
		return OMs;
	}
	
	public boolean areOMsConfident(){
		boolean confidence=true;
		for(AgentID agent : oppModels.keySet()){
			if(!oppModels.get(agent).isConfident()){
				confidence = false;
			}
		}
		return confidence;
	}
	
	public void updateMessageReceived(AgentID agent, Action act, double time, Bid bidOnTable)  {
			if (act instanceof Offer) {
				Offer offer = (Offer) act;
				Bid bidOffered = offer.getBid();
				OMScorer scorer = oppModels.get(agent);
				scorer.scoreModels(bidOffered, bidOnTable);
				scorer.updateModels(bidOffered, time);
			}
			
		}
	
	
	
}