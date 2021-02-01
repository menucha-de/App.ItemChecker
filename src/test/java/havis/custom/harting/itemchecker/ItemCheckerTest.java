package havis.custom.harting.itemchecker;

import havis.middleware.ale.service.EPC;
import havis.middleware.ale.service.ec.ECReport;
import havis.middleware.ale.service.ec.ECReportGroup;
import havis.middleware.ale.service.ec.ECReportGroupList;
import havis.middleware.ale.service.ec.ECReportGroupListMember;
import havis.middleware.ale.service.ec.ECReports;
import havis.middleware.ale.service.ec.ECReports.Reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Assert;
import org.junit.Test;

public class ItemCheckerTest {

	private Sighting sightingABC = new Sighting();
	private Sighting sightingXYZ = new Sighting();
	private Sighting signtingNull = new Sighting();

	public ItemCheckerTest() {
		sightingABC.setCode("ABC");
		sightingABC.setTid("EFG");
		sightingXYZ.setCode("XYZ");
		sightingABC.setTid("EFG");

	}

	@Test
	public void itemCheckerSetter(final @Mocked ItemManager itemManager) throws Exception {
		ItemChecker checker = new ItemChecker();
		checker.setDelimiter("EXCEL_NORTH_EUROPE_PREFERENCE");
		checker.setCurrentEncoding(Encoding.EPC_HEX, false);
		new Verifications() {
			{
				itemManager.setPreference(this.<String> withNotNull());
				times = 1;
				itemManager.clear();
				times = 0;
			}
		};

		checker.setCurrentEncoding(Encoding.EPC_HEX, true);
		new Verifications() {
			{
				itemManager.clear();
				times = 1;
			}
		};

	}

	@Test
	public void itemCheckerManagerException(final @Mocked ItemManager itemManager) throws Exception {
		new NonStrictExpectations() {
			{
				new ItemManager();
				result = new ItemCheckerException("Error");
			}
		};
		try {
			@SuppressWarnings("unused")
			ItemChecker checker = new ItemChecker();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals("Error", e.getCause().getMessage());
		}
	}

	@Test
	public void itemHandling(final @Mocked ItemManager itemManager) throws Exception {
		new NonStrictExpectations() {
			{
				itemManager.getEntries();
				Item item = new Item();
				item.setCode("XYZ");
				result = new ArrayList<Item>(Arrays.asList(item));
			}
		};
		ItemChecker checker = new ItemChecker();
		checker.addSightings(new ArrayList<Sighting>(Arrays.asList(sightingABC, sightingXYZ, signtingNull)));
		Assert.assertEquals(sightingABC.getCode(), checker.getItems().get(0).getCode());
		Assert.assertEquals(sightingABC.getCode(), checker.getItem("ABC").getCode());
		Assert.assertEquals(3, checker.getItems().size());
		checker.clear();
		Assert.assertEquals(0, checker.getItems().size());
		new Verifications() {
			{

				itemManager.updateItem(this.<Item> withNotNull());
				times = 1;
				itemManager.addItem(this.<Item> withNotNull());
				times = 2;
				itemManager.clear();
				times = 1;
			}
		};
	}

	@Test
	public void scan(final @Mocked ItemManager itemManager, final @Mocked URL url, final @Mocked HttpURLConnection http) throws ItemCheckerException,
			InterruptedException, IOException {
		new NonStrictExpectations() {
			{
				http.getResponseCode();
				result = 200;
				itemManager.getEntries();
				Item item = new Item();
				item.setCode("XYZ");
				result = new ArrayList<Item>(Arrays.asList(item));
			}
		};
		ItemChecker checker = new ItemChecker();
		Assert.assertFalse(checker.getStatus());
		checker.startScan();
		Assert.assertTrue(checker.getStatus());
		checker.addSightings(new ArrayList<Sighting>(Arrays.asList(sightingABC)));
		checker.getQueue().add(createECReports());
		Thread.sleep(10);
		checker.setCurrentEncoding(Encoding.EPC_PURE, false);
		checker.getQueue().add(createECReports());
		Thread.sleep(10);
		checker.setCurrentEncoding(Encoding.EPC_HEX, false);
		checker.getQueue().add(createECReports());
		Thread.sleep(10);
		checker.setCurrentEncoding(Encoding.RAW_HEX, false);
		checker.getQueue().add(createECReports());
		Thread.sleep(10);
		checker.stopScan();
		Assert.assertFalse(checker.getStatus());

		new NonStrictExpectations() {
			{
				itemManager.updateItem(this.<Item> withNotNull());
				result = new ItemCheckerException("Error");
			}
		};
		checker.startScan();
		checker.getQueue().add(createECReports());
		Thread.sleep(10);
		checker.stopScan();

		new NonStrictExpectations() {
			{
				http.getResponseCode();
				result = new IOException("Error");
			}
		};
		try {
			checker.startScan();
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
		new NonStrictExpectations() {
			{
				http.getResponseCode();
				result = 500;
			}
		};
		try {
			checker.startScan();
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
	}

	@Test
	public void cancelTaskException(final @Mocked ItemManager itemManager, final @Mocked ScheduledExecutorService service, final @Mocked URL url,
			final @Mocked HttpURLConnection http) throws Exception {
		new NonStrictExpectations() {
			{
				http.getResponseCode();
				result = 200;
				service.awaitTermination(30, TimeUnit.SECONDS);
				result = false;
			}
		};
		ItemChecker checker = new ItemChecker();
		Deencapsulation.setField(checker, "worker", service);
		try {
			checker.stopScan();
			Assert.fail();
		} catch (IllegalStateException e) {
		}
		new NonStrictExpectations() {
			{
				service.awaitTermination(30, TimeUnit.SECONDS);
				result = new InterruptedException();
			}
		};
		checker.stopScan();
	}

	@Test
	public void importCsv(final @Mocked ItemManager itemManager) throws Exception {
		ItemChecker checker = new ItemChecker();
		checker.importCsv(new StringReader("CSV"));
		new NonStrictExpectations() {
			{
				itemManager.unmarshal(this.<Reader> withNotNull());
				result = new Exception("Error");
			}
		};
		try {
			checker.importCsv(new BufferedReader(new StringReader("CSV")));
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
	}

	@Test
	public void exportCsv(final @Mocked ItemManager itemManager) throws Exception {
		ItemChecker checker = new ItemChecker();
		checker.exportCsv(new StringWriter());
		new NonStrictExpectations() {
			{
				itemManager.marshal(this.<Writer> withNotNull());
				result = new Exception("Error");
			}
		};
		try {
			checker.exportCsv(new StringWriter());
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
	}

	@Test
	public void close(final @Mocked ItemManager itemManager) throws Exception {
		ItemChecker checker = new ItemChecker();
		checker.close();
		new Verifications() {
			{
				itemManager.close();
				times = 1;
			}
		};
	}

	private static ECReports createECReports() {
		ECReports report = new ECReports();
		report.setSpecName("specA");
		report.setCreationDate(new Date(123456789));
		report.setReports(new Reports());
		ECReport r = new ECReport();
		r.setReportName("reportA");
		ECReportGroup g = new ECReportGroup();
		g.setGroupName("Default");
		ECReportGroupList l = new ECReportGroupList();
		ECReportGroupListMember m1 = new ECReportGroupListMember();
		m1.setEpc(new EPC("XYZ"));
		m1.setRawHex(new EPC("XYZ"));
		m1.setTag(new EPC("XYZ"));
		l.getMember().add(m1);
		g.setGroupList(l);
		r.getGroup().add(g);
		report.getReports().getReport().add(r);
		return report;
	}

}
