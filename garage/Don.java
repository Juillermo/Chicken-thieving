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
 
 public class Don extends AbstractNegotiationParty {
 
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
 	double pmin;
 	int phase3count;
 	List<NashBidDetails> phase3bids;
 	List<NashBidDetails> nashbids;
 	double phase2at;
 	double phase3at;
 	double phase4at;
 	double phase3Aat;
 	ModelDomain modelDomain;
 	Bid onTable;
+	int afterUs;
+	boolean chooseActionFlag;
+	boolean setOrderFlag;
 
 	/**
 	 * init is called when a nxt session starts with the same opponent.
 	 */
 	@Override
 	public void init(NegotiationInfo info) {
 		super.init(info);
 		modelDomain = new ModelDomain(info.getUtilitySpace());
 		int numModels;
 		if (modelDomain.getSize() > 10000)
 			numModels = 1;
 		else if (modelDomain.getSize() > 1000)
 			numModels = 2;
 		else
 			numModels = 3;
 		omr = new OMrepo(info);
 		models = new OpponentModel[2][];
 		numModels = 18;
 		models[0] = omr.getModels(numModels);
 		models[1] = omr.getModels(numModels);
 
 		ms = new ModelScore[2];
 		ms[0] = new ModelScore(info, models[0]);
 		ms[1] = new ModelScore(info, models[1]);
 		ms[0].printState(getMaxUtilityBid());
 		ms[1].printState(getMaxUtilityBid());
 
 		pmin = 0;
 		MINIMUM_BID_UTILITY = utilitySpace.getReservationValueUndiscounted();
 		t = new ModelTime();
 		sos = new SortedOutcomeSpace(info.getUtilitySpace());
 		lastThresh = 1.0;
 		lastNashThresh = 1.0;
 		rounds = 0;
 		backup = null;
 		agents = new AgentID[2];
 		agents[0] = null;
 		agents[1] = null;
 		nashbids = null;
 		phase3count = 0;
 		phase3bids = null;
 
 		phase2at = 0.5;
 		phase3Aat = 0.95;
 		phase3at = 0.9;
 		phase4at = 0.98;
 		onTable = null;
+		chooseActionFlag = false;
+		afterUs = -1;
+		setOrderFlag = false;
 	}
 
 	@Override
 	public String getDescription() {
 		return description;
 	}
 
 	public int getAgentId(AgentID a) {
 		int agentid = -1;
 		if (agents[0] == null) {
 			agentid = 0;
 			agents[0] = a;
 		} else if (agents[1] == null) {
 			agentid = 1;
 			agents[1] = a;
 		} else
 			agentid = (agents[0].equals(a)) ? 0 : 1;
 		return agentid;
 
 	}
 
 	@Override
 	public void receiveMessage(AgentID sender, Action act) {
 		super.receiveMessage(sender, act);
 		int ag = getAgentId(sender);
+		if (chooseActionFlag && !setOrderFlag) {
+			setOrderFlag = true;
+			afterUs = ag;
+		}
 		if (act instanceof Offer) { // sender is making an offer
 			Offer offer = (Offer) act;
 			try {
 				ms[ag].updateModels(offer, timeline, onTable);
 
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
+
 			lastReceivedOffer = offer.getBid();
 			onTable = lastReceivedOffer;
 
-		} else if (ag == 1 && act instanceof Accept) {
-
-			if (backup == null || (getUtility(backup) < getUtility(lastReceivedOffer))) {
-				backup = lastReceivedOffer;
-				System.out.println("backup util is" + getUtility(backup));
+		} else if (setOrderFlag && ag != afterUs && act instanceof Accept) {
+			if (backup == null || (getUtility(backup) < getUtility(onTable))) {
+				backup = onTable;
+				System.out.println("backup util is" + getUtility(backup) + "at round " + rounds);
 				phase3at = phase3Aat;
 			}
+
 		}
 
 	};
 
 	@Override
 	public Action chooseAction(List<Class<? extends Action>> classes) {
-
+		chooseActionFlag = true;
 		rounds++;
 
 		double time = getTimeLine().getTime();
 		t.model(time);
 		Action action = null;
 		int rem = t.getRemRounds(time);
 		double thresh;
 		Bid b = null;
-
+		System.out.println("Don: Remaining rounds: " + rem);
 		if (rem <= 1 && time > phase3at) {
 			System.out.println("Don: Last round!? Time: " + time + ", max time per round: " + t.maxtime);
 			action = new Accept(this.getPartyId(), lastReceivedOffer);
 			return action;
 
 		} else if ((time > phase4at || rem < 5) && backup != null) {
+			System.out.println("Don: Offering backup");
 			b = backup;
 
 		} else if (time > phase3at && nashflag) {
 			b = phase3bid(rem);
-			System.out.println("phase3");
+			System.out.println("Don: Entering phase 3");
 
 		} else if (time > phase2at && ms[0].confident(100, 5) && ms[1].confident(100, 5)) {
 			if (!nashflag) {
 				sortNash();
 				nashflag = true;
 			}
 			b = phase2bid(time);
 		} else {
 			b = phase1bid(time);
 		}
 
 		if (b != null) {
 			myLastOffer = b;
 			action = new Offer(this.getPartyId(), myLastOffer);
-			;
+
 		} else {
-			System.out.println("b is null");
+			System.out.println("Don: b is null");
 			myLastOffer = getMaxUtilityBid();
 			action = new Offer(this.getPartyId(), myLastOffer);
 		}
 
 		if (getUtility(myLastOffer) <= getUtility(lastReceivedOffer)) {
 
 			if (backup != null && getUtility(lastReceivedOffer) <= getUtility(backup))
 				action = new Offer(this.getPartyId(), backup);
 			else
 				action = new Accept(this.getPartyId(), lastReceivedOffer);
 		}
 
 		if (action instanceof Offer)
 			onTable = ((Offer) action).getBid();
 
 		return action;
 	}
 
 	@Override
 	public java.util.HashMap<java.lang.String, java.lang.String> negotiationEnded(Bid acceptedBid) {
 		Bid nashBid = getNashBids(1).get(0).getBid();
+		
 		System.out.println("Don: Our utility for Nash is " + getUtility(nashBid));
 		ms[0].printState(nashBid);
 		ms[1].printState(nashBid);
+		
 		return null;
 	}
 
 	public Bid phase3bid(int rem) {
 		if (phase3count == 0) {
 			int i = 0;
 			phase3bids = new ArrayList<NashBidDetails>();
 
 			while (i < rem) {
 				if (i < (Math.ceil(modelDomain.getSize() / 8)))
 					phase3bids.add(nashbids.get(i));
 				else
 					phase3bids.add(nashbids.get(i % (int) (Math.ceil(modelDomain.getSize() / 8))));
 				i++;
 			}
 		}
-
 		phase3count++;
 		if (phase3count > phase3bids.size())
 			phase3count = 1;
+		
 		return phase3bids.get(phase3count - 1).getBid();
 	}
 
 	public Bid phase2bid(double time) {
 		double thresh = getNashThresh(time, 0.2, 1, 0);
 		misc.Range r = getRange(lastNashThresh, thresh);
 		lastNashThresh = thresh;
 		List<BidDetails> bids = sos.getBidsinRange(r);
 		Bid b = pickBest(bids);
 		return b;
-
 	}
 
 	public Bid phase1bid(double time) {
 		double thresh = getThresh(time, 0.2, 1, 0.8);
 
 		misc.Range r = getRange(lastThresh, thresh);
 		lastThresh = thresh;
 		List<BidDetails> bids = sos.getBidsinRange(r);
 		Bid b = pickBest(bids);
 		return b;
 	}
 
 	public double getThresh(double time, double e, double Pmax, double Pmin) {
 		double theta = Math.pow(time, (1.0 / e));
 		return Pmin + (Pmax - Pmin) * (1 - theta);
 	}
 
 	public double getNashThresh(double time, double e, double Pmax, double Pmin) {
 		double theta = Math.pow(time, (1.0 / e));
 		double thresh = Pmin + (Pmax - Pmin) * (1 - theta);
 		double ut = getUtility(getNashBids(1).get(0).getBid());
 
-		System.out.println("Don: utility at nash is " + ut);
+		System.out.println("Don: Utility at nash is " + ut);
 		Pmin = ut - 0.05;
 		thresh = Pmin + (Pmax - Pmin) * (1 - theta);
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
 
 	private misc.Range getRange(double a, double b) {
 		int aI = sos.getIndexOfBidNearUtility(a);
 		int bI = sos.getIndexOfBidNearUtility(b);
 		misc.Range r;
 
 		r = (aI > bI) ? new misc.Range(a, b) : new misc.Range(b, a);
 
 		return r;

