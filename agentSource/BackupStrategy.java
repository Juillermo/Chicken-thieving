package clean;

import negotiator.Bid;

public class BackupStrategy extends BiddingStrategy{
	
	ModelHistory history;
	BiddingStrategy backupStrategy;
	
	public BackupStrategy(ModelHistory history, BiddingStrategy backupStrategy){
		this.history=history;
		this.backupStrategy = backupStrategy;
	}
	
	public Bid getBid() throws Exception{
		Bid bid = history.getBackupOffer();
		if(bid == null)
		{
			bid = backupStrategy.getBid();
		}
		return bid;
			
	}
}