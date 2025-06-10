package com.lawsuittracker.modal;

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

public class ManageHearing {
	public static final Logger logg = Logger.getLogger(ManageHearing.class);

	public String updateHearing(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONArray counselArray = new JSONArray();
		JSONArray statusArray = new JSONArray();
		JSONArray benchArray = new JSONArray();
		JSONArray hearingArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String bench = (obj.has("bench")) ? obj.getString("bench") : "";
		String counselOfCompany = (obj.has("counselOfCompany")) ? obj.getString("counselOfCompany") : "";
		String counselOfRespondent = (obj.has("counselOfRespondent")) ? obj.getString("counselOfRespondent") : "";
		String lastDateOfHearing = (obj.has("lastDateOfHearing")) ? obj.getString("lastDateOfHearing") : "";
		String nextDateOfHearing = (obj.has("nextDateOfHearing")) ? obj.getString("nextDateOfHearing") : "";
		int caseStatus = (obj.has("caseStatus")) ? obj.getInt("caseStatus") : 0;
		String outcomeLast = (obj.has("outcomeLast")) ? obj.getString("outcomeLast") : "";
		String outcomeNext = (obj.has("outcomeNext")) ? obj.getString("outcomeNext") : "";
		String enteredBy = (obj.has("enteredBy")) ? obj.getString("enteredBy") : "";
		int caseID = (obj.has("caseID")) ? obj.getInt("caseID") : 0;
		int id = (obj.has("id")) ? obj.getInt("id") : 0;
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		CallableStatement cstmt = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = null;
		java.util.Date date1 = null;
		java.sql.Date sqlDate1 = null;
		java.sql.Date sqlDate2 = null;
		try {
			if (!("".equals(lastDateOfHearing))) {
				date = sdf1.parse(lastDateOfHearing);
				sqlDate1 = new java.sql.Date(date.getTime());
			}
			if (!("".equals(nextDateOfHearing))) {
				date1 = sdf1.parse(nextDateOfHearing);
				sqlDate2 = new java.sql.Date(date1.getTime());
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (sessionID == null) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session has expired. Please logout and login again...");
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session is invalid. Please try again later!");
		} else if (!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your are not authorized to perform this action.");
		} else {
			try {

				con = SQLDBConnection.getDBConnection();
				cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.updateHearingProc
						+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
				cstmt.setString(1, type);
				cstmt.setString(2, userID);
				cstmt.setString(3, bench);
				cstmt.setString(4, counselOfCompany);
				cstmt.setString(5, counselOfRespondent);
				cstmt.setDate(6, sqlDate1);
				cstmt.setDate(7, sqlDate2);
				cstmt.setString(8, outcomeLast);
				cstmt.setString(9, outcomeNext);
				cstmt.setInt(10, caseStatus);
				cstmt.setString(11, enteredBy);
				cstmt.setInt(12, caseID);
				cstmt.setInt(13, id);
				// cstmt.setString(13, sessID);

				cstmt.execute();
				rs = cstmt.getResultSet();
				if ("fetch".equalsIgnoreCase(type)) {

					while (rs.next()) {
						JSONObject ob = new JSONObject();
						ob.put("id", rs.getInt(1));
						ob.put("name", rs.getString(2));
						counselArray.put(ob);

					}
					cstmt.getMoreResults();
					rs2 = cstmt.getResultSet();

					while (rs2.next()) {
						JSONObject ob = new JSONObject();
						ob.put("id", rs2.getInt(1));
						ob.put("name", rs2.getString(2));
						statusArray.put(ob);

					}
					cstmt.getMoreResults();
					rs3 = cstmt.getResultSet();

					while (rs3.next()) {
						JSONObject ob = new JSONObject();
						ob.put("id", rs3.getInt(1));
						ob.put("name", rs3.getString(2));
						benchArray.put(ob);

					}

					outJson.put("statusArray", statusArray);
					outJson.put("counselArray", counselArray);
					outJson.put("benchArray", benchArray);
				} else if ("history".equalsIgnoreCase(type)) {
					while (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("bench", rs.getString(1));
						jobj.put("counselOfCompany", rs.getString(2));
						jobj.put("counselOfRespondent", rs.getString(3));
						jobj.put("lastHearingDate", rs.getString(4));
						jobj.put("nextHearingDate", rs.getString(5));
						jobj.put("outcomeLast", rs.getString(6));
						jobj.put("outcomeNext", rs.getString(7));
						jobj.put("userID", rs.getString(8));
						jobj.put("status", rs.getInt(9));
						jobj.put("entryDate", rs.getString(10));
						jobj.put("enteredBy", rs.getString(11));
						jobj.put("statusName", rs.getString(12));
						hearingArray.put(jobj);
					}
					outJson.put("hearingArray", hearingArray);
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else if ("update".equalsIgnoreCase(type)) {
					if (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("add", rs.getInt(1));
						jobj.put("update", rs.getInt(2));
						resultArray.put(jobj);
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");
						outJson.put("resultArray", resultArray);

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Unable to Save Hearing... Please try later..");
					}
				} else if ("updateHearing".equalsIgnoreCase(type)) {
					if (rs.next()) {

						outJson.put("add", rs.getInt(1));
						outJson.put("update", rs.getInt(2));
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Unable to Save Hearing... Please try later..");
					}
				} else if ("delete".equalsIgnoreCase(type)) {
					if (rs.next()) {

						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");
						outJson.put("count1", rs.getInt(1));
						outJson.put("count2", rs.getInt(2));

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Unable to delete Hearing... Please try later..");
					}
				}

			} catch (SQLException e) {
				logg.error("Login Error in SQL Exception ==>" + e);
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", e.getMessage());
			} finally {
				if (rs != null)
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				if (rs1 != null)
					try {
						rs1.close();
						rs1 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				if (rs2 != null)
					try {
						rs2.close();
						rs2 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				if (rs3 != null)
					try {
						rs3.close();
						rs3 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				if (cstmt != null)
					try {
						cstmt.close();
						cstmt = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (con != null)
					try {
						con.close();
						con = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - con ==>" + e.getMessage());
					}
			}
		}
		response = outJson.toString(4);
		return response;
	}

	public String hearingUpdateSummary(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		String caseID = (obj.has("caseID")) ? obj.getString("caseID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		if (sessionID == null) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session has expired. Please logout and login again...");
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session is invalid. Please try again later!");
		} else if (!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your are not authorized to perform this action.");
		} else {
			try {
				con = SQLDBConnection.getDBConnection();
				String query = "SELECT a.id, a.bench, a.counsel_of_petitioner, a.counsel_of_respondent, CONVERT(varchar, a.last_hearing_date, 103) as last_date "
						+ ", a.outcome_last_hearing, CONVERT(varchar,a.next_hearing_date, 103) , a.outcome_next_date, CONVERT(varchar, a.entry_date, 100)"
						+ " , a.entered_by, a.status, b.status_name, a.last_hearing_date, a.next_hearing_date from hearing_update a left outer join status_master b on a.status = b.id "
						+ "  where case_id = ? and update_status = 1 order by a.entry_date desc, cast(a.last_hearing_date as date) desc";

				// System.out.println(query);
				stmt = con.prepareStatement(query);
				stmt.setInt(1, Integer.parseInt(caseID));

				rs = stmt.executeQuery();
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("bench", rs.getString(2));
					jobj.put("counselOfCompany", rs.getString(3));
					jobj.put("counselOfRespondent", rs.getString(4));
					jobj.put("lastHearingDate", rs.getString(5));
					jobj.put("outcomeLast", rs.getString(6));
					jobj.put("nextHearingDate", rs.getString(7));
					jobj.put("outcomeNext", rs.getString(8));
					jobj.put("entryDate", rs.getString(9));
					jobj.put("enteredBy", rs.getString(10));
					jobj.put("statusID", rs.getInt(11));
					jobj.put("statusName", rs.getString(12));
					jobj.put("lastDt", rs.getString(13));
					jobj.put("nextDt", rs.getString(14));

					resultArray.put(jobj);
				}

				if (resultArray.length() == 0) {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "No cases available for the case id ");
				}
				outJson.put("updateHearingArray", resultArray);

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
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				if (stmt != null)
					try {
						stmt.close();
						stmt = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (con != null)
					try {
						con.close();
						con = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - con ==>" + e.getMessage());
					}
			}
		}
		response = outJson.toString(4);
		return response;

	}
}
