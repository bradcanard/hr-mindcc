package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Getting the reporting structure for employee [{}]", id);
        // Verify the employee id exists
        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        var directReports = getDirectReports(employee.getDirectReports());
        employee.setDirectReports(directReports);

        var numberOfReports = calculateNumberOfReports(employee);
        var reportingStructure = new ReportingStructure(employee, numberOfReports);
        return reportingStructure;
    }

    /**
     * The EmployeeRepository doesn't populate the direct reports so get each direct report employee.
     * There is a performance penalty since it is making db calls for each employee.
     * @param employees
     * @return
     */
    private List<Employee> getDirectReports(List<Employee> employees) {
        List<Employee> populatedEmployees = new ArrayList<>();
        if (employees != null && !employees.isEmpty()) {
            for (Employee employee : employees) {
                var emp = employeeRepository.findByEmployeeId(employee.getEmployeeId());
                if (emp.getDirectReports() != null && !emp.getDirectReports().isEmpty()) {
                    emp.setDirectReports(getDirectReports(emp.getDirectReports()));
                }
                populatedEmployees.add(emp);
            }
        }
        return populatedEmployees;
    }

    /**
     * Uses recursion to calculate the number of reports. 
     * It seemed the simplest way to handle n levels of reporting structure.
     * I considered using streams, but I was struggling to make it work with multiple levels.
     * I also tried using 'forEach', but it also wasn't working with multiple levels.
     * Ultimately, I kept going back to needing recursion. 
     * I went with the simplest approach that is easiest to read/maintain.
     * @param employee to get the number of reports for
     * @return int the number of reports for the employee parameter
     */
    int calculateNumberOfReports(@NonNull Employee employee) {
        // break condition. This employee has no direct reports.
        if (employee.getDirectReports() == null || employee.getDirectReports().isEmpty()) {
            return 0;
        } else {
            int reports = employee.getDirectReports().size();
            for (Employee e : employee.getDirectReports()) {
                reports += calculateNumberOfReports(e);
            }
            return reports;
        }
    }
}
