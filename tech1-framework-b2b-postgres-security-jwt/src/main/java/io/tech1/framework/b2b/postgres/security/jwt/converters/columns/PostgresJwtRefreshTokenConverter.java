package io.tech1.framework.b2b.postgres.security.jwt.converters.columns;

import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static java.util.Objects.nonNull;

@Converter
public class PostgresJwtRefreshTokenConverter implements AttributeConverter<JwtRefreshToken, String> {

    @Override
    public String convertToDatabaseColumn(JwtRefreshToken accessToken) {
        return nonNull(accessToken) ? accessToken.value() : null;
    }

    @Override
    public JwtRefreshToken convertToEntityAttribute(String value) {
        return nonNull(value) ? JwtRefreshToken.of(value) : null;
    }
}
