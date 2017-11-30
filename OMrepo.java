
import java.util.HashMap;
import java.util.Map;

import negotiator.boaframework.OpponentModel;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.SessionData;


import negotiator.boaframework.opponentmodel.CUHKFrequencyModelV2;
import negotiator.boaframework.opponentmodel.TheFawkes_OM;

public class OMrepo{
	
	NegotiationSession ns;
    SessionData s;
    TimeLineInfo timeline;
    NegotiationInfo ni;
	
	public OMrepo(NegotiationInfo info){
		
		ns=new NegotiationSession(s, info.getUtilitySpace(),info.getTimeline());
		s=new SessionData();
	  
		
	}
	
	public OpponentModel[] getModels(){
		OpponentModel[] models=new OpponentModel[2];
		Map<String, Double> p= new HashMap<String,Double>();
		String arg="l";
		Double val=new Double(0.2);
		p.put(arg, val);
		models[0]=new CUHKFrequencyModelV2();
		models[0].init(ns,p);
		models[1]=new TheFawkes_OM();
		models[1].init(ns,p);
		return models;
	}
}