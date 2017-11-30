

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import agents.anac.y2015.Atlas3.Atlas3;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.SessionData;
import negotiator.boaframework.OpponentModel;


import negotiator.bidding.BidDetails;
import negotiator.actions.Accept;
import negotiator.boaframework.SortedOutcomeSpace;


public class BoaAgent extends AbstractNegotiationParty {
	
	private

	
    final String description = "Boa Agent";
    Bid lastReceivedOffer; // offer on the table
    Bid myLastOffer;
    ModelTime t;
    SortedOutcomeSpace sos;
    double lastThresh;
    int rounds;
    Atlas3 b;
    NegotiationSession ns;
    SessionData s;
    private static double MINIMUM_BID_UTILITY = 0.0;
	AgentID agent1,agent2;
    ModelScore ms;
    OpponentModel[] models;
    OMrepo omr;
   
	/**
	 * init is called when a nxt session starts with the same opponent.
	 */
	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		b=new Atlas3();
		b.init(info);
		omr=new OMrepo(info);
		models = omr.getModels();
		ms = new ModelScore(info, models);
		MINIMUM_BID_UTILITY = utilitySpace.getReservationValueUndiscounted();
		t=new ModelTime();
        sos=new SortedOutcomeSpace(info.getUtilitySpace());
        lastThresh=1.0;
        rounds=0;
	
		
	}

	@Override
	 public String getDescription() {
        return description;
    }

	@Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);
        ms.updateModels(sender, act, timeline);
        if (act instanceof Offer) { // sender is making an offer
            Offer offer = (Offer) act;

            // storing last received offer
            lastReceivedOffer = offer.getBid();
        }
    };
	


	@Override
	public Action chooseAction(List<Class<? extends Action>> classes) {
		
		rounds++;
    	
    	double time = getTimeLine().getTime();
    	System.out.println(time);
    	t.model(time);
    	Action action=null;
    	if(t.getRemRounds(time) <=1 ){
    		System.out.println("BOA: Last round!!");
    		action =  new Accept(this.getPartyId(),lastReceivedOffer);
    	}
    		
    	double thresh = getThresh(time,0.1,1,0);
    	
    	misc.Range r = getRange(lastThresh,thresh);
    	lastThresh = thresh;
    	List<BidDetails> bids = sos.getBidsinRange(r);
        Bid b = pickBestBid(bids,lastReceivedOffer);
        if (b != null){
        	myLastOffer = b;
        	action = new Offer(this.getPartyId(), myLastOffer);;    
        }else{
        	System.out.println("BOA: best bid is null");
        	myLastOffer = getMaxUtilityBid();
        	action = new Offer(this.getPartyId(), myLastOffer);
        }
        if( getUtility(lastReceivedOffer) > getUtility(myLastOffer)){
        	System.out.println("BOA: Good util!");         
     		action =  new Accept(this.getPartyId(),lastReceivedOffer);
     	}
     	
        return action;
			
	}

  
   
    public Bid pickBestBid(List<BidDetails> bids, Bid bidOnTable){
    	
    		Bid b=null;
    		List<Bid> blist=new ArrayList<Bid>();
    		try{
    			for(int i=0;i<bids.size();i++)
    		blist.add(bids.get(i).getBid());}
    		catch(Exception e){System.out.println("BOA: in except pickbestbid");
    		b=null;}
    		b=ms.pickBest(blist);
    	
    	return b;
    }
 

    public double getThresh(double time, double e,double Pmax, double Pmin){
    	double theta= Math.pow(time,(1.0/e));
    	double thresh = Pmin+(Pmax-Pmin)*(1-theta);
    	return thresh;
    }
    
    private Bid getMaxUtilityBid() {
        try {
            return this.utilitySpace.getMaxUtilityBid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private misc.Range getRange(double a,double b){
    	int aI = sos.getIndexOfBidNearUtility(a);
    	int bI=sos.getIndexOfBidNearUtility(b);
    	misc.Range r;
    	r=(aI>bI)? new misc.Range(a,b): new misc.Range(b, a);
    	return r;
    		
    	}
    }
 
    


	
	
	 
		