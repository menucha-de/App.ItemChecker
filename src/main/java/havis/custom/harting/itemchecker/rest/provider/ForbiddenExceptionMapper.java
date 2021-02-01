package havis.custom.harting.itemchecker.rest.provider;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

	@Override
	public Response toResponse(ForbiddenException e) {
		return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}