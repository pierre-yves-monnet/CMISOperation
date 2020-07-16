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
        if (args.length < 6) {
            System.out.println("Usage : <BonitaURL> <BonitaUser> <BonitaPassword> <folderId> <procId> <picker>");
            return;
        }

        String bonitaUrl = args[0];
        String bonitaUser = args[1];
        String bonitaPassword = args[2];
        String folderId = args[3];
        String procId = args[4];
        String picker = args[5];

        final Map<String, String> map = new HashMap<String, String>();
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
            MyRestAPIContext myRestAPIContext = new MyRestAPIContext(session);

            String url = PropertiesLoader.getAlfrescoApp();
            String login = UserUtil.getUserNameFromId(myRestAPIContext);
            long timeEndProperties = System.currentTimeMillis();

            CmisUtil cmisUtil = new CmisUtil();
            cmisUtil.connectCMIS(url, login);
            long timeEndConnectCMIS = System.currentTimeMillis();

            String result = cmisUtil.listChildren(folderId, procId, picker, myRestAPIContext);
            long timeEndListChildren = System.currentTimeMillis();

            System.out.println(result);
            System.out.print("LoginBonita in "+(timeEndLogin - timeStart)
                    +" ms Getproperties in "+(timeEndProperties - timeEndLogin)
                    +" ms ConnectCMIS in "+(timeEndConnectCMIS - timeEndProperties)
                    +" ms ListChildren in "+(timeEndListChildren -  timeEndConnectCMIS)
                    +" ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
