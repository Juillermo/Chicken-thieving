
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
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
	int afterUs;
	boolean chooseActionFlag;
	boolean setOrderFlag;

	int finalRounds;
	int nBackups;
	int nBackupsOffered;

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
		
		phase3at = 0.95;
		phase4at = 0.99;
		onTable = null;
		chooseActionFlag = false;
		afterUs = -1;
		setOrderFlag = false;

		finalRounds = 0;
		nBackups = 0;
		nBackupsOffered = 0;
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
		if (chooseActionFlag && !setOrderFlag) {
			setOrderFlag = true;
			afterUs = ag;
		}
		if (act instanceof Offer) { // sender is making an offer
			Offer offer = (Offer) act;
			try {
				ms[ag].updateModels(offer, timeline, onTable);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			lastReceivedOffer = offer.getBid();
			onTable = lastReceivedOffer;

		} else if (setOrderFlag && ag != afterUs && act instanceof Accept) {
			if (backup == null || (getUtility(backup) < getUtility(onTable))) {

				backup = onTable;
				System.out.println("Don: New backup bid is" + getUtility(backup) + "at round " + rounds);
				nBackups++;

				
			}

		}

	};

	@Override
	public Action chooseAction(List<Class<? extends Action>> classes) {
		chooseActionFlag = true;
		rounds++;

		double time = getTimeLine().getTime();
		t.model(time);
		Action action = null;
		int rem = t.getRemRounds(time);
		double thresh;
		Bid bidToOffer = null;
		System.out.println("Don: Remaining rounds: " + rem);
		if (rem <= 1 && time > phase3at) {
			System.out.println("Don: Last round!? Time: " + time + ", max time per round: " + t.maxtime);
			finalRounds++;
			action = new Accept(this.getPartyId(), lastReceivedOffer);
			return action;

		} else if ((time > phase4at || rem < 5) && backup != null) {
			System.out.println("Don: Offering backup, since only "+rem+" rounds left");
			bidToOffer = backup;
			nBackupsOffered++;

		} else if ((time > phase3at || rem<modelDomain.getSize()) && nashflag) {
			if (modelDomain.getSize() <= 5000) {
				nashflag = false;
				computeNash();
				sortBids(nashbids,NashBidDetails.nashComparator);
			}

			bidToOffer = phase3bid(rem);
			System.out.println("Don: In phase 3");

		} else if (time > phase2at && ms[0].confident(100, 5) && ms[1].confident(100, 5)) {
			if (!nashflag) {
				computeNash();
				sortBids(nashbids,NashBidDetails.nashComparator);
				nashflag = true;
			}
			bidToOffer = phase2bid(time);
		} else {
			bidToOffer = phase1bid(time);
		}

		if (bidToOffer != null) {
			action = new Offer(this.getPartyId(), bidToOffer);
		} else {
			System.out.println("Don: bidToOffer is null");
			bidToOffer = getMaxUtilityBid();
			action = new Offer(this.getPartyId(), bidToOffer);
		}

		if (getUtility(bidToOffer) <= getUtility(lastReceivedOffer)) {

			if (backup != null && getUtility(lastReceivedOffer) <= getUtility(backup)) {
				action = new Offer(this.getPartyId(), backup);
				nBackupsOffered++;
			} else
				action = new Accept(this.getPartyId(), lastReceivedOffer);
		}

		if (action instanceof Offer)
			onTable = ((Offer) action).getBid();

		myLastOffer = bidToOffer;
		return action;
	}

	@Override
	public java.util.HashMap<java.lang.String, java.lang.String> negotiationEnded(Bid acceptedBid) {
		Bid nashBid = getNashBids(1).get(0).getBid();

		System.out.println("Don: Our utility for Nash is " + getUtility(nashBid));
		ms[0].printState(nashBid);
		ms[1].printState(nashBid);

		System.out.println("Don: There were " + finalRounds + " final rounds.");
		System.out.println("Don: There were " + nBackups + " backups that have been offered " + nBackups + " times.");

		return null;
	}

	public Bid phase3bid(int rem) {
		if (phase3count == 0) {
			int i = 0;
			phase3bids = new ArrayList<NashBidDetails>();

			while (i < (Math.ceil(modelDomain.getSize() / 8))) {
				if (backup != null
						&& getUtility(nashbids.get(i).getBid()) > getUtility(backup))
					phase3bids.add(nashbids.get(i));

				i++;
			}
			sortBids(phase3bids, NashBidDetails.utComparator);
		}

		phase3count++;
		if (phase3count > phase3bids.size())
			phase3count = 1;
		int repeat = (int) Math.floor(rem / phase3bids.size());
		int index = (int) Math.floor((phase3count - 1) / repeat);
		return phase3bids.get(index).getBid();
	}

	public Bid phase2bid(double time) {
		double thresh = getNashThresh(time, 0.2, 1, 0);
		misc.Range r = getRange(lastNashThresh, thresh);
		lastNashThresh = thresh;
		List<BidDetails> bids = sos.getBidsinRange(r);
		Bid b = pickBest(bids);
		return b;
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

		System.out.println("Don: Utility at nash is " + ut);
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
	}

	public Bid pickBest(List<BidDetails> bd) {
		Bid b = null;
		double nash = 0;
		double maxNash = 0;
		Bid bestBid = null;
		for (int i = 0; i < bd.size(); i++) {
			b = bd.get(i).getBid();
			nash = nashProduct(b);
			if (nash >= maxNash) {
				maxNash = nash;
				bestBid = b;
			}
		}
		if (bestBid == null)
			bestBid = bd.get(0).getBid();
		return bestBid;
	}

	public double nashProduct(Bid bid) {
		OpponentModel om0 = ms[0].getBestOm();
		OpponentModel om1 = ms[1].getBestOm();
		double v1 = 0;
		double v2 = 0;
		double v3 = 0;

		v1 = getUtility(bid);
		if (om0 != null)
			v2 = om0.getBidEvaluation(bid);
		if (om1 != null)
			v3 = om1.getBidEvaluation(bid);

		return v1 * v2 * v3;
	}

	public void computeNash() {
		BidIterator bidIterator = new BidIterator(utilitySpace.getDomain());
		nashbids = new ArrayList<NashBidDetails>();
		Bid bid;
		NashBidDetails nbid;
		while (bidIterator.hasNext()) {
			bid = bidIterator.next();
			nbid = new NashBidDetails(bid, getUtility(bid), nashProduct(bid));
			nashbids.add(nbid);
		}

	}

	public void sortBids(List<NashBidDetails> nb,
			Comparator<NashBidDetails> comp) {
		Collections.sort(nb, Collections.reverseOrder(comp));
	}

	public List<NashBidDetails> getNashBids(int n) {

		List<NashBidDetails> nashList = new ArrayList<>(nashbids.subList(0, n));

		return nashList;
	}

}
