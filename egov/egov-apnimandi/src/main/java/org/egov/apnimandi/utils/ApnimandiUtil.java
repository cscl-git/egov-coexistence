package org.egov.apnimandi.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.apnimandi.utils.constants.ApnimandiConstants;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

@Service
public class ApnimandiUtil {
	
	@Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
	
	@Autowired
    private SecurityUtils securityUtils;
	
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
    
//	public static void main(String[] args) {
//		Date terminateDate = DateUtils.getDate("28-05-2020", "dd-MM-yyyy");
//		Date toDate = DateUtils.getDate("29-05-2020", "dd-MM-yyyy");
//		System.out.println("result : " + isTerminateDateGreaterThanEqualToValidToDate(terminateDate, toDate));
//	}
}
