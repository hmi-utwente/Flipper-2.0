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
package hmi.flipper2.dataflow;

import java.util.HashSet;
import java.util.Set;

public interface DataFlow {

	public Set<String> flowIn();
	
	public Set<String> flowOut();
	
	public static final String isp = "is.";
	public static final int isp_length = isp.length();
	
	public static Set<String> extractRefs(String js_expr) {
		Set<String> res = new HashSet<String>();

		int jsp_length = js_expr.length();
		int start = js_expr.indexOf(isp);
		while (start >= 0) {
			int p = start + isp_length;
			if (!(start > 0
					&& (js_expr.charAt(start - 1) == '.' || Character.isJavaIdentifierPart(js_expr.charAt(start - 1)))))
				while (true) {
					p++;
					if (p >= jsp_length) {
						res.add(js_expr.substring(start, p));
						break;
					} else {
						char c = js_expr.charAt(p);
						if (!(c == '.' || Character.isJavaIdentifierPart(c))) {
							res.add(js_expr.substring(start, p));
							break;
						}
					}
				}
			start = js_expr.indexOf(isp, p);
		}
		return res;
	}
	
	public static final Set<String> EMPTY = new HashSet<String>();
	
	public static Set<String> union(Set<String> l, Set<String> r) {
		if ( l == EMPTY )
			return r;
		else if ( r == EMPTY )
			return l;
		else {
			HashSet<String> res = new HashSet<String>();
			res.addAll(l);
			res.addAll(r);
			return res;
		}
	}
	
//	public static void main(String[] args) {
//		Set<String> res = extractRefs("is.a  \"xis.\" en is.y.v._z  is.x");
//		System.out.println("RES:" + res);
//	}
	
}
