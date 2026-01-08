package com.lawsuittracker.modal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.lawsuittracker.dao.MongoDBConnection;
import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

@WebServlet("/DocumentDownloadAll")
public class DocDownloadAllFiles extends HttpServlet {
	public static final Logger logger = LogManager.getLogger(DocDownloadForCase.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		InputStream isP = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		String query = "";
		boolean flag = false;

		String caseID = request.getParameter("caseID");

		String userName = request.getParameter("userName");
		String userID = request.getParameter("userID");
		String userType = request.getParameter("userType");
		String caseDocView = request.getParameter("caseDocView");
		
		String ip = request.getRemoteAddr();

		MongoDatabase database = MongoDBConnection.getMongoDB();
		GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoCollection);

		con = SQLDBConnection.getDBConnection();

		try {

			query = "SELECT mongo_db_id, confidential, file_name, id, source,authorized_users, created_by FROM vw_document_details  where case_id = ? and status = 1";

			stmt1 = con.prepareStatement(query);

			stmt1.setString(1, caseID);
			rs = stmt1.executeQuery();
			int i = 1;
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			try {
				while (rs.next()) {
					String paperID = rs.getString(1);
					String isConfidential = rs.getString(2);
					String fName = rs.getString(3);
					String docID = rs.getString(4);
					String source = rs.getString(5);
					String authUsers = (rs.getString(6) == null) ? "" : rs.getString(6);
					String createdBy = rs.getString(7);
					ObjectId id = new ObjectId(paperID);
					
					flag = checkAccess(userType,source,isConfidential,userID,authUsers,createdBy,caseDocView);
					
					if(flag) {

					GridFSFile gridFile = gridBucket.find(new Document("_id", id)).first();

					if (gridFile == null) {
						System.out.println("No docs");
					}

					isP = gridBucket.openDownloadStream(gridFile.getObjectId());

					if ("T".equalsIgnoreCase(isConfidential)) {
						try {

							query = "insert into conf_doc_audit_log(payroll_no,name,file_name,doc_id,device,ip) values(?, ?, ?, ?, ?, ?)";

							stmt1 = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

							stmt1.setString(1, userID);
							stmt1.setString(2, userName);
							stmt1.setString(3, fName);
							stmt1.setString(4, docID);
							stmt1.setString(5, "web");
							stmt1.setString(6, ip);
							int result = stmt1.executeUpdate();
							if (result > 0) {
								System.out.println("Audit for confidential doc - done");

							} else {
								System.out.println("Audit for confidential doc - not done");
							}

						} catch (SQLException e) {

							System.out.println(e);
							e.printStackTrace();

						}
					}

					System.out.println(gridFile.getMetadata().get("contentType"));

					System.out.println(gridFile.getFilename());

					ZipEntry ze = new ZipEntry(caseID + "_" + i + "_" + gridFile.getFilename());
					zos.putNextEntry(ze);
					byte[] bytes = new byte[1024];
					int count = isP.read(bytes);
					while (count > -1) {
						zos.write(bytes, 0, count);
						count = isP.read(bytes);
					}

					zos.closeEntry();
					isP.close();

					
					i++;
				}
				}
				
				zos.flush();
				zos.close();

				response.setContentType("application/zip");
				response.setHeader("Content-Disposition",
						"attachment; filename=\"LMMS_" + caseID + "_all_docs" + ".zip" + "\"");
				response.getOutputStream().write(baos.toByteArray());
				
				response.flushBuffer();
				
				baos.flush();				
				baos.close();
				
					
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				
				
			}

		} catch (SQLException e) {

			System.out.println(e);
			e.printStackTrace();

		} finally {

			if (rs != null)
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
					// logg.error("Login Error in Finally - con ==>" + e.getMessage());
				}

			if (stmt1 != null)
				try {
					stmt1.close();
					stmt1 = null;
				} catch (Exception e) {
					// logg.error("Login Error in Finally - stmt ==>" + e.getMessage());
				}

			if (stmt2 != null)
				try {
					stmt2.close();
					stmt2 = null;
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
	
	public boolean checkAccess(String userType, String source, String isConfidential, String userID, String authUsers ,String createdBy, String caseDocView) {
		boolean flag = false;
		
		//
		//if(("Admin".equalsIgnoreCase(userType)) && (!("CIS".equalsIgnoreCase(source))) && ("T".equalsIgnoreCase(isConfidential)) && (authUsers.indexOf(userID) > -1))
			//flag = true;
		//else if (("Admin".equalsIgnoreCase(userType)) && (!("CIS".equalsIgnoreCase(source))) && ("T".equalsIgnoreCase(isConfidential)) && !(authUsers.indexOf(userID) > -1))
		//	flag = false;
		//else if (("CIS".equalsIgnoreCase(source)) && (!("T".equalsIgnoreCase(isConfidential))))
		//	flag = true;
		//else if (!("CIS".equalsIgnoreCase(source)) && ("T".equalsIgnoreCase(isConfidential)) && (authUsers.indexOf(userID) > -1))
		//	flag = true;
		//else if (!("CIS".equalsIgnoreCase(source)) && ("T".equalsIgnoreCase(isConfidential)) && (userID.equalsIgnoreCase(createdBy)) && (!(authUsers.indexOf(userID) > -1)))
		//	flag = false;
		//else if (!("CIS".equalsIgnoreCase(source)) && (!("T".equalsIgnoreCase(isConfidential))) && (("Y".equalsIgnoreCase(caseDocView)) || (userID.equalsIgnoreCase(createdBy))))
		//	flag = true;
		//else
			//flag = false;
		//
		
		if(("Admin".equalsIgnoreCase(userType)) && (!("CIS".equalsIgnoreCase(source))) && ("T".equalsIgnoreCase(isConfidential)) && (authUsers.indexOf(userID) > -1))
			flag = true;
		else if (("Admin".equalsIgnoreCase(userType)) && (!("CIS".equalsIgnoreCase(source))) && ("T".equalsIgnoreCase(isConfidential)) && !(authUsers.indexOf(userID) > -1))
			flag = true;
		else if (("CIS".equalsIgnoreCase(source)) && (!("T".equalsIgnoreCase(isConfidential))))
			flag = true;
		else if (!("CIS".equalsIgnoreCase(source)) && ("T".equalsIgnoreCase(isConfidential)) && (authUsers.indexOf(userID) > -1))
			flag = true;
		else if (!("CIS".equalsIgnoreCase(source)) && ("T".equalsIgnoreCase(isConfidential)) && (userID.equalsIgnoreCase(createdBy)) && (!(authUsers.indexOf(userID) > -1)))
			flag = true;
		else if (!("CIS".equalsIgnoreCase(source)) && (!("T".equalsIgnoreCase(isConfidential))) && (("Y".equalsIgnoreCase(caseDocView)) || (userID.equalsIgnoreCase(createdBy))))
			flag = true;
		else
			flag = true;
		
		return flag;
	}

}
