
import java.util.HashMap;
import java.util.Map;

import negotiator.boaframework.OpponentModel;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.SessionData;

import negotiator.boaframework.opponentmodel.CUHKFrequencyModelV2;
import negotiator.boaframework.opponentmodel.TheFawkes_OM;
import negotiator.boaframework.opponentmodel.DefaultModel;
import negotiator.boaframework.opponentmodel.UniformModel;
import negotiator.boaframework.opponentmodel.BayesianModel;
import negotiator.boaframework.opponentmodel.HardHeadedFrequencyModel;
import negotiator.boaframework.opponentmodel.PerfectModel;
import negotiator.boaframework.opponentmodel.OppositeModel;
import negotiator.boaframework.opponentmodel.SmithFrequencyModel;
import negotiator.boaframework.opponentmodel.SmithFrequencyModelV2;
import negotiator.boaframework.opponentmodel.AgentLGModel;
import negotiator.boaframework.opponentmodel.IAMhagglerBayesianModel;
import negotiator.boaframework.opponentmodel.InoxAgent_OM;
import negotiator.boaframework.opponentmodel.FSEGABayesianModel;
import negotiator.boaframework.opponentmodel.PerfectIAMhagglerBayesianModel;
import negotiator.boaframework.opponentmodel.ScalableBayesianModel;
import negotiator.boaframework.NoModel;
import negotiator.boaframework.opponentmodel.NashFrequencyModel;
import negotiator.boaframework.opponentmodel.WorstModel;
import negotiator.boaframework.opponentmodel.AgentXFrequencyModel;
import negotiator.boaframework.opponentmodel.PerfectScalableBayesianModel;

public class OMrepo{
	OpponentModel[] models;
	NegotiationSession ns;
    SessionData s;
    TimeLineInfo timeline;
    NegotiationInfo ni;
	
	public OMrepo(NegotiationInfo info){
		
		ns = new NegotiationSession(s, info.getUtilitySpace(),info.getTimeline());
		s = new SessionData();
	  
		models = new OpponentModel[6];
		Map<String, Double> p= new HashMap<String,Double>();
		
		models[0] = new PerfectModel();
		models[0].init(ns, null);
		
		models[1] = new DefaultModel();
		models[1].init(ns, null);
		
		models[2] = new InoxAgent_OM();
		models[2].init(ns, null);
		
		models[3] = new TheFawkes_OM();
		models[3].init(ns, null);
		
		models[4] = new NoModel();
		models[4].init(ns, null);
		
		models[5] = new HardHeadedFrequencyModel();
		models[5].init(ns,p);
	}
	
	public OpponentModel[] getModels(){
		return models;
	}
}