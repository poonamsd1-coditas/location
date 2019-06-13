package com.location.location.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final FourSquare fourSquare = new FourSquare();
//    private final GoogleGeocode google = new GoogleGeocode();

    public FourSquare getFourSquare() { return fourSquare; }
//    public GoogleGeocode getGoogle() { return  google; }

//    public static class GoogleGeocode {
//        private String googleKey;
//        private String apiPath;
//
//        public String getGoogleKey() {
//            return googleKey;
//        }
//
//        public void setGoogleKey(String googleKey) {
//            this.googleKey = googleKey;
//        }
//
//        public String getApiPath() {
//            return apiPath;
//        }
//
//        public void setApiPath(String apiPath) {
//            this.apiPath = apiPath;
//        }
//    }

    public static class FourSquare {
        private String clientId;
        private String clientSecret;
        private String apiPath;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getApiPath() {
            return apiPath;
        }

        public void setApiPath(String apiPath) {
            this.apiPath = apiPath;
        }
    }
}
