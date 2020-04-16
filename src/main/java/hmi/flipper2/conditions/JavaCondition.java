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
package hmi.flipper2.conditions;

import java.util.Set;

import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.effect.JavaEffect;

public class JavaCondition extends Condition {

	private JavaEffect booleanEffect;
	
	public JavaCondition(String id, JavaEffect booleanEffect) throws FlipperException {
		super(id);
		if ( booleanEffect.isAssign() )
			throw new FlipperException("JavaCondition:effect cannot be is_assign: "+booleanEffect);
		this.booleanEffect = booleanEffect;
		
	}
	
	@Override
	public boolean checkIt(Is is) throws FlipperException {
		Object b = booleanEffect.doIt(is);
		try {
			return ((Boolean)b).booleanValue();
		} catch (ClassCastException e) {
			throw new FlipperException("JavaCondition: condition must be boolean: "+booleanEffect.toString());
		}
		
	}
	
	public Set<String> flowIn() {
		return booleanEffect.flowIn();
	}

}