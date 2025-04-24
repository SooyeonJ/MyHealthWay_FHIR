package kr.co.iteyes.fhirmeta.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import kr.co.iteyes.fhirmeta.utils.Lz4Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class RequestBodyDecompressWrapper extends HttpServletRequestWrapper {
    // 가로챈 데이터를 가공하여 담을 final 변수

    private final String requestDecryptBody;

    public RequestBodyDecompressWrapper(HttpServletRequest httpServletRequest) throws Exception {
        super(httpServletRequest);

        String requestHashData = requestDataByte(httpServletRequest);
        byte stringToByteTemp[] = Base64.getDecoder().decode(requestHashData);

        Lz4Utils lz4Utils = new Lz4Utils();
        byte defaultPlainText[] = lz4Utils.decompress(stringToByteTemp);

        requestDecryptBody = new String(defaultPlainText);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestDecryptBody.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    private String requestDataByte(HttpServletRequest request) throws IOException {
        byte[] rawData = new byte[128];
        InputStream inputStream = request.getInputStream();
        rawData = IOUtils.toByteArray(inputStream);
        return new String(rawData);
    }
}
