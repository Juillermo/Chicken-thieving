//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.nash.AIssueEvaluation;
import negotiator.boaframework.opponentmodel.nash.IssueEvaluationDiscrete;
import negotiator.boaframework.opponentmodel.nash.IssueEvaluationInteger;
import negotiator.boaframework.opponentmodel.nash.IssueEvaluationList;
import negotiator.boaframework.opponentmodel.nash.IssueEvaluationReal;
import negotiator.boaframework.opponentmodel.nash.Range;
import negotiator.boaframework.opponentmodel.tools.UtilitySpaceAdapter;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.utility.AdditiveUtilitySpace;

public class WindowedOpponentModel extends OpponentModel {
    private IssueEvaluationList issueEvaluationList;

    List<Issue> issues;
    List<Bid> window;
    int windowSize = -1;
    int counter = 0;

    public WindowedOpponentModel() {
    }

    public void init(NegotiationSession ns, Map<String,Double> p) {
        issues = ns.getIssues();
        this.initModel();
        windowSize = p.get("w").intValue();
        counter = 0;
        window = new ArrayList<Bid>();
    }

    private void initModel() {

        this.issueEvaluationList = new IssueEvaluationList(issues.size());

        for(int index = 0; index < issues.size(); ++index) {
            Issue issue = (Issue)issues.get(index);
            AIssueEvaluation issueEvaluation = null;
            if (issue instanceof IssueDiscrete) {
                issueEvaluation = new IssueEvaluationDiscrete((IssueDiscrete)issue);
            } else {
                Range ourNonZeroUtilityRange;
                if (issue instanceof IssueInteger) {
                    IssueInteger issueI = (IssueInteger)issue;
                    ourNonZeroUtilityRange = new Range((double)issueI.getLowerBound(), (double)issueI.getUpperBound());
                    issueEvaluation = new IssueEvaluationInteger((IssueInteger)issue, ourNonZeroUtilityRange);
                } else {
                    if (!(issue instanceof IssueReal)) {
                        throw new UnsupportedOperationException("There is no implementation for that issueType.");
                    }

                    IssueReal issueR = (IssueReal)issue;
                    ourNonZeroUtilityRange = new Range(issueR.getLowerBound(), issueR.getUpperBound());
                    issueEvaluation = new IssueEvaluationReal((IssueReal)issue, ourNonZeroUtilityRange);
                }
            }

            this.issueEvaluationList.addIssueEvaluation((AIssueEvaluation)issueEvaluation);
        }

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
            UpdateModel();
            window.clear();
            counter = 0;
        }

    }

    private void UpdateModel()
    {
        //update the shit out of this
        for(int i = 0; i< window.size(); ++i)
        {
            Bid bid = window.get(i);
            if (bid != null) {
                Iterator var6 = issues.iterator();

                while (var6.hasNext()) {
                    Issue issue = (Issue) var6.next();

                    try {
                        int issueID = issue.getNumber();
                        Value offeredValue = bid.getValue(issueID);
                        this.issueEvaluationList.updateIssueEvaluation(issueID, offeredValue);
                    } catch (Exception var9) {
                        var9.printStackTrace();
                    }
                }

                this.issueEvaluationList.updateIssueWeightMap();
            }
        }
    }

    public double getBidEvaluation(Bid bid) {
        double result = 0.0D;
        if (this.issueEvaluationList.isReady()) {
            double totalEstimatedUtility = 0.0D;
            Iterator var8 = issues.iterator();

            while(var8.hasNext()) {
                Issue issue = (Issue)var8.next();

                try {
                    int issueID = issue.getNumber();
                    double issueWeight = this.issueEvaluationList.getNormalizedIssueWeight(issueID);
                    Value offeredValue = bid.getValue(issueID);
                    AIssueEvaluation issueEvaluation = this.issueEvaluationList.getIssueEvaluation(issueID);
                    double offeredValueWeight = issueEvaluation.getNormalizedValueWeight(offeredValue);
                    totalEstimatedUtility += issueWeight * offeredValueWeight;
                } catch (Exception var16) {
                    var16.printStackTrace();
                }
            }

            result = Math.min(1.0D, Math.max(0.0D, totalEstimatedUtility));
        }

        return result;
    }

    public String toString() {
        return this.issueEvaluationList.toString();
    }

    public double getWeight(Issue issue) {
        return this.issueEvaluationList.getNormalizedIssueWeight(issue.getNumber());
    }

    public String getName() {
        return "Windowed Frequency Model";
    }

    public AdditiveUtilitySpace getOpponentUtilitySpace() {
        if (this.issueEvaluationList.isReady()) {
            return new UtilitySpaceAdapter(this, this.negotiationSession.getUtilitySpace().getDomain());
        } else {
            System.out.println("Returned own utilityspace to avoid an error (normal on first turn).");
            return (AdditiveUtilitySpace)this.negotiationSession.getUtilitySpace();
        }
    }
}
