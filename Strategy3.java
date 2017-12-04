
import java.util.ArrayList;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.parties.NegotiationInfo;


public class Strategy3 extends BiddingStrategy {

	double count;
	List<NashBidDetails> bids;
    int domainSize;
    Bid backup;
	
	public void init(NegotiationInfo info, ModelScore[] ms, AgentID[] agents, int domainSize, Bid backup, List<NashBidDetails> nashbids) {
		super.init(info, ms, agents, nashbids);
	    count=0;
	    this.domainSize=domainSize;
	    this.backup=backup;
	}

	public Bid getBid(int rem) {
		if (count == 0) {
			int i = 0;
			bids = new ArrayList<NashBidDetails>();

			while (i < (Math.ceil(domainSize / 8))) {
				if (backup!=null 	&& utilitySpace.getUtility(nashbids.get(i).getBid()) > utilitySpace.getUtility(backup))
					bids.add(nashbids.get(i));

				i++;
			}
			sortBids(bids, NashBidDetails.utComparator);
		}

		count++;
		if (count > bids.size())
			count = 1;
		int repeat = (int) Math.floor(rem / bids.size());
		int index = (int) Math.floor((count - 1) / repeat);
		return bids.get(index).getBid();
	}


	
}
