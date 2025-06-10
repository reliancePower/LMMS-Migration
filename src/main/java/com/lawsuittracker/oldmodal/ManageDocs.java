package com.lawsuittracker.oldmodal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lawsuittracker.dao.MongoDBConnection;
import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

public class ManageDocs {
	public static final Logger logger = Logger.getLogger(ManageDocs.class);

	public String manageDocuments(String input) throws Exception {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;

		String query = "";
		JSONObject output = new JSONObject();

		JSONObject obj = new JSONObject(input);
		// String caseID = (obj.has("caseID")) ? obj.getString("caseID") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String docType = (obj.has("docType")) ? obj.getString("docType") : "";
		String docDesc = (obj.has("docDesc")) ? obj.getString("docDesc") : "";
		//String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		int docID = (obj.has("docID")) ? obj.getInt("docID") : 0;
		String mongoID = (obj.has("mongoID")) ? obj.getString("mongoID") : "";
		String docDate = (obj.has("docDate")) ? obj.getString("docDate") : "";
		String confidential = (obj.has("confidential")) ? obj.getString("confidential") : "";
		try {

			con = SQLDBConnection.getDBConnection();
			Date documentDate = null;

			if ("edit".equalsIgnoreCase(type)) {
				if (!("".equals(docDate)))
					documentDate = Date.valueOf(docDate);
				query = "update document_details set document_type = ?, file_desc = ?, document_date = ?, confidential= ? where id = ?";

				stmt = con.prepareStatement(query);
				stmt.setString(1, docType);
				stmt.setString(2, docDesc);
				stmt.setDate(3, documentDate);
				stmt.setString(4, confidential);
				stmt.setInt(5, docID);
				int result = stmt.executeUpdate();
				if (result > 0) {
					output.put(LawSuitTrackerConstants.status, "T");
					output.put("msg", "Success");
				} else {
					output.put(LawSuitTrackerConstants.status, "F");
					output.put("msg", "None");
				}

			} else if ("delete".equalsIgnoreCase(type)) {
				MongoDatabase database = MongoDBConnection.getMongoDB();
				GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoCollection);
				query = "update document_details set status = 0 where id = ?";

				stmt = con.prepareStatement(query);
				stmt.setInt(1, docID);
				int result = stmt.executeUpdate();
				if (result > 0) {
					ObjectId id = new ObjectId(mongoID);
					gridBucket.delete(id);
					output.put(LawSuitTrackerConstants.status, "T");
					output.put("msg", "Success");
				} else {
					output.put(LawSuitTrackerConstants.status, "F");
					output.put("msg", "None");
				}
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

		response = output.toString(4);
		return response;
	}

	public String fetchDocuments(String input) throws Exception {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "";
		JSONObject output = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		String caseID = (obj.has("caseID")) ? obj.getString("caseID") : "";
		// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";

		try {
			con = SQLDBConnection.getDBConnection();
			query = "SELECT id, mongo_db_id, file_name, file_type, file_desc, document_type, document_type_name, source, created_by, user_name, CONVERT(varchar, timestamp, 100), CONVERT(varchar, document_date, 103), confidential  from vw_document_details where case_id  = ? order by cast(document_date as date) desc";

			System.out.println(query);
			stmt = con.prepareStatement(query);
			stmt.setInt(1, Integer.parseInt(caseID));

			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("mongoID", rs.getString(2));
				jobj.put("fileName", rs.getString(3));
				jobj.put("fileType", rs.getString(4));
				jobj.put("fileDesc", rs.getString(5));
				jobj.put("docType", rs.getString(6));
				jobj.put("docTypeName", rs.getString(7));
				jobj.put("source", rs.getString(8));
				jobj.put("createdBy", rs.getString(9));
				jobj.put("userName", rs.getString(10));
				jobj.put("timestamp", rs.getString(11));
				jobj.put("docDate", rs.getString(12));
				jobj.put("isConfidential", rs.getString(13));
				resultArray.put(jobj);
			}

			if (resultArray.length() == 0) {
				output.put(LawSuitTrackerConstants.status, "F");
				output.put("msg", "No cases available for the case id ");
				output.put("docArray", resultArray);

			} else {
				output.put(LawSuitTrackerConstants.status, "T");
				output.put("msg", "Success");
				output.put("docArray", resultArray);
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

		response = output.toString(4);
		return response;
	}

	public String fetchDocType(String input) throws Exception {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "";
		// JSONObject obj = new JSONObject(input);
		JSONObject output = new JSONObject();
		JSONArray docTypeArray = new JSONArray();
		// String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";

		try {
			con = SQLDBConnection.getDBConnection();

			query = "select id, name from document_type where status = 1 order by name";

			stmt = con.prepareStatement(query);
			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("name", rs.getString(2));
				docTypeArray.put(jobj);

			}
			if (docTypeArray.length() == 0) {
				output.put(LawSuitTrackerConstants.status, "F");
				output.put("msg", "None");
			} else {
				output.put(LawSuitTrackerConstants.status, "T");
				output.put("msg", "Success");
				output.put("docTypeArray", docTypeArray);
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
		response = output.toString(4);
		return response;
	}

	// Notices docs
	public String fetchNoticeDocs(String input) throws Exception {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "";
		JSONObject output = new JSONObject();
		JSONArray resultArray = new JSONArray();
		JSONObject obj = new JSONObject(input);
		int noticeID = (obj.has("noticeID")) ? obj.getInt("noticeID") : 0;
		// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";

		try {
			con = SQLDBConnection.getDBConnection();
			query = "SELECT id, mongo_db_id, file_name, file_type, file_desc, notice_type, notice_type_name,  created_by,  CONVERT(varchar, timestamp, 100), CONVERT(varchar, document_date, 103), confidential, user_id  from vw_notices_docs where notice_id  = ? order by cast(document_date as date) desc";

			System.out.println(query);
			stmt = con.prepareStatement(query);
			stmt.setInt(1, noticeID);

			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("mongoID", rs.getString(2));
				jobj.put("fileName", rs.getString(3));
				jobj.put("fileType", rs.getString(4));
				jobj.put("fileDesc", rs.getString(5));
				jobj.put("docType", rs.getString(6));
				jobj.put("docTypeName", rs.getString(7));
				jobj.put("userName", rs.getString(8));
				jobj.put("timestamp", rs.getString(9));
				jobj.put("docDate", rs.getString(10));
				jobj.put("isConfidential", rs.getString(11));
				jobj.put("userID", rs.getString(12));
				resultArray.put(jobj);
			}

			if (resultArray.length() == 0) {
				output.put(LawSuitTrackerConstants.status, "F");
				output.put("msg", "No info available for the notice id " + noticeID);
				output.put("docArray", resultArray);

			} else {
				output.put(LawSuitTrackerConstants.status, "T");
				output.put("msg", "Success");
				output.put("docArray", resultArray);
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

		response = output.toString(4);
		return response;
	}

	public String fetchNoticeDocType(String input) throws Exception {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "";
		// JSONObject obj = new JSONObject(input);
		JSONObject output = new JSONObject();
		JSONArray docTypeArray = new JSONArray();
		// String userID = (obj.has("userID")) ? obj.getString("userID") : "";
		// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";

		try {
			con = SQLDBConnection.getDBConnection();

			query = "select id, name from notices_doc_type where status = 1";

			stmt = con.prepareStatement(query);
			rs = stmt.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("id", rs.getInt(1));
				jobj.put("name", rs.getString(2));
				docTypeArray.put(jobj);

			}
			if (docTypeArray.length() == 0) {
				output.put(LawSuitTrackerConstants.status, "F");
				output.put("msg", "None");
			} else {
				output.put(LawSuitTrackerConstants.status, "T");
				output.put("msg", "Success");
				output.put("docTypeArray", docTypeArray);
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
		response = output.toString(4);
		return response;
	}

	public String manageNoticeDocuments(String input) throws Exception {
		String response = "";
		Connection con = null;
		PreparedStatement stmt = null;

		String query = "";
		JSONObject output = new JSONObject();

		JSONObject obj = new JSONObject(input);
		// String caseID = (obj.has("caseID")) ? obj.getString("caseID") : "";
		String type = (obj.has("type")) ? obj.getString("type") : "";
		String noticeType = (obj.has("docType")) ? obj.getString("docType") : "";
		String docDesc = (obj.has("docDesc")) ? obj.getString("docDesc") : "";
		// String sessID = (obj.has("sessID")) ? obj.getString("sessID") : "";
		int docID = (obj.has("docID")) ? obj.getInt("docID") : 0;
		String mongoID = (obj.has("mongoID")) ? obj.getString("mongoID") : "";
		String docDate = (obj.has("docDate")) ? obj.getString("docDate") : "";
		String confidential = (obj.has("confidential")) ? obj.getString("confidential") : "";
		try {

			con = SQLDBConnection.getDBConnection();
			Date documentDate = null;

			if ("edit".equalsIgnoreCase(type)) {
				if (!("".equals(docDate)))
					documentDate = Date.valueOf(docDate);
				query = "update notice_docs set notice_type = ?, file_desc = ?, document_date = ?, confidential= ? where id = ?";

				stmt = con.prepareStatement(query);
				stmt.setString(1, noticeType);
				stmt.setString(2, docDesc);
				stmt.setDate(3, documentDate);
				stmt.setString(4, confidential);
				stmt.setInt(5, docID);
				int result = stmt.executeUpdate();
				if (result > 0) {
					output.put(LawSuitTrackerConstants.status, "T");
					output.put("msg", "Success");
				} else {
					output.put(LawSuitTrackerConstants.status, "F");
					output.put("msg", "None");
				}

			} else if ("delete".equalsIgnoreCase(type)) {
				MongoDatabase database = MongoDBConnection.getMongoDB();
				GridFSBucket gridBucket = GridFSBuckets.create(database,
						LawSuitTrackerConstants.MongoNoticesCollection);
				query = "update notice_docs set status = 0 where id = ?";

				stmt = con.prepareStatement(query);
				stmt.setInt(1, docID);
				int result = stmt.executeUpdate();
				if (result > 0) {
					ObjectId id = new ObjectId(mongoID);
					gridBucket.delete(id);
					output.put(LawSuitTrackerConstants.status, "T");
					output.put("msg", "Success");
				} else {
					output.put(LawSuitTrackerConstants.status, "F");
					output.put("msg", "None");
				}
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

		response = output.toString(4);
		return response;
	}

}
