package com.elyte.domain;
import java.util.List;
import lombok.Data;

@Data
public class GoogleProperties {
    List<String> clientIds;
    boolean enabled;
}
