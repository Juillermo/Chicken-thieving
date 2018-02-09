package clean;

import java.util.List;
import negotiator.Bid;
import negotiator.timeline.TimeLineInfo;

public class SimpleBoulware extends BiddingStrategy {

	private double lastThresh;
	SmartBidSpace bidSpace;
	private double Pmax;
	private double Pmin;
	private double e;
	TimeLineInfo timeline;

	public SimpleBoulware(double startingThresh, SmartBidSpace bidSpace,
			double Pmax, double Pmin, double e, TimeLineInfo timeline ) {
		lastThresh = startingThresh;
		this.bidSpace = bidSpace;
		this.setPmax(Pmax);
		this.setPmin(Pmin);
		this.setE(e);
		this.timeline=timeline;
	}

	public Bid getBid()  {
		double time=timeline.getTime();
		double thresh = getThresh(time, e, Pmax, Pmin);
		List<BidValueDetails> bids = bidSpace.getBidsInRange(lastThresh, thresh);
		Bid b = pickBidWithBestNash(bids);
		lastThresh = thresh;
		return b;
	}

	public double getThresh(double time, double e, double Pmax, double Pmin) {
		double theta = Math.pow(time, (1.0 / e));
		return Pmin + (Pmax - Pmin) * (1 - theta);
	}

	public Bid pickBidWithBestNash(List<BidValueDetails> bids) {
		double nash = 0;
		double maxNash = 0;
		Bid bid = bids.get(0).getBid();
		Bid bestBid = bid;

		for (int i = 1; i < bids.size(); i++) {
			bid = bids.get(i).getBid();
			nash = bids.get(i).getNash();
			if (nash >= maxNash) {
				maxNash = nash;
				bestBid = bid;
			}
		}
		return bestBid;
	}

	public double getPmax() {
		return Pmax;
	}

	public void setPmax(double pmax) {
		Pmax = pmax;
	}

	public double getPmin() {
		return Pmin;
	}

	public void setPmin(double pmin) {
		Pmin = pmin;
	}

	public double getE() {
		return e;
	}

	public void setE(double e) {
		this.e = e;
	}
	
	public double getLastThresh(){
		return lastThresh;
	}

}