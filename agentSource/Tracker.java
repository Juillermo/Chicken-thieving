package clean;

import java.util.ArrayList;

public class Tracker {
	ArrayList<Integer> tracker;
	int window;
	int overlook;

	public Tracker(ArrayList<Integer> tracker, int window, int overlook) {

		this.tracker = tracker;
		this.window = window;
		this.overlook = overlook;

	}

	public void update(boolean result) {
		int addVal = result ? 1 : -1;
		tracker.add(new Integer(addVal));
		if (tracker.size() > window)
			tracker.remove(0);

	}

	public boolean isConfident() {
		if(tracker.size()<window){
			return false;
		}
		int neg = 0;
		for (int i = 1; i < window; i++) {
			neg = (tracker.get(i).intValue() == -1) ? neg + 1 : neg;
			if (neg > overlook)
				return false;
		}
		return true;
	}

}