package rockets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserUnitTest {
    private User target;
    private String validPassword = "P@55w0RD";

    @BeforeEach
    public void setUp() {
        target = new User();
    }

    // TESTING FIRSTNAME

    @DisplayName("should throw exception when an empty string is used for first name")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenFirstNameIsEmpty(String firstName) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target.setFirstName(firstName)
        );
        assertEquals("first name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when a null value is used for first name")
    @Test
    public void shouldThrowExceptionWhenFirstNameIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> target.setFirstName(null)
        );
        assertEquals("first name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("first name can be successfully set for valid first names")
    @ParameterizedTest
    @ValueSource(strings = {
            "N",
            "n",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
    })
    public void shouldSetFirstNameWhenFirstNameIsValid(String firstName) {
        target.setFirstName(firstName);
        assertEquals(target.getFirstName(), firstName);
    }

    @DisplayName("should throw exception when first name is too long")
    @ParameterizedTest
    @ValueSource(strings = {
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",  // 51 characters
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"  // 51 characters
    })
    public void shouldThrowExceptionWhenFirstNameIsTooLong(String firstName) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target.setFirstName(firstName)
        );
        assertEquals("first name length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING LASTNAME

    @DisplayName("should throw exception when an empty string is used for last name")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenLastNameIsEmpty(String lastName) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target.setLastName(lastName)
        );
        assertEquals("last name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when a null value is used for last name")
    @Test
    public void shouldThrowExceptionWhenLastNameIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> target.setLastName(null)
        );
        assertEquals("last name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("last name can be successfully set for valid last names")
    @ParameterizedTest
    @ValueSource(strings = {
            "N",
            "n",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
    })
    public void shouldSetLastNameWhenLastNameIsValid(String lastName) {
        target.setLastName(lastName);
        assertEquals(target.getLastName(), lastName);
    }

    @DisplayName("should throw exception when last name is too long")
    @ParameterizedTest
    @ValueSource(strings = {
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",  // 51 characters
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"  // 51 characters
    })
    public void shouldThrowExceptionWhenLastNameIsTooLong(String lastName) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target.setLastName(lastName)
        );
        assertEquals("last name length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING EMAIL

    @DisplayName("should throw exception when pass a empty email address to setEmail function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenEmailIsEmpty(String email) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setEmail(email));
        assertEquals("email cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass null to setEmail function")
    @Test
    public void shouldThrowExceptionWhenEmailIsNull() {
        NullPointerException exception = assertThrows(
            NullPointerException.class, () -> target.setEmail(null));
        assertEquals("email cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when invalid email is passed to setEmail function")
    @ParameterizedTest
    @ValueSource(strings = {
            "invalidEmail",  // no @ symbol
            "invalid email@test.com",  // contains spaces
            "invalidemail.com",  // no @ symbol
            "invalid@email@test.com",  // two @ symbols

    })
    public void shouldThrowExceptionWhenEmailHasNoAtSymbol(String email) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setEmail(email));
        assertEquals("email is invalid", exception.getMessage());
    }

    @DisplayName("email should be successfully set when input is valid")
    @Test
    public void shouldSetEmailForValidEmail() {
        String email = "abc@example.com";
        target.setEmail(email);
        assertEquals(email, target.getEmail());
    }

    // TESTING PASSWORD

    @DisplayName("should throw exceptions when passing an empty string to setPassword function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetPasswordToEmpty(String password) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target.setPassword(password)
        );
        assertEquals("password cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exceptions when passing a null password to setPassword function")
    @Test
    public void shouldThrowExceptionWhenSetPasswordToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setPassword(null));
        assertEquals("password cannot be null or empty", exception.getMessage());
    }

    @DisplayName("password can be set when input is valid")
    @ParameterizedTest
    @ValueSource(strings = {
            "P",
            "p",
            "PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP",  // 50 characters
            "pppppppppppppppppppppppppppppppppppppppppppppppppp"  // 50 characters
    })
    public void shouldSetPasswordWhenInputIsValid(String password) {
        target.setPassword(password);
        assertEquals(target.getPassword(), password);
    }

    @DisplayName("should throw exception when password is too long in setPassword")
    @ParameterizedTest
    @ValueSource(strings = {
            "PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP",  // 51 characters
            "ppppppppppppppppppppppppppppppppppppppppppppppppppp"  // 51 characters
    })
    public void shouldThrowExceptionWhenPasswordIsTooLong(String password) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target.setPassword(password)
        );
        assertEquals("password length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING IS PASSWORD MATCH
    @DisplayName("should return true when password matches the current user password")
    @ParameterizedTest
    @ValueSource(strings = {
            "P@55w0RD",  // exact match
            "P@55w0RD ",  // padded spaces should still match
            "P@55w0RD  ",
            " P@55w0RD",
            "  P@55w0RD"
    })
    public void shouldReturnTrueWhenPasswordsMatch(String password) {
        target.setPassword(validPassword);
        assertTrue(target.isPasswordMatch(password));
    }

    @DisplayName("should return false when password does not match the current user password")
    @ParameterizedTest
    @ValueSource(strings = {
            "P@55 w0RD",
            "p@55W0RD ",
            "differentPassword"
    })
    public void shouldReturnFalseWhenPasswordsDoNotMatch(String password) {
        target.setPassword(validPassword);
        assertFalse(target.isPasswordMatch(password));
    }

    @DisplayName("should throw exception when password is empty")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenInputPasswordIsEmpty(String password) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> target.isPasswordMatch(password));
        assertEquals("password cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when password is null")
    @Test
    public void shouldReturnFalseWhenInputPassewordIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> target.isPasswordMatch(null));
        assertEquals("password cannot be null or empty", exception.getMessage());
    }

    // TESTING EQUALS

    @DisplayName("should return true when two users have the same email")
    @Test
    public void shouldReturnTrueWhenUsersHaveTheSameEmail() {
        String email = "abc@example.com";
        target.setEmail(email);
        User anotherUser = new User();
        anotherUser.setEmail(email);
        assertTrue(target.equals(anotherUser));
    }

    @DisplayName("should return false when two users have different emails")
    @Test
    public void shouldReturnFalseWhenUsersHaveDifferentEmails() {
        target.setEmail("abc@example.com");
        User anotherUser = new User();
        anotherUser.setEmail("def@example.com");
        assertFalse(target.equals(anotherUser));
    }
}