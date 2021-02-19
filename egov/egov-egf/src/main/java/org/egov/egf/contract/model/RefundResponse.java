package org.egov.egf.contract.model;

import org.egov.infra.microservice.models.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundResponse {

	 	@JsonProperty("Status")
	    private String responseStatus;
	 
	    @JsonProperty("ResponseInfo")
	    private ResponseInfo responseInfo;

		public String getResponseStatus() {
			return responseStatus;
		}

		public void setResponseStatus(String responseStatus) {
			this.responseStatus = responseStatus;
		}

		public ResponseInfo getResponseInfo() {
			return responseInfo;
		}

		public void setResponseInfo(ResponseInfo responseInfo) {
			this.responseInfo = responseInfo;
		}
}
