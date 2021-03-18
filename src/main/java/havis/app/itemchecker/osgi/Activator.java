package havis.app.itemchecker.osgi;

import havis.app.itemchecker.ConfigurationManager;
import havis.app.itemchecker.Environment;
import havis.app.itemchecker.ItemChecker;
import havis.app.itemchecker.rest.RESTApplication;
import havis.middleware.ale.base.exception.ALEException;
import havis.middleware.ale.base.exception.ValidationException;
import havis.middleware.ale.config.service.mc.Path;
import havis.middleware.ale.service.mc.MC;
import havis.middleware.ale.service.mc.MCEventCycleSpec;
import havis.middleware.ale.service.mc.MCSubscriberSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Application;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Activator implements BundleActivator {
	private final static Logger log = Logger.getLogger(Activator.class.getName());

	private final static String QUEUE_NAME = "name";
	// subscriber URI: queue://itemchecker
	private final static String EC_QUEUE_VALUE = "itemchecker";

	private ItemChecker itemChecker;
	private ConfigurationManager manager;

	private ServiceTracker<MC, MC> tracker;
	private ServiceRegistration<?> ecQueue;
	private ServiceRegistration<Application> app;

	@SuppressWarnings("serial")
	@Override
	public void start(BundleContext context) throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(Activator.class.getClassLoader());
			itemChecker = new ItemChecker();
			manager = new ConfigurationManager();
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}

		ecQueue = context.registerService(Queue.class, itemChecker.getQueue(), new Hashtable<String, String>() {
			{
				put(QUEUE_NAME, EC_QUEUE_VALUE);
			}
		});

		create(context);

		app = context.registerService(Application.class, new RESTApplication(itemChecker, manager), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (ecQueue != null) {
			ecQueue.unregister();
			ecQueue = null;
		}

		if (itemChecker != null) {
			itemChecker.stopScan();
			itemChecker.close();
		}

		if (app != null) {
			app.unregister();
			app = null;
		}
	}

	private void create(BundleContext context) throws IOException {
		tracker = new ServiceTracker<MC, MC>(context, MC.class, null) {
			@SuppressWarnings("unchecked")
			@Override
			public MC addingService(ServiceReference<MC> reference) {
				try {
					MC mc = super.addingService(reference);
					try {
						if (new File(Environment.LOCK).createNewFile()) {
							ObjectMapper mapper = new ObjectMapper();
							try {
								Thread.currentThread().setContextClassLoader(Activator.class.getClassLoader());
								for (java.nio.file.Path path : Files.newDirectoryStream(Paths.get(Environment.SPEC, "ec"))) {
									Map<String, Object> map = mapper.readValue(path.toFile(), Map.class);
									boolean enabled = true;
									String id;
									MCEventCycleSpec ecSpec = mapper.convertValue(map.get("eventCycle"), MCEventCycleSpec.class);
									try {
										id = mc.add(Path.Service.EC.EventCycle, ecSpec);
									} catch (ValidationException e) {
										// try disabled cycle
										enabled = false;
										ecSpec.setEnable(Boolean.valueOf(enabled));
										id = mc.add(Path.Service.EC.EventCycle, ecSpec);
									}
									for (MCSubscriberSpec subscriberSpec : mapper.convertValue(map.get("subscribers"), MCSubscriberSpec[].class)) {
										subscriberSpec.setEnable(Boolean.valueOf(enabled));
										mc.add(Path.Service.EC.Subscriber, subscriberSpec, id);
									}
								}
							} catch (IOException | ALEException e) {
								log.log(Level.SEVERE, "Failed to import spec", e);
							}
						}
					} catch (Exception e) {
						log.log(Level.SEVERE, "Failed to check lock", e);
					}
					itemChecker.init(mc);
					return mc;
				} finally {
					tracker.close();
				}
			}
		};
		tracker.open();
	}

}