package com.lawsuittracker.modal;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.AesUtil;
import com.lawsuittracker.util.LawSuitTrackerConstants;

public class LoginModal implements Serializable {
	public static final Logger logger = Logger.getLogger(LoginModal.class);
	private static final long serialVersionUID = 1L;

	public String validateUser(String userDetails, String ip, String sessionID, HttpServletRequest request) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		String ldapSecServer = "";
		String ldapPrimServer = "";
		JSONObject outJson = new JSONObject();
		CallableStatement cstmt = null;
		ResultSet rs1 = null;

		JSONObject obj = new JSONObject(userDetails);
		String webMailID = (obj.has("userID")) ? obj.getString("userID") : "";
		String password1 = (obj.has("password")) ? obj.getString("password") : "";
		String userType = (obj.has("userType")) ? obj.getString("userType") : "";
		String deviceType = (obj.has("deviceType")) ? obj.getString("deviceType") : "";
		String dob = (obj.has("dob")) ? obj.getString("dob") : "";
		String password = "";
		String decryptedPassword = new String(java.util.Base64.getDecoder().decode(password1));
		// Decrypt the password
		AesUtil aesUtil = new AesUtil(128, 1000);
		if (decryptedPassword != null && decryptedPassword.split("::").length == 3) {
			password = aesUtil.decrypt(decryptedPassword.split("::")[1], decryptedPassword.split("::")[0],
					"1234567891234567", decryptedPassword.split("::")[2]);

		}
		if ("other".equalsIgnoreCase(userType)) {
			String id = webMailID;
			response = checkUser(id, password, ip, deviceType, dob, sessionID);

		} else {
			int index = webMailID.indexOf('@');
			String webmailID = "";
			if (webMailID.contains("@")) {

				if (index != -1) {
					webmailID = webMailID.substring(0, index);
					webmailID = webmailID.replaceAll("\\.", " ");
				}

			} else {
				webmailID = webMailID;

			}
			// System.out.println("Pass:::"+password);
			ldapPrimServer = checkLDAPConn(LawSuitTrackerConstants.ldapPrimURL, webmailID, password);
			if (("F".equalsIgnoreCase(ldapPrimServer)) || ("E".equalsIgnoreCase(ldapPrimServer)))
				ldapSecServer = checkLDAPConn(LawSuitTrackerConstants.ldapSecURL, webmailID, password);

			if ("T".equalsIgnoreCase(ldapPrimServer) || "T".equalsIgnoreCase(ldapSecServer)) {
				try {

					con = SQLDBConnection.getDBConnection();
					cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.loginCheckProc + "(?, ?, ?, ?, ?)}");
					cstmt.setString(1, webmailID);
					cstmt.setString(2, dob);
					cstmt.setString(3, sessionID);
					cstmt.setString(4, ip);
					cstmt.setString(5, deviceType);
					cstmt.execute();
					rs = cstmt.getResultSet();

					if (rs.next()) {
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");
						outJson.put("userName", rs.getString(1));
						outJson.put("payrollNo", rs.getString(2));
						outJson.put("mobileNo", rs.getString(3));
						outJson.put("webMailID", rs.getString(4));
						outJson.put("userType", rs.getString(5));
						outJson.put("accessCtrl", rs.getString(6));
						outJson.put("forum", rs.getString(7));
						outJson.put("caseType", rs.getString(8));
						outJson.put("company", rs.getString(9));
						outJson.put("caseCategory", rs.getString(10));
						outJson.put("emailID", rs.getString(11));
						outJson.put("dob", rs.getString(12));
						outJson.put("lastJobRun", rs.getString(13));
						outJson.put("imeiNo1", rs.getString(14));
						outJson.put("imeiNo2", rs.getString(15));
						outJson.put("imeiNo3", rs.getString(16));
						outJson.put("currentVersion", rs.getString(17));
						outJson.put("maxDevices", rs.getInt(19));
						outJson.put("maxUsageID", rs.getInt(20));
						outJson.put("deviceText1", rs.getString(21));
						outJson.put("deviceText2", rs.getString(22));
						outJson.put("deviceText3", rs.getString(23));
						outJson.put("caseDocView", rs.getString(24));
						outJson.put("noticeDocView", rs.getString(25));

						HttpSession session = request.getSession(false);
						session.setAttribute("userID", rs.getString(2));
						outJson.put("sessionID", sessionID);
						outJson.put("authHeader", LawSuitTrackerConstants.authHeader);
					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg",
								"Your details are not available in application database. Please contact IT admin!!!");
					}

				} catch (SQLException e) {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", e.getMessage());
					logger.error("Login Error in SQL Exception ==>" + e.getMessage());
				}

