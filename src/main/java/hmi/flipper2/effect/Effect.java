package hmi.flipper2.effect;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public abstract class Effect {

	public String js_weight = null;
	public double computed_weight = -1;
	
	private double rr_from, rr_to; // random range bounds
	
	public String id;
	
	public abstract Object doIt(Is is) throws FlipperException;
	
	public Effect(String id) {	
		this.id = id;
	}
	
	public void setWeight(String js_weight) {
		this.js_weight = js_weight;
	}
	
	public void setRandomRange(double rr_from, double rr_to) {
		this.rr_from = rr_from;
		this.rr_to = rr_to;
		// System.out.println("SETRANDOMRANGE: " + rr_from + " - " + rr_to);
	}
	
	public boolean inRandomRange(double d) {
		return (d > rr_from) && (d < rr_to);
	}
	
}
