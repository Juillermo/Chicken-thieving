package clean;

import negotiator.Bid;

public class NashBoulware extends BiddingStrategy {

	SimpleBoulware boulware;
	SmartBidSpace bidSpace;
	double Pmin;
   

	public NashBoulware(SimpleBoulware boulware, SmartBidSpace bidSpace) {
		this.boulware = boulware;
		this.bidSpace=bidSpace;
		Pmin=-1;
		
	}

	public Bid getBid()  {
		if(Pmin == -1)
		   {
			Pmin = bidSpace.getUtilityAtNashPoint();
		    boulware.setPmin(Pmin);
		   }
		return boulware.getBid();
		}
	
}