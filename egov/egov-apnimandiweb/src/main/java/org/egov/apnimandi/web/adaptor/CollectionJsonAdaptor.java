package org.egov.apnimandi.web.adaptor;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.egov.apnimandi.reports.entity.ApnimandiCollectionSearchResult;
import org.egov.apnimandi.utils.ApnimandiUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CollectionJsonAdaptor implements JsonSerializer<ApnimandiCollectionSearchResult>{

	@Override
	public JsonElement serialize(final ApnimandiCollectionSearchResult collection, final Type type, final JsonSerializationContext jsc) {
		final JsonObject jsonObject = new JsonObject();
        if (collection != null) {
        	if (collection.getApnimandiCollections() != null) {
	            if (collection.getApnimandiCollections().getCollectiontype() == null)
	                jsonObject.addProperty("type", "");
	            else
	                jsonObject.addProperty("type", collection.getApnimandiCollections().getCollectiontype().getName());
	
	            if (collection.getApnimandiCollections().getZone() == null)
	                jsonObject.addProperty("zone", "");
	            else
	                jsonObject.addProperty("zone", collection.getZoneName());
	            
	            if (collection.getApnimandiCollections().getSite() == null)
	                jsonObject.addProperty("site", "");
	            else
	                jsonObject.addProperty("site", collection.getSiteName());
	            
	            if (collection.getApnimandiCollections().getStatus() == null) {
	                jsonObject.addProperty("status", "");
	                jsonObject.addProperty("statusCode", "");
	            } else {
	                jsonObject.addProperty("status", collection.getStatusName());
	                jsonObject.addProperty("statusCode", collection.getApnimandiCollections().getStatus().getCode());
	            }
	
	            if (collection.getApnimandiCollections().getActive() == null)
	                jsonObject.addProperty("active", "");
	            else
	                jsonObject.addProperty("active", collection.getApnimandiCollections().getActive() ? "YES" : "NO");
	            
	            if(collection.getApnimandiCollections().getCollectionForMonth()==null)
	            	jsonObject.addProperty("collectionMonth", "");
	            else 
	            	jsonObject.addProperty("collectionMonth", ApnimandiUtil.getMonthFullName(collection.getApnimandiCollections().getCollectionForMonth()));
	            
	            if(collection.getApnimandiCollections().getCollectionForYear()==null)
	            	jsonObject.addProperty("collectionYear", "");
	            else 
	            	jsonObject.addProperty("collectionYear", collection.getApnimandiCollections().getCollectionForYear());
	            
	            if (StringUtils.isNotBlank(collection.getApnimandiCollections().getReceiptNo()))
	                jsonObject.addProperty("receiptNo", collection.getApnimandiCollections().getReceiptNo());
	            else
	                jsonObject.addProperty("receiptNo", "NA");
	            
	            if (StringUtils.isNotBlank(collection.getApnimandiCollections().getPaymentId()))
	                jsonObject.addProperty("paymentId", collection.getApnimandiCollections().getPaymentId());
	            else
	                jsonObject.addProperty("paymentId", "");
	            
	            jsonObject.addProperty("id", collection.getApnimandiCollections().getId());
        	}
        }
        return jsonObject;
	}
}
