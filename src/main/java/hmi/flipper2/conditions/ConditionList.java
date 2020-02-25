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

import java.util.ArrayList;
import java.util.Set;

import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.dataflow.DataFlow;

public class ConditionList extends ArrayList<Condition> implements DataFlow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean andMode;
	
	public boolean lastCheck;
	
	public ConditionList(String mode) throws FlipperException {
		if (mode == null) 
			andMode = true;
		else if (mode.equals("and"))
			andMode = true;
		else if (mode.equals("or"))
			andMode = false;
		else
			throw new FlipperException("ConditionList: unknown mode: "+mode);
	}
	
	private final boolean mark(boolean v) {
		this.lastCheck = v;
		return v;
	}
	
	private static final boolean checkOne(Condition c, Is is) throws FlipperException {
		boolean res;
		
		if ( Config.debugging && is.tc.dbg != null )
			is.tc.dbg.start_Precondition(c.id(), c.toString());
		res = c.checkIt(is);
		if ( Config.debugging && is.tc.dbg != null )
			is.tc.dbg.stop_Precondition(c.id(), res+"");
		return res;
	}
	
	public boolean checkIt(Is is) throws FlipperException {
		if ( andMode ) {
			for(Condition c: this)
				if ( !checkOne(c, is) )
					return mark(false);
			return mark(true);
		} else {
			for(Condition c: this)
				if ( checkOne(c, is) )
					return mark(true);
			return mark(false);
		}
	}
	
	public Set<String> flowIn() {
		Set<String> res = DataFlow.EMPTY;
		for(Condition c: this)
			res = DataFlow.union(res, c.flowIn());
		return res;
	}
	
	public Set<String> flowOut() {
		Set<String> res = DataFlow.EMPTY;
		for(Condition c: this)
			res = DataFlow.union(res, c.flowOut());
		return res;
	}
	
}
	