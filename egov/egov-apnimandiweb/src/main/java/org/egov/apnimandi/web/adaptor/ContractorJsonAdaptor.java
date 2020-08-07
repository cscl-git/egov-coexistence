package org.egov.apnimandi.web.adaptor;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import org.egov.apnimandi.reports.entity.ApnimandiContractorSearchResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ContractorJsonAdaptor implements JsonSerializer<ApnimandiContractorSearchResult>{
	@Override
	public JsonElement serialize(final ApnimandiContractorSearchResult contractor, final Type type, final JsonSerializationContext jsc) {
		final JsonObject jsonObject = new JsonObject();
		final SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (contractor != null) {
        	if (contractor.getApnimandiContractor() != null) {
	            if (contractor.getApnimandiContractor().getName() == null)
	                jsonObject.addProperty("name", "");
	            else
	                jsonObject.addProperty("name", contractor.getApnimandiContractor().getSalutation() + " " + contractor.getApnimandiContractor().getName());
	
	            if (contractor.getApnimandiContractor().getZone() == null)
	                jsonObject.addProperty("zone", "");
	            else
	                jsonObject.addProperty("zone", contractor.getZoneName());
	            
	            if (contractor.getApnimandiContractor().getStatus() == null) {
	            	jsonObject.addProperty("statusCode", "");
	                jsonObject.addProperty("status", "");
	            }
	            else {
	            	jsonObject.addProperty("statusCode", contractor.getApnimandiContractor().getStatus().getCode());
	                jsonObject.addProperty("status", contractor.getStatusName());
	            }
	
	            if (contractor.getApnimandiContractor().getActive() == null)
	                jsonObject.addProperty("active", "");
	            else
	                jsonObject.addProperty("active", contractor.getApnimandiContractor().getActive() ? "YES" : "NO");
	            
	            if(contractor.getApnimandiContractor().getValidFromDate()==null)
	            	jsonObject.addProperty("validFromDate", "");
	            else 
	            	jsonObject.addProperty("validFromDate", dtFormat.format(contractor.getApnimandiContractor().getValidFromDate()));
	            
	            if(contractor.getApnimandiContractor().getValidToDate()==null)
	            	jsonObject.addProperty("validToDate", "");
	            else 
	            	jsonObject.addProperty("validToDate", dtFormat.format(contractor.getApnimandiContractor().getValidToDate()));
	            jsonObject.addProperty("id", contractor.getApnimandiContractor().getId());
        	}
        }
        return jsonObject;
	}
}
