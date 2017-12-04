
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

public class ModelScore {

	OpponentModel[] oms;
	Score[] score;
	NegotiationInfo ni;
	int modelChanges;
	OpponentModel lastBestOm;

	public ModelScore(NegotiationInfo info, OpponentModel[] models) {

		oms = models;
		score = new Score[models.length];
		for (int i = 0; i < models.length; i++)
			score[i] = new Score();
		ni = info;

		modelChanges = 0;
		lastBestOm = oms[0];
	}

	public void updateModels(Action act, TimeLineInfo timeline, Bid bidOnTable) throws Exception {
		if (act instanceof Offer) {
			Offer offer = (Offer) act;
			double time = timeline.getTime();
			Bid bidOffered = offer.getBid();

			scoreModels(bidOffered, bidOnTable);
			for (int i = 0; i < oms.length; i++) {
				oms[i].updateModel(bidOffered, time);
			}
		}

	}

	public boolean checkConsistency(int model, Bid oldBid, Bid newBid) {
		double oldUtil = oms[model].getBidEvaluation(oldBid);
		double newUtil = oms[model].getBidEvaluation(newBid);

		// System.out.println("model "+model+" old "+oldUtil+" new "+newUtil);
		return (oldUtil <= newUtil) ? true : false;
	}

	public void scoreModels(Bid bidOffered, Bid bidOnTable) {
		if (bidOnTable != null)
			for (int i = 0; i < oms.length; i++) {
				boolean con = checkConsistency(i, bidOnTable, bidOffered);
				score[i].update(con);
			}
		else
			System.out.println("bidOnTable is null!");

	}

	public double getScore(int model) {
		return score[model].score();
	}

	public boolean confident(int window, int overlook) {
		int model = getBestOmId();
		if (model == -1)
			return false;
		return score[model].trackRec(window, overlook);
	}

	public OpponentModel getBestOm() {
		double best = -1;
		OpponentModel bestOm = null;
		for (int i = 0; i < oms.length; i++) {
			// System.out.println("Model " + i + " score " + score[i].score());
			if (score[i].score() > best) {
				best = score[i].score();
				bestOm = oms[i];
			}
		}
		if (bestOm != lastBestOm)
			modelChanges++;

		return bestOm;
	}

	public int getBestOmId() {
		double best = -1;
		int bestid = -1;
		for (int i = 0; i < oms.length; i++) {
			if (score[i].score() > best) {
				best = score[i].score();
				bestid = i;
			}
		}
		return bestid;
	}

	public void printState(Bid bid) {
		double best = -1;
		OpponentModel bestOm = null;
		System.out.println("------------------------------------");
		for (int i = 0; i < oms.length; i++) {
			System.out.println("Score " + score[i].score() + " model " + oms[i].getName() + " utility of opponent "
					+ oms[i].getBidEvaluation(bid));
			if (score[i].score() > best) {
				best = score[i].score();
				bestOm = oms[i];
			}

		}
		System.out.println("Best model is " + bestOm.getName() + ", after " + modelChanges + " changes.");
	}

}
