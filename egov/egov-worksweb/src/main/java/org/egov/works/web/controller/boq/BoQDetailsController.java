package org.egov.works.web.controller.boq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.service.BoQDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/boq")
public class BoQDetailsController {

	@Autowired
	BoQDetailsService boQDetailsService;

	@Autowired
	public MicroserviceUtils microserviceUtils;

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		return "boqDetails";
	}

	@RequestMapping(value = "/work", params = "work", method = RequestMethod.POST)
	public String saveBoQDetailsData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {
		
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}

		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement);

		return "boqDetails";

	}

	@RequestMapping(value = "/work", params = "save", method = RequestMethod.POST)
	public String saveBoqFileData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
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

		workOrderAgreement.setBoQDetailsList(boQDetailsList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "boqDetails";

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

	public List<Department> getDepartmentsFromMs() {
		List<Department> departments = microserviceUtils.getDepartments();
		return departments;
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.POST)
	public String view(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);

		for (int j = 0; j < workOrderAgreement.getNewBoQDetailsList().size(); j++) {
			responseList = workOrderAgreement.getNewBoQDetailsList();
		}
		workOrderAgreement.setBoQDetailsList(responseList);
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "view-work-agreement";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String showEstimateNewFormGet(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {

		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-work-agreement-form";
	}

	@RequestMapping(value = "/workOrderAgreementSearch1", method = RequestMethod.POST)
	public String searchWorkOrderAgreement(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}

		List<WorkOrderAgreement> workDetails = boQDetailsService.searchWorkOrderAgreement(request, workOrderAgreement);
		workList.addAll(workDetails);
		workOrderAgreement.setWorkOrderList(workList);

		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-work-agreement-form";

	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);

		for (int j = 0; j < workOrderAgreement.getNewBoQDetailsList().size(); j++) {
			responseList = workOrderAgreement.getNewBoQDetailsList();
		}
		workOrderAgreement.setBoQDetailsList(responseList);
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "edit-work-agreement";
	}
	
	@RequestMapping(value = "/edit/work1", method = RequestMethod.POST)
	public String saveEditData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {

		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}

		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement);

		return "boqDetails";

	}

}
