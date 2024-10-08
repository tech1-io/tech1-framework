package tech1.framework.foundation.utilities.browsers.impl;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import tech1.framework.foundation.domain.enums.Toggle;
import tech1.framework.foundation.domain.http.requests.UserAgentDetails;
import tech1.framework.foundation.domain.http.requests.UserAgentHeader;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import tech1.framework.foundation.utilities.browsers.UserAgentDetailsUtility;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static tech1.framework.foundation.domain.constants.FrameworkLogsConstants.FRAMEWORK_UTILITIES_PREFIX;
import static tech1.framework.foundation.domain.constants.FrameworkLogsConstants.LINE_SEPARATOR_INTERPUNCT;
import static tech1.framework.foundation.domain.enums.Status.FAILURE;
import static tech1.framework.foundation.domain.enums.Status.SUCCESS;
import static tech1.framework.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

@Slf4j
public class UserAgentDetailsUtilityImpl implements UserAgentDetailsUtility {

    private final UserAgentParser userAgentParser;
    private final boolean configured;
    private final String exceptionMessage;

    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    public UserAgentDetailsUtilityImpl(ApplicationFrameworkProperties applicationFrameworkProperties) {
        this.applicationFrameworkProperties = applicationFrameworkProperties;
        UserAgentParser userAgentParserOrNull;
        boolean configuredFlag;
        String exceptionMessageOrNull;
        LOGGER.info(LINE_SEPARATOR_INTERPUNCT);
        var userAgentConfigs = this.applicationFrameworkProperties.getUtilitiesConfigs().getUserAgentConfigs();
        LOGGER.info("{} User agent — {}", FRAMEWORK_UTILITIES_PREFIX, Toggle.of(userAgentConfigs.isEnabled()));
        if (userAgentConfigs.isEnabled()) {
            try {
                userAgentParserOrNull = new UserAgentService().loadParser(
                        List.of(
                                BrowsCapField.BROWSER,
                                BrowsCapField.PLATFORM,
                                BrowsCapField.DEVICE_TYPE
                        )
                );
                configuredFlag = true;
                exceptionMessageOrNull = null;
                LOGGER.info("{} User agent configuration status: {}", FRAMEWORK_UTILITIES_PREFIX, SUCCESS);
            } catch (ParseException | IOException ex) {
                LOGGER.error("%s User agent configuration status: %s".formatted(FRAMEWORK_UTILITIES_PREFIX, FAILURE));
                throw new IllegalArgumentException(ex);
            }
        } else {
            userAgentParserOrNull = null;
            configuredFlag = false;
            exceptionMessageOrNull = contactDevelopmentTeam("User agent configuration failure");
        }
        LOGGER.info(LINE_SEPARATOR_INTERPUNCT);
        this.userAgentParser = userAgentParserOrNull;
        this.configured = configuredFlag;
        this.exceptionMessage = exceptionMessageOrNull;
    }

    @Override
    public UserAgentDetails getUserAgentDetails(UserAgentHeader userAgentHeader) {
        if (!this.applicationFrameworkProperties.getUtilitiesConfigs().getUserAgentConfigs().isEnabled() || !this.configured) {
            return UserAgentDetails.unknown(this.exceptionMessage);
        }
        var capabilities = this.userAgentParser.parse(userAgentHeader.getValue());
        return UserAgentDetails.processed(
                capabilities.getBrowser(),
                capabilities.getPlatform(),
                capabilities.getDeviceType()
        );
    }
}
