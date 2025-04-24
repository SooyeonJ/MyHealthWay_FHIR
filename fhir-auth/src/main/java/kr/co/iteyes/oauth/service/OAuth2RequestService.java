package kr.co.iteyes.oauth.service;

import kr.co.iteyes.oauth.exception.AuthFailedException;
import kr.co.iteyes.oauth.dto.OauthTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Slf4j
@Service
public class OAuth2RequestService {


    @Autowired
    ClientService clientService;

    @Autowired
    TokenService tokenService;

    @Autowired
    ClientTokenService clientTokenService;

    public synchronized String oauthGetTokenRequestService(OauthTokenDto.request.requestToken requestToken) throws Exception {
        String validatyYN = "N";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date expired_date = null;
        Date current_date = new Date();
        String token = "";
        ClientDetails clientDetails = null;
        try {
            clientDetails = clientService.loadClientByClientId(requestToken.getClientId());
        } catch (NoSuchClientException e) {
            throw new AuthFailedException("클라이언트가 존재하지 않습니다. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AuthFailedException("인증정보가 올바르지 않습니다...." + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
        }

        if (!clientDetails.getScope().contains("read")) {
            throw new AuthFailedException("해당 클라이언트는 토큰조회 권한이 존재하지 않습니다. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
        }
        if (requestToken.getClientSecretKey().equals(clientDetails.getClientSecret())) {

            Collection<OAuth2AccessToken> collectionTokens = tokenService.readAccessTokens(requestToken.getClientId());
            if (collectionTokens.isEmpty()) {
                String jsonResult = clientTokenService.createAuthToken(requestToken.getClientId());
                return jsonResult;
            }
            ArrayList<OAuth2AccessToken> newList = new ArrayList<>(collectionTokens);
            for (OAuth2AccessToken oAuth2AccessToken : newList) {
                expired_date = oAuth2AccessToken.getExpiration();
                token = oAuth2AccessToken.getValue();
                if (current_date.before(expired_date)) {
                    validatyYN = "Y";
                    break;
                }
            }
        } else {
            throw new AuthFailedException("인증정보가 올바르지 않습니다. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
        }
        if (validatyYN.equals("Y")) {
            String jsonResult = "{\n" +
                    "\t\"validityYN\":\"" + validatyYN + "\",\n" +
                    "\t\"validityDateTime\":\"" + formatter.format(expired_date) + "\",\n" +
                    "\t\"token\":\"" + token + "\"\n" +
                    "}";
            return jsonResult;
        } else {
            throw new AuthFailedException("인증정보가 올바르지 않습니다.. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
        }

    }

    public synchronized String oauthCheckTokenRequestService(OauthTokenDto.request.requestToken requestToken) throws Exception {
        try {
            OauthTokenDto.checkToken oauthToken = null;
            String validatyYN = "N";
            OauthTokenDto.request.checkToken request = null;
            ClientDetails clientDetails = null;
            try {
                clientDetails = clientService.loadClientByClientId(requestToken.getClientId());

            } catch (NoSuchClientException e) {
                throw new AuthFailedException("클라이언트가 존재하지 않습니다. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
            }
            if (clientDetails == null) {
                throw new AuthFailedException("인증정보가 존재하지 않습니다. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
            }
            if (!clientDetails.getScope().contains("read")) {
                throw new AuthFailedException("해당 클라이언트는 토큰인증 권한이 존재하지 않습니다. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
            }
            if (!requestToken.getClientSecretKey().equals(clientDetails.getClientSecret())) {
                throw new AuthFailedException("인증정보가 올바르지 않습니다. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
            }
            String[] arrAccessToken = requestToken.getToken().split("Bearer ");
            String strAccessToken = "";
            if (arrAccessToken.length >= 2) {
                strAccessToken = arrAccessToken[1];
            } else {
                strAccessToken = requestToken.getToken();
            }

            OAuth2AccessToken oAuth2AccessToken = tokenService.getAccessTokenByToken(strAccessToken);
            if (oAuth2AccessToken == null) {
                throw new AuthFailedException("입력하신 토큰정보가 존재하지 않습니다.");
            }

            Date current_date = new Date();
            Date expired_date = oAuth2AccessToken.getExpiration();
            if (!current_date.before(expired_date)) {
                throw new AuthFailedException("해당 토큰은 유효기간이 만료되었습니다.");
            }
            String jsonResult = "{\n" +
                    "\t\"validityYN\":\"Y\"\n" +
                    "}";

            return jsonResult;
        } catch (Exception e) {
            throw new AuthFailedException("인증정보가 올바르지 않습니다.. " + requestToken.getClientId() + "/" + requestToken.getClientSecretKey() + "/" + requestToken.getToken());
        }

    }

    public void createToken(OauthTokenDto.request.createTokenRequest createTokenRequest) {
        try {
            ClientDetails clientDetails = clientService.loadClientByClientId(createTokenRequest.getClientId());
            if (!clientDetails.getScope().contains("read")) {
                clientService.updateScope(clientDetails);
                log.debug("토큰 scope 수정 완료");
            }
        } catch (NoSuchClientException e1) {
            try {
                clientService.createClient(createTokenRequest.getClientId(), createTokenRequest.getClientSecretKey());
                log.debug("토큰 생성 완료");
            } catch (Exception e2) {
                throw new AuthFailedException("토큰 생성 실패 " + createTokenRequest.getClientId() + "/" + createTokenRequest.getClientSecretKey() + "/");
            }
        } catch (SQLException e3) {
            throw new AuthFailedException("토큰 생성 실패 " + createTokenRequest.getClientId() + "/" + createTokenRequest.getClientSecretKey() + "/");
        }
        log.debug("토큰 이미 존재");
    }
}
