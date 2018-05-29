package hmi.flipper2;

public class FlipperDebugger {

		private static final boolean debugJS = false;
		private static final boolean debugIS = false;
		
		private TemplateController tc;	
		
		FlipperDebugger(TemplateController tc) {	
			this.tc = tc;
		}
		
		public void setTc(TemplateController tc) {
		}

		public void js_execute(String script) {
			if ( debugJS )
				System.out.println("JS: "+script);
		}
		
		public void js_result(String result) {
			if ( debugJS )
				System.out.println("JS-RESULT: "+result);
		}
		
		public void js_error(String error) {
			if ( debugJS )
				System.out.println("JS-ERROR: "+error);
		}
		
		public void precondition(String id, String descr, boolean v) {
			System.out.println("PRECONDITION{"+id+"}="+v+":"+descr);
		}
		
		public void effect(String id, String descr) {
			System.out.println("EFFECT{"+id+"}"+":"+descr);
			if ( debugIS ) {
				try {
					System.out.println("IS: "+this.tc.getIs("is"));
				}
				catch (FlipperException fe) {
				}
			}
		}
		
		public void log(String id, String event, String v) {
			System.out.println("#"+id + ": " +event+"\t"+v);
		}
}
