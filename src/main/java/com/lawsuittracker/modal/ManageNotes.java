package com.lawsuittracker.modal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.lawsuittracker.util.SendMail;

public class ManageNotes {
	SendMail sm = new SendMail();
	public static final Logger logg = Logger.getLogger(ManageNotes.class);

	public String managenotes(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;		
		ResultSet rs1 = null;		
		JSONObject obj = new JSONObject(input);
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONArray emailArray = new JSONArray();

		String type = (obj.has("type")) ? obj.getString("type") : "";
		int caseID = (obj.has("caseID")) ? obj.getInt("caseID") : 0;
		int companyID = (obj.has("companyID")) ? obj.getInt("companyID") : 0;
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		String toMail = (obj.has("toMail")) ? obj.getString("toMail") : "";
		String ccMail = (obj.has("ccMail")) ? obj.getString("ccMail") : "";
		String template = (obj.has("template")) ? obj.getString("template") : "";
		String finImpOneTime = (obj.has("finImpOneTime")) ? obj.getString("finImpOneTime") : "";
		String finImpRecurring = (obj.has("finImpRecurring")) ? obj.getString("finImpRecurring") : "";
		String finImaRecurringDuration = (obj.has("finImaRecurringDuration")) ? obj.getString("finImaRecurringDuration")
				: "";
		String subjMatter = (obj.has("subjMatter")) ? obj.getString("subjMatter") : "";
		String briefFacts = (obj.has("briefFacts")) ? obj.getString("briefFacts") : "";
		String additionalComments = (obj.has("additionalComments")) ? obj.getString("additionalComments") : "";
		String mailSub = (obj.has("mailSub")) ? obj.getString("mailSub") : "";
		
		String hodEmail = (obj.has("hodEmail")) ? obj.getString("hodEmail") : "";
		String fromDefaultEmail = LawSuitTrackerConstants.fromMail ;
		String fromEmail = (obj.has("fromEmail")) ? obj.getString("fromEmail") : "";
		String fromEmailPersonName = fromEmail.replaceAll("((@.*)|[^a-zA-Z])+", " ").trim();
		System.out.print(fromEmailPersonName);
		String draftByEmail = (obj.has("draftByEmail")) ? obj.getString("draftByEmail") : "";
		String draftByCCMail = (obj.has("draftByCCMail")) ? obj.getString("draftByCCMail") : "";
		
		boolean isHOD = (obj.has("isHOD")) ? obj.getBoolean("isHOD") : false;

		
		
		int id = (obj.has("id")) ? obj.getInt("id") : 0;

		template = template.replaceAll("(?i)<(?!(/?(ol|li|p|br|b|table|tr|td|span)))[^>]*>", "");

		if (sessionID == null) {
			outJson.put(LawSuitTrackerConstants.status, "S");
			outJson.put("msg", "Your session has expired. Please logout and login again...");
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "S");
			outJson.put("msg", "Your session is invalid. Please try again later!");
		} else if (!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "S");
			outJson.put("msg", "Your are not authorized to perform this action.");
		} else {
			try {
				con = SQLDBConnection.getDBConnection();

				cstmt = con.prepareCall(
						"{call " + LawSuitTrackerConstants.managenotesProc + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, type);
				cstmt.setString(2, userID);
				cstmt.setInt(3, caseID);
				cstmt.setInt(4, companyID);
				cstmt.setString(5, toMail);
				cstmt.setString(6, ccMail);
				cstmt.setString(7, template);
				cstmt.setInt(8, id);
				cstmt.setString(9, mailSub);
				cstmt.setString(10, finImpOneTime);
				cstmt.setString(11, finImpRecurring);
				cstmt.setString(12, finImaRecurringDuration);
				cstmt.setString(13, subjMatter);
				cstmt.setString(14, briefFacts);
				cstmt.setString(15, additionalComments);
				cstmt.setString(16, draftByCCMail);

				cstmt.execute();
				rs = cstmt.getResultSet();
				if ("fetchMaster".equalsIgnoreCase(type)) {
					while (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs.getInt(1));
						jobj.put("name", rs.getString(2));

						emailArray.put(jobj);
					}
					outJson.put("emailArray", emailArray);
				} else if ("fetchTemplate".equalsIgnoreCase(type)) {
					while (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs.getInt(1));
						jobj.put("template", rs.getString(2));
						jobj.put("toMail", rs.getString(3));
						jobj.put("ccMail", rs.getString(4));						
						jobj.put("HODPayrollNo", rs.getString(5));
						jobj.put("HODEmailIDS", rs.getString(6));
						jobj.put("HODsName", rs.getString(7));

						resultArray.put(jobj);
					}

					outJson.put("resultArray", resultArray);

				} else if ("fetchNotes".equalsIgnoreCase(type)) {
					while (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs.getInt(1));
						jobj.put("caseID", rs.getInt(2));
						jobj.put("templateID", rs.getInt(3));
						jobj.put("templateName", rs.getString(4));
						jobj.put("toMail", rs.getString(5));
						jobj.put("ccMail", rs.getString(6));						
						jobj.put("hods", rs.getString(7));
						jobj.put("subject", rs.getString(8));
						jobj.put("templateStatus", rs.getString(9));
						jobj.put("mailTemplate", rs.getString(10));
						jobj.put("draftBy", rs.getString(11));
						jobj.put("draftTimestamp", rs.getString(12));
						jobj.put("sentBy", rs.getString(13));
						jobj.put("sentTimestamp", rs.getString(14));
						jobj.put("HODEmailIDS", rs.getString(15));
						jobj.put("draftByName", rs.getString(16));
						jobj.put("sentByName", rs.getString(17));						
						jobj.put("hodPayrollNo", rs.getString(18));
						jobj.put("crNo", rs.getString(19));
						jobj.put("draftByEmail", rs.getString(20));
						jobj.put("draftByCCMail", rs.getString(21));
						
						resultArray.put(jobj);
					}

					outJson.put("notesArray", resultArray);
				} else if ("saveDraft".equalsIgnoreCase(type)) {
					if (rs.next()) {
						if (rs.getInt(1) > 0) {
							template = template.replaceAll("background-color: #eee;", "");
							
							template = template.replaceAll("background-color:#eee;", "");
							String templ = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><style>table {border-collapse: collapse;}</style></head><body style=\"font-family:Arial;font-size:15px;\"> <p>Dear Sir/Madam, </p> <p> Please review the below given draft email note on Case ID - <b>"
									+ caseID
									+ "</b> and send to the people specified in this draft, by opening this case in R-LMMS Mobile App or Web App (<a target = \"_blank\" href= \"https://mportal.reliancepower.co.in/LMMS/\">https://mportal.reliancepower.co.in/LMMS/</a>).</p><p style = \"color:red\"><u>Draft Email Note</u></p><p><b>To : </b> "+toMail+" </p>"
									+ "<p><b>CC : </b> "+ccMail+" </p><p><b> Subject : </b> "+mailSub+" </p><div style = \"background-color: rgb(255, 255, 255); overflow: hidden;padding: 8px !important;border-radius: 2px !important;box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 3px !important;\">" + template + "</div><p><b>Case ID: "+caseID + " / Communication Reference No : " +rs.getString(3)+"</b></p> </body></html>";
							if(!isHOD)
								sm.sendAMailToMgmt(fromDefaultEmail, hodEmail, mailSub + " - Draft Note - Sent by "+ fromEmailPersonName, templ, fromEmail +","+draftByCCMail, LawSuitTrackerConstants.devMail);
							//System.out.println(templ);
							outJson.put("count", rs.getInt(1));
							outJson.put("crNo", rs.getString(3));
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put("count", 0);
						}
					} else
						outJson.put("count", 0);

				} else if ("editDraft".equalsIgnoreCase(type)) {
					if (rs.next()) {
						if (rs.getInt(1) > 0) { 
							template = template.replaceAll("background-color: #eee;", "");
							
							template = template.replaceAll("background-color:#eee;", "");
							String templ = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><style>table {border-collapse: collapse;}</style></head> <body style=\"font-family:Arial;font-size:15px;\"> <p>Dear Sir/Madam, </p> <p> Please review the below given updated draft email note on Case ID <b>" +caseID+ "</b> with Communication Reference No - <b>" + rs.getString(3) + "</b> and send to the people specified in this draft, by opening this case in R-LMMS Mobile App or Web App"
									
									+ "(<a target = \"_blank\" href= \"https://mportal.reliancepower.co.in/LMMS/\">https://mportal.reliancepower.co.in/LMMS/</a>).</p><p style = \"color:red\"><u>Draft Email Note</u></p><p><b>To : </b> "+toMail+" </p>" + 
									" <p><b>CC : </b> "+ccMail+" </p><p><b> Subject : </b> "+mailSub+" </p><div style = \"background-color: rgb(255, 255, 255); overflow: hidden;padding: 8px !important;border-radius: 2px !important;box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 3px !important;\">"+ template + "</div><p><b>Case ID : "+caseID + " / Communication Reference No : " + rs.getString(3) + "</b></p></p></body></html>";
							
							sm.sendAMailToMgmt(fromDefaultEmail, hodEmail, mailSub + " - Draft Note - Sent by "+fromEmailPersonName, templ, fromEmail +","+draftByCCMail, LawSuitTrackerConstants.devMail);
							outJson.put("count", rs.getInt(1));
							outJson.put("crNo", rs.getString(3));
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put("count", 0);
						}
					} else
						outJson.put("count", 0);

				} else if ("deleteNote".equalsIgnoreCase(type)) {
					if (rs.next()) {
						if (rs.getInt(1) > 0) {
							outJson.put("count", rs.getInt(1));
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put("count", 0);
						}
					} else
						outJson.put("count", 0);

				} else if ("submitNotes".equalsIgnoreCase(type)) {
					if (rs.next()) {
						if (rs.getInt(1) > 0) {
							template = template.replaceAll("background-color: #eee;", "");
							
							template = template.replaceAll("background-color:#eee;", "");
							String templ = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><style>table {border-collapse: collapse;}</style></head> <body style=\"font-family:Arial;font-size:15px;\"><p style = \"font-weight:bold;text-decoration:underline\">"+mailSub+"</p><div style = \"background-color: rgb(255, 255, 255); overflow: hidden;padding: 8px !important;border-radius: 2px !important;box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 3px !important;\">" + 
									"" + template
									+ "</div><p><b>Case ID : "+caseID + " / Communication Reference No : " +rs.getString(3)+"</b></p></p></body></html>";
							//System.out.println(templ);
							sm.sendAMailToMgmt(fromDefaultEmail, toMail, mailSub+" - Sent by "+fromEmailPersonName, templ, fromEmail+","+ccMail, fromEmail);
							
							
							outJson.put("count", rs.getInt(1));
							outJson.put("crNo", rs.getString(3));
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put("count", 0);
						}
					} else
						outJson.put("count", 0);

				} else if ("approveNotes".equalsIgnoreCase(type)) {
					if (rs.next()) {
						if (rs.getInt(1) > 0) {
							template = template.replaceAll("background-color: #eee;", "");
							
							template = template.replaceAll("background-color:#eee;", "");
							String templ = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><style>table {border-collapse: collapse;}</style></head> <body style=\"font-family:Arial;font-size:15px;\"><p style = \"font-weight:bold;text-decoration:underline\">"+mailSub+"</p><div style = \"background-color: rgb(255, 255, 255); overflow: hidden;padding: 8px !important;border-radius: 2px !important;box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 3px !important;\">" + template
									+ "</div><p><b>Case ID : "+caseID + " / Communication Reference No : " + rs.getString(3)+"</b></p></p></body></html>";
							
							sm.sendAMailToMgmt(fromDefaultEmail, toMail, mailSub, templ, ccMail, fromEmail + "," +draftByEmail);
							outJson.put("count", rs.getInt(1));
							outJson.put("crNo", rs.getString(3));
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put("count", 0);
						}
					} else
						outJson.put("count", 0);

				}
			} catch (SQLException e) {
				logg.error("Login Error in SQL Exception ==>" + e);
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "There seems some error at server side. Please try again later!!!");

			} finally {
				if (rs != null)
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (rs1 != null) {
					try {
						rs1.close();
						rs1 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				}
				if (cstmt != null)
					try {
						cstmt.close();
						cstmt = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (con != null) {
					try {
						con.close();
						con = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - con ==>" + e.getMessage());
					}
				}
			}

		}

		response = outJson.toString(4);
		return response;
	}

}
