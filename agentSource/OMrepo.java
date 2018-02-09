package clean;

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

public class OMrepo {

	NegotiationSession ns;
	SessionData s;
	TimeLineInfo timeline;
	NegotiationInfo ni;

	public OMrepo(NegotiationInfo info) {

		ns = new NegotiationSession(s, info.getUtilitySpace(), info.getTimeline());
		s = new SessionData();
	}

	public OpponentModel[] getModels() {
		OpponentModel[] models = { new AgentXFrequencyModel(), new CUHKFrequencyModelV2(), new AgentLGModel(),
				new OpponentModelWindowed(), new NashFrequencyModel(), new SmithFrequencyModel(),
				new SmithFrequencyModelV2(), };

		for (int i = 0; i < models.length; i++) {
			Map<String, Double> p = new HashMap<String, Double>();
			if (models[i] instanceof IAMhagglerBayesianModel) {
				p.put("u", 0.0);
				p.put("b", 1.0);
			} else if (models[i] instanceof HardHeadedFrequencyModel) {
				p.put("l", 0.2);
			}  else if (models[i] instanceof BayesianModel) {
				p.put("m", 0.0);
			} else {
				p = null;
			}
			models[i].init(ns, p);
		}

		return models;

	}

	public OpponentModel[] getModels(int numOfModels) {

		OpponentModel[] models = getModels();
		if (numOfModels > models.length)
			numOfModels = models.length;
		OpponentModel[] selectedModels = new OpponentModel[numOfModels];

		for (int i = 0; i < numOfModels; i++)
			selectedModels[i] = models[i];

		return selectedModels;
	}
}