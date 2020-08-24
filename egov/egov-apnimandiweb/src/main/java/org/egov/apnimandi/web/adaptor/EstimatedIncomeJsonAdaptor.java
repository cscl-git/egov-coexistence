package org.egov.apnimandi.web.adaptor;

import java.lang.reflect.Type;

import org.egov.apnimandi.reports.entity.EstimatedIncomeReport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EstimatedIncomeJsonAdaptor implements JsonSerializer<EstimatedIncomeReport>{

	@Override
	public JsonElement serialize(final EstimatedIncomeReport collection, final Type type, final JsonSerializationContext jsc) {
		final JsonObject jsonObject = new JsonObject();
        if (collection != null) {        	
            if (collection.getZoneName() == null)
                jsonObject.addProperty("zone", "");
            else
                jsonObject.addProperty("zone", collection.getZoneName());
            
            if (collection.getSiteName() == null)
                jsonObject.addProperty("site", "");
            else
                jsonObject.addProperty("site", collection.getSiteName());
            jsonObject.addProperty("totalAmount", collection.getTotalAmount());
        }
        return jsonObject;
	}
}
