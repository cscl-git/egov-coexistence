package org.egov.apnimandi.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowDeptDesgMap;
import org.egov.infra.workflow.matrix.service.WorkFlowDeptDesgMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApnimandiUtil {
	
	@Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
	
	@Autowired
    private SecurityUtils securityUtils;
	
	@Autowired
    protected WorkFlowDeptDesgMapService workFlowDeptDesgMapService;

    @Autowired
    MicroserviceUtils microserviceUtils;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public EgwStatus getStatusForModuleAndCode(final String moduleName, final String statusCode) {
        return egwStatusDAO.getStatusByModuleAndCode(moduleName, statusCode);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<EgwStatus> getStatusForModule(String moduleType) {
        return egwStatusDAO.getStatusByModule(moduleType);
    }
    
    public static Date getLastDateOfMonth(int month, int year) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("MM/yyyy");
        String dateString = ((month<10)?"0"+month:month) + "/" + year;
        YearMonth yearMonth = YearMonth.parse(dateString, pattern);
        LocalDate localdate = yearMonth.atEndOfMonth();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(localdate.atStartOfDay(defaultZoneId).toInstant());
        return date;
    }
    
    public static Map<Integer, String> getYears(){
    	final Map<Integer, String> YEARS = new HashMap<Integer, String>();
    	int startYear = Integer.parseInt("2000");
        final Calendar cal = Calendar.getInstance();
        int currentYear =cal.get(Calendar.YEAR);
        for(int year=startYear;year <= (currentYear+20);year++) {
        	YEARS.put(year, String.valueOf(year));
        }
        return YEARS;
    }
    
    public long getLoggedInUserId() {
        return securityUtils.getCurrentUser().getId();
    }
    
    public static String getMonthFullName(int monthNo) {
    	return DateUtils.getAllMonthsWithFullNames().get(monthNo);
    }
    
    public static boolean isFirstDateGreaterThanEqualToSecondDate(Date firstDate, Date secondDate) {
    	String firstDateString = DateUtils.getFormattedDate(firstDate, "dd-MM-yyyy");
    	String secondDateString = DateUtils.getFormattedDate(secondDate, "dd-MM-yyyy");
    	if(firstDateString.equals(secondDateString)) {
    		return true;
    	}else {
    		return DateUtils.compareDates(firstDate, secondDate);
    	}
    }
    
    public List<Department> getDepartmentsByZone(final String currentState, final String objectType, final String additionalRule){
    	List<Department> departmentList = new ArrayList<Department>();    	
    	List<WorkFlowDeptDesgMap> deptDesgMap = null;		
    	if(!StringUtils.isBlank(additionalRule)) {
			deptDesgMap = workFlowDeptDesgMapService.findByObjectTypeAndCurrentStateAndAddRule(objectType, currentState, additionalRule);
		}else {
			deptDesgMap = workFlowDeptDesgMapService.findByObjectTypeAndCurrentState(objectType, currentState);
		}
		
		if(!CollectionUtils.isEmpty(deptDesgMap)) {
			String deptCodes = deptDesgMap.stream().map(WorkFlowDeptDesgMap::getNextDepartment).collect(Collectors.joining(","));
			departmentList = microserviceUtils.getDepartments(deptCodes);
		}		
        return departmentList;
    }
}
