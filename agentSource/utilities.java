package clean;

public class utilities {

	public static misc.Range getRange(double max, double min, int indexOfBidNearMax, int indexOfBidNearMin) {
		misc.Range r;

		r = (indexOfBidNearMin > indexOfBidNearMax) ? new misc.Range(min, max) : new misc.Range(max, min);

		return r;
	}
}
