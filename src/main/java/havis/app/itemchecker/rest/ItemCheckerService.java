package havis.app.itemchecker.rest;

import havis.app.itemchecker.ConfigurationManager;
import havis.app.itemchecker.Encoding;
import havis.app.itemchecker.Item;
import havis.app.itemchecker.ItemChecker;
import havis.app.itemchecker.ItemCheckerException;
import havis.app.itemchecker.Sighting;
import havis.net.rest.shared.Resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("itemchecker")
public class ItemCheckerService extends Resource {
	private ItemChecker itemChecker;
	private ConfigurationManager manager;

	public ItemCheckerService(ItemChecker itemChecker, ConfigurationManager manager) {
		this.itemChecker = itemChecker;
		this.manager = manager;
		try {
			itemChecker.setCurrentEncoding(manager.get().getEncoding(), false);
			itemChecker.setDelimiter(manager.get().getDelimiter());
		} catch (Exception e) {
		}
	}

	@PermitAll
	@GET
	@Path("active")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getActive() {
		return "" + itemChecker.getStatus();
	}

	@PermitAll
	@PUT
	@Path("active")
	@Consumes({ MediaType.TEXT_PLAIN })
	public void setActive(String active) throws ItemCheckerException {
		boolean isActive = Boolean.parseBoolean(active);
		if (isActive) {
			itemChecker.startScan();
		} else {
			itemChecker.stopScan();
		}
	}

	@PermitAll
	@GET
	@Path("itemlist")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Item> getItems() throws ItemCheckerException {
		return itemChecker.getItems();
	}

	@PermitAll
	@POST
	@Path("itemlist")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void addItem(Item item) throws ForbiddenException {
		throw new ForbiddenException("Forbidden");
	}

	@PermitAll
	@DELETE
	@Path("itemlist")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void clear() throws ItemCheckerException {
		itemChecker.clear();
	}

	@PermitAll
	@GET
	@Path("itemlist/{code}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Item getItem(@PathParam("code") String code) throws ItemCheckerException {
		return itemChecker.getItem(code);
	}

	@PermitAll
	@PUT
	@Path("itemlist/{code}")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void putItem(@PathParam("code") String code) throws ForbiddenException {
		throw new ForbiddenException("Forbidden");
	}

	@PermitAll
	@DELETE
	@Path("itemlist/{code}")
	public void deleteItem(@PathParam("code") String code) throws ForbiddenException {
		throw new ForbiddenException("Forbidden");
	}

	@PermitAll
	@POST
	@Path("itemlist/sightings")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void addSighting(List<Sighting> sightings) throws ItemCheckerException {
		if (itemChecker.getStatus()) {
			itemChecker.addSightings(sightings);
		} else {
			throw new ItemCheckerException("The backend service is not ready.");
		}
	}

	@POST
	@Path("itemlist/file")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public void setItemsCSV(InputStream stream) throws ItemCheckerException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			itemChecker.importCsv(reader);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ItemCheckerException(e);
		}
	}

	@GET
	@Path("itemlist/file")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getItemsCSV() throws ItemCheckerException {
		StringWriter writer = new StringWriter();
		itemChecker.exportCsv(writer);
		String filename = String.format("ItemList_%s.csv", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		byte[] data = writer.toString().getBytes(StandardCharsets.UTF_8);
		return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
				.header("Content-Type", "text/plain; charset=utf-8").header("Content-Length", data.length).build();
	}

	@PUT
	@Path("configuration/encoding")
	@Consumes(MediaType.TEXT_PLAIN)
	public void setEncoding(String encoding) throws ItemCheckerException {
		try {
			Encoding enc = Encoding.valueOf(encoding);
			manager.get().setEncoding(enc);
			manager.set(manager.get());
			itemChecker.setCurrentEncoding(enc, true);
		} catch (Exception e) {
			throw new ItemCheckerException(e);
		}
	}

	@GET
	@Path("configuration/encoding")
	@Produces(MediaType.TEXT_PLAIN)
	public String getEncoding() {
		return "" + manager.get().getEncoding();
	}

	@PUT
	@Path("configuration/delimiter")
	@Consumes(MediaType.TEXT_PLAIN)
	public void setDelimiter(String delimiter) throws ItemCheckerException {
		try {
			manager.get().setDelimiter(delimiter);
			manager.set(manager.get());
			itemChecker.setDelimiter(manager.get().getDelimiter());
		} catch (Exception e) {
			throw new ItemCheckerException(e);
		}
	}

	@GET
	@Path("configuration/delimiter")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDelimiter() {
		return manager.get().getDelimiter();
	}

	@GET
	@Path("configuration/quantity")
	@Produces(MediaType.TEXT_PLAIN)
	public String getQuantity() {
		return "" + manager.get().isQuantity();
	}

}