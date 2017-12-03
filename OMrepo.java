
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
	  
		OpponentModel[] models_v = {
				new PerfectModel(),
				new DefaultModel(),
				new InoxAgent_OM(),
				new TheFawkes_OM(),
				new NoModel(),
				new HardHeadedFrequencyModel(),
				new CUHKFrequencyModelV2(),
				new UniformModel(),
				new BayesianModel(),
				new OppositeModel(),
				new SmithFrequencyModel(),
				new SmithFrequencyModelV2(),
				new AgentLGModel(),
				new IAMhagglerBayesianModel(),
				new FSEGABayesianModel(),
				new PerfectIAMhagglerBayesianModel(),
				new ScalableBayesianModel(),
				new NashFrequencyModel(),
				new WorstModel(),
				new AgentXFrequencyModel(),
				new PerfectScalableBayesianModel(),
			};
		
		//Map<String, Double> p= new HashMap<String,Double>();

		for(int i=0; i<models_v.length; i++)
			models_v[i].init(ns, null);
		
		models = models_v;
	}
	
	public OpponentModel[] getModels(){
		return models;
	}
}