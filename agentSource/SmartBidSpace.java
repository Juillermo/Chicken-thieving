package clean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.utility.AbstractUtilitySpace;

public class SmartBidSpace{
	
	SortedOutcomeSpace outcomeSpace;
	ModelOpponents oppModels;
	AbstractUtilitySpace utilitySpace;
	ArrayList<BidValueDetails> sortedBids;
	
	
	
	public SmartBidSpace(SortedOutcomeSpace outcomeSpace, ModelOpponents oppModels, AbstractUtilitySpace utilitySpace){
		this.outcomeSpace = outcomeSpace;
		this.oppModels = oppModels;
		this.utilitySpace = utilitySpace;
		sortedBids=null;
	}
	
	public List<BidValueDetails> getBidsInRange(double thresh1, double thresh2){
		int thresh1Index = outcomeSpace.getIndexOfBidNearUtility(thresh1);
		int thresh2Index = outcomeSpace.getIndexOfBidNearUtility(thresh2);
		misc.Range range = (thresh1Index > thresh2Index) ? new misc.Range(thresh1, thresh2) : new misc.Range(thresh2, thresh1);
		List<BidDetails> bids = outcomeSpace.getBidsinRange(range);
		List<BidValueDetails> bidValues = getBidValues(bids);
		return bidValues;
	}
	
	public List<BidValueDetails> getBidValues(List<BidDetails> bids){
		List<BidValueDetails> bidValues = new ArrayList<BidValueDetails>();
		BidValueDetails bidValue;
		Bid bid;
		for(int i=0;i<bids.size();i++){
			bid = bids.get(i).getBid();
			bidValue = new BidValueDetails(bid, utilitySpace.getUtility(bid),computeNashProduct(bid));
			bidValues.add(bidValue);
		}
		return bidValues;
	}
	
	public double computeNashProduct(Bid bid) {
		
		HashMap<AgentID, OpponentModel> OMs = oppModels.getOMs();
		double utilityForUs = utilitySpace.getUtility(bid);
		double nashProduct= utilityForUs;
		double utilityForOpponent = 0;
		for(AgentID agent: OMs.keySet()){
			utilityForOpponent = OMs.get(agent).getBidEvaluation(bid);
			nashProduct = nashProduct * utilityForOpponent;
		}
	
		return nashProduct;
	}
	
	public void generateSortedBids() {
		BidIterator bidIterator = new BidIterator(utilitySpace.getDomain());
		ArrayList<BidValueDetails> sortedBids = new ArrayList<BidValueDetails>();
		Bid bid;
		BidValueDetails bidValue;

		while (bidIterator.hasNext()) {
			bid = bidIterator.next();
			bidValue = new BidValueDetails(bid, utilitySpace.getUtility(bid), computeNashProduct(bid));
			sortedBids.add(bidValue);
		}

		Collections.sort(sortedBids, Collections.reverseOrder(BidValueDetails.weightedComparator));
		this.sortedBids=sortedBids;
	}

	public List<BidValueDetails> getNashBids(int numOfBids) {

		if(sortedBids==null){
			generateSortedBids();
		}
		List<BidValueDetails> nashList = new ArrayList<>(sortedBids.subList(0, numOfBids));

		return nashList;
	}
	
	public double getUtilityAtNashPoint(){
		if(sortedBids==null){
			generateSortedBids();
		}
		return sortedBids.get(0).getUt();
	}
	
	
	
}