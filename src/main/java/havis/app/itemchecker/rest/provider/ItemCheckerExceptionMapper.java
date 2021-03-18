package havis.app.itemchecker.rest.provider;

import havis.app.itemchecker.ItemCheckerException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ItemCheckerExceptionMapper implements ExceptionMapper<ItemCheckerException> {

	@Override
	public Response toResponse(ItemCheckerException e) {
		return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}