/**
 *  Copyright 2012 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cnr.isti.hpc.dexter.milnewitten;

import java.util.Collections;

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.dexter.Document;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.entity.EntityRanker;
import it.cnr.isti.hpc.dexter.randomwalk.ReferentGraph;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spot.Spotter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 5, 2012
 */
public class Wikiminer {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Wikiminer.class);

	Spotter spotter;
	Stopwatch stopwatch;
	Disambiguator disambiguator;

	public Wikiminer() {
		stopwatch = new Stopwatch();
		spotter = new Spotter();
		disambiguator = new Disambiguator();
		disambiguator.loadDefaultClassifier();
	}

	public String stats() {
		return stopwatch.stat();
	}

	public EntityMatchList tag(Document doc) {


		stopwatch.start("spotting");
		SpotMatchList results = spotter.match(doc);

		logger.info("spotting performed in {} millis",
				stopwatch.stop("spotting"));
		// List<Spot> spots = new ArrayList<Spot>();
		// for (SpotMatch m : results) {
		// if (!spots.contains(m.getSpot())) {
		// spots.add(m.getSpot());
		// }
		// try {
		//
		// writer.write(m.toString());
		//
		// writer.write("\n");
		// } catch (IOException e) {
		// logger.error("writing the entities in the output file ({})",
		// e.toString());
		// System.exit(-1);
		// }
		// }
		// try {
		// writer.write("------ SPOTS -----\n");
		// writer.write(results.getEntities().toString());
		//
		// } catch (IOException e) {
		// logger.error("writing the entities in the output file ({})",
		// e.toString());
		// System.exit(-1);
		// }
		//
		// logger.info("spotted in {} millis", end - start);
		EntityMatchList eml = null;
		if (!results.isEmpty()) {
			logger.info("performing disambiguation");
			stopwatch.start("disambiguation");
			Context context = new Context(results);
			eml = disambiguator.disambiguate(context);
			
			
			long time = stopwatch.stop("disambiguation");
			logger.info("done in {} millis", time);
			stopwatch.start("ranking entities");
			Collections.sort(eml);
			stopwatch.stop("ranking entities");
			eml = eml.removeOverlappings();
		} else {
			logger.warn("no spot identified in text");
		}
		return eml;

	}

}