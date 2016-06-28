package com.customweb.grid.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class Property {

	private Property() {
	}

	public static Class<?> getPropertyDataType(final Class<?> clazz, final String propertyName) {
		if (propertyName.contains(".")) {
			final int first = propertyName.indexOf(".");
			final String fieldNameRest = propertyName.substring(first + 1);
			final String effectiveFieldName = propertyName.substring(0, first);

			return getPropertyDataType(getReturnTypeByPropertyName(clazz, effectiveFieldName), fieldNameRest);
		} else {
			return getReturnTypeByPropertyName(clazz, propertyName);
		}
	}

	private static Class<?> getReturnTypeByPropertyName(final Class<?> clazz, final String propertyName) {

		final Field field = getFieldByFieldName(clazz, propertyName);
		if (field != null) {
			return field.getType();
		}
		final char first = Character.toUpperCase(propertyName.charAt(0));
		String methodName = "get" + first + propertyName.substring(1);
		Method method = getMethodByMethodName(clazz, methodName);
		if (method != null) {
			return method.getReturnType();
		}
		methodName = "is" + first + propertyName.substring(1);
		method = getMethodByMethodName(clazz, methodName);
		if (method != null) {
			return method.getReturnType();
		}
		throw new RuntimeException(
				String.format("No field and no getter method found for property '%1s' on class '%2s.", propertyName,
						clazz.getCanonicalName()));
	}

	public static Field getFieldByFieldName(final Class<?> clazz, final String propertyName) {
		final Field[] fields = clazz.getFields();
		for (final Field field : fields) {
			if (field.getName().equals(propertyName)) {
				return field;
			}
		}
		if (clazz.getSuperclass() != null) {
			return getFieldByFieldName(clazz.getSuperclass(), propertyName);
		}
		return null;
	}

	public static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	public static Method getMethodByMethodName(final Class<?> clazz, final String methodName) {
		final Method[] methods = clazz.getMethods();
		for (final Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		if (clazz.getSuperclass() != null) {
			return getMethodByMethodName(clazz.getSuperclass(), methodName);
		}
		return null;
	}

}
