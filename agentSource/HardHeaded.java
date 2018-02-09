package clean;

import negotiator.Bid;
import negotiator.utility.AbstractUtilitySpace;

public class HardHeaded extends BiddingStrategy{
	
	AbstractUtilitySpace utilitySpace;
	
	public HardHeaded(AbstractUtilitySpace utilitySpace){
		this.utilitySpace = utilitySpace;
		}
	
	public Bid getBid() throws Exception {
		return utilitySpace.getMaxUtilityBid();
	}
}