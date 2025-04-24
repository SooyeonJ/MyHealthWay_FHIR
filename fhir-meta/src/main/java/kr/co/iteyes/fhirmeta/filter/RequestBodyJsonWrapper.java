package kr.co.iteyes.fhirmeta.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class RequestBodyJsonWrapper extends HttpServletRequestWrapper {
    //private final Map<String, Object> jsonRequest;
    private final Object jsonRequest;

    public RequestBodyJsonWrapper(HttpServletRequest httpServletRequest) throws Exception {
        super(httpServletRequest);
        String requestHashData = requestDataByte(httpServletRequest);
        log.debug("압축해제 후 복호화 = {}", requestHashData);
        //jsonRequest = new ObjectMapper().readValue(requestHashData, Map.class);
        jsonRequest = new ObjectMapper().readValue(requestHashData, Object.class);
    }

    private String requestDataByte(HttpServletRequest request) throws IOException {
        byte[] rawData = new byte[128];
        InputStream inputStream = request.getInputStream();
        rawData = IOUtils.toByteArray(inputStream);
        return new String(rawData);
    }
}
