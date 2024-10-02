package com.elyte.domain;
import lombok.Data;


@Data
public class MsalProperties {
    String clientId;
    String loginAuthority;
    boolean enabled;
}
