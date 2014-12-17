package com.customweb.grid.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.customweb.grid.filter.FieldFilter;
import com.customweb.grid.filter.OrderBy;
import com.customweb.grid.filter.ResultFilter;
import com.customweb.grid.jpa.plugin.filter.FilterException;
import com.customweb.grid.jpa.plugin.filter.FilterPlugin;
import com.customweb.grid.jpa.plugin.filter.FilterPluginProvider;
import com.customweb.grid.jpa.plugin.order.OrderPlugin;
import com.customweb.grid.jpa.plugin.order.OrderPluginProvider;
import com.customweb.grid.util.Property;

public class JavaPersistenceApiExecutor<T> implements Executor<T> {

	private EntityManager entityManager;
	private ResultFilter filter;
	private CriteriaBuilder criteriaBuilder;
	private Root<T> root;

	@SuppressWarnings("rawtypes")
	private CriteriaQuery query;
	private Class<T> domainClass;
	
	private List<FilterPlugin> filterPlugins = FilterPluginProvider.getPlugins();
	private List<OrderPlugin> orderPlugins = OrderPluginProvider.getPlugins();

	@Override
	public Class<?> getDomainClass() {
		return domainClass;
	}

	public JavaPersistenceApiExecutor(EntityManager entityManager, Class<T> clazz) {
		this.entityManager = entityManager;
		this.domainClass = clazz;
		criteriaBuilder = entityManager.getCriteriaBuilder();
	}
	
	public JavaPersistenceApiExecutor<T> addFilterPlugins(FilterPlugin... filterPlugins) {
		this.filterPlugins.addAll(Arrays.asList(filterPlugins));
		return this;
	}
	
	public JavaPersistenceApiExecutor<T> addOrderPlugins(OrderPlugin... orderPlugins) {
		this.orderPlugins.addAll(Arrays.asList(orderPlugins));
		return this;
	}

	@Override
	public synchronized List<T> getResultSet(ResultFilter filter) {
		this.filter = filter;
		resetQuery();
		TypedQuery<T> q = this.entityManager.createQuery(this.getQuery());

		// Limit
		q.setFirstResult(filter.getRangeStart());
		q.setMaxResults(filter.getResultsPerPage());
		return q.getResultList();
	}

	@Override
	public synchronized long getNumberOfItems(ResultFilter filter) {
		this.filter = filter;
		resetQuery();
		TypedQuery<Long> q = this.entityManager.createQuery(this.getQueryForCount());
		return q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	protected synchronized void resetQuery() {
		query = criteriaBuilder.createQuery();
		root = query.distinct(true).from(domainClass);
	}

	@SuppressWarnings("unchecked")
	protected CriteriaQuery<T> getQuery() {
		query.select(root);
		query.where(getWhere());
		query.orderBy(getOrderBy());
		return query;
	}

	@SuppressWarnings("unchecked")
	protected CriteriaQuery<Long> getQueryForCount() {
		query.select(criteriaBuilder.count(root));
		query.where(getWhere());
		return query;
	}

	protected synchronized Predicate getWhere() {
		Predicate clause = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		for (FieldFilter fieldFilter : filter.getFieldFilters()) {
			clause = this.getWhereInternal(clause, fieldFilter);
		}
		return clause;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Predicate getWhereInternal(Predicate clause, FieldFilter fieldFilter) {
		String operator = fieldFilter.getOperator();

		Class<?> fieldType = Object.class;
		try {
			fieldType = Property.getPropertyDataType(domainClass, fieldFilter.getFieldName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (operator == null) {
			if (String.class.isAssignableFrom(fieldType)) {
				operator = "contains";
			} else {
				operator = "=";

			}
		}

		// TODO: Allow also operator like 'lt' & 'gt'

		Path path = getPathCompletePath(root, fieldFilter.getFieldName());

		Predicate filterPredicate = null;
		try {
			if (this.filterPlugins != null && !this.filterPlugins.isEmpty()) {
				for (FilterPlugin plugin : this.filterPlugins) {
					if (plugin.isTypeSupported(fieldType)) {
						try {
							filterPredicate = plugin.getClause(query, criteriaBuilder, operator, path, fieldType, fieldFilter.getValue());
							break;
						} catch (FilterException e) {
						}
					}
				}
			}
			if (filterPredicate == null) {
				if (operator.equals(">")) {
					Integer value = new Integer(fieldFilter.getValue());
					filterPredicate = criteriaBuilder.gt(path, value);
				} else if (operator.equals("<")) {
					Integer value = new Integer(fieldFilter.getValue());
					filterPredicate = criteriaBuilder.lt(path, value);
				} else if (operator.equals("=")) {
					filterPredicate = criteriaBuilder.equal(path, fieldFilter.getValue());
				} else if (operator.equals("contains")) {
					String value = "%" + fieldFilter.getValue() + "%";
					filterPredicate = criteriaBuilder.like(criteriaBuilder.lower(path), criteriaBuilder.lower(criteriaBuilder.literal("%" + value + "%")));
				}
			}
		} catch (Exception e) {
			filterPredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 0);
		}
		
		if (filterPredicate != null) {
			clause = criteriaBuilder.and(clause, filterPredicate);
		}
		return clause;
	}

	protected synchronized List<Order> getOrderBy() {
		// Order By
		List<Order> orderBys = new ArrayList<Order>();
		for (OrderBy orderBy : filter.getOrderBys()) {
			Class<?> fieldType = Object.class;
			try {
				fieldType = Property.getPropertyDataType(domainClass, orderBy.getFieldName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Expression<?> path = getPathCompletePath(root, orderBy.getFieldName());
			if (this.orderPlugins != null && !this.orderPlugins.isEmpty()) {
				for (OrderPlugin plugin : this.orderPlugins) {
					if (plugin.isTypeSupported(fieldType)) {
						try {
							path = plugin.getPath(query, criteriaBuilder, (Path<?>) path, fieldType);
							break;
						} catch (FilterException e) {
						}
					}
				}
			}
			
			if (orderBy.isSortAscending()) {
				orderBys.add(criteriaBuilder.asc(path));
			} else {
				orderBys.add(criteriaBuilder.desc(path));
			}
		}
		return orderBys;
	}

	protected CriteriaBuilder getCriteriaBuilder() {
		return this.criteriaBuilder;
	}

	protected ResultFilter getFilter() {
		return this.filter;
	}

	protected Root<T> getRoot() {
		return this.root;
	}

	protected Path<?> getPathCompletePath(Path<?> rootPath, String fieldName) {
		if (fieldName.contains(".")) {
			int first = fieldName.indexOf(".");
			String fieldNameRest = fieldName.substring(first + 1);
			String effectiveFieldName = fieldName.substring(0, first);

			return this.getPathCompletePath(rootPath.get(effectiveFieldName), fieldNameRest);
		} else {
			return rootPath.get(fieldName);
		}
	}
}
