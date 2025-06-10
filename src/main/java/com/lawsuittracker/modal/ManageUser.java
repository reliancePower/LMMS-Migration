package com.lawsuittracker.modal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.lawsuittracker.util.SendMail;

public class ManageUser {
	public static final Logger logg = Logger.getLogger(ManageUser.class);
	SendMail sm = new SendMail();

	public String FetchUserRegMaster(String input, String sessionID, String sessUser) {
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
		CallableStatement cstmt = null;
		JSONObject j = new JSONObject();
		JSONArray userAccessArray = new JSONArray();
		JSONArray companyArray = new JSONArray();
		JSONArray businessArray = new JSONArray();
		JSONArray verticalArray = new JSONArray();
		JSONArray caseTypeArray = new JSONArray();
		JSONArray forumArray = new JSONArray();
		JSONArray statusArray = new JSONArray();
		JSONArray stateArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		if (sessionID == null) {
			j.put(LawSuitTrackerConstants.status, "F");
			j.put("msg", "Your session has expired. Please logout and login again...");
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			j.put(LawSuitTrackerConstants.status, "F");
			j.put("msg", "Your session is invalid or expired. Please try again  later!!!");
		} else if (!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
			j.put(LawSuitTrackerConstants.status, "F");
			j.put("msg", "Your are not authorized to perform this action.");
		} else {
			try {
				con = SQLDBConnection.getDBConnection();
				cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.userRegMaster + "(?)}");
				cstmt.setString(1, userID);
				cstmt.execute();
				rs = cstmt.getResultSet();

				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					userAccessArray.put(jobj);

				}
				j.put("userAccessArray", userAccessArray);
				cstmt.getMoreResults();

				rs2 = cstmt.getResultSet();
				while (rs2.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs2.getInt(1));
					jobj.put("name", rs2.getString(2));

					companyArray.put(jobj);
				}
				j.put("companyArray", companyArray);

				cstmt.getMoreResults();

