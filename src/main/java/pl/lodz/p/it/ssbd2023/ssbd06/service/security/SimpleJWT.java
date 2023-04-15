package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import java.util.Set;

public record SimpleJWT(String login, Set<String> roles) {

}
