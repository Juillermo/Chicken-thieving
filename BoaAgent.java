

import java.util.List;
import java.util.ArrayList;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.BidIterator;
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

	
    final String description = "OMScore Agent";
    Bid lastReceivedOffer; // offer on the table
    Bid myLastOffer;
    ModelTime t;
    SortedOutcomeSpace sos;
    double lastThresh;
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
	/**
	 * init is called when a nxt session starts with the same opponent.
	 */
	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		
		omr=new OMrepo(info);
		models = new OpponentModel[2][numModels];
		models[0]=omr.getModels();
		models[1]=omr.getModels();
		ms=new ModelScore[2];
		ms[0]=new ModelScore(info,models[0]);
		ms[1]=new ModelScore(info,models[1]);
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
            ms[ag].updateModels( offer, timeline,lastReceivedOffer);
            lastReceivedOffer = offer.getBid();
        }
        
    };
	


	@Override
	public Action chooseAction(List<Class<? extends Action>> classes) {
		
rounds++;
    	
    	double time = getTimeLine().getTime();
    	t.model(time);
    	Action action=null;
    	int rem=t.getRemRounds(time);
    	if(rem<=2)
    		{System.out.println("last");
    		action =  new Accept(this.getPartyId(),lastReceivedOffer);
    		}
        double thresh=getThresh(time,0.2,1,0);
    	
    	
    	misc.Range r=getRange(lastThresh,thresh);
    	lastThresh=thresh;
    	List<BidDetails> bids=sos.getBidsinRange(r);
        Bid b=pickBest(bids);
         if (b!=null){
                myLastOffer = b;
                action=new Offer(this.getPartyId(), myLastOffer);;
                
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
    
	public Bid pickBest(List<BidDetails> bd){
		Bid b=null;
		List<Bid> bids=new ArrayList<Bid>();
		for(int i=0;i<bd.size();i++)
		bids.add(bd.get(i).getBid());
		System.out.print("Agent 0: ");
		OpponentModel om0=ms[0].getBestOm();
		System.out.print("\n Agent 1: ");
		OpponentModel om1=ms[1].getBestOm();
		System.out.println("\n !!!!!!!!!");
		double maxNash=0;
		double nash=0;
		Bid bestBid=null;
		double v0=1;
		double v1=1;
		double v2=1;
		
		for(int i=0;i<bids.size();i++){
			b=bids.get(i);
			if(om0!=null)
			    v0=om0.getBidEvaluation(b);
			if(om1!=null)
				v1=om1.getBidEvaluation(b);
			v2=getUtility(b);
			nash=v0*v1*v2 ;
			if(nash>=maxNash){
				maxNash=nash;
				bestBid=b;
			}
		}
		return bestBid;
	}

    }
 
    


	
			