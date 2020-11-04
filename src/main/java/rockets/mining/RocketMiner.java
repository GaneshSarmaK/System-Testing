package rockets.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class RocketMiner {
    private static Logger logger = LoggerFactory.getLogger(RocketMiner.class);

    private DAO dao;

    public RocketMiner(DAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the top-k most active rockets, as measured by number of completed launches.
     *
     * @param k the number of rockets to be returned.
     * @return the list of k most active rockets.
     */
    public List<Rocket> mostLaunchedRockets(int k) {
        notNull(k);             // Ensures input is not null

        Collection<Launch> launches = dao.loadAll(Launch.class);
        Map<Rocket, Integer> occurrence = new HashMap<>();

        // Iterate through launches and find occurrences of each rocket
        for (Launch l : launches) {
            Launch.LaunchOutcome outcome = l.getLaunchOutcome();
            if (outcome.toString().equals("SUCCESSFUL")) {      // If launch is successful, count occurrences of rocket
                Rocket rocket = l.getLaunchVehicle();
                if (occurrence.containsKey(rocket)) {
                    occurrence.put(rocket, occurrence.get(rocket) + 1);
                } else {
                    occurrence.put(rocket, 1);
                }
            }
        }

        isTrue(k <= occurrence.entrySet().stream().count(), "Input integer is higher than the number of rockets");
        // Sort the results based on highest key value, and return first k elements
        return occurrence.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(k).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns the top-k most reliable launch service providers as measured
     * by percentage of successful launches.
     *
     * @param k the number of launch service providers to be returned.
     * @return the list of k most reliable ones.
     */
    public List<LaunchServiceProvider> mostReliableLaunchServiceProviders(int k) {
        notNull(k);              // Ensures input is not null

        Collection<Launch> launches = dao.loadAll(Launch.class);
        Map<LaunchServiceProvider, Integer> occurrence = new HashMap<>();

        // Iterate through launches and compare manufacturers
        for (Launch l : launches) {
            Launch.LaunchOutcome outcome = l.getLaunchOutcome();

            if (outcome.toString().equals("SUCCESSFUL")) {      // If launch was successful, increase launch service provider count
                LaunchServiceProvider provider = l.getLaunchServiceProvider();
                if (occurrence.containsKey(provider)) {
                    occurrence.put(provider, occurrence.get(provider) + 1);
                } else {
                    occurrence.put(provider, 1);
                }
            }
        }

        isTrue(k <= occurrence.entrySet().stream().count(), "Input integer is higher than the number of launch service providers");
        // Sort the results based on highest key value, and return first k elements
        return occurrence.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(k).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns the top-k most recent launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most recent launches.
     */
    public List<Launch> mostRecentLaunches(int k) {
        notNull(k);
        logger.info(String.format("find most recent %d launches", k));
        Collection<Launch> launches = dao.loadAll(Launch.class);
        isTrue(k <= launches.size(), "Input integer is higher than the number of launches");
        Comparator<Launch> launchDateComparator = Comparator.comparing(Launch::getLaunchDate).reversed();
        return launches.stream().sorted(launchDateComparator).limit(k).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns the top-k most expensive launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most expensive launches.
     */
    public List<Launch> mostExpensiveLaunches(int k) {
        notNull(k);
        Collection<Launch> launches = dao.loadAll(Launch.class);
        isTrue(k <= launches.size(), "Input integer is higher than the number of launches");
        Comparator<Launch> launchPriceComparator = Comparator.comparing(Launch::getPrice).reversed();
        //limit(k) returns k number of launches
        return launches.stream().sorted(launchPriceComparator).limit(k).collect(Collectors.toList());
    }
    // k = 1 should return

    /**
     * <p>
     * Returns the dominant country who has the most launched rockets in an orbit.
     *
     * @param orbit the orbit
     * @return the country who sends the most payload to the orbit
     */
    public String dominantCountry(String orbit) {
        notNull(orbit);

        // for all launches, get the rockets
        List<Rocket> rockets = dao.loadAll(Launch.class)
                .stream()
                .filter(launch -> launch.getOrbit().equals(orbit))
                .map(Launch::getLaunchVehicle)
                .collect(Collectors.toList());

        isTrue(!rockets.isEmpty(), "There are no rockets in this orbit.");

        // count the number of occurrences per country
        Map<String, Long> countries = rockets
                .stream()
                .collect(Collectors.groupingBy(Rocket::getCountry, Collectors.counting()));

        // return the country that occurred the most
        Map.Entry<String, Long> dominantCountry = countries.entrySet()
                .stream()
                .max((country1, country2) -> country1.getValue() > country2.getValue() ? 1 : -1)
                .orElse(null);

        return dominantCountry == null ? null : dominantCountry.getKey();
    }

    /**
     * <p>
     * Returns a list of launch service provider that has the top-k highest
     * sales revenue in a year.
     *
     * @param k the number of launch service provider.
     * @param year the year in request
     * @return the list of k launch service providers who has the highest sales revenue.
     */
    public List<LaunchServiceProvider> highestRevenueLaunchServiceProviders(int k, int year) {
        notNull(k);
        notNull(year);

        isTrue(year <= Calendar.getInstance().get(Calendar.YEAR), "Input integer year is beyond a valid year of launches");

        List<Launch> launchesByYear = dao.loadAll(Launch.class)
                .stream()
                .filter(launch -> launch.getLaunchDate().getYear() == year)
                .collect(Collectors.toList());

        isTrue(k <= launchesByYear.size(),"Input integer is higher than the number of launches");
        isTrue(!launchesByYear.isEmpty(), "There are no launches in year " + year);

        // sum the price of launches per service provider
        Map<LaunchServiceProvider, BigDecimal> serviceProvider = new HashMap<>();
        for (Launch l: launchesByYear){
            LaunchServiceProvider provider = l.getLaunchServiceProvider();
            if (serviceProvider.containsKey(provider)){
                serviceProvider.put(provider, serviceProvider.get(provider).add(l.getPrice()));
            } else{
                serviceProvider.put(provider, l.getPrice());
            }
        }

        return serviceProvider.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(k).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns a list of launches from a specific country
     *
     * @param country the name of the country.
     * @return the list of launches from country, country
     */
    public List<Launch> launchesFromCountry(String country) {
        notNull(country);
        List<Launch> countryLaunches = dao.loadAll(Launch.class)
                .stream()
                .filter(launch -> launch.getLaunchVehicle().getCountry().equals(country))
                .collect(Collectors.toList());

        // no launches
        isTrue(!countryLaunches.isEmpty(), "There are no launches from this country");

        // return the list
        return countryLaunches;
    }

//TODO this seems to be not implemented cross check once again
    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns the successful launch rate in <code>year</code> measured by the
     * number of successful launches and total number of launches
     *
     * @param year the year
     * @return the successful launch rate in BigDecimal with scale 2.
     */
    public BigDecimal successfulLaunchRateInYear(int year) {
        return BigDecimal.valueOf(0);
    }
}
