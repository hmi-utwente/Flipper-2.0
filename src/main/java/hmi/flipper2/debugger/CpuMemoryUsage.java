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
package hmi.flipper2.debugger;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import hmi.flipper2.TemplateController;

import static hmi.flipper2.TemplateController.logger;

public class CpuMemoryUsage {

	enum Channel {
		JS, CE, LOG
	};

	CpuMemoryUsage() {
		this.rt = Runtime.getRuntime();
		//
	}

	com.sun.management.OperatingSystemMXBean operatingSystemMXBean;
	RuntimeMXBean runtimeMXBean;
	int availableProcessors;
	long prevUpTime;
	long prevProcessCpuTime;
	long timeMarker;
	Runtime rt;
	
	public void startCpuTimer() {
		this.operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();
		this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		this.availableProcessors = operatingSystemMXBean.getAvailableProcessors();
		this.prevUpTime = runtimeMXBean.getUptime();
		this.prevProcessCpuTime = operatingSystemMXBean.getProcessCpuTime();
		//
		this.timeMarker = System.currentTimeMillis();
	}

	public double getCpuTimer() {
		this.operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();
		long upTime = runtimeMXBean.getUptime();
		long processCpuTime = operatingSystemMXBean.getProcessCpuTime();
		long elapsedCpu = processCpuTime - prevProcessCpuTime;
		long elapsedTime = upTime - prevUpTime;

		return Math.min(99F, elapsedCpu / (elapsedTime * 10000F * availableProcessors));
	}
	
	public long elapsedTime() {
		return System.currentTimeMillis() - this.timeMarker;
	}

	public double getProcessCpuLoad() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

			if (list.isEmpty())
				return Double.NaN;

			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();

			// usually takes a couple of seconds before we get real values
			if (value == -1.0)
				return Double.NaN;
			// returns a percentage value with 1 decimal point precision
			return ((int) (value * 1000) / 10.0);
		} catch (Exception e) {
			logger.error("EXCEPTION: " + e);
			return 0.0;
		}
	}
	
	public long maxMB() {
		return this.rt.maxMemory() / 1000000;
	}
	
	public long totalMB() {
		return this.rt.totalMemory() / 1000000;
	}
	
	public long freeMB() {
		return this.rt.freeMemory() / 1000000;
	}
	
	public long useMB() {
		return (this.rt.totalMemory() - this.rt.freeMemory()) / 1000000;
	}
	
}
