package org.egov.egf.web.controller.supplier;

import java.util.List;

import org.egov.egf.masters.services.SupplierService;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.egov.model.masters.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coesupplier")
public class ViewSupplierController {
	public static final String SUCCESS = "Success";
	
	@Autowired
    private SupplierService supplierService;
	
	@GetMapping(value = "/_get")
	public ResponseEntity<ResponseInfoWrapper> getSEPApplication(@RequestParam(required = false) final String code) {
		Supplier supplier = new Supplier();
		if(!StringUtils.isEmpty(code)) {
			supplier.setCode(code);
		}
		final List<Supplier> searchResultList = supplierService.search(supplier);
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(searchResultList).build(), HttpStatus.OK);
	}
}
