package com.lawsuittracker.modal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.dao.SecondDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;

public class ManageNotices {
	public static final Logger logger = Logger.getLogger(ManageNotices.class);

	public String manageNotices(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;
		ResultSet rs8 = null;
		ResultSet rs9 = null;
		JSONArray noticeArr = new JSONArray();
		JSONArray noticesArr = new JSONArray();
		JSONArray companyArr = new JSONArray();
		JSONArray caseTypeArr = new JSONArray();
		JSONArray gstTypeArr = new JSONArray();
		JSONArray gstStatusArr = new JSONArray();
		JSONArray gstSectionArr = new JSONArray();
		JSONArray stateArr = new JSONArray();
		JSONArray priorityArr = new JSONArray();
		JSONArray noticeTypeArr = new JSONArray();
		JSONArray noticeSectionArr = new JSONArray();
		JSONArray noticeStatusArr = new JSONArray();
		CallableStatement cstmt = null;
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String gstType = (obj.has("gstType")) ? obj.getString("gstType") : "";
		String gstSection = (obj.has("gstSection")) ? obj.getString("gstSection") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		int id = (obj.has("id")) ? obj.getInt("id") : 0;
		int company = (obj.has("company")) ? obj.getInt("company") : 0;
		int business = (obj.has("business")) ? obj.getInt("business") : 0;
		int vertical = (obj.has("vertical")) ? obj.getInt("vertical") : 0;
		int caseType = (obj.has("caseType")) ? obj.getInt("caseType") : 0;
		String asstYear = (obj.has("asstYear")) ? obj.getString("asstYear") : "";
		String panNo = (obj.has("panNo")) ? obj.getString("panNo") : "";
		String noticeType = (obj.has("noticeType")) ? obj.getString("noticeType") : "";
		int noticeSection = (obj.has("noticeSection")) ? obj.getInt("noticeSection") : 0;
		String issuedByName = (obj.has("issuedByName")) ? obj.getString("issuedByName") : "";
		String issuedByDesig = (obj.has("issuedByDesig")) ? obj.getString("issuedByDesig") : "";
		String dateOnNotice = (obj.has("dateOnNotice")) ? obj.getString("dateOnNotice") : "";
		String dateOnWhichNoticeReceived = (obj.has("dateOnWhichNoticeReceived"))
				? obj.getString("dateOnWhichNoticeReceived")
				: "";
		String dateOnWhichNoticeRequiredToReply = (obj.has("dateOnWhichNoticeRequiredToReply"))
				? obj.getString("dateOnWhichNoticeRequiredToReply")
				: "";
		String dateOfReply = (obj.has("dateOfReply")) ? obj.getString("dateOfReply") : "";
		String remarks = (obj.has("remarks")) ? obj.getString("remarks") : "";
		int status = (obj.has("status")) ? obj.getInt("status") : 0;
		String enteredBy = (obj.has("enteredBy")) ? obj.getString("enteredBy") : "";
		String addressedTo = (obj.has("addressedTo")) ? obj.getString("addressedTo") : "";
		int state = (obj.has("state")) ? obj.getInt("state") : 0;
		String issuedByOrganization = (obj.has("issuedByOrganization")) ? obj.getString("issuedByOrganization") : "";
		String natureOfParty = (obj.has("natureOfParty")) ? obj.getString("natureOfParty") : "";
		String briefFacts = (obj.has("briefFacts")) ? obj.getString("briefFacts") : "";
		String stakeInvolment = (obj.has("stakeInvolment")) ? obj.getString("stakeInvolment") : "";
		int priority = (obj.has("priority")) ? obj.getInt("priority") : 0;
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date1 = null;
		java.util.Date date2 = null;
		java.util.Date date3 = null;
		java.util.Date date4 = null;
		java.sql.Date sqlDate1 = null;
		java.sql.Date sqlDate2 = null;
		java.sql.Date sqlDate3 = null;
		java.sql.Date sqlDate4 = null;
		try {
			if (!("".equals(dateOnNotice))) {
				date1 = sdf1.parse(dateOnNotice);
				sqlDate1 = new java.sql.Date(date1.getTime());
			}
			if (!("".equals(dateOnWhichNoticeReceived))) {
				date2 = sdf1.parse(dateOnWhichNoticeReceived);
				sqlDate2 = new java.sql.Date(date2.getTime());
			}
			if (!("".equals(dateOnWhichNoticeRequiredToReply))) {
				date3 = sdf1.parse(dateOnWhichNoticeRequiredToReply);
				sqlDate3 = new java.sql.Date(date3.getTime());
			}
			if (!("".equals(dateOfReply))) {
				date4 = sdf1.parse(dateOfReply);
				sqlDate4 = new java.sql.Date(date4.getTime());
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
			cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.noticeManagProc
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			cstmt.setString(1, type);
			cstmt.setString(2, userID);
			cstmt.setInt(3, id);
			cstmt.setInt(4, company);
			cstmt.setInt(5, business);
			cstmt.setInt(6, vertical);
			cstmt.setInt(7, caseType);
			cstmt.setString(8, asstYear);
			cstmt.setString(9, panNo);
			cstmt.setString(10, noticeType);
			cstmt.setInt(11, noticeSection);
			cstmt.setString(12, issuedByName);
			cstmt.setString(13, issuedByDesig);
			cstmt.setDate(14, sqlDate1);
			cstmt.setDate(15, sqlDate2);
			cstmt.setDate(16, sqlDate3);
			cstmt.setDate(17, sqlDate4);
			cstmt.setString(18, remarks);
			cstmt.setInt(19, status);
			cstmt.setString(20, enteredBy);
			cstmt.setInt(21, state);
			cstmt.setString(22, addressedTo);
			cstmt.setString(23, issuedByOrganization);
			cstmt.setString(24, natureOfParty);
			cstmt.setString(25, briefFacts);
			cstmt.setString(26, stakeInvolment);
			cstmt.setInt(27, priority);
			cstmt.execute();
			rs = cstmt.getResultSet();
			if ("add".equalsIgnoreCase(type) || "edit".equalsIgnoreCase(type) || "disable".equalsIgnoreCase(type)) {
				if (rs.next()) {

					outJson.put("count", rs.getInt(1));
					outJson.put("id", rs.getInt(2));
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");

				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Unable to update notice...Pls try later");
				}
			} else if ("fetchSection".equalsIgnoreCase(type)) {

				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("sectionId", rs.getString(2));
					jobj.put("desc", rs.getString(3));

					noticeSectionArr.put(jobj);

				}
				outJson.put("noticeSectionMaster", noticeSectionArr);
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
			} else if ("fetchNotices".equalsIgnoreCase(type)) {

				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("noticeID", rs.getInt(1));
					jobj.put("company", rs.getInt(2));
					jobj.put("companyName", rs.getString(3));
					jobj.put("companyAlias", rs.getString(4));
					jobj.put("business", rs.getInt(5));
					jobj.put("businessName", rs.getString(6));
					jobj.put("vertical", rs.getInt(7));
					jobj.put("verticalName", rs.getString(8));
					jobj.put("caseType", rs.getInt(9));
					jobj.put("caseTypeName", rs.getString(10));
					jobj.put("assessYear", rs.getString(11));
					jobj.put("panNo", rs.getString(12));
					jobj.put("noticeMasterID", rs.getInt(13));
					jobj.put("noticeType", rs.getString(14));
					jobj.put("noticeSection", rs.getString(15));
					jobj.put("sectionDesc", rs.getString(16));
					jobj.put("dateOnNotice", rs.getDate(17));
					jobj.put("dateOnWhichNoticeReceived", rs.getDate(18));
					jobj.put("dateOnWhichNoticeRequiredToReply", rs.getDate(19));
					jobj.put("dateOfReply", rs.getDate(20));
					jobj.put("remarks", rs.getString(21));
					jobj.put("enrtyDate", rs.getTimestamp(22));
					jobj.put("statusID", rs.getString(23));
					jobj.put("statusName", rs.getString(24));
					jobj.put("enteredBy", rs.getString(25));
					jobj.put("state", rs.getInt(26));
					jobj.put("stateName", rs.getString(27));
					jobj.put("issuedByName", rs.getString(28));
					jobj.put("issuedByDesig", rs.getString(29));
					jobj.put("issuedByOrganization", rs.getString(30));
					jobj.put("addressedTo", rs.getString(31));
					jobj.put("natureOfParty", rs.getString(32));
					jobj.put("briefFacts", rs.getString(33));
					jobj.put("stakeInvolment", rs.getString(34));
					jobj.put("priority", rs.getInt(35));
					jobj.put("priorityName", rs.getString(36));

					noticesArr.put(jobj);

				}
				outJson.put("noticesMaster", noticesArr);
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
			} else if ("fetchLastWeekNotices".equalsIgnoreCase(type)) {
				if(rs != null) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("noticeID", rs.getInt(1));
					jobj.put("company", rs.getInt(2));
					jobj.put("companyName", rs.getString(3));
					jobj.put("companyAlias", rs.getString(4));
					jobj.put("business", rs.getInt(5));
					jobj.put("businessName", rs.getString(6));
					jobj.put("vertical", rs.getInt(7));
					jobj.put("verticalName", rs.getString(8));
					jobj.put("caseType", rs.getInt(9));
					jobj.put("caseTypeName", rs.getString(10));
					jobj.put("assessYear", rs.getString(11));
					jobj.put("panNo", rs.getString(12));
					jobj.put("noticeMasterID", rs.getInt(13));
					jobj.put("noticeType", rs.getString(14));
					jobj.put("noticeSection", rs.getString(15));
					jobj.put("sectionDesc", rs.getString(16));
					jobj.put("dateOnNotice", rs.getDate(17));
					jobj.put("dateOnWhichNoticeReceived", rs.getDate(18));
					jobj.put("dateOnWhichNoticeRequiredToReply", rs.getDate(19));
					jobj.put("dateOfReply", rs.getDate(20));
					jobj.put("remarks", rs.getString(21));
					jobj.put("enrtyDate", rs.getTimestamp(22));
					jobj.put("statusID", rs.getString(23));
					jobj.put("statusName", rs.getString(24));
					jobj.put("enteredBy", rs.getString(25));
					jobj.put("state", rs.getInt(26));
					jobj.put("stateName", rs.getString(27));
					jobj.put("issuedByName", rs.getString(28));
					jobj.put("issuedByDesig", rs.getString(29));
					jobj.put("issuedByOrganization", rs.getString(30));
					jobj.put("addressedTo", rs.getString(31));
					jobj.put("natureOfParty", rs.getString(32));
					jobj.put("briefFacts", rs.getString(33));
					jobj.put("stakeInvolment", rs.getString(34));
					jobj.put("priority", rs.getInt(35));
					jobj.put("priorityName", rs.getString(36));

					noticesArr.put(jobj);

				}
				}
				outJson.put("noticesMaster", noticesArr);
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
			}else if ("fetchNoticeInfo".equalsIgnoreCase(type)) {

				if (rs.next()) {

					
					outJson.put("noticeID", rs.getInt(1));
					outJson.put("company", rs.getInt(2));
					outJson.put("companyName", rs.getString(3));
					outJson.put("companyAlias", rs.getString(4));
					outJson.put("business", rs.getInt(5));
					outJson.put("businessName", rs.getString(6));
					outJson.put("vertical", rs.getInt(7));
					outJson.put("verticalName", rs.getString(8));
					outJson.put("caseType", rs.getInt(9));
					outJson.put("caseTypeName", rs.getString(10));
					outJson.put("assessYear", rs.getString(11));
					outJson.put("panNo", rs.getString(12));
					outJson.put("noticeMasterID", rs.getInt(13));
					outJson.put("noticeType", rs.getString(14));
					outJson.put("noticeSection", rs.getString(15));
					outJson.put("sectionDesc", rs.getString(16));
					outJson.put("dateOnNotice", rs.getString(17));
					outJson.put("dateOnWhichNoticeReceived", rs.getString(18));
					outJson.put("dateOnWhichNoticeRequiredToReply", rs.getString(19));
					outJson.put("dateOfReply", rs.getString(20));
					outJson.put("remarks", rs.getString(21));
					outJson.put("entryDate", rs.getString(22));
					outJson.put("statusID", rs.getString(23));
					outJson.put("statusName", rs.getString(24));
					outJson.put("enteredBy", rs.getString(25));
					outJson.put("state", rs.getInt(26));
					outJson.put("stateName", rs.getString(27));
					outJson.put("issuedByName", rs.getString(28));
					outJson.put("issuedByDesig", rs.getString(29));
					outJson.put("issuedByOrganization", rs.getString(30));
					outJson.put("addressedTo", rs.getString(31));
					outJson.put("natureOfParty", rs.getString(32));
					outJson.put("briefFacts", rs.getString(33));
					outJson.put("stakeInvolment", rs.getString(34));
					outJson.put("priority", rs.getInt(35));
					outJson.put("priorityName", rs.getString(36));
					outJson.put("docCount", rs.getInt(37));
					

				}
			
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
			} else if ("fetchMaster".equalsIgnoreCase(type)) {

				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));

					companyArr.put(jobj);

				}
				cstmt.getMoreResults();
				rs2 = cstmt.getResultSet();
				while (rs2.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs2.getInt(1));
					jobj.put("name", rs2.getString(2));

					caseTypeArr.put(jobj);
				}

				cstmt.getMoreResults();
				rs3 = cstmt.getResultSet();
				while (rs3.next()) {

					noticeTypeArr.put(rs3.getString(1));
				}

				cstmt.getMoreResults();
				rs4 = cstmt.getResultSet();
				while (rs4.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs4.getInt(1));
					jobj.put("name", rs4.getString(2));

					noticeStatusArr.put(jobj);
				}

				cstmt.getMoreResults();
				rs5 = cstmt.getResultSet();
				while (rs5.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs5.getInt(1));
					jobj.put("name", rs5.getString(2));

					stateArr.put(jobj);
				}

				cstmt.getMoreResults();
				rs6 = cstmt.getResultSet();
				while (rs6.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs6.getInt(1));
					jobj.put("name", rs6.getString(2));

					priorityArr.put(jobj);
				}
				
				cstmt.getMoreResults();
				rs7 = cstmt.getResultSet();
				while (rs7.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs7.getInt(1));
					jobj.put("type", rs7.getString(2));

					gstTypeArr.put(jobj);
				}
				
				cstmt.getMoreResults();
				rs8 = cstmt.getResultSet();
				while (rs8.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs8.getInt(1));
					jobj.put("status", rs8.getString(2));

					gstStatusArr.put(jobj);
				}
				
				cstmt.getMoreResults();
				rs9 = cstmt.getResultSet();
				while (rs9.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs9.getInt(1));
					jobj.put("section", rs9.getString(2));

					gstSectionArr.put(jobj);
				}
				
				outJson.put("companyArray", companyArr);
				outJson.put("caseTypeArray", caseTypeArr);
				outJson.put("noticeTypeArray", noticeTypeArr);
				outJson.put("noticeStatusArray", noticeStatusArr);
				outJson.put("stateArray", stateArr);
				outJson.put("priorityArray", priorityArr);
				outJson.put("gstTypeArray", gstTypeArr);
				outJson.put("gstStatusArray", gstStatusArr);
				outJson.put("gstSectionArray", gstSectionArr);
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
			}

		} catch (Exception e) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", e.getMessage());
		} finally {
			if (rs != null)
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - rs ==>" + e.getMessage());
				}
			if (rs2 != null)
				try {
					rs2.close();
					rs2 = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - rs ==>" + e.getMessage());
				}
			if (rs3 != null)
				try {
					rs3.close();
					rs3 = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - rs ==>" + e.getMessage());
				}
			if (rs4 != null)
				try {
					rs4.close();
					rs4 = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - rs ==>" + e.getMessage());
				}
			if (rs5 != null)
				try {
					rs5.close();
					rs5 = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - rs ==>" + e.getMessage());
				}

			if (cstmt != null)
				try {
					cstmt.close();
					cstmt = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (con != null)
				try {
					con.close();
					con = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - con ==>" + e.getMessage());
				}
		}
		}
		response = outJson.toString(4);
		return response;

	}
	
	public String searchNotices(String input,String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		Connection con1=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String company = (obj.has("company")) ? obj.getString("company") : "";
		String business = (obj.has("business")) ? obj.getString("business") : "";
		String vertical = (obj.has("vertical")) ? obj.getString("vertical") : "";
		String caseType = (obj.has("caseType")) ? obj.getString("caseType") : "";	
		String noticeStatus = (obj.has("noticeStatus")) ? obj.getString("noticeStatus") : "";	
		String state = (obj.has("state")) ? obj.getString("state") : "";
		String startDate = (obj.has("startDate")) ? obj.getString("startDate") : "";
		String endDate = (obj.has("endDate")) ? obj.getString("endDate") : "";
		String dateOnNoticeSDate = (obj.has("dateOnNoticeSDate")) ? obj.getString("dateOnNoticeSDate") : "";
		String dateOnNoticeEDate = (obj.has("dateOnNoticeEDate")) ? obj.getString("dateOnNoticeEDate") : "";
		String dateRequiredToReply = (obj.has("dateRequiredToReply")) ? obj.getString("dateRequiredToReply") : "";
		
		

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = null;
		java.util.Date date1 = null;
		java.sql.Date sqlDate1 = null;
		java.sql.Date sqlDate2 = null;
		try {
			if (!("".equals(startDate))) {
				date = sdf1.parse(startDate);
				sqlDate1 = new java.sql.Date(date.getTime());
			}
			if (!("".equals(endDate))) {
				date1 = sdf1.parse(endDate);
				sqlDate2 = new java.sql.Date(date1.getTime());
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		java.util.Date date3 = null;
		java.util.Date date4 = null;
		java.sql.Date sqlDate3 = null;
		java.sql.Date sqlDate4 = null;
		try {
			if (!("".equals(dateOnNoticeSDate))) {
				date3 = sdf1.parse(dateOnNoticeSDate);
				sqlDate3 = new java.sql.Date(date3.getTime());
			}
			if (!("".equals(dateOnNoticeEDate))) {
				date4 = sdf1.parse(dateOnNoticeEDate);
				sqlDate4 = new java.sql.Date(date4.getTime());
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		java.util.Date date5 = null;
		java.sql.Date sqlDate5 = null;
		try {
			if (!("".equals(dateRequiredToReply))) {
				date5 = sdf1.parse(dateRequiredToReply);
				sqlDate5 = new java.sql.Date(date5.getTime());
			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int i = 0;
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		if (sessionID == null) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session has expired. Please logout and login again...");
		} else if("".equals(sessID) || (!sessionID.equals(sessID))) {
		 outJson.put(LawSuitTrackerConstants.status, "F");
		 outJson.put("msg", "Your session is invalid or expired. Please try again later!");
		 }
		 else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		 outJson.put(LawSuitTrackerConstants.status, "F");
		 outJson.put("msg", "Your are not authorized to perform this action.");
		 }
		 else
		 {
		try {
			con = SQLDBConnection.getDBConnection();
			String query = " select a.notice_id,company,company_name,company_alias,business,business_name,vertical,vertical_name " + 
					" ,case_category,case_type,asst_year,PAN,notice_master_id,notice_type,notice_section,section_desc " + 
					" ,date_on_notice,date_on_which_notice_received,date_on_which_notice_required_to_reply,date_of_reply " + 
					" ,remarks,timestamp,notice_status,status_name,enteredBy,state_id,state_name " + 
					" ,issued_by_name,issued_by_desig,issued_by_company,addressed_to_name,nature_of_party,brief_facts " + 
					" ,stake_involment,notice_priority,priority_name, (select count(*) as count " + 
					" from notice_docs where notice_id = a.notice_id and status = 1) from vw_notice_master as a where vertical in (SELECT id " + 
					" FROM vw_user_vertical where user_id = ?) and case_category in (select case_id from vw_user_case_type where payroll_no =  ?) and status = 1 ";

			if (!("".equals(company)) && !("0".equals(company)))
				query += " and company in (" + company + ")";
			if (!("".equals(noticeStatus)) && !("0".equals(noticeStatus)))
				query += " and status in (" + noticeStatus + ")";
			if (!("".equals(business)) && !("0".equals(business)))
				query += " and business in (" + business + ")";
			if (!("".equals(vertical)) && !("0".equals(vertical)))
				query += " and vertical in (" + vertical + ")";
			if (!("".equals(caseType)) && !("0".equals(caseType)))
				query += " and case_category in (" + caseType + ")";			
			if (!("".equals(state)) && !("0".equals(state)))
				query += " and state_circle in (" + state + ")";
			if (!("".equals(dateRequiredToReply)))
				query += " and CAST(date_on_which_notice_required_to_reply as date) < = '" + sqlDate5 + "'";
			

			
			if (!("".equals(startDate)) && !("".equals(endDate)))
				query += " and CAST(timestamp as date) between ? and ?";
			else if (!("".equals(startDate)) && ("".equals(endDate)))
				query += " and CAST(timestamp as date) >= ? ";
			else if (("".equals(startDate)) && !("".equals(endDate)))
				query += " and CAST(timestamp as date) <= ? ";
			
			if (!("".equals(dateOnNoticeSDate)) && !("".equals(dateOnNoticeEDate)))
				query += " and CAST(date_on_notice as date) between '"+sqlDate3+"' and '"+sqlDate4 + "'";
			else if (!("".equals(dateOnNoticeSDate)) && ("".equals(dateOnNoticeEDate)))
				query += " and CAST(date_on_notice as date) >= '"+sqlDate3+"'";
			else if (("".equals(dateOnNoticeSDate)) && !("".equals(dateOnNoticeEDate)))
				query += " and CAST(date_on_notice as date) <= '"+sqlDate4+"' ";	
			
			
			
			
			query += " order by company, a.notice_id, timestamp";

			System.out.println(query);
			stmt = con.prepareStatement(query);
			stmt.setString(1, userID);
			stmt.setString(2, userID);
			if (!("".equals(dateRequiredToReply)))
				stmt.setDate(3, sqlDate5);
			
			System.out.print(stmt.getFetchSize());

			if (!("".equals(startDate)) && !("".equals(endDate))) {
				stmt.setDate(3, sqlDate1);
				stmt.setDate(4, sqlDate2);
			} else if (!("".equals(startDate)) && ("".equals(endDate)))
				stmt.setDate(3, sqlDate1);
			else if (("".equals(startDate)) && !("".equals(endDate)))
				stmt.setDate(3, sqlDate2);
			
			
			
			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("vertical_name", rs.getInt(1));
				i++;
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
				resultArray.put(jobj);

			}
			if (resultArray.length() == 0) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No notices available");
			}
			outJson.put("noticesMaster", resultArray);
		} catch (SQLException e) {
			logger.error("Login Error in SQL Exception ==>" + e);
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "There seems some error at server side. Please try again later!!!");
		} finally {
			if (rs != null)
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - rs ==>" + e.getMessage());
				}
			if (stmt != null)
				try {
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (con != null)
				try {
					con.close();
					con = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - con ==>" + e.getMessage());
				}
		}
		 }
		response = outJson.toString(4); //pretty print 
		return response;
	}
	
	public String testapi(String input, String sessionID, String sessUser) {
	    String response = "";
	    Connection con = null;
	    Connection con1 = null;
	    Connection con2 = null;
	    PreparedStatement stmt = null;
	    PreparedStatement stmt1 = null;
	    //PreparedStatement stmt2 = null;
	    ResultSet rs = null;
	    ResultSet rs1 = null;
	    //ResultSet rs2 = null;

	    JSONObject outJson = new JSONObject();
	    JSONArray resultArray = new JSONArray();

	    try {
	        JSONObject obj = new JSONObject(input);
	        String userID = obj.optString("userID", "");
	        String vertical = obj.optString("vertical", "");
	        String sessID = obj.optString("sessID", "");	        
			String company = (obj.has("company")) ? obj.getString("company") : "";
			String business = (obj.has("business")) ? obj.getString("business") : "";
			String gstType = (obj.has("gstType")) ? obj.getString("gstType") : "";
			String gstStatus = (obj.has("gstStatus")) ? obj.getString("gstStatus") : "";
			String gstSection = (obj.has("gstSection")) ? obj.getString("gstSection") : "";	
			String state = (obj.has("state")) ? obj.getString("state") : "";
//			String dateRequiredToReply = (obj.has("dateRequiredToReply")) ? obj.getString("dateRequiredToReply") : "";

	        // Session validation
	        if (sessionID == null || sessionID.isEmpty()) {
	            outJson.put("status", "F");
	            outJson.put("msg", "Your session has expired. Please logout and login again...");
	            return outJson.toString(4);
	        }

	        if (!sessionID.equals(sessID)) {
	            outJson.put("status", "F");
	            outJson.put("msg", "Your session is invalid or expired.");
	            return outJson.toString(4);
	        }

	        if (!sessUser.equals(userID)) {
	            outJson.put("status", "F");
	            outJson.put("msg", "You are not authorized to perform this action.");
	            return outJson.toString(4);
	        }
	        
	        List<String> verticalNames = new ArrayList<>();
	        con =
	        		SQLDBConnection.getDBConnection();
	        String verticalQuery = "SELECT a.vertical_name FROM vertical_master a ";
	        
	        if(!("".equals(vertical)) && !("0".equals(vertical))) {
	        	verticalQuery += "where a.id=? order by a.vertical_name";
	        	  stmt = con.prepareStatement(verticalQuery);
	  	          stmt.setString(1, vertical);
	        }
	        else if(!("".equals(company)) && !("0".equals(company)) && !("".equals(business)) && !("0".equals(business))) {
	        	verticalQuery += ", business_master b , company_master c where c.id=b.company_id and b.id=a.business_id and ";
	        	verticalQuery += " b.id in (" + business + ") and ";
	        	verticalQuery += " c.id in (" + company + ") order by a.vertical_name";
	        	  stmt = con.prepareStatement(verticalQuery);
	        }
	        else if(!("".equals(company))&& !("0".equals(company))) {
	        	verticalQuery += ", business_master b , company_master c where c.id=b.company_id and b.id=a.business_id and  ";
	        	verticalQuery += " c.id in (" + company + ") order by a.vertical_name";
	        	  stmt = con.prepareStatement(verticalQuery);
	        }
	        else {
	        	  stmt = con.prepareStatement(verticalQuery);
			}
	        logger.info("Query : "+verticalQuery);
	        rs = stmt.executeQuery();
	        while (rs.next()) {
	            verticalNames.add(rs.getString("vertical_name").trim().toLowerCase());
	        }
	        rs.close();
	        stmt.close();
	        con.close();

	        if (verticalNames.isEmpty()) {
	            outJson.put("status", "F");
	            outJson.put("msg", "No verticals found for given ID.");
	            return outJson.toString(4);
	        }

	        // --- PHASE 2: Fetch matching companies and GST IDs ---
	        con1 = SecondDBConnection.getDBConnection();
	        String companyQuery = "SELECT companyname, gstid FROM tbl_gstmaster";
	        stmt1 = con1.prepareStatement(companyQuery);
	        rs1 = stmt1.executeQuery();

	        List<String> matchedGSTIds = new ArrayList<>();
	        Map<String, String> gstCompanyMap = new HashMap<>();

	        while (rs1.next()) {
	            String companyName = rs1.getString("companyname").trim();
	            String gstid = rs1.getString("gstid").trim();

	            // Normalize for comparison
	            String companyClean = companyName.replaceAll("\\s+", " ").toLowerCase();

	            if (verticalNames.contains(companyClean)) {
	                matchedGSTIds.add(gstid);
	                gstCompanyMap.put(gstid, companyName);
	            }
	        }

	        rs1.close();
	        stmt1.close();
	        con1.close();

	        if (matchedGSTIds.isEmpty()) {
	            outJson.put("status", "F");
	            outJson.put("msg", "No matched company names found for verticals.");
	            return outJson.toString(4);
	        }
	        // --- PHASE 3: Fetch GST details using gstid ---
	        con2 = SecondDBConnection.getDBConnection();

	        String detailQuery = 
	        	    "SELECT * FROM ( " +
	        	    "  SELECT " +
	        	    "    a.GSTId, " +
	        	    "    a.GSTIN, " +
	        	    "    a.CompanyName, " +
	        	    "    b.GSTNoticeOrderId AS REFID, " +
	        	    "    b.type AS TYPE, " +
	        	    "    c.status, " +
	        	    "    b.DateOfIssue as DateOfIssue, " +
	        	    "    b.descr, " +
	        	    "    b.issuedBy, " +
	        	    "    b.docId, " +
	        	    "    'Notices and Orders' AS section, " +
	        	    "    c.id AS statusId, " +
	        	    "    1 AS section_id " +
	        	    "  FROM tbl_gstmaster a " +
	        	    "  INNER JOIN tbl_noticeordermaster b ON a.GSTId = b.FkGSTId " +
	        	    "  INNER JOIN tbl_noticeordermaster_status d ON b.NoticeOrderId = d.FkNoticeOrderId " +
	        	    "  INNER JOIN tbl_gst_status_master c ON d.StatusId = c.id " +

	        	    "  UNION " +

	        	    "  SELECT " +
	        	    "    a.GSTId, " +
	        	    "    a.GSTIN, " +
	        	    "    a.CompanyName, " +
	        	    "    E.NoticeOrderId AS REFID, " +
	        	    "    E.TYPE AS TYPE, " +
	        	    "    c.status, " +
	        	    "    E.AssignmentDate as DateOfIssue, " +
	        	    "    E.descr, " +
	        	    "    E.issuedBy, " +
	        	    "    NULL AS docId, " +
	        	    "    'Additional Notices' AS section, " +
	        	    "    c.id AS statusId, " +
	        	    "    2 AS section_id " +
	        	    "  FROM tbl_gstmaster a " +
	        	    "  INNER JOIN tbl_additionalnoticemaster E ON a.GSTId = E.FkGSTId " +
	        	    "  INNER JOIN tbl_additionalnoticestatus F ON F.FkAdditionalNoticeId = E.AdditionalNoticeId " +
	        	    "  INNER JOIN tbl_gst_status_master c ON F.StatusId = c.id " +
	        	    ") AS unioned ";


	        
	        //Handling Multiple String Values
	        
	        if (matchedGSTIds != null && !matchedGSTIds.isEmpty()) {
	            String gstPlaceholders = String.join(",", Collections.nCopies(matchedGSTIds.size(), "?"));
	            detailQuery += " where unioned.GSTId IN (" + gstPlaceholders + ")";
	        }
	        
	        
	        String[] stateArray = null;
	        if (!("".equals(state)) && !("0".equals(state))) {
	        	stateArray = state.split(",");
	            String stateHolders = String.join(",", Collections.nCopies(stateArray.length, "?"));
	            detailQuery += " AND unioned.issuedBy IN (" + stateHolders + ")";
	        }
	        
	        
	        String[] gstTypeArray = null;
	        if (!("".equals(gstType)) && !("0".equals(gstType))) {
	            gstTypeArray = gstType.split(",");
	            String placeholders = String.join(",", Collections.nCopies(gstTypeArray.length, "?"));
	            detailQuery += " AND unioned.TYPE IN (" + placeholders + ")";
	        }
	        
	        if (!("".equals(gstStatus)) && !("0".equals(gstStatus))) {	           
	            detailQuery += " AND unioned.statusId IN (" + gstStatus + ")";
	        }
	        
	        if (!("".equals(gstSection)) && !("0".equals(gstSection))) {	           
	            detailQuery += " AND unioned.section_id IN (" + gstSection + ")";
	        }
	        
	        logger.info("Detail Query : "+detailQuery);
	            try (PreparedStatement stmt2 = con2.prepareStatement(detailQuery)) {
	            	int paramIndex = 1;
	                for (String gstid : matchedGSTIds) {
	                    stmt2.setString(paramIndex++, gstid);
	                }
	                
	                if (stateArray != null) {
	                    for (String typeValue : stateArray) {
	                        stmt2.setString(paramIndex++, typeValue.trim());
	                    }
	                }
	                
	                if (gstTypeArray != null) {
	                    for (String typeValue : gstTypeArray) {
	                        stmt2.setString(paramIndex++, typeValue.trim());
	                    }
	                }
	                
	                try (ResultSet rs2 = stmt2.executeQuery()) {
	                    while (rs2.next()) {
	                        JSONObject gstInfo = new JSONObject();
	                        gstInfo.put("refId" , getSafeString(rs2, "REFID"));
	                        gstInfo.put("section_id" , getSafeString(rs2, "section_id"));
	                        gstInfo.put("gstStatus" , getSafeString(rs2, "status"));
	                        gstInfo.put("gstin", getSafeString(rs2,"GSTIN"));
	                        gstInfo.put("companyname", getSafeString(rs2,"CompanyName"));
	                        gstInfo.put("type", getSafeString(rs2,"TYPE"));
	                        String dateOfIssue = getSafeString(rs2,"DateOfIssue").split(" ")[0];
	                        gstInfo.put("dateOfIssue", dateOfIssue);
	                        gstInfo.put("description", getSafeString(rs2,"descr"));
	                        gstInfo.put("issuedBy", getSafeString(rs2,"issuedBy"));
	                        gstInfo.put("docId", getSafeString(rs2,"docId"));
	                        gstInfo.put("section", getSafeString(rs2,"section"));
	                        gstInfo.put("status", "Matched");

	                        resultArray.put(gstInfo);
	                    }
		                rs2.close();
	                }
	    	        stmt2.close();
	    	        con2.close();
	            } catch (SQLException e) {
	                logger.error("Error querying GST detail"+ e.getMessage());
	            }
	        

	        if (con2 != null) {
	            try {
	                con2.close();
	            } catch (Exception e) {
	                logger.error("Error closing con2: " + e.getMessage());
	            }
	        }



	        if (resultArray.length() > 0) {
	            outJson.put("status", "T");
	            outJson.put("msg", "Success");
	            outJson.put("gstMaster", resultArray);
	        } else {
	            outJson.put("status", "F");
	            outJson.put("msg", "No GST details found for matched GST IDs.");
	        }

	    } catch (Exception e) {
	        logger.error("Error in testapi() ==> " + e.getMessage(), e);
	        try {
	            outJson.put("status", "F");
	            outJson.put("msg", "Serv0er error occurred. Please try again later.");
	        } catch (Exception ex) {
	            logger.error("Error writing JSON response: " + ex.getMessage());
	        }
	    } finally {
	        try { if (rs != null) rs.close(); } catch (Exception e) {}
	        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
	        try { if (con != null) con.close(); } catch (Exception e) {}
	        try { if (rs1 != null) rs1.close(); } catch (Exception e) {}
	        try { if (stmt1 != null) stmt1.close(); } catch (Exception e) {}
	        try { if (con1 != null) con1.close(); } catch (Exception e) {}
	        try { if (con2 != null) con2.close(); } catch (Exception e) {}
	    }

	    response = outJson.toString(4);
	    return response;
	}

	private String getSafeString(ResultSet rs, String columnName) throws SQLException {
	    String value = rs.getString(columnName);
	    return (value != null) ? value : "";
	}
	
	private String getSafeIntAsString(ResultSet rs, String columnName) throws SQLException {
	    int value = rs.getInt(columnName);
	    return rs.wasNull() ? "" : String.valueOf(value);
	}




}
