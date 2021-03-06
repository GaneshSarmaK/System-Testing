package rockets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.*;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;

@NodeEntity
@CompositeIndex(properties = {"name", "country", "manufacturer"}, unique = true)
public class Rocket extends Entity {
    @Property(name="name")
    private String name;

    @Property(name="country")
    private String country;

    @Relationship(type = "MANUFACTURES", direction = INCOMING)
    private LaunchServiceProvider manufacturer;

    @Property(name="massToLEO")
    private String massToLEO;

    @Property(name="massToGTO")
    private String massToGTO;

    @Property(name="massToOther")
    private String massToOther;

    @Property(name="firstYearFlight")
    private int firstYearFlight;

    @Property(name="lastYearFlight")
    private int latestYearFlight;

    @Relationship(type = "PROVIDES", direction = OUTGOING)
    @JsonIgnore
    private Set<Launch> launches;

    public Rocket() {
        super();
    }

    public Rocket(String name, String country, LaunchServiceProvider manufacturer) {
        notNull(name);
        notNull(country);
        notNull(manufacturer);

        notBlank(name, "name cannot be null or empty");
        notBlank(country, "country cannot be null or empty");
        isTrue(name.length() <= 50, "name length cannot exceed 50 characters");
        isTrue(country.length() <= 50, "country length cannot exceed 50 characters");

        this.name = name;
        this.country = country;
        this.manufacturer = manufacturer;

        this.launches = new LinkedHashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public LaunchServiceProvider getManufacturer() {
        return manufacturer;
    }

    public String getMassToLEO() {
        return massToLEO;
    }

    public String getMassToGTO() {
        return massToGTO;
    }

    public String getMassToOther() {
        return massToOther;
    }

    public int getFirstYearFlight() {
        return firstYearFlight;
    }

    public int getLatestYearFlight() {
        return latestYearFlight;
    }

    public void setMassToLEO(String massToLEO) {
        notBlank(massToLEO, "massToLEO cannot be null or empty");
        isTrue(massToLEO.length() <= 50, "massToLEO length cannot exceed 50 characters");
        this.massToLEO = massToLEO;
    }

    public void setMassToGTO(String massToGTO) {
        notBlank(massToGTO, "massToGTO cannot be null or empty");
        isTrue(massToGTO.length() <= 50, "massToGTO length cannot exceed 50 characters");
        this.massToGTO = massToGTO;
    }

    public void setMassToOther(String massToOther) {
        notBlank(massToOther, "massToOther cannot be null or empty");
        isTrue(massToOther.length() <= 50, "massToOther length cannot exceed 50 characters");
        this.massToOther = massToOther;
    }

    public void setFirstYearFlight(int firstYearFlight) {
        this.firstYearFlight = firstYearFlight;
    }

    public void setLatestYearFlight(int latestYearFlight) {
        this.latestYearFlight = latestYearFlight;
    }

    public Set<Launch> getLaunches() {
        return launches;
    }

    public void setLaunches(Set<Launch> launches) {
        this.launches = launches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(name, rocket.name) &&
                Objects.equals(country, rocket.country) &&
                Objects.equals(manufacturer, rocket.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, manufacturer);
    }

    @Override
    public String toString() {
        return "Rocket{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", massToLEO='" + massToLEO + '\'' +
                ", massToGTO='" + massToGTO + '\'' +
                ", massToOther='" + massToOther + '\'' +
                ", firstYearFlight=" + firstYearFlight +
                ", latestYearFlight=" + latestYearFlight +
                '}';
    }
}
