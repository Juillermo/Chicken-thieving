package clean;


import java.util.HashMap;
import negotiator.Bid;
import negotiator.boaframework.OpponentModel;

public class OMScorer {

	HashMap<OpponentModel, Score> scores;

	public OMScorer(HashMap<OpponentModel, Score> scores) {

		this.scores = scores;
	}

	public boolean checkConsistency(OpponentModel model, Bid oldBid, Bid newBid) {
		double oldUtil = model.getBidEvaluation(oldBid);
		double newUtil = model.getBidEvaluation(newBid);
		return (oldUtil <= newUtil) ? true : false;
	}

	public void scoreModels(Bid bidOffered, Bid bidOnTable) {

		for (OpponentModel model : scores.keySet()) {
			boolean consistency = checkConsistency(model, bidOnTable,
					bidOffered);
			scores.get(model).update(consistency);
		}

	}

	public double getScore(OpponentModel model) {
		return scores.get(model).getScore();
	}

	public boolean isConfident() {
		OpponentModel model = getBestOm();
		if (model == null)
			return false;
		return scores.get(model).isConfident();
	}

	public OpponentModel getBestOm() {
		OpponentModel bestOm = null;
		double bestScore = -Integer.MAX_VALUE;
		for (OpponentModel model : scores.keySet()) {
			if (getScore(model) > bestScore) {
				bestScore = getScore(model);
				bestOm = model;
			}

		}
		return bestOm;
	}
	
	public void updateModels(Bid bid, double time){
		for (OpponentModel model : scores.keySet()){
			model.updateModel(bid, time);
		}
	}

	public void printState(Bid bid) {
		double bestScore = -1;
		OpponentModel bestOm = null;
		System.out.println("------------------------------------");
		for (OpponentModel model : scores.keySet()) {
			System.out.println("Score " + getScore(model)
					+ " model " + model.getName() + " utility of opponent "
					+ model.getBidEvaluation(bid));
			if (getScore(model) > bestScore) {
				bestScore = getScore(model);
				bestOm = model;
			}

		}
		System.out.println("Best model is " + bestOm.getName());
	}

}
