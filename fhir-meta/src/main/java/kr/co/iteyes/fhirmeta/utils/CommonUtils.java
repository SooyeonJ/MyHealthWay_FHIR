package kr.co.iteyes.fhirmeta.utils;

import org.apache.commons.lang3.StringUtils;
import kr.re.nsr.crypto.otp.TimeOtp;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class CommonUtils {

    private static final int MYMD_DISTANCE = 300000;

    public static Document xmlStringToDoc(String xmlString) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

    public static boolean isValidHash(String serverDomainNo, String hashValue) {
        TimeOtp timeOtp = new TimeOtp(serverDomainNo, MYMD_DISTANCE);
        if(StringUtils.isBlank(serverDomainNo) || StringUtils.isBlank(hashValue)) return false;
        return timeOtp.verifyTimeOtpStringWithNow(hashValue);
    }

    public static String createHashValue(String serverDomainNo) {
        if(StringUtils.isBlank(serverDomainNo)) return null;
        TimeOtp timeOtp = new TimeOtp(serverDomainNo, MYMD_DISTANCE);
        return timeOtp.obtainTimeOtpStringWithNow();
    }
}
