package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String employeeUrl;
    private String compensationUrl;
    private Employee employee;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        compensationUrl = "http://localhost:" + port + "/employee/{id}/compensations";

        Employee emp = new Employee();
        emp.setFirstName("Jane");
        emp.setLastName("Doe");
        emp.setDepartment("Engineering");
        emp.setPosition("Developer");

        // Create employee to associate compensations to.
        employee = restTemplate.postForEntity(employeeUrl, emp, Employee.class).getBody();
    }

    @Test
    public void testCreateRead() {
        // Create compensations
        Compensation compensation1 = new Compensation();
        compensation1.setEffectiveDate(LocalDate.of(2023, 2, 20));
        compensation1.setSalary(175000);
        Compensation savedCompensation1 = restTemplate.postForEntity(compensationUrl, compensation1, Compensation.class, employee.getEmployeeId()).getBody();
        assertNotNull(savedCompensation1);
        assertEquals(employee.getEmployeeId(), savedCompensation1.getEmployeeId());

        Compensation compensation2 = new Compensation();
        compensation2.setEffectiveDate(LocalDate.of(2024, 2, 20));
        compensation2.setSalary(192500);
        Compensation savedCompensation2 = restTemplate.postForEntity(compensationUrl, compensation2, Compensation.class, employee.getEmployeeId()).getBody();
        assertNotNull(savedCompensation2);
        assertEquals(employee.getEmployeeId(), savedCompensation2.getEmployeeId());

        Compensation[] compensations = restTemplate.getForEntity(compensationUrl, Compensation[].class, employee.getEmployeeId()).getBody();
        assertNotNull(compensations);
        assertEquals(2, compensations.length);
    }
}
