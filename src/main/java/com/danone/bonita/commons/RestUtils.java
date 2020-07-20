package com.danone.bonita.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitaire pour les appels aux web services
 *
 * @author glethiec
 */
public final class RestUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestUtils.class.getName());

	/**
	 * UTF_8
	 */
	private static final String UTF_8 = "UTF-8";
	private static final String SSO_HEADER = "X-Alfresco-Remote-User";
	public static final String RESPONSE_CODE = "CODE";
	public static final String RESPONSE_MESSAGE = "MESSAGE";

	/**
	 * Call Get Web Script
	 *
	 * @param url
	 *            URL of WebService
	 * @param paramHM
	 *            Map of request parameters
	 * @param retourStr
	 *            return format
	 * @param user
	 *            User logged in
	 * @param initialRequest
	 *            Requ�te HTTP initialement re�u par la tomcat
	 * @return Alfresco Response in a Map
	 */
	public static Map<String, String> getWS(final String url, final Map<String, String> paramHM, final String retourStr, final String user) {
		Map<String, String> reponse;

		final StringBuilder bld = new StringBuilder();
		boolean firstTime = true; // Permet de savoir s'il y a & dans l'url
		// pour le get

		if ((paramHM != null) && (!paramHM.isEmpty())) {
			bld.append('?');

			for (final Map.Entry<String, String> entry : paramHM.entrySet()) {
				if (!firstTime) {
					bld.append('&');
				}
				bld.append(entry.getKey());
				bld.append('=');
				bld.append(entry.getValue());
				firstTime = false;
			}
		}

		if (retourStr != null) {
			if (!firstTime) {
				bld.append('&');
			} else {
				bld.append('?');
			}
			bld.append("format=");
			bld.append(retourStr);
			firstTime = false;
		}

		final Map<String, String> requestProp = new HashMap<>();
		requestProp.put(SSO_HEADER, user);

		final String urlAndParams = url + bld.toString();
		reponse = executeWS(urlAndParams, "GET", requestProp, null);

		return reponse;
	}

	/**
	 * Call Post Web Script with a JSON content
	 *
	 * @param url
	 *            URL of WebService
	 * @param jsonContent
	 *            JSON content of the Post request
	 * @param user
	 *            User logged in
	 * @param initialRequest
	 *            Requ�te HTTP initialement re�u par la tomcat
	 * @return Alfresco Response in a Map
	 */
	public static Map<String, String> postWS(final String url, final String jsonContent, final String user) {
		Map<String, String> reponse;

		final String completeUrl = url;

		final Map<String, String> requestProp = new HashMap<>();
		requestProp.put("Content-Type", "application/json;charset=UTF-8");
		requestProp.put(SSO_HEADER, user);
		LOGGER.debug("jsonContent: "+jsonContent);
		reponse = executeWS(completeUrl, "POST", requestProp, jsonContent);
		return reponse;
	}

	/**
	 * Generic webscript call with all parameter
	 *
	 * @param urlParam
	 *            String of URL parameters
	 * @param type
	 *            type of call: "GET", "POST", "DELETE"...
	 * @param requestProp
	 *            Map of all request properties
	 * @param requestDataStr
	 *            resquest data source
	 * @param initialRequest
	 *            Requ�te HTTP initialement re�u par la tomcat
	 * @return Alfresco Response in a Map
	 */
	// Le suppress warning concerne un faux positif � "if (problem) {"
	// Le commentaire pr�c�dent est consid�r� comme du code comment�
	private static Map<String, String> executeWS(final String urlParam, final String type, final Map<String, String> requestProp,
			final String requestDataStr) {

		// On stock le code retour et le message
		Map<String, String> reponse = new HashMap<>();
		final boolean bSendData = !(requestDataStr == null || requestDataStr.equals(""));
		try {

			final HttpsURLConnection httpsConnection = openConnection(urlParam, type, requestProp);

			if (bSendData) {
				httpsConnection.setRequestProperty("Content-Length", Integer.toString(requestDataStr.length()));
				final OutputStream os = httpsConnection.getOutputStream();

				final CharsetEncoder encoder = Charset.forName(UTF_8).newEncoder();
				os.write(encoder.encode(CharBuffer.wrap(requestDataStr.toCharArray())).array());
				os.close();
			}

			reponse = readResult(httpsConnection);

		} catch (final IOException e) {
			LOGGER.warn("error while open connection", e);
		}

		return reponse;
	}

	/**
	 * Ouvre la connexion avec le serveur contenant l'url avec le type et les
	 * headers pass�s en param�tre
	 *
	 * @param urlParam
	 *            URL du service appel�
	 * @param type
	 *            type d'appel : GET, POST, DELETE
	 * @param requestProp
	 *            Header ajouter a la requ�te
	 * @return la connexion avec le serveur
	 * @throws IOException
	 *             erreur lors de l'ouverture de la connexion
	 */
	private static HttpsURLConnection openConnection(final String urlParam, final String type, final Map<String, String> requestProp)
			throws IOException {
		final URL url = new URL(urlParam);
		final HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
		httpsConnection.setConnectTimeout(30000);
		httpsConnection.setDoOutput(true);
		httpsConnection.setDoInput(true);
		httpsConnection.setUseCaches(false);
		httpsConnection.setRequestMethod(type);
		if (requestProp != null) {
			for (final Entry<String, String> value : requestProp.entrySet()) {
				httpsConnection.setRequestProperty(value.getKey(), value.getValue());
			}
		}

		return httpsConnection;
	}

	/**
	 * R�cup�re le r�sultat de la requ�te
	 *
	 * @param httpsConnection
	 *            connexion avec le service
	 * @return le r�sultat {CODE: X, MESSAGE:"..." }
	 */
	private static Map<String, String> readResult(final HttpsURLConnection httpsConnection) {
		final Map<String, String> reponse = new HashMap<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream(), UTF_8));

			httpsConnection.connect();

			final int codeRetour = httpsConnection.getResponseCode();

			reponse.put(RESPONSE_CODE, String.valueOf(codeRetour));
			final int okReturnCode = 200;
			if (codeRetour == okReturnCode) {
				final StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				reponse.put(RESPONSE_MESSAGE, stringBuilder.toString());
			} else {
				reponse.put(RESPONSE_MESSAGE, httpsConnection.getResponseMessage());
			}
		} catch (final IOException e) {
			LOGGER.warn("error while reading response", e);
			try {
				final int codeRetour = httpsConnection.getResponseCode();
				reponse.put(RESPONSE_CODE, String.valueOf(codeRetour));
				reponse.put(RESPONSE_MESSAGE, httpsConnection.getResponseMessage());
			} catch (final IOException ioe) {
				LOGGER.warn("Unable to get response", ioe);
			}
		}

		// close connection
		if (httpsConnection != null) {
			httpsConnection.disconnect();
		}

		// close reader
		if (reader != null) {
			try {
				reader.close();
			} catch (final IOException ioe) {
				LOGGER.warn("Unable to close connection reader", ioe);
			}
		}

		return reponse;
	}

	/**
	 * Private constructor
	 */
	private RestUtils() {
	}

}