package org.egov.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DivisionReportPOJO {
	private String name;
	private boolean isglcode;
	private boolean isdep;
	private Set<String> glcodeused = new HashSet<String>();
	private BigDecimal amount;
	public String depcode;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isIsglcode() {
		return isglcode;
	}
	public void setIsglcode(boolean isglcode) {
		this.isglcode = isglcode;
	}
	public Set<String> getGlcodeused() {
		return glcodeused;
	}
	public void setGlcodeused(Set<String> glcodeused) {
		this.glcodeused = glcodeused;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public boolean isIsdep() {
		return isdep;
	}
	public void setIsdep(boolean isdep) {
		this.isdep = isdep;
	}
	public String getDepcode() {
		return depcode;
	}
	public void setDepcode(String depcode) {
		this.depcode = depcode;
	}
	public DivisionReportPOJO(String name, boolean isglcode, boolean isdep, Set<String> glcodeused, BigDecimal amount,
			String depcode) {
		super();
		this.name = name;
		this.isglcode = isglcode;
		this.isdep = isdep;
		this.glcodeused = glcodeused;
		this.amount = amount;
		this.depcode = depcode;
	}
	
	
	public DivisionReportPOJO() {
		
	}
	
	
	
	
	public List<DivisionReportPOJO> getDataList(){
		
		Set<String> a1 = new HashSet<String>();
		a1.add("1100101");
		Set<String> a2 = new HashSet<String>();
		a2.add("1100102");

		Set<String> a3 = new HashSet<String>();
		a3.add("1401102");

		Set<String> a4 = new HashSet<String>();
		a4.add("1105203");
		a4.add( "1404056");

		Set<String> a5 = new HashSet<String>();
		a5.add("1105202");
		
		Set<String> a6 = new HashSet<String>();
		a6.add("1105201");
		Set<String> a7 = new HashSet<String>();
		a7.add("1711001");
		Set<String> a8 = new HashSet<String>();
		a8.add("1701001");
		Set<String> a9 = new HashSet<String>();
		a9.add("1808001");
		
		List<DivisionReportPOJO> divisionReportPOJO = new ArrayList<DivisionReportPOJO>();
		DivisionReportPOJO  dr1 = new DivisionReportPOJO("Property Tax -Residential",true,false,a1,new BigDecimal(0),null);
		DivisionReportPOJO  dr2 = new DivisionReportPOJO("Property Tax -Commercial",true,false,a2,new BigDecimal(0),null);

		DivisionReportPOJO  dr3 = new DivisionReportPOJO("ROAD DIV 2",false,true,null,new BigDecimal(0),"428");	
		DivisionReportPOJO  dr4 = new DivisionReportPOJO("ROAD DIV 1",false,true,null,new BigDecimal(0),"427");
		DivisionReportPOJO  dr5 = new DivisionReportPOJO("ROAD DIV 3",false,true,null,new BigDecimal(0),"429");


		DivisionReportPOJO  dr6 = new DivisionReportPOJO("PUBLIC HEALTH DIV 3",false,true,null,new BigDecimal(0),"425");
		DivisionReportPOJO  dr7 = new DivisionReportPOJO("PUBLIC HEALTH DIV 2",false,true,null,new BigDecimal(0),"424");
		DivisionReportPOJO  dr8 = new DivisionReportPOJO("PUBLIC HEALTH DIV 4",false,true,null,new BigDecimal(0),"426");
		DivisionReportPOJO  dr9 = new DivisionReportPOJO("PUBLIC HEALTH DIV 1",false,true,null,new BigDecimal(0),"423");


		DivisionReportPOJO  dr10 = new DivisionReportPOJO("PARKING BRANCH",false,true,null,new BigDecimal(0),"386");
		DivisionReportPOJO  dr11 = new DivisionReportPOJO("BOOKING BRANCH",false,true,null,new BigDecimal(0),"394");
		DivisionReportPOJO  dr12 = new DivisionReportPOJO("ESTATE BRANCH",false,true,null,new BigDecimal(0),"388");
		DivisionReportPOJO  dr13 = new DivisionReportPOJO("BUILDING",false,true,null,new BigDecimal(0),"398");


		DivisionReportPOJO  dr14 = new DivisionReportPOJO("Vendor cell",true,false,a3,new BigDecimal(0),null);

		DivisionReportPOJO  dr15 = new DivisionReportPOJO("LICENSING",false,true,null,new BigDecimal(0),"396");
		DivisionReportPOJO  dr16 = new DivisionReportPOJO("ENFORCENMENT BRANCH",false,true,null,new BigDecimal(0),"392");
		DivisionReportPOJO  dr17 = new DivisionReportPOJO("FIRE AND EMERGENCY SERVICES",false,true,null,new BigDecimal(0),"406");
		DivisionReportPOJO  dr18 = new DivisionReportPOJO("Sub Office Manimajra",false,true,null,new BigDecimal(0),"466");

		DivisionReportPOJO  dr19 = new DivisionReportPOJO("MC Cess on Electricity Charges",true,false,a4,new BigDecimal(0),null);
		DivisionReportPOJO  dr20 = new DivisionReportPOJO("Covid Cess",true,false,a5,new BigDecimal(0),null);
		DivisionReportPOJO  dr21 = new DivisionReportPOJO("Cow Cess",true,false,a6,new BigDecimal(0),null);
		DivisionReportPOJO  dr22 = new DivisionReportPOJO("HORTICULTURE DIV 2",false,true,null,new BigDecimal(0),"434");
		DivisionReportPOJO  dr23 = new DivisionReportPOJO("HORTICULTURE DIV 1",false,true,null,new BigDecimal(0),"433");

		DivisionReportPOJO  dr24 = new DivisionReportPOJO("Int. On SB accounts",true,false,a7,new BigDecimal(0),null);
		DivisionReportPOJO  dr25 = new DivisionReportPOJO("Interest on FD",true,false,a8,new BigDecimal(0),null);

		DivisionReportPOJO  dr26 = new DivisionReportPOJO("Miscellaneous Receipt",true,false,a9,new BigDecimal(0),null);
		divisionReportPOJO.add(dr1);
		divisionReportPOJO.add(dr2);
		divisionReportPOJO.add(dr3);
		divisionReportPOJO.add(dr4);
		divisionReportPOJO.add(dr5);
		divisionReportPOJO.add(dr6);

		divisionReportPOJO.add(dr7);
		divisionReportPOJO.add(dr8);
		divisionReportPOJO.add(dr9);
		divisionReportPOJO.add(dr10);
		divisionReportPOJO.add(dr11);

		divisionReportPOJO.add(dr12);
		divisionReportPOJO.add(dr13);
		divisionReportPOJO.add(dr14);
		divisionReportPOJO.add(dr15);
		divisionReportPOJO.add(dr16);

		divisionReportPOJO.add(dr17);
		divisionReportPOJO.add(dr18);
		divisionReportPOJO.add(dr19);
		divisionReportPOJO.add(dr20);
		divisionReportPOJO.add(dr21);

		divisionReportPOJO.add(dr22);
		divisionReportPOJO.add(dr23);
		divisionReportPOJO.add(dr24);
		divisionReportPOJO.add(dr25);
		divisionReportPOJO.add(dr26);


		return divisionReportPOJO;
		}
	
	public Set<String> getExlcudingGlcodeForDepartments(){
		
		
		Set<String> a1 = new HashSet<String>();
		a1.add("1100101");
		a1.add("1100102");		
		a1.add("1401102");
		a1.add("1105203");
		a1.add("1404056");
		a1.add("1105202");
		a1.add("1105201");
		a1.add("1711001");
		a1.add("1701001");	
		a1.add("1808001");
	
		return a1;
	}
}
