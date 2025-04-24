package kr.co.iteyes.fhirmeta.utils;

import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

@Slf4j
public class HttpUtils {

    public static HttpServletResponse commonRes(HttpServletResponse res, String responseMessage, int statusCode) {
        try {
            log.debug("응답 = {}", responseMessage);

            byte[] responseMessageBytes = responseMessage.getBytes("utf-8");
            int contentLength = responseMessageBytes.length;
            res.setStatus(statusCode);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setContentLength(contentLength);
            res.getOutputStream().write(responseMessageBytes);
            res.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return res;
        }
    }

    public static HttpServletResponse commonEncryptRes(HttpServletResponse res, String key, String responseMessage, int statusCode) {
        try {
            log.debug("응답 = {}", responseMessage);

            Lz4Utils lz4Utils = new Lz4Utils();
            byte compressTemp[] = lz4Utils.compress(responseMessage.getBytes("utf-8"));

            // Response 처리
            responseMessage = SeedCtrUtils.SEED_CTR_Encrypt(key, compressTemp);;
            log.debug("압축 후 암호화 응답 = {}", responseMessage);

            byte[] responseMessageBytes = responseMessage.getBytes("utf-8");

            int contentLength = responseMessageBytes.length;

            res.setStatus(statusCode);
//            res.setHeader("serverDomainNo", serverDomainNo);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setContentLength(contentLength);
            res.getOutputStream().write(responseMessageBytes);
            res.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return res;
        }
    }
}
