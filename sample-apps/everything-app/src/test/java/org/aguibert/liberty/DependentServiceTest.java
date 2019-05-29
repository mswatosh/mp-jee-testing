package org.aguibert.liberty;

import static org.aguibert.liberty.AppContainerConfig.mockServer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.aguibert.testcontainers.framework.jupiter.MicroProfileTest;
import org.aguibert.testcontainers.framework.jupiter.SharedContainerConfig;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.google.common.net.MediaType;

@SuppressWarnings("resource")

@Testcontainers
@MicroProfileTest
@SharedContainerConfig(AppContainerConfig.class)
public class DependentServiceTest {

    @Inject
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