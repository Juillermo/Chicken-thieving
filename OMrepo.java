
import java.util.HashMap;
import java.util.Map;

import negotiator.boaframework.OpponentModel;
import negotiator.parties.NegotiationInfo;
import negotiator.timeline.TimeLineInfo;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.SessionData;


import negotiator.boaframework.opponentmodel.CUHKFrequencyModelV2;
import negotiator.boaframework.opponentmodel.SmithFrequencyModel;
import negotiator.boaframework.opponentmodel.AgentXFrequencyModel;

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
		OpponentModel[] models=new OpponentModel[3];
		Map<String, Double> p= new HashMap<String,Double>();
		String arg="l";
		Double val=new Double(0.2);
		p.put(arg, val);
		models[0]=new CUHKFrequencyModelV2();
		models[0].init(ns,p);
		models[1]=new SmithFrequencyModel();
		models[1].init(ns,p);
		models[2]=new AgentXFrequencyModel();
		models[2].init(ns,p);
		
		return models;
	}
	
	public OpponentModel[] getModels(int n){
		if(n>3)
			n=3;
		OpponentModel[] models=getModels();
		OpponentModel[] nmodels=new OpponentModel[n];
		for(int i=0;i<n;i++)
			nmodels[i]=models[i];
		return nmodels;
	}
}