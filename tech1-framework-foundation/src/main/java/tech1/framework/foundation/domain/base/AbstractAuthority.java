package tech1.framework.foundation.domain.base;

public interface AbstractAuthority {
    String SUPERADMIN = "superadmin";
    String INVITATION_CODE_READ = "invitationCode:read";
    String INVITATION_CODE_WRITE = "invitationCode:write";

    String getValue();
}
