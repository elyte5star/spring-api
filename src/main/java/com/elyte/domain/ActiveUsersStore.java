package com.elyte.domain;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ActiveUsersStore {

    public List<String> usernames;

    private Date expiryDate;

    
}

