package kr.co.iteyes.fhirmeta.dto;

import javax.validation.constraints.NotBlank;
import lombok.*;

public class FhrRscUpdtDto {

    @Getter
    public static class RequestDelete {
        @NotBlank
        private String tokenHashSpSystem;
        @NotBlank
        private String tokenHashSpValue;
    }

    @Getter
    public static class Resource {
        private String resourceType;
        private Long resourceId;
        public void setResourceId(Object value) {
            if (value != null) resourceId = Long.parseLong(value.toString());
        }
    }

}
