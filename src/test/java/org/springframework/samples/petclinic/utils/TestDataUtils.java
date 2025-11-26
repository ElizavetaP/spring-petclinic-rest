package org.springframework.samples.petclinic.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;

public final class TestDataUtils {

    private TestDataUtils() {
    }

    public static void setWeightUniversal(Object dto, Object value) {
        Method weightMethod = null;
        for (Method m : dto.getClass().getMethods()) {
            if (m.getName().equals("weight") && m.getParameterCount() == 1) {
                weightMethod = m;
                break;
            }
        }
        if (weightMethod == null) {
            throw new IllegalStateException("No weight(...) method found on " + dto.getClass());
        }

        Class<?> paramType = weightMethod.getParameterTypes()[0];
        Object converted = convertToType(value, paramType);

        try {
            weightMethod.invoke(dto, converted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke weight(...) on " + dto.getClass(), e);
        }
    }

    private static Object convertToType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return value;
        }

        if (targetType == BigDecimal.class) {
            if (value instanceof BigDecimal bd) return bd;
            if (value instanceof Number n)     return BigDecimal.valueOf(n.doubleValue());
            return new BigDecimal(value.toString());
        }

        if (targetType == Double.class || targetType == double.class) {
            if (value instanceof Number n) return n.doubleValue();
            return Double.valueOf(value.toString());
        }

        if (targetType == String.class) {
            return value.toString();
        }

        throw new IllegalArgumentException("Don't know how to convert " +
            value.getClass() + " to " + targetType);
    }
}
