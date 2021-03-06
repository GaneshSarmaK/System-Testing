package rockets.dataaccess.neo4j;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
import rockets.model.User;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Neo4jDAOUnitTest {
    private static final String TEST_DB = "target/test-data/test-db";

    private static DAO dao;
    private static Session session;
    private static SessionFactory sessionFactory;

    private static LaunchServiceProvider esa;
    private static LaunchServiceProvider spacex;
    private Rocket rocket;
    private User user;

    @BeforeAll
    public void initializeNeo4j() {
        EmbeddedDriver driver = createEmbeddedDriver(TEST_DB);

        sessionFactory = new SessionFactory(driver, User.class.getPackage().getName());
        session = sessionFactory.openSession();
        dao = new Neo4jDAO(sessionFactory);
    }

    @BeforeEach
    public void setup() {
        esa = new LaunchServiceProvider("ESA", 1970, "Europe");
        spacex = new LaunchServiceProvider("SpaceX", 2002, "USA");
        rocket = new Rocket("F9", "USA", spacex);
        user = new User();
    }

    private static EmbeddedDriver createEmbeddedDriver(String fileDir) {
        File file = new File(fileDir);
        Configuration configuration = new Configuration.Builder()
                .uri(file.toURI().toString()) // For Embedded
                .build();
        EmbeddedDriver driver = new EmbeddedDriver();
        driver.configure(configuration);
        return driver;
    }

    @Test
    public void shouldCreateNeo4jDAOSuccessfully() {
        assertNotNull(dao);
    }
    
    @Test
    public void shouldCreateARocketSuccessfully() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");
        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull(graphRocket.getId());
        assertEquals(rocket, graphRocket);
        LaunchServiceProvider manufacturer = graphRocket.getManufacturer();
        assertNotNull(manufacturer.getId());
        assertEquals(rocket.getWikilink(), graphRocket.getWikilink());
        assertEquals(spacex, manufacturer);
    }

    @Test
    public void shouldUpdateRocketAttributeSuccessfully() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");

        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull(graphRocket.getId());
        assertEquals(rocket, graphRocket);

        String newLink = "http://adifferentlink.com";
        rocket.setWikilink(newLink);
        dao.createOrUpdate(rocket);
        graphRocket = dao.load(Rocket.class, rocket.getId());
        assertEquals(newLink, graphRocket.getWikilink());
    }

    @Test
    public void shouldNotSaveTwoSameRockets() {
        assertNull(spacex.getId());

        Rocket rocket1 = new Rocket("F9", "USA", spacex);
        Rocket rocket2 = new Rocket("F9", "USA", spacex);
        assertEquals(rocket1, rocket2);
        dao.createOrUpdate(rocket1);
        assertNotNull(spacex.getId());
        Collection<Rocket> rockets = dao.loadAll(Rocket.class);
        assertEquals(1, rockets.size());
        Collection<LaunchServiceProvider> manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, manufacturers.size());
        dao.createOrUpdate(rocket2);
        manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, manufacturers.size());
        rockets = dao.loadAll(Rocket.class);
        assertEquals(1, rockets.size());
    }

    @Test
    public void shouldLoadAllRockets() {
        Set<Rocket> rockets = Sets.newHashSet(
                new Rocket("Ariane4", "France", esa),
                new Rocket("F5", "USA", spacex),
                new Rocket("BFR", "USA", spacex)
        );

        for (Rocket r : rockets) {
            dao.createOrUpdate(r);
        }

        Collection<Rocket> loadedRockets = dao.loadAll(Rocket.class);
        assertEquals(rockets.size(), loadedRockets.size());
        for (Rocket r : rockets) {
            assertTrue(rockets.contains(r));
        }
    }

    @Test
    public void shouldDeleteRocket() {
        // check that there are no rockets initially
        assertEquals(this.session.loadAll(Rocket.class).size(), 0);
        // add a rocket and check that it has been added to the count
        dao.createOrUpdate(rocket);
        assertEquals(this.session.loadAll(Rocket.class).size(), 1);
        // delete the rocket and check that it has definitely been removed from the count
        session.delete(rocket);
        assertEquals(this.session.loadAll(Rocket.class).size(), 0);
    }

    @Test
    public void shouldCreateAUserSuccessfully() {
        user.setFirstName("Test");
        user.setFirstName("Name");
        User curUser = dao.createOrUpdate(user);
        assertNotNull(curUser.getId());
        assertEquals(user, curUser);
    }

    @Test
    public void shouldUpdateUserAttributeSuccessfully() {
        user.setFirstName("Test");
        user.setFirstName("Name");
        User curUser = dao.createOrUpdate(user);
        assertNotNull(curUser.getId());
        assertEquals(user, curUser);

        String newLastName = "New-Name";
        String newEmail = "test@test.com";
        String newPassword = "P@55w0rd";

        user.setLastName(newLastName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);

        dao.createOrUpdate(user);
        curUser = dao.load(User.class, user.getId());
        assertEquals(newLastName, curUser.getLastName());
        assertEquals(newEmail, curUser.getEmail());
        assertEquals(newPassword, curUser.getPassword());
    }

    @Test
    public void shouldNotSaveTwoSameUsers() {
        assertNull(user.getId());

        User user1 = new User();
        user1.setEmail("test@test.com");
        User user2 = new User();
        user2.setEmail("test@test.com");
        assertEquals(user1, user2);
        dao.createOrUpdate(user1);
        assertNotNull(user1.getId());
        Collection<User> users = dao.loadAll(User.class);
        assertEquals(1, users.size());
        dao.createOrUpdate(user2);
        users = dao.loadAll(User.class);
        assertEquals(1, users.size());
    }

    @Test
    public void shouldLoadAllUsers() {
        Set<User> users = Sets.newHashSet(
                new User(),
                new User(),
                new User()
        );

        for (User u : users) {
            dao.createOrUpdate(u);
        }

        Collection<User> loadedUsers = dao.loadAll(User.class);
        assertEquals(users.size(), loadedUsers.size());
        for (User u : users) {
            assertTrue(loadedUsers.contains(u));
        }
    }

    @Test
    public void shouldDeleteUser() {
        // check that there are no rockets initially
        assertEquals(this.session.loadAll(User.class).size(), 0);
        // add a rocket and check that it has been added to the count
        dao.createOrUpdate(user);
        assertEquals(this.session.loadAll(User.class).size(), 1);
        // delete the rocket and check that it has definitely been removed from the count
        session.delete(user);
        assertEquals(this.session.loadAll(User.class).size(), 0);
    }

    @Test
    public void shouldThrowExceptionForDeleteWithNullInput() {
        // check that there are no rockets initially
        assertEquals(this.session.loadAll(Rocket.class).size(), 0);
        assertThrows(NullPointerException.class, () -> session.delete(null));
    }

    @Test
    public void shouldDoNothingWhenNoRocketIsNotInSession() {
        // check that there are no rockets initially
        assertEquals(this.session.loadAll(Rocket.class).size(), 0);
        session.delete(rocket);
        // check that nothing has changed
        assertEquals(this.session.loadAll(Rocket.class).size(), 0);
    }

