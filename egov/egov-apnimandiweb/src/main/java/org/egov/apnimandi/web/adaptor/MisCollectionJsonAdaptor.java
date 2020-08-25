package org.egov.apnimandi.web.adaptor;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import org.egov.apnimandi.reports.entity.ApnimandiCollectionMISReport;
import org.egov.apnimandi.utils.ApnimandiUtil;
import org.egov.apnimandi.utils.constants.ApnimandiConstants;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MisCollectionJsonAdaptor implements JsonSerializer<ApnimandiCollectionMISReport>{
	@Override
	public JsonElement serialize(final ApnimandiCollectionMISReport collection, final Type type, final JsonSerializationContext jsc) {
		final JsonObject jsonObject = new JsonObject();
		final SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (collection != null) {
        	jsonObject.addProperty("id", collection.getId());
        	if (collection.getCollectionType() == null)
                jsonObject.addProperty("type", "");
            else
                jsonObject.addProperty("type", collection.getCollectionType());

            if (collection.getZoneName() == null)
                jsonObject.addProperty("zone", "");
            else
                jsonObject.addProperty("zone", collection.getZoneName());
            
            if (collection.getSiteName() == null)
                jsonObject.addProperty("site", "");
            else
                jsonObject.addProperty("site", collection.getSiteName());
            
            if (collection.getStatus() == null) {
                jsonObject.addProperty("status", "");
            } else {
                jsonObject.addProperty("status", collection.getStatus());
            }
            
            if (collection.getContractorName() == null) {
                jsonObject.addProperty("contractor", "");
            } else {
                jsonObject.addProperty("contractor", collection.getContractorName());
            }

            if(collection.getMonth()<=0)
            	jsonObject.addProperty("collectionMonth", "");
            else 
            	jsonObject.addProperty("collectionMonth", ApnimandiUtil.getMonthFullName(collection.getMonth()));
            
            if (collection.getAmountType() == null) {
                jsonObject.addProperty("amountType", "");
            } else {
                jsonObject.addProperty("amountType", collection.getAmountType().equalsIgnoreCase(ApnimandiConstants.SERVICE_TYPE_CONTRACTOR_SECURITY_FEE)?"Registration Fee":"Rent Amount");
            }
            jsonObject.addProperty("collectionDate", dtFormat.format(collection.getCollectionDate()));
            jsonObject.addProperty("collectionYear", collection.getYear());
            jsonObject.addProperty("totalAmount", collection.getTotalAmount());
        }
        return jsonObject;
	}
}
