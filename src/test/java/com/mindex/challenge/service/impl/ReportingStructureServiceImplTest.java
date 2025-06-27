package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {
    @LocalServerPort
    private int port;
  
    @Autowired
    private TestRestTemplate restTemplate;

    private String url;
  
    @Before
    public void setup() {
        url = "http://localhost:" + port + "/reportingstructure/{id}";
    }

    @Test
    public void paulStructure() {
        ReportingStructure paul = restTemplate.getForEntity(
            url, 
            ReportingStructure.class, 
            "b7839309-3348-463b-a7e3-5de1c168beb3"
        ).getBody();
        assertNotNull(paul);
        assertEquals(0, paul.getNumberOfReports());
    }

    @Test
    public void ringoStructure() {
        ReportingStructure ringo = restTemplate.getForEntity(
            url, 
            ReportingStructure.class, 
            "03aa1462-ffa9-4978-901b-7c001562cf6f"
        ).getBody();
        assertNotNull(ringo);
        assertEquals(2, ringo.getNumberOfReports());
    }

    @Test
    public void johnStructure() {
        ReportingStructure john = restTemplate.getForEntity(
            url, 
            ReportingStructure.class, 
            "16a596ae-edd3-4847-99fe-c4518e82c86f"
            ).getBody();
        assertNotNull(john);
        assertEquals(4, john.getNumberOfReports());
    }

    @Test
    public void noReports() {
        Employee employee = new Employee();
        var service = new ReportingStructureServiceImpl();

        assertEquals(0, service.calculateNumberOfReports(employee));

        employee.setDirectReports(Collections.emptyList());
        assertEquals(0, service.calculateNumberOfReports(employee));
    }

    @Test
    public void directReports() {
        Employee employee = new Employee();
        employee.setDirectReports(Arrays.asList(new Employee(), new Employee()));
        var service = new ReportingStructureServiceImpl();
        assertEquals(2, service.calculateNumberOfReports(employee));
    }

    @Test
    public void twoNestedReports() {
        Employee e1 = new Employee();
        Employee e2 = new Employee();
        Employee e3 = new Employee();
        Employee e4 = new Employee();
        e3.setDirectReports(Arrays.asList(e4));
        e1.setDirectReports(Arrays.asList(e2, e3));
        var service = new ReportingStructureServiceImpl();

        assertEquals(3, service.calculateNumberOfReports(e1));
    }

    @Test
    public void threeNestedReports() {
        Employee e1 = new Employee();
        Employee e2 = new Employee();
        Employee e3 = new Employee();
        Employee e4 = new Employee();
        Employee e5 = new Employee();
        e4.setDirectReports(Arrays.asList(e5));
        e3.setDirectReports(Arrays.asList(e4));
        e1.setDirectReports(Arrays.asList(e2, e3));
        var service = new ReportingStructureServiceImpl();

        assertEquals(4, service.calculateNumberOfReports(e1));
    }
}