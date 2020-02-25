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
package hmi.flipper2.launcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.nio.file.SensitivityWatchEventModifier;

public class FlipperTemplateWatcher extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(FlipperTemplateWatcher.class.getName());
	
	private boolean stop;
	private boolean fileChanged;
	private String pathString;
	private boolean paused;
	
	private final Object pauseLock = new Object();
	
	public FlipperTemplateWatcher(String p) {
		this.pathString = p;
	}
	
	public String getTemplatePath() {
		return pathString;
	}
	
	public boolean hasChanged() {
		return fileChanged;
	}

	public void stopGracefully() {
		stop = true;
	}
	
	public void reset() {
		paused = false;
    	fileChanged = false;
	}
	
	public void run() {
		final Path path = FileSystems.getDefault().getPath(pathString);
		final String watchedFileName = path.getFileName().toString();
		stop = false;
		paused = false;
		fileChanged = false;
		logger.debug("Watching template: "+watchedFileName);
		try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
			path.getParent().register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
			final WatchKey wk = watchService.take();
		    while (!stop) {
		    	if (paused) continue;
		    	
		    	if (!wk.reset()) {
		    		logger.debug("WatchKey invalid, stoped watching "+pathString);
		    		stop = true;
		        }
		    	
		        for (WatchEvent<?> event : wk.pollEvents()) {
		            final Path changed = (Path) event.context();
		            if (changed.equals(path.getFileName())){
			    		logger.debug("Changed ");
		            	fileChanged = true;
		            	paused = true;
		            }
		        }
		    }
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		logger.debug("==== ENDED WATCHER =====");
	}
}

