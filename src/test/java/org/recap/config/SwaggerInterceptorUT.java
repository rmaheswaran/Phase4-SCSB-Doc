package org.recap.config;



import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertTrue;


/**
 * Created by hemalathas on 25/1/17.
 */
public class SwaggerInterceptorUT extends BaseTestCase {

    @Autowired
    SwaggerInterceptor swaggerInterceptor;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Test
    public void testPreHandle() throws Exception {
        httpServletRequest.setAttribute("api_key","recap");
        swaggerInterceptor.afterCompletion(httpServletRequest,httpServletResponse,new Object(),new Exception());
        swaggerInterceptor.postHandle(httpServletRequest,httpServletResponse,new Object(),new ModelAndView());
        boolean continueExport = swaggerInterceptor.preHandle(httpServletRequest,httpServletResponse,new Object());
        assertTrue(!continueExport);
    }

}