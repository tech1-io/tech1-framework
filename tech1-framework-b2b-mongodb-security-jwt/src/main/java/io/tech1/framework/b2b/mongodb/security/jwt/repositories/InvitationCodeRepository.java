package io.tech1.framework.b2b.mongodb.security.jwt.repositories;

import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.DbInvitationCode;
import io.tech1.framework.domain.base.Username;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.tech1.framework.domain.asserts.Asserts.assertNonNullOrThrow;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.entityNotFound;

@Repository
public interface InvitationCodeRepository extends MongoRepository<DbInvitationCode, String> {
    List<DbInvitationCode> findByOwner(Username username);
    List<DbInvitationCode> findByInvitedIsNull();
    DbInvitationCode findByValue(String value);

    default DbInvitationCode getById(String invitationCodeId) {
        return this.findById(invitationCodeId).orElse(null);
    }

    default DbInvitationCode requirePresence(String invitationCodeId) {
        var invitationCode = this.getById(invitationCodeId);
        assertNonNullOrThrow(invitationCode, entityNotFound("DbInvitationCode", invitationCodeId));
        return invitationCode;
    }
}
