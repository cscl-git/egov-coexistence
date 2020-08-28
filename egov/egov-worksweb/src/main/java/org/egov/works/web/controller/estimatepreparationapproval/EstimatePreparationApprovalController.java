package org.egov.works.web.controller.estimatepreparationapproval;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.QueryParam;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/estimatePreparation")
public class EstimatePreparationApprovalController {

	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		return "estimatepreparationapproval-form";
	}

	@RequestMapping(value = "/estimate", params = "estimate", method = RequestMethod.POST)
	public String saveBoQDetailsData(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file1") MultipartFile file1, final HttpServletRequest request)
			throws Exception {

		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date estimateDate = inputFormat.parse(estimatePreparationApproval.getEstimateDt());
		estimatePreparationApproval.setEstimateDate(estimateDate);

		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalService
				.saveEstimatePreparationData(request, estimatePreparationApproval);

		return "estimatepreparationapproval-form";

	}

	@RequestMapping(value = "/estimate", params = "save", method = RequestMethod.POST)
	public String saveBoqFileData(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file") MultipartFile file, final HttpServletRequest request)
			throws Exception {

		List boQDetailsList = new ArrayList();
		Long count = 0L;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		String FILE_PATH_PROPERTIES = "D:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));

		// String documentPath = "D://Upload/";

		String documentPath = FILE_PATH_PROPERTIES + FILE_PATH_SEPERATOR;

		long currentTime = new Date().getTime();
		if (file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
			fileName = file.getOriginalFilename().toString().split("\\.")[0];
			extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			fileName = fileName.replace(" ", "") + "_" + currentTime + extension;
			filePath = documentPath + fileName;
			fileToUpload = new File(filePath);
			byte[] bytes = file.getBytes();
			Path Path = null;
			Path = Paths.get(filePath);

			Path doc = Paths.get(documentPath);
			if (!Files.exists(doc)) {
				Files.createDirectories(doc);
			}

			Files.write(Path, bytes);
		}
		File xlsFile = new File(fileToUpload.toString());
		if (xlsFile.exists()) {

			FileInputStream inputStream = new FileInputStream(new File(filePath));
			Workbook workbook = getWorkbook(inputStream, filePath);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				BoQDetails aBoQDetails = new BoQDetails();

				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();

					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

						if (cell.getColumnIndex() == 0) {
							aBoQDetails.setItem_description(cell.getStringCellValue());
						} else if (cell.getColumnIndex() == 1) {
							aBoQDetails.setRef_dsr(cell.getStringCellValue());
						}

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {

						if (cell.getColumnIndex() == 2) {
							aBoQDetails.setUnit(cell.getNumericCellValue());
						} else if (cell.getColumnIndex() == 3) {
							aBoQDetails.setRate(cell.getNumericCellValue());
						} else if (cell.getColumnIndex() == 4) {
							aBoQDetails.setQuantity(cell.getNumericCellValue());
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
						}

					}

					if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
							&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
							&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
						count++;
						aBoQDetails.setSlNo(count);
						boQDetailsList.add(aBoQDetails);
						System.out.println("aBoQDetails:: " + boQDetailsList);

					}
				}
			}

			// workbook.close();
			inputStream.close();

		} else {
			// response = "Please choose a file.";
		}

		estimatePreparationApproval.setBoQDetailsList(boQDetailsList);
		model.addAttribute("estimatePreparationApproval", estimatePreparationApproval);

		return "estimatepreparationapproval-form";

	}

	public Workbook getWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;
		if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);

		} else if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}
		return workbook;
	}

}
