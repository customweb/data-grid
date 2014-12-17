package com.customweb.grid.jpa.plugin.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class BooleanFilterPlugin implements FilterPlugin {

	@Override
	public boolean isTypeSupported(Class<?> type) {
		return Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Predicate getClause(CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String operator, Path<?> path, Class<?> type, String value) throws FilterException {
		if (Boolean.valueOf(value)) {
			return criteriaBuilder.isTrue((Expression<Boolean>) path);
		} else {
			return criteriaBuilder.isFalse((Expression<Boolean>) path);
		}
	}

}