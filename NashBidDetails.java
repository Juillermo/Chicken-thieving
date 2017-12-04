import negotiator.Bid;

import java.util.Comparator;

public class NashBidDetails implements Comparator<NashBidDetails> {
	private Bid b;
	double utility;
	double nash;

	public NashBidDetails(Bid b, double ut, double nash) {
		this.b = b;
		utility = ut;
		this.nash = nash;
	}

	public Bid getBid() {
		return b;
	}

	public double getUt() {
		return utility;
	}

	public double getNash() {
		return nash;
	}

	@Override
	public int compare(NashBidDetails b1, NashBidDetails b2) {
		return Double.compare(b1.nash, b2.nash);
	}

	public static Comparator<NashBidDetails> nashComparator = new Comparator<NashBidDetails>() {

		public int compare(NashBidDetails b1, NashBidDetails b2) {
			return Double.compare(b1.nash, b2.nash);
		}
	};

	public static Comparator<NashBidDetails> utComparator = new Comparator<NashBidDetails>() {

		public int compare(NashBidDetails b1, NashBidDetails b2) {
			return Double.compare(b1.utility, b2.utility);
		}
	};

	public static Comparator<NashBidDetails> weightedComparator = new Comparator<NashBidDetails>() {

		public int compare(NashBidDetails b1, NashBidDetails b2) {
			return Double.compare(b1.utility * b1.nash, b2.utility * b2.nash);
		}
	};
}