package com.lawsuittracker.pvIntegration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.log4j.Logger;
//import org.apache.tomcat.util.net.SSLContext;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.MongoDBConnection;
import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.lawsuittracker.util.SendMail;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

public class PVManageAlerts implements java.io.Serializable {
	public static final Logger logger = Logger.getLogger(PVManageAlerts.class);

	private static final long serialVersionUID = 1L;
	SendMail sm = new SendMail();

	public String doPVOperations(String input, String sessionID, String sessUser) {
		String response = "";
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		Connection con = null;
		JSONObject obj = new JSONObject(input);
		JSONObject outJson = new JSONObject();
		String type = obj.has("type") ? obj.getString("type") : "";
		String pvID = obj.has("usercase") ? obj.getString("usercase") : "";
		String lmmsCaseID = obj.has("external_id") ? obj.getString("external_id") : "";
		if ("".equals(pvID))
			pvID = obj.has("id") ? obj.getString("id") : "";
		JSONArray shouldHaveKey = obj.has("should_have") ? obj.getJSONArray("should_have") : null;
		JSONArray shouldNotHaveKey = obj.has("should_not_have") ? obj.getJSONArray("should_not_have") : null;
		String link = obj.has("link") ? obj.getString("link") : "";
		String notificationType = obj.has("notification_type") ? obj.getString("notification_type") : "";

		String formattedNotificationType = StringUtils.capitalize(notificationType.replaceAll("_", " "));
		JSONArray caseArray = obj.has("case_nums") ? obj.getJSONArray("case_nums") : null;
		String caseNo = "";
		int forum = 0;
		int caseType = 0;
		if ((caseArray != null) && (caseArray.length() > 0)) {
			JSONObject lastObj = caseArray.getJSONObject(caseArray.length() - 1);
			caseNo = lastObj.getString("case_id") + "/" + lastObj.getInt("case_year");
			forum = lastObj.has("forum_num") ? lastObj.getInt("forum_num") : 0;
			caseType = lastObj.has("case_type_num") ? lastObj.getInt("case_type_num") : 0;
		} else {
			String caseID = obj.has("case_id") ? obj.getString("case_id") : "";
			int caseYear = obj.has("case_year") ? obj.getInt("case_year") : 0;
			caseNo = caseID + "/" + caseYear;
			forum = obj.has("forum_num") ? obj.getInt("forum_num") : 0;
			caseType = obj.has("case_type_num") ? obj.getInt("case_type_num") : 0;
		}

		String petitioner = obj.has("petitioner") ? obj.getString("petitioner") : "";
		String respondent = obj.has("respondent") ? obj.getString("respondent") : "";

		JSONArray petitionerArray = obj.has("petitioners") ? obj.getJSONArray("petitioners") : null;
		JSONArray respondentArray = obj.has("respondents") ? obj.getJSONArray("respondents") : null;

		JSONArray bench = obj.has("bench") ? obj.getJSONArray("bench") : null;

		String shouldKey = "";
		if ((shouldHaveKey != null) && (shouldHaveKey.length() > 0)) {
			shouldKey = shouldHaveKey.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
		}

		String shouldNotKey = "";
		if ((shouldNotHaveKey != null) && (shouldNotHaveKey.length() > 0)) {
			shouldNotKey = shouldNotHaveKey.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
		}

		String ben = "";
		if ((bench != null) && (bench.length() > 0)) {
			ben = bench.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
		}

		String pet = "";
		if (petitioner != "") {
			pet = petitionerArray == null ? petitioner
					: petitionerArray.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
		}

		String res = "";
		if (respondent != "") {
			res = respondentArray == null ? respondent
					: respondentArray.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
		}

		String counselOfCompany = "";
		String counselOfRespondent = "";
		JSONArray advocates = obj.has("advocates") ? obj.getJSONArray("advocates") : null;

		if ((advocates != null) && (advocates.length() > 0)) {
			JSONObject firstAdvObj = advocates.getJSONObject(0);
			String forP = firstAdvObj.has("for") ? firstAdvObj.getString("for") : "";

			if ((forP != "") && ("petitioner".equalsIgnoreCase(forP))) {
				counselOfCompany = firstAdvObj.has("name") ? firstAdvObj.getString("name") : "";
			} else if ("respondent".equalsIgnoreCase(forP)) {
				counselOfRespondent = firstAdvObj.has("name") ? firstAdvObj.getString("name") : "";
			}

			if (advocates.length() == 2) {
				JSONObject secondAdvObj = advocates.getJSONObject(1);
				String resP = secondAdvObj.has("for") ? secondAdvObj.getString("for") : "";
				if ((resP != "") && ("petitioner".equalsIgnoreCase(resP))) {
					counselOfCompany = secondAdvObj.has("name") ? secondAdvObj.getString("name") : "";
				} else if ("respondent".equalsIgnoreCase(resP)) {
					counselOfRespondent = secondAdvObj.has("name") ? secondAdvObj.getString("name") : "";
				}
			}
		}

		String nextDateOfHearing = obj.has("likely_listing_date") ? obj.getString("likely_listing_date") : "";
		if ("".equals(nextDateOfHearing))
			nextDateOfHearing = obj.has("causelist_date") ? obj.getString("causelist_date") : "";
		if ("".equals(nextDateOfHearing))
			nextDateOfHearing = obj.has("next_date") ? obj.getString("next_date") : "";
		String subMatter = obj.has("subject") ? obj.getString("subject") : "";

		String caseStatus = obj.has("status") ? obj.getString("status") : "";

		String courtNo = obj.has("court_num") ? obj.getString("court_num") : "";
		String itemNo = obj.has("item_num") ? obj.getString("item_num") : "";
		String causeListExtract = obj.has("causelist_extract") ? obj.getString("causelist_extract") : "";

		String enteredBy = obj.has("enteredBy") ? obj.getString("enteredBy") : "";

		String causeListType = obj.has("causelist_type") ? obj.getString("causelist_type") : "";
		String message = obj.has("message") ? obj.getString("message") : "";

		String courtNum = obj.has("court_num") ? obj.getString("court_num") : "";

		String itemNum = obj.has("item_num") ? obj.getString("item_num") : "";

		boolean attachment = false;
		JSONArray papers = obj.has("papers") ? obj.getJSONArray("papers") : null;

		CallableStatement cstmt = null;
		PreparedStatement stmt = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		java.util.Date date1 = null;
		java.sql.Date sqlDate2 = null;
		try {
			if (!"".equals(nextDateOfHearing)) {
				date1 = sdf1.parse(nextDateOfHearing);
				sqlDate2 = new java.sql.Date(date1.getTime());
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		String[] split = lmmsCaseID.split(",");

		for (int s = 0; s < split.length; s++) {
			String externalID = split[s].trim();

			try {
				con = SQLDBConnection.getDBConnection();
				cstmt = con.prepareCall("{call " + LawSuitTrackerConstants.pvOpnProc
						+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, type);
				cstmt.setString(2, caseNo);
				cstmt.setString(3, pet);
				cstmt.setString(4, res);
				cstmt.setInt(5, forum);
				cstmt.setString(6, ben);
				cstmt.setString(7, counselOfCompany);
				cstmt.setString(8, counselOfRespondent);
				cstmt.setDate(9, sqlDate2);
				cstmt.setString(10, subMatter);
				cstmt.setString(11, caseStatus);
				cstmt.setInt(12, caseType);
				cstmt.setString(13, caseNo);
				cstmt.setString(14, courtNo);
				cstmt.setString(15, itemNo);
				cstmt.setString(16, causeListExtract);
				cstmt.setString(17, shouldKey);
				cstmt.setString(18, pvID);
				cstmt.setString(19, link);
				cstmt.setString(20, shouldNotKey);
				cstmt.setString(21, notificationType);
				cstmt.setString(22, obj.toString());
				cstmt.setString(23, enteredBy);
				cstmt.setString(24, petitioner);
				cstmt.setString(25, respondent);
				cstmt.setInt(26, ("".equals(externalID)) ? 0 : Integer.parseInt(externalID));

				cstmt.execute();
				rs2 = cstmt.getResultSet();

				if ("pvAlert".equalsIgnoreCase(type)) {
					String sub = "";
					String text = "";
					if ("proactive_alert".equalsIgnoreCase(notificationType)) {
						sub = " A New case found - " + shouldKey;
						text = " A new case has been received in LMMS application with the following details: ";
					} else {
						sub =

								" A New update found - "
										+ ("new_listing".equals(notificationType) ? "Cause List"
												: formattedNotificationType)
										+ (externalID == "" ? ""
												: new StringBuilder(" on Case ID - ").append(externalID).toString());
						text = " A new update has been received in LMMS application with the following details: ";

						if ("new_paper".equalsIgnoreCase(notificationType) && papers.length() > 0) {
							text = text + " Please refer the attachments.";
						}
					}
					String to = "";
					StringBuilder bld = new StringBuilder();

					if (rs2.next()) {
						outJson.put("count", rs2.getInt(1));
						outJson.put("uniqueID", rs2.getInt(2));
						String forumName = rs2.getString(3);
						String caseTypeName = rs2.getString(4);
						String comp = rs2.getString(5);
						String busi = rs2.getString(6);
						String vert = rs2.getString(7);
						String dt = rs2.getString(8);
						int uiID = rs2.getInt(2);

						String subHTML = "";
						String benchHTML = "";
						String advHTML = "";
						String causeListHTML = "";
						String causeListTypeHTML = "";
						String shouldKeyHTML = "";
						String hearingHTML = "";
						String messageHTML = "";
						String companyHTML = "";
						String businessHTML = "";
						String verticalHTML = "";
						String courtNumHTML = "";
						String itemNumHTML = "";

						if (comp != null && comp != "") {
							companyHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Company</td><td>" + comp
									+ "</td></tr>";
						}
						if (busi != null && busi != "") {
							businessHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Business</td><td>"
									+ busi + "</td></tr>";
						}
						if (vert != null && vert != "") {
							verticalHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Vertical</td><td>"
									+ vert + "</td></tr>";
						}
						if (dt != null && !("new_paper".equalsIgnoreCase(notificationType))) {
							hearingHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Hearing Date </td><td>"
									+ dt + "</td></tr>";
						}
						if (!"".equals(shouldKey)) {
							shouldKeyHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Keywords</td><td>"
									+ shouldKey + "</td></tr>";
						}
						if (!"".equals(ben)) {
							benchHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Bench</td><td>" + ben
									+ "</td></tr>";
						}
						if (!"".equals(subMatter)) {
							subHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Subject</td><td>" + subMatter
									+ "</td></tr>";
						}
						if ((!"".equals(counselOfCompany)) || (!"".equals(counselOfRespondent))) {
							advHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Advocates</td><td>"
									+ counselOfCompany + counselOfRespondent + "</td></tr>";
						}
						if (!"".equals(causeListExtract)) {
							causeListHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Cause List Extract</td><td>"
									+ causeListExtract + "</td></tr>";
						}
						if (!"".equals(causeListType)) {
							causeListTypeHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Cause List Type</td><td>"
									+ causeListType + "</td></tr>";
						}
						if (!"".equals(message)) {
							messageHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Mannual Review</td><td>"
									+ message + "</td></tr>";
						}
						if (!"".equals(courtNum)) {
							courtNumHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Court No.</td><td>"
									+ courtNum + "</td></tr>";
						}
						if (!"".equals(itemNum)) {
							itemNumHTML = "<tr><td style=\"color:steelblue;font-weight:bold;\">Item No.</td><td>"
									+ itemNum + "</td></tr>";
						}
						String in = null;

						cstmt.getMoreResults();
						rs = cstmt.getResultSet();
						if (rs != null) {
							while (rs.next()) {
								bld.append(rs.getString(2) + ",");
							}

							to = bld.toString();
							File directory = new File("D://LMMS_DOCS//");
							FileUtils.cleanDirectory(directory);
							if ((papers != null) && (papers.length() > 0) && (!"".equals(to)) && (to != null)) {
								attachment = true;
								for (int i = 0; i < papers.length(); i++) {
									JSONObject jobj = papers.getJSONObject(i);

									String paperType = jobj.getString("type");
									JSONArray results = jobj.has("results") ? jobj.getJSONArray("results") : null;

									for (int j = 0; j < results.length(); j++) {
										String date = results.getJSONObject(j).getString("date");
										try {
											if (!"".equals(date)) {
												date1 = sdf1.parse(date);
												sqlDate2 = new java.sql.Date(date1.getTime());
											}
										} catch (ParseException e1) {
											e1.printStackTrace();
										}
										
										downloadPaper(paperType, results.getJSONObject(j).getString("id"), pvID,
												results.getJSONObject(j).getString("name"), uiID,
												externalID == "" ? 0 : Integer.parseInt(externalID), caseNo, sqlDate2);

									}
								}
							}

							System.out.println(to);
							if ((!"".equals(to)) && (to != null)) {
								String template = "<html><body style=\"font-family:Georgia;font-size:14;\"><p> Dear Sir/Madam, </p>\t<p>"
										+ text
										+ "</p> <p> <table border = \"1\" style=\"font-family:Georgia;font-size:12;\">"
										+ companyHTML + businessHTML + verticalHTML
										+ "<tr><td style=\"color:steelblue;font-weight:bold;\">Forum</td><td>"
										+ forumName
										+ "</td></tr><tr><td style=\"color:steelblue;font-weight:bold;\">Case Type</td><td>"
										+ caseTypeName
										+ "</td></tr><tr><td style=\"color:steelblue;font-weight:bold;\">Case No</td><td>"
										+ caseNo
										+ "</td></tr><tr><td style=\"color:steelblue;font-weight:bold;\">Petitioner</td><td>"
										+ pet
										+ "</td></tr><tr><td style=\"color:steelblue;font-weight:bold;\">Respondent</td><td>"
										+ res + "</td></tr>" + benchHTML + advHTML + subHTML + causeListTypeHTML
										+ causeListHTML + hearingHTML + shouldKeyHTML + messageHTML + courtNumHTML
										+ itemNumHTML
										+ "<tr><td style=\"color:steelblue;font-weight:bold;\">Notification Type</td><td>"
										+ formattedNotificationType + "</td></tr>"
										+ "</table></p><p><strong>Regards,</strong></p><p>LMMS Team</p><br><br><p><b>PS: </b>This is system generated mail. Hence do not reply.</p></body></html>";
								System.out.println(template);

								sm.sendAUpdateMail(LawSuitTrackerConstants.fromUpdateMail, to, sub, template,
										LawSuitTrackerConstants.adminMail, attachment);
							}
						}
						outJson.put(LawSuitTrackerConstants.status, "T");
						outJson.put("msg", "Success");
					} else {
						outJson.put(LawSuitTrackerConstants.status, "F");
						outJson.put("msg", "Record not inserted.. Pls try later..");
					}
				} else if (rs2.next()) {
					outJson.put("count", rs2.getInt(1));
					outJson.put("uniqueID", rs2.getInt(2));
					outJson.put(LawSuitTrackerConstants.status, "T");
					outJson.put("msg", "Success");
				}
			} catch (SQLException e) {
				outJson.put(LawSuitTrackerConstants.status, "F");
				outJson.put("msg", e);

				logger.error("Login Error in SQL Exception ==>" + e);

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {
						logger.error("Login Error in Finally - rs ==>" + e.getMessage());
					}
				}
				if (rs2 != null) {
					try {
						rs2.close();
						rs2 = null;
					} catch (Exception e) {
						logger.error("Login Error in Finally - rs2 ==>" + e.getMessage());
					}
				}
				if (rs3 != null) {
					try {
						rs3.close();
						rs3 = null;
					} catch (Exception e) {
						logger.error("Login Error in Finally - rs3 ==>" + e.getMessage());
					}
				}
				if (stmt != null)
					try {
						stmt.close();
						stmt = null;
					} catch (Exception e) {
						logger.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (cstmt != null)
					try {
						cstmt.close();
						cstmt = null;
					} catch (Exception e) {
						logger.error("Login Error in Finally - stmt ==>" + e.getMessage());
					}
				if (con != null) {
					try {
						con.close();
						con = null;
					} catch (Exception e) {
						logger.error("Login Error in Finally - con ==>" + e.getMessage());
					}
				}
			}
		}
		response = outJson.toString(4);
		return response;
	}

	public void downloadPaper(String type, String paperID, String pvCaseID, String paperName, int alertID, int caseID,
			String caseNo, Date docDate) {
		java.io.InputStream in = null;
		java.io.InputStream targetStream = null;
		OutputStream outputStream = null;
		Connection con = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet tab = null;
		boolean checkPaperFlag = false;

		checkPaperFlag = checkPaperExisits(paperID);
		
		System.out.println(checkPaperFlag);

		if (!checkPaperFlag) {
			HttpHost proxy = new HttpHost(LawSuitTrackerConstants.proxyHost, LawSuitTrackerConstants.proxyPort, null);
			System.setProperty("https.protocols","TLSv1");
			SSLContext sslContext = null;
			try {
				sslContext = SSLContext.getInstance("TLSv1");
				sslContext.init(null, null, null);
			} catch (KeyManagementException | NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme httpsScheme = new Scheme("https", 443, sslSocketFactory);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(httpsScheme);

			ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);

			HttpClient
			client = new DefaultHttpClient(cm);
			//DefaultHttpClient client = new DefaultHttpClient();
			String fileType = "";
			String finalFileName = "";
			String query = "";
			String source = "CIS";
			MongoDatabase database = MongoDBConnection.getMongoDB();
			GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoCollection);
			ObjectId id = null;

			try {
				String url = "https://provakil.com/synapse/papers/" + paperID + "/content";
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
				in = response.getEntity().getContent();

				String[] contentDisposition = null;
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					if ("Content-Type".equalsIgnoreCase(header.getName())) {
						fileType = header.getValue();
					} else if ("Content-Disposition".equalsIgnoreCase(header.getName())) {
						contentDisposition = header.getValue().split(";");
					}

				}

				for (String filename : contentDisposition) {
					if (filename.trim().startsWith("filename")) {
						String[] fname = filename.split("=");

						finalFileName = fname[1].trim().replaceAll("\"", "");
					}
				}

				System.out.println("File name from provakil ==>" + finalFileName);
				GridFSUploadOptions options = new GridFSUploadOptions().chunkSizeBytes(1024)
						.metadata(new Document("contentType", fileType).append("source", source));

				String[] split = fileType.split("/");
				String[] splitFileName = caseNo.split("/");
				String lmmsFileName = type + "_" + splitFileName[0] + "_" + splitFileName[1] + "_" + alertID + "."
						+ (split[1].trim());

				int docTypeID = fetchDocTypeID(type);

				File file = new File("D://LMMSDOCS//" + type + "_" + splitFileName[0] + "_" + splitFileName[1] + "_"
						+ alertID + "." + (split[1].trim()));

				try {
					outputStream = new FileOutputStream(file);
					IOUtils.copy(in, outputStream);
					targetStream = new FileInputStream(file);
					long fileSize = file.length();
					id = gridBucket.uploadFromStream(lmmsFileName, targetStream, options);
					System.out.println(id);

					try {
						con = SQLDBConnection.getDBConnection();

						query = "insert into document_details(document_type, file_desc, case_id, file_type, file_name, source, created_by, mongo_db_id, alert_id, pv_paper_id, pv_case_id, document_date, confidential, file_size_bytes) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

						stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
						stmt.setInt(1, docTypeID);
						stmt.setString(2, paperName);
						stmt.setInt(3, caseID);
						stmt.setString(4, fileType);
						stmt.setString(5, lmmsFileName);
						stmt.setString(6, "CIS");
						stmt.setString(7, "PV");
						stmt.setString(8, id.toString());
						stmt.setInt(9, alertID);
						stmt.setString(10, paperID);
						stmt.setString(11, pvCaseID);
						stmt.setDate(12, docDate);
						stmt.setString(13, "F");
						stmt.setLong(14, fileSize);

						int result = stmt.executeUpdate();

						if (result > 0) {

							System.out.println("Success");
						}

					} catch (SQLException e) {
						System.out.println("error");
						e.printStackTrace();

					} finally {

						if (stmt != null)
							try {
								stmt.close();
								stmt = null;
							} catch (Exception e) {
								// logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
							}
						if (stmt1 != null)
							try {
								stmt1.close();
								stmt1 = null;
							} catch (Exception e) {
								// logg.error("Login Error in Finally - stmt1 ==>" + e.getMessage());
							}
						if (con != null)
							try {
								con.close();
								con = null;
							} catch (Exception e) {
								// logg.error("Login Error in Finally - con ==>" + e.getMessage());
							}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// handle exception here
					e.printStackTrace();
				} finally {
					try {
						if (in != null)
							in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (targetStream != null)
							targetStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (outputStream != null)
							outputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				System.out.println("Exception in NetClientGet:- " + e);
			}
		}
	}

	private int fetchDocTypeID(String type) {
		int docTypeID = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "";

		try {
			con = SQLDBConnection.getDBConnection();

			query = "select id from document_type where name = ?";
			stmt = con.prepareStatement(query);
			stmt.setString(1, type);
			rs = stmt.executeQuery();
			if (rs.next()) {
				docTypeID = rs.getInt(1);

			}

		} catch (SQLException e) {

			System.out.println("There seems some error at server side. Please try again later!!!");
		} finally {

			if (stmt != null)
				try {
					stmt.close();
					stmt = null;
				} catch (Exception e) {

				}
			if (con != null)
				try {
					con.close();
					con = null;
				} catch (Exception e) {

				}
		}
		return docTypeID;

	}

	public boolean checkPaperExisits(String paperID) {
		boolean isExists = false;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = SQLDBConnection.getDBConnection();
			String query = "select count(*) from document_details where pv_paper_id = ?";
			stmt = con.prepareStatement(query);
			stmt.setString(1, paperID);

			rs = stmt.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					System.out.println("Paper already exists ==>" + paperID);
					isExists = true;
				}
				else
					isExists = false;

			} else {
				isExists = false;
			}
		} catch (SQLException e) {
			System.out.println("Login Error in SQL Exception ==>" + e);
		} finally {
			if (rs != null)
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
					System.out.println("Login Error in Finally - rs ==>" + e.getMessage());
				}
			if (stmt != null)
				try {
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					System.out.println("Login Error in Finally - stmt ==>" + e.getMessage());
				}
			if (con != null)
				try {
					con.close();
					con = null;
				} catch (Exception e) {
					System.out.println("Login Error in Finally - con ==>" + e.getMessage());
				}
		}

		return isExists;
	}

}
