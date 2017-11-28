
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.parties.NegotiationInfo;
import negotiator.actions.Offer;
import negotiator.timeline.TimeLineInfo;
import negotiator.boaframework.OpponentModel;

public class ModelScore{

	OpponentModel[] oms;
    double[][] score;
    TimeLineInfo timeline;
    AgentID agents[];
    Bid onTable;
    int numOms;
    NegotiationInfo ni;
    
	public ModelScore(NegotiationInfo info,OpponentModel[] models){
		
	    timeline=info.getTimeline();
		agents=new AgentID[2];
		oms=models;
		numOms=models.length;
		onTable=null;
		score=new double[numOms][2];
		ni=info;
	}
	
	
	
	public int getAgentId(AgentID agent){
		int id=-1;
		if(agents[0]==null)
        	{agents[0]=agent;id=0;}
        else if(agents[1]==null)
        	{agents[1]=agent;id=1;}
        else
        	id=(agent.equals(agents[0]))? 0:1;
              
        return id;		
	}
	
	public void updateModels(AgentID agent, Action act, TimeLineInfo timeline){
		int id = getAgentId(agent);
				
        if (act instanceof Offer) { 
            Offer offer = (Offer) act;
            double time=timeline.getTime();
            Bid b = offer.getBid();
            scoreModels(b,onTable,id);
            onTable=b;
            for(int i=0;i<numOms;i++)
            	oms[i].updateModel(b, time);
            
            
        }
		
	}
	
	public int checkConsistency(int model, Bid oldBid, Bid newBid){
		double oldUtil=oms[model].getBidEvaluation(oldBid);
		double newUtil=oms[model].getBidEvaluation(newBid);
		System.out.println("model "+model+" old "+oldUtil+" new "+newUtil);	  
		return (oldUtil<=newUtil)?1:-1;		
	}
	
	public void scoreModels(Bid b, Bid ot, int agentId){
		if(ot!=null)
		for(int i=0;i<numOms;i++)
			{
			int con=checkConsistency(i,ot,b);
			System.out.println(con);
			score[i][agentId]=score[i][agentId]+con;
			System.out.println("model "+ i+" agent "+agentId+" score "+score[i][agentId]);
			}
		for(int mod=0;mod<2;mod++)
		{for(int ag=0;ag<2;ag++)
			System.out.print(score[mod][ag]+"  ");
		System.out.println("");}
	}
	
	public double getScore(int model, int agent){
		return score[model][agent];
	}
	
	public OpponentModel getBestOm(AgentID a){
		int agent=getAgentId(a);
		double best= 0;
		int bestid=-1;
		OpponentModel bestOm=null;
		for (int i=0;i<numOms;i++)
			{
			//System.out.println("model "+i+" agent "+agent+" score "+score[i][agent]);
			if(score[i][agent]>best)
			{
				best=score[i][agent];
				bestOm=oms[i];
				bestid=i;
			}
			}
		System.out.println("Picking model "+bestid+" for agent "+agent);
		return bestOm;
	}
	
	public Bid pickBest(List<Bid> bids){
		OpponentModel om0=getBestOm(agents[0]);
		OpponentModel om1=getBestOm(agents[1]);
		double maxNash=0;
		double nash=0;
		Bid b=null;
		Bid bestBid=null;
		double v0=1;
		double v1=1;
		double v2=1;
		
		for(int i=0;i<bids.size();i++){
			b=bids.get(i);
			if(om0!=null)
			    v0=om0.getBidEvaluation(b);
			if(om1!=null)
				v1=om1.getBidEvaluation(b);
			v2=ni.getUtilitySpace().getUtility(b);
			nash=v0*v1*v2 ;
			if(nash>maxNash){
				maxNash=nash;
				bestBid=b;
			}
		}
		return bestBid;
	}
}
