package clean;
import negotiator.Bid;

import java.util.Comparator;

public class BidValueDetails implements Comparator<BidValueDetails>{
	private Bid b;
	private double utility;
	private double nash;
	
	
	public BidValueDetails(Bid b, double ut, double nash){
	this.b=b;
	utility=ut;
	this.nash=nash;
	}
	
	public Bid getBid(){
		return b;
		
	}
	
	public double getUt(){
		return utility;
	}
	
	public double getNash(){
		return nash;
	}
	
	@Override
	public int compare(BidValueDetails b1, BidValueDetails b2){
		return Double.compare(b1.nash,b2.nash);}
	
	public static Comparator<BidValueDetails> nashComparator= new Comparator<BidValueDetails>() {

		public int compare(BidValueDetails b1, BidValueDetails b2){
				return Double.compare(b1.nash,b2.nash);
	}};
	
	public static Comparator<BidValueDetails> utComparator= new Comparator<BidValueDetails>() {

		public int compare(BidValueDetails b1, BidValueDetails b2){
				return Double.compare(b1.utility,b2.utility);
	}};
			
	public static Comparator<BidValueDetails> weightedComparator= new Comparator<BidValueDetails>() {

		public int compare(BidValueDetails b1, BidValueDetails b2){
				return Double.compare(b1.utility*b1.nash,b2.utility*b2.nash);
	  }};
}