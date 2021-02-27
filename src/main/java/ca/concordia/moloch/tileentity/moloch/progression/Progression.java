package ca.concordia.moloch.tileentity.moloch.progression;

import java.util.LinkedList;
import java.util.List;

import ca.concordia.moloch.tileentity.moloch.action.Action;
import ca.concordia.moloch.tileentity.moloch.desire.Desire;

public class Progression {
	private long id;
	private long start=0;
	private long end=0;
	private boolean active=false;
	private List<Desire> desires = new LinkedList<Desire>();
	private List<Action> rewards = new LinkedList<Action>();
	private List<Action> punishments = new LinkedList<Action>();
	public Progression(long id, long start, long end, boolean active, List<Desire> desires, List<Action> rewards,
			List<Action> punishments) {
		super();
		this.id = id;
		this.start = start;
		this.end = end;
		this.active = active;
		this.desires = desires;
		this.rewards = rewards;
		this.punishments = punishments;
	}
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public List<Desire> getDesires() {
		return desires;
	}
	public void setDesires(List<Desire> desires) {
		this.desires = desires;
	}
	public List<Action> getRewards() {
		return rewards;
	}
	public void setRewards(List<Action> rewards) {
		this.rewards = rewards;
	}
	public List<Action> getPunishments() {
		return punishments;
	}
	public void setPunishments(List<Action> punishments) {
		this.punishments = punishments;
	}
	public long getId() {
		return id;
	}	

	
	
}

