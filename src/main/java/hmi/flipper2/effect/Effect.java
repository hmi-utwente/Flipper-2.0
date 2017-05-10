package hmi.flipper2.effect;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public abstract class Effect {

	public double weight = -1;
	private double rr_from, rr_to; // random range bounds
	
	public abstract Object doIt(Is is) throws FlipperException;
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void setRandomRange(double rr_from, double rr_to) {
		this.rr_from = rr_from;
		this.rr_to = rr_to;
	}
	
	public boolean inRandomRange(double d) {
		return (d > rr_from) && (d < rr_to);
	}
	
}
