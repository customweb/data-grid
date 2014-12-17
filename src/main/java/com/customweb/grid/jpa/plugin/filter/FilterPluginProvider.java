package com.customweb.grid.jpa.plugin.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterPluginProvider {

	private static List<FilterPlugin> filterPlugins = new ArrayList<FilterPlugin>();
	
	static {
		filterPlugins.add(new BooleanFilterPlugin());
		filterPlugins.add(new EnumFilterPlugin());
	}
	
	public static List<FilterPlugin> getPlugins() {
		return new ArrayList<FilterPlugin>(filterPlugins);
	}
	
	public static void addPlugins(FilterPlugin... plugins) {
		filterPlugins.addAll(Arrays.asList(plugins));
	}
	
}
