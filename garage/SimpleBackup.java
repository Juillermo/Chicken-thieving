

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.SessionData;
import negotiator.boaframework.OpponentModel;


import negotiator.bidding.BidDetails;
import negotiator.actions.Accept;
import negotiator.boaframework.SortedOutcomeSpace;


public class SimpleBackup extends AbstractNegotiationParty {
	
	private

	
    final String description = "Backup Agent";
    Bid lastReceivedOffer; // offer on the table
    Bid myLastOffer;
    ModelTime t;
    SortedOutcomeSpace sos;
    double lastThresh;
    double lastNashThresh;
    int rounds;
    NegotiationSession ns;
    SessionData s;
    private static double MINIMUM_BID_UTILITY = 0.0;
	AgentID[] agents;
    ModelScore[] ms;
    OpponentModel[][] models;
    OMrepo omr;
    Bid backup;
    boolean nashflag;
    double pmin=0;
   int numModels=2;
   int phase3count;
   List<NashBidDetails> phase3bids;
	List<NashBidDetails> nashbids;
   double phase2at;
   double phase3at;
	ModelDomain md;
	/**
	 * init is called when a nxt session starts with the same opponent.
	 */
	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		
		MINIMUM_BID_UTILITY = utilitySpace.getReservationValueUndiscounted();
		t=new ModelTime();
        sos=new SortedOutcomeSpace(info.getUtilitySpace());
        lastThresh=1.0;
        rounds=0;
        backup=null;
        agents=new AgentID[2];
        agents[0]=null;
        agents[1]=null;
              
	
		
	}

	@Override
	 public String getDescription() {
        return description;
    }

	public int getAgentId(AgentID a){
		int agentid=-1;
		if(agents[0]==null)
			{agentid=0;agents[0]=a;}
		else if(agents[1]==null)
		{agentid=1;agents[1]=a;}
		else agentid=(agents[0].equals(a))?0:1;
		return agentid;
			
	}
	@Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);
        int ag = getAgentId(sender);
       if (act instanceof Offer) { // sender is making an offer
            Offer offer = (Offer) act;
            lastReceivedOffer = offer.getBid();
        }
       else if(ag==1 && act instanceof Accept){
    	   if(backup==null || (getUtility(backup)<getUtility(lastReceivedOffer)))
    		   {backup=lastReceivedOffer;System.out.println("backup util is"+ getUtility(backup));}
    	      		   
       }
        
    };
	


	@Override
	public Action chooseAction(List<Class<? extends Action>> classes) {
		
rounds++;
    	
    	double time = getTimeLine().getTime();
    	t.model(time);
    	Action action=null;
    	int rem=t.getRemRounds(time);
    	double thresh;
    	Bid b=null;
    	if(rem<=2)
    		{System.out.println("last");
    		action =  new Accept(this.getPartyId(),lastReceivedOffer);
    		}
    	
    	else if(time>0.95 || rem<5 && backup!=null){
    		b= backup;
    	}
    	
    	else {
    		    b=phase1bid(time);
                
            }
    	if (b!=null){
            myLastOffer = b;
            action=new Offer(this.getPartyId(), myLastOffer);
            
        }
    else{
    	System.out.println("b is null");
    	myLastOffer=getMaxUtilityBid();
    	action=new Offer(this.getPartyId(), myLastOffer);
    }
        
    	
         if(getUtility(myLastOffer)<=getUtility(lastReceivedOffer))
        	 action=new Accept(this.getPartyId(), lastReceivedOffer);
           	
        		
        return action;
			
	}

  
   public Bid phase1bid(double time){

    double thresh=getThresh(time,0.1,1,0.7);
	
	
	misc.Range r=getRange(lastThresh,thresh);
	lastThresh=thresh;
	List<BidDetails> bids=sos.getBidsinRange(r);
   Bid b=bids.get(0).getBid();
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
 
    


	
			