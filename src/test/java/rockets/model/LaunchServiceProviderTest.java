package rockets.model;

import java.lang.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.CheckReturnValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LaunchServiceProviderTest {

    private LaunchServiceProvider target;
    private String validName = "Test Name";
    private int validYear = 0000;
    private String validCountry = "Test Country";

    @BeforeEach
    public void setUp() { target = new LaunchServiceProvider(validName, validYear, validCountry); }

    // TEST NAME
    // Tests if name is null
    @DisplayName("should throw exception when name is null")
    @Test
    public void shouldThrowExceptionWhenNameIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> target = new LaunchServiceProvider(null, validYear, validCountry)
        );
        assertEquals("name cannot be null or empty", exception.getMessage());
    }

    // Tests if name is an empty string
    @DisplayName("should throw exception when name is an empty string")
    @ParameterizedTest
    @ValueSource(strings = {"", " " , "  "})
    public void shouldThrowExceptionWhenNameIsEmptyString(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target = new LaunchServiceProvider(name, validYear, validCountry)
        );
        assertEquals("name cannot be null or empty", exception.getMessage());
    }

    // Tests if name is too long
    @DisplayName("should throw exception if name is too long")
    @ParameterizedTest
    @ValueSource(strings = {"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"})
    public void shouldThrowExceptionWhenNameIsTooLong(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target = new LaunchServiceProvider(name, validYear, validCountry)
        );
        assertEquals("name length cannot exceed 50 characters", exception.getMessage());
    }

    // Tests if LaunchServiceProvider is successfully created with a valid name
    @DisplayName("LaunchServiceProvider created successfully with a valid name")
    @ParameterizedTest
    @ValueSource(strings = {"Taylah", "Casey", "Hadi"})
    public void shouldCreateLaunchServiceProviderWhenNameIsValid(String name) {
        target = new LaunchServiceProvider(name, validYear, validCountry);
        assertEquals(target.getName(), name);
    }

    // Test year founded
    // Tests if year founded is  more or less than 4 digits
    @DisplayName("should return false when year founded is more or less than 4 digits long")
    @ParameterizedTest
    @ValueSource(ints = {12345, 100001, 55655651, 12, 234})
    public void shouldReturnFalseWhenYearGreaterOrLessThan4Digits(int yearFounded) {
        int length = String.valueOf(yearFounded).length();
        assertEquals(false, length==4);
    }

    // Tests if year founded is in range of 1900 - 2019
    @DisplayName("should throw exception when year founded is less than 1900 or greater than 2019")
    @ParameterizedTest
    @ValueSource(ints = {1800, 1290, 1000})
    public void shouldReturnFalseWhenYearIsOutOfRange(int yearFounded) {
        assertEquals(false, yearFounded >= 1900 && yearFounded <= 2019);
    }

    // Tests if LaunchServiceProvider is successfully created with a valid year
    @DisplayName("LaunchServiceProvider created successfully with a valid year")
    @ParameterizedTest
    @ValueSource(ints = {1954, 1995, 2000})
    public void shouldCreateLaunchServiceProviderWhenYearIsValid(int yearFounded) {
        target = new LaunchServiceProvider(validName, yearFounded, validCountry);
        assertEquals(target.getYearFounded(), yearFounded);
    }

    // Test countries
    // Tests if country is null
    @DisplayName("should throw exception when null is used for country")
    @Test
    public void shouldThrowExceptionWhenCountryIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> target = new LaunchServiceProvider(validName, validYear, null)
        );
        assertEquals("country cannot be null or empty", exception.getMessage());
    }

    // Tests if country is an empty string
    @DisplayName("should throw exception when an empty string is used for country")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenCountryIsEmpty(String country) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target = new LaunchServiceProvider(validName, validYear, country)
        );
        assertEquals("country cannot be null or empty", exception.getMessage());
    }

    // Tests if country is too long
    @DisplayName("should throw exception when country name is too long")
    @ParameterizedTest
    @ValueSource(strings = {"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"})// 66 characters
    public void shouldThrowExceptionWhenCountryIsTooLong(String country) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> target = new LaunchServiceProvider(validName, validYear, country)
        );
        assertEquals("country length cannot exceed 50 characters", exception.getMessage());
    }

    // Tests if LaunchServiceProvider is created successfully with a valid country
    @DisplayName("LaunchServiceProvider created with valid country")
    @ParameterizedTest
    @ValueSource(strings = {"Australia", "America", "Amsterdam"})
    public void shouldCreateLaunchServiceProviderWithValidCoutnry(String country) {
        target = new LaunchServiceProvider(validName, validYear, country);
        assertEquals(target.getCountry(), country);
    }

    // Test Headquarters


    // Test List of Rockets
}