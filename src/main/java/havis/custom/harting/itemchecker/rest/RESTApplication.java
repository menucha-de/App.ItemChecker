package havis.custom.harting.itemchecker.rest;

import havis.custom.harting.itemchecker.ConfigurationManager;
import havis.custom.harting.itemchecker.ItemChecker;
import havis.custom.harting.itemchecker.rest.provider.ForbiddenExceptionMapper;
import havis.custom.harting.itemchecker.rest.provider.ItemCheckerExceptionMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RESTApplication extends Application {

	private final static String PROVIDERS = "javax.ws.rs.ext.Providers";

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	private Map<String, Object> properties = new HashMap<>();

	public RESTApplication(ItemChecker main, ConfigurationManager manager) {
		singletons.add(new ItemCheckerService(main, manager));
		properties.put(PROVIDERS, new Class<?>[] { ItemCheckerExceptionMapper.class, ForbiddenExceptionMapper.class });
	}

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
}