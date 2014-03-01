package ph.edu.dlsu.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ph.edu.dlsu.model.Instruction;
import ph.edu.dlsu.model.MemoryAddress;
import ph.edu.dlsu.service.MemoryAddressService;
import ph.edu.dlsu.service.MemoryAddressServiceImpl;

@Path("memory")
public class MemoryAddressResource {

	private static final int HEX = 16;
	private MemoryAddressService memoryAddressService = new MemoryAddressServiceImpl();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response find(@QueryParam(value = "from") String startAddress, @QueryParam(value = "to") String endAddress) {
		System.out.println("startAddress: " + startAddress + ", endAddress: " + endAddress);
		
		if (isMemoryAddressValid(Integer.parseInt(startAddress, HEX), Integer.parseInt(endAddress, HEX))) {
			List<MemoryAddress> memoryAddresses = memoryAddressService.find(startAddress, endAddress);
			return Response.ok().entity(new GenericEntity<List<MemoryAddress>>(memoryAddresses) {}).build();
		}

		return Response.status(Status.BAD_REQUEST).build();
	}

	private boolean isMemoryAddressValid(int startAddress, int endAddress) {
		if (endAddress < startAddress || endAddress > MemoryAddress.MAX_MEMORY) {
			return false;
		}
		return true;
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{address}")
	public Response update(@PathParam("address") String address, MemoryAddress request) {
		int intAddress = Integer.parseInt(address, 16);
		if (!address.equals(request.getAddress()) || intAddress < MemoryAddress.MIN_UPDATABLE || intAddress > MemoryAddress.MAX_MEMORY) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		MemoryAddress memoryAddress = memoryAddressService.update(request);
		return Response.ok().entity(memoryAddress).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response compile(List<Instruction> instructions) {
		for (Instruction i : instructions) {
			System.out.println(i.toString());
		}
		memoryAddressService.compile(instructions);
		return Response.ok().build();
	}

}
