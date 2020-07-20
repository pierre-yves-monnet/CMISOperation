package com.danone.bonita.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
//import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for the manipulation of the properties file
 * @author glethiec
 *
 */
public final class PropertiesLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);
	private static Properties properties = null;

	/*
	 * Static class so no public constructor
	 */
	private PropertiesLoader(){}

	public static String propertiesPathParam;
	//Initial load of properties
	/**
	 * Load the properties from the classpath
	 * @throws IOException
	 */
	private static Properties init() throws IOException{
		Properties prop;
		String propertiesPath = System.getProperty("properties.path");
		if (propertiesPath==null || propertiesPathParam.trim().length()==0)
		    propertiesPath=propertiesPathParam;
		prop = new Properties();
		File propertiesFile = new File(propertiesPath);
		FileInputStream stream = new FileInputStream(propertiesFile);
		BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		prop.load(in);
		LOGGER.debug("#### properties  : "+prop);
		return prop;
	}
	
//	private static Properties init() throws IOException{
//		Properties prop;
//		String propertiesPath = System.getProperty("properties.path");
//
//		prop = new Properties();
//		File propertiesFile = new File(propertiesPath);
//		try (InputStream stream = new FileInputStream(propertiesFile)){
//			prop.load(stream);
//		}
//		String filesToLoad = (String) prop.get("properties.extension");
//		String[] arrayFileToLoad = filesToLoad.split(";");
//		
//		for(String fileToLoad : arrayFileToLoad){
//			if (StringUtils.isNotBlank(fileToLoad)){
//				File propFile = new File(fileToLoad);
//				try (InputStream streamFile = new FileInputStream(propFile)){
//					prop.load(streamFile);
//				}
//			}
//		}
//		
//		LOGGER.error("#### properties  : "+prop);
//		return prop;
//	}
	
	//generic methods to retrieve properties
	/**
	 * Return a map of the properties needed
	 * @param listproperties String of property keys separated by ';'
	 * @return Map of the propertyKey - value
	 * @throws IOException
	 */
	public static Map<String, Object> getProperties(String listproperties) throws IOException{
		Map<String, Object> propertiesMap = new HashMap<>();
		String[] propertiesArray = listproperties.split(";");
		Properties prop = getProperties();
		for (String propertyName : propertiesArray){
			propertiesMap.put(propertyName, prop.getProperty(propertyName));
		}
		return propertiesMap;
	}

	/**
	 * Return a json of the properties needed
	 * @param listproperties String of property keys separated by ';'
	 * @return json of the propertyKey - value
	 * @throws IOException
	 */
	public static String getPropertiesAsString(String listproperties) throws IOException{
		return JSONObject.toJSONString(getProperties(listproperties));
	}
	/**
	 * getter of the properties with inital load.
	 * @return
	 */
	public static synchronized Properties getProperties(){
		if(properties == null){
			try {
				properties = init();
			} catch (IOException e) {
				LOGGER.error("Impossible to load properties from server", e);
			}
		}
		return properties;
	}

	/**
	 * getter of a needed property
	 * @param key wanted property key
	 * @return property value as a string
	 */
	public static String getProperty(String key){
		LOGGER.debug("#### key  :"+key + "    value : "+getProperties().get(key));
		return (String) getProperties().get(key);
	}

	//Specific methods to retrieve properties

	//Alfresco properties
	/**
	 * get the alfresco app url
	 * @return alfresco server + alfresco app name as a full url
	 */
	public static String getAlfrescoApp(){
		return (String) getProperties().get("alfrescourl")+getProperties().get("alfrescoapp");
	}

	//Docusign properties
	/**
	 * get user name for docusign account
	 * @return authusername
	 */
	public static String getAuthUserName(){
		return (String) getProperties().get("authUserName");
	}

	/**
	 * get user password for docusign account
	 * @return password
	 */
	public static String getAuthUserPass(){
		return (String) getProperties().get("authUserPass");
	}

	/**
	 * get the accound id for docusign
	 * @return accountId
	 */
	public static String getDestAccountId(){
		return (String) getProperties().get("destAccountId");
	}

	/**
	 * get the authentification key of the integrator for docusign
	 * @return integrator key
	 */
	public static String getAuthIntegratorKey(){
		return (String) getProperties().get("authIntegratorKey");
	}

	/**
	 * return the full url to be call for docusign
	 * @return docusign url
	 */
	public static String getUrldocusign(){
		return (String) getProperties().get("urldocusign");
	}

	//mail properties

	/**
	 * get the mail server
	 * @return mail server Url
	 */
	public static String getMailServer(){
		return (String) getProperties().get("mailserver");
	}

	/**
	 * get the mail server port
	 * @return port number
	 */
	public static Integer getMailPort(){
		return Integer.parseInt((String)getProperties().get("mailport"));
	}

	/**
	 * return the sender to be use in the from field
	 * @return sender's email
	 */
	public static String getMailSender(){
		return (String) getProperties().get("mailsender");
	}

	/**
	 * account id to use to login on the mail server
	 * @return login
	 */
	public static String getMailServerUsername(){
		return (String) getProperties().get("mailserverusername");
	}

	/**
	 * account password to use to login on the mail server
	 * @return password
	 */
	public static String getMailServerPassword(){
		return (String) getProperties().get("mailserverpassword");
	}

	/**
	 * account password to use to login on the mail server
	 * @return password
	 */
	public static Boolean getMailServerSsl(){
		return Boolean.valueOf((String)getProperties().get("mailserverssl"));
	}

}


