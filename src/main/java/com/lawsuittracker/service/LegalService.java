package com.lawsuittracker.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.lawsuittracker.modal.CommonFunctions;
import com.lawsuittracker.modal.LoginModal;
import com.lawsuittracker.modal.LogoutModal;
import com.lawsuittracker.modal.ManageAlerts;
import com.lawsuittracker.modal.ManageCases;
import com.lawsuittracker.modal.ManageDashboard;
import com.lawsuittracker.modal.ManageDocs;
import com.lawsuittracker.modal.ManageHearing;
import com.lawsuittracker.modal.ManageMaster;
import com.lawsuittracker.modal.ManageNotes;
import com.lawsuittracker.modal.ManageNotices;
import com.lawsuittracker.modal.ManageUser;

import java.io.File;
import jakarta.ws.rs.core.Response;


@Path("/V2")
public class LegalService {
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
	ManageNotes mNotes = new ManageNotes();

	public static final Logger logger = Logger.getLogger(LegalService.class);

	@POST
	@Path("/login")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String validateUser(@Context HttpServletRequest request, String userDetails,
			@Context HttpServletResponse res) throws Exception {
		String response = "";

		String ip = request.getRemoteAddr();
		HttpSession session = request.getSession(false);

		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[16];
		random.nextBytes(bytes);
		String token = new BigInteger(1, bytes).toString(16);
		session.setAttribute("csrfToken", token);
		String sessionID = (String) session.getAttribute("csrfToken");

		response = lm.validateUser(userDetails, ip, sessionID, request);

		return response;

	}

	@POST
	@Path("/logout")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String logoutUser(@Context HttpServletRequest request, String input, @Context HttpServletResponse res)
			throws Exception {
		String response = "";

		request.getSession(false).removeAttribute("csrfToken");
		request.getSession(false).removeAttribute("userID");
		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("Cache-Control", "no-store");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
		request.getSession(false).invalidate();

		Cookie cookie = new Cookie("JSESSIONID", null);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response = lo.logout(input);

		return response;

	}

	@POST
	@Path("/fetchBusiVertMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchBusiVertMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mMaster.fetchBusiVertMaster(input, sessionID, sessUser);

		return response;

	}

	@POST
	@Path("/fetchBusiVertMasterView")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchBusiVertMasterView(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mMaster.fetchBusiVertMasterView(input, sessionID, sessUser);

		return response;

	}

	@POST
	@Path("/userRegMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String FetchUserRegMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mUser.FetchUserRegMaster(input, sessionID, sessUser);

		return response;

	}

	@POST
	@Path("/fetchAllCases")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAllCases(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mCases.fetchAllCases(input, sessionID, sessUser);

		return response;

	}

	@POST
	@Path("/getSummaryUpcoming")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getSummaryUpcoming(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mDashboard.getSummaryUpcoming(input, sessionID, sessUser);

		return response;

	}

	@POST
	@Path("/viewCase")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchCase(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mCases.fetchCaseDetails(input, sessionID, sessUser);

		return response;

	}
	
	@POST
	@Path("/viewAiSuggestion")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAiDetails(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mCases.fetchAiDetails(input, sessionID, sessUser);

		return response;

	}
	
	@POST
	@Path("/saveFeedBack")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String saveFeedBack(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mCases.saveAiFeedback(input, sessionID, sessUser);

		return response;

	}
	
	@POST
	@Path("/viewAddCase")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String ViewAddCase(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");

		response = mCases.fetchAddCaseDetails(input, sessionID, sessUser);

		return response;

	}

