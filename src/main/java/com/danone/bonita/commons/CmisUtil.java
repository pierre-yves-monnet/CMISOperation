package com.danone.bonita.commons;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.ContentStreamUtils;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.document.DocumentNotFoundException;
import org.bonitasoft.engine.bpm.document.DocumentValue;
import org.bonitasoft.engine.bpm.parameter.ParameterInstance;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.exception.NotFoundException;
import org.bonitasoft.engine.io.IOUtil;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for all CMIS action from bonita on the Alfresco
 * @author glethiec
 *
 */
public class CmisUtil {

	private static final String WORKFLOW_PROPERTIES_ID = "workflowPropertiesId";
	private static final Logger LOGGER = LoggerFactory.getLogger(CmisUtil.class);
	private static final String CMIS_URL = "api/-default-/public/cmis/versions/1.1/atom";
	private static final String KEY_MODEL = "%s.picker.%s.root.path";
	private Session cmisSession;

	/**
	 * Create a session to call alfresco
	 * @param url the alfresco atom 1.1 url
	 * @param login the login to use to call alfresco
	 */
	public void connectCMIS(String url, String login){
		LOGGER.info("CMIS - Connection to alfresco with url:"+url+" and login: "+login);
		Map<String, String> parameter = createParameter(login, url);
		// R�cup�ration du repository de connexion
		List<Repository> repositories = SessionFactoryImpl.newInstance().getRepositories(parameter);
		cmisSession = repositories.get(0).createSession();
	}

	public String documentInformation(String documentId) {
		CmisObject cmisObject = cmisSession.getObject(documentId);
		String result = null;
		if (BaseTypeId.CMIS_DOCUMENT.equals(cmisObject.getType().getBaseTypeId())){
			result = JsonUtils.cmisObject2Json(cmisObject).toString();
		} else {
			LOGGER.error("The document " + documentId + " is not a document");
		}
		return result;
	}

	/**
	 * Update a list of properties on a document in alfresco
	 * @param docId the UUID of the document to update
	 * @param properties the map of [QNAME, value] of properties
	 */
	public void updateDocumentProperties(String docId, Map<String, String> properties){
		Document docu = (Document) cmisSession.getObject(docId);
		docu.updateProperties(properties);
	}

	/**
	 * JsonArray representation of the descendant of a folder
	 * @param folderId the UUID of the folder
	 * @return the list of all children
	 */
	public String listChildren(String folderId, String procId, String pickerId, RestAPIContext context) {
		JSONArray result = new JSONArray();

		String path = getPickerRootFolderPath(procId, pickerId, context);
		
		Folder folder = null;
		
		if (folderId == null || folderId.isEmpty()) {
			folder = getRootFolderbyPath(path);
		} else {
			CmisObject folderObject = cmisSession.getObject(folderId);
			// Verify type
			if (folderObject.getType().getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
				folder = (Folder) folderObject;
				String folderPath = folder.getPath();
				if(!folderPath.contains(path)){
					LOGGER.error("The given ID references a folder not in the wanted path");
					folder = getRootFolderbyPath(path);
				}
			}else{
				result.put(JsonUtils.cmisObject2Json(folderObject));
			}
		}

		if(folder != null){
			List<CmisObject> child = getGetDescendantFolder(folder, cmisSession);
			// Get parent
			if ((folder.getPath().length() > path.length()) && folder.getParentId() != null) {
				result.put(JsonUtils.cmisObject2Json("..", folder.getType(), folder.getParentId()));
			}

			for (CmisObject item: child) {
				JSONObject tmp = JsonUtils.cmisObject2Json(item);
				result.put(tmp);
			}
		}
		return result.toString();
	}

	/**
	 * add it to be call by a connector or a direct web application
	 * @param path
	 * @return
	 */
	public String listChildren(String path) {
        JSONArray result = new JSONArray();

        Folder folder = null;
        
        folder = getRootFolderbyPath(path);
        
        if(folder != null){
            List<CmisObject> child = getGetDescendantFolder(folder, cmisSession);
            // Get parent
            if ((folder.getPath().length() > path.length()) && folder.getParentId() != null) {
                result.put(JsonUtils.cmisObject2Json("..", folder.getType(), folder.getParentId()));
            }

            for (CmisObject item: child) {
                JSONObject tmp = JsonUtils.cmisObject2Json(item);
                result.put(tmp);
            }
        }
        return result.toString();
    }
	
	
	public Folder getRootFolderbyPath(String path) {
		Folder folder = null;
		CmisObject cmisObject = cmisSession.getObjectByPath(path);
		if (cmisObject.getType().getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
			folder = (Folder) cmisObject;
		}else{
			LOGGER.error("Error as the path given doesn't reference a folder. path = "+path);
		}
		return folder;
	}

