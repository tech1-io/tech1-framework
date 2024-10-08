package tech1.framework.foundation.utilities.geo.functions.mindmax.impl;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import tech1.framework.foundation.domain.enums.Toggle;
import tech1.framework.foundation.domain.geo.GeoLocation;
import tech1.framework.foundation.domain.http.requests.IPAddress;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import tech1.framework.foundation.utilities.geo.facades.GeoCountryFlagUtility;
import tech1.framework.foundation.utilities.geo.functions.mindmax.MindMaxGeoLocationUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.InetAddress;

import static tech1.framework.foundation.domain.constants.FrameworkLogsConstants.FRAMEWORK_UTILITIES_PREFIX;
import static tech1.framework.foundation.domain.constants.FrameworkLogsConstants.LINE_SEPARATOR_INTERPUNCT;
import static tech1.framework.foundation.domain.enums.Status.FAILURE;
import static tech1.framework.foundation.domain.enums.Status.SUCCESS;
import static tech1.framework.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

@Slf4j
public class MindMaxGeoLocationUtilityImpl implements MindMaxGeoLocationUtility {
    private static final String GEO_DATABASE_NAME = "GeoLite2-City.mmdb";

    // Database
    private final DatabaseReader databaseReader;
    // Utilities
    private final GeoCountryFlagUtility geoCountryFlagUtility;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    public MindMaxGeoLocationUtilityImpl(
            ResourceLoader resourceLoader,
            GeoCountryFlagUtility geoCountryFlagUtility,
            ApplicationFrameworkProperties applicationFrameworkProperties
    ) {
        this.geoCountryFlagUtility = geoCountryFlagUtility;
        this.applicationFrameworkProperties = applicationFrameworkProperties;
        LOGGER.info(LINE_SEPARATOR_INTERPUNCT);
        var geoLocationsConfigs = applicationFrameworkProperties.getUtilitiesConfigs().getGeoLocationsConfigs();
        LOGGER.info("{} Geo location {} database — {}", FRAMEWORK_UTILITIES_PREFIX, GEO_DATABASE_NAME, Toggle.of(geoLocationsConfigs.isGeoLiteCityDatabaseEnabled()));
        if (geoLocationsConfigs.isGeoLiteCityDatabaseEnabled()) {
            try {
                var resource = resourceLoader.getResource("classpath:" + GEO_DATABASE_NAME);
                var inputStream = resource.getInputStream();
                this.databaseReader = new DatabaseReader.Builder(inputStream).build();
                LOGGER.info("{} Geo location {} database configuration status: {}", FRAMEWORK_UTILITIES_PREFIX, GEO_DATABASE_NAME, SUCCESS);
            } catch (IOException | RuntimeException ex) {
                LOGGER.error("%s Geo location %s database loading status: %s".formatted(FRAMEWORK_UTILITIES_PREFIX, GEO_DATABASE_NAME, FAILURE));
                LOGGER.error("Please make sure {} database is in classpath", GEO_DATABASE_NAME);
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else {
            this.databaseReader = null;
        }
        LOGGER.info(LINE_SEPARATOR_INTERPUNCT);
    }

    @Override
    public GeoLocation getGeoLocation(IPAddress ipAddress) {
        if (!this.applicationFrameworkProperties.getUtilitiesConfigs().getGeoLocationsConfigs().isGeoLiteCityDatabaseEnabled()) {
            return GeoLocation.unknown(ipAddress, contactDevelopmentTeam("Geo configurations failure"));
        }
        try {
            var inetAddress = InetAddress.getByName(ipAddress.value());
            var response = this.databaseReader.city(inetAddress);
            var countryCode = response.getCountry().getIsoCode();
            var countryFlag = this.geoCountryFlagUtility.getFlagEmojiByCountryCode(countryCode);
            return GeoLocation.processed(
                    ipAddress,
                    response.getCountry().getName(),
                    countryCode,
                    countryFlag,
                    response.getCity().getName()
            );
        } catch (IOException | GeoIp2Exception ex) {
            return GeoLocation.unknown(ipAddress, ex.getMessage());
        }
    }
}
