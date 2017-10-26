package org.recap.matchingalgorithm;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.controller.MatchingAlgorithmController;
import org.recap.matchingalgorithm.service.MatchingAlgorithmHelperService;
import org.recap.matchingalgorithm.service.MatchingAlgorithmUpdateCGDService;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.util.MatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 27/10/16.
 */
public class MatchingAlgorithmUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmUT.class);

    @Mock
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Mock
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Mock
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Autowired
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
    MatchingAlgorithmController matchingAlgorithmController;

    @Mock
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    private Integer batchSize = 1000;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void populateTempMatchingPointsEntity() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Mockito.when(matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities()).thenReturn(new Long(7));
        long count = matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities();

        stopWatch.stop();
        logger.info("Total Time taken : " + stopWatch.getTotalTimeSeconds());

        Thread.sleep(10000);
        Mockito.when(matchingMatchPointsDetailsRepository.count()).thenReturn(new Long(7));
        long savedCount = matchingMatchPointsDetailsRepository.count();
        assertTrue(savedCount>0);
    }

    @Test
    public void populateTempMatchingBibsEntity() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Mockito.when(matchingAlgorithmHelperService.populateMatchingBibEntities()).thenReturn(new Long(1));
        long count = matchingAlgorithmHelperService.populateMatchingBibEntities();

        stopWatch.stop();
        logger.info("Total Time taken : " + stopWatch.getTotalTimeSeconds());
        Mockito.when(matchingBibDetailsRepository.count()).thenReturn(new Long(1));
        long savedBibsCount = matchingBibDetailsRepository.count();
        assertTrue(savedBibsCount>0);
    }

    @Test
    public void testDiacriticTitles() {
        String title = "--A bude hůř : román o třech-dílech /-";
        System.out.println("Actual Title : " + title);
        String normalizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD);
        System.out.println("Step 1 Normalized Title : " + normalizedTitle);
        normalizedTitle = normalizedTitle.replaceAll("[^\\p{ASCII}]", "");
        System.out.println("Step 2 Normalized Title : " + normalizedTitle);
        normalizedTitle = normalizedTitle.replaceAll("\\p{M}", "");
        System.out.println("Step 3 Normalized Title : " + normalizedTitle);
    }

    @Test
    public void testGetTitleToMatch() {
        String title = "--A bude hůř : román o třech-dílech /-";
        System.out.println("Actual Title : " + title);
        String titleToMatch = matchingAlgorithmUtil.getTitleToMatch(title);
        System.out.println("Title To Match : " + titleToMatch);
    }

    @Test
    public void runWholeMatchingAlgorithm() throws Exception {
        Date matchingAlgoDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        String matchingAlgoDateString = sdf.format(matchingAlgoDate);
        Mockito.when(matchingAlgorithmController.matchingAlgorithmFull(matchingAlgoDateString)).thenReturn("Status  : Done");
        String status = matchingAlgorithmController.matchingAlgorithmFull(matchingAlgoDateString);
        assertNotNull(status);
        assertTrue(status.contains("Done"));
    }

    @Test
    public void updateCGDForMatchingAlgorithm() throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmUpdateCGDService.updateCGDProcessForMonographs(batchSize);

        stopWatch.stop();
        logger.info("Total Time taken to update CGD is : " + stopWatch.getTotalTimeSeconds());
    }

}
