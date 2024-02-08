package io.tech1.framework.domain.properties.utilities;

import io.tech1.framework.domain.asserts.Asserts;
import io.tech1.framework.domain.constants.LogsConstants;
import io.tech1.framework.domain.properties.annotations.MandatoryProperty;
import io.tech1.framework.domain.properties.annotations.MandatoryToggleProperty;
import io.tech1.framework.domain.properties.annotations.NonMandatoryProperty;
import io.tech1.framework.domain.properties.base.AbstractPropertyConfigs;
import io.tech1.framework.domain.properties.base.AbstractTogglePropertyConfigs;
import io.tech1.framework.domain.properties.configs.AbstractPropertiesConfigs;
import io.tech1.framework.domain.properties.configs.AbstractPropertiesConfigsV2;
import io.tech1.framework.domain.reflections.ReflectionProperty;
import io.tech1.framework.domain.utilities.reflections.ReflectionUtility;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.tech1.framework.domain.asserts.Asserts.assertNonNullNotEmptyOrThrow;
import static io.tech1.framework.domain.asserts.Asserts.assertNonNullOrThrow;
import static io.tech1.framework.domain.constants.FrameworkLogsConstants.PROPERTIES_ASSERTER_DEBUG;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;
import static io.tech1.framework.domain.utilities.reflections.ReflectionUtility.getPropertyName;
import static java.util.Collections.emptyList;

@Slf4j
@UtilityClass
public class PropertiesAsserter {
    private static final Map<Function<Class<?>, Boolean>, Consumer<ReflectionProperty>> ACTIONS = new HashMap<>();

