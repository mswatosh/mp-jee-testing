package org.aguibert.liberty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.aguibert.testcontainers.framework.jupiter.MicroProfileTest;
import org.aguibert.testcontainers.framework.jupiter.RestClient;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@MicroProfileTest
public class MongoAndLibertyTest {

    @ClassRule
    public static Network network = Network.newNetwork();

    @Container
    public static MicroProfileApplication<?> myService = new MicroProfileApplication<>("my-service")
                    .withAppContextRoot("/myservice")
                    .withNetwork(network)
                    .withEnv("MONGO_HOSTNAME", "testmongo")
                    .withEnv("MONGO_PORT", "27017");

    @Container
    public static GenericContainer<?> mongo = new GenericContainer<>("mongo:3.4")
                    .withNetwork(network)
                    .withNetworkAliases("testmongo");

    @RestClient
    public static PersonServiceWithMongo personSvc;

    @Test
    public void testGetPerson() {
        Long bobId = personSvc.createPerson("Bob", 24);
        Person bob = personSvc.getPerson(bobId);
        assertEquals("Bob", bob.name);
        assertEquals(24, bob.age);
    }

    @Test
    public void testGetAllPeople() {
        Long person1Id = personSvc.createPerson("Person1", 1);
        Long person2Id = personSvc.createPerson("Person2", 2);

        Person expected1 = new Person("Person1", 1, person1Id);
        Person expected2 = new Person("Person2", 2, person2Id);

        Collection<Person> allPeople = personSvc.getAllPeople();
        assertTrue(allPeople.size() >= 2, "Expected at least 2 people to be registered, but there were only: " + allPeople);
        assertTrue(allPeople.contains(expected1), "Did not find person " + expected1 + " in all people: " + allPeople);
        assertTrue(allPeople.contains(expected2), "Did not find person " + expected2 + " in all people: " + allPeople);
    }

}