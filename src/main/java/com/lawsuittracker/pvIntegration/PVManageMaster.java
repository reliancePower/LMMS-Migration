package com.lawsuittracker.pvIntegration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;

public class PVManageMaster {
	public String fetchAllForums(String sessionID, String sessUser) {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject outJson = new JSONObject();
		String out = "";
		HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			for (int j = 0; j < 16; j++) {
				int k = j + 1;

				String url = "https://provakil.com/synapse/forums/types?page=" + k;
				System.out.println(url);
				client.getParams().setParameter("http.route.default-proxy", proxy);
				HttpGet request = new HttpGet(url);

				request.addHeader("Accept", "application/json");
				request.addHeader("Authorization",
						"Bearer 3Yf2JJkodBY526LHPOkzi+kD/fwwqgWe25vsWr+ZlV/HFe7DkOGibkmBNdVPma2h");

				HttpResponse response = client.execute(request);

				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException(
							"Failed : HTTP Error code : " + response.getStatusLine().getStatusCode());
				}
				InputStreamReader in = new InputStreamReader(response.getEntity().getContent());
				// = new InputStreamReader(conn.getInputStream());
				BufferedReader br = new BufferedReader(in);
				StringBuilder sb = new StringBuilder();
				while ((out = br.readLine()) != null) {
					// System.out.println(out);
					sb.append(out);

				}
				in.close();
				JSONObject jobj = new JSONObject(sb.toString());
				JSONArray arr = jobj.getJSONArray("results");

				try {

					con = SQLDBConnection.getDBConnection();
					for (int i = 0; i < arr.length(); i++) {
						String query = "insert into pv_forum_master(id, forum_category, full_name, short_name, forum_group, sort_key, forum_dir) values(?, ?, ?, ?, ?, ?, ?)";
						JSONObject obj = arr.getJSONObject(i);
						stmt = con.prepareStatement(query);
						stmt.setInt(1, obj.getInt("index"));
						stmt.setString(2, (obj.has("forum_category")) ? (obj.getString("forum_category")) : "others");
						stmt.setString(3, obj.getString("full_name"));
						stmt.setString(4, obj.getString("short_name"));
						stmt.setString(5, (obj.has("forum_group")) ? (obj.getString("forum_group")) : "");
						stmt.setInt(6, (obj.has("sort_key")) ? (obj.getInt("sort_key")) : 00);
						stmt.setString(7, (obj.has("forum_dir")) ? (obj.getString("forum_dir")) : "others");
						int result = stmt.executeUpdate();
						if (result > 0) {
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put(LawSuitTrackerConstants.status, "F");
							outJson.put("msg", "No cases available for the case id ");
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
			}

			client.getConnectionManager().shutdown();

		} catch (Exception e) {
			System.out.println("Exception in NetClientGet:- " + e);
		}
		return outJson.toString(4);
	}

	public String fetchAllCaseTypes(String sessionID, String sessUser) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		JSONObject outJson = new JSONObject();
		String out = "";
		HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);

