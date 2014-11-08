package com.customweb.grid.filter.builder;

import com.customweb.grid.filter.ResultFilter;

public interface FilterBuilder {
	public ResultFilter getFilter(String gridId);
}
