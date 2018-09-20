package hmi.flipper2.debugger;

import hmi.flipper2.TemplateController;

public class FlipperDebugger {
	
	enum Kind {
		Start, Stop
	};
	
	enum Action {
		CheckTemplates, CheckTemplate, Precondition, Effect, JavascriptExec, JavaExec, Other
	};
	
	protected long totalTimes[] = null;
	
	private FlipperState state = null;
	
	public void handle_action(FlipperState s) {
		boolean verbose = true;
		
		if ( verbose )
			System.out.println("#"+s.action.name() + "\t" + s.id + "\t"+pt(s.duration()));
		switch (s.action) {
		case CheckTemplates: {
			for (Action a : Action.values()) {
				System.out.println(a.name() + " = " + pt(this.totalTimes[a.ordinal()]));
			}
		}
			break;
		case CheckTemplate:
			break;
		case Precondition:
			break;
		case Effect:
			break;
		case JavascriptExec:
			break;
		case JavaExec:
			break;
		case Other:
			throw new RuntimeException("Other actions Not Implemented Yet");
		}
	}
	
	private void handle_startstop(Kind kind, Action action, String other_action, String id, String arg) {
		boolean verbose = false;
		
		if ( kind == Kind.Start ) {
			state = state.push();
			state.action = action;
			state.id = id;
			state.startTime = System.nanoTime();
			state.startArg = arg;
			if ( verbose )
				System.out.println(kind.name() + "\t" + action.name() + "\t" + id);
		} else {
			state.stopTime = System.nanoTime();
			state.stopArg = arg;
			if ( verbose )
				System.out.println(kind.name() + "\t" + action.name() + "\t" + id + "\t("+pt(state.duration())+")");
			this.totalTimes[action.ordinal()] += state.duration();
			handle_action(state);
			state = state.pop();
		}
				
	}
	
	public void start_CheckTemplates(String id, String comment) {
		this.handle_startstop(Kind.Start, Action.CheckTemplates, null, id, comment);
	}
	
	public void stop_CheckTemplates(String id, String comment) {
		this.handle_startstop(Kind.Stop, Action.CheckTemplates, null, id, comment);
	}
	
	public void start_CheckTemplate(String id, String comment) {
		this.handle_startstop(Kind.Start, Action.CheckTemplate, null, id, comment);
	}
	
	public void stop_CheckTemplate(String id, String comment) {
		this.handle_startstop(Kind.Stop, Action.CheckTemplate, null, id, comment);
	}
	
	public void start_Precondition(String id, String comment) {
		this.handle_startstop(Kind.Start, Action.Precondition, null, id, comment);
	}
	
	public void stop_Precondition(String id, String comment) {
		this.handle_startstop(Kind.Stop, Action.Precondition, null, id, comment);
	}
	
	public void start_Effect(String id, String comment) {
		this.handle_startstop(Kind.Start, Action.Effect, null, id, comment);
	}
	
	public void stop_Effect(String id, String comment) {
		this.handle_startstop(Kind.Stop, Action.Effect, null, id, comment);
	}
	
	public void start_JavascriptExec(String id, String comment) {
		this.handle_startstop(Kind.Start, Action.JavascriptExec, null, id, comment);
	}
	
	public void stop_JavascriptExec(String id, String comment) {
		this.handle_startstop(Kind.Stop, Action.JavascriptExec, null, id, comment);
	}
	
	public void start_JavaExec(String id, String comment) {
		this.handle_startstop(Kind.Start, Action.JavaExec, null, id, comment);
	}
	
	public void stop_JavaExec(String id, String comment) {
		this.handle_startstop(Kind.Stop, Action.JavaExec, null, id, comment);
	}

	public static final String pt(long t) {
		if ( t != (long)-1 )
			return ((int) (t / 1000)) + "us";
		else 
			return "EMPTY";
	}

	protected TemplateController tc;

	public FlipperDebugger(TemplateController tc) {
		this.tc = tc;
		start();
	}
	
	private void start() {
		this.state = FlipperState.create(8);
		this.totalTimes = new long[Action.values().length];
		for (Action a : Action.values() ) {
			this.totalTimes[a.ordinal()] = 0;
		}
	}
	
	public void restart() {
		start();
	}
	
}
