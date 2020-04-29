package org.egov.lcms.web.adaptor;

import java.lang.reflect.Type;

import org.egov.lcms.transactions.entity.Library;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LibraryJsonAdaptor  implements JsonSerializer<Library>{

	@Override
	public JsonElement serialize(final Library library, final Type type, final JsonSerializationContext jsc) {
		final JsonObject jsonObject = new JsonObject();
        if (library != null) {
            if (library.getTitle() == null)
                jsonObject.addProperty("title", "");
            else
                jsonObject.addProperty("title", library.getTitle());

            if (library.getDocumentType() == null)
                jsonObject.addProperty("documentType", "");
            else
                jsonObject.addProperty("documentType", library.getDocumentType().getDocumentType());

            if (library.getActive() == null)
                jsonObject.addProperty("active", "");
            else
                jsonObject.addProperty("active", library.getActive() ? "YES" : "NO");

            jsonObject.addProperty("id", library.getId());
        }
        return jsonObject;
	}

}
