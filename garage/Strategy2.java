
import java.util.List;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.parties.NegotiationInfo;


import negotiator.bidding.BidDetails;


public class Strategy2 extends BiddingStrategy {

	double lastThresh;
	
 
	
	public void init(NegotiationInfo info, ModelScore[] ms, AgentID[] agents, List<NashBidDetails> nashbids) {
		super.init(info, ms, agents, nashbids);
	    lastThresh=1;
	}

	public Bid getBid(double time) {
		double thresh = getNashThresh(time, 0.2, 1, 0);
		misc.Range r = getRange(lastThresh, thresh);
		lastThresh = thresh;
		List<BidDetails> bids = sos.getBidsinRange(r);
		Bid b = pickBest(bids);
		return b;
	}

	
}
