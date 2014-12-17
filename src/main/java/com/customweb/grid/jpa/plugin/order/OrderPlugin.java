package com.customweb.grid.jpa.plugin.order;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import com.customweb.grid.jpa.plugin.filter.FilterException;

public interface OrderPlugin {

public boolean isTypeSupported(Class<?> type);
	
	public Expression<?> getPath(CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Path<?> path, Class<?> type) throws FilterException;
	
}
