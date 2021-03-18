package havis.app.itemchecker;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvResultSetWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public class ItemManager {
	private final static int ID = 1, CODE = 2, DESCRIPTION = 3, QUANTITY = 4, STATE = 5;
	private final static String CLEAR = "DELETE FROM items";
	private final static String SELECT = "SELECT id, code, description, quantity, state FROM items";
	private final static String INSERT = "INSERT INTO items (id, code, description, quantity, state) VALUES (?, ?, ?, ?, ?)";
	private final static String UPDATE = "UPDATE items SET id=?, code=?, description=?, quantity=?, state=? WHERE code=?";

	private Connection connection;
	private CsvPreference preference = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;

	private final static CellProcessor processor = new CellProcessor() {
		@SuppressWarnings("unchecked")
		@Override
		public String execute(Object value, CsvContext context) {
			if (value instanceof Clob) {
				Clob clob = (Clob) value;
				try {
					try (InputStream stream = clob.getAsciiStream()) {
						byte[] bytes = new byte[stream.available()];
						stream.read(bytes);
						return new String(bytes, StandardCharsets.UTF_8);
					}
				} catch (Exception e) {
					// log.log(Level.FINE, "Failed to read column data", e);
				}
			}
			return null;
		}
	};

	private final static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] { new Optional(), // id
				new NotNull(), // code
				new Optional(), // desciption
				new Optional(new ParseInt()), // quantity
				new Optional(new ParseInt()) // state
		};
		return processors;
	}

	public ItemManager() throws ItemCheckerException {
		try {
			connection = DriverManager.getConnection(Environment.JDBC_URL, Environment.JDBC_USERNAME, Environment.JDBC_PASSWORD);
		} catch (SQLException e) {
			throw new ItemCheckerException("Failed to get connection", e);
		}
	}

	public synchronized int clear() throws ItemCheckerException {
		try (Statement stmt = connection.createStatement()) {
			return stmt.executeUpdate(CLEAR);
		} catch (SQLException e) {
			throw new ItemCheckerException(e);
		}
	}

	public synchronized void addItem(Item item) throws ItemCheckerException {
		try (PreparedStatement stmt = connection.prepareStatement(INSERT)) {
			stmt.setString(ID, item.getId());
			stmt.setString(CODE, item.getCode());
			stmt.setString(DESCRIPTION, item.getDescription());
			stmt.setInt(QUANTITY, item.getCount());
			stmt.setInt(STATE, item.getState());
			stmt.execute();
		} catch (SQLException e) {
			throw new ItemCheckerException(e);
		}
	}

	public synchronized void updateItem(Item item) throws ItemCheckerException {
		try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
			stmt.setString(ID, item.getId());
			stmt.setString(CODE, item.getCode());
			stmt.setString(DESCRIPTION, item.getDescription());
			stmt.setInt(QUANTITY, item.getCount());
			stmt.setInt(STATE, item.getState());
			stmt.setString(6/* WHERE */, item.getCode());
			stmt.execute();
		} catch (SQLException e) {
			throw new ItemCheckerException(e);
		}
	}

	public synchronized List<Item> getEntries() throws ItemCheckerException {
		List<Item> result = new ArrayList<>();
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(SELECT)) {
			while (rs.next()) {
				Item item = new Item();
				item.setId(rs.getString(ID));
				item.setCode(rs.getString(CODE));
				item.setDescription(rs.getString(DESCRIPTION));
				item.setCount(rs.getInt(QUANTITY));
				item.setState(rs.getInt(STATE));
				result.add(item);
			}
		} catch (SQLException e) {
			throw new ItemCheckerException(e);
		}
		return result;
	}

	public synchronized void marshal(Writer writer) throws SQLException, IOException, ItemCheckerException {
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(SELECT)) {
			ResultSetMetaData data = rs.getMetaData();
			CellProcessor[] processors = new CellProcessor[data.getColumnCount()];
			for (int i = 0; i < data.getColumnCount(); i++)
				if (data.getColumnType(i + 1) == Types.CLOB)
					processors[i] = processor;
			try (CsvResultSetWriter csv = new CsvResultSetWriter(writer, preference)) {
				csv.write(rs, processors);
				csv.flush();
			}
		}
	}

	public synchronized void unmarshal(Reader reader) throws IOException, ItemCheckerException {

		ICsvListReader listReader = null;
		try {
			listReader = new CsvListReader(reader, preference);

			listReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();

			List<Object> itemList;
			while ((itemList = listReader.read(processors)) != null) {
				try (PreparedStatement stmt = connection.prepareStatement(INSERT)) {
					stmt.setString(ID, (String) itemList.get(ID - 1));
					stmt.setString(CODE, (String) itemList.get(CODE - 1));
					stmt.setString(DESCRIPTION, (String) itemList.get(DESCRIPTION - 1));
					stmt.setInt(QUANTITY, (int) itemList.get(QUANTITY - 1));
					stmt.setInt(STATE, -1 /* ignore state */);
					stmt.execute();
				} catch (SQLException e) {
					throw new ItemCheckerException(e);
				}
			}

		} finally {
			if (listReader != null) {
				listReader.close();
			}
		}

	}

	public synchronized void close() throws ItemCheckerException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new ItemCheckerException(e);
		}
	}

	public synchronized void setPreference(String delimiter) {
		preference = CsvPreference.STANDARD_PREFERENCE;
		switch (delimiter) {
		case "EXCEL_NORTH_EUROPE_PREFERENCE":
			preference = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
			break;
		case "EXCEL_PREFERENCE":
			preference = CsvPreference.EXCEL_PREFERENCE;
			break;
		case "TAB_PREFERENCE":
			preference = CsvPreference.TAB_PREFERENCE;
			break;
		default:
			preference = CsvPreference.STANDARD_PREFERENCE;
		}
	}

}
