package com.customweb.grid.jpa.plugin.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderPluginProvider {

	private static List<OrderPlugin> orderPlugins = new ArrayList<OrderPlugin>();
	
	public static List<OrderPlugin> getPlugins() {
		return new ArrayList<OrderPlugin>(orderPlugins);
	}
	
	public static void addPlugins(OrderPlugin... plugins) {
		orderPlugins.addAll(Arrays.asList(plugins));
	}
	
}
