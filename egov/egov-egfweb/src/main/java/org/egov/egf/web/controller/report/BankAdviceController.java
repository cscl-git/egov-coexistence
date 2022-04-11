package org.egov.egf.web.controller.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.egov.commons.Accountdetailkey;
import org.egov.commons.Bankaccount;
import org.egov.egf.contract.model.AccountDetailContract;
import org.egov.egf.contract.model.Bank;
import org.egov.egf.contract.model.BankAccount;
import org.egov.egf.contract.model.BankBranch;
import org.egov.infra.microservice.models.Instrument;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.services.instrument.InstrumentHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/bankAdvice")
public class BankAdviceController {

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@Autowired
	InstrumentHeaderService instrumentHeaderService;

	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss", LOCALE);
//	public static final SimpleDateFormat DDMMYYYYFORMAT2 = new SimpleDateFormat("dd hh:mm:ss", LOCALE);

	@RequestMapping(value = "/bankAdviceReport")
	public String search(@ModelAttribute("bankAdvice") final BankAdvice bankAdvice, final Model model,
			HttpServletRequest request) {
		List<Bank> bankNames = persistenceService.findAllBy(
				"select distinct b from Bank b , Bankbranch bb , Bankaccount ba WHERE bb.bank=b and ba.bankbranch=bb and ba.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and b.isactive=true order by b.name");

		model.addAttribute("bankNames", bankNames);
//		model.addAttribute("bankNames", bankNames);

		return "newBankAdviceReport";
	}

	@RequestMapping(value = "/bankAdviceReports")
	public @ResponseBody List<BankBranch> searchBankBranch(@RequestParam("bankValue") String bankValue,
			final Model model, HttpServletRequest request) {

		List<BankBranch> bankBranchID = persistenceService.findAllBy(
				"select distinct bb from Bankbranch bb,Bankaccount ba where bb.bank.id=? and ba.bankbranch=bb and ba.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and bb.isactive=true",
				Integer.parseInt(bankValue));

		return bankBranchID;
	}

	@RequestMapping("/bankAdviceReportss")
	public @ResponseBody List<Bankaccount> searchBranchAccounts(@RequestParam("branchname") String branchname,
			final Model model, HttpServletRequest request) {

		List<Bankaccount> accountNumbers = persistenceService
				.findAllBy("from Bankaccount where bankbranch.id=? and isactive=true", Integer.parseInt(branchname));

		return accountNumbers;
	}

	@RequestMapping("/bankAdviceReportPexx")
	public @ResponseBody List<InstrumentHeader> searchBranchAccountsPEX(
			@RequestParam("accountnumber") String accountnumber, @RequestParam("fromdate") String fromdate,
			@RequestParam("todate") String todate, final Model model, HttpServletRequest request)
			throws ParseException {

//		fromdate = DDMMYYYYFORMAT1.format(fromdate);
//		todate = DDMMYYYYFORMAT1.format(todate);
//		Date fromdaate = new Date(fromdate);
//		Date todaate = new Date(todate);

		InstrumentHeader instrumentHeader = new InstrumentHeader();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fromdatee = dateFormat.parse(fromdate);
		Date todatee = dateFormat.parse(todate);
		instrumentHeader.setFromDate(fromdatee);
		instrumentHeader.setToDate(todatee);
		instrumentHeader.setAccountNumber(accountnumber);
		List<InstrumentHeader> pexNumbers = persistenceService.findAllBy(
				"from InstrumentHeader where bankaccountid=?" + " and transactiondate BETWEEN ? AND ?",
				Long.parseLong(instrumentHeader.getAccountNumber()), instrumentHeader.getFromDate(),
				instrumentHeader.getToDate());

		List<InstrumentHeader> pex = new ArrayList<InstrumentHeader>();
		for (InstrumentHeader li : pexNumbers) {
			if (li.getRealizationDate() == null) {

				InstrumentHeader h = new InstrumentHeader();
				h.setTransactionNumber(li.getTransactionNumber());
				h.setRealizationDate(li.getRealizationDate());
				pex.add(h);
			}
		}

		return pex;
	}

	@RequestMapping("/bankAdviceReportPex")
	public String searchBranchAccountsPEX(@ModelAttribute("bankAdvice") InstrumentHeader instrumentHeader,
			final Model model, HttpServletRequest request) throws ParseException {

		List<InstrumentHeader> pexNumbers = persistenceService.findAllBy(
				"from InstrumentHeader where bankaccountid=?" + " and transactiondate BETWEEN ? AND ?",
				Long.parseLong(instrumentHeader.getAccountNumber()), instrumentHeader.getFromDate(),
				instrumentHeader.getToDate());
		try {
			for (int i = 0; i < pexNumbers.size(); i++) {
				String pex = request.getParameter("transactionNumber[" + i + "].pex");
				String realizationDate = request.getParameter("realDate[" + i + "].pex");
				if (realizationDate != null) {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date date = dateFormat.parse(realizationDate);
					pexNumbers.get(i).setRealizationDate(date);
					instrumentHeaderService.reconciled(date, pexNumbers.get(i).getId());
				}
			}
		} catch (Exception e) {
			model.addAttribute("message", "Failure: Realization Date is Not Saved with Corresponding PEX Numbers.");
			return "success_saved";
		}

		model.addAttribute("message", "Success: Realization Date is Saved with Corresponding PEX Numbers.");
		return "success_saved";
	}

	@RequestMapping(value = "/bankAdviceReportPexEXCEL")
	public @ResponseBody ResponseEntity<InputStreamResource> searchBranchAccountsEXCEL(
			@RequestParam("accountnumber") String accountnumber, @RequestParam("fromdate") String fromdate,
			@RequestParam("todate") String todate, final Model model, HttpServletRequest request)
			throws ParseException, IOException {

		InstrumentHeader instrumentHeader = new InstrumentHeader();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fromdatee = dateFormat.parse(fromdate);
		Date todatee = dateFormat.parse(todate);
		instrumentHeader.setFromDate(fromdatee);
		instrumentHeader.setToDate(todatee);
		instrumentHeader.setAccountNumber(accountnumber);
		List<InstrumentHeader> pexNumbers = persistenceService.findAllBy(
				"from InstrumentHeader where bankaccountid=?" + " and transactiondate BETWEEN ? AND ?",
				Long.parseLong(instrumentHeader.getAccountNumber()), instrumentHeader.getFromDate(),
				instrumentHeader.getToDate());

		String[] COLUMNS = { "SlNo", "Pex Number", "Realization Date" };

		ByteArrayInputStream in = resultToExcel(pexNumbers, COLUMNS);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=PEXReport.xls");
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	}

	public static ByteArrayInputStream resultToExcel(List<InstrumentHeader> pexNumbers, String[] COLUMNS)
			throws IOException {

		HSSFWorkbook workbook = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		CreationHelper createHelper = workbook.getCreationHelper();

		Sheet sheet = workbook.createSheet("PEX REPORT");

		// Row for Header
		Row headerRow = sheet.createRow(0);
		int sl = 1;
		// Header
		for (int col = 0; col < COLUMNS.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(COLUMNS[col]);
		}

		int rowIdx = 1;
		for (InstrumentHeader detail : pexNumbers) {
			Row row = sheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(String.valueOf(sl++));
			row.createCell(1).setCellValue(detail.getTransactionNumber());
			if (detail.getRealizationDate() != null) {
				row.createCell(2).setCellValue(detail.getRealizationDate().toString());
			}

		}

		workbook.write(out);
		return new ByteArrayInputStream(out.toByteArray());

	}

}
