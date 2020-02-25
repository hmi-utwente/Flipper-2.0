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

import hmi.flipper2.FlipperException;
import hmi.flipper2.FlipperObject;
import hmi.flipper2.Is;

public abstract class Effect extends FlipperObject {

	public String js_weight = null;
	public double computed_weight = -1;
	
	private double rr_from, rr_to; // random range bounds
	
	public abstract Object doIt(Is is) throws FlipperException;
	
	public Effect(String id) {	
		super(id);
	}
	
	public void setWeight(String js_weight) {
		this.js_weight = js_weight;
	}
	
	public void setRandomRange(double rr_from, double rr_to) {
		this.rr_from = rr_from;
		this.rr_to = rr_to;
	}
	
	public boolean inRandomRange(double d) {
		return (d > rr_from) && (d < rr_to);
	}
	
}
