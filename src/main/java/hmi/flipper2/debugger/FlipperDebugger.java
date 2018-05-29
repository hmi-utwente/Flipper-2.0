package hmi.flipper2.debugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import hmi.flipper2.FlipperException;
import hmi.flipper2.TemplateController;

public class FlipperDebugger {

	enum Channel {
		JS, CE, LOG
	};

	private boolean debugJS = false;
	private boolean debugIS = false;
	private boolean debugPC = false;
	private boolean debugEF = false;
	private boolean debugCPU = false;
	private boolean debugMEM = false;
	private boolean singleStep = false;

	private TemplateController tc;
	private CpuMemoryUsage cmu = new CpuMemoryUsage();

	public FlipperDebugger(TemplateController tc) {
		this.tc = tc;
	}
	
	public void start_checktemplates(String name) {
		this.log(name, "checkTemplates", "start");
		if ( debugCPU )
			this.cmu.startCpuTimer();
	}

	public void end_checktemplates(String name) {
		this.log(name, "checkTemplates", "end");
		if (debugCPU) {
			double cpu_usage = cmu.getCpuTimer();
			long elapsedTime = cmu.elapsedTime();
			System.out.println(
					"#CPU usage elapsed=" + elapsedTime + "ms: m1=" + cpu_usage + "%\tm2=" + cmu.getProcessCpuLoad() + "%");
		}
		if (debugMEM)
			System.out.println("#Memory[max/use/tot/free Mb]: " + cmu.maxMB() + "/" + cmu.useMB() + "/" + cmu.totalMB()
					+ "/" + cmu.freeMB());
	}

	public void js_execute(String script) {
		if (debugJS)
			out(Channel.JS, "JS: " + script);
	}

	public void js_result(String result) {
		if (debugJS)
			out(Channel.JS, "JS-RESULT: " + result);
	}

	public void js_error(String error) {
		if (debugJS)
			out(Channel.JS, "JS-ERROR: " + error);
	}

	public void precondition(String id, String descr, boolean v) {
		if (debugPC)
			out(Channel.CE, "PRECONDITION{" + id + "}=" + v + ":" + descr);
	}

	public void effect(String id, String descr) {
		if (debugEF) {
			out(Channel.CE, "EFFECT{" + id + "}" + ":" + descr);
			if (debugIS) {
				try {
					System.out.println("IS: " + this.tc.getIs("is"));
				} catch (FlipperException fe) {
				}
			}
		}
	}

	public void log(String id, String event, String v) {
		out(Channel.LOG, "#" + id + ": " + event + "\t" + v);
	}

	int stepsToDo = 0;

	private boolean handle_step(String command) {
		if (command.length() == 0)
			return true;
		if (command.equals("cont") || command.equals("continue")) {
			this.singleStep = false;
			return true;
		}
		if (command.equals("stop") || command.equals("exit")) {
			System.exit(0);
		}
		if (command.equals("is")) {
			try {
				System.out.println("IS: " + this.tc.getIs("is"));
			} catch (FlipperException fe) {
			}
			return false;
		}
		if (command.startsWith("do ")) {
			this.stepsToDo = Integer.parseInt(command.substring(3));
			return true;
		}
		System.out.println("UNKNOWN COMMAND: " + command);
		return false;
	}

	public void out(Channel ch, String message) {
		System.out.println(message);
		if (this.stepsToDo > 1) {
			this.stepsToDo--;
			return;
		}
		while (singleStep) {
			System.out.print("Step>");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				if (handle_step(reader.readLine()))
					return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.console().readLine();
		}
	}

}
