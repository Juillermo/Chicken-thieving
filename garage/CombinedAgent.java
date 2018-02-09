

import java.util.List;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Action;
import agents.anac.y2015.Atlas3.Atlas3;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

public class CombinedAgent extends AbstractNegotiationParty {
	
	private
	Action actionOfPartner;
	Bid lastPartnerBid;
    
    Atlas3 b;
    
	private static double MINIMUM_BID_UTILITY = 0.0;

	
	/**
	 * init is called when a nxt session starts with the same opponent.
	 */
	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		MINIMUM_BID_UTILITY = utilitySpace.getReservationValueUndiscounted();
		actionOfPartner = null;
		lastPartnerBid= null;
		
		b=new Atlas3();
		b.init(info);
	}

	@Override
	 public String getDescription() {
        return "combined agent";
    }

	  @Override
	    public void receiveMessage(AgentID sender, Action act) {
	        super.receiveMessage(sender, act);

	       b.receiveMessage(sender, act);
	    }
	


	@Override
	public Action chooseAction(List<Class<? extends Action>> classes) {
		
		
			Action action = b.chooseAction(classes);
		    return action;
			
	}

}

	
	
	 
		