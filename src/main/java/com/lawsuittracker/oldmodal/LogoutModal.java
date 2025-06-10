package com.lawsuittracker.oldmodal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;

public class LogoutModal {
	public static final Logger logger = Logger.getLogger(LogoutModal.class);

	public String logout(String input) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject obj = new JSONObject(input);
		JSONObject outJson = new JSONObject();
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";

		try {
			con = SQLDBConnection.getDBConnection();
			String query = "update usage_log set logout_time = CURRENT_TIMESTAMP, is_LoggedIn = ? where session_id = ? and payroll_no = ?";
			stmt = con.prepareStatement(query);

			stmt.setString(1, "N");
			stmt.setString(2, sessID);
			stmt.setString(3, userID);

			int result = stmt.executeUpdate();
			if (result > 0) {
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");

			} else {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "Session is not able to set!!!");
			}

		} catch (SQLException e) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", e.getMessage());
			logger.error("Login Error in SQL Exception ==>" + e.getMessage());

		}

		finally {

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
		return response;
	}
}
