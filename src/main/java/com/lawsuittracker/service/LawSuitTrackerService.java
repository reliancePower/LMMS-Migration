package com.lawsuittracker.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.lawsuittracker.modal.CommonFunctions;
import com.lawsuittracker.oldmodal.LoginModal;
import com.lawsuittracker.oldmodal.LogoutModal;
import com.lawsuittracker.oldmodal.ManageAlerts;
import com.lawsuittracker.oldmodal.ManageCases;
import com.lawsuittracker.oldmodal.ManageDashboard;
import com.lawsuittracker.oldmodal.ManageDocs;
import com.lawsuittracker.oldmodal.ManageHearing;
import com.lawsuittracker.oldmodal.ManageMaster;
import com.lawsuittracker.oldmodal.ManageNotices;
import com.lawsuittracker.oldmodal.ManageUser;

@Path("/LawSuit")
public class LawSuitTrackerService {
	LoginModal lm = new LoginModal();
	ManageDashboard mDashboard = new ManageDashboard();
	LogoutModal lo = new LogoutModal();
	CommonFunctions cf = new CommonFunctions();
	ManageAlerts mAlert = new ManageAlerts();
	ManageDocs mDocs = new ManageDocs();
	ManageCases mCases = new ManageCases();
	ManageHearing mHearing = new ManageHearing();
	ManageMaster mMaster = new ManageMaster();
	ManageNotices mNotice = new ManageNotices();
	ManageUser mUser = new ManageUser();

	public static final Logger logger = Logger.getLogger(LawSuitTrackerService.class);