//    @Test
//    public void shouldThrowExceptionForIllegalArgumentDelete() {
//        assertThrows(IllegalArgumentException.class, () -> session.delete(2));
//        assertThrows(IllegalArgumentException.class, () -> session.delete(3.4));
//        assertThrows(IllegalArgumentException.class, () -> session.delete("String"));
//    }

    @Test
    public void shouldCreateALaunchSuccessfully() {
        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(2017, 1, 1));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchSite("VAFB");
        launch.setOrbit("LEO");
        dao.createOrUpdate(launch);

        Collection<Launch> launches = dao.loadAll(Launch.class);
        assertFalse(launches.isEmpty());
        assertTrue(launches.contains(launch));
    }


    @Test
    public void shouldUpdateLaunchAttributesSuccessfully() {
        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(2017, 1, 1));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchSite("VAFB");
        launch.setOrbit("LEO");
        dao.createOrUpdate(launch);

        Collection<Launch> launches = dao.loadAll(Launch.class);

        Launch loadedLaunch = launches.iterator().next();
        assertNull(loadedLaunch.getFunction());

        launch.setFunction("experimental");
        dao.createOrUpdate(launch);
        launches = dao.loadAll(Launch.class);
        assertEquals(1, launches.size());
        loadedLaunch = launches.iterator().next();
        assertEquals("experimental", loadedLaunch.getFunction());
    }

    @Test
    public void shouldCreateLaunchServiceProviderSuccessfully() {
        LaunchServiceProvider lsp = dao.createOrUpdate(esa);
        assertNotNull(lsp.getId());
        assertEquals(esa, lsp);
    }

    // Can't update any of the relevant attributes for LaunchServiceProvider

    @Test
    public void shouldNotSaveTwoSameLaunchServiceProviders() {
        assertNull(esa.getId());

        LaunchServiceProvider lsp1 = new LaunchServiceProvider("ESA", 1970, "Europe");;
        LaunchServiceProvider lsp2 = new LaunchServiceProvider("ESA", 1970, "Europe");;
        assertEquals(lsp1, lsp2);
        dao.createOrUpdate(lsp1);
        assertNotNull(lsp1.getId());
        Collection<LaunchServiceProvider> lsps = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, lsps.size());
        dao.createOrUpdate(lsp2);
        lsps = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, lsps.size());
    }

    @Test
    public void shouldLoadAllLaunchServiceProviders() {
        Set<LaunchServiceProvider> lsps = Sets.newHashSet(
                new LaunchServiceProvider("ESA", 1970, "Europe"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("Third", 2005, "Australia")
        );

        for (LaunchServiceProvider l : lsps) {
            dao.createOrUpdate(l);
        }

        Collection<LaunchServiceProvider> loadedLsps = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(lsps.size(), loadedLsps.size());
        for (LaunchServiceProvider l : lsps) {
            assertTrue(loadedLsps.contains(l));
        }
    }

    @Test
    public void shouldDeleteLaunchServiceProvider() {
        // check that there are no rockets initially
        assertEquals(this.session.loadAll(LaunchServiceProvider.class).size(), 0);
        // add a rocket and check that it has been added to the count
        dao.createOrUpdate(esa);
        assertEquals(this.session.loadAll(LaunchServiceProvider.class).size(), 1);
        // delete the rocket and check that it has definitely been removed from the count
        session.delete(esa);
        assertEquals(this.session.loadAll(LaunchServiceProvider.class).size(), 0);
    }

    @Test
    public void shouldDeleteRocketWithoutDeleteLSP() {
        dao.createOrUpdate(rocket);
        assertNotNull(rocket.getId());
        assertNotNull(rocket.getManufacturer().getId());
        assertFalse(dao.loadAll(Rocket.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
        dao.delete(rocket);
        assertTrue(dao.loadAll(Rocket.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
    }

    @Test
    public void shouldSaveARocketBeforeALSPDoesAcrossSessionsNotCreateDuplicateRockets() {
        assertEquals(spacex, rocket.getManufacturer());
        spacex.getRockets().add(rocket);
        dao.createOrUpdate(spacex);
        assertEquals(1, dao.loadAll(Rocket.class).size());

        dao.close();

        initializeNeo4j();

        rocket.setId(null);
        spacex.setId(null);
        dao.createOrUpdate(spacex);
        assertEquals(1, dao.loadAll(Rocket.class).size());
    }

    @AfterEach
    public void tearDown() {
        session.purgeDatabase();
    }

    @AfterAll
    public void closeNeo4jSession() {
        session.clear();
        sessionFactory.close();
    }
}