package com.lawsuittracker.modal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.dao.SecondDBConnection;
import com.lawsuittracker.pvIntegration.PVManageCases;
import com.lawsuittracker.util.LawSuitTrackerConstants;

public class ManageCases {
	public static final Logger logg = Logger.getLogger(ManageCases.class);
	PVManageCases pvMCases = new PVManageCases();

	public String addUpdateNewCase(String input, String sessionID, String sessUser) {
		String response = "";
		ResultSet rs = null;
		Connection con = null;
		JSONObject obj = new JSONObject(input);
		JSONObject outJson = new JSONObject();
		int company = obj.has("company") ? obj.getInt("company") : 0;
		int business = obj.has("business") ? obj.getInt("business") : 0;
		int vertical = obj.has("vertical") ? obj.getInt("vertical") : 0;
		int forum = obj.has("forum") ? obj.getInt("forum") : 1000000;
		String caseNo = obj.has("caseNo") ? obj.getString("caseNo") : "";
		String petitioner = obj.has("petitioner") ? obj.getString("petitioner") : "";
		String respondent = obj.has("respondent") ? obj.getString("respondent") : "";
		String bench = obj.has("bench") ? obj.getString("bench") : "";
		String aorOfCompany = obj.has("aorOfCompany") ? obj.getString("aorOfCompany") : "";
		String counselOfCompany = obj.has("counselOfCompany") ? obj.getString("counselOfCompany") : "";
		String aorOfRespondent = obj.has("aorOfRespondent") ? obj.getString("aorOfRespondent") : "";
		String counselOfRespondent = obj.has("counselOfRespondent") ? obj.getString("counselOfRespondent") : "";
		String lastDateOfHearing = obj.has("lastDateOfHearing") ? obj.getString("lastDateOfHearing") : "";
		String nextDateOfHearing = obj.has("nextDateOfHearing") ? obj.getString("nextDateOfHearing") : "";
		String furtherDates = obj.has("furtherDates") ? obj.getString("furtherDates") : "";
		String businessRep = obj.has("businessRep") ? obj.getString("businessRep") : "";
		String legalRep = obj.has("legalRep") ? obj.getString("legalRep") : "";
		int caseStatus = obj.has("caseStatus") ? obj.getInt("caseStatus") : 0;
		String subMatter = obj.has("subMatter") ? obj.getString("subMatter") : "";
		String briefFacts = obj.has("briefFacts") ? obj.getString("briefFacts") : "";
		String interimPrayer = obj.has("interimPrayer") ? obj.getString("interimPrayer") : "";
		String finalPrayer = obj.has("finalPrayer") ? obj.getString("finalPrayer") : "";
		String outcomeLast = obj.has("outcomeLast") ? obj.getString("outcomeLast") : "";
		String outcomeNext = obj.has("outcomeNext") ? obj.getString("outcomeNext") : "";
		String finImpact = obj.has("finImpact") ? obj.getString("finImpact") : "";
		int caseCategory = obj.has("caseCategory") ? obj.getInt("caseCategory") : 0;
		String enteredBy = obj.has("enteredBy") ? obj.getString("enteredBy") : "";
		String type = obj.has("type") ? obj.getString("type") : "";
		String refNo = obj.has("refNo") ? obj.getString("refNo") : "";
		String userID = obj.has("userID") ? obj.getString("userID") : "";
		int caseID = obj.has("caseID") ? obj.getInt("caseID") : 0;
		int caseType = obj.has("caseType") ? obj.getInt("caseType") : 0;
		String assessYear = obj.has("assessYear") ? obj.getString("assessYear") : "";
		String finYear = obj.has("finYear") ? obj.getString("finYear") : "";
		String currency = obj.has("currency") ? obj.getString("currency") : "";
		int state = obj.has("state") ? obj.getInt("state") : 0;
		String sessID = obj.has("sessID") ? obj.getString("sessID") : "";
		Long amtOfDisallow = Long.valueOf(obj.has("amtOfDisallow") ? obj.getLong("amtOfDisallow") : 0L);
		int alertID = obj.has("alertID") ? obj.getInt("alertID") : 0;
		int caseYear = obj.has("caseYear") ? obj.getInt("caseYear") : 0;
		String pvID = obj.has("pvID") ? obj.getString("pvID") : "";
		int courtCaseType = obj.has("courtCaseType") ? obj.getInt("courtCaseType") : 0;
		int accountName = obj.has("accountName") ? obj.getInt("accountName") : 0;
		String substitutionFiled = obj.has("substitutionFiled") ? obj.getString("substitutionFiled") : "";
		String substitutionAllowed = obj.has("substitutionAllowed") ? obj.getString("substitutionAllowed") : "";
		String interimStay = obj.has("interimStay") ? obj.getString("interimStay") : "";
		String stayOrder = obj.has("stayOrder") ? obj.getString("stayOrder") : "";

		String finImpactRecurring = obj.has("finImpactRecurring") ? obj.getString("finImpactRecurring") : "";
		String finImpactRecurringDuration = obj.has("finImpactRecurringDuration")
				? obj.getString("finImpactRecurringDuration")
				: "";

		JSONArray finImpArray = obj.has("finImp") ? obj.getJSONArray("finImp") : new JSONArray();
		CallableStatement cstmt = null;
		PreparedStatement stmt = null;

		petitioner = petitioner.replaceAll("(?i)<(?!(/?(ol|li)))[^>]*>", "");
		respondent = respondent.replaceAll("(?i)<(?!(/?(ol|li)))[^>]*>", "");
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = null;
		java.util.Date date1 = null;
		java.sql.Date sqlDate1 = null;
		java.sql.Date sqlDate2 = null;
		try {
			if (!"".equals(lastDateOfHearing)) {
				date = sdf1.parse(lastDateOfHearing);
				sqlDate1 = new java.sql.Date(date.getTime());
			}
			if (!"".equals(nextDateOfHearing)) {
				date1 = sdf1.parse(nextDateOfHearing);
				sqlDate2 = new java.sql.Date(date1.getTime());
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if (sessionID == null) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session has expired. Please logout and login again...");
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session is invalid or expired. Please try again later!");
		} else if (!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your are not authorized to perform this action.");
		} else {

			try {
				con = SQLDBConnection.getDBConnection();
				cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.caseAddEditProc
						+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, userID);
				cstmt.setString(2, type);
				cstmt.setInt(3, company);
				cstmt.setInt(4, business);
				cstmt.setInt(5, vertical);
				cstmt.setString(6, caseNo);
				cstmt.setString(7, petitioner);
				cstmt.setString(8, respondent);
				cstmt.setInt(9, forum);
				cstmt.setString(10, bench);
				cstmt.setString(11, aorOfCompany);
				cstmt.setString(12, aorOfRespondent);
				cstmt.setString(13, counselOfCompany);
				cstmt.setString(14, counselOfRespondent);
				cstmt.setDate(15, sqlDate1);
				cstmt.setDate(16, sqlDate2);
				cstmt.setString(17, furtherDates);
				cstmt.setString(18, businessRep);
				cstmt.setString(19, legalRep);
				cstmt.setString(20, subMatter);
				cstmt.setString(21, briefFacts);
				cstmt.setString(22, interimPrayer);
				cstmt.setString(23, finalPrayer);
				cstmt.setString(24, outcomeLast);
				cstmt.setString(25, outcomeNext);
				cstmt.setString(26, finImpact);
				cstmt.setInt(27, caseCategory);
				cstmt.setInt(28, caseStatus);
				cstmt.setString(29, enteredBy);
				cstmt.setInt(30, caseID);
				cstmt.setString(31, refNo);
				cstmt.setInt(32, caseType);
				cstmt.setString(33, assessYear);
				cstmt.setString(34, finYear);
				cstmt.setLong(35, amtOfDisallow.longValue());
				cstmt.setInt(36, state);
				cstmt.setString(37, sessID);
				cstmt.setInt(38, alertID);
				cstmt.setInt(39, caseYear);
				cstmt.setInt(40, courtCaseType);
				cstmt.setInt(41, accountName);
				cstmt.setString(42, substitutionFiled);
				cstmt.setString(43, substitutionAllowed);
				cstmt.setString(44, interimStay);
				cstmt.setString(45, stayOrder);
				cstmt.setString(46, finImpactRecurring);
				cstmt.setString(47, finImpactRecurringDuration);

				cstmt.execute();
				rs = cstmt.getResultSet();

				if (rs.next()) {
					outJson.put("count", rs.getInt(1));
					if ((rs.getInt(1) == 1) && (finImpArray.length() > 0)
							&& (("add".equalsIgnoreCase(type)) || ("edit".equalsIgnoreCase(type)))) {
						for (int i = 0; i < finImpArray.length(); i++) {
							try {
								String query = "insert into financial_impact(case_id,particular, principal, penalty, interest, total, currency, user_id) values(?,?,?,?,?,?,?,?)";
								stmt = con.prepareStatement(query);
								stmt.setInt(1, rs.getInt(2));
								String part = finImpArray.getJSONObject(i).getString("particular");
								String parseStr = part.replaceAll("(?i)<(?!(/?(li)))[^>]*>", "");
								stmt.setString(2, parseStr);
								stmt.setLong(3,
										Long.valueOf(finImpArray.getJSONObject(i).getLong("principal")).longValue());
								stmt.setLong(4,
										Long.valueOf(finImpArray.getJSONObject(i).getLong("penalty")).longValue());
								stmt.setLong(5,
										Long.valueOf(finImpArray.getJSONObject(i).getLong("interest")).longValue());
								stmt.setLong(6,
										Long.valueOf(finImpArray.getJSONObject(i).getLong("total")).longValue());
								stmt.setString(7, currency);
								stmt.setString(8, userID);
								int result = stmt.executeUpdate();
								if (result > 0) {
									outJson.put("finUpdate", "true");
								} else {
									outJson.put("finUpdate", "false");
								}
							} catch (SQLException e) {
								outJson.put("finUpdate", "false");
								logg.error("Login Error in SQL Exception ==>" + e.getMessage());
							}
						}
					} else if ("add".equalsIgnoreCase(type) || ("edit".equalsIgnoreCase(type)) && ("".equals(pvID))) {
						if (!("".equals(caseNo)) && forum <= 100000 && caseYear != 0 && courtCaseType != 1000000) {
							obj.put("id", rs.getInt(2));

							pvMCases.registerCaseWithPV(obj.toString());
						}
					}

					outJson.put("caseID", rs.getInt(2));
					outJson.put("refNo", rs.getString(3));
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "Record not inserted.. Pls try later..");
				}

			} catch (SQLException e) {

				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", e);

				logg.error("Login Error in SQL Exception ==>" + e);

			}

			finally {
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
						stmt = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				}
				if (cstmt != null) {
					try {
						cstmt.close();
						cstmt = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
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

	// public String fetchAllCases(String input, String sessionID, String sessUser)
	// {
	// String response = "";
	// Connection con = null;
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// JSONObject outJson = new JSONObject();
	// JSONArray resultArray = new JSONArray();
	// JSONObject obj = new JSONObject(input);
	// String userID = (obj.has("userID")) ? obj.getString("userID") : "";
	// String company = (obj.has("company")) ? obj.getString("company") : "";
	// String business = (obj.has("business")) ? obj.getString("business") : "";
	// String vertical = (obj.has("vertical")) ? obj.getString("vertical") : "";
	// String caseType = (obj.has("caseType")) ? obj.getString("caseType") : "";
	// String forum = (obj.has("forum")) ? obj.getString("forum") : "";
	// String state = (obj.has("state")) ? obj.getString("state") : "";
	// String startDate = (obj.has("startDate")) ? obj.getString("startDate") : "";
	// String endDate = (obj.has("endDate")) ? obj.getString("endDate") : "";
	// String caseStatus = (obj.has("caseStatus")) ? obj.getString("caseStatus") :
	// "";
	// String caseID = (obj.has("caseID")) ? obj.getString("caseID") : "";
	// String caseNo = (obj.has("caseNo")) ? obj.getString("caseNo") : "";
	// String startDateLast = (obj.has("startDateLast")) ?
	// obj.getString("startDateLast") : "";
	// String endDateLast = (obj.has("endDateLast")) ? obj.getString("endDateLast")
	// : "";
	// String nextDateNA = (obj.has("nextDateNA")) ? obj.getString("nextDateNA") :
	// "";
	// String judgeResPronCheck = (obj.has("judgeResPronCheck")) ?
	// obj.getString("judgeResPronCheck") : "";
	//
	// String st = "";
	// String[] values = state.split(",");
	// for (int i = 0; i < values.length; i++) {
	// st += "'" + values[i] + "'";
	// if (i != (values.length - 1)) {
	// st += ",";
	// }
	// }
	//
	// SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
	// java.util.Date date = null;
	// java.util.Date date1 = null;
	// java.sql.Date sqlDate1 = null;
	// java.sql.Date sqlDate2 = null;
	// try {
	// if (!("".equals(startDate))) {
	// date = sdf1.parse(startDate);
	// sqlDate1 = new java.sql.Date(date.getTime());
	// }
	// if (!("".equals(endDate))) {
	// date1 = sdf1.parse(endDate);
	// sqlDate2 = new java.sql.Date(date1.getTime());
	// }
	// } catch (ParseException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// java.util.Date date2 = null;
	// java.util.Date date3 = null;
	// java.sql.Date sqlDate3 = null;
	// java.sql.Date sqlDate4 = null;
	// try {
	// if (!("".equals(startDateLast))) {
	// date2 = sdf1.parse(startDateLast);
	// sqlDate3 = new java.sql.Date(date2.getTime());
	// }
	// if (!("".equals(endDateLast))) {
	// date3 = sdf1.parse(endDateLast);
	// sqlDate4 = new java.sql.Date(date3.getTime());
	// }
	// } catch (ParseException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// int i = 0;
	// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
	// // if("".equals(sessID) || (!sessionID.equals(sessID))) {
	// // outJson.put(LawSuitTrackerConstants.status, "F");
	// // outJson.put("msg", "Your session is invalid or expired. Please try again
	// // later!");
	// // }
	// // else if(!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
	// // outJson.put(LawSuitTrackerConstants.status, "F");
	// // outJson.put("msg", "Your are not authorized to perform this action.");
	// // }
	// // else
	// // {
	// try {
	// con = SQLDBConnection.getDBConnection();
	// String query = "SELECT distinct a.id, a.company_alias,
	// a.business_name,a.vertical_name,a.case_no ,a.forum,
	// CONVERT(varchar,a.next_hearing_date, 103),a.sub_matter ,a.user_id,
	// a.status_name, b.access_type, a.case_type , CONVERT(varchar,
	// a.last_hearing_date, 103) as last_hearing_date,CONVERT(varchar,
	// a.next_hearing_date, 103) as next_hearing_date ,a.state,a.last_hearing_date ,
	// a.next_hearing_date, petitioner,respondent FROM vw_case_view_master a left
	// outer join vw_case_user_master b on a.id = b.id and b.user_id = ? where
	// a.flag=1 ";
	//
	// if (!("".equals(company)) && !("0".equals(company)))
	// query += " and a.company_id in (" + company + ")";
	// if (!("".equals(business)) && !("0".equals(business)))
	// query += " and a.business_id in (" + business + ")";
	// if (!("".equals(vertical)) && !("0".equals(vertical)))
	// query += " and a.vertical_id in (" + vertical + ")";
	// if (!("".equals(caseType)) && !("0".equals(caseType)))
	// query += " and a.case_type_id in (" + caseType + ")";
	// if (!("".equals(forum)) && !("0".equals(forum)))
	// query += " and a.forum_id in (" + forum + ")";
	// if (!("".equals(caseStatus)) && !("0".equals(caseStatus)))
	// query += " and a.status_id in (" + caseStatus + ")";
	// if (!("".equals(state)) && !("0".equals(state)))
	// query += " and a.state in (" + st + ")";
	// if (!("".equals(startDate)) && !("".equals(endDate)))
	// query += " and CAST(a.next_hearing_date as date) between ? and ?";
	// else if (!("".equals(startDate)) && ("".equals(endDate)))
	// query += " and CAST(a.next_hearing_date as date) >= ? ";
	// else if (("".equals(startDate)) && !("".equals(endDate)))
	// query += " and CAST(a.next_hearing_date as date) <= ? ";
	// if (!("".equals(caseID)) && !("0".equals(caseID)))
	// query += " and a.id = " + String.valueOf(caseID);
	// if (!("".equals(caseNo)))
	// query += " and a.case_no like '%" + caseNo + "%'";
	// if (!("".equals(startDateLast)) && !("".equals(endDateLast)))
	// query += " and CAST(a.last_hearing_date as date) between '" + sqlDate3 + "'
	// and '" + sqlDate4 + "'";
	// else if (!("".equals(startDateLast)) && ("".equals(endDateLast)))
	// query += " and CAST(a.last_hearing_date as date) >= '" + sqlDate3 + "'";
	// else if (("".equals(startDateLast)) && !("".equals(endDateLast)))
	// query += " and CAST(a.last_hearing_date as date) <= '" + sqlDate4 + "'";
	// if ("Y".equals(nextDateNA))
	// query += " and ( a.next_hearing_date is NULL or a.next_hearing_date = '') ";
	// if ("Y".equals(judgeResPronCheck))
	// query += " and (a.status_id not in (4,5) or a.status_id is NULL)";
	//
	// query += " order by CAST(a.next_hearing_date as date), a.company_alias,
	// a.case_type, a.vertical_name";
	// System.out.println(query);
	// stmt = con.prepareStatement(query);
	// stmt.setString(1, userID);
	// if (!("".equals(startDate)) && !("".equals(endDate))) {
	// stmt.setDate(2, sqlDate1);
	// stmt.setDate(3, sqlDate2);
	// } else if (!("".equals(startDate)) && ("".equals(endDate)))
	// stmt.setDate(2, sqlDate1);
	// else if (("".equals(startDate)) && !("".equals(endDate)))
	// stmt.setDate(2, sqlDate2);
	//
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// JSONObject ob = new JSONObject();
	// ob.put("id", i);
	// ob.put("caseID", String.valueOf(rs.getInt(1)));
	// ob.put("companyAlias", rs.getString(2));
	// ob.put("company", rs.getString(2));
	// ob.put("business", rs.getString(3));
	// ob.put("vertical", rs.getString(4));
	// ob.put("caseNo", rs.getString(5));
	// ob.put("forum", rs.getString(6));
	// ob.put("nextHearingDate", rs.getString(7));
	// ob.put("subMatter", rs.getString(8));
	// ob.put("userID", rs.getString(9));
	// ob.put("statusName", rs.getString(10));
	// ob.put("accessType", rs.getString(11));
	// ob.put("caseType", rs.getString(12));
	// ob.put("lastHearingDate", rs.getString(13));
	// ob.put("nextHearingDate", rs.getString(14));
	// ob.put("state", rs.getString(15));
	// ob.put("lastHearing", rs.getDate(16));
	// ob.put("nextHearing", rs.getDate(17));
	// ob.put("petitioner", rs.getString(18));
	// ob.put("respondent", rs.getString(19));
	// i++;
	// outJson.put(LawSuitTrackerConstants.status, "T");
	// outJson.put("msg", "Success");
	// resultArray.put(ob);
	//
	// }
	// if (resultArray.length() == 0) {
	// outJson.put(LawSuitTrackerConstants.status, "F");
	// outJson.put("msg", "No cases available for the case id ");
	// }
	// outJson.put("listOfCases", resultArray);
	// } catch (SQLException e) {
	// logg.error("Login Error in SQL Exception ==>" + e);
	// outJson.put(LawSuitTrackerConstants.status, "F");
	// outJson.put("msg", "There seems some error at server side. Please try again
	// later!!!");
	// } finally {
	// if (rs != null)
	// try {
	// rs.close();
	// rs = null;
	// } catch (Exception e) {
	// logg.error("Login Error in Finally - rs ==>" + e.getMessage());
	// }
	// if (stmt != null)
	// try {
	// stmt.close();
	// stmt = null;
	// } catch (Exception e) {
	// logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
	// }
	// if (con != null)
	// try {
	// con.close();
	// con = null;
	// } catch (Exception e) {
	// logg.error("Login Error in Finally - con ==>" + e.getMessage());
	// }
	// }
	// // }
	// response = outJson.toString(4);
	// return response;
	// }

	public String fetchAllCases(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
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
		String forum = (obj.has("forum")) ? obj.getString("forum") : "";
		String state = (obj.has("state")) ? obj.getString("state") : "";
		String startDate = (obj.has("startDate")) ? obj.getString("startDate") : "";
		String endDate = (obj.has("endDate")) ? obj.getString("endDate") : "";
		String caseStatus = (obj.has("caseStatus")) ? obj.getString("caseStatus") : "";
		String caseID = (obj.has("caseID")) ? obj.getString("caseID") : "";
		String caseNo = (obj.has("caseNo")) ? obj.getString("caseNo") : "";
		String startDateLast = (obj.has("startDateLast")) ? obj.getString("startDateLast") : "";
		String endDateLast = (obj.has("endDateLast")) ? obj.getString("endDateLast") : "";
		String nextDateNA = (obj.has("nextDateNA")) ? obj.getString("nextDateNA") : "";
		String judgePronCheck = (obj.has("judgePronCheck")) ? obj.getString("judgePronCheck") : "";
		String forumCategory = (obj.has("forumCategory")) ? obj.getString("forumCategory") : "";

		String st = "";
		String[] values = state.split(",");
		for (int i = 0; i < values.length; i++) {
			st += "'" + values[i] + "'";
			if (i != (values.length - 1)) {
				st += ",";
			}
		}

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

		java.util.Date date2 = null;
		java.util.Date date3 = null;
		java.sql.Date sqlDate3 = null;
		java.sql.Date sqlDate4 = null;
		try {
			if (!("".equals(startDateLast))) {
				date2 = sdf1.parse(startDateLast);
				sqlDate3 = new java.sql.Date(date2.getTime());
			}
			if (!("".equals(endDateLast))) {
				date3 = sdf1.parse(endDateLast);
				sqlDate4 = new java.sql.Date(date3.getTime());
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
		} else if ("".equals(sessID) || (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your session is invalid or expired. Please try again later!");
		} else if (!(sessUser.equals(userID)) && (!sessionID.equals(sessID))) {
			outJson.put(LawSuitTrackerConstants.status, "F");
			outJson.put("msg", "Your are not authorized to perform this action.");
		} else {
			try {
				con = SQLDBConnection.getDBConnection();
				String query = "SELECT distinct a.id,a.company_name,a.business_name,a.vertical_name,a.case_no   "
						+ " ,a.forum, CONVERT(varchar,a.next_hearing_date, 103),a.sub_matter   "
						+ " ,a.user_id, a.ref_no, a.status_name, b.access_type, a.case_type   "
						+ " ,a.bench,  CONVERT(varchar, a.last_hearing_date, 103) as last_hearing_date,CONVERT(varchar, a.next_hearing_date, 103) as next_hearing_date   "
						+ " ,a.outcome_last_hearing, a.likeli_outcome_next_hearing, a.counsel_of_company, a.counsel_of_respondent, a.status_id, a.state, a.case_no, a.next_hearing_date as HearDate,  a.company_alias, a.last_hearing_date  "
						+ " ,a.aor_of_company,a.aor_of_respondent ,a.further_dates,a.business_rep,a.legal_team_rep, a.brief_facts,a.interim_prayer,a.final_prayer   "
						+ " ,a.finanicial_impact,a.category_name, a.entry_date,a.entered_by,a.user_id, a.ref_no, a.case_type_id, a.assessment_year, a.financial_year, a.amount_disallowence, a.petitioner,a.respondent,a.petitioners,a.respondents, a.forum_category "
						+ " ,a.forum_id, a.case_type_num, a.case_id, a.case_year, a.pv_id, a.state_id, a.case_type_name, a.asset_account_id "
						+ " ,a.substitution_filed,a.substitution_allowed,a.interim_stay, a.stay_order_info , a.asset_segment, a.trust_name, a.account_name, a.vertical_alias , a.company_id, a.financial_impact_recurring, a.financial_impact_recurring_deuration, a.additional_comments, (select count(*) as count from document_details where case_id = a.id and status = 1)  "
						+ " FROM  (Select t1.id, t0.access_type from (Select * from dbo.access_level  where User_id = ?) t0 INNER JOIN"
						+ " (SELECT * FROM  dbo.case_master WHERE  (flag = 1)) AS t1 ON (t0.case_id = t1.id OR"
						+ " t0.case_id = 0) AND (t0.company = t1.company OR"
						+ " t0.company = 0) AND (t0.business = t1.business OR"
						+ " t0.business = 0) AND (t0.vertical = t1.vertical OR"
						+ " t0.vertical = 0) and t1.case_type in (select s from Split(',' ,(select case_type from user_master where payroll_no = ? and status = 1)))) b "
						+ " left outer Join vw_case_view_master a on b.id = a.id " + " where a.flag=1 ";

				if (!("".equals(company)) && !("0".equals(company)))
					query += " and a.company_id in (" + company + ")";
				if (!("".equals(business)) && !("0".equals(business)))
					query += " and a.business_id in (" + business + ")";
				if (!("".equals(vertical)) && !("0".equals(vertical)))
					query += " and a.vertical_id in (" + vertical + ")";
				if (!("".equals(caseType)) && !("0".equals(caseType)))
					query += " and a.case_type_id in (" + caseType + ")";
				if (!("".equals(forum)) && !("100000000".equals(forum)))
					query += " and a.forum_id in (" + forum + ")";
				if (!("".equals(caseStatus)) && !("0".equals(caseStatus)))
					query += " and a.status_id in (" + caseStatus + ")";
				if (!("".equals(state)) && !("0".equals(state)))
					query += " and a.state_id in (" + st + ")";
				if (!("".equals(startDate)) && !("".equals(endDate)))
					query += " and CAST(a.next_hearing_date as date) between ? and ?";
				else if (!("".equals(startDate)) && ("".equals(endDate)))
					query += " and CAST(a.next_hearing_date as date) >= ? ";
				else if (("".equals(startDate)) && !("".equals(endDate)))
					query += " and CAST(a.next_hearing_date as date) <= ? ";
				if (!("".equals(caseID)) && !("0".equals(caseID)))
					query += " and a.id = " + String.valueOf(caseID);
				if (!("".equals(caseNo)))
					query += " and a.case_no like '%" + caseNo + "%'";
				if (!("".equals(startDateLast)) && !("".equals(endDateLast)))
					query += " and CAST(a.last_hearing_date as date) between '" + sqlDate3 + "' and '" + sqlDate4 + "'";
				else if (!("".equals(startDateLast)) && ("".equals(endDateLast)))
					query += " and CAST(a.last_hearing_date as date) >= '" + sqlDate3 + "'";
				else if (("".equals(startDateLast)) && !("".equals(endDateLast)))
					query += " and CAST(a.last_hearing_date as date) <= '" + sqlDate4 + "'";
				if ("Y".equals(nextDateNA))
					query += " and ( a.next_hearing_date is NULL or a.next_hearing_date = '') ";
				if ("Y".equals(judgePronCheck))
					query += "  and (a.status_id not in (5) or a.status_id is NULL)";
				if (!("".equals(forumCategory)))
					query += "  and a.forum_category in (select s from dbo.split(',','" + forumCategory + "')) ";

				query += " order by a.forum, a.vertical_name, CAST(a.next_hearing_date as date)";

				// System.out.println(query);
				stmt = con.prepareStatement(query);
				stmt.setString(1, userID);
				stmt.setString(2, userID);
				if (!("".equals(startDate)) && !("".equals(endDate))) {
					stmt.setDate(3, sqlDate1);
					stmt.setDate(4, sqlDate2);
				} else if (!("".equals(startDate)) && ("".equals(endDate)))
					stmt.setDate(3, sqlDate1);
				else if (("".equals(startDate)) && !("".equals(endDate)))
					stmt.setDate(3, sqlDate2);

				rs = stmt.executeQuery();
				while (rs.next()) {
					JSONObject ob = new JSONObject();
					ob.put("id", i);
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
					ob.put("petitioner", rs.getString(45));
					ob.put("respondent", rs.getString(46));
					ob.put("petitioners", rs.getString(47));
					ob.put("respondents", rs.getString(48));
					ob.put("forumCategory", rs.getString(49));
					ob.put("forumID", rs.getInt(50));
					ob.put("caseTypeNum", rs.getInt(51));
					ob.put("courtCaseID", rs.getString(52));
					ob.put("caseYear", rs.getInt(53));
					ob.put("pvID", rs.getString(54));
					ob.put("stateID", rs.getInt(55));
					ob.put("caseTypeName", rs.getString(56));
					ob.put("accountID", rs.getInt(57));
					ob.put("substitutionFiled", rs.getString(58));
					ob.put("substitutionAllowed", rs.getString(59));
					ob.put("interimStay", rs.getString(60));
					ob.put("stayOrder", rs.getString(61));
					ob.put("assetSegment", rs.getString(62));
					ob.put("trustName", rs.getString(63));
					ob.put("accountName", rs.getString(64));
					ob.put("verticalAlias", rs.getString(65));
					ob.put("companyID", rs.getString(66));
					ob.put("finImpRecurring", rs.getString(67));
					ob.put("finImaRecurringDuration", rs.getString(68));
					ob.put("additionalComments", rs.getString(69));
					ob.put("docCount", rs.getInt(70));

					String cNo = (rs.getString(56) == null) ? ""
							: ("Others".equalsIgnoreCase(rs.getString(56)) ? "NA" : rs.getString(56));

					if (!("".equalsIgnoreCase((rs.getString(5)))) && (rs.getString(5) != null)
							&& (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo + " / " + rs.getString(5);
					else if ((rs.getString(5) == "") && (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo;
					else if ((rs.getString(5) != "") && ("".equalsIgnoreCase((cNo))))
						cNo = rs.getString(5);

					if ((rs.getInt(53) != 0) && (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo + " / " + rs.getInt(53);
					else if ((rs.getInt(53) == 0) && (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo;
					else if ((rs.getInt(53) != 0) && ("".equalsIgnoreCase((cNo))))
						cNo = rs.getString(53);

					ob.put("cNo", cNo);

					i++;
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
					resultArray.put(ob);

				}
				if (resultArray.length() == 0) {
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "No cases found for selected criteria ");
				}
				outJson.put("listOfCases", resultArray);
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

	public String fetchCaseDetails(String input, String sessionID, String sessUser) {
		String response = "";
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		ResultSet rs1 = null;
		PreparedStatement stmt1 = null;
		ResultSet rs2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs3 = null;
		PreparedStatement stmt3 = null;
		JSONObject obj = new JSONObject(input);
		JSONObject outJson = new JSONObject();
		JSONArray finArray = new JSONArray();
		JSONArray hearingArray = new JSONArray();
		JSONArray paperArray = new JSONArray();
		int caseID = (obj.has("caseID")) ? obj.getInt("caseID") : 0;
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
				String query = "SELECT id,company_name,business_name,vertical_name,case_no "
						+ ",petitioner,respondent,forum,bench,aor_of_company,aor_of_respondent"
						+ ",counsel_of_company,counsel_of_respondent,CONVERT(varchar, last_hearing_date, 103)"
						+ ",CONVERT(varchar,next_hearing_date, 103),further_dates,business_rep"
						+ ",legal_team_rep,sub_matter,brief_facts,interim_prayer,final_prayer"
						+ ",outcome_last_hearing,likeli_outcome_next_hearing,finanicial_impact"
						+ ",category_name, entry_date,entered_by, user_id, ref_no, company_id, company_alias, business_id "
						+ ",category_id, status_id, vertical_id, status_name, case_type, case_type_id, assessment_year"
						+ ",financial_year, amount_disallowence, state, vertical_alias, next_hearing_date, forum_category"
						+ ",forum_id, case_type_num, case_id, case_year, pv_id, state_id, case_type_name "
						+ ",case when next_hearing_date <= CAST(getdate() as date) then 'T' else 'F' end , asset_account_id "
						+ ",substitution_filed,substitution_allowed,interim_stay, stay_order_info, asset_segment, trust_name, account_name, vertical_alias, petitioners, respondents "
						+ ",financial_impact_recurring, financial_impact_recurring_deuration, additional_comments FROM vw_case_view_master where id = ? and id in (select id from vw_case_user_master where user_id = ? and id = ?)";
				stmt = con.prepareStatement(query);
				stmt.setInt(1, caseID);
				stmt.setString(2, userID);
				stmt.setInt(3, caseID);
				rs = stmt.executeQuery();
				if (rs.next()) {
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
					outJson.put("caseID", String.valueOf(rs.getInt(1)));
					outJson.put("company", rs.getString(2));
					outJson.put("business", rs.getString(3));
					outJson.put("vertical", rs.getString(4));
					outJson.put("caseNo", rs.getString(5));
					outJson.put("petitioner", rs.getString(6));
					outJson.put("respondent", rs.getString(7));
					outJson.put("forum", rs.getString(8));
					outJson.put("bench", rs.getString(9));
					outJson.put("aorOfCompany", rs.getString(10));
					outJson.put("aorOfRespondent", rs.getString(11));
					outJson.put("counselOfCompany", rs.getString(12));
					outJson.put("counselOfRespondent", rs.getString(13));
					outJson.put("lastHearingDate", rs.getString(14));
					outJson.put("nextHearingDate", rs.getString(15));
					outJson.put("furtherDates", rs.getString(16));
					outJson.put("businessRep", rs.getString(17));
					outJson.put("legalRep", rs.getString(18));
					outJson.put("subMatter", rs.getString(19));
					outJson.put("briefFacts", rs.getString(20));
					outJson.put("interimPrayer", rs.getString(21));
					outJson.put("finalPrayer", rs.getString(22));
					outJson.put("outcomeLast", rs.getString(23));
					outJson.put("outcomeNext", rs.getString(24));
					outJson.put("finImpact", rs.getString(25));
					outJson.put("caseCategoryName", rs.getString(26));
					outJson.put("updatedOn", rs.getTimestamp(27));
					outJson.put("enteredBy", rs.getString(28));
					outJson.put("userID", rs.getString(29));
					outJson.put("refNo", rs.getString(30));
					outJson.put("companyID", rs.getInt(31));
					outJson.put("companyAlias", rs.getString(32));
					outJson.put("businessID", rs.getInt(33));
					outJson.put("categoryID", rs.getInt(34));
					outJson.put("statusID", rs.getInt(35));
					outJson.put("verticalID", rs.getInt(36));
					outJson.put("statusName", rs.getString(37));
					outJson.put("caseType", rs.getString(38));
					outJson.put("caseTypeID", rs.getInt(39));
					outJson.put("assessYear", rs.getString(40));
					outJson.put("finYear", rs.getString(41));
					outJson.put("amtOfDisallow", rs.getLong(42));
					outJson.put("state", rs.getString(43));
					outJson.put("verticalAlias", rs.getString(44));
					outJson.put("nextHearing", rs.getDate(45));
					outJson.put("forumCategory", rs.getString(46));
					outJson.put("forumID", rs.getInt(47));
					outJson.put("caseTypeNum", rs.getInt(48));
					outJson.put("courtCaseID", rs.getString(49));
					outJson.put("caseYear", rs.getInt(50));
					outJson.put("pvID", rs.getString(51));
					outJson.put("stateID", rs.getInt(52));
					outJson.put("caseTypeName", rs.getString(53));
					outJson.put("showStatus", rs.getString(54));
					outJson.put("accountID", rs.getInt(55));
					outJson.put("substitutionFiled", rs.getString(56));
					outJson.put("substitutionAllowed", rs.getString(57));
					outJson.put("interimStay", rs.getString(58));
					outJson.put("stayOrder", rs.getString(59));
					outJson.put("assetSegment", rs.getString(60));
					outJson.put("trustName", rs.getString(61));
					outJson.put("accountName", rs.getString(62));
					outJson.put("verticalAlias", rs.getString(63));
					outJson.put("petitioners", rs.getString(64));
					outJson.put("respondents", rs.getString(65));
					outJson.put("finImpRecurring", rs.getString(66));
					outJson.put("finImaRecurringDuration", rs.getString(67));
					outJson.put("additionalComments", rs.getString(68));

					String cNo = (rs.getString(53) == null) ? ""
							: ("Others".equalsIgnoreCase(rs.getString(53)) ? "NA" : rs.getString(53));

					if (!("".equalsIgnoreCase((rs.getString(5)))) && (rs.getString(5) != null)
							&& (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo + "/" + rs.getString(5);
					else if ((rs.getString(5) == "") && (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo;
					else if ((rs.getString(5) != "") && ("".equalsIgnoreCase((cNo))))
						cNo = rs.getString(5);

					if ((rs.getInt(50) != 0) && (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo + "/" + rs.getInt(50);
					else if ((rs.getInt(50) == 0) && (!("".equalsIgnoreCase((cNo)))))
						cNo = cNo;
					else if ((rs.getInt(50) != 0) && ("".equalsIgnoreCase((cNo))))
						cNo = rs.getString(50);

					outJson.put("cNo", cNo);

					String sql = "SELECT * from financial_impact where case_id = ?";
					stmt1 = con.prepareStatement(sql);
					stmt1.setInt(1, caseID);
					rs1 = stmt1.executeQuery();
					while (rs1.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs1.getInt(1));
						jobj.put("particular", rs1.getString(3));
						jobj.put("principal", rs1.getInt(4));
						jobj.put("penalty", rs1.getInt(5));
						jobj.put("interest", rs1.getInt(6));
						jobj.put("total", rs1.getInt(7));
						jobj.put("currency", rs1.getString(9));
						jobj.put("userID", rs1.getString(10));
						finArray.put(jobj);
					}
					outJson.put("finImpArray", finArray);

					String sql2 = "SELECT a.id, a.bench, a.counsel_of_petitioner, a.counsel_of_respondent, CONVERT(varchar, a.last_hearing_date, 103) as last_date "
							+ " , a.outcome_last_hearing, CONVERT(varchar,a.next_hearing_date, 103) , a.outcome_next_date, CONVERT(varchar, a.entry_date, 100) "
							+ " , a.entered_by, a.status, b.status_name from hearing_update a left outer join status_master b on a.status = b.id "
							+ " where case_id = ? and update_status = 1 order by a.entry_date desc, cast(a.last_hearing_date as date) desc";

					// String sql2 = "SELECT a.bench, a.counsel_of_petitioner,
					// a.counsel_of_respondent, CONVERT(varchar, a.last_hearing_date, 103), "
					// + " CONVERT(varchar,a.next_hearing_date, 103), a.outcome_last_hearing, "
					// + " a.outcome_next_date, a.user_id, a.status, a.entry_date, a.entered_by,
					// b.status_name from hearing_update a left outer join status_master b on
					// a.status = b.id where case_id = ?"
					// + " and a.entry_date IN (select MAX(b.entry_date) from hearing_update b where
					// b.case_id = ? group by b.next_hearing_date, b.last_hearing_date ) order by
					// a.last_hearing_date";
					stmt2 = con.prepareStatement(sql2);
					stmt2.setInt(1, caseID);
					// stmt2.setInt(2, caseID);
					rs2 = stmt2.executeQuery();
					while (rs2.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs2.getInt(1));
						jobj.put("bench", rs2.getString(2));
						jobj.put("counselOfCompany", rs2.getString(3));
						jobj.put("counselOfRespondent", rs2.getString(4));
						jobj.put("lastHearingDate", rs2.getString(5));
						jobj.put("outcomeLast", rs2.getString(6));
						jobj.put("nextHearingDate", rs2.getString(7));
						jobj.put("outcomeNext", rs2.getString(8));
						jobj.put("entryDate", rs2.getString(9));
						jobj.put("enteredBy", rs2.getString(10));
						jobj.put("statusID", rs2.getInt(11));
						jobj.put("statusName", rs2.getString(12));
						hearingArray.put(jobj);
					}
					outJson.put("hearingArray", hearingArray);

					String sql3 = "SELECT id, mongo_db_id, file_name, file_type, file_desc, document_type, document_type_name, source, created_by, user_name, CONVERT(varchar, timestamp, 100), CONVERT(varchar, document_date, 103), confidential, authorized_users, file_size_bytes, STUFF((SELECT distinct ', ' + t1.user_name "
							+ " from user_master t1  where  t1.payroll_no in (select s from dbo.split(',',t.authorized_users)) FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'),1,2,'') names from vw_document_details t where case_id  = ? order by cast(timestamp as datetime) desc";

					stmt3 = con.prepareStatement(sql3);
					stmt3.setInt(1, caseID);

					rs3 = stmt3.executeQuery();
					while (rs3.next()) {
						JSONObject jobj = new JSONObject();
						jobj.put("id", rs3.getInt(1));
						jobj.put("mongoID", rs3.getString(2));
						jobj.put("fileName", rs3.getString(3));
						jobj.put("fileType", rs3.getString(4));
						jobj.put("fileDesc", rs3.getString(5));
						jobj.put("docType", rs3.getString(6));
						jobj.put("docTypeName", rs3.getString(7));
						jobj.put("source", rs3.getString(8));
						jobj.put("createdBy", rs3.getString(9));
						jobj.put("userName", rs3.getString(10));
						jobj.put("timestamp", rs3.getString(11));
						jobj.put("docDate", rs3.getString(12));
						jobj.put("isConfidential", rs3.getString(13));
						jobj.put("authUsers", (rs3.getString(14) == null) ? "" : rs3.getString(14));
						final double roundOff = ((double) (rs3.getLong(15)) / 1048576);
						DecimalFormat f = new DecimalFormat("0.00");
						jobj.put("fileSize", f.format(roundOff) + "MB");
						jobj.put("authUsersName", (rs3.getString(16) == null) ? "" : rs3.getString(16));
						paperArray.put(jobj);
					}

					outJson.put("paperArray", paperArray);

				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "No details availabe for the case id :" + caseID);
				}

			} catch (SQLException e) {
				logg.error("Login Error in SQL Exception ==>" + e.getMessage());
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", e);
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
				if (rs1 != null)
					try {
						rs1.close();
						rs1 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs1 ==>" + e.getMessage());
					}
				if (stmt1 != null)
					try {
						stmt1.close();
						stmt1 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt1 ==>" + e.getMessage());
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

	public String fetchAddCaseDetails(String input, String sessionID, String sessUser) {
		String response = "";
		String caseId = "";
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		ResultSet rs1 = null;
		PreparedStatement stmt1 = null;
		ResultSet rs2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs3 = null;
		PreparedStatement stmt3 = null;
		JSONObject obj = new JSONObject(input);
		JSONObject outJson = new JSONObject();
		JSONArray finArray = new JSONArray();
		JSONArray hearingArray = new JSONArray();
		JSONArray paperArray = new JSONArray();
		String refId = (obj.has("refId")) ? obj.getString("refId") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
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
				con = SecondDBConnection.getDBConnection();
				String query = "SELECT b.foldertypename, c.jsondata, f.gstin , e.status "
						+ "FROM tbl_additionalnoticemaster a, tbl_additionalfolder b, tbl_additionalfolderitem c, "
						+ "tbl_additionalnoticestatus d, tbl_gst_status_master e, tbl_gstmaster f "
						+ "WHERE f.gstid = a.FkGSTId " + "AND d.FkAdditionalNoticeId = a.AdditionalNoticeId "
						+ "AND d.statusid = e.id " + "AND b.FkAdditionalNoticeId = a.AdditionalNoticeId "
						+ "AND c.fkfolderid = b.folderid " + "AND a.noticeorderid = ?;";

				stmt = con.prepareStatement(query);
				stmt.setString(1, refId);
				rs = stmt.executeQuery();
				JSONArray intimArray = new JSONArray();
				JSONArray noticesArray = new JSONArray();
				JSONArray repliesArray = new JSONArray();
				JSONArray ordersArray = new JSONArray();
				JSONArray reportsArray = new JSONArray();

				while (rs.next()) {
					String folderType = rs.getString(1);
					String jsonData = rs.getString(2);
					String gstin = rs.getString(3);

					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
					outJson.put("type",type);
					outJson.put("folderType", folderType);
					outJson.put("gstin", gstin);
					outJson.put("jsonData", jsonData);
					outJson.put("gstStatus",rs.getString(4));

					// Parse the main JSONArray from jsonData
					JSONArray mainJsonArray = new JSONArray(jsonData);

					for (int i = 0; i < mainJsonArray.length(); i++) {
						JSONObject currentObject = mainJsonArray.getJSONObject(i);

						JSONObject itemJson = new JSONObject(currentObject.getString("itemJson"));

						JSONObject structuredEntry = new JSONObject();
						structuredEntry.put("refId", currentObject.optString("refId"));
						structuredEntry.put("insertDate", currentObject.optString("insertDate"));

						if ("NOTICES".equalsIgnoreCase(folderType)) {
							
							if(type.equalsIgnoreCase("AUDIT")) {
							structuredEntry.put("noticeType", itemJson.optString("noticeType"));
							structuredEntry.put("arn", itemJson.optString("arn"));
							
							if (itemJson.has("inpData") && !itemJson.isNull("inpData")) {
							    JSONArray docUploadedArray = itemJson.getJSONArray("inpData");

							    List<String> fileNames = new ArrayList<>();
							    List<String> fileIds = new ArrayList<>();

							    for (int j = 0; j < docUploadedArray.length(); j++) {
							        JSONObject fileObj = docUploadedArray.getJSONObject(j);
							        fileNames.add(fileObj.getString("fileName"));
							        fileIds.add(fileObj.getString("id"));
							    }

							    String joinedFileNames = String.join(", ", fileNames);
							    String joinedFileIds = String.join(", ", fileIds);

							    structuredEntry.put("joinedFileNames", joinedFileNames);
							    structuredEntry.put("joinedFileIds", joinedFileIds);
							} else {
							    structuredEntry.put("joinedFileNames", "");
							    structuredEntry.put("joinedFileIds", "");
							}

							
							JSONObject todtlsObject = itemJson.getJSONObject("todtls");
							structuredEntry.put("issuedBy",todtlsObject.optString("nm"));

							noticesArray.put(structuredEntry);
							}
							else {
								JSONObject sdtls = itemJson.optJSONObject("sdtls");
								if (sdtls != null && sdtls.has("dtscn")) {
								    JSONObject dtscn = sdtls.optJSONObject("dtscn");
								    structuredEntry.put("type", dtscn.optString("type", ""));
								    structuredEntry.put("facts", dtscn.optString("facts", ""));
								    structuredEntry.put("sec", dtscn.optString("sec", ""));
								    structuredEntry.put("grounds", dtscn.optString("grounds", ""));

								    List<String> docNames = new ArrayList<>();
								    List<String> docIds = new ArrayList<>();

								    if (dtscn.has("suppdocs") && !dtscn.isNull("suppdocs")) {
								        JSONArray suppDocs = dtscn.getJSONArray("suppdocs");
								        for (int k = 0; k < suppDocs.length(); k++) {
								            JSONObject dcupdtls = suppDocs.getJSONObject(k).optJSONObject("dcupdtls");
								            if (dcupdtls != null) {
								                docNames.add(dcupdtls.optString("docName", ""));
								                docIds.add(dcupdtls.optString("id", ""));
								            }
								        }
								    }

								    if (dtscn.has("maindocs") && !dtscn.isNull("maindocs")) {
								        JSONArray mainDocs = dtscn.getJSONArray("maindocs");
								        for (int l = 0; l < mainDocs.length(); l++) {
								            JSONObject dcupdtls = mainDocs.getJSONObject(l).optJSONObject("dcupdtls");
								            if (dcupdtls != null) {
								                docNames.add(dcupdtls.optString("docName", ""));
								                docIds.add(dcupdtls.optString("id", ""));
								            }
								        }
								    }

								    String joinedDocNames = String.join(", ", docNames);
								    String joinedDocIds = String.join(", ", docIds);

								    structuredEntry.put("joinedFileNames", joinedDocNames);
								    structuredEntry.put("joinedFileIds", joinedDocIds);
								}

							}

						} else if ("REPLIES".equalsIgnoreCase(folderType)) {
							if(type.equalsIgnoreCase("AUDIT")) {				
							structuredEntry.put("replyType", itemJson.optString("replyType"));
							structuredEntry.put("noticeReferenceNumber", itemJson.optString("noticeReferenceNumber"));
							structuredEntry.put("replyDate", itemJson.optString("replyDate"));
							structuredEntry.put("noticeDueDate", itemJson.optString("noticeDueDate"));
							repliesArray.put(structuredEntry);
							}
							else{
								JSONObject reply = itemJson.optJSONObject("reply");
								if (reply != null) {
								    structuredEntry.put("replyty", reply.optString("replyty", ""));
								    structuredEntry.put("ntcdt", reply.optString("ntcdt", ""));
								    structuredEntry.put("ntcno", reply.optString("ntcno", ""));

								    List<String> docNames = new ArrayList<>();
								    List<String> docIds = new ArrayList<>();
								    if (reply.has("suppdocs") && !reply.isNull("suppdocs")) {
								        JSONArray suppDocs = reply.getJSONArray("suppdocs");
								        for (int m = 0; m < suppDocs.length(); m++) {
								            JSONObject dcupdtls = suppDocs.getJSONObject(m).optJSONObject("dcupdtls");
								            if (dcupdtls != null) {
								                docNames.add(dcupdtls.optString("docName", ""));
								                docIds.add(dcupdtls.optString("id", ""));
								            }
								        }
								    }
								    if (reply.has("maindocs") && !reply.isNull("maindocs")) {
								        JSONArray mainDocs = reply.getJSONArray("maindocs");
								        for (int n = 0; n < mainDocs.length(); n++) {
								            JSONObject dcupdtls = mainDocs.getJSONObject(n).optJSONObject("dcupdtls");
								            if (dcupdtls != null) {
								                docNames.add(dcupdtls.optString("docName", ""));
								                docIds.add(dcupdtls.optString("id", ""));
								            }
								        }
								    }
								    String joinedFileNames = String.join(", ", docNames);
								    String joinedFileIds = String.join(", ", docIds);

								    structuredEntry.put("joinedFileNames", joinedFileNames);
								    structuredEntry.put("joinedFileIds", joinedFileIds);
								}	
							}

						} else if ("ORDERS".equalsIgnoreCase(folderType)) {
							structuredEntry.put("arn", itemJson.optString("arn"));
							structuredEntry.put("arndt", itemJson.optString("arndt"));
							structuredEntry.put("gstin", itemJson.optString("gstin"));
							structuredEntry.put("refid", itemJson.optString("refid"));

							JSONObject sdtls = itemJson.optJSONObject("sdtls");
							if (sdtls != null && sdtls.has("order")) {
								JSONObject order = sdtls.getJSONObject("order");
								structuredEntry.put("orderType", order.optString("type"));
								structuredEntry.put("orderDate", order.optString("orderdt"));
								structuredEntry.put("orderPassedBy", order.optString("orderPassedBy"));
								structuredEntry.put("orderContent", order.optString("orderContent"));
							}

							ordersArray.put(structuredEntry);
						}
						
						 else if ("REPORT".equalsIgnoreCase(folderType)) {
								structuredEntry.put("noticeType", itemJson.optString("noticeType"));
								structuredEntry.put("referenceNumber", itemJson.optString("referenceNumber"));
								structuredEntry.put("noticeDate", itemJson.optString("noticeDate"));
								structuredEntry.put("caseID", itemJson.optString("caseID"));
								structuredEntry.put("noticeDueDate", itemJson.optString("noticeDueDate"));
								structuredEntry.put("arn", itemJson.optString("arn"));

								
								if (itemJson.has("inpData") && !itemJson.isNull("inpData")) {
								    JSONArray docUploadedArray = itemJson.getJSONArray("inpData");

								    List<String> fileNames = new ArrayList<>();
								    List<String> fileIds = new ArrayList<>();

								    for (int j = 0; j < docUploadedArray.length(); j++) {
								        JSONObject fileObj = docUploadedArray.getJSONObject(j);
								        fileNames.add(fileObj.getString("fileName"));
								        fileIds.add(fileObj.getString("id"));
								    }

								    String joinedFileNames = String.join(", ", fileNames);
								    String joinedFileIds = String.join(", ", fileIds);

								    structuredEntry.put("joinedFileNames", joinedFileNames);
								    structuredEntry.put("joinedFileIds", joinedFileIds);
								} else {
								    structuredEntry.put("joinedFileNames", "");
								    structuredEntry.put("joinedFileIds", "");
								}
								
								reportsArray.put(structuredEntry);
							}	
					}
				}

				// After loop completion, put all arrays in final output JSON
				if (noticesArray.length() > 0)
					outJson.put("noticesArray", noticesArray);

				if (repliesArray.length() > 0)
					outJson.put("repliesArray", repliesArray);

				if (ordersArray.length() > 0)
					outJson.put("ordersArray", ordersArray);
				outJson.put("intimationArray",intimArray);
				outJson.put("reportsArray",reportsArray);

			} catch (SQLException e) {
				logg.error("Login Error in SQL Exception ==>" + e.getMessage());
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", e);
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
				if (rs1 != null)
					try {
						rs1.close();
						rs1 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - rs1 ==>" + e.getMessage());
					}
				if (stmt1 != null)
					try {
						stmt1.close();
						stmt1 = null;
					} catch (Exception e) {
						logg.error("Login Error in Finally - stmt1 ==>" + e.getMessage());
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
