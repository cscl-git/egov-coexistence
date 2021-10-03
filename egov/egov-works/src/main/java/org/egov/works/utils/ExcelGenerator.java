package org.egov.works.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.entity.WorksAbstractliabilities;

public class ExcelGenerator {
	
	public static ByteArrayInputStream estimateResultToExcel(List<EstimatePreparationApproval> estimatelist,String[] COLUMNS)
			throws IOException {
		
		try(
				Workbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		   ){
			CreationHelper createHelper = workbook.getCreationHelper();
	 
			Sheet sheet = workbook.createSheet("EstimateResult");
	 
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
	 
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
	 
			// Row for Header
			Row headerRow = sheet.createRow(0);
	 
			// Header
			for (int col = 0; col < COLUMNS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(COLUMNS[col]);
				cell.setCellStyle(headerCellStyle);
			}
	 
			int rowIdx = 1;
			for (EstimatePreparationApproval estimate : estimatelist) {
				Row row = sheet.createRow(rowIdx++);
	 
				row.createCell(0).setCellValue(estimate.getWorkName());
				row.createCell(1).setCellValue(estimate.getEstimateNumber());
				row.createCell(2).setCellValue(estimate.getEstimateDt());
	            row.createCell(3).setCellValue(estimate.getEstimateAmount()!=null?Double.parseDouble(estimate.getEstimateAmount().toString()):0);
	            row.createCell(4).setCellValue(estimate.getStatusDescription());
	            row.createCell(5).setCellValue(estimate.getPendingWith());
			}
	 
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}
	
	public static ByteArrayInputStream estimateResultToExcel1(List<EstimatePreparationApproval> estimatelist,String[] COLUMNS)
			throws IOException {
		
		try(
				Workbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		   ){
			CreationHelper createHelper = workbook.getCreationHelper();
	 
			Sheet sheet = workbook.createSheet("EstimateResult");
	 
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
	 
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
	 
			// Row for Header
			Row headerRow = sheet.createRow(0);
	 
			// Header
			for (int col = 0; col < COLUMNS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(COLUMNS[col]);
				cell.setCellStyle(headerCellStyle);
			}
	 
			int rowIdx = 1;
			int slno=1;
			for (EstimatePreparationApproval estimate : estimatelist) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(slno);
				row.createCell(1).setCellValue(estimate.getWorkName());
				row.createCell(2).setCellValue(estimate.getExecuteDiv());
				row.createCell(3).setCellValue(estimate.getWorksWing());
	            row.createCell(4).setCellValue(estimate.getExpHead_est());
	            row.createCell(5).setCellValue(Double.parseDouble(estimate.getEstimateAmount().toString()));
	            row.createCell(6).setCellValue(estimate.getEstimateDt());
	            row.createCell(7).setCellValue(estimate.getCreatedDt());
	            slno++;
			}
	 
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}
	
	public static ByteArrayInputStream estimateworksToExcel(Map<String, WorksAbstractliabilities> worksdata,String[] COLUMNS,String heading)
			throws IOException {
		
		try(
				Workbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		   ){
			CreationHelper createHelper = workbook.getCreationHelper();
	 
			Sheet sheet = workbook.createSheet("ABSTRACT OF WORK");
	sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
	sheet.addMergedRegion(new CellRangeAddress(2, 5, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(6, 9, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(10, 13, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(14, 17, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(18, 21, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(22, 25, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(26, 29, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(30, 33, 0, 0));
	sheet.addMergedRegion(new CellRangeAddress(34, 37, 0, 0));
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
	 
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			Row titleRow = sheet.createRow(0);
			Cell titlecell = titleRow.createCell(0);
			titlecell.setCellValue(heading);
			titlecell.setCellStyle(headerCellStyle);
			
			// Row for Header
			Row headerRow = sheet.createRow(1);
	// headerRow.createCell(0).setCellValue("ABSTRACT/LIABILITIES OF WORK  (Date - 00-00-00 to 00-00-00)");
			// Header
	 //int cellno=1;
			for (int col = 0; col < COLUMNS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(COLUMNS[col]);
				cell.setCellStyle(headerCellStyle);
}
	 
			int rowIdx = 2;
			int check=1;
			String[] estimatedesc = {"Capital","Ward Development Funds", "Mayor Dev Fund", "SR.DY.DEV.FUND", "DY.MAYOR DEV Fund", "VILLAGE DEV. WORK", "Deposit Estimate works","CARPETTING WORK","Revenue"};
			String[] desc = {"Capital (Rs in lakh)","Ward Development Funds (Rs in lakh)", "Mayor Dev Fund (Rs in lakh)", "SR.DY.DEV.FUND (Rs in lakh)", "DY.MAYOR DEV Fund (Rs in lakh)", "VILLAGE DEV. WORK (Rs in lakh)", "Deposit Estimate works (Rs in lakh)","CARPETTING WORK (Rs in lakh)","Revenue (Rs in lakh)"};
			for(int j=0;j<estimatedesc.length;j++) {
			WorksAbstractliabilities test=worksdata.get(estimatedesc[j]);
				for(int i=0;i<4;i++) {
					Row row = sheet.createRow(rowIdx++);
					if(check==1) {
						
					row.createCell(0).setCellValue(desc[j]);
					row.createCell(1).setCellValue("PH");
					row.createCell(2).setCellValue(((test!=null && test.getRoughPH()!=null)?test.getRoughPH().doubleValue()/100000.00:0));
					row.createCell(3).setCellValue(((test!=null && test.getEstimatePH()!=null)?test.getEstimatePH().doubleValue()/100000.00:0));
					row.createCell(4).setCellValue(((test!=null && test.getTechnicalPH()!=null)?test.getTechnicalPH().doubleValue()/100000.00:0));
					row.createCell(5).setCellValue(((test!=null && test.getDnitPH()!=null)?test.getDnitPH().doubleValue()/100000.00:0));
					row.createCell(6).setCellValue(((test!=null && test.getWorksPH()!=null)?test.getWorksPH().doubleValue()/100000.00:0));
					row.createCell(7).setCellValue(((test!=null && test.getOngoingworksPH()!=null)?test.getOngoingworksPH().doubleValue()/100000.00:0));
					row.createCell(8).setCellValue(((test!=null && test.getGtotalPH()!=null)?test.getGtotalPH().doubleValue()/100000.00:0));
					
					}
					if(check==2) {
						row.createCell(0).setCellValue(desc[j]);
						row.createCell(1).setCellValue("BR");
						row.createCell(2).setCellValue(((test!=null && test.getRoughBR()!=null)?test.getRoughBR().doubleValue()/100000.00:0));
						row.createCell(3).setCellValue(((test!=null && test.getEstimateBR()!=null)?test.getEstimateBR().doubleValue()/100000.00:0));
						row.createCell(4).setCellValue(((test!=null && test.getTechnicalBR()!=null)?test.getTechnicalBR().doubleValue()/100000.00:0));
						row.createCell(5).setCellValue(((test!=null && test.getDnitBR()!=null)?test.getDnitBR().doubleValue()/100000.00:0));
						row.createCell(6).setCellValue(((test!=null && test.getWorksBR()!=null)?test.getWorksBR().doubleValue()/100000.00:0));
						row.createCell(7).setCellValue(((test!=null && test.getOngoingworksBR()!=null)?test.getOngoingworksBR().doubleValue()/100000.00:0));
						row.createCell(8).setCellValue(((test!=null && test.getGtotalBR()!=null)?test.getGtotalBR().doubleValue()/100000.00:0));
						
						}
					if(check==3) {
						row.createCell(0).setCellValue(desc[j]);
						row.createCell(1).setCellValue("HE");
						row.createCell(2).setCellValue(((test!=null && test.getRoughHE()!=null)?test.getRoughHE().doubleValue()/100000.00:0));
						row.createCell(3).setCellValue(((test!=null && test.getEstimateHE()!=null)?test.getEstimateHE().doubleValue()/100000.00:0));
						row.createCell(4).setCellValue(((test!=null && test.getTechnicalHE()!=null)?test.getTechnicalHE().doubleValue()/100000.00:0));
						row.createCell(5).setCellValue(((test!=null && test.getDnitHE()!=null)?test.getDnitHE().doubleValue()/100000.00:0));
						row.createCell(6).setCellValue(((test!=null && test.getWorksHE()!=null)?test.getWorksHE().doubleValue()/100000.00:0));
						row.createCell(7).setCellValue(((test!=null && test.getOngoingworksHE()!=null)?test.getOngoingworksHE().doubleValue()/100000.00:0));
						row.createCell(8).setCellValue(((test!=null && test.getGtotalHE()!=null)?test.getGtotalHE().doubleValue()/100000.00:0));
						
						}
					if(check==4) {
						row.createCell(0).setCellValue(desc[j]);
						row.createCell(1).setCellValue("TOTAL");
						row.createCell(2).setCellValue(((test!=null && test.getRoughtotal()!=null)?test.getRoughtotal().doubleValue()/100000.00:0));
						row.createCell(3).setCellValue(((test!=null && test.getEstimatehtotal()!=null)?test.getEstimatehtotal().doubleValue()/100000.00:0));
						row.createCell(4).setCellValue(((test!=null && test.getTechnicaltotal()!=null)?test.getTechnicaltotal().doubleValue()/100000.00:0));
						row.createCell(5).setCellValue(((test!=null && test.getDnittotal()!=null)?test.getDnittotal().doubleValue()/100000.00:0));
						row.createCell(6).setCellValue(((test!=null && test.getWorkstotal()!=null)?test.getWorkstotal().doubleValue()/100000.00:0));
						row.createCell(7).setCellValue(((test!=null && test.getOngoingworkstotal()!=null)?test.getOngoingworkstotal().doubleValue()/100000.00:0));
						row.createCell(8).setCellValue(((test!=null && test.getGtotaltotal()!=null)?test.getGtotaltotal().doubleValue()/100000.00:0));
						}
					check++;
					if(check==5) 
					{
						check=1;
					}
				}
			}
			
			
			WorksAbstractliabilities gt = worksdata.get("Grand Total");
			Row row = sheet.createRow(rowIdx++);
			
				
			row.createCell(0).setCellValue("Grand Total (Rs in lakh)");
			row.createCell(1).setCellValue("Total");
			row.createCell(2).setCellValue(gt.getGrandtotalrough()!=null?gt.getGrandtotalrough().doubleValue()/100000.00:0);
			row.createCell(3).setCellValue(gt.getGrandtotalestimate()!=null?gt.getGrandtotalestimate().doubleValue()/100000.00:0);
			row.createCell(4).setCellValue(gt.getGrandtotaltechnical()!=null?gt.getGrandtotaltechnical().doubleValue()/100000.00:0);
			row.createCell(5).setCellValue(gt.getGrandtotaldnit()!=null?gt.getGrandtotaldnit().doubleValue()/100000.00:0);
			row.createCell(6).setCellValue(gt.getGrandtotalworks()!=null?gt.getGrandtotalworks().doubleValue()/100000.00:0);
			row.createCell(7).setCellValue(gt.getGrandtotalongoing()!=null?gt.getGrandtotalongoing().doubleValue()/100000.00:0);
		    row.createCell(8).setCellValue(gt.getGrandtotalg()!=null?gt.getGrandtotalg().doubleValue()/100000.00:0);
			
			
			
	 
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}
	
	
}