	public String getPickerRootFolderPath(String procId, String pickerId, RestAPIContext context) {
		String path = "/";
		Long longProcId = Long.parseLong(procId);
		try {
			ParameterInstance paramInstance = context.getApiClient().getProcessAPI().getParameterInstance(longProcId, WORKFLOW_PROPERTIES_ID);
			String procPropId = (String) paramInstance.getValue();
			String key = String.format(KEY_MODEL, procPropId, pickerId);
			path = PropertiesLoader.getProperty(key);
		} catch (NotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return path;
	}

	/**
	 * Get the document contenet from alfresco
	 * @param docId the UUID of the content to retrive
	 * @return the Document object to be use in bonita
	 * @throws ConnectorException
	 */
	public DocumentValue downloadDocument(String docId) throws ConnectorException{

		Document docu = (Document) cmisSession.getObject(docId);
		final ContentStream contentStream = docu.getContentStream();
		DocumentValue docValue;
		try {
			docValue = new DocumentValue(IOUtil.getAllContentFrom(contentStream.getStream()),contentStream.getMimeType(), contentStream.getFileName());
		} catch (final IOException e) {
			throw new ConnectorException(e);
		}
		return docValue;
	}

	/**
	 * push a doc content inside alfresco as an update
	 * @param doc the bonita document to push to alfresco
	 * @param docId the UUID to update
	 * @param processApi the processApi from bonita to get stream of the bonita document
	 * @throws ConnectorException
	 */
	public void uploadDocument(org.bonitasoft.engine.bpm.document.Document doc, String docId, ProcessAPI processApi) throws ConnectorException{

		Document docu = (Document) cmisSession.getObject(docId);

		ObjectId pwcId = docu.checkOut();
		Document pwc = (Document) cmisSession.getObject(pwcId);

		try (InputStream stream = getStream(doc, processApi)) {
			ContentStream cs = getContentStream(doc, stream);
			String filename = pwc.getName();
			filename = filename.substring(0, filename.lastIndexOf('.'))+".pdf";
			pwc.rename(filename, true);
			pwc.checkIn(false, null, cs, "signature added");
		} catch (IOException e) {
			LOGGER.error("Cannot read document", e);
		}

	}

	private List<CmisObject> getGetDescendantFolder(Folder folder, Session session) {

		OperationContext operationContext = session.createOperationContext();
		operationContext.setIncludeAcls(false);
		operationContext.setIncludeRelationships(IncludeRelationships.BOTH);
		operationContext.setIncludeAllowableActions(false);
		operationContext.setIncludePathSegments(false);
		operationContext.setIncludePolicies(false);
		operationContext.setLoadSecondaryTypeProperties(false);

		ItemIterable<CmisObject> children = folder.getChildren(operationContext);

		List<CmisObject> finalList = new ArrayList<>();
		for(CmisObject item: children)
		{
			Property<Boolean> prop = item.getProperty("cmis:versionSeriesCheckedOutId");

			if(prop == null || prop.getValue() == null || !prop.getValue().toString().equals(item.getId()) /*|| prop.getValue().toString().equals("false")*/){
				finalList.add(item);
			}
		}
		return finalList;
	}

	/**
	 * Create parameter for the session creation
	 * @param login User login
	 * @param password User password
	 * @return parameter for CMIS session creation
	 */
	private Map<String, String> createParameter(String login, String serverUrl) {

		Map<String, String> parameter = new HashMap<>();
		// Ajout des parametres de communication
		parameter.put(SessionParameter.ATOMPUB_URL, serverUrl + CMIS_URL);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
		parameter.put(SessionParameter.HEADER + ".0", " X-Alfresco-Remote-User: " + login);

		return parameter;
	}

	/**
	 * get the stream content of a bonita document
	 * @param doc the bonita doc
	 * @param processApi bonita processApi
	 * @return the stream value
	 * @throws ConnectorException
	 */
	private InputStream getStream(final org.bonitasoft.engine.bpm.document.Document doc, ProcessAPI processApi)
			throws ConnectorException {
		byte[] documentContent;
		try {
			documentContent = processApi.getDocumentContent(doc.getContentStorageId());
		} catch (final DocumentNotFoundException e) {
			throw new ConnectorException("Failed to retrieve document content for " + doc.getName(), e);
		}

		InputStream stream = new ByteArrayInputStream(documentContent);
		return stream;

	}

	private ContentStream getContentStream(org.bonitasoft.engine.bpm.document.Document doc, InputStream stream) {
		return ContentStreamUtils.createContentStream(doc.getContentFileName(), null, doc.getContentMimeType(), stream);
	}
}
