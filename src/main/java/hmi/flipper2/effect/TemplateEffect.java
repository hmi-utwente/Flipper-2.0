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

import java.util.Set;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.dataflow.DataFlow;

import static hmi.flipper2.TemplateController.logger;

public class TemplateEffect extends Effect {

	private String regexpr;
	private String isregexpr;
	public TemplateEffect(String id, String regexpr, String isregexpr) throws FlipperException {
		super(id);
		if ( regexpr != null && isregexpr != null )
			throw new FlipperException("TemplateEffect cannot have both regexpr and isregexpr");
		this.regexpr	= regexpr;
		this.isregexpr	= isregexpr;
		logger.debug("regexpr="+this.regexpr);
		logger.debug("isregexpr="+this.isregexpr);
	}
	
	@Override
	public Object doIt(Is is) throws FlipperException {
		if ( this.isregexpr != null ) {
			this.regexpr = is.getIs(this.isregexpr);
			if ( this.regexpr == null )
				throw new FlipperException("checktemplates: isregexpr not found: "+this.isregexpr);
			else 
				this.regexpr = this.regexpr.substring(1, this.regexpr.length()-1); // strip quotes
		}
		is.tc.checkConditionalTemplates(this.regexpr);
		return null;
	}
	
	public Set<String> flowIn() {
		return DataFlow.EMPTY;
	}
	
	public Set<String> flowOut() {
		return DataFlow.EMPTY;
	}

}
