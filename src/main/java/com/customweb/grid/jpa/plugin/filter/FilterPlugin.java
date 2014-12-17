package com.customweb.grid.jpa.plugin.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public interface FilterPlugin {
	
	public boolean isTypeSupported(Class<?> type);
	
	public Predicate getClause(CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, String operator, Path<?> path, Class<?> type, String value) throws FilterException;

}
