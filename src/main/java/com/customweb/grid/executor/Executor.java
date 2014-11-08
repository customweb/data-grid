package com.customweb.grid.executor;

import java.util.List;

import com.customweb.grid.filter.ResultFilter;


public interface Executor<T> {
	public List<T> getResultSet(ResultFilter filter);
	public long getNumberOfItems(ResultFilter filter);
	public Class<?> getDomainClass();
}
