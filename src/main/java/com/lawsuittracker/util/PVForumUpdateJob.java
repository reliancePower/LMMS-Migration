package com.lawsuittracker.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.pvIntegration.PVManageMaster;

public class PVForumUpdateJob implements Job {
	PVManageMaster mm = new PVManageMaster();
	SendMail sm = new SendMail();

	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("LMMS Job executing sucessfully..." + new Date());

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;

		JSONObject outJson = new JSONObject();
		String out = "";
		HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);
		DefaultHttpClient client = new DefaultHttpClient();
		try {

			String url = "https://provakil.com/synapse/forums/types?only_new=true";
			System.out.println(url);
			client.getParams().setParameter("http.route.default-proxy", proxy);
			HttpGet request = new HttpGet(url);

			request.addHeader("Accept", "application/json");
			request.addHeader("Authorization",
					"Bearer 3Yf2JJkodBY526LHPOkzi+kD/fwwqgWe25vsWr+ZlV/HFe7DkOGibkmBNdVPma2h");

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP Error code : " + response.getStatusLine().getStatusCode());
			}
			InputStreamReader in = new InputStreamReader(response.getEntity().getContent());
			BufferedReader br = new BufferedReader(in);
			StringBuilder sb = new StringBuilder();
			while ((out = br.readLine()) != null) {
				// System.out.println(out);
				sb.append(out);
			}
			in.close();
			JSONArray arr = new JSONArray(sb.toString());

			try {

				con = SQLDBConnection.getDBConnection();
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					int forumID = obj.getInt("index");
					String query = "select full_name, forum_category from pv_forum_master where id = ?";
					stmt = con.prepareStatement(query);
					stmt.setInt(1, forumID);
					rs = stmt.executeQuery();
					if (rs.next()) {
						String query1 = "delete from pv_case_type_master where forum_id = ?";
						stmt1 = con.prepareStatement(query1);
						stmt1.setInt(1, forumID);
						int result = stmt1.executeUpdate();
						if (result > 0) {

							mm.fetchNewCaseTypes(forumID);
							mm.updateLog(forumID, rs.getString(1), rs.getString(2), "Case Type", "Update", "Manual");
							String sub = "LMMS App - Job Update on Case Types";
							String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear Admin, </p>	<p> The case types have been added or modified for some of the forums. <br><br><p>Please click <a href= \"https://mportal.reliancepower.co.in/LMMS/\">here</a> to view the updates</p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
							System.out.println(template);
							sm.sendAMail(LawSuitTrackerConstants.adminMail, sub, template, "",
									LawSuitTrackerConstants.devMail);
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put(LawSuitTrackerConstants.status, "F");
							outJson.put("msg", "No cases available for the case id ");
						}

					} else {
						String query1 = "insert into pv_forum_master(id, forum_category, full_name, short_name, forum_group, sort_key, forum_dir) values(?, ?, ?, ?, ?, ?, ?)";
						JSONObject obj1 = arr.getJSONObject(i);
						stmt1 = con.prepareStatement(query1);
						stmt1.setInt(1, obj1.getInt("index"));
						stmt1.setString(2,
								(obj1.has("forum_category")) ? (obj1.getString("forum_category")) : "others");
						stmt1.setString(3, obj1.getString("full_name"));
						stmt1.setString(4, obj1.getString("short_name"));
						stmt1.setString(5, (obj1.has("forum_group")) ? (obj1.getString("forum_group")) : "");
						stmt1.setInt(6, (obj1.has("sort_key")) ? (obj1.getInt("sort_key")) : 00);
						stmt1.setString(7, (obj1.has("forum_dir")) ? (obj1.getString("forum_dir")) : "others");
						int result = stmt1.executeUpdate();
						if (result > 0) {
							mm.updateLog(forumID, obj1.getString("full_name"),
									(obj1.has("forum_category")) ? (obj1.getString("forum_category")) : "others",
									"Forum", "New", "Job");
							mm.fetchNewCaseTypes(obj1.getInt("index"));

							String sub1 = "LMMS App - Job Update on New Forums";
							String template1 = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear Admin, </p>	<p> The new forums have been added. <br><br><p>Please click <a href= \"https://mportal.reliancepower.co.in/LMMS/\">here</a> to view the updates</p><p><strong>Regards,</strong></p><p>LMMS admin Team</p><br><br><p><b>Note : </b>This is system generated mail. Hence do not reply</p></body></html>";
							System.out.println(template1);
							sm.sendAMail(LawSuitTrackerConstants.adminMail, sub1, template1, "",
									LawSuitTrackerConstants.devMail);
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put(LawSuitTrackerConstants.status, "F");
							outJson.put("msg", "No cases available for the case id ");
						}
					}

				}
			} catch (SQLException e) {
				System.out.println("error");
				e.printStackTrace();
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", "There seems some error at server side. Please try again later!!!");
			} finally {

				if (stmt != null)
					try {
						stmt.close();
						stmt = null;
					} catch (Exception e) {
						// logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (con != null)
					try {
						con.close();
						con = null;
					} catch (Exception e) {
						// logg.error("Login Error in Finally - con ==>" + e.getMessage());
					}
			}

			client.getConnectionManager().shutdown();

		} catch (Exception e) {
			System.out.println("Exception in NetClientGet:- " + e);
		}

	}

}
