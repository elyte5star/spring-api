package com.elyte.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum JobType {
    @JsonProperty("NOOP")
    NOOP,
    @JsonProperty("SEARCH")
    SEARCH,
    @JsonProperty("BOOKING")
    BOOKING
    
}
