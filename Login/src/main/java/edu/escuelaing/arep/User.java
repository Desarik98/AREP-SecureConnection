package edu.escuelaing.arep;

import lombok.Getter;
import lombok.Setter;

/**
 * A system user
 */

@Getter
@Setter
public class User {

    private String email;
    private String password;

    public User() {
    }


}
