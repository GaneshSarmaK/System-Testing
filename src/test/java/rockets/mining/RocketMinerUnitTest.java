package rockets.mining;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.dataaccess.neo4j.Neo4jDAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RocketMinerUnitTest {
    Logger logger = LoggerFactory.getLogger(RocketMinerUnitTest.class);

    private DAO dao;
    private RocketMiner miner;
    private List<Rocket> rockets;
    private List<LaunchServiceProvider> lsps;
    private List<Launch> launches;
    private List<Launch.LaunchOutcome> outcomes;
    private List<String> countries;

    @BeforeEach
    public void setUp() {
        dao = mock(Neo4jDAO.class);
        miner = new RocketMiner(dao);
        rockets = Lists.newArrayList();

        lsps = Arrays.asList(
                new LaunchServiceProvider("ULA", 1990, "USA"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("ESA", 1975, "Europe "),
                new LaunchServiceProvider("ULA", 1991, "USA"),
                new LaunchServiceProvider("ULA", 1992, "USA"),
                new LaunchServiceProvider("SpaceX", 2003, "USA"),
                new LaunchServiceProvider("SpaceX", 2004, "USA"),
                new LaunchServiceProvider("ESA", 1976, "Europe "),
                new LaunchServiceProvider("ESA", 1977, "Europe "),
                new LaunchServiceProvider("ESA", 1978, "Europe ")
        );

        outcomes = Arrays.asList(
                Launch.LaunchOutcome.SUCCESSFUL, Launch.LaunchOutcome.SUCCESSFUL,
                Launch.LaunchOutcome.SUCCESSFUL, Launch.LaunchOutcome.SUCCESSFUL, Launch.LaunchOutcome.SUCCESSFUL,
                Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.FAILED,
                Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.SUCCESSFUL
        );

        countries = Arrays.asList("USA", "Japan", "Australia", "NZ", "Ireland");

        // index of lsp of each rocket
        int[] lspIndex = new int[]{0, 0, 0, 1, 1};
        // index of countries of each rocket
        int[] countryIndex = new int[]{1, 1, 2, 0, 4};
        // 5 rockets
        for (int i = 0; i < 5; i++) {
            rockets.add(new Rocket("rocket_" + i, countries.get(countryIndex[i]), lsps.get(lspIndex[i])));
        }
        // month of each launch
        int[] months = new int[]{1, 6, 4, 3, 4, 11, 6, 5, 12, 5};

        // index of rocket of each launch
        int[] rocketIndex = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};

        BigDecimal[] prices = new BigDecimal[]{
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(20000),
                BigDecimal.valueOf(1000000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(15000),
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(1000.50),
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(90000000),
                BigDecimal.valueOf(10000.99)
        };

        // 10 launches
        launches = IntStream.range(0, 10).mapToObj(i -> {
            logger.info("create " + i + " launch in month: " + months[i]);
            Launch l = new Launch();
            l.setLaunchDate(LocalDate.of(2017, months[i], 1));
            l.setLaunchVehicle(rockets.get(rocketIndex[i]));
            l.setLaunchSite("VAFB");
            l.setOrbit("LEO");
            l.setPrice(prices[i]);
            l.setLaunchServiceProvider(lsps.get(i));
            l.setLaunchOutcome(outcomes.get(i));
            spy(l);
            return l;
        }).collect(Collectors.toList());

        // adding a launch that uses the same values as the last launch in the list to reach more conditions
        Launch l = new Launch();
        l.setLaunchDate(LocalDate.of(2017, months[9], 1));
        l.setLaunchVehicle(rockets.get(rocketIndex[9]));
        l.setLaunchSite("VAFB");
        l.setOrbit("LEO");
        l.setPrice(prices[9]);
        l.setLaunchServiceProvider(lsps.get(9));
        l.setLaunchOutcome(outcomes.get(9));
        spy(l);
        launches.add(l);

        rockets.get(0).setLaunches(Sets.newHashSet(launches.subList(0, 4)));
        rockets.get(1).setLaunches(Sets.newHashSet(launches.subList(4, 7)));
        rockets.get(2).setLaunches(Sets.newHashSet(launches.subList(7, 9)));
        rockets.get(3).setLaunches(Sets.newHashSet(launches.get(9)));
    }

    // Testing mostRecentLaunches
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 9, 10, 11})
    public void shouldReturnTopMostRecentLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        sortedLaunches.sort((a, b) -> -a.getLaunchDate().compareTo(b.getLaunchDate()));
        List<Launch> loadedLaunches = miner.mostRecentLaunches(k);
        assertEquals(k, loadedLaunches.size());
        assertEquals(sortedLaunches.subList(0, k), loadedLaunches);
    }

    // Testing MostLaunchedRockets(int k)
    // Asserts that the length of the list returned is equal to k
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void shouldReturnListContainingKLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        assertEquals(k, miner.mostLaunchedRockets(k).size());
    }

    // Asserts that input is too high
    @ParameterizedTest
    @ValueSource(ints = {4, 5})
    public void shouldThrowErrorForTooHighIntMostLaunchedRockets(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.mostLaunchedRockets(k)
        );
        assertEquals("Input integer is higher than the number of rockets", exception.getMessage());
    }

    // Testing MostReliableLaunchServiceProvider(int k)
    // Asserts that the length of the list returned is equal to k
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void shouldReturnListContainingKMostReliableProviders(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        assertEquals(k, miner.mostReliableLaunchServiceProviders(k).size());
    }

    // Asserts that input is too high
    @ParameterizedTest
    @ValueSource(ints = {15, 20})
    public void shouldThrowErrorForTooHighIntMostReliableProviders(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.mostReliableLaunchServiceProviders(k)
        );
        assertEquals("Input integer is higher than the number of launch service providers", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {12, 20, 50})
    public void shouldThrowErrorForTooHighIntMostRecentLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.mostRecentLaunches(k)
        );
        assertEquals("Input integer is higher than the number of launches", exception.getMessage());
    }

    // Testing dominantCountry
    @Test
    public void shouldReturnDominantCountry () {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        assertEquals(miner.dominantCountry("LEO"), "Japan");
    }

    @Test
    public void shouldReturnDominantCountryWhenNoRocketsExist () {
        // rather than use the mocked data, create an empty one
        Neo4jDAO daoEmpty = mock(Neo4jDAO.class);
        RocketMiner minerEmpty = new RocketMiner(daoEmpty);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> minerEmpty.dominantCountry("LEO")
        );
        assertEquals("There are no rockets in this orbit.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenNullOrbitForDominantCountry () {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        assertThrows(NullPointerException.class, () -> miner.dominantCountry(null));
    }

    // Tests for Most Expensive Launch
    // Case where the the input is beyond the number of launches
    @ParameterizedTest
    @ValueSource(ints = {12, 20, 50})
    public void shouldThrowErrorForTooHighIntMostExpensiveLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.mostExpensiveLaunches(k)
        );
        assertEquals("Input integer is higher than the number of launches", exception.getMessage());
    }

    // Case where it returns correctly
    @Test
    public void shouldReturnMostExpensiveLaunches(){
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        // where entry 8 (index 9) is the most expensive launch
        List<Launch> expectedResult = Arrays.asList(launches.get(8));
        assertEquals(miner.mostExpensiveLaunches(1), expectedResult);
    }

    // Tests for Highest Revenue Launch Service Provider
    // Case where K is beyond the number of launches
    @ParameterizedTest
    @ValueSource(ints = {12, 20, 50})
    public void shouldThrowErrorForTooHighIntHighestRevenueLaunchServiceProvider(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.highestRevenueLaunchServiceProviders(k, 2017)
        );
        assertEquals("Input integer is higher than the number of launches", exception.getMessage());
    }

    // Case where the year is beyond this year as launches can't happen
    @ParameterizedTest
    @ValueSource(ints = {2020, 9999, 999999})
    public void shouldThrowErrorForTooHighYearHighestRevenueLaunchServiceProvider(int year) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.highestRevenueLaunchServiceProviders(0, year)
        );
        assertEquals("Input integer year is beyond a valid year of launches", exception.getMessage());
    }

    // Case where the year being requested has no launches
    @ParameterizedTest
    @ValueSource(ints = {1, 120, 1300})
    public void shouldThrowErrorIfNoLaunchesInYearHighestRevenueLaunchServiceProvider(int year) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.highestRevenueLaunchServiceProviders(0, year)
        );
        assertEquals("There are no launches in year " + year, exception.getMessage());
    }

    // Case where it returns the highest revenue launch service provider
    @Test
    public void shouldReturnHighestRevenueLaunchServiceProvider() {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<LaunchServiceProvider> expectedResult =  Arrays.asList(lsps.get(8));
        assertEquals(miner.highestRevenueLaunchServiceProviders(1, 2017), expectedResult);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Australia", "USA", "Japan"})
    public void shouldReturnLaunchesFromCountry(String aCountry) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> launchesByCountry = miner.launchesFromCountry(aCountry);

        assertEquals(launchesByCountry.size() > 0, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"NZ", "Fiji", "Lebanon"})
    public void shouldThrowErrorWhenNoLaunchesByCountryForLaunchesFromCountry(String aCountry) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> miner.launchesFromCountry(aCountry)
        );
        assertEquals("There are no launches from this country", exception.getMessage());
    }
}