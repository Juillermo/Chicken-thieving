//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.List;

import negotiator.Bid;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.NashFrequencyModel;
import negotiator.boaframework.opponentmodel.nash.IssueEvaluationList;
import negotiator.issue.Issue;
import negotiator.utility.AdditiveUtilitySpace;

public class OpponentModelWindowed {
    private IssueEvaluationList issueEvaluationList;

    List<Issue> issues;
    List<Bid> window;
    int windowSize = -1;
    int counter = 0;
    OpponentModel oppModel;

    public OpponentModelWindowed() {
    }

    public void init(OpponentModel model, int windowsize)
    {
        oppModel =  model;
    }

    public void updateModel(Bid bid, double time) {
        if(bid == null) return;

        if(counter < windowSize)
        {
            window.add(bid);
            counter++;
        }
        else
        {
            UpdateModel(time);
            window.clear();
            counter = 0;
        }

    }

    private void UpdateModel(double time)
    {
        //update the model for all the bids in the window
        for(int i = 0; i< window.size(); ++i)
        {
            Bid bid = window.get(i);
            if (bid != null) {
                oppModel.updateModel(bid, time);
            }
        }
    }

    public double getBidEvaluation(Bid bid) {
        return oppModel.getBidEvaluation(bid);
    }

    public String toString() {
        return oppModel.toString();
    }

    public double getWeight(Issue issue) {
        return oppModel.getWeight(issue);
    }

    public String getName() {
        return "Windowed Model of " + oppModel.getName();
    }

    public AdditiveUtilitySpace getOpponentUtilitySpace()
    {
        return (AdditiveUtilitySpace)oppModel.getOpponentUtilitySpace();
    }
}
