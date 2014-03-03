package ph.edu.dlsu.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ph.edu.dlsu.model.Pipeline;
import ph.edu.dlsu.model.Register;
import ph.edu.dlsu.service.MemoryAddressService;
import ph.edu.dlsu.service.MemoryAddressServiceImpl;
import ph.edu.dlsu.service.RegisterService;
import ph.edu.dlsu.service.RegisterServiceImpl;
import ph.edu.dlsu.service.SystemService;
import ph.edu.dlsu.service.SystemServiceImpl;

@Path("system")
public class SystemResource {

	private RegisterService registerService = new RegisterServiceImpl();
	private MemoryAddressService memoryAddressService = new MemoryAddressServiceImpl();
	private SystemService systemService = new SystemServiceImpl();

	@PUT
	@Path("init")
	public Response init() {
		try {
			registerService.init();
			memoryAddressService.init();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok().build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("registers")
	public Response findSystemRegisters() {
		List<Register> registers = registerService.findSystemRegisters();
		return Response.ok().entity(new GenericEntity<List<Register>>(registers) {}).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("clock/{cycle}")
	public Response clock(@PathParam("cycle") Integer cycle) throws IOException {
		System.out.println("Processing cycle: " + cycle);
		Pipeline pipeline = systemService.runCycle(cycle);
		return Response.ok().entity(pipeline).build();
	}

}
