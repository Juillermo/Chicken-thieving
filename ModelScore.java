

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.actions.Action;
import negotiator.parties.NegotiationInfo;
import negotiator.actions.Offer;
import negotiator.timeline.TimeLineInfo;
import negotiator.boaframework.OpponentModel;
import negotiator.BidIterator;
import negotiator.issue.*;
import negotiator.utility.*;

public class ModelScore{

	OpponentModel[] oms;
    Score[] score;
    TimeLineInfo timeline;
    AgentID agent;
    NegotiationInfo ni;
    AbstractUtilitySpace u;
    
    public ModelScore(NegotiationInfo info,OpponentModel[] models){
		
	    timeline = info.getTimeline();
		oms = models;
		score = new Score[models.length];
		for(int i=0;i<models.length;i++)
			score[i] = new Score();
		ni=info;
		u = ni.getUtilitySpace();
	}
	

	
	public void updateModels(Action act, TimeLineInfo timeline, Bid lastBidOffered) throws Exception{
        if (act instanceof Offer) { 
            Offer offer = (Offer) act;
            double time = timeline.getTime();
            Bid b = offer.getBid();
            scoreModels(b, lastBidOffered);
            for(int i=0;i<oms.length; i++) {
            	oms[i].updateModel(b, time);
//            	AbstractUtilitySpace oppUtilitySpace = oms[i].getOpponentUtilitySpace();
//            	AdditiveUtilitySpace additiveUtilitySpace = (AdditiveUtilitySpace) oppUtilitySpace;
//
//            	List<Issue> issues = additiveUtilitySpace.getDomain().getIssues();
//            	System.out.println("BOA: Model "+i+"---------------------------");
//            	for (Issue issue : issues) {
//            	    int issueNumber = issue.getNumber();
//            	    System.out.println("BOA: >>>>>>>>>>>>>>>>>>>>>> " + issue.getName() + " weight: " + additiveUtilitySpace.getWeight(issueNumber));
//
//            	    // Assuming that issues are discrete only
//            	    IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
//            	    EvaluatorDiscrete evaluatorDiscrete = (EvaluatorDiscrete) additiveUtilitySpace.getEvaluator(issueNumber);
//
//            	    for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()) {
//            	        System.out.println("BOA: "+valueDiscrete.getValue()+": "+evaluatorDiscrete.getValue(valueDiscrete)+" " +evaluatorDiscrete.getEvaluation(valueDiscrete));
//            	    }
//
//            	}
//            	//System.out.println("BOA: Weights of model "+i+": "+ Arrays.toString(oppUtility );getIssueWeights
//            	System.out.println("perro");

            }
            
        }
	
	}
	
	public boolean checkConsistency(int model, Bid oldBid, Bid newBid){
		double oldUtil = oms[model].getBidEvaluation(oldBid);
		double newUtil = oms[model].getBidEvaluation(newBid);
		
		//System.out.println("model "+model+" old "+oldUtil+" new "+newUtil);	  
		return (oldUtil <= newUtil)?true:false;		
	}
	
	public void scoreModels(Bid b, Bid bidOnTable){
		if(bidOnTable != null)
			for(int i=0; i<oms.length; i++){
				boolean con=checkConsistency(i, bidOnTable, b);
				score[i].update(con);
			}
		else
			System.out.println("bidOnTable is null!");

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
			System.out.print(" model "+i+" score "+score[i].score());
			if(score[i].score() > best)
			{
				best = score[i].score();
				bestOm = oms[i];
			}
			}
		return bestOm;
	}
	
	public int getBestOmId(){
		double best= 0;
		int bestid=-1;
		for (int i=0; i<oms.length; i++)
			{
			if(score[i].score()>best)
			{
				best = score[i].score();
				bestid = i;
			}
			}
		return bestid;
	}
	

	
	
}
