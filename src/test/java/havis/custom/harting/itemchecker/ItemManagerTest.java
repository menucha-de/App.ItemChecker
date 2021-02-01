package havis.custom.harting.itemchecker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class ItemManagerTest {
	@Test
	public void itemManagerDefaultException(@Mocked final Connection connection) throws ItemCheckerException, IOException {
		clear();
		try {
			@SuppressWarnings("unused")
			ItemManager manager = new ItemManager();
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
	}

	@Test
	public void itemHandling(@Mocked final Connection connection, @Mocked final DriverManager driver, @Mocked final Statement statement,
			@Mocked final PreparedStatement preparedStatement) throws ItemCheckerException, SQLException {
		ItemManager manager = new ItemManager();
		manager.clear();
		manager.setPreference("EXCEL_NORTH_EUROPE_PREFERENCE");
		manager.addItem(new Item());
		manager.updateItem(new Item());

		manager.close();
		new NonStrictExpectations() {
			{
				statement.executeUpdate(this.<String> withNotNull());
				result = new SQLException();
				statement.executeQuery(this.<String> withNotNull());
				result = new SQLException();
				preparedStatement.execute();
				result = new SQLException();
				connection.close();
				result = new SQLException();
			}
		};
		try {
			manager.clear();
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
		try {
			manager.addItem(new Item());
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
		try {
			manager.updateItem(new Item());
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
		try {
			manager.close();
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
		try {
			manager.getEntries();
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
	}

	@Test
	public void unmarshal(@Mocked final Connection connection, @Mocked final DriverManager driver, @Mocked final Statement statement,
			@Mocked final PreparedStatement preparedStatement) throws ItemCheckerException, SQLException, IOException {
		ItemManager manager = new ItemManager();
		manager.unmarshal(new StringReader("id;code;description;quantity;state" + System.getProperty("line.separator") + "abc;xyz;test;1;1"));
		new NonStrictExpectations() {
			{
				preparedStatement.execute();
				result = new SQLException();
			}
		};
		try {
			manager.unmarshal(new StringReader("id;code;description;quantity;state" + System.getProperty("line.separator") + "abc;xyz;test;1;1"));
			Assert.fail();
		} catch (ItemCheckerException e) {
		}
	}

	@Test
	public void marshal() throws ItemCheckerException, SQLException, IOException {
		File folder = new File("conf/havis/custom/harting/itemchecker");
		folder.mkdirs();
		String db = "CREATE TABLE IF NOT EXISTS items (" + "id TEXT," + "code TEXT NOT NULL," + "description TEXT," + "quantity INT NOT NULL,"
				+ "state INT NOT NULL" + ");";
		Files.write(new File("conf/havis/custom/harting/itemchecker/items.sql").toPath(), db.getBytes(), StandardOpenOption.CREATE);
		ItemManager manager = new ItemManager();
		String data = "ID;CODE;DESCRIPTION;QUANTITY;STATE" + System.getProperty("line.separator") + "abc;xyz;test;1;-1" + System.getProperty("line.separator");
		manager.unmarshal(new StringReader(data));
		StringWriter writer = new StringWriter();
		manager.marshal(writer);
		Assert.assertEquals(data, writer.toString());
		Assert.assertEquals("xyz", manager.getEntries().get(0).getCode());
	}

	@AfterClass
	public static void clear() throws IOException {
		deleteDir(new File("conf"));
		new File("itemchecker.mv.db").delete();
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
