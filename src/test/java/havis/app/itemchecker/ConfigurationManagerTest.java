package havis.app.itemchecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationManagerTest {

	@Test
	public void configurationManagerWithDefaultConfig() throws Exception {
		ConfigurationManager manager = new ConfigurationManager();
		Configuration configuration = manager.get();
		Assert.assertEquals("EXCEL_NORTH_EUROPE_PREFERENCE", configuration.getDelimiter());
		Assert.assertEquals(Encoding.EPC_TAG, configuration.getEncoding());
	}

	@Test
	public void configurationManagerWithExistingConfigFile(final @Mocked File configFile, final @Mocked ObjectMapper mapper) throws Exception {
		new NonStrictExpectations() {
			{
				configFile.exists();
				result = true;
			}
		};
		new ConfigurationManager();

		new Verifications() {
			{
				mapper.readValue(new File(Environment.CONFIG_FILE), Configuration.class);
				times = 1;
			}
		};
	}

	@Test
	public void reset() throws Exception {
		ConfigurationManager manager = new ConfigurationManager();

		Configuration configuration = manager.get();
		Assert.assertEquals("EXCEL_NORTH_EUROPE_PREFERENCE", configuration.getDelimiter());
		Assert.assertEquals(Encoding.EPC_TAG, configuration.getEncoding());

		manager.reset();

		Configuration configurationAfter = manager.get();
		Assert.assertEquals("EXCEL_NORTH_EUROPE_PREFERENCE", configurationAfter.getDelimiter());
		Assert.assertEquals(Encoding.EPC_TAG, configurationAfter.getEncoding());
	}

	@Test
	public void set() throws Exception {
		ConfigurationManager manager = new ConfigurationManager();

		Configuration configuration = manager.get();
		Assert.assertEquals("EXCEL_NORTH_EUROPE_PREFERENCE", configuration.getDelimiter());
		Assert.assertEquals(Encoding.EPC_TAG, configuration.getEncoding());

		Configuration newConfiguration = new Configuration();
		newConfiguration.setDelimiter("EXCEL_PREFERENCE");
		newConfiguration.setEncoding(Encoding.EPC_PURE);
		newConfiguration.setQuantity(true);
		manager.set(newConfiguration);

		Configuration configurationAfter = manager.get();
		Assert.assertEquals("EXCEL_PREFERENCE", configurationAfter.getDelimiter());
		Assert.assertEquals(Encoding.EPC_PURE, configurationAfter.getEncoding());

		newConfiguration.setDelimiter("TAB_PREFERENCE");
		newConfiguration.setEncoding(Encoding.RAW_HEX);
		newConfiguration.setQuantity(false);
		manager.set(newConfiguration);

		newConfiguration.setDelimiter("STANDARD_PREFERENCE");
		newConfiguration.setEncoding(Encoding.EPC_HEX);
		newConfiguration.setQuantity(false);
		manager.set(newConfiguration);

		Assert.assertEquals("STANDARD_PREFERENCE", manager.get().getDelimiter());

		manager.reset();
	}

	@Test
	public void setWithError(final @Mocked Files files, final @Mocked IOException ioException) throws Exception {
		new NonStrictExpectations() {
			{
				Files.createDirectories(new File(Environment.CONFIG_FILE).toPath().getParent(), new FileAttribute<?>[] {});
				result = ioException;
			}
		};

		ConfigurationManager manager = new ConfigurationManager();

		Configuration configuration = new Configuration();

		try {
			manager.set(configuration);
		} catch (ConfigurationManagerException e) {
			Assert.assertEquals(ioException, e.getCause());
		}
	}

	@Test
	public void configurationManagerWithInvalidConfiguration(final @Mocked File configFile, final @Mocked ObjectMapper mapper) throws Exception {
		final Exception e = new Exception();

		new NonStrictExpectations() {
			{
				configFile.exists();
				result = true;

				mapper.readValue(new File(Environment.CONFIG_FILE), Configuration.class);
				result = e;
			}
		};
		try {
			new ConfigurationManager();
		} catch (ConfigurationManagerException e1) {
			Assert.assertSame(e, e1.getCause());
		}

	}

	@AfterClass
	public static void clear() throws IOException {
		deleteDir(new File("conf"));
	}

	private static void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

}
