package clean;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.utility.AbstractUtilitySpace;

public class ModelHistory{
	
	private Bid lastReceivedOffer;
	private Bid myLastOffer;
	private Bid backupOffer;
	private Bid offerOnTable;
	AbstractUtilitySpace utilitySpace;
	
	public ModelHistory(AbstractUtilitySpace utilitySpace){
		this.utilitySpace = utilitySpace;
	}
	
	public Bid getLastReceivedOffer() {
		return lastReceivedOffer;
	}
	public void setLastReceivedOffer(Bid lastReceivedOffer) {
		this.lastReceivedOffer = lastReceivedOffer;
	}
	public Bid getMyLastOffer() {
		return myLastOffer;
	}
	public void setMyLastOffer(Bid myLastOffer) {
		this.myLastOffer = myLastOffer;
	}
	public Bid getBackupOffer() {
		return backupOffer;
	}
	public void setBackupOffer(Bid backupOffer) {
		this.backupOffer = backupOffer;
	}
	public Bid getOfferOnTable() {
		return offerOnTable;
	}
	public void setOfferOnTable(Bid offerOnTable) {
		this.offerOnTable = offerOnTable;
	}
	
	public void updateMessageReceived(Action act, AgentID agent, AgentID afterUs){
		if(act instanceof Offer){
			Offer offer = (Offer) act;
			lastReceivedOffer = offer.getBid();
			offerOnTable = lastReceivedOffer;
		}
		else if( afterUs !=null && !agent.equals(afterUs)&& act instanceof Accept){
			if (backupOffer == null
					|| (utilitySpace.getUtility(backupOffer) < utilitySpace.getUtility(offerOnTable))) {

				backupOffer = offerOnTable;
		}
	}
	}
	
	public void updateActionChosen(Action act){
		if(act instanceof Offer){
			Offer offer = (Offer) act;
			offerOnTable = offer.getBid();
		}
	}
}
	
	
	
