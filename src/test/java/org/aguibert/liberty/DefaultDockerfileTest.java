package org.aguibert.liberty;

import static org.junit.Assert.assertNotNull;

import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.aguibert.testcontainers.framework.jupiter.MicroProfileTest;
import org.aguibert.testcontainers.framework.jupiter.RestClient;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@MicroProfileTest
public class DefaultDockerfileTest {

    /**
     * This approach can be used when a Dockerfile has been created by the user,
     * but the docker build may not be tied into the local maven/gradle build
     */
    @Container
    public static MicroProfileApplication<?> myService = new MicroProfileApplication<>()
                    .withAppContextRoot("myservice");

    @RestClient
    public static PersonService personSvc;

    @Test
    public void testCreatePerson() {
        Long createId = personSvc.createPerson("Hank", 42);
        assertNotNull(createId);
    }

}