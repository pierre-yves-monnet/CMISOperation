package com.bonitasoft.cmis;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

import com.danone.bonita.commons.CmisUtil;
import com.danone.bonita.commons.PropertiesLoader;
import com.danone.bonita.commons.UserUtil;

public class CMISOperation {

    public static void main(final String args[]) {
        System.out.println("CMIS Operation 1.2.1");
        if (args.length < 7) {
            System.out.println("Usage : <BonitaURL> <BonitaUser> <BonitaPassword> <folderId> <procId> <picker> <PropertiesFile> [<temporisationInSeconds>]");
            return;
        }

        
        String bonitaUrl = args[0];
        String bonitaUser = args[1];
        String bonitaPassword = args[2];
        String folderId = args[3];
        String procId = args[4];
        String picker = args[5];
        PropertiesLoader.propertiesPathParam = args[6];
        String temporisation = args.length>7? args[7] : "0";
        try {
            Long temporisationLong = Long.valueOf(temporisation);
            if (temporisationLong>0) {
                System.out.println("Wait "+temporisationLong+" s");
                Thread.sleep(temporisationLong * 1000);
            }
        } catch( Exception e) {
            System.out.print("Temporisation error ["+args[7]+"] is not a number");
        }
            
        String propertiesPath = System.getProperty("properties.path");
        System.out.println("Properties.path= ["+propertiesPath+"]");

        System.out.println("BonitaUrl      : ["+bonitaUrl+"]");
        System.out.println("bonitauser     : ["+bonitaUser+"]");
        System.out.println("BonitaPassword : ["+ bonitaPassword +"]");;
        System.out.println("FolderId       : ["+folderId+"]");   
        System.out.println("procId         : ["+ procId+"]");
        System.out.println("picker         : ["+picker+"]");
        System.out.println("Properties     : ["+PropertiesLoader.propertiesPathParam+"]"); 
        

        final Map<String, String> map = new HashMap<>();
        map.put("server.url", bonitaUrl);
        map.put("application.name", "bonita");
        APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, map);

        // Set the username and password
        final String username = bonitaUser;
        final String password = bonitaPassword;
        // get the LoginAPI using the TenantAPIAccessor
        try {
            long timeStart = System.currentTimeMillis();
            final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
            // log in to the tenant to create a session
            final APISession session = loginAPI.login(username, password);
            
            long timeEndLogin = System.currentTimeMillis();
            System.out.println("Connection to Bonita correct in "+(timeEndLogin - timeStart )+" ms");
            
            MyRestAPIContext myRestAPIContext = new MyRestAPIContext(session);
            String url = PropertiesLoader.getAlfrescoApp();
            String login = UserUtil.getUserNameFromId(myRestAPIContext);
            long timeEndProperties = System.currentTimeMillis();
            System.out.println("Get URL["+url+"] and login["+login+"] correct in "+(timeEndProperties - timeEndLogin )+" ms");

            CmisUtil cmisUtil = new CmisUtil();
            cmisUtil.connectCMIS(url, login);
            long timeEndConnectCMIS = System.currentTimeMillis();
            System.out.println("Connection to CMIS correct in "+(timeEndConnectCMIS-timeEndProperties)+" ms");
            
            String path = cmisUtil.getPickerRootFolderPath(procId, picker, myRestAPIContext);
            System.out.println("Get CMIS Path["+path+"]");
            
            String result = cmisUtil.listChildren(folderId, procId, picker, myRestAPIContext);
            long timeEndListChildren = System.currentTimeMillis();

            System.out.println(result);
            System.out.println("LoginBonita in "+(timeEndLogin - timeStart)
                    +" ms Getproperties in "+(timeEndProperties - timeEndLogin)
                    +" ms ConnectCMIS in "+(timeEndConnectCMIS - timeEndProperties)
                    +" ms ListChildren in "+(timeEndListChildren -  timeEndConnectCMIS)
                    +" ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
