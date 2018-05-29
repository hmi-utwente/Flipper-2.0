package hmi.flipper2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FlipperDebugger {

		enum Channel { JS, CE, LOG };
		
		private boolean debugJS = false;
		private boolean debugIS = false;
		private boolean singleStep = true;
		
		private TemplateController tc;	
		
		FlipperDebugger(TemplateController tc) {	
			this.tc = tc;
		}
		
		public void js_execute(String script) {
			if ( debugJS )
				out(Channel.JS, "JS: "+script);
		}
		
		public void js_result(String result) {
			if ( debugJS )
				out(Channel.JS, "JS-RESULT: "+result);
		}
		
		public void js_error(String error) {
			if ( debugJS )
				out(Channel.JS, "JS-ERROR: "+error);
		}
		
		public void precondition(String id, String descr, boolean v) {
			out(Channel.CE, "PRECONDITION{"+id+"}="+v+":"+descr);
		}
		
		public void effect(String id, String descr) {
			out(Channel.CE, "EFFECT{"+id+"}"+":"+descr);
			if ( debugIS ) {
				try {
					System.out.println("IS: "+this.tc.getIs("is"));
				}
				catch (FlipperException fe) {
				}
			}
		}
		
		public void log(String id, String event, String v) {
			out(Channel.LOG, "#"+id + ": " +event+"\t"+v);
		}
		
		int stepsToDo = 0;
		
		private boolean handle_step(String command) {
			if ( command.length() == 0 ) 
				return true;
			if ( command.equals("cont") || command.equals("continue") ) {
				this.singleStep = false;
				return true;
			}
			if ( command.equals("stop") || command.equals("exit")) {
				System.exit(0);
			}
			if ( command.equals("is") ) {
				try {
					System.out.println("IS: "+this.tc.getIs("is"));
				}
				catch (FlipperException fe) {
				}
				return false;
			}
			if ( command.startsWith("do ") ) {
				this.stepsToDo = Integer.parseInt(command.substring(3));
				return true;
			}
		    System.out.println("UNKNOWN COMMAND: "+command);
			return false;
		}
		
		public void out(Channel ch, String message) {
			System.out.println(message);
			if ( this.stepsToDo > 1 ) {
				this.stepsToDo --;
				return;
			}
			while ( singleStep ) {
				System.out.print("Step>");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				try {
					if ( handle_step( reader.readLine() ) )
						return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.console().readLine();
			}
		}
		
}
