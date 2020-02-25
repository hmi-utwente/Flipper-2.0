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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hmi.flipper2.FlipperException;
import hmi.flipper2.TemplateController;

// TODO: Add database support
public class FlipperLauncher {

	private static Logger logger = LoggerFactory.getLogger(FlipperLauncher.class.getName());
	private static FlipperLauncherThread flt;
	
	public static void main(String[] args) {
		String help = "Expecting commandline arguments in the form of \"-<argname> <arg>\".\nAccepting the following argnames: config";
    	String flipperPropFile = "flipper.properties";
    	
        if(args.length % 2 != 0){
        	System.err.println(help);
        	System.exit(0);
        }
        
        for(int i = 0; i < args.length; i = i + 2){
        	if(args[i].equals("-config")) {
        		flipperPropFile = args[i+1];
        	} else {
            	System.err.println("Unknown commandline argument: \""+args[i]+" "+args[i+1]+"\".\n"+help);
            	System.exit(0);
        	}
        }
        
		Properties ps = new Properties();
        InputStream flipperPropStream = FlipperLauncher.class.getClassLoader().getResourceAsStream(flipperPropFile);

        try {
            ps.load(flipperPropStream);
        } catch (IOException ex) {
            logger.warn("Could not load flipper settings from "+flipperPropFile);
            ex.printStackTrace();
        }

        // If you want to check templates based on events (i.e. messages on middleware),
        // you can run  flt.forceCheck(); from a callback to force an immediate check.
        logger.debug("FlipperLauncher: Starting Thread");
        flt = new FlipperLauncherThread(ps);
        flt.start();
	}
}

/* Sample flipper.properties file:

# name for TemplateController
name: TestTest
# comma seperated list of external lists
jslibs: jslibs/underscore-min.js
# comma seperated list of template paths
templates: example/Underscore.xml
#evaluate template every 1000/evalFrequency milliseconds
evalFrequency: 1
#n=0:don't stop, n=1+: stop after n times checking templates
maxSteps: 0
#True|False
stopIfUnchanged: False
#ALL,NONE,ONLY_CHANGED
autoReloadTemplates: ONLY_CHANGED

*/