		try {
			con = SQLDBConnection.getDBConnection();
			String query = "select id from pv_forum_master";
			stmt = con.prepareStatement(query);

			rs = stmt.executeQuery();
			while (rs.next()) {
				int forumID = rs.getInt(1);
				try {
					DefaultHttpClient client = new DefaultHttpClient();
					String url = "https://provakil.com/synapse/case_types/types?forum=" + forumID;
					System.out.println(url);
					client.getParams().setParameter("http.route.default-proxy", proxy);
					HttpGet request = new HttpGet(url);

					request.addHeader("Accept", "application/json");
					request.addHeader("Authorization",
							"Bearer 3Yf2JJkodBY526LHPOkzi+kD/fwwqgWe25vsWr+ZlV/HFe7DkOGibkmBNdVPma2h");

					HttpResponse response = client.execute(request);

					if (response.getStatusLine().getStatusCode() != 200) {
						throw new RuntimeException(
								"Failed : HTTP Error code : " + response.getStatusLine().getStatusCode());
					}
					InputStreamReader in = new InputStreamReader(response.getEntity().getContent());
					// = new InputStreamReader(conn.getInputStream());
					BufferedReader br = new BufferedReader(in);
					StringBuilder sb = new StringBuilder();
					while ((out = br.readLine()) != null) {
						// System.out.println(out);
						sb.append(out);

					}
					in.close();

					// JSONObject jobj = new JSONObject(sb.toString());
					JSONArray arr = new JSONArray(sb.toString());
					for (int i = 0; i < arr.length(); i++) {
						String query1 = "insert into pv_case_type_master(case_type_id,  forum_id, case_type_name, sort_key) values(?, ?, ?, ?)";
						JSONObject obj = arr.getJSONObject(i);
						stmt = con.prepareStatement(query1);
						stmt.setInt(1, obj.getInt("index"));
						stmt.setInt(2, forumID);
						stmt.setString(3, obj.getString("name"));
						stmt.setInt(4, obj.has("sort_key") ? obj.getInt("sort_key") : 0);

						int result = stmt.executeUpdate();
						if (result > 0) {
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							outJson.put(LawSuitTrackerConstants.status, "F");
							outJson.put("msg", "No cases available for the case id ");
						}
					}
					client.getConnectionManager().shutdown();
				} catch (Exception e) {
					System.out.println("Exception in NetClientGet:- " + e);
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

		return outJson.toString(4);
	}

	public String fetchNewForums(String sessionID, String sessUser) {
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
							fetchNewCaseTypes(forumID);
							updateLog(forumID, rs.getString(1), rs.getString(2), "Case Type", "Update", "Manual");
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
							updateLog(forumID, obj1.getString("full_name"),
									(obj1.has("forum_category")) ? (obj1.getString("forum_category")) : "others",
									"Forum", "New", "Manual");
							fetchNewCaseTypes(obj1.getInt("index"));
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
		return outJson.toString(4);
	}

	public void fetchNewCaseTypes(int forumID) {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject outJson = new JSONObject();
		String out = "";
		HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			String url = "https://provakil.com/synapse/case_types/types?forum=" + forumID;
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
			try {
				con = SQLDBConnection.getDBConnection();

				JSONArray arr = new JSONArray(sb.toString());
				System.out.println(arr);
				for (int i = 0; i < arr.length(); i++) {
					String query1 = "insert into pv_case_type_master(case_type_id,  forum_id, case_type_name, sort_key) values(?, ?, ?, ?)";
					JSONObject obj = arr.getJSONObject(i);
					stmt = con.prepareStatement(query1);
					stmt.setInt(1, obj.getInt("index"));
					stmt.setInt(2, forumID);
					stmt.setString(3, obj.getString("name"));
					stmt.setInt(4, obj.has("sort_key") ? obj.getInt("sort_key") : 0);

					int result = stmt.executeUpdate();
					if (result > 0) {
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");
					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "No cases available for the case id ");
					}
				}
				client.getConnectionManager().shutdown();
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

		} catch (Exception e) {
			System.out.println("Exception in NetClientGet:- " + e);
		}

	}

	public void updateLog(int forumID, String forumName, String category, String item, String action,
			String executionType) {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = SQLDBConnection.getDBConnection();

			String query1 = "insert into pv_forum_casetype_update_log(forum_id, Category, Forum_Name, Updated_Item, action, execution_type) values(?, ?, ?, ?, ?, ?)";

			stmt = con.prepareStatement(query1);

			stmt.setInt(1, forumID);
			stmt.setString(2, category);
			stmt.setString(3, forumName);
			stmt.setString(4, item);
			stmt.setString(5, action);
			stmt.setString(6, executionType);
			int result = stmt.executeUpdate();
			if (result > 0) {
				System.out.println("log updated");
			} else {
				System.out.println("log not updated");
			}

		} catch (SQLException e) {
			System.out.println("There seems some error at server side. Please try again later!!!");
			e.printStackTrace();

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
	}

	

}
