package havis.app.itemchecker.rest.async;

import havis.app.itemchecker.Item;
import havis.app.itemchecker.Sighting;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.TextCallback;

@Path("../rest/itemchecker")
public interface ItemCheckerServiceAsync extends RestService {

	@GET
	@Path("itemlist")
	void getItems(MethodCallback<List<Item>> callback);

	@PUT
	@Path("active")
	@Consumes({ MediaType.TEXT_PLAIN })
	void setActive(String active, MethodCallback<Void> callback);

	@GET
	@Path("active")
	@Produces({ MediaType.TEXT_PLAIN })
	void getActive(TextCallback active);
	
	@POST
	@Path("itemlist/sightings")
	@Consumes({ MediaType.APPLICATION_JSON })
	void addSighting(List<Sighting> sightings, MethodCallback<Void> callback);

	@PUT
	@Path("configuration/encoding")
	@Consumes({ MediaType.TEXT_PLAIN })
	void setEncoding(String encoding, MethodCallback<Void> callback);

	@GET
	@Path("configuration/encoding")
	@Produces({ MediaType.TEXT_PLAIN })
	void getEncoding(TextCallback encoding);
	
	@PUT
	@Path("configuration/delimiter")
	@Consumes({ MediaType.TEXT_PLAIN })
	void setDelimiter(String encoding, MethodCallback<Void> callback);

	@GET
	@Path("configuration/delimiter")
	@Produces({ MediaType.TEXT_PLAIN })
	void getDelimiter(TextCallback encoding);

	@DELETE
	@Path("itemlist")
	void clear(MethodCallback<Void> callback);

}