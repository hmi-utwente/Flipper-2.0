package hmi.flipper2.conditions;

import java.util.ArrayList;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class ConditionList extends ArrayList<Condition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean andMode;
	
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
	
	public boolean checkIt(Is is) throws FlipperException {
		if ( andMode ) {
			for(Condition c: this)
				if ( !c.checkIt(is) )
					return false;
			return true;
		} else {
			for(Condition c: this)
				if ( c.checkIt(is) )
					return true;
			return false;
		}
	}
	
}
	