package se.fortnox.reactivewizard.helloworld;

import com.fasterxml.jackson.databind.JsonNode;
import rx.Observable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/anything")
public interface GoogleResource {
    @GET
    Observable<JsonNode> getIndex();
}
