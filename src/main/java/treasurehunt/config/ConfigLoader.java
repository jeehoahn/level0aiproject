package treasurehunt.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ConfigLoader {

    private final ObjectMapper mapper;

    private final GameConfig gameConfig;
    private final Map<String, CountryConfig> countryConfigs;
    private final Map<String, AnimalConfig> animalConfigs;
    private final Map<String, ItemConfig> itemConfigs;
    private final Map<String, PartnerConfig> partnerConfigs;

    public ConfigLoader() throws IOException {
        this(Path.of(System.getProperty("user.dir"), "data"));
    }

    public ConfigLoader(Path dataDirectory) throws IOException {

        mapper = new ObjectMapper();

        gameConfig = mapper.readValue(
                dataDirectory.resolve("game.json").toFile(),
                GameConfig.class
        );

        countryConfigs = mapper.readValue(
                dataDirectory.resolve("country.json").toFile(),
                new TypeReference<Map<String, CountryConfig>>() {}
        );

        animalConfigs = mapper.readValue(
                dataDirectory.resolve("animals.json").toFile(),
                new TypeReference<Map<String, AnimalConfig>>() {}
        );

        itemConfigs = mapper.readValue(
                dataDirectory.resolve("items.json").toFile(),
                new TypeReference<Map<String, ItemConfig>>() {}
        );

        partnerConfigs = mapper.readValue(
                dataDirectory.resolve("partners.json").toFile(),
                new TypeReference<Map<String, PartnerConfig>>() {}
        );
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public Map<String, CountryConfig> getCountryConfigs() {
        return countryConfigs;
    }

    public Map<String, AnimalConfig> getAnimalConfigs() {
        return animalConfigs;
    }

    public Map<String, ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    public Map<String, PartnerConfig> getPartnerConfigs() {
        return partnerConfigs;
    }
}