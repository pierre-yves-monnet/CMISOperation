package com.bonitasoft.cmis;

import java.util.Locale;

import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.web.extension.ResourceProvider;
import org.bonitasoft.web.extension.rest.RestAPIContext;

public class MyRestAPIContext implements RestAPIContext {

    APISession apiSession;

    public class MyAPIClient extends APIClient {

        APISession apiSession;

        public MyAPIClient(APISession apiSession) {
            this.apiSession = apiSession;
        }

        public ProcessAPI getProcessAPI() {
            try {
                return TenantAPIAccessor.getProcessAPI(apiSession);
            } catch (Exception e) {
                System.out.println("Can't create getProcessAPI " + e.getMessage());
                return null;
            }
        }

        public IdentityAPI getIdentityAPI() {
            try {
                return TenantAPIAccessor.getIdentityAPI(apiSession);
            } catch (Exception e) {
                System.out.println("Can't create getIdentityAPI " + e.getMessage());
                return null;
            }

        }
    }

    public MyRestAPIContext(APISession apiSession) {
        this.apiSession = apiSession;
    };

    public APIClient getApiClient() {
        return new MyAPIClient(apiSession);
    }

    public APISession getApiSession() {
        return apiSession;
    }

    public Locale getLocale() {
        return null;
    }

    public ResourceProvider getResourceProvider() {
        return null;
    }

}
