import negotiator.Bid;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.opponentmodel.nash.*;
import negotiator.issue.*;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.*;

import java.util.*;

public class WindowedFrequencyModelTryout extends OpponentModel {

    int windowSize = -1;
    List<Bid> window;

    List<List<String>> issuesAndValueNames;
    List<List<Double>> previousWeights;
    List<List<Double>> currentWeights;

    private IssueEvaluationList issueEvaluationList;

    List<Issue> issueList;

    int counter = 0;

    int roundcounter = 0;

    AdditiveUtilitySpace additiveUtilitySpace;


    public void init(NegotiationSession ns, Map<String,Double> p)//, List<Issue> issues)
    {
    	issueList = ns.getDomain().getIssues();

        window = new ArrayList<Bid>();
        previousWeights = new LinkedList<List<Double>>();
        currentWeights = new LinkedList<List<Double>>();
        this.windowSize = p.get("w").intValue();

        //issueList.get(j).toString() + currentBidToBeLookedAt.getValue(issueList.get(j).getNumber());

        additiveUtilitySpace = (AdditiveUtilitySpace) ns.getUtilitySpace();

        // ((AdditiveUtilitySpace) space).setWeight()

        this.issueEvaluationList = new IssueEvaluationList(issueList.size());

        issuesAndValueNames = new LinkedList<List<String>>();
        for (Issue issue : issueList) {
            int issueNumber = issue.getNumber();

            // Assuming that issues are discrete only
            IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
            EvaluatorDiscrete evaluatorDiscrete = (EvaluatorDiscrete) additiveUtilitySpace.getEvaluator(issueNumber);

            //Set the names in the issuesandvalue list
            List<String> tempList = new LinkedList<String>();
            List<Double> anotherTempList = new LinkedList<Double>();
            for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()) {
                tempList.add(issue.getName() + valueDiscrete.getValue());
                System.out.println(tempList.get(tempList.size() - 1));
                anotherTempList.add(new Double(1/issueDiscrete.getValues().size()));
                //   anotherTempList.add(new Double(1/issueDiscrete.getValues().size()));

            }
            issuesAndValueNames.add(tempList);
            currentWeights.add(anotherTempList);
            previousWeights.add(anotherTempList);



            //even newer shit/*
            /*AIssueEvaluation issueEvaluation = null;
            if (issue instanceof IssueDiscrete)
                issueEvaluation = new IssueEvaluationDiscrete((IssueDiscrete)issue);
            else {
                Range ourNonZeroUtilityRange;
                if (issue instanceof IssueInteger) {
                    IssueInteger issueI = (IssueInteger) issue;
                    ourNonZeroUtilityRange = new Range((double) issueI.getLowerBound(), (double) issueI.getUpperBound());
                    issueEvaluation = new IssueEvaluationInteger((IssueInteger) issue, ourNonZeroUtilityRange);
                } else {
                    if (!(issue instanceof IssueReal)) {
                        throw new UnsupportedOperationException("There is no implementation for that issueType.");
                    }

                    IssueReal issueR = (IssueReal) issue;
                    ourNonZeroUtilityRange = new Range(issueR.getLowerBound(), issueR.getUpperBound());
                    issueEvaluation = new IssueEvaluationReal((IssueReal) issue, ourNonZeroUtilityRange);
                }
            }/**/

        }


    }

    //to be called whenever a bid is received
    @Override
    public void updateModel(Bid bid, double v) {
        if (bid == null) return;

        if (counter < windowSize) {
            window.add(bid);
            counter++;
        } else {
            UpdateModel(v);
            window.clear();
            counter = 0;
        }
    }

    //to be called whenever a full window has been observed
    private void UpdateModel(double dTime) {


        //the previous list is what was the current list last round.
        for (int i = 0; i < currentWeights.size(); ++i) {
            for (int j = 0; j < currentWeights.get(i).size(); ++j) {
                previousWeights.get(i).set(j, currentWeights.get(i).get(j).doubleValue());
            }
        }

        boolean concession = true; //Assume the other agent is always conceding

        List<Issue> unchangedIssues;
        List<List<Double>> weightestimation = new LinkedList<List<Double>>();

        //for each negotiation issue...
        //calculate frequency distribution of issue values in previous window F', and compare to current window F
        //distribution of issue values. Use equation 3 (found below)

        currentWeights = CalculateNegotiationFrequencyValue(window);

        for(int i = 0; i< currentWeights.size(); ++i)
        {
            for(int j = 0; j< currentWeights.get(i).size(); ++j)
            {
                //Chi-squared test, null hypothisis is that Fi and Fi' are statistically equivalent.
                //chi = sum of ((all observed values - expected values)/expected values)
                double delta = currentWeights.get(i).get(j)-previousWeights.get(i).get(j);
                double chi = delta*delta*windowSize*windowSize/previousWeights.get(i).get(j);

                //System.out.println("Chi : " + chi);

                //Has distribution of issue values for i changed since last window? If so, update the weight.

                //the comments below talk about how it would work in a perfect world, where we could predict whether or not
                //the agent is conceding or not, but here I am just assuming it always concedes.
                if(chi > 0.05)
                {
                    UpdateIssueWeights(i,j,dTime, delta);
                }

                //If not changed, add issue i to set of issues e whose distribution did not change from previous to current

                //ELSE! Frequency distribution changed from past to current! In what direction? (Concession or upped utility?)
                // Still assuming important issues stay unchanged more often.
                // See what the issue has done in the whole negotiation (V), using equation 1. => calculate expected utility
                // for issue i in previous (line 12) and current (line 13) window.
                // Compare => Has conceded?

            }
        }
    }

    List<List<Double>> CalculateNegotiationFrequencyValue(List<Bid> window) {
        List<List<Double>> output = new LinkedList<List<Double>>();

        LinkedHashMap<String, Double> issueLinkedHashMap = new LinkedHashMap<String, Double>();
        Bid currentBidToBeLookedAt;
        //look at all bids in the window...
        for (int i = 0; i < windowSize; ++i) {
            currentBidToBeLookedAt = window.get(i);
            List<Issue> issueList = currentBidToBeLookedAt.getIssues();
            String key;
            //and all issues in the bid...
            for (int j = 0; j < issueList.size(); ++j) {
                key = issueList.get(j).toString() + currentBidToBeLookedAt.getValue(issueList.get(j).getNumber());
                //System.out.println(key);
                //if it has been found, add a frequency +1, if not add a new Pair<string,double> to issueLinkedHashMap
                Double value = new Double(1);
                if (issueLinkedHashMap.containsKey(key)) {
                    issueLinkedHashMap.put(key, issueLinkedHashMap.get(key) + 1);
                } else issueLinkedHashMap.putIfAbsent(key, value);
            }

        }
        //when everything has been counted, fill it in for formula (3) from the paper

        double handleableDouble = 0;
        for(int i =0 ; i< issuesAndValueNames.size();++i)
        {
            List<Double> tempOutputList = new LinkedList<Double>();
            int howManyEvaluatorsInThisIssue = issuesAndValueNames.get(i).size();
            for(int j =0; j< howManyEvaluatorsInThisIssue;++j)
            {
                handleableDouble = 0;
                String key = issuesAndValueNames.get(i).get(j);
                if(issueLinkedHashMap.containsKey(key))
                {
                    handleableDouble = issueLinkedHashMap.get(key).doubleValue() + 1;
                    handleableDouble = handleableDouble/(howManyEvaluatorsInThisIssue+windowSize);

                    //System.out.println(key + " : " +handleableDouble);
                }
                tempOutputList.add(handleableDouble);
            }
            output.add(tempOutputList);
        }
        System.out.println("End of round " + roundcounter);
        roundcounter++;

        int counter = 0;

        return output;
    }

    private void UpdateIssueWeights(int issueindex, int evaluatorindex, double dTime, double delta)
    {
        double beginvalue =0;
        double newvalue = 0;

        double deltaIssueWeight = delta*(1-dTime);
        newvalue = beginvalue + deltaIssueWeight;

        //this.issueEvaluationList.updateIssueEvaluation(evaluatorindex,newvalue);

        AIssueEvaluation issueEvaluation = new IssueEvaluationDiscrete((IssueDiscrete)issueList.get(issueindex));
        //System.out.println("issueEvaluation to string : " + issueEvaluation.toString());
        System.out.println("issueEvaluation.getIssueName() to string : " + issueEvaluation.getIssueName());

        EvaluatorDiscrete evaluatorDiscrete = (EvaluatorDiscrete) additiveUtilitySpace.getEvaluator(issueindex);

        evaluatorDiscrete.setWeight(newvalue);





        //TODO  : SET THE WEIGHT FOR THE EVALUATOR evaluatorindex IN ISSUE issueindex TO NEWVALUE
        // WHY IS THIS NOT AN EASY THING TO DOOOOOOOOOOOOOOOOOOOOOOOO









       // List<ValueDiscrete> vals = ((IssueDiscrete) issueList.get(issueindex)).getValues();//.getValue(issueList.get(j).getNumber());
        //System.out.println("value is " + vals.get(evaluatorindex).getValue());
        //ValueDiscrete newissuediscrete = vals.get(evaluatorindex);
        //vals.set(evaluatorindex, newissuediscrete);

        //issueList.set(issueindex, (Issue)vals);
       // vals.set(evaluatorindex,)
      //  ((IssueDiscrete) issueList.get(issueindex)).getValue(evaluatorindex) = newvalue;
       // System.out.println(evaluatorDiscrete.toString());
/*        IssueDiscrete issueDiscrete = (IssueDiscrete) issueList.get(issueindex);
        for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()) {
            System.out.println(valueDiscrete.getValue());
            System.out.println("Evaluation(getValue): " + evaluatorDiscrete.getValue(valueDiscrete));
            System.out.println("Evaluation(getEvaluation): " + evaluatorDiscrete.getEvaluation(valueDiscrete));
        }*/


        this.issueEvaluationList.updateIssueWeightMap();
        System.out.println("Setting "+ issuesAndValueNames.get(issueindex).get(evaluatorindex) + " from "
                + beginvalue + " to " + newvalue);
    }

    /*
    *    public double[] getIssueWeights() {
        double[] estimatedIssueWeights = new double[this.negotiationSession.getUtilitySpace().getDomain().getIssues().size()];
        int i = 0;

        for(Iterator var4 = this.negotiationSession.getUtilitySpace().getDomain().getIssues().iterator(); var4.hasNext(); ++i) {
            Issue issue = (Issue)var4.next();
            estimatedIssueWeights[i] = this.getWeight(issue);
        }

        return estimatedIssueWeights;
    }
    * */

    private void Useless(UtilitySpace space) {
        AdditiveUtilitySpace additiveUtilitySpace = (AdditiveUtilitySpace) space;

        for (Issue issue : issueList) {
            int issueNumber = issue.getNumber();
            System.out.println(">> " + issue.getName() + " weight: " + additiveUtilitySpace.getWeight(issueNumber));

            // Assuming that issues are discrete only
            IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
            EvaluatorDiscrete evaluatorDiscrete = (EvaluatorDiscrete) additiveUtilitySpace.getEvaluator(issueNumber);

            for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()) {
                System.out.println(valueDiscrete.getValue());
                System.out.println("Evaluation(getValue): " + evaluatorDiscrete.getValue(valueDiscrete));
                //  System.out.println("Evaluation(getEvaluation): " + evaluatorDiscrete.getEvaluation(valueDiscrete));
            }
        }
    }
}