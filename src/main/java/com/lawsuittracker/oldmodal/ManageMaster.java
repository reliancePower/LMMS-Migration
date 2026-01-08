package com.lawsuittracker.oldmodal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.lawsuittracker.util.SendMail;

public class ManageMaster {
	public static final Logger logg = Logger.getLogger(ManageMaster.class);
	SendMail sm = new SendMail();

	public String fetchRefNo(String input) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);

		int id = (obj.has("id")) ? obj.getInt("id") : 0;
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "select alias + RIGHT('000000' + convert(varchar, code+1),4) from vertical_master where id = ?";
			stmt = con.prepareStatement(query);
			stmt.setInt(1, id);

			rs = stmt.executeQuery();
			if (rs.next()) {
				outJson.put("refNo", rs.getString(1));
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
			} else {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the case id ");
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
		response = outJson.toString(4);
		return response;
	}

	// This can be deleted in future
	public String fetchCompanyMaster(String input, String sessionID, String sessUser) {
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs3 = null;
		ResultSet rs2 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;
		ResultSet rs8 = null;
		CallableStatement cstmt = null;
		JSONObject j = new JSONObject();
		JSONArray companyArray = new JSONArray();
		JSONArray forumCategoryArray = new JSONArray();
		JSONArray benchArray = new JSONArray();
		JSONArray aorArray = new JSONArray();
		JSONArray counselArray = new JSONArray();
		JSONArray statusArray = new JSONArray();
		JSONArray categoryArray = new JSONArray();
		JSONArray caseTypeArray = new JSONArray();
		JSONArray stateArray = new JSONArray();
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		// if("".equals(sessID) || (!sessionID.equals(sessID))) {
		// j.put(LawSuitTrackerConstants.status, "F");
		// j.put("msg", "Your session is invalid. Please try again later!");
		// }
		// else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		// j.put(LawSuitTrackerConstants.status, "F");
		// j.put("msg", "Your are not authorized to perform this action.");
		// }
		// else
		// {
		try {
			con = SQLDBConnection.getDBConnection();
			cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.fechInitMaster + "(?)}");
			cstmt.setString(1, userID);
			cstmt.execute();
			rs = cstmt.getResultSet();

			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("name", rs.getString(2));
				companyArray.put(jobj);

			}
			j.put("companyMaster", companyArray);
			cstmt.getMoreResults();

			rs1 = cstmt.getResultSet();
			while (rs1.next()) {
				JSONObject jobj = new JSONObject();

				jobj.put("name", rs1.getString(1));

				forumCategoryArray.put(jobj);
			}
			j.put("forumCategoryMaster", forumCategoryArray);

			cstmt.getMoreResults();
			rs2 = cstmt.getResultSet();
			while (rs2.next()) {
				JSONObject jobj = new JSONObject();
				// jobj.put("id", rs2.getInt(1));
				jobj.put("name", rs2.getString(1));

				benchArray.put(jobj);
			}
			j.put("benchMaster", benchArray);

			cstmt.getMoreResults();

			rs3 = cstmt.getResultSet();
			while (rs3.next()) {
				JSONObject jobj = new JSONObject();
				// jobj.put("id", rs3.getInt(1));
				jobj.put("name", rs3.getString(1));

				aorArray.put(jobj);
			}
			j.put("aorCompanyMaster", aorArray);

			cstmt.getMoreResults();
			rs4 = cstmt.getResultSet();
			while (rs4.next()) {
				JSONObject jobj = new JSONObject();
				// jobj.put("id", rs4.getInt(1));
				jobj.put("name", rs4.getString(1));

				counselArray.put(jobj);
			}
			j.put("counselCompanyMaster", counselArray);

			cstmt.getMoreResults();
			rs5 = cstmt.getResultSet();
			while (rs5.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs5.getInt(1));
				jobj.put("name", rs5.getString(2));

				statusArray.put(jobj);
			}
			j.put("statusMaster", statusArray);

			cstmt.getMoreResults();
			rs6 = cstmt.getResultSet();
			while (rs6.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs6.getInt(1));
				jobj.put("name", rs6.getString(2));

				categoryArray.put(jobj);
			}
			j.put("categoryMaster", categoryArray);

			cstmt.getMoreResults();
			rs7 = cstmt.getResultSet();
			while (rs7.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs7.getInt(1));
				jobj.put("name", rs7.getString(2));
				caseTypeArray.put(jobj);
			}
			j.put("caseTypeMaster", caseTypeArray);

			cstmt.getMoreResults();
			rs8 = cstmt.getResultSet();
			while (rs8.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs8.getInt(1));
				jobj.put("name", rs8.getString(2));

				stateArray.put(jobj);
			}
			j.put("stateArray", stateArray);

			j.put(LawSuitTrackerConstants.status, "T");
			// j.put("msg", "Success");

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
			if (rs1 != null)
				try {
					rs1.close();
					rs1 = null;
				} catch (Exception e) {
					logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (rs2 != null)
				try {
					rs2.close();
					rs2 = null;
				} catch (Exception e) {
					logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
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
		// }
		response = j.toString(4);
		return response;

	}

	// This can be deleted in future
	public String fetchBusinessMaster(String input, String sessionID, String sessUser) {
		String response = "";
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		int companyID = (obj.has("companyID")) ? obj.getInt("companyID") : 0;
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		CallableStatement cstmt = null;
		JSONObject j = new JSONObject();
		JSONArray businessArray = new JSONArray();
		JSONArray businessRepArray = new JSONArray();
		JSONArray legalRepArray = new JSONArray();
		// if("".equals(sessID) || (!sessionID.equals(sessID))) {
		// j.put(LawSuitTrackerConstants.status, "F");
		// j.put("msg", "Your session is invalid. Please try again later!");
		// }
		// else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		// j.put(LawSuitTrackerConstants.status, "F");
		// j.put("msg", "Your are not authorized to perform this action.");
		// }
		// else
		// {
		try {
			con = SQLDBConnection.getDBConnection();
			cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.fetchCompanyMaster + "(?, ?)}");
			cstmt.setString(1, String.valueOf(companyID));
			cstmt.setString(2, userID);
			cstmt.execute();
			rs = cstmt.getResultSet();

			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("name", rs.getString(2));
				businessArray.put(jobj);

			}
			j.put("businessMaster", businessArray);
			cstmt.getMoreResults();

			rs2 = cstmt.getResultSet();
			while (rs2.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs2.getInt(1));
				jobj.put("name", rs2.getString(2));

				businessRepArray.put(jobj);
			}
			j.put("businessRepMaster", businessRepArray);

			cstmt.getMoreResults();

			rs3 = cstmt.getResultSet();
			while (rs3.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs3.getInt(1));
				jobj.put("name", rs3.getString(2));

				legalRepArray.put(jobj);
			}
			j.put("legalRepMaster", legalRepArray);

			j.put(LawSuitTrackerConstants.status, "T");
			j.put("msg", "Success");

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
					logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (rs3 != null)
				try {
					rs3.close();
					rs3 = null;
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
		// }
		response = j.toString(4);
		return response;

	}

	public String fetchVerticalMaster(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		int businessID = (obj.has("businessID")) ? obj.getInt("businessID") : 0;
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray verticalArray = new JSONArray();
		// if("".equals(sessID) || (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your session is invalid. Please try again later!");
		// }
		// else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your are not authorized to perform this action.");
		// }
		// else
		// {
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "SELECT id, vertical_name from dbo.vw_user_vertical where user_id = ? and business_id = ?";
			stmt = con.prepareStatement(query);
			stmt.setString(1, userID);
			stmt.setInt(2, businessID);
			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jObj = new JSONObject();
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
				jObj.put("id", rs.getString(1));
				jObj.put("name", rs.getString(2));
				verticalArray.put(jObj);

			}

			outJson.put("verticalMaster", verticalArray);
		} catch (SQLException e) {
			logg.error("Login Error in SQL Exception ==>" + e.getMessage());
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "There seems some error at server side. Please try again later!!!");
		}

		finally {
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
		// }
		response = outJson.toString(4);
		return response;

	}

	public String fetchBusiVertMasterView(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject obj = new JSONObject(input);
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String id = (obj.has("id")) ? obj.getString("id") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";

		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		// if("".equals(sessID) || (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your session is invalid. Please try again later!");
		// }
		// else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your are not authorized to perform this action.");
		// }
		// else
		// {
		try {
			String query = "";
			con = SQLDBConnection.getDBConnection();
			if ("business".equalsIgnoreCase(type) && (!("".equals(id)))) {
				if ("All".equalsIgnoreCase(id)) {
					query = "SELECT distinct id, business_name from vw_user_business where user_id = '" + userID
							+ "' order by business_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);

				} else {
					query = "SELECT distinct id, business_name from vw_user_business where CAST(company_id as varchar) in ("
							+ id + ") and user_id = '" + userID + "' order by business_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);

				}
			} else if ("vertical".equalsIgnoreCase(type) && (!("".equals(id)))) {
				if ("All".equalsIgnoreCase(id)) {
					query = "SELECT distinct id, alias, vertical_name from vw_user_vertical where user_id = '" + userID
							+ "' order by vertical_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);
				} else {
					query = "SELECT distinct id, alias, vertical_name from vw_user_vertical where CAST(business_id as varchar) in ("
							+ id + ") and user_id = '" + userID + "' order by vertical_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);

				}
			}

			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jObj = new JSONObject();
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
				jObj.put("id", rs.getInt(1));
				jObj.put("name", rs.getString(2));
				if ("vertical".equalsIgnoreCase(type))
					jObj.put("fullname", rs.getString(3));
				resultArray.put(jObj);

			}
			outJson.put("resultArray", resultArray);
		} catch (SQLException e) {
			logg.error("Login Error in SQL Exception ==>" + e.getMessage());
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "There seems some error at server side. Please try again later!!!");
		}

		finally {
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
		// }
		response = outJson.toString(4);
		return response;

	}

	public String fetchBusiVertMaster(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject obj = new JSONObject(input);
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String id = (obj.has("id")) ? obj.getString("id") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		// if("".equals(sessID) || (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your session is invalid. Please try again later!");
		// }
		// else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your are not authorized to perform this action.");
		// }
		// else
		// {
		try {
			String query = "";
			con = SQLDBConnection.getDBConnection();
			if ("business".equalsIgnoreCase(type) && (!("".equals(id)))) {
				if ("All".equalsIgnoreCase(id)) {
					query = "SELECT distinct business_id, business_name from vw_comp_busi_vert_mater order by business_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);

				} else {
					query = "SELECT distinct business_id, business_name from vw_comp_busi_vert_mater where CAST(company_id as varchar) in ("
							+ id + ")  order by business_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);

				}
			} else if ("vertical".equalsIgnoreCase(type) && (!("".equals(id)))) {
				if ("All".equalsIgnoreCase(id)) {
					query = "SELECT distinct id, alias, vertical_name from vw_user_vertical order by vertical_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);
				} else {
					query = "SELECT distinct id, alias, vertical_name from vw_user_vertical where CAST(business_id as varchar) in ("
							+ id + ")  order by vertical_name";
					// System.out.println(query);
					stmt = con.prepareStatement(query);

				}
			}

			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jObj = new JSONObject();
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
				jObj.put("id", rs.getInt(1));
				jObj.put("name", rs.getString(2));
				if ("vertical".equalsIgnoreCase(type))
					jObj.put("fullname", rs.getString(3));
				resultArray.put(jObj);

			}
			outJson.put("resultArray", resultArray);
		} catch (SQLException e) {
			logg.error("Login Error in SQL Exception ==>" + e.getMessage());
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "There seems some error at server side. Please try again later!!!");
		}

		finally {
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
		// }
		response = outJson.toString(4);
		return response;

	}

	public String pvForumUpdateLog(String input) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Date sDate = null;
		Date eDate = null;
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		JSONArray resultArray = new JSONArray();
		String startDate = (obj.has("startDate")) ? obj.getString("startDate") : "";
		String endDate = (obj.has("endDate")) ? obj.getString("endDate") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";

		if (!("".equals(startDate)))
			sDate = Date.valueOf(startDate);
		if (!("".equals(endDate)))
			eDate = Date.valueOf(endDate);

		try {
			con = SQLDBConnection.getDBConnection();
			String query = "select id,Category,forum_id,Forum_Name,CONVERT(varchar, Updated_On, 100),Updated_Item,action,execution_type from pv_forum_casetype_update_log where cast(Updated_On as date) between ? and ?";
			stmt = con.prepareStatement(query);
			stmt.setDate(1, sDate);
			stmt.setDate(2, eDate);
			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject ob = new JSONObject();
				ob.put("id", rs.getInt(1));
				ob.put("category", rs.getString(2));
				ob.put("forumID", rs.getString(3));
				ob.put("forumName", rs.getString(4));
				ob.put("updatedOn", rs.getString(5));
				ob.put("updatedItem", rs.getString(6));
				ob.put("action", rs.getString(7));
				ob.put("executionType", rs.getString(8));
				resultArray.put(ob);

			}
			if (resultArray.length() > 0) {
				outJson.put("resultArray", resultArray);
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");

			} else {
				outJson.put("resultArray", resultArray);
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No details available for the case id ");

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
		response = outJson.toString(4);
		return response;
	}

	public String manageForum(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		JSONArray categoryArray = new JSONArray();
		JSONArray stateArray = new JSONArray();
		JSONArray resultArray = new JSONArray();
		CallableStatement cstmt = null;

		String type = obj.has("type") ? obj.getString("type") : "";
		String userID = obj.has("userID") ? obj.getString("userID") : "";
		String category = obj.has("category") ? obj.getString("category") : "";
		String name = obj.has("name") ? obj.getString("name") : "";
		String state = obj.has("state") ? obj.getString("state") : "";
		int id = obj.has("id") ? obj.getInt("id") : 0;
		int forumID = obj.has("forumID") ? obj.getInt("forumID") : 10000000;
		String remark = obj.has("remark") ? obj.getString("remark") : "";

		try {
			con = SQLDBConnection.getDBConnection();

			cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.manageForumProc + "(?,?,?,?,?, ?,?,?)}");
			cstmt.setString(1, type);
			cstmt.setString(2, userID);
			cstmt.setString(3, category);
			cstmt.setString(4, name);
			cstmt.setString(5, state);
			cstmt.setInt(6, id);
			cstmt.setInt(7, forumID);
			cstmt.setString(8, remark);
			cstmt.execute();
			rs = cstmt.getResultSet();
			if ("fetchMaster".equalsIgnoreCase(type)) {
				while (rs.next()) {
					categoryArray.put(rs.getString(1));
				}
				outJson.put("forumCategoryArray", categoryArray);

				cstmt.getMoreResults();
				rs1 = cstmt.getResultSet();

				while (rs1.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs1.getInt(1));
					jobj.put("name", rs1.getString(2));

					stateArray.put(jobj);
				}

				outJson.put("stateArray", stateArray);
				if (categoryArray.length() > 0) {
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "No cases available for the case id ");
				}
			} else if ("checkDB".equalsIgnoreCase(type)) {
				if (rs.next()) {
					if (rs.getInt(1) > 0) {
						cstmt.getMoreResults();
						rs1 = cstmt.getResultSet();
						while (rs1.next()) {
							JSONObject jobj = new JSONObject();
							jobj.put("id", rs1.getInt(1));
							jobj.put("category", rs1.getString(2));
							jobj.put("name", rs1.getString(3));
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
							stateArray.put(jobj);
						}
						outJson.put("forumArray", stateArray);
					} else {
						cstmt.getMoreResults();
						rs1 = cstmt.getResultSet();
						if (rs1.next()) {
							if (rs1.getInt(1) > 0) {
								outJson.put("count", rs1.getInt(1));
								outJson.put("reqID", rs1.getInt(2));
								String sub = "LMMS App - Forum Addition Request from " + rs1.getString(3);
								String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear Admin, </p>	<p> A new forum addition request is pending with you for approval. <br><br>Kindly take the appropriate action. PFB - the details <p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
										+ "<tr><td><b>Forum Category</b></td><td>" + WordUtils.capitalizeFully(category) + "</td></tr><tr><td><b>Name</b></td><td>" + name + ", " + state 
										+ "</td></tr></table></p><p>Please click <a href= \"https://mportal.reliancepower.co.in/LMMS/\">here</a> to approve the forum</p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
								System.out.println(template);
								sm.sendAMail(LawSuitTrackerConstants.adminMail, sub, template, "",
										LawSuitTrackerConstants.devMail);
								outJson.put(LawSuitTrackerConstants.status, "T");
								outJson.put("msg", "Success");

							} else {
								outJson.put(LawSuitTrackerConstants.status, "F");
								outJson.put("msg", "Failed");
							}
						} else {
							outJson.put(LawSuitTrackerConstants.status, "F");
							outJson.put("msg", "Failed");
						}
					}
				}

			} else if ("addForum".equalsIgnoreCase(type)) {
				if (rs.next()) {

					if (rs.getInt(1) > 0) {
						outJson.put("count", rs.getInt(1));
						outJson.put("forumID", rs.getInt(2));
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Failed");
					}
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}

			} else if ("editForum".equalsIgnoreCase(type)) {
				if (rs.next()) {

					if (rs.getInt(1) > 0) {
						outJson.put("count", rs.getInt(1));
						outJson.put("forumID", rs.getInt(2));
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Failed");
					}
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}

			} else if ("deleteForum".equalsIgnoreCase(type)) {
				if (rs.next()) {

					if (rs.getInt(1) > 0) {
						outJson.put("count", rs.getInt(1));
						outJson.put("forumID", rs.getInt(2));
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Failed");
					}
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}

			} else if ("fetchForum".equalsIgnoreCase(type)) {

				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					jobj.put("category", rs.getString(3));
					jobj.put("source", rs.getString(4));
					jobj.put("userID", rs.getString(5));
					jobj.put("caseExists", rs.getString(6));
					resultArray.put(jobj);
				}
				outJson.put("resultArray", resultArray);

			} else if ("fetchCaseType".equalsIgnoreCase(type)) {

				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					resultArray.put(jobj);
				}
				if (resultArray.length() == 0 || forumID >= 100000) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", 1000000);
					jobj.put("name", "Others");
					resultArray.put(jobj);
				}

				outJson.put("resultArray", resultArray);

			} else if ("sendForumReq".equalsIgnoreCase(type)) {
				if (rs.next()) {

					if (rs.getInt(1) > 0) {
						outJson.put("count", rs.getInt(1));
						outJson.put("reqID", rs.getInt(2));
						String sub = "LMMS App - Forum Addition Request from " + rs.getString(3);
						String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear Admin, </p>	<p> A new forum addition request is pending with you for approval. <br><br>Kindly take the appropriate action. PFB - the details <p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
								+ "<tr><td><b>Forum Category</b></td><td>" + WordUtils.capitalizeFully(category) + "</td></tr><tr><td><b>Name</b></td><td>" + name + ", " + state 
								+ "</td></tr></table></p><p>Please click <a href= \"https://mportal.reliancepower.co.in/LMMS/\">here</a> to approve the forum</p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
						System.out.println(template);
						sm.sendAMail(LawSuitTrackerConstants.adminMail, sub, template, "",
								LawSuitTrackerConstants.devMail);
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Failed");
					}
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}

			}else if ("approveForumReq".equalsIgnoreCase(type)) {
				if (rs.next()) {

					if (rs.getInt(1) > 0) {
						outJson.put("count", rs.getInt(1));
						outJson.put("forumID", rs.getInt(2));
						String sub = "LMMS App - Forum Addition Request - Approved";
						String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p>Dear <b>" + rs.getString(4)  + 
								"</b>, </p>	<p> A new forum addition request sent by you is approved successfully . <br><br> Now you can access forum based on Category. PFB - the details <p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
								+ "<tr><td><b>Forum Category</b></td><td>" + WordUtils.capitalizeFully(category) + "</td></tr><tr><td><b>Name</b></td><td>" + name + ", " + state 
								+ "</td></tr><tr><td><b>Remark</b></td><td>" + remark + "</td></tr>" 
								+ "</table></p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
						sm.sendAMail(rs.getString(5), sub, template, name, LawSuitTrackerConstants.adminMail);
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Failed");
					}
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}

			}else if ("rejectForumReq".equalsIgnoreCase(type)) {
				if (rs.next()) {

					if (rs.getInt(1) > 0) {
						outJson.put("count", rs.getInt(1));
						
						String sub = "LMMS App - Forum Addition Request - Rejected";
						String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear <b>" + rs.getString(2) + 
								"</b>,</p>	<p> A new forum addition request sent by you is not approved. Refer the adminstrator comment below: <p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
								+ "<tr><td><b>Forum Category</b></td><td>" + WordUtils.capitalizeFully(category) + "</td></tr><tr><td><b>Name</b></td><td>" + name + ", " + state 
								+ "</td></tr><tr><td><b>Remark</b></td><td>" + remark + "</td></tr>" 
								+ "</table></p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
						sm.sendAMail(rs.getString(3), sub, template, name, LawSuitTrackerConstants.adminMail);
						
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");

					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Failed");
					}
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Failed");
				}

			}else if ("fetchForumReq".equalsIgnoreCase(type)) {

				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					jobj.put("category", rs.getString(3));				
					jobj.put("userID", rs.getString(4));
					jobj.put("timestamp", rs.getString(5));
					resultArray.put(jobj);
				}
				outJson.put("resultArray", resultArray);

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
		response = outJson.toString(4);
		return response;
	}

	public String addToMaster(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;

		String query = "";
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		String name = (obj.has("name")) ? obj.getString("name") : "";
		name = name.replaceAll("(?i)<(?!(/?(li)))[^>]*>", "");
		name = name.replaceAll("[^\\w.,& ]+", "");
		int company = (obj.has("company")) ? obj.getInt("company") : 0;
		// if("".equals(sessID) || (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your session is invalid. Please try again later!");
		// }
		// else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your are not authorized to perform this action.");
		// }
		// else {
		try {
			con = SQLDBConnection.getDBConnection();
			if ("bench".equals(type))
				query = "insert into bench_master(bench_name, user_id) values(?, ?)";
			else if ("aorCompany".equals(type))
				query = "insert into aor_of_company_master(aor_name, user_id) values(?, ?)";
			else if ("councelCompany".equals(type))
				query = "insert into counsel_of_company_master(counsel_name, user_id) values(?, ?)";
			else if ("businessRep".equals(type))
				query = "insert into business_rep_master(business_rep_name, user_id, company) values(?, ?, ?)";
			else if ("legalRep".equals(type))
				query = "insert into legal_rep_master(legal_rep_name, user_id, company) values(?, ?, ?)";
			else if ("forum".equals(type))
				query = "insert into forum_master(forum_name, user_id) values(?, ?)";

			stmt = con.prepareStatement(query);
			stmt.setString(1, name);
			stmt.setString(2, userID);
			if (("legalRep".equals(type)) || ("businessRep".equals(type)))
				stmt.setInt(3, company);
			int result = stmt.executeUpdate();
			if (result > 0) {
				outJson.put(LawSuitTrackerConstants.status, "T");
				outJson.put("msg", "Success");
			} else {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the case id ");
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
		// }
		response = outJson.toString(4);
		return response;

	}

	public String fetchAllMasters(String input, String sessionID, String sessUser) {
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs3 = null;
		ResultSet rs2 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;
		ResultSet rs8 = null;
		CallableStatement cstmt = null;
		JSONObject j = new JSONObject();
		JSONArray companyArray = new JSONArray();
		JSONArray forumCategoryArray = new JSONArray();
		JSONArray benchArray = new JSONArray();
		JSONArray verticalArray = new JSONArray();
		JSONArray aorArray = new JSONArray();
		JSONArray counselArray = new JSONArray();
		JSONArray statusArray = new JSONArray();
		JSONArray categoryArray = new JSONArray();
		JSONArray caseTypeArray = new JSONArray();
		JSONArray stateArray = new JSONArray();
		JSONArray businessArray = new JSONArray();
		JSONArray businessRepArray = new JSONArray();
		JSONArray legalRepArray = new JSONArray();
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String compID = (obj.has("compID")) ? obj.getString("compID") : "";
		String forumCategory = (obj.has("forumCategory")) ? obj.getString("forumCategory") : "";

		try {
			con = SQLDBConnection.getDBConnection();
			cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.fechMasterForAllProc + "(?,?,?,?)}");
			cstmt.setString(1, type);
			cstmt.setString(2, userID);
			cstmt.setString(3, compID);
			cstmt.setString(4, forumCategory);
			cstmt.execute();
			rs = cstmt.getResultSet();
			if ("fetchMasterForAdd".equalsIgnoreCase(type)) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					companyArray.put(jobj);

				}
				j.put("companyMaster", companyArray);
				cstmt.getMoreResults();

				rs1 = cstmt.getResultSet();
				while (rs1.next()) {
					JSONObject jobj = new JSONObject();

					jobj.put("name", rs1.getString(1));

					forumCategoryArray.put(jobj);
				}
				j.put("forumCategoryMaster", forumCategoryArray);

				cstmt.getMoreResults();
				rs2 = cstmt.getResultSet();
				while (rs2.next()) {
					JSONObject jobj = new JSONObject();
					// jobj.put("id", rs2.getInt(1));
					jobj.put("name", rs2.getString(1));

					benchArray.put(jobj);
				}
				j.put("benchMaster", benchArray);

				cstmt.getMoreResults();

				rs3 = cstmt.getResultSet();
				while (rs3.next()) {
					JSONObject jobj = new JSONObject();
					// jobj.put("id", rs3.getInt(1));
					jobj.put("name", rs3.getString(1));

					aorArray.put(jobj);
				}
				j.put("aorCompanyMaster", aorArray);

				cstmt.getMoreResults();
				rs4 = cstmt.getResultSet();
				while (rs4.next()) {
					JSONObject jobj = new JSONObject();
					// jobj.put("id", rs4.getInt(1));
					jobj.put("name", rs4.getString(1));

					counselArray.put(jobj);
				}
				j.put("counselCompanyMaster", counselArray);

				cstmt.getMoreResults();
				rs5 = cstmt.getResultSet();
				while (rs5.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs5.getInt(1));
					jobj.put("name", rs5.getString(2));

					statusArray.put(jobj);
				}
				j.put("statusMaster", statusArray);

				cstmt.getMoreResults();
				rs6 = cstmt.getResultSet();
				while (rs6.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs6.getInt(1));
					jobj.put("name", rs6.getString(2));

					categoryArray.put(jobj);
				}
				j.put("categoryMaster", categoryArray);

				cstmt.getMoreResults();
				rs7 = cstmt.getResultSet();
				while (rs7.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs7.getInt(1));
					jobj.put("name", rs7.getString(2));
					caseTypeArray.put(jobj);
				}
				j.put("caseTypeMaster", caseTypeArray);

				cstmt.getMoreResults();
				rs8 = cstmt.getResultSet();
				while (rs8.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs8.getInt(1));
					jobj.put("name", rs8.getString(2));

					stateArray.put(jobj);
				}
				j.put("stateArray", stateArray);
				j.put("msg", "Success");
				j.put(LawSuitTrackerConstants.status, "T");
			} else if ("fetchMasterForEdit".equalsIgnoreCase(type)) {

				while (rs.next()) {
					JSONObject jobj = new JSONObject();

					jobj.put("name", rs.getString(1));

					forumCategoryArray.put(jobj);
				}
				j.put("forumCategoryMaster", forumCategoryArray);

				cstmt.getMoreResults();

				rs3 = cstmt.getResultSet();
				while (rs3.next()) {
					JSONObject jobj = new JSONObject();
					// jobj.put("id", rs3.getInt(1));
					jobj.put("name", rs3.getString(1));

					aorArray.put(jobj);
				}
				j.put("aorCompanyMaster", aorArray);

				cstmt.getMoreResults();
				rs6 = cstmt.getResultSet();
				while (rs6.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs6.getInt(1));
					jobj.put("name", rs6.getString(2));

					categoryArray.put(jobj);
				}
				j.put("categoryMaster", categoryArray);

				cstmt.getMoreResults();
				rs7 = cstmt.getResultSet();
				while (rs7.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs7.getInt(1));
					jobj.put("name", rs7.getString(2));
					caseTypeArray.put(jobj);
				}
				j.put("caseTypeMaster", caseTypeArray);

				cstmt.getMoreResults();
				rs8 = cstmt.getResultSet();
				while (rs8.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs8.getInt(1));
					jobj.put("name", rs8.getString(2));

					stateArray.put(jobj);
				}
				j.put("stateArray", stateArray);
				j.put("msg", "Success");
				j.put(LawSuitTrackerConstants.status, "T");
			} else if ("fetchMasterForSearch".equalsIgnoreCase(type)) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					companyArray.put(jobj);

				}
				j.put("companyMaster", companyArray);
				cstmt.getMoreResults();

				rs7 = cstmt.getResultSet();
				while (rs7.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs7.getInt(1));
					jobj.put("name", rs7.getString(2));
					caseTypeArray.put(jobj);
				}
				j.put("caseTypeMaster", caseTypeArray);

				cstmt.getMoreResults();
				rs5 = cstmt.getResultSet();
				while (rs5.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs5.getInt(1));
					jobj.put("name", rs5.getString(2));

					statusArray.put(jobj);
				}
				j.put("statusMaster", statusArray);

				cstmt.getMoreResults();
				rs8 = cstmt.getResultSet();
				while (rs8.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs8.getInt(1));
					jobj.put("name", rs8.getString(2));

					stateArray.put(jobj);
				}
				j.put("stateArray", stateArray);

				cstmt.getMoreResults();
				rs2 = cstmt.getResultSet();
				while (rs2.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("name", rs2.getString(1));

					forumCategoryArray.put(jobj);
				}
				j.put("forumCategoryMaster", forumCategoryArray);
				j.put("msg", "Success");
				j.put(LawSuitTrackerConstants.status, "T");
			} else if ("fetchMasterForUserAdd".equalsIgnoreCase(type)
					|| "fetchMasterForUserEdit".equalsIgnoreCase(type)) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					companyArray.put(jobj);

				}
				j.put("companyMaster", companyArray);
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

				rs7 = cstmt.getResultSet();
				while (rs7.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs7.getInt(1));
					jobj.put("name", rs7.getString(2));
					caseTypeArray.put(jobj);
				}
				j.put("caseTypeMaster", caseTypeArray);

				cstmt.getMoreResults();
				rs6 = cstmt.getResultSet();
				while (rs6.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs6.getInt(1));
					jobj.put("name", rs6.getString(2));

					categoryArray.put(jobj);
				}
				j.put("userTypeMaster", categoryArray);

				cstmt.getMoreResults();
				rs5 = cstmt.getResultSet();
				while (rs5.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs5.getInt(1));
					jobj.put("name", rs5.getString(2));

					statusArray.put(jobj);
				}
				j.put("userAccessMaster", statusArray);

				if ("fetchMasterForUserEdit".equalsIgnoreCase(type)) {
					cstmt.getMoreResults();
					rs1 = cstmt.getResultSet();
					while (rs1.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs1.getInt(1));
						jobj.put("name", rs1.getString(2));

						stateArray.put(jobj);
					}
					j.put("disableUserReason", stateArray);
				}
				j.put(LawSuitTrackerConstants.status, "T");
				j.put("msg", "Success");
			} else if ("fetchBusiness".equalsIgnoreCase(type)) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					businessArray.put(jobj);

				}
				j.put("businessMaster", businessArray);
				cstmt.getMoreResults();

				rs2 = cstmt.getResultSet();
				while (rs2.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs2.getInt(1));
					jobj.put("name", rs2.getString(2));

					businessRepArray.put(jobj);
				}
				j.put("businessRepMaster", businessRepArray);

				cstmt.getMoreResults();

				rs3 = cstmt.getResultSet();
				while (rs3.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs3.getInt(1));
					jobj.put("name", rs3.getString(2));

					legalRepArray.put(jobj);
				}
				j.put("legalRepMaster", legalRepArray);

				j.put(LawSuitTrackerConstants.status, "T");
				j.put("msg", "Success");

			} else if ("fetchVertical".equalsIgnoreCase(type)) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("fullname", rs.getString(2));
					verticalArray.put(jobj);

				}
				j.put("verticalMaster", verticalArray);

				j.put(LawSuitTrackerConstants.status, "T");
				j.put("msg", "Success");

			}else if ("fetchForumForSearch".equalsIgnoreCase(type)) {
				while (rs.next()) {
					JSONObject jobj = new JSONObject();
					jobj.put("id", rs.getInt(1));
					jobj.put("name", rs.getString(2));
					forumCategoryArray.put(jobj);
				}
				j.put("resultArray", forumCategoryArray);

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
			if (rs1 != null)
				try {
					rs1.close();
					rs1 = null;
				} catch (Exception e) {
					logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (rs2 != null)
				try {
					rs2.close();
					rs2 = null;
				} catch (Exception e) {
					logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
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

		response = j.toString(4);
		return response;

	}
	
	
	public String fetchMastersForAsset(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONObject obj = new JSONObject(input);	
		String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String assetSegment = (obj.has("assetSegment")) ? obj.getString("assetSegment") : "";
		String trustName = (obj.has("trustName")) ? obj.getString("trustName") : "";
	
		try {
			con = SQLDBConnection.getDBConnection();
		if("fetchAssetSegment".equalsIgnoreCase(type)) {
			String query = "SELECT distinct asset_segment from asset_trust_account_master order by asset_segment ";

			 System.out.println(query);
			stmt = con.prepareStatement(query);
			

			rs = stmt.executeQuery();
			while (rs.next()) {		

				resultArray.put(rs.getString(1));
			}

			if (resultArray.length() == 0) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the case id ");
			}
			outJson.put("assetNameArray", resultArray);
		}else if ("fetchTrustName".equalsIgnoreCase(type)) {
			String query = "SELECT distinct trust_name FROM asset_trust_account_master where asset_segment = ?  order by trust_name ";

			
			stmt = con.prepareStatement(query);
			stmt.setString(1, assetSegment);
			 System.out.println(query);
			rs = stmt.executeQuery();
			while (rs.next()) {		

				resultArray.put(rs.getString(1));
			}

			if (resultArray.length() == 0) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the case id ");
			}
			outJson.put("trustNameArray", resultArray);
		}else if ("fetchAccountName".equalsIgnoreCase(type)) {
			String query = "SELECT id, account_name FROM asset_trust_account_master where asset_segment = ? and trust_name = ?  order by account_name";

			 
			stmt = con.prepareStatement(query);
			stmt.setString(1, assetSegment);
			stmt.setString(2, trustName);
			rs = stmt.executeQuery();
			System.out.println(query);
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("name", rs.getString(2));
				resultArray.put(jobj);
			}

			if (resultArray.length() == 0) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the case id ");
			}
			outJson.put("accountNameArray", resultArray);
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
		
		response = outJson.toString(4);
		return response;

	}
	
}
