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

public class ManageDashboard {
	public static final Logger logg = Logger.getLogger(ManageDashboard.class);

	public String getSummaryUpcoming(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject outJson = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		String endDate = (obj.has("endDate")) ? obj.getString("endDate") : "";
		String startDate = (obj.has("startDate")) ? obj.getString("startDate") : "";
		String state = (obj.has("state")) ? obj.getString("state") : "";
		String forumCategory = (obj.has("forumCategory")) ? obj.getString("forumCategory") : "";
		String caseType = (obj.has("caseType")) ? obj.getString("caseType") : "";
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = null;
		java.sql.Date sqlDate1 = null;
		java.sql.Date sqlDate2 = null;
		JSONArray chartArray = new JSONArray();
		try {
			if (!("".equals(endDate))) {
				date = sdf1.parse(endDate);
				sqlDate1 = new java.sql.Date(date.getTime());
			}
			if (!("".equals(startDate))) {
				date = sdf1.parse(startDate);
				sqlDate2 = new java.sql.Date(date.getTime());
			}

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		// if("".equals(sessID) || (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your session is invalid or expired. Please try again
		// later!");
		// }
		// else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
		// outJson.put(LawSuitTrackerConstants.status, "F");
		// outJson.put("msg", "Your are not authorized to perform this action.");
		// }
		// else
		// {
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "SELECT distinct a.id,a.company_name,a.business_name,a.vertical_name,a.case_no   "
					+ " ,a.forum, CONVERT(varchar,a.next_hearing_date, 103),a.sub_matter   "
					+ " ,a.user_id, a.ref_no, a.status_name, b.access_type, a.case_type   "
					+ " ,a.bench,  CONVERT(varchar, a.last_hearing_date, 103) as last_hearing_date,CONVERT(varchar, a.next_hearing_date, 103) as next_hearing_date   "
					+ " ,a.outcome_last_hearing, a.likeli_outcome_next_hearing, a.counsel_of_company, a.counsel_of_respondent, a.status_id, a.state, a.case_no, a.next_hearing_date as HearDate,  a.company_alias, a.last_hearing_date  "
					+ " ,a.aor_of_company,a.aor_of_respondent ,a.further_dates,a.business_rep,a.legal_team_rep, a.brief_facts,a.interim_prayer,a.final_prayer   "
					+ " ,a.finanicial_impact,a.category_name, a.entry_date,a.entered_by,a.user_id, a.ref_no, a.case_type_id, a.assessment_year, a.financial_year, a.amount_disallowence, a.petitioners, a.respondents, a.forum_category "
					+ " ,a.forum_id, a.case_type_num, a.case_id, a.case_year, a.pv_id, a.state_id, a.case_type_name,a.asset_account_id "
					+ " ,a.substitution_filed,a.substitution_allowed,a.interim_stay, a.stay_order_info, a.asset_segment, a.trust_name, a.account_name, a.vertical_alias,(select count(*) as count from document_details where case_id = a.id and status = 1)  "
					+ " FROM  (Select t1.id, t0.access_type from (Select * from dbo.access_level  where User_id = ?) t0 INNER JOIN "
					+ " (SELECT * FROM  dbo.case_master WHERE  (flag = 1)) AS t1 ON (t0.case_id = t1.id OR "
					+ " t0.case_id = 0) AND (t0.company = t1.company OR "
					+ " t0.company = 0) AND (t0.business = t1.business OR "
					+ " t0.business = 0) AND (t0.vertical = t1.vertical OR "
					+ " t0.vertical = 0) and t1.case_type in (select s from Split(',' ,(select case_type from user_master where payroll_no = ? and status = 1)))) b "
					+ " left outer Join vw_case_view_master a on b.id = a.id "
					+ " where a.flag=1 and  a.next_hearing_date >= CAST(? as DATE) and a.next_hearing_date <= CAST(? as DATE) ";

			if ("0".equals(state) || "All".equals(state) || "".equals(state))
				System.out.print("No state selection");
			else {
				if (state.matches("[0-9]+")) {
					query += " and a.state_id = '" + state+"'";
				} else
					query += " and a.state = '" + state+"'";

			}

			if (!("".equals(forumCategory)))
				query += "  and a.forum_category = '" + forumCategory + "'";

			if (!("".equals(caseType)) && !("0".equals(caseType)))
				query += " and a.case_type_id in (" + caseType + ")";

			query += " order by CAST(a.next_hearing_date as date), a.company_name, a.vertical_name";

			System.out.println(query);
			stmt = con.prepareStatement(query);
			stmt.setString(1, userID);
			stmt.setString(2, userID);
			stmt.setDate(3, sqlDate2);
			stmt.setDate(4, sqlDate1);

			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject ob = new JSONObject();
				ob.put("caseID", String.valueOf(rs.getInt(1)));
				ob.put("companyAlias", rs.getString(2));
				ob.put("business", rs.getString(3));
				ob.put("vertical", rs.getString(4));
				ob.put("caseNo", rs.getString(5));
				ob.put("forum", rs.getString(6));
				ob.put("nextHearingDate", rs.getString(7));
				ob.put("subMatter", rs.getString(8));
				ob.put("userID", rs.getString(9));
				ob.put("refNo", rs.getString(10));
				ob.put("statusName", rs.getString(11));
				ob.put("accessType", rs.getString(12));
				ob.put("caseType", rs.getString(13));
				ob.put("bench", rs.getString(14));
				ob.put("lastHearingDate", rs.getString(15));
				ob.put("nextHearingDate", rs.getString(16));
				ob.put("outcomeLast", rs.getString(17));
				ob.put("outcomeNext", rs.getString(18));
				ob.put("counselOfCompany", rs.getString(19));
				ob.put("counselOfRespondent", rs.getString(20));
				ob.put("statusID", rs.getInt(21));
				ob.put("state", rs.getString(22));
				ob.put("caseNo", rs.getString(23));
				ob.put("nextHearing", rs.getDate(24));
				ob.put("company", rs.getString(25));
				ob.put("lastHearing", rs.getDate(26));
				ob.put("aorOfCompany", rs.getString(27));
				ob.put("aorOfRespondent", rs.getString(28));
				ob.put("furtherDates", rs.getString(29));
				ob.put("businessRep", rs.getString(30));
				ob.put("legalRep", rs.getString(31));
				ob.put("briefFacts", rs.getString(32));
				ob.put("interimPrayer", rs.getString(33));
				ob.put("finalPrayer", rs.getString(34));
				ob.put("finImpact", rs.getString(35));
				ob.put("caseCategoryName", rs.getString(36));
				ob.put("updatedOn", rs.getTimestamp(37));
				ob.put("enteredBy", rs.getString(38));
				ob.put("userID", rs.getString(39));
				ob.put("refNo", rs.getString(40));
				ob.put("caseTypeID", rs.getInt(41));
				ob.put("assessYear", rs.getString(42));
				ob.put("finYear", rs.getString(43));
				ob.put("amtOfDisallow", rs.getLong(44));
				ob.put("petitioners", rs.getString(45));
				ob.put("respondents", rs.getString(46));
				ob.put("forumCategory", rs.getString(47));
				ob.put("forumID", rs.getInt(48));
				ob.put("caseTypeNum", rs.getInt(49));
				ob.put("courtCaseID", rs.getString(50));
				ob.put("caseYear", rs.getInt(51));
				ob.put("pvID", rs.getString(52));
				ob.put("stateID", rs.getInt(53));
				ob.put("caseTypeName", rs.getString(54));
				ob.put("accountID", rs.getInt(55));
				ob.put("substitutionFiled", rs.getString(56));
				ob.put("substitutionAllowed", rs.getString(57));
				ob.put("interimStay", rs.getString(58));
				ob.put("stayOrder", rs.getString(59));
				ob.put("assetSegment", rs.getString(60));
				ob.put("trustName", rs.getString(61));
				ob.put("accountName", rs.getString(62));
				ob.put("verticalAlias", rs.getString(63));
				ob.put("docCount", rs.getInt(64));

				resultArray.put(ob);
			}

			if (resultArray.length() == 0) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "No cases available for the case id ");
			}
			outJson.put("upComingHearingArray", resultArray);
			outJson.put("chartArrayMaster", chartArray);
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

	public String homePageInfo(String input, String sessionID, String sessUser) {
		String response = "";
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";

		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs3 = null;
		ResultSet rs2 = null;
		// ResultSet rs4 = null;
		ResultSet rs5 = null;
		CallableStatement cstmt = null;
		JSONObject j = new JSONObject();
		JSONArray todayHearingArray = new JSONArray();
		// JSONArray newAlertsArray = new JSONArray();
		JSONArray stateArray = new JSONArray();
		JSONArray alertActionArray = new JSONArray();

		// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
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
		j.put("notificationStatus", "F");
		try {
			con = SQLDBConnection.getDBConnection();
			cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.homePageMaster + "(?)}");

			cstmt.setString(1, userID);

			cstmt.execute();
			rs1 = cstmt.getResultSet();

			while (rs1.next()) {

				j.put("notificationStatus", rs1.getString(1));

			}

			cstmt.getMoreResults();
			rs2 = cstmt.getResultSet();
			while (rs2.next()) {
				JSONObject ob = new JSONObject();
				ob.put("caseID", String.valueOf(rs2.getInt(1)));
				ob.put("companyAlias", rs2.getString(2));
				ob.put("business", rs2.getString(3));
				ob.put("vertical", rs2.getString(4));
				ob.put("caseNo", rs2.getString(5));
				ob.put("forum", rs2.getString(6));
				ob.put("nextHearingDate", rs2.getString(7));
				ob.put("subMatter", rs2.getString(8));
				ob.put("userID", rs2.getString(9));
				ob.put("refNo", rs2.getString(10));
				ob.put("statusName", rs2.getString(11));
				ob.put("accessType", rs2.getString(12));
				ob.put("caseType", rs2.getString(13));
				ob.put("bench", rs2.getString(14));
				ob.put("lastHearingDate", rs2.getString(15));
				ob.put("nextHearingDate", rs2.getString(16));
				ob.put("outcomeLast", rs2.getString(17));
				ob.put("outcomeNext", rs2.getString(18));
				ob.put("counselOfCompany", rs2.getString(19));
				ob.put("counselOfRespondent", rs2.getString(20));
				ob.put("statusID", rs2.getInt(21));
				ob.put("state", rs2.getString(22));
				ob.put("caseNo", rs2.getString(23));
				ob.put("nextHearing", rs2.getDate(24));
				ob.put("company", rs2.getString(25));
				ob.put("lastHearing", rs2.getDate(26));
				ob.put("aorOfCompany", rs2.getString(27));
				ob.put("aorOfRespondent", rs2.getString(28));
				ob.put("furtherDates", rs2.getString(29));
				ob.put("businessRep", rs2.getString(30));
				ob.put("legalRep", rs2.getString(31));
				ob.put("briefFacts", rs2.getString(32));
				ob.put("interimPrayer", rs2.getString(33));
				ob.put("finalPrayer", rs2.getString(34));
				ob.put("finImpact", rs2.getString(35));
				ob.put("caseCategoryName", rs2.getString(36));
				ob.put("updatedOn", rs2.getTimestamp(37));
				ob.put("enteredBy", rs2.getString(38));
				ob.put("userID", rs2.getString(39));
				ob.put("refNo", rs2.getString(40));
				ob.put("caseTypeID", rs2.getInt(41));
				ob.put("assessYear", rs2.getString(42));
				ob.put("finYear", rs2.getString(43));
				ob.put("amtOfDisallow", rs2.getLong(44));
				ob.put("petitioners", rs2.getString(45));
				ob.put("respondents", rs2.getString(46));
				ob.put("forumCategory", rs2.getString(47));
				ob.put("forumID", rs2.getInt(48));
				ob.put("caseTypeNum", rs2.getInt(49));
				ob.put("courtCaseID", rs2.getString(50));
				ob.put("caseYear", rs2.getInt(51));
				ob.put("pvID", rs2.getString(52));
				ob.put("stateID", rs2.getInt(53));
				ob.put("caseTypeName", rs2.getString(54));
				ob.put("accountID", rs2.getInt(55));
				ob.put("substitutionFiled", rs2.getString(56));
				ob.put("substitutionAllowed", rs2.getString(57));
				ob.put("interimStay", rs2.getString(58));
				ob.put("stayOrder", rs2.getString(59));
				ob.put("assetSegment", rs2.getString(60));
				ob.put("trustName", rs2.getString(61));
				ob.put("accountName", rs2.getString(62));
				ob.put("verticalAlias", rs2.getString(63));
				ob.put("docCount", rs2.getInt(64));

				todayHearingArray.put(ob);
			}
			j.put("todayHearingArray", todayHearingArray);

			cstmt.getMoreResults();
			rs3 = cstmt.getResultSet();
			while (rs3.next()) {
				JSONObject ob = new JSONObject();
				ob.put("id", rs3.getInt(1));
				ob.put("name", rs3.getString(2));

				stateArray.put(ob);

			}
			cstmt.getMoreResults();
			rs5 = cstmt.getResultSet();
			while (rs5.next()) {
				JSONObject ob = new JSONObject();
				ob.put("id", rs5.getInt(1));
				ob.put("name", rs5.getString(2));

				alertActionArray.put(ob);

			}

			// cstmt.getMoreResults();
			// rs4 = cstmt.getResultSet();
			// while (rs4.next()) {
			// JSONObject ob = new JSONObject();
			// ob.put("id", rs4.getInt(1));
			// ob.put("caseNo", rs4.getString(2));
			// ob.put("petitioner", rs4.getString(3));
			// ob.put("respondent", rs4.getString(4));
			// ob.put("forumName", rs4.getString(5));
			// ob.put("bench", rs4.getString(6));
			// ob.put("counselOfCompany", rs4.getString(7));
			// ob.put("counselOfRespondent", rs4.getString(8));
			// ob.put("nextHearingDate", rs4.getString(9));
			// ob.put("subMatter", rs4.getString(10));
			// ob.put("caseStatus", rs4.getString(11));
			// ob.put("caseType", rs4.getString(12));
			// ob.put("dairyNo", rs4.getString(13));
			// ob.put("courtNo", rs4.getString(14));
			// ob.put("itemNo", rs4.getString(15));
			// ob.put("company", rs4.getInt(16));
			// ob.put("business", rs4.getInt(17));
			// ob.put("vertical", rs4.getInt(18));
			// ob.put("caseCategory", rs4.getInt(19));
			// ob.put("forum", rs4.getInt(20));
			// ob.put("timestamp", rs4.getTimestamp(23));
			// ob.put("companyName", rs4.getString(24));
			// ob.put("businessName", rs4.getString(25));
			// ob.put("verticalName", rs4.getString(26));
			// ob.put("caseTypeName", rs4.getString(27));
			// ob.put("keywords", rs4.getString(34));
			// ob.put("pvID", rs4.getString(35));
			// ob.put("notificationType", rs4.getString(38));
			// ob.put("updateType", rs4.getString(40));
			// //ob.put("notificationStatus", rs4.getString(39));
			// j.put("notificationStatus", rs4.getString(41));
			// newAlertsArray.put(ob);
			// }
			// j.put("newAlerts", newAlertsArray);
			j.put("todayHearingArray", todayHearingArray);
			j.put("stateArray", stateArray);
			j.put("alertActionArray", alertActionArray);

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

	public String fetchPushNotifications(String input) {
		String response = "";
		JSONObject obj = new JSONObject(input);
		String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		Connection con = null;
		ResultSet rs = null;
		CallableStatement cstmt = null;
		JSONObject j = new JSONObject();
		JSONArray pushArray = new JSONArray();

		try {
			con = SQLDBConnection.getDBConnection();
			cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.pushNotifProc + "(?)}");
			cstmt.setString(1, userID);
			cstmt.execute();
			rs = cstmt.getResultSet();

			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("userID", rs.getString(2));
				jobj.put("caseID", rs.getInt(3));
				jobj.put("notificationTitle", rs.getString(4));
				jobj.put("notificationDesc", rs.getString(5));
				jobj.put("remark", rs.getString(7));
				jobj.put("nextHearingDate", rs.getString(8));
				pushArray.put(jobj);
				j.put("pushArray", pushArray);
				j.put(LawSuitTrackerConstants.status, "T");
				j.put("msg", "Success");

			}

			if (pushArray.length() == 0) {
				j.put("pushArray", pushArray);
				j.put(LawSuitTrackerConstants.status, "T");
				j.put("msg", "None");
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
}