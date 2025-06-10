package com.lawsuittracker.oldmodal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;

public class ManageAlerts {

	public static final Logger logger = Logger.getLogger(ManageAlerts.class);

	public String searchAlert(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray newAlertsArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		String userID = obj.has("enteredBy") ? obj.getString("enteredBy") : "";
		int company = obj.has("company") ? obj.getInt("company") : 99;
		
		String startDate = obj.has("startDate") ? obj.getString("startDate") : "";
		String endDate = obj.has("endDate") ? obj.getString("endDate") : "";
		int notificationType = obj.has("notificationType") ? obj.getInt("notificationType") : 0;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = null;
		java.util.Date date1 = null;
		java.sql.Date sqlDate1 = null;
		java.sql.Date sqlDate2 = null;
		try {
			if (!"".equals(startDate)) {
				date = sdf1.parse(startDate);
				sqlDate1 = new java.sql.Date(date.getTime());
			}
			if (!"".equals(endDate)) {
				date1 = sdf1.parse(endDate);
				sqlDate2 = new java.sql.Date(date1.getTime());
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "declare @comp varchar(100) = '';declare @busi varchar(MAX) = '';declare @vert varchar(MAX) = '';declare @caseTy varchar(MAX) = '';select @busi = @busi + isnull(cast(id as varchar), '') + ',' from vw_user_business where user_id = '"+userID+"'; " + 
					"select @vert = @vert + isnull(cast(id as varchar), '') + ',' from vw_user_vertical where user_id = '"+userID+"';select @comp = @comp + isnull(cast(id as varchar), '') + ',' from vw_user_company where user_id = '"+userID+"';select @caseTy = @caseTy + isnull(cast(case_id as varchar), '') + ',' from vw_user_case_type where payroll_no = '"+userID+"';select distinct id,case_no,petitioner,respondent,forum_name,bench,counsel_of_company ,counsel_of_respondent,Expr2,sub_matter,case_status,case_type_name,diary_no ,court_no,item_no,company,business,vertical,case_category,forum,remark,action, timestamp,company_name,business_name,vertical_name,case_type ,causelist_extract ,updated_by,updated_on,lmms_case_id,next_hearing_date,status,should_have,pv_id ,link,should_not_have,pv_notification_type,json_request,type,forum_category,company_alias,not_id,description from vw_pv_alerts where status = 1 and business in (select s from dbo.split(',',@busi)) and vertical in (select s from dbo.split(',',@vert)) and (case_category in (select s from dbo.split(',',@caseTy)) or pv_notification_type = 'proactive_alert')  ";

			if ((!"".equals(Integer.valueOf(company))) && (company != 99))
				query = query + " and company in (" + company + ")";
			else
				query = query + " and company in (select s from dbo.split(',',@comp))";
			if ((notificationType != 99) && (notificationType != 0))
				query = query + " and not_id in (" + notificationType + ")";
			if ((!"".equals(startDate)) && (!"".equals(endDate))) {
				query = query + " and CAST(timestamp as date) between ? and ?";
			} else if ((!"".equals(startDate)) && ("".equals(endDate))) {
				query = query + " and CAST(timestamp as date) >= ? ";
			} else if (("".equals(startDate)) && (!"".equals(endDate))) {
				query = query + " and CAST(timestamp as date) <= ? ";
			}

			query = query + "  order by pv_notification_type desc,timestamp desc,company_alias,forum option(maxrecursion 0);";

			System.out.println(query);

			stmt = con.prepareStatement(query);

			if ((!"".equals(startDate)) && (!"".equals(endDate))) {
				stmt.setDate(1, sqlDate1);
				stmt.setDate(2, sqlDate2);
			} else if ((!"".equals(startDate)) && ("".equals(endDate))) {
				stmt.setDate(1, sqlDate1);
			} else if (("".equals(startDate)) && (!"".equals(endDate))) {
				stmt.setDate(1, sqlDate2);
			}

			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject ob = new JSONObject();
				ob.put("id", rs.getInt(1));
				ob.put("caseNo", rs.getString(2));
				ob.put("petitioner", rs.getString(3));
				ob.put("respondent", rs.getString(4));
				ob.put("forumName", rs.getString(5));
				ob.put("bench", rs.getString(6));
				ob.put("counselOfCompany", rs.getString(7));
				ob.put("counselOfRespondent", rs.getString(8));
				ob.put("nextHearingDate", rs.getString(9));
				ob.put("subMatter", rs.getString(10));
				ob.put("caseStatus", rs.getString(11));
				ob.put("caseType", rs.getString(12));
				ob.put("dairyNo", rs.getString(13));
				ob.put("courtNo", rs.getString(14));
				ob.put("itemNo", rs.getString(15));
				ob.put("company", rs.getInt(16));
				ob.put("business", rs.getInt(17));
				ob.put("vertical", rs.getInt(18));
				ob.put("caseCategory", rs.getInt(19));
				ob.put("forum", rs.getInt(20));
				ob.put("timestamp", rs.getTimestamp(23));
				ob.put("companyName", rs.getString(24));
				ob.put("businessName", rs.getString(25));
				ob.put("verticalName", rs.getString(26));
				ob.put("caseTypeName", rs.getString(27));
				ob.put("lmmsID", rs.getInt(31));
				ob.put("keywords", rs.getString(34));
				ob.put("pvID", rs.getString(35));
				ob.put("notificationType", rs.getString(38));
				ob.put("updateType", rs.getString(40));
				ob.put("forumCategory", rs.getString(41));
				ob.put("companyAlias", rs.getString(42));
				ob.put("notificationTypeID", rs.getString(43));
				ob.put("notificationTypeDesc", rs.getString(44));

				newAlertsArray.put(ob);
			}

			if (newAlertsArray.length() == 0) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No alerts available ");
			}
			outJson.put("newAlertsArray", newAlertsArray);
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
			if (con != null) {
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

	public String manageAlert(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		JSONArray compArray = new JSONArray();
		JSONArray notificationTypeArray = new JSONArray();
		CallableStatement cstmt = null;

		String type = obj.has("type") ? obj.getString("type") : "";
		String userID = obj.has("userID") ? obj.getString("userID") : "";
		int alertID = obj.has("alertID") ? obj.getInt("alertID") : 0;
		int action = obj.has("action") ? obj.getInt("action") : 0;
		String remark = obj.has("remark") ? obj.getString("remark") : "";
		int lmmsID = obj.has("lmmsID") ? obj.getInt("lmmsID") : 0;
		int companyID = obj.has("companyID") ? obj.getInt("companyID") : 0;
		int businessID = obj.has("businessID") ? obj.getInt("businessID") : 0;
		int verticalID = obj.has("verticalID") ? obj.getInt("verticalID") : 0;
		int caseCategoryID = obj.has("caseCategoryID") ? obj.getInt("caseCategoryID") : 0;
		try {
			con = SQLDBConnection.getDBConnection();

			cstmt = con
					.prepareCall("{call " + LawSuitTrackerConstants.alertNotificationProc + "(?,?,?,?,?,?,?,?,?,?)}");
			cstmt.setString(1, type);
			cstmt.setString(2, userID);
			cstmt.setInt(3, companyID);
			cstmt.setInt(4, businessID);
			cstmt.setInt(5, verticalID);
			cstmt.setInt(6, caseCategoryID);
			cstmt.setInt(7, action);
			cstmt.setString(8, remark);
			cstmt.setInt(9, alertID);
			cstmt.setInt(10, lmmsID);

			cstmt.execute();
			rs = cstmt.getResultSet();
			if ("fetchMaster".equalsIgnoreCase(type)) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));

					compArray.put(jobj);
				}
				outJson.put("compArray", compArray);

				cstmt.getMoreResults();
				rs1 = cstmt.getResultSet();

				while (rs1.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs1.getInt(1));
					jobj.put("name", rs1.getString(2));
					jobj.put("desc", rs1.getString(3));

					notificationTypeArray.put(jobj);
				}

				outJson.put("notificationTypeArray", notificationTypeArray);
				if (compArray.length() > 0) {
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "No cases available for the case id ");
				}
			} else if ("updateAlert".equalsIgnoreCase(type)) {
				if (rs.next()) {
					outJson.put("count", rs.getInt(1));
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}
			} else if ("editAlert".equalsIgnoreCase(type)) {
				if (rs.next()) {
					outJson.put("count", rs.getInt(1));
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}
			}
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
					logger.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (rs1 != null) {
				try {
					rs1.close();
					rs1 = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			}
			if (cstmt != null)
				try {
					cstmt.close();
					cstmt = null;
				} catch (Exception e) {
					logger.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (con != null) {
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

}
