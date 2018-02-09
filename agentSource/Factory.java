package clean;

import java.util.ArrayList;
import java.util.HashMap;

import negotiator.AgentID;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

public class Factory {

	NegotiationInfo info;

	public Factory(NegotiationInfo info) {
		this.info = info;
	}

	
	public ModelDomain initModelDom(){
		ModelDomain domain = new ModelDomain(info.getUtilitySpace());
		return domain;

	}
	
	public ModelEnvironment initModelEnv(){
		ArrayList<AgentID> agentsPlaceholder = new ArrayList<AgentID>();
		ModelEnvironment environment = new ModelEnvironment(agentsPlaceholder);
		return environment;

	}
	
	public ModelHistory initModelHist(AbstractUtilitySpace utilitySpace){
		ModelHistory history = new ModelHistory(utilitySpace);
		return history;
	}
	
	public ModelRounds initModelRounds(){
		double[] store = new double[Parameters.timeStoreSize];
		ModelRounds roundModel = new ModelRounds(0, store);
		return roundModel;
	}
	
	public SmartBidSpace initBidSpace(ModelOpponents oppModels){
		SortedOutcomeSpace outcomeSpace = new SortedOutcomeSpace(
				info.getUtilitySpace());
		SmartBidSpace bidSpace = new SmartBidSpace(outcomeSpace, oppModels,
				info.getUtilitySpace());
		return bidSpace;
	}
	
	public SimpleBoulware initSimpleBoulware( SmartBidSpace bidSpace){
		SimpleBoulware boulware = new SimpleBoulware(Parameters.startingThresh,
				bidSpace, Parameters.Pmax, Parameters.Pmin, Parameters.e,
				info.getTimeline());
		return boulware;
	}
	
	public NashBoulware initNashBoulware( SmartBidSpace bidSpace, double startingThresh){
		SimpleBoulware nBoulware = new SimpleBoulware(startingThresh,
				bidSpace, Parameters.nPmax, Parameters.nPmin, Parameters.ne,
				info.getTimeline());
		NashBoulware nashBoulware = new NashBoulware(nBoulware, bidSpace);
		return nashBoulware;
	}
	
	public NashIterator initNashIterator(SmartBidSpace bidSpace, ModelDomain domain, ModelHistory history){
		NashIterator nashIterator = new NashIterator(bidSpace,
				domain.getSize(), history, info.getUtilitySpace());
		return nashIterator;
	}
	
	public BackupStrategy initBackupStrategy(ModelHistory history, BiddingStrategy fallback){
		BackupStrategy backupStrategy = new BackupStrategy(history,
				fallback);
		return backupStrategy;

	}
	
	public Phases initPhases(){
		Phases phases = new Phases(Parameters.phase2, Parameters.phase3A,
				Parameters.phase3, Parameters.phase4, Parameters.phase4R);
		return phases;
	}
	
	public PhasewiseStrategy initPhasewiseStrategy( ModelRounds roundModel, ModelDomain domain, ModelHistory history, ModelOpponents oppModels, SmartBidSpace bidSpace ){
		Phases phases = initPhases();
		SimpleBoulware boulware = initSimpleBoulware( bidSpace);
		NashBoulware nashBoulware = initNashBoulware( bidSpace, boulware.getLastThresh());
		NashIterator nashIterator = initNashIterator(bidSpace, domain, history);
		BackupStrategy backupStrategy = initBackupStrategy(history, nashIterator);
		BiddingStrategy[] strategies = new BiddingStrategy[]{boulware, nashBoulware, nashIterator, backupStrategy};
		PhasewiseStrategy phasewiseStrategy = new PhasewiseStrategy(phases,
				strategies, info.getTimeline(), roundModel, domain, history,
				oppModels);
		return phasewiseStrategy;
	}

	public Tracker initTracker() {
		ArrayList<Integer> trackArray = new ArrayList<Integer>();
		Tracker tracker = new Tracker(trackArray, Parameters.trackerWindow,
				Parameters.trackerOverlook);
		return tracker;
	}

	public Score initScore(Tracker tracker) {
		Score score = new Score(0, 0, tracker);
		return score;
	}

	public OMScorer initScorer(OpponentModel[] models) {
		HashMap<OpponentModel, Score> scores = new HashMap<OpponentModel, Score>();
		for (int i = 0; i < models.length; i++) {
			Tracker tracker = initTracker();
			Score score = initScore(tracker);
			scores.put(models[i], score);
		}
		OMScorer scorer = new OMScorer(scores);
		return scorer;
	}
	
	public OMrepo initRepo(){
		OMrepo repository = new OMrepo(info);
		return repository;
	}

	public OpponentModel[] initOppModels( ModelDomain domain) {
		int numOppModels = Parameters.getNumOfOppModels(domain.getSize());
		OMrepo repository = initRepo();
		OpponentModel[] models = repository.getModels(numOppModels);
		return models;
	}

	
	public ModelOpponents initModelOpp( ModelDomain domain, ModelEnvironment environment) {
		
		OpponentModel[] models = initOppModels( domain);
		ArrayList<AgentID> agents = environment.getAgents();
		HashMap<AgentID, OMScorer> scorers = new HashMap<AgentID, OMScorer>();
		for (int i = 0; i < agents.size(); i++) {
			scorers.put(agents.get(i), initScorer(models));
		}
		ModelOpponents oppModels = new ModelOpponents(scorers);
		return oppModels;
	}
	
	public LastRoundAcceptor initLastRoundAcceptor(ModelHistory history, ModelRounds rounds, TimeLineInfo timeline, AbstractUtilitySpace utilitySpace){
		LastRoundAcceptor lastRoundAcceptor = new LastRoundAcceptor(history, rounds, timeline, utilitySpace);
		return lastRoundAcceptor;
	}
}