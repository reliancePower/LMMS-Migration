
package com.lawsuittracker.pvIntegration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;

public class PVManageCases implements java.io.Serializable {
	public static final Logger logger = Logger.getLogger(PVManageAlerts.class);

	private static final long serialVersionUID = 1L;

	public void registerCaseWithPV(String input) {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject obj = new JSONObject(input);
		JSONObject outJson = new JSONObject();
		int forum = obj.has("forum") ? obj.getInt("forum") : 10000000;
		int caseType = obj.has("courtCaseType") ? obj.getInt("courtCaseType") : 100000000;
		int caseYear = obj.has("caseYear") ? obj.getInt("caseYear") : 0;
		String caseNo = obj.has("caseNo") ? obj.getString("caseNo") : "";
		int id = obj.has("id") ? obj.getInt("id") : 0;
		JSONObject json = new JSONObject();
		JSONObject subjson = new JSONObject();
		String out = "";
		HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);
		DefaultHttpClient client = new DefaultHttpClient();
		try {

			String url = "https://provakil.com/synapse/usercases";
			System.out.println(url);
			client.getParams().setParameter("http.route.default-proxy", proxy);
			HttpPost request = new HttpPost(url);

			request.addHeader("Accept", "application/json");
			request.addHeader("Authorization",
					"Bearer 3Yf2JJkodBY526LHPOkzi+kD/fwwqgWe25vsWr+ZlV/HFe7DkOGibkmBNdVPma2h");

			subjson.put("forum", forum);
			subjson.put("case_type", caseType);
			subjson.put("case_id", caseNo);
			subjson.put("case_year", caseYear);
			json.put("external_id", id);
			json.put("case_num", subjson);
			System.out.println(json.toString());
			System.out.println("Registering Case With PV.....");
			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			request.setEntity(se);

			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP Error code : " + response.getStatusLine().getStatusCode());
			}
			InputStreamReader in = new InputStreamReader(response.getEntity().getContent());
			// = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(in);
			StringBuilder sb = new StringBuilder();
			while ((out = br.readLine()) != null) {
				System.out.println(out);
				sb.append(out);
				System.out.println("Registering Case With CIS");

			}
			in.close();
			JSONObject jobj = new JSONObject(sb.toString());
			int extID = jobj.getInt("external_id");
			String pvID = jobj.getString("id");

