package com.freetmp.errorcoder.support;

import com.freetmp.errorcoder.support.filter.ErrorCoderFilter;
import org.apache.log4j.BasicConfigurator;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class ErrorCoderFilterTest {

    @BeforeClass
    public static void setUp(){
        BasicConfigurator.configure();
    }

    @Test
    public void testDoFilter() throws ServletException, IOException {
        /**
         * mock the servlet api object
         */
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        FilterConfig filterConfig = mock(FilterConfig.class);
        PrintWriter writer = mock(PrintWriter.class);

        /**
         * custom their behavior to do what we wanted
         */
        doThrow(ServletException.class).when(filterChain).doFilter(request,response);
        when(filterConfig.getInitParameter(ErrorCoderFilter.MAPPER_LOCATIONS_KEY)).thenReturn("com.freetmp.errorcoder.support.filter");
        doReturn(writer).when(response).getWriter();

        /**
         * real process of the object being tested
         */
        ErrorCoderFilter filter = new ErrorCoderFilter();
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);


        /**
         * verify the result
         */
        ArgumentCaptor<String> errorStrCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).println(errorStrCaptor);

        Assertions.assertThat(errorStrCaptor.capture()).isNotEmpty().contains("code", "message");
        System.out.println(errorStrCaptor.capture());

        filter.destroy();
    }
}
