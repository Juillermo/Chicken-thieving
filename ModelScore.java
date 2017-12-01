
import java.util.ArrayList;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.bidding.BidDetails;
import negotiator.actions.Action;
import negotiator.parties.NegotiationInfo;
import negotiator.actions.Offer;
import negotiator.timeline.TimeLineInfo;
import negotiator.boaframework.OpponentModel;
import negotiator.BidIterator;

public class ModelScore{

	OpponentModel[] oms;
    Score[] score;
    TimeLineInfo timeline;
    AgentID agent;
    NegotiationInfo ni;
    AbstractUtilitySpace u;
    
    public ModelScore(NegotiationInfo info,OpponentModel[] models){
		
	    timeline=info.getTimeline();
		oms=models;
		score=new Score[models.length];
		for(int i=0;i<models.length;i++)
			score[i]=new Score();
		ni=info;
		u=ni.getUtilitySpace();
	}
	

	
	public void updateModels(Action act, TimeLineInfo timeline, Bid onTable){
		AdditiveUtilitySpace adu;
        if (act instanceof Offer) { 
            Offer offer = (Offer) act;
            double time=timeline.getTime();
            Bid b = offer.getBid();
            scoreModels(b,onTable);
            for(int i=0;i<oms.length;i++)
            	{oms[i].updateModel(b, time);
            	adu = (AdditiveUtilitySpace)oms[i].getOpponentUtilitySpace();
            		}
            
            
        }
		
	}
	
	public boolean checkConsistency(int model, Bid oldBid, Bid newBid){
		double oldUtil=oms[model].getBidEvaluation(oldBid);
		double newUtil=oms[model].getBidEvaluation(newBid);
		
		//System.out.println("model "+model+" old "+oldUtil+" new "+newUtil);	  
		return (oldUtil<=newUtil)?true:false;		
	}
	
	public void scoreModels(Bid b, Bid ot){
		if(ot!=null)
		for(int i=0;i<oms.length;i++)
			{
			boolean con=checkConsistency(i,ot,b);
			score[i].update(con);
			}
		else
			System.out.println("ot is null!");
		
	}
	
	public double getScore(int model){
		return score[model].score();
	}
	
	public boolean confident(int window, int overlook){
		int model=getBestOmId();
		if(model==-1)
			return false;
		return score[model].trackRec(window,overlook);
	}
	
	public OpponentModel getBestOm(){
		double best= 0;
		OpponentModel bestOm=null;
		for (int i=0;i<oms.length;i++)
			{
			
			if(score[i].score()>best)
			{
				best=score[i].score();
				bestOm=oms[i];
			}
			}
		return bestOm;
	}
	
	public int getBestOmId(){
		double best= 0;
		int bestid=-1;
		for (int i=0;i<oms.length;i++)
			{
			if(score[i].score()>best)
			{
				best=score[i].score();
				bestid=i;
			}
			}
		return bestid;
	}
	

	
	
}
