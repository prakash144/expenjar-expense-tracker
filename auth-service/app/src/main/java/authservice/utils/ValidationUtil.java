package authservice.utils;

import authservice.model.UserInfoDto;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Regex for basic email validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Regex for international phone numbers (10 to 15 digits)
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,15}$");

    public static void validateUserAttributes(UserInfoDto user) {
        if (user == null) {
            throw new IllegalArgumentException("User info cannot be null");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }

        if (StringUtils.isBlank(user.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }

        if (StringUtils.isBlank(user.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (StringUtils.isBlank(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (!PHONE_PATTERN.matcher(user.getPhoneNumber()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
}