				finally {
					if (rs != null)
						try {
							rs.close();
							rs = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - rs ==>" + e.getMessage());
						}
					if (rs1 != null)
						try {
							rs1.close();
							rs1 = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - rs1 ==>" + e.getMessage());
						}
					if (cstmt != null)
						try {
							cstmt.close();
							cstmt = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - cstmt ==>" + e.getMessage());
						}
					if (con != null)
						try {
							con.close();
							con = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - con ==>" + e.getMessage());
						}
				}
			} else if (("F".equalsIgnoreCase(ldapPrimServer)) && ("F".equalsIgnoreCase(ldapSecServer))
					|| ("E".equalsIgnoreCase(ldapPrimServer)) && ("F".equalsIgnoreCase(ldapSecServer))
					|| ("F".equalsIgnoreCase(ldapPrimServer)) && ("E".equalsIgnoreCase(ldapSecServer))) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "Your web mail credentials are wrong!!!");
			} else if (("E".equalsIgnoreCase(ldapPrimServer)) && ("E".equalsIgnoreCase(ldapSecServer))) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "There is some issue with LDAP connectivity...Please try later..!!!");
			}
			response = outJson.toString(4);

		}
		return response;
	}

	public String validateUserMob(String userDetails, String ip, String sessionID, HttpServletRequest request, String tc) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		String ldapSecServer = "E";
		String ldapPrimServer = "E";
		JSONObject outJson = new JSONObject();
		CallableStatement cstmt = null;
		ResultSet rs1 = null;
		JSONArray stateArray = new JSONArray();
		JSONObject obj = new JSONObject(userDetails);
		String webMailID = (obj.has("userID")) ? obj.getString("userID") : "";
		String password = (obj.has("password")) ? obj.getString("password") : "";
		String userType = (obj.has("userType")) ? obj.getString("userType") : "";
		String deviceType = (obj.has("deviceType")) ? obj.getString("deviceType") : "";
		String deviceNo = (obj.has("deviceNo")) ? obj.getString("deviceNo") : "";
		String version = (obj.has("version")) ? obj.getString("version") : "";
		String dob = (obj.has("dob")) ? obj.getString("dob") : "";
		if ("other".equalsIgnoreCase(userType)) {
			String id = webMailID;
			response = checkUser(id, password, ip, deviceType, dob, sessionID);

		} else {
			int index = webMailID.indexOf('@');
			String webmailID = "";
			if (webMailID.contains("@")) {

				if (index != -1) {
					webmailID = webMailID.substring(0, index);
					webmailID = webmailID.replaceAll("\\.", " ");
				}

			} else {
				webmailID = webMailID;

			}

			ldapPrimServer = checkLDAPConn(LawSuitTrackerConstants.ldapPrimURL, webmailID, password);
			if ("F".equalsIgnoreCase(ldapPrimServer) || "E".equalsIgnoreCase(ldapPrimServer))
				ldapSecServer = checkLDAPConn(LawSuitTrackerConstants.ldapSecURL, webmailID, password);

			if ("T".equalsIgnoreCase(ldapPrimServer) || "T".equalsIgnoreCase(ldapSecServer)) {
				try {

					con = SQLDBConnection.getDBConnection();
					cstmt = con.prepareCall(
							"{call " + LawSuitTrackerConstants.loginCheckProcForMob + "(?, ?, ?, ?, ?, ?, ?)}");
					cstmt.setString(1, webmailID);
					cstmt.setString(2, dob);
					cstmt.setString(3, sessionID);
					cstmt.setString(4, ip);
					cstmt.setString(5, deviceType);
					cstmt.setString(6, deviceNo);
					cstmt.setString(7, version);
					cstmt.execute();
					rs = cstmt.getResultSet();

					if (rs.next()) {
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");
						outJson.put("userName", rs.getString(1));
						outJson.put("payrollNo", rs.getString(2));
						outJson.put("mobileNo", rs.getString(3));
						outJson.put("webMailID", rs.getString(4));
						outJson.put("userType", rs.getString(5));
						outJson.put("accessCtrl", rs.getString(6));
						outJson.put("forum", rs.getString(7));
						outJson.put("caseType", rs.getString(8));
						outJson.put("company", rs.getString(9));
						outJson.put("caseCategory", rs.getString(10));
						outJson.put("emailID", rs.getString(11));
						outJson.put("dob", rs.getString(12));
						outJson.put("lastLogin", rs.getString(13));
						outJson.put("imeiNo1", rs.getString(14));
						outJson.put("imeiNo2", rs.getString(15));
						outJson.put("imeiNo3", rs.getString(16));
						outJson.put("currentVersion", rs.getString(17));
						outJson.put("pin", rs.getInt(18));
						outJson.put("maxDevices", rs.getInt(19));
						outJson.put("deviceStatus", rs.getBoolean(20));
						outJson.put("caseDocView", rs.getString(21));
						outJson.put("noticeDocView", rs.getString(22));
						outJson.put("deviceText1", rs.getString(23));
						outJson.put("deviceText2", rs.getString(24));
						outJson.put("deviceText3", rs.getString(25));
						outJson.put("sessionID", sessionID);
						outJson.put("tc", tc);
						HttpSession session = request.getSession(false);
						session.setAttribute("userID", rs.getString(2));
					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg",
								"Your details are not available in application database. Please contact IT admin!!!");
					}

					cstmt.getMoreResults();
					rs1 = cstmt.getResultSet();
					while (rs1.next()) {
						JSONObject ob = new JSONObject();
						ob.put("id", rs1.getInt(1));
						ob.put("name", rs1.getString(2));

						stateArray.put(ob);
					}
					outJson.put("stateArray", stateArray);

				} catch (SQLException e) {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", e.getMessage());
					logger.error("Login Error in SQL Exception ==>" + e.getMessage());
				}

				finally {
					if (rs != null)
						try {
							rs.close();
							rs = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - rs ==>" + e.getMessage());
						}
					if (rs1 != null)
						try {
							rs1.close();
							rs1 = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - rs1 ==>" + e.getMessage());
						}
					if (cstmt != null)
						try {
							cstmt.close();
							cstmt = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - cstmt ==>" + e.getMessage());
						}
					if (con != null)
						try {
							con.close();
							con = null;
						} catch (Exception e) {
							logger.error("Login Error in Finally - con ==>" + e.getMessage());
						}
				}
			} else if (("F".equalsIgnoreCase(ldapPrimServer)) && ("F".equalsIgnoreCase(ldapSecServer))
					|| ("E".equalsIgnoreCase(ldapPrimServer)) && ("F".equalsIgnoreCase(ldapSecServer))
					|| ("F".equalsIgnoreCase(ldapPrimServer)) && ("E".equalsIgnoreCase(ldapSecServer))) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "Your web mail credentials are wrong!!!");
			} else if (("E".equalsIgnoreCase(ldapPrimServer)) && ("E".equalsIgnoreCase(ldapSecServer))) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "There is some issue with LDAP connectivity...Please try later..!!!");
			}
			response = outJson.toString(4);

		}
		return response;
	}

	public String checkLDAPConn(String ldapURL, String webmailID, String password) {

		Hashtable<String, String> env = new Hashtable<String, String>();
		String b = "";
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapURL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, webmailID);
		env.put(Context.SECURITY_CREDENTIALS, password);
		try {
			if ("".equals(password))
				b = "F";
			else {
				DirContext ctx = new InitialDirContext(env);
				b = "T";
				// System.out.println(ctx.getEnvironment());
				ctx.close();
			}

		} catch (AuthenticationNotSupportedException ex) {
			b = "E";
			System.out.println("The authentication is not supported by the server" + ex.getMessage());
		} catch (AuthenticationException authEx) {
			System.out.println("Authentication failed!" + authEx.getMessage());
			b = "F";
		} catch (NamingException e) {
			System.out.println("Authentication failed!" + e.getMessage());
			b = "E";
		}
		return b;
	}

	public String checkUser(String id, String password, String ip, String deviceType, String dob, String sessionID) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "SELECT user_name, payroll_no, mobile_no, webmail_id, user_type, access_type, forum, user_plant  from dbo.user_master where user_id = ? and password = ? and dob = ? and status = 1";
			stmt = con.prepareStatement(query);
			stmt.setString(1, id);
			stmt.setString(2, password);
			stmt.setString(3, dob);
			rs = stmt.executeQuery();
			if (rs.next()) {
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
				outJson.put("userName", rs.getString(1));
				outJson.put("payrollNo", rs.getString(2));
				outJson.put("mobileNo", rs.getString(3));
				outJson.put("webMailID", rs.getString(4));
				outJson.put("userType", rs.getString(5));
				outJson.put("accessCtrl", rs.getString(6));
				outJson.put("forum", rs.getString(7));
				outJson.put("userPlant", rs.getString(8));

				boolean status = insertIntoUsageLog(rs.getString(1), ip, sessionID, deviceType, rs.getString(2));
				outJson.put("logInsert", status);

				outJson.put("sessionID", sessionID);

			} else {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg",
						"Your details are not available in application database. Please contact IT admin!!!");
			}

		} catch (SQLException e) {
			logger.error("Login Error in SQL Exception ==>" + e.getMessage());
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "There seems some error at server side. Please try again later!!!");
		}

		finally {
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

	public boolean insertIntoUsageLog(String userName, String ip, String sessionID, String deviceType,
			String payrollNo) {
		boolean response = false;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "insert into usage_log(user_name,ip, device_type, session_id, payroll_no) values(?,?,?,?,?)";
			stmt = con.prepareStatement(query);
			stmt.setString(1, userName);
			stmt.setString(2, ip);
			stmt.setString(3, deviceType);
			stmt.setString(4, sessionID);
			stmt.setString(5, payrollNo);
			int result = stmt.executeUpdate();
			if (result > 0) {
				response = true;

			} else
				response = false;

		} catch (SQLException e) {
			response = false;
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