				rs3 = cstmt.getResultSet();
				while (rs3.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs3.getInt(1));
					jobj.put("name", rs3.getString(2));
					jobj.put("compID", rs3.getInt(3));
					businessArray.put(jobj);
				}
				j.put("businessArray", businessArray);

				cstmt.getMoreResults();

				rs4 = cstmt.getResultSet();
				while (rs4.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs4.getInt(1));
					jobj.put("name", rs4.getString(2));
					jobj.put("fullname", rs4.getString(3));
					jobj.put("busiID", rs4.getInt(4));
					jobj.put("compID", rs4.getInt(5));
					verticalArray.put(jobj);
				}
				j.put("verticalArray", verticalArray);

				cstmt.getMoreResults();

				rs5 = cstmt.getResultSet();
				while (rs5.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs5.getInt(1));
					jobj.put("name", rs5.getString(2));

					caseTypeArray.put(jobj);
				}
				j.put("caseTypeArray", caseTypeArray);

				cstmt.getMoreResults();
				rs6 = cstmt.getResultSet();
				while (rs6.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs6.getInt(1));
					jobj.put("name", rs6.getString(2));
					forumArray.put(jobj);
				}

				cstmt.getMoreResults();
				rs7 = cstmt.getResultSet();
				while (rs7.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs7.getInt(1));
					jobj.put("name", rs7.getString(2));
					statusArray.put(jobj);
				}
				cstmt.getMoreResults();
				rs8 = cstmt.getResultSet();
				while (rs8.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs8.getInt(1));
					jobj.put("name", rs8.getString(2));
					stateArray.put(jobj);
				}
				j.put("statusArray", statusArray);
				j.put("forumArray", forumArray);
				j.put("stateArray", stateArray);
				j.put(LawSuitTrackerConstants.status, "T");
				j.put("msg", "Success");

			} catch (Exception e) {
				j.put(LawSuitTrackerConstants.status, "F");
				j.put("msg", e.getMessage());
			} finally {
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				}
				if (rs2 != null) {
					try {
						rs2.close();
						rs2 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				}
				if (rs3 != null)
					try {
						rs3.close();
						rs3 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (rs4 != null)
					try {
						rs4.close();
						rs4 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (rs5 != null)
					try {
						rs5.close();
						rs5 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (rs6 != null)
					try {
						rs6.close();
						rs6 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (rs7 != null)
					try {
						rs7.close();
						rs7 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (rs8 != null)
					try {
						rs8.close();
						rs8 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
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
		response = j.toString(4);
		return response;
	}

	public String updateDOB(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;

		String query = "";
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		String dob = (obj.has("dob")) ? obj.getString("dob") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
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

				query = "update user_master set dob = ? where payroll_no = ?";

				stmt = con.prepareStatement(query);
				stmt.setString(1, dob);
				stmt.setString(2, userID);
				int result = stmt.executeUpdate();
				if (result > 0) {
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Not able to Update DOB");
				}
			} catch (SQLException e) {
				logg.error("Login Error in SQL Exception ==>" + e);
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "There seems some error at server side. Please try again later!!!");
			} finally {

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

	public String AddUpdateUser(String input, String sessionID, String sessUser) {
		String response = "";
		JSONObject obj = new JSONObject(input);
		String payrollNo = (obj.has("payrollNo")) ? obj.getString("payrollNo") : "";
		String userName = (obj.has("userName")) ? obj.getString("userName") : "";
		String webMail = (obj.has("webMail")) ? obj.getString("webMail") : "";
		String mobileNo = (obj.has("mobileNo")) ? obj.getString("mobileNo") : "";
		String emailID = (obj.has("emailID")) ? obj.getString("emailID") : "";
		String accessType = (obj.has("accessType")) ? obj.getString("accessType") : "";
		String dob = (obj.has("dob")) ? obj.getString("dob") : "";
		String imeiNo1 = (obj.has("imeiNo1")) ? obj.getString("imeiNo1") : "";
		String imeiNo2 = (obj.has("imeiNo2")) ? obj.getString("imeiNo2") : "";
		String imeiNo3 = (obj.has("imeiNo3")) ? obj.getString("imeiNo3") : "";
		String company = (obj.has("company")) ? obj.getString("company") : "";
		String business = (obj.has("business")) ? obj.getString("business") : "";
		String vertical = (obj.has("vertical")) ? obj.getString("vertical") : "";
		String caseType = (obj.has("caseType")) ? obj.getString("caseType") : "";
		String enteredBy = (obj.has("enteredBy")) ? obj.getString("enteredBy") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String deletedBy = (obj.has("deletedBy")) ? obj.getString("deletedBy") : "";
		String userType = (obj.has("userType")) ? obj.getString("userType") : "";
		String reason = (obj.has("reason")) ? obj.getString("reason") : "";
		String remark = (obj.has("remark")) ? obj.getString("remark") : "";

		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		Connection con = null;
		CallableStatement cstmt = null;
		JSONObject j = new JSONObject();
		JSONArray userArray = new JSONArray();
		JSONArray accessLevelArray = new JSONArray();
		JSONArray selArray = new JSONArray();
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		if (sessionID == null) {
			j.put(LawSuitTrackerConstants.status, "F");
			j.put("msg", "Your session has expired. Please logout and login again...");
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			j.put(LawSuitTrackerConstants.status, "F");
			j.put("msg", "Your session is invalid. Please try again later!");
		} else if (!(sessUser.equals(enteredBy)) && (!sessionID.equals(sessID))) {
			j.put(LawSuitTrackerConstants.status, "F");
			j.put("msg", "Your are not authorized to perform this action.");
		} else {
			try {
				con = SQLDBConnection.getDBConnection();
				cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.userManagProc
						+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)}");
				cstmt.setString(1, type);
				cstmt.setString(2, payrollNo);
				cstmt.setString(3, userName);
				cstmt.setString(4, webMail);
				cstmt.setString(5, mobileNo);
				cstmt.setString(6, emailID);
				cstmt.setString(7, userType);
				cstmt.setString(8, accessType);
				cstmt.setString(9, dob);
				cstmt.setString(10, imeiNo1);
				cstmt.setString(11, company);
				cstmt.setString(12, business);
				cstmt.setString(13, vertical);
				cstmt.setString(14, caseType);
				cstmt.setString(15, enteredBy);
				cstmt.setString(16, imeiNo2);
				cstmt.setString(17, imeiNo3);
				cstmt.setString(18, deletedBy);
				cstmt.setString(19, reason);
				cstmt.setString(20, remark);
				cstmt.execute();
				rs = cstmt.getResultSet();
				if ("add".equalsIgnoreCase(type) || "edit".equalsIgnoreCase(type) || "disable".equalsIgnoreCase(type)) {
					if (rs.next()) {

						j.put("count", rs.getInt(1));
						j.put(LawSuitTrackerConstants.status, "T");
						j.put("msg", "Success");

					} else {
						j.put(LawSuitTrackerConstants.status, "F");
						j.put("msg", "Unable to update user...Pls try later");
					}
				} else if ("view".equalsIgnoreCase(type)) {

					while (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs.getInt(1));
						jobj.put("userID", rs.getString(2));
						jobj.put("userName", rs.getString(3));
						jobj.put("webMailID", rs.getString(4));
						jobj.put("mobileNo", rs.getString(5));
						jobj.put("emailID", rs.getString(6));
						jobj.put("userType", rs.getString(7));
						jobj.put("accessType", rs.getString(8));
						jobj.put("dob", rs.getString(9));
						jobj.put("imeiNo1", rs.getString(10));
						jobj.put("imeiNo2", rs.getString(11));
						jobj.put("imeiNo3", rs.getString(12));

						userArray.put(jobj);

					}
					j.put("userArrayMaster", userArray);
					j.put(LawSuitTrackerConstants.status, "T");
					j.put("msg", "Success");
				} else if ("inactiveUsers".equalsIgnoreCase(type)) {

					while (rs.next()) {
						JSONObject jobj = new JSONObject();

						jobj.put("userID", rs.getString(1));
						jobj.put("userName", rs.getString(2));
						jobj.put("reason", rs.getString(3));
						jobj.put("remark", rs.getString(4));
						jobj.put("deletedBy", rs.getString(5));
						jobj.put("timestamp", rs.getString(6));

						userArray.put(jobj);

					}
					j.put("disabledUsers", userArray);
					j.put(LawSuitTrackerConstants.status, "T");
					j.put("msg", "Success");
				} else if("fetch".equalsIgnoreCase(type)) {

					while (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("userID", rs.getString(1));
						jobj.put("company", rs.getInt(2));
						jobj.put("business", rs.getInt(3));
						jobj.put("vertical", rs.getInt(4));
						jobj.put("caseID", rs.getInt(5));
						jobj.put("accessType", rs.getString(6));
						accessLevelArray.put(jobj);

					}
					cstmt.getMoreResults();
					rs2 = cstmt.getResultSet();
					while (rs2.next()) {
						selArray.put((rs2.getString(1) == null) ? "" : rs2.getString(1));
					}

					j.put("accessLevelArrayMaster", accessLevelArray);
					j.put("selMaster", selArray);

					j.put(LawSuitTrackerConstants.status, "T");
					j.put("msg", "Success");
				}

			} catch (Exception e) {
				j.put(LawSuitTrackerConstants.status, "F");
				j.put("msg", e.getMessage());
			} finally {
				if (rs != null)
					try {
						rs.close();
						rs = null;
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
				if (rs4 != null)
					try {
						rs4.close();
						rs4 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				if (rs5 != null)
					try {
						rs5.close();
						rs5 = null;
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
		response = j.toString(4);
		return response;

	}

	public String updatePIN(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;

		String query = "";
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		String pin = (obj.has("pin")) ? obj.getString("pin") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		System.out.println(sessID);
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

				query = "update user_master set pin = ? where payroll_no = ?";

				stmt = con.prepareStatement(query);
				stmt.setInt(1, Integer.valueOf(pin));
				stmt.setString(2, userID);
				int result = stmt.executeUpdate();
				if (result > 0) {
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Not able to Update PIN");
				}
			} catch (SQLException e) {
				logg.error("Login Error in SQL Exception ==>" + e);
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "There seems some error at server side. Please try again later!!!");
			} finally {

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

	public String getAppVersion() {
		String response = "";
		Connection con = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;

		JSONObject j = new JSONObject();
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "SELECT app_version FROM app_version where status = 1";
			stmt1 = con.createStatement();
			rs1 = stmt1.executeQuery(query);
			while (rs1.next()) {
				j.put("currVersion", rs1.getString(1));
				j.put("msg", "Success");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			j.put("msg", "Failed");
		}

		finally {
			if (rs1 != null)
				try {
					rs1.close();
				} catch (Exception e) {
				}
			if (stmt1 != null)
				try {
					stmt1.close();
				} catch (Exception e) {
				}
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
				}
		}
		response = j.toString(4);
		return response;

	}

	public String getDeviceRequest(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		CallableStatement cstmt = null;
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		JSONArray userArray = new JSONArray();
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String uID = (obj.has("uID")) ? obj.getString("uID") : "";
		String name = (obj.has("name")) ? obj.getString("name") : "";
		String company = (obj.has("company")) ? obj.getString("company") : "";
		String emailID = (obj.has("emailID")) ? obj.getString("emailID") : "";
		String mobileNo = (obj.has("mobileNo")) ? obj.getString("mobileNo") : "";
		int deviceNo = (obj.has("deviceNo")) ? obj.getInt("deviceNo") : 0;
		int id = (obj.has("id")) ? obj.getInt("id") : 0;
		String deviceUID = (obj.has("deviceUID")) ? obj.getString("deviceUID") : "";
		String deviceType = (obj.has("deviceType")) ? obj.getString("deviceType") : "";
		String deviceManufact = (obj.has("deviceManufact")) ? obj.getString("deviceManufact") : "";
		String approveUserID = (obj.has("approveUserID")) ? obj.getString("approveUserID") : "";
		String deviceVersion = (obj.has("deviceVersion")) ? obj.getString("deviceVersion") : "";
		if (sessionID == null) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session has expired. Please logout and login again...");
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session is invalid. Please try again later!");
		} else if (!(sessUser.equals(uID)) && (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your are not authorized to perform this action.");
		} else {
			try {
				con = SQLDBConnection.getDBConnection();
				cstmt = con
						.prepareCall("{call " + LawSuitTrackerConstants.deviceReqProc + "(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, type);
				cstmt.setString(2, userID);
				cstmt.setString(3, name);
				cstmt.setString(4, company);
				cstmt.setString(5, emailID);
				cstmt.setString(6, mobileNo);
				cstmt.setInt(7, deviceNo);
				cstmt.setString(8, deviceUID);
				cstmt.setString(9, deviceType);
				cstmt.setString(10, deviceManufact);
				cstmt.setString(11, approveUserID);
				cstmt.setInt(12, id);
				cstmt.setString(13, deviceVersion);

				cstmt.execute();
				rs = cstmt.getResultSet();

				if ("register".equalsIgnoreCase(type) || "approve".equalsIgnoreCase(type)
						|| "reject".equalsIgnoreCase(type) || "delete".equalsIgnoreCase(type)) {
					if (rs.next()) {

						outJson.put("count", rs.getInt(1));
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

						if ((rs.getInt(1) == 1) && ("register".equalsIgnoreCase(type))) {
							String sub = "LMMS App - Device Registration Request for " + name;
							String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear Admin, </p>	<p> A new device registration request is pending with you. Kindly take the appropriate action. PFB - the details <p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
									+ "<tr><td>Name</td><td>" + name + "</td></tr><tr><td>Company</td><td>" + company
									+ "</td></tr><tr><td>Email</td><td>" + emailID
									+ "</td></tr><tr><td>Payroll No</td><td>" + userID
									+ "</td></tr><tr><td>Mobile No</td><td>" + mobileNo + "</td></tr>"
									+ "<tr><td>Device ID</td><td>" + deviceUID
									+ "</td></tr><tr><td>Device Type</td><td>" + deviceType + " " + deviceVersion
									+ "</td></tr>" + "<tr><td>Device Manufacturer</td><td>" + deviceManufact
									+ "</td></tr>"
									+ "</table></p><p>Please click <a href= \"https://mportal.reliancepower.co.in/LMMS/\">here</a> to approve the device</p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
							sm.sendAMail(LawSuitTrackerConstants.adminMail, sub, template, "",
									LawSuitTrackerConstants.devMail);
						} else if ((rs.getInt(1) == 1) && ("approve".equalsIgnoreCase(type))) {
							String sub = "LMMS App - Device Registration Approval";
							String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear <b>"
									+ name
									+ "</b>, </p>	<p> Your device registration request for LMMS App has been approved. </p> <p> Now You can login to LMMS mobile App successfully. </p><table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
									+ "<tr><td>Name</td><td>" + name + "</td></tr><tr><td>Company</td><td>" + company
									+ "</td></tr><tr><td>Email</td><td>" + emailID
									+ "</td></tr><tr><td>Payroll No</td><td>" + userID
									+ "</td></tr><tr><td>Mobile No</td><td>" + mobileNo + "</td></tr>"
									+ "<tr><td>Device ID</td><td>" + deviceUID
									+ "</td></tr><tr><td>Device Type</td><td>" + deviceType + " " + deviceVersion
									+ "</td></tr>" + "<tr><td>Device Manufacturer</td><td>" + deviceManufact
									+ "</td></tr>"
									+ "</table><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
							sm.sendAMail(emailID, sub, template, name, LawSuitTrackerConstants.adminMail);
						} else if ((rs.getInt(1) == 1) && ("reject".equalsIgnoreCase(type))) {
							String sub = "LMMS App - Device Registration Approval";
							String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear <b>"
									+ name
									+ "</b>, </p>	<p> Your device request for LMMS App is <b>NOT </b> approved. </p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
									+ "<tr><td>Name</td><td>" + name + "</td></tr><tr><td>Company</td><td>" + company
									+ "</td></tr><tr><td>Email</td><td>" + emailID
									+ "</td></tr><tr><td>Payroll No</td><td>" + userID
									+ "</td></tr><tr><td>Mobile No</td><td>" + mobileNo + "</td></tr>"
									+ "<tr><td>Device ID</td><td>" + deviceUID
									+ "</td></tr><tr><td>Device Type</td><td>" + deviceType + " " + deviceVersion
									+ "</td></tr>" + "<tr><td>Device Manufacturer</td><td>" + deviceManufact
									+ "</td></tr></table><p>Please contact Rpower IT team for further queries. </p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
							sm.sendAMail(emailID, sub, template, name, LawSuitTrackerConstants.adminMail);
						} else if ((rs.getInt(1) == 1) && ("delete".equalsIgnoreCase(type))) {
							String sub = "LMMS App - Device Deleted from registered device list. ";
							String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear "
									+ rs.getString(2) + ", "
									+ "</p>	<p> Your device with the following details has been deleted </p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"

									+ "<tr><td>Device ID</td><td>" + deviceUID
									+ "</td></tr><tr><td>Device Details</td><td>" + name
									+ "</td></tr></table><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
							sm.sendAMail(rs.getString(3), sub, template, name, LawSuitTrackerConstants.adminMail);
						}

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Not able to register your device... Please contact RPower IT admin");
					}
				} else if ("fetch".equalsIgnoreCase(type)) {

					while (rs.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs.getInt(1));
						jobj.put("userID", rs.getString(2));
						jobj.put("name", rs.getString(3));
						jobj.put("company", rs.getString(4));
						jobj.put("emailID", rs.getString(5));
						jobj.put("mobileNo", rs.getString(6));
						jobj.put("deviceNo", rs.getInt(7));
						jobj.put("deviceUID", rs.getString(8));
						jobj.put("deviceType", rs.getString(9));
						jobj.put("deviceManufact", rs.getString(10));
						jobj.put("requestDate", rs.getString(11));
						jobj.put("status", rs.getBoolean(12));
						jobj.put("deviceVersion", rs.getString(13));
						userArray.put(jobj);

					}
					outJson.put("userArrayMaster", userArray);
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
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
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				if (cstmt != null)
					try {
						cstmt.close();
						cstmt = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - cstmt ==>" + e.getMessage());
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
		// System.out.println();
		return response;

	}
}
