package havis.app.itemchecker;

import havis.middleware.ale.config.service.mc.Path;
import havis.middleware.ale.service.ec.ECReport;
import havis.middleware.ale.service.ec.ECReportGroup;
import havis.middleware.ale.service.ec.ECReportGroupListMember;
import havis.middleware.ale.service.ec.ECReports;
import havis.middleware.ale.service.mc.MC;
import havis.middleware.ale.service.mc.MCSpec;
import havis.middleware.misc.TdtWrapper;
import havis.middleware.tdt.TdtTagInfo;
import havis.middleware.tdt.TdtTranslationException;
import havis.middleware.tdt.TdtTranslator;

import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

public class ItemChecker {

	private final static Logger log = Logger.getLogger(ItemChecker.class.getName());
	private ScheduledExecutorService worker;

	private final BlockingQueue<ECReports> ecQueue = new LinkedBlockingQueue<>();
	private ItemManager itemManager;
	private boolean running = false;
	private Map<String, Item> items = new LinkedHashMap<String, Item>();
	private URL startURL;
	private URL stopURL;
	private TdtTranslator tdtTranslator;

	private Encoding currentEncoding = Encoding.EPC_TAG;

	public ItemChecker() throws ItemCheckerException {
		try {
			itemManager = new ItemManager();
			startURL = new URL("http://localhost:8888/services/ALE/trigger/ItemCheckerStart");			
			stopURL = new URL("http://localhost:8888/services/ALE/trigger/ItemCheckerStop");
			tdtTranslator = TdtWrapper.getTdt();
		} catch (Exception e) {
			throw new ItemCheckerException(e);
		}
		getItemsFromDb();
	}

	public synchronized void setDelimiter(String delimiter) {
		itemManager.setPreference(delimiter);
	}

	public synchronized void clear() throws ItemCheckerException {
		items.clear();
		if (itemManager != null) {
			itemManager.clear();
		}
	}

	public synchronized void setCurrentEncoding(Encoding currentEncoding, boolean clear) throws ItemCheckerException {
		if (clear) {
			clear();
		}
		this.currentEncoding = currentEncoding;
	}

	public synchronized void close() throws ItemCheckerException {
		if (itemManager != null) {
			itemManager.close();
		}
	}

	public synchronized void startScan() throws ItemCheckerException {
		running = true;
		worker = Executors.newScheduledThreadPool(1);
		worker.execute(new ECThread());
		performTrigger(true /* start */);
	}

	public synchronized void stopScan() throws ItemCheckerException {
		if (worker != null) {
			worker.shutdownNow();
			try {
				if (!worker.awaitTermination(30, TimeUnit.SECONDS)) {
					IllegalStateException illegalStateException = new IllegalStateException("Termination failed");
					log.log(Level.SEVERE, "Failed to stop executor", illegalStateException);
					throw illegalStateException;
				}
			} catch (InterruptedException e) {
				// ignore
			}
			performTrigger(false /* stop */);
			running = false;
			worker = null;
		}
	}

