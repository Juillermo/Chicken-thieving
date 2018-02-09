package clean;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import negotiator.Bid;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class NashIterator extends BiddingStrategy {

	SmartBidSpace bidSpace;
    long domainSize;
    ModelHistory history;
    List<BidValueDetails> topBids;
    Queue<BidValueDetails> qBids;
    AbstractUtilitySpace utilitySpace;
   
	public NashIterator(SmartBidSpace bidSpace,  long domainSize, ModelHistory history, AbstractUtilitySpace utilitySpace) {
		this.bidSpace=bidSpace;
		this.domainSize=domainSize;
		this.history = history;
		this.utilitySpace = utilitySpace;
		topBids = null;
		qBids = new LinkedList<BidValueDetails>();

		
	}
	
	public List<BidValueDetails> getTopBids(){
		topBids = bidSpace.getNashBids((int)domainSize/8);
		BidValueDetails bid;
		double minUtility = Parameters.nashIteratorMin;
		Bid backupOffer = history.getBackupOffer();
		if(backupOffer!=null){
			minUtility=utilitySpace.getUtility(history.getBackupOffer());
		}
		for(int i=0;i<topBids.size();i++){
			bid= topBids.get(i);
			if(bid.getUt() <minUtility)
				topBids.remove(bid);
		}
		return topBids;
	}

	@Override
	public Bid getBid()  {
		if(topBids == null){
			topBids = getTopBids();
			}
		if(qBids.isEmpty()){
			qBids.addAll(topBids);
		}
		Bid bid = qBids.remove().getBid();
		return bid;
		}
	
}