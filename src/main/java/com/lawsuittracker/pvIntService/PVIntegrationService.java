package com.lawsuittracker.pvIntService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.lawsuittracker.modal.CommonFunctions;
import com.lawsuittracker.pvIntegration.PVManageAlerts;
import com.lawsuittracker.pvIntegration.PVManageCases;
import com.lawsuittracker.pvIntegration.PVManageMaster;

@Path("/Law")
public class PVIntegrationService {

	CommonFunctions cf = new CommonFunctions();
	PVManageAlerts pvi = new PVManageAlerts();
	PVManageMaster pvmm = new PVManageMaster();
	PVManageCases pvcase = new PVManageCases();
	public static final Logger logger = Logger.getLogger(PVIntegrationService.class);

	@POST
	@Path("/pvOperation")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String doPVOperations(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		logger.info("doPVOperations ==> input ==>" + input);
		response = pvi.doPVOperations(input, sessionID, sessUser);
		logger.info("doPVOperations ==> Output ==>" + response);
		return response;

	}

	@GET
	@Path("/fetchAllForums")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAllForum(@Context HttpServletRequest request) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = pvmm.fetchAllForums(sessionID, sessUser);
		//logger.info("fetchForum ==> Output ==>" + response);
		return response;

	}

	@GET
	@Path("/fetchAllCaseTypes")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAllCaseTypes(@Context HttpServletRequest request) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = pvmm.fetchAllCaseTypes(sessionID, sessUser);
		//logger.info("fetchForum ==> Output ==>" + response);
		return response;

	}

	@GET
	@Path("/fetchNewForums")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchNewForums(@Context HttpServletRequest request) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = pvmm.fetchNewForums(sessionID, sessUser);
		//logger.info("fetchForum ==> Output ==>" + response);
		return response;

	}
	
	@POST
	@Path("/fetchNewCaseTypes")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchNewCaseTypes(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		JSONObject obj = new JSONObject(input);		
		int forumID = obj.has("forumID") ? obj.getInt("forumID") : 0;
		 pvmm.fetchNewCaseTypes(forumID);		
		//logger.info("fetchForum ==> Output ==>" + response);
		return response;

	}
	@GET
	@Path("/fetchAllCasesFromPV")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAllCasesFromPV(@Context HttpServletRequest request) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = pvcase.fetchAllCasesFromPV(sessionID, sessUser);
		//logger.info("fetchForum ==> Output ==>" + response);
		return response;

	}
	
	

}