	public synchronized List<Item> getItems() {
		List<Item> items = new ArrayList<Item>(this.items.values());
		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				if (o1.getCode() != null && o2.getCode() != null) {
					return o1.getCode().compareTo(o2.getCode());
				}
				return 0;
			}
		});
		return items;
	}

	public synchronized boolean getStatus() {
		return running;
	}

	public synchronized BlockingQueue<ECReports> getQueue() {
		return ecQueue;
	}

	public synchronized Item getItem(String code) {
		return items.get(code);
	}

	public synchronized void importCsv(Reader reader) throws ItemCheckerException {
		clear();
		try {
			itemManager.unmarshal(reader);
		} catch (Exception e) {
			throw new ItemCheckerException(e);
		}
		getItemsFromDb();
	}

	public synchronized void exportCsv(Writer writer) throws ItemCheckerException {
		try {
			itemManager.marshal(writer);
		} catch (Exception e) {
			throw new ItemCheckerException(e);
		}
	}
	
	private MC mc;
	public void init(MC mc){
		this.mc = mc;
	}
	
	/**
	 * Activates or deactivates the subscriber
	 */
	private void setSubscriber(boolean start){
		try {			
			List<String> ecIds = mc.list(Path.Service.EC.EventCycle);
			for(String ec : ecIds){
				MCSpec spec = mc.get(Path.Service.EC.EventCycle, ec);
				if(Objects.equals(spec.getName(), "ItemChecker")){
					List<String> subIds = mc.list(Path.Service.EC.Subscriber, ec);
					for(String sub : subIds){
						spec = mc.get(Path.Service.EC.Subscriber, sub, ec);
						spec.setEnable(start);
						mc.update(Path.Service.EC.Subscriber, sub, spec, ec);
						break;
					}
					break;
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to update subscriber.", e);
		}
	}

	private void performTrigger(boolean start) throws ItemCheckerException {
		HttpURLConnection trigger;

		try {
			if (start) {
				trigger = (HttpURLConnection) startURL.openConnection();
				setSubscriber(start);
			} else {
				setSubscriber(start);
				trigger = (HttpURLConnection) stopURL.openConnection();
			}
			int response = trigger.getResponseCode();
			if (response < 200 || response > 299) {
				throw new ItemCheckerException("Server response with " + response);
			}
		} catch (Exception e) {
			throw new ItemCheckerException(e);
		}

	}

	private void getItemsFromDb() throws ItemCheckerException {
		for (Item item : itemManager.getEntries()) {
			items.put(item.getCode(), item);
		}
	}

	private class ECThread extends Thread {
		public ECThread() {
			super("EC-Thread");
		}

		private void checkItems(ECReports reports) throws ItemCheckerException {
			if (reports.getReports() != null) {
				for (ECReport ecReport : reports.getReports().getReport()) {
					for (ECReportGroup group : ecReport.getGroup()) {
						if (group.getGroupList() != null) {
							for (ECReportGroupListMember member : group.getGroupList().getMember()) {
								String code = "";
								switch (currentEncoding) {
								case EPC_TAG:
									code = member.getTag().getValue();
									break;
								case EPC_PURE:
									code = member.getEpc().getValue();
									break;
								case EPC_HEX:
									code = member.getRawHex().getValue();
									break;
								case RAW_HEX:
								default:
									String[] epcElements = member.getRawHex().getValue().split("x");
									code = "x" + epcElements[epcElements.length - 1];
									break;
								}
								checkItem(code);
							}
						}
					}
				}
			}
		}

		@Override
		public void run() {
			try {
				while (running) {
					ECReports reports = ecQueue.poll(100, TimeUnit.MILLISECONDS);
					if (reports != null) {
						try {
							checkItems(reports);
						} catch (Throwable e) {
							log.log(Level.WARNING, "Failed to evaluate EC reports", e);
						}
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public void addSightings(List<Sighting> sightings) throws ItemCheckerException {
		for (Sighting sighting : sightings) {
			String code = sighting.getCode();
			try {
				if (code.startsWith("x")) {
					code = code.substring(1);
				}
				TdtTagInfo tdtTagInfo = tdtTranslator.translate(DatatypeConverter.parseHexBinary(code));
				switch (currentEncoding) {
				case EPC_TAG:
					code = tdtTagInfo.getUriTag();
					break;
				case EPC_PURE:
					code = tdtTagInfo.getUriId();
					break;
				case EPC_HEX:
					code = tdtTagInfo.getUriRawHex();
					break;
				case RAW_HEX:
					code = "x" + code;
				default:
				}
			} catch (IndexOutOfBoundsException | IllegalArgumentException | TdtTranslationException e) {
				log.log(Level.WARNING, "Failed to translate sighting '" + code + "'.", e);
			}
			checkItem(code);
		}
	}

	private void checkItem(String code) throws ItemCheckerException {
		Item current = items.get(code);
		if (current != null) {
			if (current.getState() == -1) {
				current.setState(0);
				itemManager.updateItem(current);
			}
		} else {
			Item item = new Item();
			item.setCount(1);
			item.setDescription("");
			item.setId("");
			item.setState(1);
			item.setCode(code);
			items.put(code, item);
			itemManager.addItem(item);
		}
	}

}