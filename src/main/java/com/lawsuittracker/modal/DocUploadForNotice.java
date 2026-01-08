package com.lawsuittracker.modal;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.dao.MongoDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

@WebServlet("/NoticeUpload")
@MultipartConfig
public class DocUploadForNotice extends HttpServlet {
	public static final Logger logger = LogManager.getLogger(DocUploadForNotice.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet tab = null;
		PrintWriter out = response.getWriter();
		String query = "";
		Date docDate = null;

		String noticeType = request.getParameter("docType");
		String noticeID = request.getParameter("noticeID");
		String userID = request.getParameter("userID");
		String docDesc = request.getParameter("docDesc");
		String docDateFormat = request.getParameter("docDateFormat");
		String isConfidential = request.getParameter("isChecked");
		final Part filePart = request.getPart("docFile");
		boolean testAnyFile = true;
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
		// String fileName = getSubmittedFileName(filePart);
		String contentType = filePart.getContentType();
		StringBuffer tempURL = request.getRequestURL();

		String url = tempURL.toString();
		String res = url.split("NoticeUpload")[0];

		if (!("".equals(docDateFormat)))
			docDate = Date.valueOf(docDateFormat);// converting string into sql date.

		url = res.concat("#/dashboard/managenoticedocs");

		int count = StringUtils.countMatches(fileName, ".");
		if ((count == 1) || !("/[/\\?%*:|'<>]/g").contains(fileName)) {
			System.out.print(contentType);
			System.out.println(fileName);
			String split[] = fileName.split("\\.");
			split[1] = split[split.length-1].trim();
			//split[1] = split[1].trim();
			System.out.print(split[1]);

			if ("vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(split[1])
					|| "vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(split[1])
					|| "vnd.ms-excel".equalsIgnoreCase(split[1]) || "xls".equalsIgnoreCase(split[1])
					|| "xlsx".equalsIgnoreCase(split[1]) || "pdf".equalsIgnoreCase(split[1])
					|| "excel".equalsIgnoreCase(split[1]) || "docx".equalsIgnoreCase(split[1])
					|| "doc".equalsIgnoreCase(split[1]) || "msword".equalsIgnoreCase(split[1])
					|| "zip".equalsIgnoreCase(split[1]) || "rar".equalsIgnoreCase(split[1])
					|| "x-zip-compressed".equalsIgnoreCase(split[1])) {

				// if (ServletFileUpload.isMultipartContent(request)) {

				MongoDatabase database = MongoDBConnection.getMongoDB();
				GridFSBucket gridBucket = GridFSBuckets.create(database,
						LawSuitTrackerConstants.MongoNoticesCollection);
				ObjectId id;

				if ("zip".equalsIgnoreCase(split[1]) || "rar".equalsIgnoreCase(split[1])
						|| "x-zip-compressed".equalsIgnoreCase(split[1])) {
					testAnyFile = readUsingZipInputStream(filePart);
					System.out.println(testAnyFile);
				}

				if (testAnyFile) {
					GridFSUploadOptions options = new GridFSUploadOptions().chunkSizeBytes(1024).metadata(
							new Document("contentType", contentType).append("user", "lmms").append("source", "notice"));
					InputStream fileContent = filePart.getInputStream();

					id = gridBucket.uploadFromStream(fileName, fileContent, options);
					System.out.println(id);

					try {
						con = SQLDBConnection.getDBConnection();

						query = "insert into notice_docs(notice_type, file_desc, notice_id, file_type, file_name,  created_by, mongo_db_id, document_date, confidential) values(?, ?, ?, ?, ?, ?, ?,?,?)";

						stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
						stmt.setInt(1, Integer.parseInt(noticeType));
						stmt.setString(2, docDesc);
						stmt.setInt(3, Integer.parseInt(noticeID));
						stmt.setString(4, contentType);
						stmt.setString(5, fileName);
						stmt.setString(6, userID);
						stmt.setString(7, id.toString());
						stmt.setDate(8, docDate);
						stmt.setString(9, isConfidential);

						int result = stmt.executeUpdate();

						if (result > 0) {

							long autoGenID = 0;
							tab = stmt.getGeneratedKeys();
							if (tab.next()) {
								autoGenID = tab.getInt(1);
								String objID = String.valueOf(autoGenID);
								System.out.println(objID);
								out.println("<html><body>");
								out.println("<script type=\"text/javascript\">");
								out.println("alert('File Uploded Successfully');");
								out.println("window.open(\"" + url + "\", '_self');");
								out.println("</script>");
								out.println("</body></html>");

							}
						} else {
							out.println("<html><body>");
							out.println("<script type=\"text/javascript\">");
							out.println("alert('Unable to upload file... Please try again later...');");
							out.println("window.open(\"" + url + "\", '_self');");
							out.println("</script>");
							out.println("</body></html>");
						}

					} catch (SQLException e) {
						out.println("<html><body>");
						out.println("<script type=\"text/javascript\">");
						out.println("alert('Unable to upload file... Please try again later...');");
						out.println("window.open(\"" + url + "\", '_self');");
						out.println("</script>");
						out.println("</body></html>");
						System.out.println("error");
						e.printStackTrace();

					} finally {
						try {
							if (fileContent != null)
								fileContent.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
				} else {
					out.println("<html><body>");
					out.println("<script type=\"text/javascript\">");
					out.println(
							"alert('Unsupported file type found in Zip/Rar file. Please upload Word, PDF or Excel files only.');");
					out.println("window.open(\"" + url + "\", '_self');");
					out.println("</script>");
					out.println("</body></html>");
				}
			} else {
				out.println("<html><body>");
				out.println("<script type=\"text/javascript\">");
				out.println("alert('No file found to upload...');");
				out.println("window.open(\"" + url + "\", '_self');");
				out.println("</script>");
				out.println("</body></html>");
			}
			// } else {
			// out.println("<html><body>");
			// out.println("<script type=\"text/javascript\">");
			// out.println("alert('Unsupported file name. File name contanis special
			// charecters or more than one dot..');");
			// out.println("window.open(\"" + url + "\", '_self');");
			// out.println("</script>");
			// out.println("</body></html>");
			// }
		} else {
			out.println("<html><body>");
			out.println("<script type=\"text/javascript\">");
			out.println("alert('Unsupported file type. Please upload Word, PDF or Excel files only.');");
			out.println("window.open(\"" + url + "\", '_self');");
			out.println("</script>");
			out.println("</body></html>");
		}

	}

	private static String getSubmittedFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

	private boolean readUsingZipInputStream(Part filePart) {
		boolean supportedFileType = false;
		InputStream fileContent = null;
		try {
			fileContent = filePart.getInputStream();

			final ZipInputStream is = new ZipInputStream(fileContent);

			ZipEntry entry;
			while ((entry = is.getNextEntry()) != null) {
				System.out.printf("File: %s Size %d Modified on %TD %n", entry.getName(), entry.getSize(),
						new Date(entry.getTime()));
				System.out.printf(entry.getName());
				String entryName = entry.getName();
				if (entryName.lastIndexOf(".") != -1) {
					String fileTypes = entryName.substring(entryName.lastIndexOf(".") + 1);

					if ("vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(fileTypes)
							|| "vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(fileTypes)
							|| "vnd.ms-excel".equalsIgnoreCase(fileTypes) || "xls".equalsIgnoreCase(fileTypes)
							|| "xlsx".equalsIgnoreCase(fileTypes) || "pdf".equalsIgnoreCase(fileTypes)
							|| "excel".equalsIgnoreCase(fileTypes) || "docx".equalsIgnoreCase(fileTypes)
							|| "doc".equalsIgnoreCase(fileTypes) || "msword".equalsIgnoreCase(fileTypes)
							|| "zip".equalsIgnoreCase(fileTypes) || "rar".equalsIgnoreCase(fileTypes)
							|| "x-zip-compressed".equalsIgnoreCase(fileTypes)) {
						supportedFileType = true;
					} else {
						supportedFileType = false;
						break;
					}
				}
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				fileContent.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return supportedFileType;
	}

}
