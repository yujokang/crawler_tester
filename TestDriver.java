/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * Adapted from Yasser Ganjisaffar
 * This starts a single-thread crawler at command line specified depth with and URL seeds.
 */
public class TestDriver {
    private static final Logger logger = LoggerFactory.getLogger(TestDriver.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            logger.info("Needed parameters: ");
            logger.info("\t rootFolder (it will contain intermediate crawl data)");
            logger.info("\t crawler depth");
            logger.info("\t seed URLs (if not present default are used)");
            return;
        }

        boolean seeds_supplied = false;
        if (args.length > 2) {
            seeds_supplied = true;
        }

    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String crawlStorageFolder = args[0];

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(crawlStorageFolder);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(1000);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
        config.setMaxDepthOfCrawling(Integer.parseInt(args[1]));

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(1000);

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(true);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
        config.setResumableCrawling(false);

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        if (!seeds_supplied) {
            /* Use default seeds if none are supplied */
            controller.addSeed("http://www.ics.uci.edu/");
            controller.addSeed("http://www.ics.uci.edu/~lopes/");
            controller.addSeed("http://www.ics.uci.edu/~welling/");
        } else {
            int seeds = 2;
            while (seeds < args.length) {
                controller.addSeed(args[seeds]);
                seeds++;
            }
        }
    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(TestCrawler.class, numberOfCrawlers);
    }
}
