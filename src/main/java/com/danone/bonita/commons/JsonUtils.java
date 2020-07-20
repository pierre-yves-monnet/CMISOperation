package com.danone.bonita.commons;

import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.json.JSONObject;


/**
 * Class used to convert CMIS object to json
 * @author glethiec
 *
 */
public final class JsonUtils {

	/*
	 * Static class so no public constructor
	 */
	private JsonUtils(){}
	
	/**
	 * Convert the cmis object to a json representation
	 * @param name name of the object
	 * @param type true if is a folder
	 * @param nodeRef id of the object
	 * @return
	 */
	public static JSONObject cmisObject2Json(String name, ObjectType type, String nodeRef) {
		// remove prefixe
		String[] splitedId = nodeRef.split("workspace://SpacesStore/");
		String nodeRefId = splitedId[splitedId.length-1];
		
		// remove version
		splitedId = nodeRefId.split(";");
		nodeRefId = splitedId[0];
		
		boolean isFolder = type.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER);
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("isFolder", isFolder);
		json.put("id", nodeRefId);
		
		return json;
	}
	
	/**
	 * Convert the cmis object to a json representation
	 * @param item CMIS object
	 * @return
	 */
	public static JSONObject cmisObject2Json(CmisObject item) {
		
		// remove prefixe
		String nodeRef = item.getId();
		String[] splitedId = nodeRef.split("workspace://SpacesStore/");
		String nodeRefId = splitedId[splitedId.length-1];
		
		// remove version
		splitedId = nodeRefId.split(";");
		nodeRefId = splitedId[0];

		ObjectType type = item.getType();
		boolean isFolder = type.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER);
		
		JSONObject json = new JSONObject();
		json.put("name", item.getName());
		json.put("isFolder", isFolder);
		json.put("id", nodeRefId);
		
		JSONObject prop = new JSONObject();
		for (Property<?> p: item.getProperties()) {
			
			Object value = p.getValue();
			if (p.getType() ==PropertyType.DATETIME && value != null){
				value = ((GregorianCalendar)value).getTime();
			}
			prop.put(p.getId(), value);
		}
		json.put("properties", prop);
		
		return json;
	}
}