			try {

				con = SQLDBConnection.getDBConnection();

				String query = "update case_master set pv_id = ? where id = ?";

				stmt = con.prepareStatement(query);
				stmt.setString(1, pvID);
				stmt.setInt(2, extID);
				int result = stmt.executeUpdate();
				if (result > 0) {
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				} else {
					outJson.put(LawSuitTrackerConstants.status, "F");
					outJson.put("msg", "No cases available for the case id ");
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

	public String fetchAllCasesFromPV(String sessionID, String sessUser) {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject outJson = new JSONObject();
		String out = "";
		HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			for (int j = 0; j < 3; j++) {
				int k = j + 1;

				String url = "https://provakil.com/synapse/usercases?page=" + k;
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
						JSONObject obj = arr.getJSONObject(i);
						String pvID = obj.has("usercase_id") ? obj.getString("usercase_id") : "";
						String iden = obj.has("external_id") ? obj.getString("external_id") : "";

						if (!(pvID.equalsIgnoreCase(iden))) {
							if (iden.contains(",")) {
								String parts[] = iden.split("\\,");
								System.out.print(parts.length);

								for (int l = 0; l < parts.length; l++) {
									int id = Integer.parseInt(parts[l].trim());
									JSONArray caseArray = obj.has("case_nums") ? obj.getJSONArray("case_nums") : null;
									String caseID = "";
									int caseYear = 0;
									int forum = 0;
									int caseType = 0;

									System.out.println(caseArray.length());
									if ((caseArray != null) && (caseArray.length() > 1)) {
										JSONObject lastObj = caseArray.getJSONObject(caseArray.length() - 1);
										caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
										caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
										forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
										caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
									} else {
										JSONObject lastObj = caseArray.getJSONObject(0);
										caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
										caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
										forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
										caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
									}
									String query = "update case_master set forum_id = ?, case_type_num = ?, case_id = ?, case_year = ?, pv_id = ? where id = ?";

									stmt = con.prepareStatement(query);
									stmt.setInt(1, forum);
									stmt.setInt(2, caseType);
									stmt.setString(3, caseID);
									stmt.setInt(4, caseYear);
									stmt.setString(5, pvID);
									stmt.setInt(6, id);

									int result = stmt.executeUpdate();
									if (result > 0) {
										System.out.println("success for case ID == > " + id);
										outJson.put(LawSuitTrackerConstants.status, "T");
										outJson.put("msg", "Success");
									} else {
										System.out.println("failed for case id == > " + id);
										outJson.put(LawSuitTrackerConstants.status, "F");
										outJson.put("msg", "No cases available for the case id ");
									}
								}
							} else {
								int id = Integer.parseInt(iden);
								JSONArray caseArray = obj.has("case_nums") ? obj.getJSONArray("case_nums") : null;
								String caseID = "";
								int caseYear = 0;
								int forum = 0;
								int caseType = 0;

								System.out.println(caseArray.length());
								if ((caseArray != null) && (caseArray.length() > 1)) {
									JSONObject lastObj = caseArray.getJSONObject(caseArray.length() - 1);
									caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
									caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
									forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
									caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
								} else {
									JSONObject lastObj = caseArray.getJSONObject(0);
									caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
									caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
									forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
									caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
								}
								String query = "update case_master set forum_id = ?, case_type_num = ?, case_id = ?, case_year = ?, pv_id = ? where id = ?";

								stmt = con.prepareStatement(query);
								stmt.setInt(1, forum);
								stmt.setInt(2, caseType);
								stmt.setString(3, caseID);
								stmt.setInt(4, caseYear);
								stmt.setString(5, pvID);
								stmt.setInt(6, id);

								int result = stmt.executeUpdate();
								if (result > 0) {
									System.out.println("success for case ID == > " + id);
									outJson.put(LawSuitTrackerConstants.status, "T");
									outJson.put("msg", "Success");
								} else {
									System.out.println("failed for case id == > " + id);
									outJson.put(LawSuitTrackerConstants.status, "F");
									outJson.put("msg", "No cases available for the case id ");
								}
							}
						} else {
							System.out.println("failed for pv id == > " + pvID);
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

	public String fetchCaseDeatilsFromPV(String sessionID, String sessUser, String input) {
		Connection con = null;
		PreparedStatement stmt = null;
		JSONObject outJson = new JSONObject();
		JSONObject obj = new JSONObject(input);
		String pv_ID = obj.has("pvID") ? obj.getString("pvID") : "";
		String out = "";
		HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);
		DefaultHttpClient client = new DefaultHttpClient();
		try {

			String url = "https://provakil.com/synapse/usercases/" + pv_ID;
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
			// = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(in);
			StringBuilder sb = new StringBuilder();
			while ((out = br.readLine()) != null) {
				System.out.println(out);
				sb.append(out);

			}
			in.close();
			JSONObject jobj = new JSONObject(sb.toString());

			try {
				con = SQLDBConnection.getDBConnection();

				String pvID = jobj.has("usercase_id") ? jobj.getString("usercase_id") : "";
				String iden = jobj.has("external_id") ? jobj.getString("external_id") : "";

				if (!(pvID.equalsIgnoreCase(iden))) {
					if (iden.contains(",")) {
						String parts[] = iden.split("\\,");
						System.out.print(parts.length);

						for (int l = 0; l < parts.length; l++) {
							int id = Integer.parseInt(parts[l].trim());
							JSONArray caseArray = jobj.has("case_nums") ? jobj.getJSONArray("case_nums") : null;
							String caseID = "";
							int caseYear = 0;
							int forum = 0;
							int caseType = 0;

							System.out.println(caseArray.length());
							if ((caseArray != null) && (caseArray.length() > 1)) {
								JSONObject lastObj = caseArray.getJSONObject(caseArray.length() - 1);
								caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
								caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
								forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
								caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
							} else {
								JSONObject lastObj = caseArray.getJSONObject(0);
								caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
								caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
								forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
								caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
							}
							String query = "update case_master set forum_id = ?, case_type_num = ?, case_id = ?, case_year = ?, pv_id = ? where id = ?";

							stmt = con.prepareStatement(query);
							stmt.setInt(1, forum);
							stmt.setInt(2, caseType);
							stmt.setString(3, caseID);
							stmt.setInt(4, caseYear);
							stmt.setString(5, pvID);
							stmt.setInt(6, id);

							int result = stmt.executeUpdate();
							if (result > 0) {
								System.out.println("success for case ID == > " + id);
								outJson.put(LawSuitTrackerConstants.status, "T");
								outJson.put("msg", "Success");
							} else {
								System.out.println("failed for case id == > " + id);
								outJson.put(LawSuitTrackerConstants.status, "F");
								outJson.put("msg", "No cases available for the case id ");
							}
						}
					} else {
						int id = Integer.parseInt(iden);
						JSONArray caseArray = jobj.has("case_nums") ? jobj.getJSONArray("case_nums") : null;
						String caseID = "";
						int caseYear = 0;
						int forum = 0;
						int caseType = 0;

						System.out.println(caseArray.length());
						if ((caseArray != null) && (caseArray.length() > 1)) {
							JSONObject lastObj = caseArray.getJSONObject(caseArray.length() - 1);
							caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
							caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
							forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
							caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
						} else {
							JSONObject lastObj = caseArray.getJSONObject(0);
							caseID = lastObj.has("case_id") ? lastObj.getString("case_id") : "";
							caseYear = lastObj.has("case_year") ? lastObj.getInt("case_year") : 0;
							forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
							caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
						}
						String query = "update case_master set forum_id = ?, case_type_num = ?, case_id = ?, case_year = ?, pv_id = ? where id = ?";

						stmt = con.prepareStatement(query);
						stmt.setInt(1, forum);
						stmt.setInt(2, caseType);
						stmt.setString(3, caseID);
						stmt.setInt(4, caseYear);
						stmt.setString(5, pvID);
						stmt.setInt(6, id);

						int result = stmt.executeUpdate();
						if (result > 0) {
							System.out.println("success for case ID == > " + id);
							outJson.put(LawSuitTrackerConstants.status, "T");
							outJson.put("msg", "Success");
						} else {
							System.out.println("failed for case id == > " + id);
							outJson.put(LawSuitTrackerConstants.status, "F");
							outJson.put("msg", "No cases available for the case id ");
						}
					}
				} else {
					System.out.println("failed for pv id == > " + pvID);
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

}