	@POST
	@Path("/fetchCompanyMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchCompanyMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.fetchCompanyMaster(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchBusinessMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchBusinessMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.fetchBusinessMaster(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchVerticalMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchVerticalMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.fetchVerticalMaster(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/homePageInfo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String homePageInfo(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		//System.out.println(session.getAttribute("csrfToken"));
		String sessUser = (String) session.getAttribute("userID");
		//System.out.println(session.getAttribute("userID"));
		response = mDashboard.homePageInfo(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchPushNotifications")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchPushNotifications(String input) throws Exception {
		String response = "";
		response = mDashboard.fetchPushNotifications(input);
		return response;

	}

	@POST
	@Path("/addUpdateNewCase")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String addUpdateNewCase(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		System.out.println(session.getAttribute("csrfToken"));
		String sessUser = (String) session.getAttribute("userID");
		response = mCases.addUpdateNewCase(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchRefNo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchRefNo(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		System.out.println(session.getAttribute("csrfToken"));
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.fetchRefNo(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/riaCaseInfo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String riaCaseInfo(@QueryParam("caseID") String caseID) throws Exception {
		String response = "";
		response = cf.riaCaseInfo(caseID);
		return response;

	}

	@POST
	@Path("/riaFetchCaseOnDates")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String riaFetchAllCases(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate)
			throws Exception {
		String response = "";
		response = cf.riaFetchAllCases(startDate, endDate);
		return response;

	}

	@POST
	@Path("/addToMaster")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String addToMaster(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		System.out.println(session.getAttribute("csrfToken"));
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.addToMaster(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/AddUpdateUser")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String AddUpdateUser(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mUser.AddUpdateUser(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/updateHearing")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String updateHearing(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mHearing.updateHearing(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/updateDOB")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String updateDOB(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mUser.updateDOB(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/updatePIN")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String updatePIN(@Context HttpServletRequest request, String userDetails) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mUser.updatePIN(userDetails, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/hearingUpdateSummary")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String hearingUpdateSummary(@Context HttpServletRequest request, String userDetails) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mHearing.hearingUpdateSummary(userDetails, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/loginMobile")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String validateUserMob(@Context HttpServletRequest request, String userDetails) throws Exception {
		String response = "";
		String ip = request.getRemoteAddr();
		HttpSession session = request.getSession(false);

		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[16];
		random.nextBytes(bytes);
		String token = new BigInteger(1, bytes).toString(16);
		session.setAttribute("csrfToken", token);
		String sessionID = (String) session.getAttribute("csrfToken");
		response = lm.validateUserMob(userDetails, ip, sessionID, request, session.getId());
		return response;

	}

	@GET
	@Path("/getAppVersion")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getAppVersion() throws Exception {
		String response = "";
		response = mUser.getAppVersion();
		return response;

	}

	@POST
	@Path("/deviceRequest")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String getDeviceRequest(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mUser.getDeviceRequest(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/notice")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String manageNotices(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mNotice.manageNotices(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/searchNotices")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String searchNotices(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";

		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mNotice.searchNotices(input, sessionID, sessUser);
		return response;

	}
	
	@POST
	@Path("/testApi")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String testapi(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";

		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mNotice.testapi(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchDocuments")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchDocuments(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";

		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mDocs.fetchDocuments(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchDocType")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchDocType(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mDocs.fetchDocType(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/manageDocs")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String manageDocuments(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mDocs.manageDocuments(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/searchAlert")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String searchAlert(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mAlert.searchAlert(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/manageAlert")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchCompanyAlert(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mAlert.manageAlert(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/manageForum")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String manageForum(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.manageForum(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/pvForumUpdateLog")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String pvForumUpdateLog(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.pvForumUpdateLog(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchAllMasters")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchAllMasters(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.fetchAllMasters(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchMastersForAsset")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String fetchMastersForAsset(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mMaster.fetchMastersForAsset(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchNoticeDocs")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchNoticeDocs(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mDocs.fetchNoticeDocs(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/fetchNoticeDocType")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String fetchNoticeDocType(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mDocs.fetchNoticeDocType(input, sessionID, sessUser);
		return response;

	}

	@POST
	@Path("/manageNoticeDocs")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String manageNoticeDocs(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mDocs.manageNoticeDocuments(input, sessionID, sessUser);
		return response;

	}
	
	@POST
	@Path("/manageNotes")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String sendEmail(@Context HttpServletRequest request, String input) throws Exception {
		String response = "";
		HttpSession session = request.getSession(false);
		String sessionID = (String) session.getAttribute("csrfToken");
		String sessUser = (String) session.getAttribute("userID");
		response = mNotes.managenotes(input, sessionID, sessUser);
		return response;

	}
	
	@GET
	@Path("/downloadPdf")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadPdf(@QueryParam("docId") String docId) throws Exception {
	    File file = new File("D:/GSTPDF/" + docId + ".pdf");

	    if (!file.exists()) {
	        return Response.status(Response.Status.NOT_FOUND)
	                       .entity("File not found").build();
	    }

	    return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
	                   .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
	                   .build();
	}

}