import negotiator.AgentID;
import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.boaframework.SortedOutcomeSpace;




import java.util.List;

public class Thief2 extends AbstractNegotiationParty {
    private
    final String description = "Thief2";
    Bid lastReceivedOffer; // offer on the table
    Bid myLastOffer;
    ModelTime t;
    SortedOutcomeSpace sos;
    double lastThresh;
    int rounds;
    
    
    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        t=new ModelTime();
        sos=new SortedOutcomeSpace(info.getUtilitySpace());
        lastThresh=1.0;
        rounds=0;
    }

   
    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
    	rounds++;
    	
    	double time = getTimeLine().getTime();
    	t.model(time);
    	Action action=null;
    	if(t.getRemRounds(time)<=1)
    		action =  new Accept(this.getPartyId(),lastReceivedOffer);
    		
    	double thresh=getThresh(time,0.3,1,0);
    	
    	
    	misc.Range r=getRange(lastThresh,thresh);
    	lastThresh=thresh;
    	List<BidDetails> bids=sos.getBidsinRange(r);
        Bid b=pickBestBid(bids,lastReceivedOffer);
         if (b!=null){
                myLastOffer = b;
                action=new Offer(this.getPartyId(), myLastOffer);;
                
            }
        else{
        	System.out.println("b is null");
        	myLastOffer=getMaxUtilityBid();
        	action=new Offer(this.getPartyId(), myLastOffer);
        }
        return action;
    }
   
    public Bid pickBestBid(List<BidDetails> bids, Bid bidOnTable){
    	
    		Bid b=null;
    		try{b=bids.get(0).getBid();}
    		catch(Exception e){b=null;}
    	
    	return b;
    }
       @Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);

        if (act instanceof Offer) { // sender is making an offer
            Offer offer = (Offer) act;

            // storing last received offer
            lastReceivedOffer = offer.getBid();
        }
    }

    @Override
    public String getDescription() {
        return description;
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
 
    

