package com.lawsuittracker.modal;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.lawsuittracker.dao.MongoDBConnection;
import com.lawsuittracker.dao.SQLDBConnection;
import com.lawsuittracker.util.LawSuitTrackerConstants;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

@WebServlet("/DocumentUpload")
@MultipartConfig
public class DocUploadForCase extends HttpServlet {
	public static final Logger logger = LogManager.getLogger(DocUploadForCase.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ManageDocs md = new ManageDocs();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet tab = null;
		PrintWriter out = response.getWriter();
		String query = "";
		Date docDate = null;

		String docType = request.getParameter("docType");
		String caseID = request.getParameter("caseID");
		String userID = request.getParameter("userID");
		String docDesc = request.getParameter("docDesc");
		String docDateFormat = request.getParameter("docDateFormat");
		String isConfidential = request.getParameter("isChecked");
		String authUsers = request.getParameter("authUsersM");
		System.out.println(authUsers);
		final Part filePart = request.getPart("docFile");
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
		System.out.println(fileName);
		// String fileName = getSubmittedFileName(filePart);
		String contentType = filePart.getContentType();
		StringBuffer tempURL = request.getRequestURL();
		String url = tempURL.toString();
		String res = url.split("DocumentUpload")[0];
		boolean testAnyFile = true;
		if (!("".equals(docDateFormat)))
			docDate = Date.valueOf(docDateFormat);// converting string into sql date.

		url = res.concat("#/dashboard/managedocs");

		int count = StringUtils.countMatches(fileName, ".");

		if ((count == 1) || !("/[/\\?%*:|'<>]/g").contains(fileName)) {
			System.out.println(fileName);
			String split[] = fileName.split("\\.");
			split[1] = split[split.length - 1].trim();
			System.out.print(split[1]);
			// System.out.print(fileName.substring(fileName.lastIndexOf(".") + 1));

			if ("vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(split[1])
					|| "vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(split[1])
					|| "vnd.ms-excel".equalsIgnoreCase(split[1]) || "xls".equalsIgnoreCase(split[1])
					|| "xlsx".equalsIgnoreCase(split[1]) || "pdf".equalsIgnoreCase(split[1])
					|| "excel".equalsIgnoreCase(split[1]) || "docx".equalsIgnoreCase(split[1])
					|| "doc".equalsIgnoreCase(split[1]) || "msword".equalsIgnoreCase(split[1])
					|| "zip".equalsIgnoreCase(split[1]) || "rar".equalsIgnoreCase(split[1])
					|| "x-zip-compressed".equalsIgnoreCase(split[1])) {

				// if (ServletFileUpload.isMultipartContent(request)) {
				long fileSize = filePart.getSize();
				System.out.println(fileSize);
				MongoDatabase database = MongoDBConnection.getMongoDB();
				GridFSBucket gridBucket = GridFSBuckets.create(database, LawSuitTrackerConstants.MongoCollection);
				ObjectId id;

				if ("zip".equalsIgnoreCase(split[1]) || "rar".equalsIgnoreCase(split[1])
						|| "x-zip-compressed".equalsIgnoreCase(split[1])) {
					testAnyFile = readUsingZipInputStream(filePart);
					System.out.println(testAnyFile);
				}

				if (testAnyFile) {
					GridFSUploadOptions options = new GridFSUploadOptions().chunkSizeBytes(1024)
							.metadata(new Document("contentType", contentType).append("source", "lmms"));
					InputStream fileContent = filePart.getInputStream();

					id = gridBucket.uploadFromStream(fileName, fileContent, options);
					System.out.println(id);

					try {
						con = SQLDBConnection.getDBConnection();

						query = "insert into document_details(document_type, file_desc, case_id, file_type, file_name, source, created_by, mongo_db_id, document_date, confidential, authorized_users, file_size_bytes) values(?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

						stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
						stmt.setInt(1, Integer.parseInt(docType));
						stmt.setString(2, docDesc);
						stmt.setInt(3, Integer.parseInt(caseID));
						stmt.setString(4, contentType);
						stmt.setString(5, fileName);
						stmt.setString(6, "UPD");
						stmt.setString(7, userID);
						stmt.setString(8, id.toString());
						stmt.setDate(9, docDate);
						stmt.setString(10, isConfidential);
						stmt.setString(11, authUsers);
						stmt.setLong(12, fileSize);

						int result = stmt.executeUpdate();

						if (result > 0) {

							long autoGenID = 0;
							tab = stmt.getGeneratedKeys();
							if (tab.next()) {
								autoGenID = tab.getInt(1);
								String objID = String.valueOf(autoGenID);
								System.out.println(objID);
								md.updateCaseLastModified(caseID);
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
						System.out.println(e);
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
		// String n = part.getSubmittedFileName().getFileName().toString();
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
