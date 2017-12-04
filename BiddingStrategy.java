
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.parties.NegotiationInfo;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.boaframework.OpponentModel;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.SortedOutcomeSpace;

public class BiddingStrategy {

	SortedOutcomeSpace sos;
	AgentID[] agents;
	ModelScore[] ms;
	AbstractUtilitySpace utilitySpace;
	List<NashBidDetails> nashbids;

	public void init(NegotiationInfo info, ModelScore[] ms, AgentID[] agents,
			List<NashBidDetails> nashbids) {
		this.agents = agents;
		utilitySpace = info.getUtilitySpace();
		sos = new SortedOutcomeSpace(utilitySpace);
		this.ms = ms;
		this.nashbids = nashbids;

	}

	public void init(NegotiationInfo info, ModelScore[] ms, AgentID[] agents) {
		this.agents = agents;
		utilitySpace = info.getUtilitySpace();
		sos = new SortedOutcomeSpace(utilitySpace);
		this.ms = ms;
		this.nashbids = null;

	}

	public Bid getBid(double time) {
		return getMaxUtilityBid();
	}

	public double getThresh(double time, double e, double Pmax, double Pmin) {
		double theta = Math.pow(time, (1.0 / e));
		return Pmin + (Pmax - Pmin) * (1 - theta);
	}

	public double getNashThresh(double time, double e, double Pmax, double Pmin) {
		double theta = Math.pow(time, (1.0 / e));
		double thresh = Pmin + (Pmax - Pmin) * (1 - theta);
		double ut = utilitySpace.getUtility(getNashBids(1).get(0).getBid());

		System.out.println("Don: Utility at nash is " + ut);
		Pmin = ut - 0.05;
		thresh = Pmin + (Pmax - Pmin) * (1 - theta);

		return thresh;
	}

	public Bid getMaxUtilityBid() {
		try {
			return this.utilitySpace.getMaxUtilityBid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public misc.Range getRange(double a, double b) {
		int aI = sos.getIndexOfBidNearUtility(a);
		int bI = sos.getIndexOfBidNearUtility(b);
		misc.Range r;

		r = (aI > bI) ? new misc.Range(a, b) : new misc.Range(b, a);

		return r;
	}

	public Bid pickBest(List<BidDetails> bd) {
		Bid b = null;
		double nash = 0;
		double maxNash = 0;
		Bid bestBid = null;
		for (int i = 0; i < bd.size(); i++) {
			b = bd.get(i).getBid();
			nash = nashProduct(b);
			if (nash >= maxNash) {
				maxNash = nash;
				bestBid = b;
			}
		}
		if (bestBid == null)
			bestBid = bd.get(0).getBid();
		return bestBid;
	}

	public double nashProduct(Bid bid) {
		OpponentModel om0 = ms[0].getBestOm();
		OpponentModel om1 = ms[1].getBestOm();
		double v1 = 0;
		double v2 = 0;
		double v3 = 0;

		v1 = utilitySpace.getUtility(bid);
		if (om0 != null)
			v2 = om0.getBidEvaluation(bid);
		if (om1 != null)
			v3 = om1.getBidEvaluation(bid);

		return v1 * v2 * v3;
	}

	public void computeNash() {
		BidIterator bidIterator = new BidIterator(utilitySpace.getDomain());
		nashbids = new ArrayList<NashBidDetails>();
		Bid bid;
		NashBidDetails nbid;
		while (bidIterator.hasNext()) {
			bid = bidIterator.next();
			nbid = new NashBidDetails(bid, utilitySpace.getUtility(bid),
					nashProduct(bid));
			nashbids.add(nbid);
		}

	}

	public void sortBids(List<NashBidDetails> bids,
			Comparator<NashBidDetails> comp) {
		Collections.sort(bids, Collections.reverseOrder(comp));
	}

	public List<NashBidDetails> getNashBids(int n) {

		List<NashBidDetails> nashList = new ArrayList<>(nashbids.subList(0, n));

		return nashList;
	}

}
