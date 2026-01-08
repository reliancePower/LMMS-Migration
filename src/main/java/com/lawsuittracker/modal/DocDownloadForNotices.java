package com.lawsuittracker.modal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

@WebServlet("/NoticeViewer")
public class DocDownloadForNotices extends HttpServlet {
	public static final Logger logger = LogManager.getLogger(DocDownloadForNotices.class);
	private static final int bufferSize = 4096;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		InputStream isP = null;
		String paperID = request.getParameter("paperID");
		OutputStream outStream = null;
		MongoDatabase database = MongoDBConnection.getMongoDB();
		GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoNoticesCollection);
		ObjectId id = new ObjectId(paperID);
		try {

			GridFSFile gridFile = gridBucket.find(new Document("_id", id)).first();

			// GridFSFile gridFile = gridBucket.find(eq("_id", id)).first();

			if (gridFile == null) {
				System.out.println("No docs");
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
				if(outStream != null)
					outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(isP != null)
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

		InputStream isP = null;
		String paperID = request.getParameter("paperID");
		OutputStream outStream = null;
		MongoDatabase database = MongoDBConnection.getMongoDB();
		GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoNoticesCollection);
		ObjectId id = new ObjectId(paperID);
		try {

			GridFSFile gridFile = gridBucket.find(new Document("_id", id)).first();

			// GridFSFile gridFile = gridBucket.find(eq("_id", id)).first();

			if (gridFile == null) {
				System.out.println("No docs");
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
				if(outStream != null)
					outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(isP != null)
					isP.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
