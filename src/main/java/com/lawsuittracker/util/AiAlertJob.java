package com.lawsuittracker.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.lawsuittracker.dao.SQLDBConnection;

public class AiAlertJob implements Job {

	SendMail sm = new SendMail();

	public AiAlertJob() {
		System.out.println(">>> AiAlertJob constructor called at " + new Date());
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("AiAdvisoryMailJob executing successfully " + new Date());

		Connection con = null;
		PreparedStatement distinctCaseStmt = null;
		PreparedStatement caseViewStmt = null;
		PreparedStatement advisoryStmt = null;
		PreparedStatement updateFlagStmt = null;
		ResultSet rsCaseIds = null;
		ResultSet rsCaseView = null;
		ResultSet rsAdvisory = null;

		try {
			con = SQLDBConnection.getDBConnection();

			// 1️⃣ Get distinct case_ids to process
			String distinctCaseSql = "SELECT DISTINCT TOP 1 case_id " + "FROM ai_advisory " + "WHERE mail_sent_flag = 0 "
					+ "  AND confirm_flag = 1";
			distinctCaseStmt = con.prepareStatement(distinctCaseSql);
			rsCaseIds = distinctCaseStmt.executeQuery();

			// Prepare other statements once
			String caseViewSql = "SELECT id, company_name, business_name, vertical_name, case_no, "
					+ "       petitioner, respondent, forum, bench, aor_of_company, aor_of_respondent, "
					+ "       counsel_of_company, counsel_of_respondent, "
					+ "       CONVERT(varchar, last_hearing_date, 103) AS last_hearing_date_str, "
					+ "       CONVERT(varchar, next_hearing_date, 103) AS next_hearing_date_str, "
					+ "       further_dates, business_rep, legal_team_rep, sub_matter, brief_facts, "
					+ "       interim_prayer, final_prayer, outcome_last_hearing, "
					+ "       likeli_outcome_next_hearing, finanicial_impact, category_name, "
					+ "       entry_date, entered_by, user_id, ref_no, company_id, company_alias, "
					+ "       business_id, category_id, status_id, vertical_id, status_name, "
					+ "       case_type, case_type_id, assessment_year, financial_year, "
					+ "       amount_disallowence, state, vertical_alias, next_hearing_date, "
					+ "       forum_category, forum_id, case_type_num, case_id, case_year, pv_id, "
					+ "       state_id, case_type_name, "
					+ "       CASE WHEN next_hearing_date <= CAST(GETDATE() AS date) THEN 'T' ELSE 'F' END AS is_past_hearing, "
					+ "       asset_account_id, substitution_filed, substitution_allowed, interim_stay, "
					+ "       stay_order_info, asset_segment, trust_name, account_name, "
					+ "       vertical_alias AS vertical_alias2, petitioners, respondents, "
					+ "       financial_impact_recurring, financial_impact_recurring_deuration, additional_comments "
					+ "FROM vw_case_view_master " + "WHERE id = ?";

			caseViewStmt = con.prepareStatement(caseViewSql);

			String advisorySql = "SELECT case_id, subject, description, "
					+ "       FORMAT(hearing_date, 'MMM dd, yyyy') AS formatted_hearing_date " + "FROM ai_advisory "
					+ "WHERE case_id = ? " + "ORDER BY hearing_date DESC";
			advisoryStmt = con.prepareStatement(advisorySql);

			String updateFlagSql = "UPDATE ai_advisory " + "SET mail_sent_flag = 1 " + "WHERE case_id = ? "
					+ "  AND mail_sent_flag = 0 " + "  AND confirm_flag = 1";
			updateFlagStmt = con.prepareStatement(updateFlagSql);

			int processedCases = 0;

			// 2️⃣ Loop on each distinct case_id
			while (rsCaseIds.next()) {
				long caseId = rsCaseIds.getLong("case_id");
				System.out.println("Processing case_id: " + caseId);

				// 2a. Fetch case master data
				caseViewStmt.setLong(1, caseId);
				rsCaseView = caseViewStmt.executeQuery();

				if (!rsCaseView.next()) {
					System.out.println("No vw_case_view_master row found for case_id: " + caseId);
					continue;
				}

				// Minimal fields for mail header – use more if needed
				String companyName = rsCaseView.getString("company_name");
				String businessName = rsCaseView.getString("business_name");
				String verticalName = rsCaseView.getString("vertical_name");
				String caseType = rsCaseView.getString("case_type_id");
				String caseNo = rsCaseView.getString("case_no");
				String caseYear = rsCaseView.getString("case_year");
				String caseNoYear = caseNo + "/" + caseYear;
				String forum = rsCaseView.getString("forum");
				String petitioner = rsCaseView.getString("petitioner");
				String respondent = rsCaseView.getString("respondent");
				String lastHearing = rsCaseView.getString("last_hearing_date_str");
				String nextHearing = rsCaseView.getString("next_hearing_date_str");
				String businessRep = rsCaseView.getString("business_rep");
				String legalTeamRep = rsCaseView.getString("legal_team_rep");
				String briefFacts = rsCaseView.getString("brief_facts");

				int companyId = rsCaseView.getInt("company_id");
				int businessId = rsCaseView.getInt("business_id");
				int verticalId = rsCaseView.getInt("vertical_id");

				advisoryStmt.setLong(1, caseId);
				rsAdvisory = advisoryStmt.executeQuery();

				StringBuilder body = new StringBuilder();
				body.append("<html><body style=\"font-family:Arial; font-size:14px;\">");
				body.append("<p>Dear Sir/Madam,</p>");
				body.append(
					    "<p>Please find the AI Analysis based on the LMMS data for the case mentioned in the subject.<br/>"
					  + "(LMMS Master Data has been added at the bottom for reference).</p>"
					);

					body.append(
					    "<p>Please revert to <b><a href=\"mailto:rpower.appsupport@reliancegroupindia.com\" "
					  + "style=\"color:blue; text-decoration:none;\">rpower.appsupport@reliancegroupindia.com</a></b> "
					  + "in case of any discrepancy or suggestions for necessary fine tuning.</p>"
					);


				body.append("<p><strong>AI-powered case insights complemented by precedent analysis :</strong></p>");
				body.append(
						"<table border='1' cellspacing='0' cellpadding='5' style='border-collapse:collapse;width:100%; font-family:Arial; font-size:14px;'>");
				body.append("<tr>")
//                        .append("<th align='left'>Hearing Date</th>")
						.append("<th align='left'>Arguments</th>")
						.append("<th align='left'>Supporting Information</th>").append("</tr>");

				boolean hasAdvisory = false;
				while (rsAdvisory.next()) {
					hasAdvisory = true;
//                    String formattedDate = rsAdvisory.getString("formatted_hearing_date");
					String subject = rsAdvisory.getString("subject");
					String description = rsAdvisory.getString("description");

					body.append("<tr>");
//                    body.append("<td>").append(nullSafe(formattedDate)).append("</td>");
					body.append("<td>").append(nullSafe(subject)).append("</td>");
					body.append("<td>").append(nullSafe(description)).append("</td>");
					body.append("</tr>");
				}
				body.append("</table>");

				if (!hasAdvisory) {
					body.append("<p>No advisory records found for this case.</p>");
				}

				body.append("<br/><br/>");
				body.append("<p><strong>LMMS Master Data :</strong></p>");
				body.append("<table border='1' cellspacing='0' cellpadding='5' style='border-collapse:collapse; font-family:Arial; font-size:14px;'>");
				body.append("<tr><th align='left'>Petitioner</th><td>").append(nullSafe(petitioner))
						.append("</td></tr>");
				body.append("<tr><th align='left'>Respondent</th><td>").append(nullSafe(respondent))
						.append("</td></tr>");
				body.append("<tr><th align='left'>Brief Facts</th><td>").append(nullSafe(briefFacts))
						.append("</td></tr>");
				body.append("<tr><th align='left'>Last Hearing Date</th><td>").append(nullSafe(lastHearing))
						.append("</td></tr>");
				body.append("<tr><th align='left'>Next Hearing Date</th><td>").append(nullSafe(nextHearing))
						.append("</td></tr>");
				body.append("<tr><th align='left'>Business Rep</th><td>").append(nullSafe(businessRep))
						.append("</td></tr>");
				body.append("<tr><th align='left'>Legal Team Rep</th><td>").append(nullSafe(legalTeamRep))
						.append("</td></tr>");
				body.append("</table><br/>");

				body.append("<br/><br/>");

				body.append("<div style=\"font-family:Arial; font-size:14px; white-space:pre-wrap; line-height:1.3;\">");
				body.append("*******************************************************************\n");
				body.append("<span style='color:red; font-weight:bold;'>Disclaimer:</span>\n");
				body.append("This advisory has been generated using AI-based analysis of the information available in the LMMS system and external public sources. It is intended solely to assist in preliminary understanding, provide decision support, and present alternative viewpoints. It does not constitute legal advice, professional opinion, or a substitute for expert consultation.\n\n");
				body.append("While efforts have been made to ensure accuracy and relevance, the output may not always reflect complete facts or the correct legal/technical interpretation. AI systems may interpret data differently or miss case-specific legal or technical nuances. Users are therefore advised to independently verify the information and exercise their own judgement before taking any decisions or actions based on this advisory.\n");
				body.append("*******************************************************************");
				body.append("</div>");


				body.append(
						"<br/><p><a href=\"https://mportal.reliancepower.co.in/LMMS/\">Click here</a> to view the case in LMMS.</p>");
				body.append("<br/><p><strong>Regards,</strong><br/>LMMS Admin Team</p>");
				body.append("<br/><p><b>Note:</b> This is a system generated mail. Please do not reply.</p>");
				body.append("</body></html>");

				// 2d. Decide recipients – adjust as per your requirement
				String subjectLine = "LMMS - AI Advisory Update | Case Id: " + caseId + " | Case No: " + caseNoYear
						+ " | " + verticalName + " | " + forum;

				// get to emails
				PreparedStatement ps = con.prepareStatement(
					    "SELECT DISTINCT email_id " +
					    "FROM user_master " +
					    "WHERE status = 1 and id IN ( " +
					    "    SELECT DISTINCT recipient_id " +
					    "    FROM new_case_alert_users " +
					    "    WHERE notifications_all = 1 " +
					    "      AND (company_id = 0 OR company_id = ?) " +
					    "      AND (vertical_id = 0 OR vertical_id = ?) " +
					    "      AND (business_id = 0 OR business_id = ?) " +
					    "      AND ? IN (SELECT s FROM Split(',', case_category_id)) " +
					    ") " +
					    "OR id IN ( " +
					    "    SELECT notification_recipient_id " +
					    "    FROM case_master " +
					    "    WHERE id = ? " +
					    "      AND flag = 1 " +
					    ")"
					);


				ps.setInt(1, companyId);
				ps.setInt(2, verticalId);
				ps.setInt(3, businessId);
				ps.setString(4, caseType);
				ps.setLong(5, caseId);

				ResultSet rs = ps.executeQuery();

				StringBuilder emails = new StringBuilder();

				while (rs.next()) {
					String email = rs.getString("email_id");
					if (email != null && !email.trim().isEmpty()) {
						emails.append(email.trim()).append(",");
					}
				}

				String to = emails.length() > 0 ? emails.substring(0, emails.length() - 1) : "";

				System.out.println("Final TO List :  " + to);

				try {
					sm.sendAMail(to, subjectLine, body.toString(), "", LawSuitTrackerConstants.devMail);
					//sm.sendAMail("gaurav.kanojiya@reliancegroupindia.com", subjectLine, body.toString(), "", "gaurav.kanojiya@reliancegroupindia.com");

					// 2e. Mark mail_sent_flag = 1 for this case_id
					updateFlagStmt.setLong(1, caseId);
					int updatedRows = updateFlagStmt.executeUpdate();
					System.out.println("Updated mail_sent_flag for case_id " + caseId + " rows: " + updatedRows);

					processedCases++;
				} catch (Exception mailEx) {
					System.out.println("Error sending advisory mail for case_id: " + caseId);
					mailEx.printStackTrace();
				}

				// close these per iteration
				closeQuietly(rsCaseView);
				closeQuietly(rsAdvisory);
			}

			System.out.println("AiAdvisoryMailJob completed. Total cases processed: " + processedCases);

		} catch (Exception e) {
			System.out.println("Error in AiAdvisoryMailJob: ");
			e.printStackTrace();
		} finally {
			closeQuietly(rsAdvisory);
			closeQuietly(rsCaseView);
			closeQuietly(rsCaseIds);
			closeQuietly(distinctCaseStmt);
			closeQuietly(caseViewStmt);
			closeQuietly(advisoryStmt);
			closeQuietly(updateFlagStmt);
			closeQuietly(con);
		}
	}

	private static String nullSafe(String val) {
		return (val == null) ? "" : val;
	}

	private static void closeQuietly(AutoCloseable ac) {
		if (ac != null) {
			try {
				ac.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
}
