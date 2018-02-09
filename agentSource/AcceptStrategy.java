package clean;

import negotiator.Bid;

public abstract class AcceptStrategy{
	
	public AcceptStrategy(){
		
	}
	
	public abstract boolean isAcceptable(Bid bid);
	
}