
import java.util.List;
import java.util.ArrayList;

import negotiator.AgentID;
import negotiator.Bid;

import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

import negotiator.boaframework.OpponentModel;

import negotiator.actions.Accept;

public class Thief1 extends AbstractNegotiationParty {

	private final String description = "Observer Agent";
	Bid lastReceivedOffer; // offer on the table
	Bid myLastOffer;
	ModelTime t;
	int rounds;
	int numModels;
	AgentID[] agents;
	ModelScore[] ms;
	OpponentModel[][] models;
	OMrepo omr;
	Bid backup;
	boolean nashflag;
	List<NashBidDetails> nashbids;

	BiddingStrategy s1, s2, s3;
	ModelDomain modelDomain;
	Bid onTable;
	int afterUs;
	boolean chooseActionFlag;
	boolean setOrderFlag;
	boolean phase3flag;

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
		numModels = getNumModels();
		omr = new OMrepo(info);
		models = new OpponentModel[2][];
		models[0] = omr.getModels(numModels);
		models[1] = omr.getModels(numModels);

		ms = new ModelScore[2];
		ms[0] = new ModelScore(info, models[0]);
		ms[1] = new ModelScore(info, models[1]);

		t = new ModelTime();

		agents = new AgentID[2];
		agents[0] = null;
		agents[1] = null;

		nashbids = new ArrayList<NashBidDetails>();
		s1 = new Strategy1();
		s1.init(info, ms, agents);
		s2 = new Strategy2();
		s2.init(info, ms, agents, nashbids);
		s3 = new Strategy3();
		s3.init(info, ms, agents, nashbids);

		onTable = null;
		chooseActionFlag = false;
		afterUs = -1;
		setOrderFlag = false;
		rounds = 0;
		backup = null;
		finalRounds = 0;
		nBackups = 0;
		nBackupsOffered = 0;
		nashflag = false;
		phase3flag=false;

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
			ms[ag].updateModels(offer, timeline, onTable);

			lastReceivedOffer = offer.getBid();
			onTable = lastReceivedOffer;

		} else if (setOrderFlag && ag != afterUs && act instanceof Accept) {
			if (backup == null || (getUtility(backup) < getUtility(onTable))) {

				backup = onTable;
				System.out.println("Don: New backup bid is"
						+ getUtility(backup) + "at round " + rounds);
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

		Bid bidToOffer = null;
		System.out.println("Don: Remaining rounds: " + rem);
		int phase = getPhase(time, rem);
		switch (phase) {
		case 5: {
			System.out.println("Don: Last round!? Time: " + time
					+ ", max time per round: " + t.maxtime);
			finalRounds++;
			action = new Accept(this.getPartyId(), lastReceivedOffer);
			return action;

		}

		case 4: {
			System.out.println("Don: Offering backup, since only " + rem
					+ " rounds left");
			bidToOffer = backup;
			nBackupsOffered++;

		}
			break;

		case 3: {
            
			if (modelDomain.getSize() <= 100) {
				if(!phase3flag)
				{s3.computeNash();
				s3.sortBids(nashbids, NashBidDetails.nashComparator);
				phase3flag=true;}
			}

			bidToOffer = s3.getBid(rem);
			System.out.println("Don: In phase 3");

		}
			break;

		case 2: {
			if (!nashflag) {
				s2.computeNash();
				s2.sortBids(nashbids, NashBidDetails.nashComparator);
				nashflag = true;
			}
			bidToOffer = s2.getBid(time);
		}
			break;

		case 1: {
			bidToOffer = s1.getBid(time);
		}
			break;
		}

		if (bidToOffer != null) {
			action = new Offer(this.getPartyId(), bidToOffer);
		} else {
			System.out.println("Don: bidToOffer is null");
			bidToOffer = s1.getMaxUtilityBid();
			action = new Offer(this.getPartyId(), bidToOffer);
		}

		if (getUtility(bidToOffer) <= getUtility(lastReceivedOffer)) {

			if (backup != null
					&& getUtility(lastReceivedOffer) <= getUtility(backup)) {
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
	public java.util.HashMap<java.lang.String, java.lang.String> negotiationEnded(
			Bid acceptedBid) {
		
		if(!nashflag)
			{
			System.out.println("Don: Calculating nash");
			s3.computeNash();
		    s3.sortBids(nashbids, NashBidDetails.nashComparator);
		    }
		
		Bid nashBid = s3.getNashBids(1).get(0).getBid();

		System.out.println("Don: Our utility for Nash is "
				+ getUtility(nashBid));
		ms[0].printState(nashBid);
		ms[1].printState(nashBid);

		System.out.println("Don: There were " + finalRounds + " final rounds.");
		System.out.println("Don: There were " + nBackups
				+ " backups that have been offered " + nBackups + " times.");

		return null;
	}

	public int getNumModels() {
		int n;

		if (modelDomain.getSize() > 20000)
			n = 1;
		else if (modelDomain.getSize() > 10000)
			n = 2;
		else if (modelDomain.getSize() > 5000)
			n = 3;
		else if (modelDomain.getSize() > 1000)
			n = 4;
		else
			n = 5;
		return n;
	}

	public int getPhase(double time, int rem) {
		int phase = 0;
		double phase3at = 0.95;
		double phase4at = 0.99;
		double phase2at = 0.5;
		if (rem <= 1 && time > phase3at)
			phase = 5;
		else if ((time > phase4at || rem < 5) && backup != null)
			phase = 4;
		else if ((time > phase3at || rem < modelDomain.getSize()) && nashflag)
			phase = 3;
		else if (time > phase2at && ms[0].confident(100, 5)
				&& ms[1].confident(100, 5))
			phase = 2;
		else
			phase = 1;

		return phase;

	}

}
