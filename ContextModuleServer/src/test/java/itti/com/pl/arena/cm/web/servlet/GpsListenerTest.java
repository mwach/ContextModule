package itti.com.pl.arena.cm.web.servlet;

import itti.com.pl.arena.cm.utils.helper.StringHelper;
import itti.com.pl.arena.cm.web.servlet.GpsListener;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

public class GpsListenerTest {

    private GpsListener gpsListener = new GpsListener();

    @Test
    public void testDoGet() throws ServletException, IOException {

        // do the 'get' request
        HttpServletResponse response = getMockHttpResponse();
        gpsListener.doGet(getMockHttpRequest("dummyRequest"), response);

        // verify, that error message was written in the response
        ServletOutputStream mockOutputStream = response.getOutputStream();
        Mockito.verify(mockOutputStream).write(Mockito.any(byte[].class));
    }

    @Test
    public void testDoPost() throws ServletException, IOException {

        // do the 'post' request
        HttpServletResponse response = getMockHttpResponse();
        gpsListener.doPost(getMockHttpRequest("dummyRequest"), response);

        // verify, that error message was written in the response
        ServletOutputStream mockOutputStream = response.getOutputStream();
        Mockito.verify(mockOutputStream).write(GpsListener.POST_RESPONSE_OK);
    }

    @Test
    public void testDoPostEmpty() throws ServletException, IOException {

        // do the 'post' request
        HttpServletResponse response = getMockHttpResponse();
        gpsListener.doPost(getMockHttpRequest(""), response);

        // verify, that error message was written in the response
        ServletOutputStream mockOutputStream = response.getOutputStream();
        Mockito.verify(mockOutputStream).write(GpsListener.POST_RESPONSE_FAIL);
    }

    @Test
    public void testDoPostNull() throws ServletException, IOException {

        // do the 'post' request
        HttpServletResponse response = getMockHttpResponse();
        gpsListener.doPost(getMockHttpRequest(null), response);

        // verify, that error message was written in the response
        ServletOutputStream mockOutputStream = response.getOutputStream();
        Mockito.verify(mockOutputStream).write(GpsListener.POST_RESPONSE_FAIL);
    }

    /**
     * creates dummy http request
     * 
     * @param requestContent
     *            data to be written
     * @return Http Servlet Request object
     * @throws IOException
     */
    private HttpServletRequest getMockHttpRequest(String requestContent) throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getInputStream()).thenReturn(new TestServletInputStream(requestContent));
        return request;
    }

    /**
     * creates dummy http response
     * 
     * @return Http Servlet Response object
     * @throws IOException
     */
    private HttpServletResponse getMockHttpResponse() throws IOException {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream mockServletOutputStream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(mockServletOutputStream);
        return response;
    }

    /**
     * Test implementation of the Input Stream
     * 
     * @author mawa
     * 
     */
    private static class TestServletInputStream extends ServletInputStream {

        private static final int EOM = -1;

        private String message = null;
        private int position = 0;

        /**
         * Constructor
         * 
         * @param message
         *            message to be written
         */
        public TestServletInputStream(String message) {
            this.message = message;
        }

        @Override
        public int read() throws IOException {

            // check if there is data to write, or all data was already written
            if (StringHelper.hasContent(message) && position < message.length()) {
                return ((int) message.charAt(position++));
            }
            // end of message
            return EOM;
        }

        @Override
        public int available() throws IOException {
            return StringHelper.hasContent(message) ? (message.length() - position) : 0;
        }
    }
}
