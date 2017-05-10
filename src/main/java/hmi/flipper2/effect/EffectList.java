package hmi.flipper2.effect;

import java.util.ArrayList;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class EffectList extends ArrayList<Effect> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean weighted;
	
	public EffectList() {
		this(false);
	}
	
	public EffectList(boolean weighted) {
		this.weighted = weighted;
	}
	
	public void doIt(Is is) throws FlipperException {
		if (weighted) {
			// pick one at a weighted random
			double total_weight = 0;
			for (Effect eff : this) {
				if (eff.weight < 0)
					throw new FlipperException("Effect has nog weight in weighted effect List: " + eff.toString());
				total_weight += eff.weight;
			}
			double high_bound = 0.0;
			for (Effect eff : this) {
				double n_weight = eff.weight / total_weight;
				eff.setRandomRange(high_bound, high_bound + n_weight);
				high_bound += n_weight;
			}
			double rand = Math.random();
			for (Effect eff : this) {
				if (eff.inRandomRange(rand)) {
					eff.doIt(is);
					return;
				}
			}
			throw new RuntimeException("UNEXPECTED");
		} else {
			for (Effect eff : this) {
				eff.doIt(is);
			}
		}
	}
}

