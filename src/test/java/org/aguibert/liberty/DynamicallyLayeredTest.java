package org.aguibert.liberty;

import static org.junit.Assert.assertNotNull;

import org.aguibert.testcontainers.framework.ComposedMicroProfileApplication;
import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.aguibert.testcontainers.framework.jupiter.MicroProfileTest;
import org.aguibert.testcontainers.framework.jupiter.RestClient;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@MicroProfileTest
public class DynamicallyLayeredTest {

    /**
     * This approach can be used when only a .war file is produced by the build, and there is no
     * docker knowledge in the project. This essentially does:
     * FROM open-liberty:microProfile2
     * ADD build/libs/myservice.war /config/dropins
     * COPY src/main/liberty/config /config/
     */
    @Container
    public static MicroProfileApplication<?> myService = new ComposedMicroProfileApplication<>()
                    .withAppContextRoot("myservice");

    @RestClient
    public static PersonService personSvc;

    @Test
    public void testCreatePerson() {
        Long createId = personSvc.createPerson("Hank", 42);
        assertNotNull(createId);
    }

}