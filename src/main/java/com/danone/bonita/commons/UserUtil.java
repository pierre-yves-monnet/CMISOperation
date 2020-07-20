package com.danone.bonita.commons;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.identity.UserWithContactData;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for the manipulation of users informations
 * @author glethiec
 *
 */
public final class UserUtil {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserUtil.class);

	
	/*
	 * Static class so no public constructor
	 */
	private UserUtil(){}
	
	
	/**
	 * return the UserName from the User Id
	 * @param identityAPI bonita identity api
	 * @param userId long id of the user
	 * @return the userName.
	 */
	public static String getUserNameFromId(IdentityAPI identityAPI, Long userId){
		String username = null;
		User user;
		try {
			user = identityAPI.getUser(userId);
			username = user.getUserName();
		} catch (UserNotFoundException e) {
			LOGGER.error("Error whil trying to get the userName", e);
		}
		
		return username;
	}
	
	/**
	 * return the UserName from the User Id
	 * @param apiAccessor bonita accessor of all api
	 * @param userId long id of the user
	 * @return the userName.
	 */
	public static String getUserNameFromId(APIAccessor apiAccessor, Long userId){
		return getUserNameFromId(apiAccessor.getIdentityAPI(), userId);
	}
	
	/**
	 * return the UserName from the User Id
	 * @param context bonita context of the current restcall
	 * @param userId long id of the user
	 * @return the userName.
	 */
	public static String getUserNameFromId(RestAPIContext context, Long userId){	
		return getUserNameFromId(context.getApiClient().getIdentityAPI(), userId);
	}
	
	/**
	 * return the UserName from the User Id
	 * @param context bonita context of the current restcall
	 * @return the userName.
	 */
	public static String getUserNameFromId(RestAPIContext context){	
		return getUserNameFromId(context.getApiClient().getIdentityAPI(), context.getApiSession().getUserId());
	}
	
	/**
	 * return the email from the User name
	 * @param apiAccessor bonita accessor of all api
	 * @param userName login of the user to be used
	 * @return the email.
	 */
	public static String getEmailFromUserName(APIAccessor apiAccessor, String userName){
		return getEmailFromUserName(apiAccessor.getIdentityAPI(), userName);
	}
	
	/**
	 * return the email from the User Name
	 * @param identityAPI bonita identity api
	 * @param userName login of the user to be used
	 * @return the email.
	 */
	public static String getEmailFromUserName(IdentityAPI identityAPI, String userName){
		String email = null;
		UserWithContactData user;
		try {
			user = identityAPI.getUserWithProfessionalDetails(identityAPI.getUserByUserName(userName).getId());
			email = user.getContactData().getEmail();
		} catch (UserNotFoundException e) {
			LOGGER.error("Error whil trying to get the email", e);
		}
		
		return email;
	}
	
	/**
	 * return the display name from the User name
	 * @param apiAccessor bonita accessor of all api
	 * @param userName login of the user to be used
	 * @return the display name.
	 */
	public static String getUserDisplayNameFromUserName(APIAccessor apiAccessor, String userName){
		return getUserDisplayNameFromUserName(apiAccessor.getIdentityAPI(), userName);
	}
	
	/**
	 * return the display name from the User Name
	 * @param identityAPI bonita identity api
	 * @param userName login of the user to be used
	 * @return the display name.
	 */
	public static String getUserDisplayNameFromUserName(IdentityAPI identityAPI, String userName){
		String displayName = null;
		User user;
		try {
			user = identityAPI.getUserByUserName(userName);
			displayName = user.getFirstName()+" "+user.getLastName();
		} catch (UserNotFoundException e) {
			LOGGER.error("Error whil trying to get the displayName", e);
		}
		
		return displayName;
	}
}
