
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

public class OMrepo{
	OpponentModel[] models;
	NegotiationSession ns;
    SessionData s;
    TimeLineInfo timeline;
    NegotiationInfo ni;
	
	public OMrepo(NegotiationInfo info){
		
		ns = new NegotiationSession(s, info.getUtilitySpace(),info.getTimeline());
		s = new SessionData();
	  
		models = new OpponentModel[7];
		Map<String, Double> p= new HashMap<String,Double>();
		
		//models[0] = new UniformModel();
		models[0] = new PerfectModel();
		models[0].init(ns, null);
		
		models[1] = new UniformModel();
		models[1].init(ns, null);
		
		models[2] = new CUHKFrequencyModelV2();
		models[2].init(ns, null);
		
		models[3] = new TheFawkes_OM();
		models[3].init(ns, null);
		
		models[4] = new BayesianModel();
		p.put("m", 0.0);
		models[4].init(ns, p);
		
		models[5] = new OppositeModel();
		models[5].init(ns, null);
		
		models[6] = new HardHeadedFrequencyModel();
		p.put("l", 0.2);
		models[6].init(ns,p);
	}
	
	public OpponentModel[] getModels(){
		return models;
	}
}