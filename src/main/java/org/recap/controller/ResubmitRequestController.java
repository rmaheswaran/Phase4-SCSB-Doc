package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.request.ReplaceRequest;
import org.recap.spring.SwaggerAPIProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by rajeshbabuk on 16/11/17.
 */
@Controller
public class ResubmitRequestController {

    private static final Logger logger = LoggerFactory.getLogger(ResubmitRequestController.class);

    @Value("${scsb.url}")
    private String scsbUrl;

    /**
     * This is the action method to resubmit the requests based on the parameters selected by the user in the UI.
     * Calls the SCSB micrcservice to put the request mesaages into outgoing queue.
     * @param replaceRequest
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/resubmitRequests", method = RequestMethod.POST)
    public String resubmitRequests(@Valid @ModelAttribute("replaceRequest") ReplaceRequest replaceRequest) {
        String responseMessage = null;
        try {
            logger.info(replaceRequest.toString());
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity request = new HttpEntity<>(replaceRequest, getHttpHeadersAuth());
            Map resultMap = restTemplate.postForObject(scsbUrl + RecapConstants.SERVICE_PATH.REPLACE_REQUEST, request, Map.class);
            if (resultMap.containsKey(RecapConstants.TOTAL_REQUESTS_FOUND)) {
                responseMessage = RecapConstants.TOTAL_REQUESTS_FOUND + ":" + resultMap.get(RecapConstants.TOTAL_REQUESTS_FOUND);
                resultMap.remove(RecapConstants.TOTAL_REQUESTS_FOUND);
            }
            if (resultMap.containsKey(RecapConstants.TOTAL_REQUESTS_IDS)) {
                responseMessage = StringUtils.isNotBlank(responseMessage) ? responseMessage + "\n" : null;
                responseMessage = responseMessage + RecapConstants.TOTAL_REQUESTS_IDS + ":" + resultMap.get(RecapConstants.TOTAL_REQUESTS_IDS);
                resultMap.remove(RecapConstants.TOTAL_REQUESTS_IDS);
            }
            if (!resultMap.containsKey(RecapConstants.INVALID_REQUEST) && !resultMap.containsKey(RecapConstants.FAILURE)) {
                Map<String, String> sortedResultMap = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));
                sortedResultMap.putAll(resultMap);
                responseMessage = responseMessage + "\n" + sortedResultMap.toString();
            } else {
                responseMessage = resultMap.toString();
            }
        } catch (Exception exception) {
            logger.error(RecapConstants.LOG_ERROR, exception);
            responseMessage = RecapConstants.FAILURE + ":" + exception.getMessage();
        }
        logger.info("Resubmit requests status : {}", responseMessage);
        return responseMessage;
    }

    /**
     * Builds http headers to make api calls.
     * @return
     */
    private HttpHeaders getHttpHeadersAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RecapConstants.API_KEY, SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        return headers;
    }
}
