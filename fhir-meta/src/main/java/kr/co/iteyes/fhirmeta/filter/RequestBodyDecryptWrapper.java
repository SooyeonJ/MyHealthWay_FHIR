package kr.co.iteyes.fhirmeta.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import kr.co.iteyes.fhirmeta.utils.SeedCtrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class RequestBodyDecryptWrapper extends HttpServletRequestWrapper {
    // 가로챈 데이터를 가공하여 담을 final 변수

    private final String requestDecryptBody;

    public RequestBodyDecryptWrapper(HttpServletRequest httpServletRequest, String key) throws Exception {
        super(httpServletRequest);

        String requestHashData = requestDataByte(httpServletRequest);

        byte[] compressDefaultPlainText = SeedCtrUtils.SEED_CTR_Decrypt_byte(key, requestHashData);
        String byteToStringTemp = Base64.getEncoder().encodeToString(compressDefaultPlainText);

        log.debug("요청 = {}", requestHashData);

        requestDecryptBody = byteToStringTemp;
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
//        if(inputStream.available() == 0) throw new NullPointerException();

        rawData = IOUtils.toByteArray(inputStream);
        return new String(rawData);
    }
}
