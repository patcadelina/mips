package ph.edu.dlsu.resource;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ph.edu.dlsu.service.MemoryAddressService;
import ph.edu.dlsu.service.MemoryAddressServiceImpl;
import ph.edu.dlsu.service.RegisterService;
import ph.edu.dlsu.service.RegisterServiceImpl;

@Path("system")
public class SystemResource {

	private RegisterService registerService = new RegisterServiceImpl();
	private MemoryAddressService memoryAddressService = new MemoryAddressServiceImpl();

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

}
