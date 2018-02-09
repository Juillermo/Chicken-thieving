package clean;


import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

public class AgentDon extends AbstractNegotiationParty {

	private final String description = "Makes you an offer you can't refuse.";
	Parameters params;
	ModelRounds rounds;
	ModelEnvironment environment;
	ModelHistory history;
	ModelDomain domain;
	ModelOpponents oppModels;
	BiddingStrategy biddingStrategy;
	AcceptStrategy acceptStrategy;
	SmartBidSpace bidSpace;
	Factory factory;
	NegotiationInfo info;
	
	public void init(NegotiationInfo info) {
		this.info = info;
		super.init(info);
		initFirst(info);
		}
	
	public void initFirst(NegotiationInfo info){
		this.factory = new Factory(info);
		this.rounds = factory.initModelRounds();
		this.domain = factory.initModelDom();
		this.environment = factory.initModelEnv();
		this.history = factory.initModelHist(info.getUtilitySpace());
		this.acceptStrategy = factory.initLastRoundAcceptor(history, rounds, timeline, utilitySpace);
		this.oppModels = null;
		this.biddingStrategy = null;
		}
	
	public void initRest(){
		this.oppModels = factory.initModelOpp( domain, environment);
		this.bidSpace = factory.initBidSpace(oppModels);
		this.biddingStrategy = factory.initPhasewiseStrategy( rounds, domain, history, oppModels, bidSpace);
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void receiveMessage(AgentID sender, Action act) {
		super.receiveMessage(sender, act);
		if(oppModels == null && environment.getAgents().size()>=2){
				initRest();				
			}
		if(sender!=null){
			updateAllMessageReceived(sender,act);		
		}
		
	};

	@Override
	public Action chooseAction(List<Class<? extends Action>> classes) {
		Bid bid;
		Action act;
		try {
			bid = (biddingStrategy == null)?utilitySpace.getMaxUtilityBid():biddingStrategy.getBid();
			boolean isAcceptable = acceptStrategy.isAcceptable(bid);
			if(isAcceptable){
				act = new Accept(this.getPartyId(), history.getLastReceivedOffer());
			}
			else{
				act = new Offer(this.getPartyId(), bid);
			}
			updateAllActionChosen(act);
		} catch (Exception e) {
			act = new Accept(this.getPartyId(), history.getLastReceivedOffer()); 
			e.printStackTrace();
		}
		
		return act;
		}
		

	public void updateAllMessageReceived(AgentID sender, Action act){
		environment.updateMessageReceived(sender);
		history.updateMessageReceived(act, sender, environment.getAfterUs());
		if(oppModels != null){
			oppModels.updateMessageReceived(sender, act, getTimeLine().getTime(), history.getOfferOnTable());
		}
		}
	
	public void updateAllActionChosen(Action act){
		history.updateActionChosen(act);
		rounds.updateActionChosen(getTimeLine().getTime());
	}

}
