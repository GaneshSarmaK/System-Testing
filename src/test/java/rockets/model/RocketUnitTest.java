package rockets.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RocketUnitTest {

    private String name;
    private String country;
    private LaunchServiceProvider manufacturer;

    @BeforeEach
    public void setUp() {
        this.name = "BFR";
        this.country = "USA";
        this.manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
    }

    @AfterEach
    public void tearDown() {
    }

    @DisplayName("should create rocket successfully when given right parameters to constructor")
    @Test
    public void shouldConstructRocketObject() {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);
        assertNotNull(bfr);
    }

    // TESTING NAME

    @DisplayName("should throw exception when given an empty string name to constructor")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenNameIsEmpty(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Rocket (name, this.country, this.manufacturer)
        );
        assertEquals("name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when given null name to constructor")
    @Test
    public void shouldThrowExceptionWhenNoNameGiven() {
        assertThrows(
                NullPointerException.class,
                () -> new Rocket(null, this.country, this.manufacturer));
    }

    @DisplayName("rockets can be successfully created with valid names")
    @ParameterizedTest
    @ValueSource(strings = {
            "N",
            "n",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",  // 49 characters
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"  // 50 characters
    })
    public void shouldCreateRocketWhenNameIsValid(String name) {
        Rocket bfr = new Rocket(name, this.country, this.manufacturer);
        assertNotNull(bfr);
    }

    @DisplayName("should throw exception when name is too long")
    @ParameterizedTest
    @ValueSource(strings = {
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",  // 51 characters
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"  // 51 characters
    })
    public void shouldThrowExceptionWhenNameIsTooLong(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Rocket (name, this.country, this.manufacturer)
        );
        assertEquals("name length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING COUNTRY

    @DisplayName("should throw exception when given an empty string country to constructor")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenCountryIsEmpty(String country) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Rocket (this.name, country, this.manufacturer)
        );
        assertEquals("country cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when given null country to constructor")
    @Test
    public void shouldThrowExceptionWhenNoCountryGiven() {
        assertThrows(
                NullPointerException.class,
                () -> new Rocket(this.name, null, this.manufacturer)
        );
    }

    @DisplayName("rockets can be successfully created with valid countries")
    @ParameterizedTest
    @ValueSource(strings = {
            "C",
            "c",
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC",  // 49 characters
            "cccccccccccccccccccccccccccccccccccccccccccccccccc"  //50 characters
    })
    public void shouldCreateRocketWhenCountryIsValid(String country) {
        Rocket bfr = new Rocket(this.name, country, this.manufacturer);
        assertEquals(bfr.getCountry(), country);
    }

    @DisplayName("should throw exception when country is too long")
    @ParameterizedTest
    @ValueSource(strings = {
            "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC",  // 51 characters
            "ccccccccccccccccccccccccccccccccccccccccccccccccccc"  // 51 characters
    })
    public void shouldThrowExceptionWhenCountryIsTooLong(String country) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Rocket (this.name, country, this.manufacturer)
        );
        assertEquals("country length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING MANUFACTURER

    @DisplayName("should throw exception when given null manufacturer to constructor")
    @Test
    public void shouldThrowExceptionWhenNoManufacturerGiven() {
        assertThrows(
                NullPointerException.class,
                () -> new Rocket(this.name, this.country, null)
        );
    }

    // TESTING MASSTOLEO

    @DisplayName("should set rocket massToLEO value")
    @ParameterizedTest
    @ValueSource(strings = {
            "10000",
            "15000",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",  // 49 character
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"  // 50 characters
    })
    public void shouldSetMassToLEOWhenGivenCorrectValue(String massToLEO) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        bfr.setMassToLEO(massToLEO);
        assertEquals(massToLEO, bfr.getMassToLEO());
    }

    @DisplayName("should throw exception passing an empty string to setMassToLEO")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenMassToLEOToEmpty(String massToLEO) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bfr.setMassToLEO(massToLEO)
        );
        assertEquals("massToLEO cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when set massToLEO to null")
    @Test
    public void shouldThrowExceptionWhenSetMassToLEOToNull() {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);
        assertThrows(
                NullPointerException.class,
                () -> bfr.setMassToLEO(null)
        );
    }

    @DisplayName("should throw exception when massToLEO is too long in setMassToLEO")
    @ParameterizedTest
    @ValueSource(strings = {
            "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",  // 51 characters
            "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"  // 51 characters
    })
    public void shouldThrowExceptionWhenMassToLEOIsTooLong(String massToLEO) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bfr.setMassToLEO(massToLEO)
        );
        assertEquals("massToLEO length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING MASSTOGTO

    @DisplayName("should set rocket massToGTO value")
    @ParameterizedTest
    @ValueSource(strings = {
            "10000",
            "15000",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",  // 49 character
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"  // 50 characters
    })
    public void shouldSetMassToGTOWhenGivenCorrectValue(String massToGTO) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        bfr.setMassToGTO(massToGTO);
        assertEquals(massToGTO, bfr.getMassToGTO());
    }

    @DisplayName("should throw exception passing an empty string to setMassToGTO")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenMassToGTOToEmpty(String massToGTO) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bfr.setMassToGTO(massToGTO)
        );
        assertEquals("massToGTO cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when set massToGTO to null")
    @Test
    public void shouldThrowExceptionWhenSetMassToGTOToNull() {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);
        assertThrows(NullPointerException.class, () -> bfr.setMassToGTO(null));
    }

    @DisplayName("should throw exception when massToGTO is too long in setMassToGTO")
    @ParameterizedTest
    @ValueSource(strings = {
            "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",  // 51 characters
            "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"  // 51 characters
    })
    public void shouldThrowExceptionWhenMassToGTOIsTooLong(String massToGTO) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bfr.setMassToGTO(massToGTO)
        );
        assertEquals("massToGTO length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING MASSTOOTHER

    @DisplayName("should set rocket massToOther value")
    @ParameterizedTest
    @ValueSource(strings = {
            "10000",
            "15000",
            "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",  // 49 character
            "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"  // 50 characters
    })
    public void shouldSetMassToOtherWhenGivenCorrectValue(String massToOther) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        bfr.setMassToOther(massToOther);
        assertEquals(massToOther, bfr.getMassToOther());
    }

    @DisplayName("should throw exception passing an empty string to setMassToOther")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenMassToOtherToEmpty(String massToOther) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bfr.setMassToOther(massToOther)
        );
        assertEquals("massToOther cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when set massToOther to null")
    @Test
    public void shouldThrowExceptionWhenSetMassToOtherToNull() {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);
        assertThrows(
                NullPointerException.class,
                () -> bfr.setMassToOther(null)
        );
    }

    @DisplayName("should throw exception when massToOther is too long in setMassToOther")
    @ParameterizedTest
    @ValueSource(strings = {
            "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",  // 51 characters
            "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"  // 51 characters
    })
    public void shouldThrowExceptionWhenMassToOtherIsTooLong(String massToOther) {
        Rocket bfr = new Rocket(this.name, this.country, this.manufacturer);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bfr.setMassToOther(massToOther)
        );
        assertEquals("massToOther length cannot exceed 50 characters", exception.getMessage());
    }

    // TESTING EQUALS

    @DisplayName("should return true when two rockets have the same name, country, and manufacturer")
    @Test
    public void shouldReturnTrueWhenRocketsAreTheSame() {
        Rocket rocket1 = new Rocket(this.name, this.country, this.manufacturer);
        Rocket rocket2 = new Rocket(this.name, this.country, this.manufacturer);
        assertTrue(rocket1.equals(rocket2));
    }

    @DisplayName("should return false when two rockets have two different name")
    @Test
    public void shouldReturnFalseWhenRocketsHaveDifferentNames() {
        Rocket rocket1 = new Rocket(this.name, this.country, this.manufacturer);
        Rocket rocket2 = new Rocket("DIFF", this.country, this.manufacturer);
        assertFalse(rocket1.equals(rocket2));
    }

    @DisplayName("should return false when two rockets have two different countries")
    @Test
    public void shouldReturnFalseWhenRocketsHaveDifferentCountries() {
        Rocket rocket1 = new Rocket(this.name, this.country, this.manufacturer);
        Rocket rocket2 = new Rocket(this.name, "DIFF", this.manufacturer);
        assertFalse(rocket1.equals(rocket2));
    }

    @DisplayName("should return false when two rockets have two different manufacturers")
    @Test
    public void shouldReturnFalseWhenRocketsHaveDifferentManufacturers() {
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX2", 2002, "USA");
        Rocket rocket1 = new Rocket(this.name, this.country, this.manufacturer);
        Rocket rocket2 = new Rocket(this.name, this.country, manufacturer);
        assertFalse(rocket1.equals(rocket2));
    }
}
