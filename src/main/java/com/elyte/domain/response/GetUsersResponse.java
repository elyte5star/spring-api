package com.elyte.domain.response;

import com.elyte.domain.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor(staticName = "build")
public class GetUsersResponse {

    private Status status;
    private Iterable<User> users;

}
