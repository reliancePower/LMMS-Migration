package com.lawsuittracker.modal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletContext;
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

@WebServlet("/DocumentViewer")
public class DocDownloadForCase extends HttpServlet {
	public static final Logger logger = LogManager.getLogger(DocDownloadForCase.class);
	private static final int bufferSize = 4096;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		InputStream isP = null;
		Connection con = null;
		PreparedStatement stmt = null;

		String query = "";

		String paperID = request.getParameter("paperID");
		String fName = request.getParameter("fileName");
		String userName = request.getParameter("userName");
		String userID = request.getParameter("userID");
		String docID = request.getParameter("docID");
		String isConfidential = request.getParameter("isConfidential");
		String ip = request.getRemoteAddr();

		OutputStream outStream = null;
		MongoDatabase database = MongoDBConnection.getMongoDB();
		GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoCollection);
		ObjectId id = new ObjectId(paperID);
		try {

			GridFSFile gridFile = gridBucket.find(new Document("_id", id)).first();

			// GridFSFile gridFile = gridBucket.find(eq("_id", id)).first();

			if (gridFile == null) {
				System.out.println("No docs");
			}

			if ("T".equalsIgnoreCase(isConfidential)) {
				try {
					con = SQLDBConnection.getDBConnection();

					query = "insert into conf_doc_audit_log(payroll_no,name,file_name,doc_id,device,ip) values(?, ?, ?, ?, ?, ?)";

					stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

					stmt.setString(1, userID);
					stmt.setString(2, userName);
					stmt.setString(3, fName);
					stmt.setString(4, docID);
					stmt.setString(5, "web");
					stmt.setString(6, ip);
					int result = stmt.executeUpdate();
					if (result > 0) {
						System.out.println("Audit for confidential doc - done");

					} else {
						System.out.println("Audit for confidential doc - not done");
					}

				} catch (SQLException e) {

					System.out.println(e);
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

			byte[] buffer = new byte[bufferSize];
			int bytesRead = -1;
			isP = gridBucket.openDownloadStream(gridFile.getObjectId());

			System.out.println(gridFile.getMetadata().get("contentType"));

			String fileName = gridFile.getFilename();

			ServletContext context = getServletContext();

			String mimeType = context.getMimeType(gridFile.getFilename());
			System.out.println("mimetype==>" + mimeType);
			if (mimeType == null)
				mimeType = "application/octet-stream";

			response.setContentType(mimeType);

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=" + fileName);
			response.setHeader(headerKey, headerValue);
			outStream = response.getOutputStream();

			while ((bytesRead = isP.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (isP != null)
					isP.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection con = null;
		PreparedStatement stmt = null;

		String query = "";

		InputStream isP = null;
		String paperID = request.getParameter("paperID");
		String fName = request.getParameter("fileName");
		String userName = request.getParameter("userName");
		String userID = request.getParameter("userID");
		String docID = request.getParameter("docID");

		String isConfidential = request.getParameter("isConfidential");
		String ip = request.getRemoteAddr();

	if (fName == null || userName == null || userID == null || docID == null || isConfidential == null) {
			 response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your session is invalid or You are not authorized to perform this action");

		} else {
			OutputStream outStream = null;
			MongoDatabase database = MongoDBConnection.getMongoDB();
			GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoCollection);
			ObjectId id = new ObjectId(paperID);
			try {

				GridFSFile gridFile = gridBucket.find(new Document("_id", id)).first();

				// GridFSFile gridFile = gridBucket.find(eq("_id", id)).first();

				if (gridFile == null) {
					System.out.println("No docs");
				}
				if ("T".equalsIgnoreCase(isConfidential)) {
					try {
						con = SQLDBConnection.getDBConnection();

						query = "insert into conf_doc_audit_log(payroll_no,name,file_name,doc_id,device,ip) values(?, ?, ?, ?, ?, ?)";

						stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

						stmt.setString(1, userID);
						stmt.setString(2, userName);
						stmt.setString(3, fName);
						stmt.setString(4, docID);
						stmt.setString(5, "mobile");
						stmt.setString(6, ip);
						int result = stmt.executeUpdate();
						if (result > 0) {
							System.out.println("Audit for confidential doc - done");

						} else {
							System.out.println("Audit for confidential doc - not done");
						}

					} catch (SQLException e) {

						System.out.println(e);
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

				byte[] buffer = new byte[bufferSize];
				int bytesRead = -1;
				isP = gridBucket.openDownloadStream(gridFile.getObjectId());

				System.out.println(gridFile.getMetadata().get("contentType"));

				String fileName = gridFile.getFilename();

				ServletContext context = getServletContext();

				String mimeType = context.getMimeType(gridFile.getFilename());
				System.out.println("mimetype==>" + mimeType);
				if (mimeType == null)
					mimeType = "application/octet-stream";

				response.setContentType(mimeType);

				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=" + fileName);
				response.setHeader(headerKey, headerValue);
				outStream = response.getOutputStream();

				while ((bytesRead = isP.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (outStream != null)
						outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if (isP != null)
						isP.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
