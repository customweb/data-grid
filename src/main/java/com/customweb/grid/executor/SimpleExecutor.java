package com.customweb.grid.executor;

import java.util.ArrayList;
import java.util.List;

import com.customweb.grid.filter.FieldFilter;
import com.customweb.grid.filter.ResultFilter;

/**
 * 
 * @author Thomas Hunziker
 *
 * @param <T>
 */
public class SimpleExecutor<T> implements Executor<T> {
	
	private final List<? extends T> results;
	private final Class<? extends T> clazz;
	
	
	public SimpleExecutor(List<? extends T> results, Class<? extends T> clazz) {
		this.results = results;
		this.clazz = clazz;
	}

	@Override
	public List<T> getResultSet(ResultFilter filter) {
		
		for (FieldFilter fieldFilter : filter.getFieldFilters()) {
			
			// TODO Apply filters
		}
		
		// TODO Implement order by etc.
		
		return new ArrayList<>(this.results);
	}

	@Override
	public long getNumberOfItems(ResultFilter filter) {
		return this.results.size();
	}

	@Override
	public Class<?> getDomainClass() {
		return this.clazz;
	}

}
