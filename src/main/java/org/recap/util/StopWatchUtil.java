package org.recap.util;

import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class StopWatchUtil  {

    private StopWatchUtil(){
        throw new IllegalStateException("Util class");
    }

    private static final Logger logger = LoggerFactory.getLogger(StopWatchUtil.class);

    public static <T> void executeAndEstimateTotalTimeTaken(Supplier<T> supplier,String functionName) {
        StopWatch stopWatchFunc = new StopWatch();
        stopWatchFunc.start();
        supplier.get();
        stopWatchFunc.stop();
        stopWatchFunc.getTotalTimeSeconds();
        logger.info(RecapConstants.LOG_EXECUTION_TIME,functionName,stopWatchFunc.getTotalTimeSeconds());
    }

    public static void executeAndEstimateTotalTimeTaken(IntConsumer consumer, Integer batchSize, String functionName) {
        StopWatch stopWatchFunc = new StopWatch();
        stopWatchFunc.start();
        consumer.accept(batchSize);
        stopWatchFunc.stop();
        stopWatchFunc.getTotalTimeSeconds();
        logger.info(RecapConstants.LOG_EXECUTION_TIME ,functionName, stopWatchFunc.getTotalTimeSeconds());
    }

    public static  Map<String, Integer> executeAndEstimateTotalTimeTaken(BiFunction<Integer, Map<String, Integer>, Map<String, Integer>> biFunction, Integer batchSize, Map<String,Integer> institutionCounterMap, String functionName) {
        StopWatch stopWatchFunc = new StopWatch();
        stopWatchFunc.start();
        Map<String, Integer> counterMap = biFunction.apply(batchSize,institutionCounterMap);
        stopWatchFunc.stop();
        stopWatchFunc.getTotalTimeSeconds();
        logger.info(RecapConstants.LOG_EXECUTION_TIME ,functionName, stopWatchFunc.getTotalTimeSeconds());
        return counterMap;
    }

}
