package tech1.framework.foundation.domain.constants;

import tech1.framework.foundation.domain.enums.Toggle;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FrameworkLogsConstants {
    // =================================================================================================================
    // Line Separators
    // =================================================================================================================
    public static final String LINE_SEPARATOR_INTERPUNCT = "··································································································";

    // =================================================================================================================
    // Prefixes
    // =================================================================================================================
    public static final String FRAMEWORK_PREFIX = "[Tech1 Framework, ";
    public static final String FRAMEWORK_PROPERTIES_PREFIX = FRAMEWORK_PREFIX + "Properties]";
    public static final String FRAMEWORK_UTILITIES_PREFIX = FRAMEWORK_PREFIX + "Utilities]";
    public static final String FRAMEWORK_INCIDENT_PREFIX = FRAMEWORK_PREFIX + "Incident]";
    public static final String FRAMEWORK_B2B_SECURITY_JWT_PREFIX = FRAMEWORK_PREFIX + "B2B SecurityJWT]";
    public static final String FRAMEWORK_SESSION_REGISTRY_PREFIX = FRAMEWORK_PREFIX + "SessionRegistry]";
    public static final String FRAMEWORK_SERVER = FRAMEWORK_PREFIX + "Server]";

    // =================================================================================================================
    // Incidents
    // =================================================================================================================
    public static final String INCIDENT_FEATURE_DISABLED = FRAMEWORK_INCIDENT_PREFIX + " `{}` feature is " + Toggle.DISABLED.getLowerCase();
    public static final String INCIDENT = FRAMEWORK_INCIDENT_PREFIX + " `{}`. incident type: `{}`";
    public static final String INCIDENT_AUTHENTICATION_LOGIN = FRAMEWORK_INCIDENT_PREFIX + " `{}` - /login. Username: `{}`";
    public static final String INCIDENT_AUTHENTICATION_LOGIN_FAILURE = FRAMEWORK_INCIDENT_PREFIX + " `{}` - /login failure. Username: `{}`";
    public static final String INCIDENT_AUTHENTICATION_LOGOUT = FRAMEWORK_INCIDENT_PREFIX + " `{}` - :/logout. Username: `{}`";
    public static final String INCIDENT_REGISTER1 = FRAMEWORK_INCIDENT_PREFIX + " `{}` - /register1. Username: `{}`";
    public static final String INCIDENT_REGISTER1_FAILURE = FRAMEWORK_INCIDENT_PREFIX + " `{}` - /register1 failure. Username: `{}`";
    public static final String INCIDENT_SESSION_REFRESHED = FRAMEWORK_INCIDENT_PREFIX + " `{}` - /refreshToken. Username: `{}`";
    public static final String INCIDENT_SESSION_EXPIRED = FRAMEWORK_INCIDENT_PREFIX + " `{}` - session expired. Username: `{}`";
    public static final String INCIDENT_SYSTEM_RESET_SERVER = FRAMEWORK_INCIDENT_PREFIX + " `{}` - system reset server. Username: `{}`. Status: `{}`";

    // =================================================================================================================
    // SecurityJWT
    // =================================================================================================================
    public static final String SECURITY_JWT_AUTHENTICATION_LOGIN = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}` - /login. Username: `{}`";
    public static final String SECURITY_JWT_AUTHENTICATION_LOGIN_FAILURE = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}` - login failure. Username: `{}`";
    public static final String SECURITY_JWT_AUTHENTICATION_LOGOUT = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}`- /logout. Username: `{}`";
    public static final String SECURITY_JWT_REGISTER1 = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}`- /register1. Username: `{}`";
    public static final String SECURITY_JWT_REGISTER1_FAILURE = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}`- /register1 failure. Username: `{}`";
    public static final String SECURITY_JWT_SESSION_REFRESHED = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}`- /refreshToken. Username: `{}`";
    public static final String SECURITY_JWT_SESSION_EXPIRED = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}`- session expired. Username: `{}`";
    public static final String SECURITY_JWT_SESSION_ADD_USER_REQUEST_METADATA = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}`- Session add user request metadata. Username: `{}`";
    public static final String SECURITY_JWT_SESSION_RENEW_USER_REQUEST_METADATA = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " `{}`- Session renew user request metadata. Username: `{}`. Session: `{}`";

    // =================================================================================================================
    // SessionRegistry
    // =================================================================================================================
    public static final String SESSION_REGISTRY_REGISTER_SESSION = FRAMEWORK_SESSION_REGISTRY_PREFIX + " Username `{}` - register session";
    public static final String SESSION_REGISTRY_RENEW_SESSION = FRAMEWORK_SESSION_REGISTRY_PREFIX + " Username `{}` - renew session";
    public static final String SESSION_REGISTRY_REMOVE_SESSION = FRAMEWORK_SESSION_REGISTRY_PREFIX + " Username `{}` - remove session";
    public static final String SESSION_REGISTRY_EXPIRE_SESSION = FRAMEWORK_SESSION_REGISTRY_PREFIX + " Username `{}` - expire session";

    // =================================================================================================================
    // Server
    // =================================================================================================================
    public static final String SERVER_RESET_SERVER_TASK = FRAMEWORK_SERVER + " Reset Server Initiator: `{}`. Status: `{}`";
}
