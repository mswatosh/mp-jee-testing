package org.aguibert.liberty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.aguibert.testcontainers.framework.jupiter.MicroProfileTest;
import org.aguibert.testcontainers.framework.jupiter.RestClient;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.google.common.net.MediaType;

@Testcontainers
@MicroProfileTest
public class DependentServiceTest {

    @ClassRule
    public static Network network = Network.newNetwork();

    @Container
    public static MockServerContainer mockServer = new MockServerContainer()
                    .withNetwork(network)
                    .withNetworkAliases("mockserver");

    @Container
    public static MicroProfileApplication<?> myService = new MicroProfileApplication<>("my-service", "myservice")
                    .withNetwork(network)
                    .withEnv("org_aguibert_liberty_ExternalRestServiceClient_mp_rest_url", "http://mockserver:" + MockServerContainer.PORT);

    @RestClient
    public static PersonServiceWithPassthrough personSvc;

    static final Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testCreatePerson() {
        Person expectedPerson = new Person("Hank", 42, 5L);
        new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort())
                        .when(request("/mock-passthrough/person/5"))
                        .respond(response().withBody(jsonb.toJson(expectedPerson), MediaType.JSON_UTF_8));

        Person actualPerson = personSvc.getPersonFromExternalService(5);
        assertEquals("Hank", actualPerson.name);
        assertEquals(42, actualPerson.age);
        assertEquals(5, actualPerson.id);
    }

}