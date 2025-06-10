package com.lawsuittracker.modal;

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

public class CommonFunctions {
	public static final Logger logger = Logger.getLogger(CommonFunctions.class);

	public String riaFetchAllCases(String startDate, String endDate) {
		String response = "";

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = null;
		java.util.Date date1 = null;
		java.sql.Date sqlDate1 = null;
		java.sql.Date sqlDate2 = null;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
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

		try {
			con = SQLDBConnection.getDBConnection();
			String query = "SELECT a.id, a.forum, CONVERT(varchar, a.next_hearing_date, 103) as dateCol, a.bench from case_master a where a.next_hearing_date between ? and ?";
			stmt = con.prepareStatement(query);
			stmt.setDate(1, sqlDate1);
			stmt.setDate(2, sqlDate2);
			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject ob = new JSONObject();
				ob.put("caseID", rs.getInt(1));
				ob.put("forumName", rs.getString(2));
				ob.put("nextHearingDate", rs.getString(3));
				ob.put("benchName", rs.getString(4));
				resultArray.put(ob);
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");

			}
			if (resultArray.length() == 0) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the selected criteria");
			}
			outJson.put("listOfCases", resultArray);
		} catch (SQLException e) {
			logger.error("Login Error in SQL Exception ==>" + e.getMessage());
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
		response = outJson.toString(4);
		return response;
	}

	public String riaCaseInfo(String caseID) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();

		try {
			con = SQLDBConnection.getDBConnection();
			String query = "SELECT a.id, a.forum,CONVERT(varchar, a.next_hearing_date, 103) as dateCol, a.bench  from case_master a where a.id = ?";
			stmt = con.prepareStatement(query);
			stmt.setInt(1, Integer.parseInt(caseID));

			rs = stmt.executeQuery();
			if (rs.next()) {
				outJson.put("caseID", rs.getInt(1));
				outJson.put("forumName", rs.getString(2));
				outJson.put("nextHearingDate", rs.getString(3));
				outJson.put("benchName", rs.getString(4));

				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");

			} else {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the case id " + caseID);
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
		response = outJson.toString(4);
		return response;
	}

}
