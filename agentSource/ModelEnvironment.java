package clean;

import java.util.ArrayList;

import negotiator.AgentID;

public class ModelEnvironment{
	
	private ArrayList<AgentID> agents;
	private AgentID afterUs;
	private boolean chooseActionFlag;
	
	public ModelEnvironment(ArrayList<AgentID> agentsPlaceholder){
		this.agents = agentsPlaceholder;
		afterUs = null;
		chooseActionFlag=false;
	}

	public void updateMessageReceived(AgentID agent){
		if(!agents.contains(agent))
			agents.add(agent);
		if(afterUs == null && chooseActionFlag)
			afterUs = agent;
		
	}
	
	public void updateActionChosen(){
		chooseActionFlag=true;
	}
	
	public ArrayList<AgentID> getAgents() {
		return agents;
	}

	public void setAgents(ArrayList<AgentID> agents) {
		this.agents = agents;
	}

	public AgentID getAfterUs() {
		return afterUs;
	}

	public void setAfterUs(AgentID afterUs) {
		this.afterUs = afterUs;
	}
	
	public boolean isAfterUs(AgentID agent) {
		return agent.equals(afterUs);
	}
	
}