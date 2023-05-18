package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Stateless
public class ReCAPTCHA {

    @Inject
    @Property("recaptcha.secret.key")
    private String recaptchaSecretKey;
    @Inject
    @Property("recaptcha.site.verify")
    private String recaptchaSiteVerify;
    public boolean verifyRecaptcha(final String recaptchaResponse) {
        try {
            String url = recaptchaSiteVerify;
            String parameters = "secret=" + recaptchaSecretKey + "&response=" + recaptchaResponse;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(postData.length));

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postData);
                outputStream.flush();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining());

                return response.contains("\"success\": true");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}