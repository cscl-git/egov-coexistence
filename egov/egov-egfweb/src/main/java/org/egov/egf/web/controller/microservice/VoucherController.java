package org.egov.egf.web.controller.microservice;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.billsaccounting.services.VoucherConstant;

import org.egov.collection.entity.MisReceiptDetail;
import org.egov.collection.service.MisReceiptDetailService;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.EgModules;
import org.egov.egf.contract.model.AccountDetailContract;
import org.egov.egf.contract.model.MisReceiptsDetailsRequest;
import org.egov.egf.contract.model.MisReceiptsDetailsResponse;
import org.egov.egf.contract.model.MisReceiptsPOJO;
import org.egov.egf.contract.model.SubledgerDetailContract;
import org.egov.egf.contract.model.Voucher;
import org.egov.egf.contract.model.VoucherRequest;
import org.egov.egf.contract.model.VoucherResponse;
import org.egov.egf.contract.model.VoucherSearchRequest;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.services.voucher.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoucherController {
	private static final Logger LOGGER = Logger.getLogger(VoucherController.class);
	@Autowired
	private CreateVoucher createVoucher;
	@Autowired
	private VoucherService voucherService;
	@Autowired
	MisReceiptDetailService misReceiptDetailService;

	@PostMapping(value = "/rest/voucher/_search")
	@ResponseBody
	public VoucherResponse create(@RequestBody VoucherSearchRequest voucherSearchRequest) {
		try {
			VoucherResponse response = voucherService.findVouchers(voucherSearchRequest);
			response.setResponseInfo(MicroserviceUtils.getResponseInfo(voucherSearchRequest.getRequestInfo(),
					HttpStatus.SC_OK, null));
			
			return response;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ApplicationRuntimeException(e.getMessage());
		}

	}
	
	@PostMapping(value = "/rest/voucher/_ismanualreceiptdateenabled")
        @ResponseBody
        public AppConfigValues getManualReceiptDateConsiderationForVoucher() {
                try {
                    return voucherService.isManualReceiptDateEnabledForVoucher();
                } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new ApplicationRuntimeException(e.getMessage());
                }
        }
	
	@PostMapping(value = "/rest/voucher/_getmoduleidbyname")
        @ResponseBody
        public EgModules getEgModuleIdByName(@Param("moduleName") String moduleName) {
                try {
                        return voucherService.getModulesIdByName(moduleName);
                } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new ApplicationRuntimeException(e.getMessage());
                }
        }

	@PostMapping(value = "/rest/voucher/_create")
	@ResponseBody
	public VoucherResponse create(@RequestBody VoucherRequest voucherRequest) {

		VoucherResponse response = new VoucherResponse();
		final HashMap<String, Object> headerDetails = new HashMap<String, Object>();
		HashMap<String, Object> detailMap = null;
		HashMap<String, Object> subledgertDetailMap = null;
		final List<HashMap<String, Object>> accountdetails = new ArrayList<>();
		final List<HashMap<String, Object>> subledgerDetails = new ArrayList<>();

		for (Voucher voucher : voucherRequest.getVouchers()) {
			try {
				SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
				Date vDate = fm.parse(voucher.getVoucherDate());
				headerDetails.put(VoucherConstant.DEPARTMENTCODE, voucher.getDepartment());
				headerDetails.put(VoucherConstant.VOUCHERNAME, voucher.getName());
				headerDetails.put(VoucherConstant.VOUCHERTYPE, voucher.getType());
				headerDetails.put(VoucherConstant.VOUCHERNUMBER, voucher.getVoucherNumber());
				headerDetails.put(VoucherConstant.VOUCHERDATE, vDate);
				headerDetails.put(VoucherConstant.DESCRIPTION, voucher.getDescription());
				headerDetails.put(VoucherConstant.MODULEID, voucher.getModuleId());
				String source = voucher.getSource();
				headerDetails.put(VoucherConstant.SOURCEPATH, source);
				headerDetails.put(VoucherConstant.RECEIPTNUMBER, voucher.getReceiptNumber());
//				String receiptNumber = !source.isEmpty() & source != null ? source.indexOf("?selectedReceipts=") != -1 ? source.substring(source.indexOf("?selectedReceipts=")).split("=")[1]: "" : "";
				if(voucher.getReferenceDocument() != null && !voucher.getReferenceDocument().isEmpty()){
				    headerDetails.put(VoucherConstant.REFERENCEDOC, voucher.getReferenceDocument());
				}
				if(voucher.getServiceName() != null && !voucher.getServiceName().isEmpty()){
				    headerDetails.put(VoucherConstant.SERVICE_NAME, voucher.getServiceName());
				}
				// headerDetails.put(VoucherConstant.BUDGETCHECKREQ, voucher());
				if (voucher.getFund() != null)
					headerDetails.put(VoucherConstant.FUNDCODE, voucher.getFund().getCode());

				if (voucher.getFunction() != null)
					headerDetails.put(VoucherConstant.FUNCTIONCODE, voucher.getFunction().getCode());

				if (voucher.getFunctionary() != null)
					headerDetails.put(VoucherConstant.FUNCTIONARYCODE, voucher.getFunctionary().getCode());
				if (voucher.getScheme() != null)
					headerDetails.put(VoucherConstant.SCHEMECODE, voucher.getScheme().getCode());
				if (voucher.getSubScheme() != null)
					headerDetails.put(VoucherConstant.SUBSCHEMECODE, voucher.getSubScheme().getCode());

				for (AccountDetailContract ac : voucher.getLedgers()) {

					detailMap = new HashMap<>();
					detailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
					detailMap.put(VoucherConstant.DEBITAMOUNT, ac.getDebitAmount());
					detailMap.put(VoucherConstant.CREDITAMOUNT, ac.getCreditAmount());
					if (ac.getFunction() != null)
						detailMap.put(VoucherConstant.FUNCTIONCODE, ac.getFunction().getCode());

					accountdetails.add(detailMap);

					for (SubledgerDetailContract sl : ac.getSubledgerDetails()) {

						subledgertDetailMap = new HashMap<>();
						subledgertDetailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
						subledgertDetailMap.put(VoucherConstant.DETAILAMOUNT, sl.getAmount());
						subledgertDetailMap.put(VoucherConstant.DETAIL_TYPE_ID, sl.getAccountDetailType().getId());
						subledgertDetailMap.put(VoucherConstant.DETAIL_KEY_ID, sl.getAccountDetailKey().getId());
						subledgerDetails.add(subledgertDetailMap);
					}
				}
				CVoucherHeader voucherHeader = createVoucher.createVoucher(headerDetails, accountdetails,
						subledgerDetails);
				voucher.setId(voucherHeader.getId());
				voucher.setVoucherNumber(voucherHeader.getVoucherNumber());
				response.getVouchers().add(voucher);
				response.setResponseInfo(MicroserviceUtils.getResponseInfo(voucherRequest.getRequestInfo(),
						HttpStatus.SC_CREATED, null));
			} catch (ValidationException e) {
				throw e;

			} catch (ApplicationRuntimeException e) {

				throw e;
			} catch (ParseException e) {

				throw new ApplicationRuntimeException(e.getMessage());
			}

		}
		return response;
	}
	@PostMapping(value = "/rest/voucher/_cancel")
	@ResponseBody
	public VoucherResponse cancel(@RequestBody VoucherSearchRequest voucherSearchRequest) {
		try {
			VoucherResponse response = voucherService.cancel(voucherSearchRequest);
			response.setResponseInfo(MicroserviceUtils.getResponseInfo(voucherSearchRequest.getRequestInfo(),
					HttpStatus.SC_OK, null));
			
			return response;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ApplicationRuntimeException(e.getMessage());
		}

	}
	
	@PostMapping(value = "/rest/voucher/_searchbyserviceandreference",produces="application/json")
        @ResponseBody
        public VoucherResponse searchVoucherByServiceCodeAndReferenceDoc(@RequestParam(name="servicecode",required=false)  String serviceCode, @RequestParam("referencedocument")  String referenceDocument) {
                try {
                        referenceDocument = URLDecoder.decode(referenceDocument, "UTF-8");
                        List<CVoucherHeader> cVoucherHeaders = voucherService.getVoucherByServiceNameAndReferenceDocument(serviceCode, referenceDocument);
                        VoucherResponse res = new VoucherResponse();
                        if(cVoucherHeaders == null){
                            res.setResponseInfo(MicroserviceUtils.getResponseInfo(null,
                                    HttpStatus.SC_NOT_FOUND, null));
                        }else{
                            res.setVouchers(cVoucherHeaders.stream().map(cv -> new Voucher(cv)).collect(Collectors.toList()));
                        }
                        return res;
                } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new ApplicationRuntimeException(e.getMessage());
                }
        }
	
	@PostMapping(value = "/rest/voucher/_misreceipt")
	@ResponseBody
	public MisReceiptsDetailsResponse  createMiscReceiptDetails(@RequestBody MisReceiptsDetailsRequest misReceiptsDetailsRequest ){
		System.out.println("1....nEW");
		ModelMap model =null;
		MisReceiptsDetailsResponse res = new MisReceiptsDetailsResponse();
		res.setSuccess(false);
		MisReceiptDetail misReceiptDetail = null;
		MisReceiptsPOJO m = misReceiptsDetailsRequest.getMisReceiptsPOJO();
		Date date   = null;
		 try {
			 model= new ModelMap();
			date   = new Date(m.getReceipt_date());
			misReceiptDetail = new MisReceiptDetail();
         	misReceiptDetail.setBank_branch(m.getBank_branch());
         	misReceiptDetail.setBank_name(m.getBank_name());
         	misReceiptDetail.setCollectedbyname(m.getCollectedbyname());
         	misReceiptDetail.setGstno(m.getGstno());
         	misReceiptDetail.setNarration(m.getNarration());
         	misReceiptDetail.setPaid_by(m.getPaid_by());
         	misReceiptDetail.setPayer_address(m.getPayer_address());
         	misReceiptDetail.setPayment_mode(m.getPayment_mode());
         	misReceiptDetail.setPayment_status(m.getPayment_status());
         	misReceiptDetail.setPayments_id(m.getPayments_id());
         	misReceiptDetail.setReceipt_number(m.getReceipt_number());
         	misReceiptDetail.setReceipt_date(date);
         	misReceiptDetail.setServicename(m.getServicename());
         	misReceiptDetail.setSubdivison(m.getSubdivison());
         	misReceiptDetail.setTotal_amt_paid(m.getTotal_amt_paid());
         	misReceiptDetail  = misReceiptDetailService.save(misReceiptDetail);
         	res.setSuccess(true);
         	res.setMessage("Saved Successfully");
         	model.addAttribute("response", res);
        	model.addAttribute("data",misReceiptDetail);
         } catch (Exception e) {
                 LOGGER.error(e.getMessage(), e);
               //  throw new ApplicationRuntimeException(e.getMessage());
                 e.printStackTrace();
         }
			 return res;
	       
	 
	}

}
