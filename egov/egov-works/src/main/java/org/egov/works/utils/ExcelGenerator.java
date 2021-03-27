package org.egov.works.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;

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
	            row.createCell(3).setCellValue(estimate.getEstimateAmount());
	            row.createCell(4).setCellValue(estimate.getStatusDescription());
	            row.createCell(5).setCellValue(estimate.getPendingWith());
			}
	 
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}
}