

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


public class Observer extends AbstractNegotiationParty {
	
	private

	
    final String description = "Observer Agent";
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
   double phase4at;
   double phase3Aat;
	ModelDomain md;
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
        lastNashThresh=1.0;
        rounds=0;
        backup=null;
        agents=new AgentID[2];
        agents[0]=null;
        agents[1]=null;
        nashbids=null;
        phase3count=0;
        phase3bids=null;
        md=new ModelDomain(info.getUtilitySpace());
        phase2at=0.5;
        phase3Aat=0.95;
        phase3at=0.9;
        phase4at=0.98;
        
        
	
		
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
            try {
				ms[ag].updateModels( offer, timeline,lastReceivedOffer);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            lastReceivedOffer = offer.getBid();
        }
       else if(ag==1 && act instanceof Accept){
    	   if(backup==null || (getUtility(backup)<getUtility(lastReceivedOffer)))
    		   {backup=lastReceivedOffer;System.out.println("backup util is"+ getUtility(backup));phase3at=phase3Aat;}
    	      		   
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
    		if(backup==null)System.out.println("no backup");
    		action =  new Accept(this.getPartyId(),lastReceivedOffer);
    		return action;
    		}
    	else if(time>phase4at || rem<5 && backup!=null){
    		b= backup;
    	}
    	else if(time>phase3at && nashflag){
    		b=phase3bid(rem);
    		System.out.println("phase3");
    	}
    	
    	
    	else if(time>phase2at && ms[0].confident(100,5) && ms[1].confident(100,5))
		{
		if(!nashflag)
			{sortNash();nashflag=true;}
		b=phase2bid(time);
	         
    	}
    	
    	else{
    		    b=phase1bid(time);
                
            }
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
        	 {
        	 if(backup!=null && getUtility(lastReceivedOffer)<=getUtility(backup))
        		 action=new Offer(this.getPartyId(), backup);
        	 else
        		 
        		 action=new Accept(this.getPartyId(), lastReceivedOffer);
        	 }
           	
        		
        return action;
			
	}

  
   public Bid phase3bid(int rem){
	   if(phase3count==0 ){
		  int i=0;
		  phase3bids=new ArrayList<NashBidDetails>();
		  
		  while(i<rem){
			  if(i<(Math.ceil(md.getSize()/8)))
				  phase3bids.add(nashbids.get(i));
			  else
				  phase3bids.add(nashbids.get(i%(int)(Math.ceil(md.getSize()/8))));
			  i++;
		  }
	   }
	  
	   phase3count++;
	   if(phase3count>phase3bids.size())
		   phase3count=1;
	   return phase3bids.get(phase3count-1).getBid();
   }
   
   public Bid phase2bid(double time){
	   double thresh=getNashThresh(time,0.2,1,0);
    	misc.Range r=getRange(lastNashThresh,thresh);
    	lastNashThresh=thresh;
   	   List<BidDetails> bids=sos.getBidsinRange(r);
       Bid b=pickBest(bids);
       return b;
       
        
   }
   
   public Bid phase1bid(double time){
    double thresh=getThresh(time,0.2,1,0.8);
	
	
	misc.Range r=getRange(lastThresh,thresh);
	lastThresh=thresh;
	List<BidDetails> bids=sos.getBidsinRange(r);
   Bid b=pickBest(bids);
   return b;
   }
   
    public double getThresh(double time, double e,double Pmax, double Pmin){
    	double theta= Math.pow(time,(1.0/e));
    	double thresh = Pmin+(Pmax-Pmin)*(1-theta);		
    	return thresh;
    }
    
    public double getNashThresh(double time, double e,double Pmax, double Pmin){
    	double theta= Math.pow(time,(1.0/e));
    	double thresh = Pmin+(Pmax-Pmin)*(1-theta);		
    	double ut=getUtility(getNashBids(1).get(0).getBid());
    	System.out.println("utility at nash is "+ut);
		Pmin=ut-0.05;
		thresh = Pmin+(Pmax-Pmin)*(1-theta);
		
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
		double nash=0;
		double maxNash=0;
		Bid bestBid=null;
		for(int i=0;i<bd.size();i++){
			b=bd.get(i).getBid();
			nash=nashProduct(b);
			if(nash>=maxNash){
				maxNash=nash;
				bestBid=b;
			}
		}
		if(bestBid==null)bestBid=bd.get(0).getBid();
		return bestBid;
	}
	
	public double nashProduct(Bid bid){
		OpponentModel om0=ms[0].getBestOm();
		OpponentModel om1=ms[1].getBestOm();
		double v1=0;
        double v2=0;
        double v3=0;
        v1=getUtility(bid);
        if(om0!=null)
		    v2=om0.getBidEvaluation(bid);
		if(om1!=null)
			v3=om1.getBidEvaluation(bid);
	    double nash=v1*v2*v3;
	    return nash;	
	}
	

	public void sortNash(){
		BidIterator bidIterator = new BidIterator(utilitySpace.getDomain());
		nashbids=new ArrayList<NashBidDetails>();
		Bid bid;
		NashBidDetails nbid;
		while (bidIterator.hasNext()) {
		   bid = bidIterator.next();
		   nbid=new NashBidDetails(bid,getUtility(bid),nashProduct(bid));
		   nashbids.add(nbid);
		   }
		Collections.sort(nashbids,Collections.reverseOrder(NashBidDetails.weightedComparator));
		
	}
	
	public List<NashBidDetails> getNashBids(int n){
		List<NashBidDetails> nashList = new ArrayList<>(nashbids.subList(0,n));
		return nashList;
		
	}

    }
 
    


	
			