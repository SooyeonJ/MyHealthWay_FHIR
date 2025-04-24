package kr.co.iteyes.fhirmeta.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResponseBodyWrapper extends HttpServletResponseWrapper {
    // 가로챈 데이터를 가공하여 담을 final 변수

    ByteArrayOutputStream byteArrayOutputStream;
    ResponseBodyServletOutputStream responseBodyServletOutputStream;

    public ResponseBodyWrapper(HttpServletResponse response) {
        super(response);
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (responseBodyServletOutputStream == null) {
            responseBodyServletOutputStream = new ResponseBodyServletOutputStream(byteArrayOutputStream);
        }
        return responseBodyServletOutputStream;
    }

    // 가로챈 Response Body Get
    public String getDataStreamToString() {
        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
