/*******************************************************************************
 * Copyright (C) 2017-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Flipper-2.0 Dialogue Control project.
 *
 * Flipper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flipper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flipper.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.flipper2.effect;

import java.util.ArrayList;
import java.util.Set;

import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.conditions.Condition;
import hmi.flipper2.dataflow.DataFlow;

public class EffectList extends ArrayList<Effect> implements DataFlow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean weighted = false;
	boolean initialized = false;
	boolean dynamic = false;
	
	public EffectList() {
		this(false);
	}
	
	public EffectList(boolean weighted) {
		this.weighted = weighted;
	}
	
	public EffectList(boolean weighted, boolean dynamic) {
		this.weighted = weighted;
		this.dynamic = dynamic;
	}
	
	public void doIt(Is is) throws FlipperException {
		if (weighted) {
			if ( this.dynamic || !this.initialized ) {
				// pick one at a weighted random
				double total_weight = 0;
				for (Effect eff : this) {
					if (eff.js_weight == null)
						throw new FlipperException("Effect has nog weight in weighted effect List: " + eff.toString());
					eff.computed_weight = is.numericExpression(eff.js_weight);
					total_weight += eff.computed_weight;
				}
				double high_bound = 0.0;
				for (Effect eff : this) {
					double n_weight = eff.computed_weight / total_weight;
					eff.setRandomRange(high_bound, high_bound + n_weight);
					high_bound += n_weight;
				}
				initialized = true;
			}
			double rand = Math.random();
			for (Effect eff : this) {
				if (eff.inRandomRange(rand)) {
					if ( Config.debugging && is.tc.dbg != null )
						is.tc.dbg.start_Effect(eff.id(), eff.toString());	
					eff.doIt(is);
					if ( Config.debugging && is.tc.dbg != null )
						is.tc.dbg.stop_Effect(eff.id(),null);	
					return;
				}
			}
			throw new RuntimeException("UNEXPECTED");
		} else {
			for (Effect eff : this) {
				if ( Config.debugging && is.tc.dbg != null )
					is.tc.dbg.start_Effect(eff.id(), eff.toString());	
				eff.doIt(is);
				if ( Config.debugging && is.tc.dbg != null )
					is.tc.dbg.stop_Effect(eff.id(),null);	
			}
		}
	}
	
	public Set<String> flowIn() {
		Set<String> res = DataFlow.EMPTY;
		for(Effect c: this)
			res = DataFlow.union(res, c.flowIn());
		return res;
	}
	
	public Set<String> flowOut() {
		Set<String> res = DataFlow.EMPTY;
		for(Effect c: this)
			res = DataFlow.union(res, c.flowOut());
		return res;
	}
	
}