    static {
        ACTIONS.put(Date.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(LocalDate.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(LocalDateTime.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(ChronoUnit.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(TimeUnit.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Boolean.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Short.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Integer.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Long.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(BigInteger.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(BigDecimal.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(String.class::equals, Asserts::assertNonNullPropertyOrThrow);
        ACTIONS.put(Collection.class::isAssignableFrom, cp -> assertNonNullNotEmptyOrThrow((Collection<?>) cp.getPropertyValue(), invalidAttribute(cp.getPropertyName())));
    }

    @Deprecated
    public static void assertProperties(AbstractPropertiesConfigs abstractConfigs, String parentName) {
        assertNonNullOrThrow(abstractConfigs, invalidAttribute(parentName));
        abstractConfigs.assertProperties();
    }

    @Deprecated
    public static void assertNotNullProperties(AbstractPropertiesConfigs abstractConfigs, String parentName) {
        var getters = getGetters(abstractConfigs, parentName, emptyList());
        verifyProperties(getters, abstractConfigs, parentName, emptyList());
    }

    public static void assertMandatoryPropertiesConfigs(AbstractPropertiesConfigsV2 propertiesConfigs, String propertyName) {
        assertNonNullOrThrow(propertiesConfigs, invalidAttribute(propertyName));
        var getters = getMandatoryGetters(propertiesConfigs, propertyName, emptyList());
        verifyPropertiesConfigs(getters, propertiesConfigs, propertyName);
    }

    public static void assertMandatoryTogglePropertiesConfigs(AbstractPropertiesConfigsV2 propertiesConfigs, String propertyName) {
        assertNonNullOrThrow(propertiesConfigs, invalidAttribute(propertyName));
        var getters = getMandatoryToggleGetters(propertiesConfigs, propertyName, emptyList());
        verifyPropertiesConfigs(getters, propertiesConfigs, propertyName);
    }

    public static void assertMandatoryPropertyConfigs(AbstractPropertyConfigs propertyConfigs, String propertyName) {
        assertNonNullOrThrow(propertyConfigs, invalidAttribute(propertyName));
        var getters = getMandatoryGetters(propertyConfigs, propertyName, emptyList());
        verifyPropertyConfigs(getters, propertyConfigs, propertyName);
    }

    public static void assertMandatoryTogglePropertyConfigs(AbstractTogglePropertyConfigs propertyConfigs, String propertyName) {
        assertNonNullOrThrow(propertyConfigs, invalidAttribute(propertyName));
        var getters = getMandatoryToggleGetters(propertyConfigs, propertyName, emptyList());
        verifyPropertyConfigs(getters, propertyConfigs, propertyName);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    @Deprecated
    private static void verifyProperties(List<Method> getters, Object property, String parentName, List<String> projection) {
        getters.forEach(getter -> {
            var propertyName = getPropertyName(getter);
            var attributeName = parentName + "." + propertyName;
            try {
                var getterValue = getter.invoke(property);
                innerClass(getterValue, attributeName, projection);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new IllegalArgumentException("Unexpected. Attribute: " + attributeName);
            }
        });
    }

    private static void verifyPropertyConfigs(List<Method> getters, AbstractPropertyConfigs propertyConfigs, String parentName) {
        getters.forEach(getter -> {
            var propertyName = parentName + "." + getPropertyName(getter);
            try {
                var propertyValue = getter.invoke(propertyConfigs);
                verifyProperty(propertyName, propertyValue);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new IllegalArgumentException("Unexpected. Attribute: " + propertyName);
            }
        });
    }

    private static void verifyPropertiesConfigs(List<Method> getters, AbstractPropertiesConfigsV2 propertiesConfigs, String parentName) {
        getters.forEach(getter -> {
            var propertyName = parentName + "." + getPropertyName(getter);
            try {
                var propertyValue = getter.invoke(propertiesConfigs);
                Class<?> propertyClass = propertyValue.getClass();
                if (AbstractPropertiesConfigsV2.class.isAssignableFrom(propertyClass)) {
                    ((AbstractPropertiesConfigsV2) propertyValue).assertProperties(propertyName);
                } else if (AbstractPropertyConfigs.class.isAssignableFrom(propertyClass)) {
                    ((AbstractPropertyConfigs) propertyValue).assertProperties(propertyName);
                } else {
                    verifyProperty(propertyName, propertyValue);
                }
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new IllegalArgumentException("Unexpected. Attribute: " + propertyName);
            }
        });
    }

    private static void verifyProperty(String propertyName, Object propertyValue) {
        if (LogsConstants.DEBUG) {
            LOGGER.info(PROPERTIES_ASSERTER_DEBUG, propertyName, propertyValue);
        }
        assertNonNullOrThrow(propertyValue, invalidAttribute(propertyName));
        var propertyClass = propertyValue.getClass();
        var consumerOpt = ACTIONS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(propertyClass))
                .map(Map.Entry::getValue)
                .findFirst();
        if (consumerOpt.isPresent()) {
            var reflectionProperty = ReflectionProperty.of(propertyClass.getSimpleName(), propertyName, propertyValue);
            consumerOpt.get().accept(reflectionProperty);
        }
    }

    private static void innerClass(Object property, String attributeName, List<String> projection) {
        assertNonNullOrThrow(property, invalidAttribute(attributeName));
        var propertyClass = property.getClass();
        var consumerOpt = ACTIONS.entrySet().stream()
                .filter(entry -> entry.getKey().apply(propertyClass))
                .map(Map.Entry::getValue)
                .findFirst();
        if (consumerOpt.isPresent()) {
            var reflectionProperty = ReflectionProperty.of(propertyClass.getSimpleName(), attributeName, property);
            consumerOpt.get().accept(reflectionProperty);
        } else {
            var getters = getGetters(property, attributeName, projection);
            verifyProperties(getters, property, attributeName, projection);
        }
    }

    @Deprecated
    private static List<Method> getGetters(Object property, String attributeName, List<String> skipProjection) {
        assertNonNullOrThrow(property, invalidAttribute(attributeName));
        return ReflectionUtility.getGetters(property).stream()
                .filter(Objects::nonNull)
                .filter(method -> !method.getName().equals("getOrder"))
                .filter(method -> {
                    try {
                        var propertyName = getPropertyName(method);
                        var declaredField = property.getClass().getDeclaredField(propertyName);
                        return declaredField.isAnnotationPresent(MandatoryProperty.class) && !declaredField.isAnnotationPresent(NonMandatoryProperty.class);
                    } catch (NoSuchFieldException ex) {
                        return true;
                    }
                })
                .filter(method -> {
                    var lowerCaseAttribute = method.getName().toLowerCase().replaceAll("^get", "");
                    return !skipProjection.contains(lowerCaseAttribute);
                })
                .toList();
    }

    private static List<Method> getMandatoryGetters(Object property, String attributeName, List<String> skipProjection) {
        return getGetters(property, attributeName, MandatoryProperty.class, skipProjection);
    }

    private static List<Method> getMandatoryToggleGetters(Object property, String attributeName, List<String> skipProjection) {
        return getGetters(property, attributeName, MandatoryToggleProperty.class, skipProjection);
    }

    private static List<Method> getGetters(Object property, String attributeName, Class<? extends Annotation> annotation, List<String> skipProjection) {
        assertNonNullOrThrow(property, invalidAttribute(attributeName));
        return ReflectionUtility.getGetters(property).stream()
                .filter(Objects::nonNull)
                .filter(method -> !method.getName().equals("getOrder"))
                .filter(method -> {
                    try {
                        var propertyName = getPropertyName(method);
                        var declaredField = property.getClass().getDeclaredField(propertyName);
                        return declaredField.isAnnotationPresent(annotation);
                    } catch (NoSuchFieldException ex) {
                        return true;
                    }
                })
                .filter(method -> {
                    var lowerCaseAttribute = method.getName().toLowerCase().replaceAll("^get", "");
                    return !skipProjection.contains(lowerCaseAttribute);
                })
                .sorted(Comparator.comparing(Method::getName))
                .toList();
    }
}
