import negotiator.utility.AbstractUtilitySpace;
import negotiator.Domain;


public class ModelDomain {

	long DomainSize;
	Domain d;
	AbstractUtilitySpace u;
	
	
	public ModelDomain(AbstractUtilitySpace u) {
		this.u=u;
	    d = u.getDomain();
		DomainSize=d.getNumberOfPossibleBids();
	}
	
	public long getSize(){
		return DomainSize;
	}
	

}
