package org.recap.camel.processor;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 9/1/17.
 */
public class MatchingAlgorithmProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    MatchingAlgorithmProcessor matchingAlgorithmProcessor;

    @Mock
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Mock
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Test
    public void updateItemEntityTest() throws Exception {
        Mockito.when(itemDetailsRepository.saveAll(Mockito.anyCollection())).thenReturn(new ArrayList<>());
        matchingAlgorithmProcessor.updateItemEntity(Arrays.asList(new ItemEntity()));
        assertNotNull(matchingAlgorithmProcessor);
    }

    @Test
    public void saveMatchingReportEntity() throws Exception {
        Mockito.when(reportDetailRepository.saveAll(Mockito.anyCollection())).thenReturn(new ArrayList<>());
        matchingAlgorithmProcessor.saveMatchingReportEntity(Arrays.asList(new ReportEntity()));
        assertNotNull(matchingAlgorithmProcessor);
    }

    @Test
    public void updateMatchingBibEntityTest() throws Exception {
        Mockito.when(matchingBibDetailsRepository.saveAll(Mockito.anyCollection())).thenReturn(new ArrayList<>());
        matchingAlgorithmProcessor.updateMatchingBibEntity( new HashMap());
        assertNotNull(matchingAlgorithmProcessor);
    }

    @Test
    public void saveMatchingBibEntity() throws Exception {
        Mockito.when(matchingBibDetailsRepository.updateStatusBasedOnBibs(Mockito.anyString(),Mockito.anyList())).thenReturn(1);
        matchingAlgorithmProcessor.saveMatchingBibEntity(Arrays.asList(new MatchingBibEntity()));
        assertNotNull(matchingAlgorithmProcessor);
    }

    @Test
    public void saveMatchingMatchPointEntity() throws Exception {
        Mockito.when(matchingMatchPointsDetailsRepository.saveAll(Mockito.anyCollection())).thenReturn(new ArrayList<>());
        matchingAlgorithmProcessor.saveMatchingMatchPointEntity(Arrays.asList(new MatchingMatchPointsEntity()));
        assertNotNull(matchingAlgorithmProcessor);
    }
}