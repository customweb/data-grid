package com.customweb.grid.jpa.plugin.filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class EnumFilterPlugin implements FilterPlugin {

	@Override
	public boolean isTypeSupported(Class<?> type) {
		return type.isEnum();
	}

	@Override
	public Predicate getClause(CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String operator, Path<?> path, Class<?> type, String value) throws FilterException {
		try {
			Method method = type.getMethod("valueOf", String.class);
			List<Predicate> enumPredicates = new ArrayList<Predicate>();
			for (Object constant : type.getEnumConstants()) {
				String constantName = constant.toString();
				if (operator.equals("=")) {
					if (constantName.equalsIgnoreCase(value)) {
						return criteriaBuilder.equal(path, method.invoke(null, constantName));
					}
				} else {
					String constantNameLower = constantName.toLowerCase();
					if (constantNameLower.contains(value.toLowerCase())) {
						enumPredicates.add(criteriaBuilder.equal(path, method.invoke(null, constantName)));
					}
				}
			}
			return criteriaBuilder.or(enumPredicates.toArray(new Predicate[0]));
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new FilterException();
	}

}
