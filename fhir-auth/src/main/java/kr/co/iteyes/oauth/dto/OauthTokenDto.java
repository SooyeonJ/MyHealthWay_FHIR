package kr.co.iteyes.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 토큰 관련 정보
 */
@Data
public class OauthTokenDto {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class response {

        private String access_token;
        private String token_type;
        private String refresh_token;
        private long expires_in;
        private String scope;
        private String error;
        private String error_description;

    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class checkToken {

        private Boolean active;
        private long exp;
        private String user_name;
        private List<String> authorities;
        private String client_id;
        private List<String> scope;
        private String error;
        private String error_description;

    }

    @Data
    public static class request {

        @Data
        public static class accessToken {
            public String code;
            private String grant_type;
            private String redirect_uri;

            public Map getMapData() {
                Map map = new HashMap();
                map.put("code", code);
                map.put("grant_type", grant_type);
                map.put("redirect_uri", redirect_uri);
                return map;
            }
        }

        @Data
        public static class refreshToken {
            private String refreshToken;
            private String grant_type;

            public Map getMapData() {
                Map map = new HashMap();
                map.put("refresh_token", refreshToken);
                map.put("grant_type", grant_type);
                return map;
            }
        }

        @Data
        public static class checkToken {
            private String token;
            private List<String> authorities;
            private String client_id;
            private List<String> scope;

            public Map getMapData() {
                Map map = new HashMap();
                map.put("token", token);
                map.put("scope", scope);
                map.put("authorities", authorities);

                return map;
            }
        }

        @Data
        public static class requestToken {
            private String token;
            private String clientId;
            private String clientSecretKey;

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public String getClientId() {
                return clientId;
            }

            public void setClientId(String clientId) {
                this.clientId = clientId;
            }

            public String getClientSecretKey() {
                return clientSecretKey;
            }

            public void setClientSecretKey(String clientSecretKey) {
                this.clientSecretKey = clientSecretKey;
            }
        }

        @Getter
        public static class createTokenRequest {
            private String clientId;
            private String clientSecretKey;
        }
    }
}
