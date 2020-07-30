package com.bonitasoft.cmis;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.danone.bonita.commons.CmisUtil;

public class CMISServlet extends HttpServlet {

    private static final long serialVersionUID = 8798739864323956415L;

    private static final Logger logger = Logger.getLogger(CMISServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CMISServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    /**
     * Handle data received from the form defined in index.jsp file.
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = "";
        StringBuilder result = new StringBuilder();
        try {
            action = request.getParameter("action");
            String url = request.getParameter("url");
            if (url == null)
                url = "http://10.254.46.61:8080/alfresco/";
            String login = request.getParameter("login");
            if (login == null)
                login = "meirami";
            String pathCmis = request.getParameter("path");
            if (pathCmis == null)
                pathCmis = "/Sites/sitebpmdms/documentLibrary/Revision/Draft";
            result.append("CMIS SERVLET 1.2.0 url[" + url + "] login=[" + login + "] pathCMIS[" + pathCmis + "];");

            CmisUtil cmisUtil = new CmisUtil();
            long timeBeginConnectCMIS = System.currentTimeMillis();
            cmisUtil.connectCMIS(url, login);
            long timeEndConnectCMIS = System.currentTimeMillis();
            result.append("Connection to CMIS correct in " + (timeEndConnectCMIS - timeBeginConnectCMIS) + " ms");

            String resultCmis = cmisUtil.listChildren(pathCmis);
            long timeEndListChildren = System.currentTimeMillis();
            result.append("GetChilden in " + (timeEndListChildren - timeEndConnectCMIS) + " ms; resultCMIS=" + resultCmis);
            logger.info("Call action=[" + action + "] result=" + result.toString());

        } catch (Exception e) {

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionDetails = sw.toString();
            result.append("Exception " + e.getMessage() + " at " + exceptionDetails);
            logger.severe("Exception [" + e.getMessage() + "] at " + exceptionDetails + " Result=" + result.toString());
        }
        PrintWriter out;
        out = response.getWriter();
        out.println("<div id=\"content\">" + result.toString() + "</div>");
        out.flush();

    }
}
