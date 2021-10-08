/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.lcms.web.controller.ajax;

import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AjaxEmployeePositionController {

    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    MicroserviceUtils microserviceUtils;

    @RequestMapping(value = "/ajax/getpositionEmployee", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<Long, String> getPositionForDeptAndDesig(
            @ModelAttribute("employeeBean") @RequestParam final String positionName) {
        final Map<Long, String> positionEmployeeMap = new HashMap<Long, String>();
        String posEmpName;
        final List<Assignment> assignmentList = assignmentService
                .getAllAssignmentsByPositionNameForGivenRange(positionName);
        for (final Assignment assign : assignmentList) {
            posEmpName = assign.getPosition().getName().concat("@").concat(assign.getEmployee().getUsername());
            positionEmployeeMap.put(assign.getEmployee().getId(), posEmpName);
        }
        return positionEmployeeMap;
    }

    @RequestMapping(value = "/ajax/getposition", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<Long, String> getPosition(
            @ModelAttribute("employeeBean") @RequestParam final String positionName) {
        final Map<Long, String> positionEmployeeMap = new HashMap<Long, String>();
        String posEmpName;
        final List<Assignment> assignmentList = assignmentService
                .getAllAssignmentsByPositionNameForGivenRange(positionName);
        for (final Assignment assign : assignmentList) {
            posEmpName = assign.getPosition().getName().concat("@").concat(assign.getEmployee().getUsername());
            positionEmployeeMap.put(assign.getPosition().getId(), posEmpName);
        }
        return positionEmployeeMap;
    }
    
    @RequestMapping(value = "ajax/getemployeeNames", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<Long, String> getEmployeeForDeptAndDesig(
            @ModelAttribute("employeeBean") @RequestParam final String employeeName) {
        final Map<Long, String> employeeMap = new HashMap<Long, String>();
        String empName;
        final List<Assignment> assignmentList = assignmentService.getAllAssignmentForEmployeeNameLike(employeeName);
        for (final Assignment assign : assignmentList)
            if (assign != null) {
                empName = assign.getEmployee().getName().concat("@")
                        .concat(assign.getDepartment().getName().concat("@").concat(assign.getDesignation().getName()));
                employeeMap.put(assign.getEmployee().getId(), empName);
            }
        return employeeMap;
    }
    
    @RequestMapping(value = "ajax/getEmployeesByDeptAndDesig", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<Long, String> getEmployeeByDeptAndDesig(@RequestParam final String deptId, @RequestParam final String desgnId) {
        final Map<Long, String> employeeMap = new HashMap<Long, String>();
        List<EmployeeInfo> employees = microserviceUtils.getApprovers(deptId, desgnId);
        for (final EmployeeInfo employee : employees) {
            if (employee != null) {
                employeeMap.put(employee.getUser().getId(), employee.getUser().getName());
            }
        }
        return employeeMap;
    }
    
    @RequestMapping(value = "ajax/getDepartments", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, String> getDepartments() {
        final Map<String, String> departmentsMap = new HashMap<String, String>();
        List<Department> departments = microserviceUtils.getDepartments();
        for (final Department department : departments) {
            if (department != null) {
            	departmentsMap.put(department.getCode(), department.getName());
            }
        }
        return departmentsMap;
    }
    
    @RequestMapping(value = "ajax/getDesignations", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, String> getDesignations() {
        final Map<String, String> designationsMap = new HashMap<String, String>();
        List<Designation> designations = microserviceUtils.getDesignations();
        for (final Designation designation : designations) {
            if (designation != null) {
            	designationsMap.put(designation.getCode(), designation.getName());
            }
        }
        return designationsMap;
    }
}