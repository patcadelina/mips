package ph.edu.dlsu.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ph.edu.dlsu.model.Register;
import ph.edu.dlsu.service.RegisterService;
import ph.edu.dlsu.service.RegisterServiceImpl;

@Path("registers")
public class RegisterResource {

	private RegisterService registerService = new RegisterServiceImpl();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll() {
		List<Register> registers = registerService.findAll();
		return Response.ok().entity(new GenericEntity<List<Register>>(registers) {}).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{registerId}")
	public Response find(@PathParam("registerId") String registerId) {
		if (null == registerId) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Register register = registerService.find(registerId);
		if (null == register) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().entity(register).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{registerId}")
	public Response update(@PathParam("registerId") String registerId, Register request) {
		if (null == registerId || !registerId.equals(request.getName()) || !isRegisterWritable(registerId)) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Register register = registerService.update(request);
		return Response.ok().entity(register).build();
	}

	private boolean isRegisterWritable(String registerId) {
		if (registerId.equals("R0") || registerId.equals("PC") || registerId.equals("hi") || registerId.equals("lo") || registerId.equals("c0"))
			return false;
		return true;
	}

}
