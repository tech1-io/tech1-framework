package tech1.framework.iam.domain.dto.requests;

import tech1.framework.foundation.domain.base.Password;

public record RequestUserChangePasswordBasic(
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password newPassword,
        @Password.ValidPasswordNotBlank Password confirmPassword
) {

    public static RequestUserChangePasswordBasic testsHardcoded() {
        return new RequestUserChangePasswordBasic(
                Password.testsHardcoded(),
                Password.testsHardcoded()
        );
    }

    public static RequestUserChangePasswordBasic random() {
        return new RequestUserChangePasswordBasic(
                Password.random(),
                Password.random()
        );
    }

    public void assertPasswordsOrThrow() {
        this.newPassword.assertEqualsOrThrow(this.confirmPassword);
    }
}
