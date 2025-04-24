package kr.co.iteyes.oauth.controller;

import kr.co.iteyes.oauth.dto.CommonDto;
import kr.co.iteyes.oauth.service.OAuth2RequestService;
import kr.co.iteyes.oauth.dto.OauthTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuth2RequestService oAuth2RequestService;

    @RequestMapping(value = "/token", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String oauthRequest(Principal principal, @RequestBody OauthTokenDto.request.requestToken requestToken) throws Exception {
        return oAuth2RequestService.oauthGetTokenRequestService(requestToken);
    }


    @RequestMapping(value = "/check", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String oauthCheck(Principal principal, @RequestBody OauthTokenDto.request.requestToken requestToken) throws Exception {
        return oAuth2RequestService.oauthCheckTokenRequestService(requestToken);
    }

    @PostMapping("/token/create")
    public ResponseEntity<?> createToken(@RequestBody OauthTokenDto.request.createTokenRequest createTokenRequest) {
        oAuth2RequestService.createToken(createTokenRequest);

        CommonDto commonDto = CommonDto.builder()
                .result("SUCCESS")
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return ResponseEntity.ok()
                .headers(header)
                .body(commonDto);
    }
}
