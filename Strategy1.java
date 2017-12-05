
import java.util.List;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.parties.NegotiationInfo;


import negotiator.bidding.BidDetails;


public class Strategy1 extends BiddingStrategy {

	double lastThresh;
	
 
	
	public void init(NegotiationInfo info, ModelScore[] ms, AgentID[] agents) {
		super.init1(info, ms, agents);
	    lastThresh=1;
	}

	public Bid getBid(double time) {
		double thresh = getThresh(time, 0.2, 1, 0.8);

		misc.Range r = getRange(lastThresh, thresh);
		lastThresh = thresh;
		List<BidDetails> bids = sos.getBidsinRange(r);
		Bid b = pickBest(bids);
		return b;
	}

	
}