	@POST
	@Path("/login")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String validateUser(@Context HttpServletRequest request, String userDetails,
			@Context HttpServletResponse res) throws Exception {
		String response = "";
		// String ip = request.getLocalAddr();
		String ip = request.getRemoteAddr();
		HttpSession session = request.getSession();

		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[16];
		random.nextBytes(bytes);
		String token = new BigInteger(1, bytes).toString(16);
		session.setAttribute("csrfToken", token);
		String sessionID = (String) session.getAttribute("csrfToken");

		// if (request.getParameter("JSESSIONID") != null) {
		// Cookie userCookie = new Cookie("JSESSIONID",
		// request.getParameter("JSESSIONID"));
		// res.addCookie(userCookie);
		// } else {
		// String sessionId = session.getId();
		// Cookie userCookie = new Cookie("JSESSIONID", sessionId);
		// res.addCookie(userCookie);
		// }
		// logger.info("validateUser ==> input ==>" + userDetails);
		// commented by DD

		response = lm.validateUser(userDetails, ip, sessionID, request);
		// logger.info("validateUser ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/logout")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String logoutUser(@Context HttpServletRequest request, String input, @Context HttpServletResponse res)
			throws Exception {
		String response = "";

		request.getSession().removeAttribute("csrfToken");
		// logger.info("logoutUser ==> input ==>" + input);
		response = lo.logout(input);

		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("Cache-Control", "no-store");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
		request.getSession().invalidate();
		// request.getSession(true);
		Cookie cookie = new Cookie("JSESSIONID", null);
		cookie.setPath("/");
		cookie.setMaxAge(0);

		// logger.info("logoutUser ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchBusiVertMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchBusiVertMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("fetchBusiVertMaster ==> input ==>" + input);

		response = mMaster.fetchBusiVertMaster(input, sessionID, sessUser);
		// logger.info("fetchBusiVertMaster ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchBusiVertMasterView")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchBusiVertMasterView(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("fetchBusiVertMasterView ==> input ==>" + input);
		response = mMaster.fetchBusiVertMasterView(input, sessionID, sessUser);
		// logger.info("fetchBusiVertMasterView ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/userRegMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String FetchUserRegMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("userRegMaster ==> input ==>" + input);
		response = mUser.FetchUserRegMaster(input, sessionID, sessUser);
		// logger.info("userRegMaster ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchAllCases")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAllCases(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("fetchAllCases ==> input ==>" + input);
		response = mCases.fetchAllCases(input, sessionID, sessUser);
		// logger.info("fetchAllCases ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/getSummaryUpcoming")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getSummaryUpcoming(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("getSummaryCases ==> input ==>" + input);
		response = mDashboard.getSummaryUpcoming(input, sessionID, sessUser);
		// logger.info("getSummaryCases ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/viewCase")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchCase(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		 //logger.info("fetchCase ==> input ==>" + input);
		response = mCases.fetchCaseDetails(input, sessionID, sessUser);
		 //logger.info("fetchCase ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchCompanyMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchCompanyMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("fetchCompanyMaster ==> input ==>" + input);
		response = mMaster.fetchCompanyMaster(input, sessionID, sessUser);
		// logger.info("fetchCompanyMaster ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchBusinessMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchBusinessMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("fetchBusinessMaster ==> input ==>" + input);
		response = mMaster.fetchBusinessMaster(input, sessionID, sessUser);
		// logger.info("fetchBusinessMaster ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchVerticalMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchVerticalMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("fetchVerticalMaster ==> input ==>" + input);
		response = mMaster.fetchVerticalMaster(input, sessionID, sessUser);
		// logger.info("fetchVerticalMaster ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/homePageInfo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String homePageInfo(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		System.out.println(session.getAttribute("csrfToken"));
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("homePageInfo ==> input ==>" + sessionID);
		response = mDashboard.homePageInfo(input, sessionID, sessUser);
		// logger.info("homePageInfo ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchPushNotifications")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchPushNotifications(String input) throws Exception {
		String response = "";

		// logger.info("fetchPushNotifications ==> input ==>" + input);
		response = mDashboard.fetchPushNotifications(input);
		// logger.info("fetchPushNotifications ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/addUpdateNewCase")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String addUpdateNewCase(String input) throws Exception {
		String response = "";

		// logger.info("addUpdateNewCase ==> input ==>" + input);
		response = mCases.addUpdateNewCase(input);
		// logger.info("addUpdateNewCase ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchRefNo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchRefNo(String input) throws Exception {
		String response = "";

		// logger.info("fetchRefNo ==> input ==>" + input);
		response = mMaster.fetchRefNo(input);
		// logger.info("fetchRefNo ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/riaCaseInfo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String riaCaseInfo(@QueryParam("caseID") String caseID) throws Exception {
		String response = "";

		// logger.info("riaCaseInfo ==> input ==>" + caseID);
		response = cf.riaCaseInfo(caseID);
		// logger.info("riaCaseInfo ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/riaFetchCaseOnDates")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String riaFetchAllCases(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate)
			throws Exception {
		String response = "";

		// logger.info("riaFetchAllCases ==> input ==>" + startDate);
		response = cf.riaFetchAllCases(startDate, endDate);
		// logger.info("riaFetchAllCases ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/addToMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String addToMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		System.out.println(session.getAttribute("csrfToken"));
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("addToMaster ==> input ==>" + input);
		response = mMaster.addToMaster(input, sessionID, sessUser);
		// logger.info("addToMaster ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/AddUpdateUser")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String AddUpdateUser(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		 //logger.info("AddUpdateUser ==> input ==>" + input);
		response = mUser.AddUpdateUser(input, sessionID, sessUser);
		 //logger.info("AddUpdateUser ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/updateHearing")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String updateHearing(String input) throws Exception {
		String response = "";

		// logger.info("filterCases ==> input ==>" + input);
		response = mHearing.updateHearing(input);
		// logger.info("filterCases ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/updateDOB")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String updateDOB(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("filterCases ==> input ==>" + input);
		response = mUser.updateDOB(input, sessionID, sessUser);
		// logger.info("filterCases ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/updatePIN")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String updatePIN(String userDetails) throws Exception {
		String response = "";

		// logger.info("updatePIN ==> input ==>" + userDetails);

		response = mUser.updatePIN(userDetails);
		// logger.info("updatePIN ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/hearingUpdateSummary")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String hearingUpdateSummary(@Context HttpServletRequest request, String userDetails) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("hearingUpdateSummary ==> input ==>" + userDetails);
		response = mHearing.hearingUpdateSummary(userDetails, sessionID, sessUser);
		// logger.info("hearingUpdateSummary ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/loginMobile")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String validateUserMob(@Context HttpServletRequest request, String userDetails) throws Exception {
		String response = "";
		// String ip = request.getLocalAddr();
		String ip = request.getRemoteAddr();
		HttpSession session = request.getSession();
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[16];
		random.nextBytes(bytes);
		String token = new BigInteger(1, bytes).toString(16);
		session.setAttribute("csrfToken", token);
		String sessionID = (String) session.getAttribute("csrfToken");

		// logger.info("validateUser ==> input ==>" + userDetails);
		response = lm.validateUserMob(userDetails, ip, sessionID, request);
		// logger.info("validateUser ==> Output ==>" + response);
		return response;

	}

	@GET
	@Path("/getAppVersion")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getAppVersion() throws Exception {
		String response = "";
		// logger.info("logoutUser ==> input ==>" + input);
		response = mUser.getAppVersion();
		// logger.info("logoutUser ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/deviceRequest")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getDeviceRequest(String input) throws Exception {
		String response = "";

		// logger.info("logoutUser ==> input ==>" + input);
		response = mUser.getDeviceRequest(input);
		// logger.info("logoutUser ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/notice")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String manageNotices(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		logger.info("res"+input);
		response = mNotice.manageNotices(input);

	 logger.info("res"+response);
		return response;

	}

	@POST
	@Path("/searchNotices")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String searchNotices(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		logger.info("res"+input);
		response = mNotice.searchNotices(input);

	 logger.info("res"+response);
		return response;

	}

	
	
	
	@POST
	@Path("/fetchDocuments")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchDocuments(String input) throws Exception {
		String response = "";
		//logger.info("manageDocuments ==> input ==>" + input);
		response = mDocs.fetchDocuments(input);
		//logger.info("manageDocuments ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchDocType")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchDocType(String input) throws Exception {
		String response = "";
		// logger.info("fetchDocType ==> input ==>" + input);
		response = mDocs.fetchDocType(input);
		// logger.info("fetchDocType ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/manageDocs")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String manageDocuments(String input) throws Exception {
		String response = "";
		//logger.info("manageDocuments ==> input ==>" + input);
		response = mDocs.manageDocuments(input);
		//logger.info("manageDocuments ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/searchAlert")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String searchAlert(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("searchAlert ==> input ==>" + input);
		response = mAlert.searchAlert(input, sessionID, sessUser);
		// logger.info("searchAlert ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/manageAlert")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchCompanyAlert(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		// logger.info("manageAlert ==> input ==>" + input);
		response = mAlert.manageAlert(input, sessionID, sessUser);
		// logger.info("manageAlert ==> Output ==>" + response);
		return response;

	}
	
	@POST
	@Path("/manageForum")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String manageForum(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		//logger.info("manageForum ==> input ==>" + input);
		response = mMaster.manageForum(input, sessionID, sessUser);
		//logger.info("manageForum ==> Output ==>" + response);
		return response;

	}
	
	
	
	@POST
	@Path("/pvForumUpdateLog")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String pvForumUpdateLog(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();		
		//logger.info("pvForumUpdateLog ==> input ==>" + input);
		response = mMaster.pvForumUpdateLog(input);
		//logger.info("pvForumUpdateLog ==> Output ==>" + response);
		return response;

	}
	

	@POST
	@Path("/fetchAllMasters")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAllMasters(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		//logger.info("fetchAllMasters ==> input ==>" + input);
		response = mMaster.fetchAllMasters(input, sessionID, sessUser);
		//logger.info("fetchAllMasters ==> Output ==>" + response);
		return response;

	}
	
	@POST
	@Path("/fetchMastersForAsset")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchMastersForAsset(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession();
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		//logger.info("fetchAllMasters ==> input ==>" + input);
		response = mMaster.fetchMastersForAsset(input, sessionID, sessUser);
		//logger.info("fetchAllMasters ==> Output ==>" + response);
		return response;

	}
	
	@POST
	@Path("/fetchNoticeDocs")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchNoticeDocs(String input) throws Exception {
		String response = "";
		logger.info("manageDocuments ==> input ==>" + input);
		response = mDocs.fetchNoticeDocs(input);
		logger.info("manageDocuments ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/fetchNoticeDocType")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchNoticeDocType(String input) throws Exception {
		String response = "";
		 logger.info("fetchDocType ==> input ==>" + input);
		response = mDocs.fetchNoticeDocType(input);
		logger.info("fetchDocType ==> Output ==>" + response);
		return response;

	}

	@POST
	@Path("/manageNoticeDocs")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String manageNoticeDocs(String input) throws Exception {
		String response = "";
		logger.info("manageDocuments ==> input ==>" + input);
		response = mDocs.manageNoticeDocuments(input);
		logger.info("manageDocuments ==> Output ==>" + response);
		return response;

	}
}