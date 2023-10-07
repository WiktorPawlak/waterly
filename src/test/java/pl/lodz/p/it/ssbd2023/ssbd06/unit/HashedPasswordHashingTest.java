//package pl.lodz.p.it.ssbd2023.ssbd06.unit;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//
//import jakarta.security.enterprise.identitystore.PasswordHash;
//import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptPasswordHashImpl;
//
//class HashedPasswordHashingTest {
//
//    private final PasswordHash hashProvider = new BCryptPasswordHashImpl();
//
//    /**
//     * This test is made for technical purposes only. There is no sense to test library methods.
//     * If you want to generate hash for new user in init.sql you can use this method for it.
//     * HashProvider has @Inject @Property member. In tests there is no context, so you need to
//     * execute this method on debug and set this member's value manually. By default, it should be set to 4.
//     */
//    @Test
//    @Disabled
//    void passwordVerifiedSuccessfully() {
//        String password = "jantes123";
//        var hashedPassword = hashProvider.generate(password.toCharArray());
//        assertTrue(hashProvider.verify(password.toCharArray(), hashedPassword));
//    }
//}
