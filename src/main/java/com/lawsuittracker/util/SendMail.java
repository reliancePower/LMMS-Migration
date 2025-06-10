package com.lawsuittracker.util;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;

public class SendMail {
	public void sendAMail(String toMails, String sub, String msg, String toName, String cc) {

		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", LawSuitTrackerConstants.mailHost);
		properties.put("mail.smtp.port", LawSuitTrackerConstants.mailPort);
		Session session = Session.getInstance(properties, null);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(LawSuitTrackerConstants.fromMail));
			if (toMails.indexOf(',') == -1)
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMails));
			else {
				String[] uni_mail = toMails.split(",");
				for (int i = 0; i < uni_mail.length; i++)
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(uni_mail[i]));
			}
			if (cc.indexOf(',') == -1)
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			else {
				String[] uni_mail1 = cc.split(",");
				for (int i = 0; i < uni_mail1.length; i++)
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(uni_mail1[i]));
			}

			message.addRecipient(Message.RecipientType.BCC, new InternetAddress(LawSuitTrackerConstants.devMail));
			message.setSubject(sub);
			message.setContent(msg, "text/html;charset=UTF-8");
			Transport transport = session.getTransport("smtp");
			transport.connect();
			Transport.send(message);
			System.out.println("LMMSApp - Mail sent successfully");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void sendAMailToMgmt(String fromMail, String toMails, String sub, String msg, String cc, String bcc) {

		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", LawSuitTrackerConstants.mailHost);
		properties.put("mail.smtp.port", LawSuitTrackerConstants.mailPort);
		Session session = Session.getInstance(properties, null);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromMail));
			if (toMails.indexOf(',') == -1)
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMails));
			else {
				String[] uni_mail = toMails.split(",");
				for (int i = 0; i < uni_mail.length; i++)
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(uni_mail[i]));
			}

			if (!("".equals(cc))) {
				if (cc.indexOf(',') == -1)
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
				else {
					String[] uni_mail1 = cc.split(",");
					for (int i = 0; i < uni_mail1.length; i++)
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(uni_mail1[i]));
				}
			}

			if (bcc.indexOf(',') == -1)
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
			else {
				String[] uni_mail1 = bcc.split(",");
				for (int i = 0; i < uni_mail1.length; i++)
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(uni_mail1[i]));
			}

			// message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
			message.setSubject(sub);
			message.setContent(msg, "text/html;charset=UTF-8");
			Transport transport = session.getTransport("smtp");
			transport.connect();
			Transport.send(message);
			System.out.println("LMMSApp - Mail sent successfully");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void sendAUpdateMail(String fromMail, String toMails, String sub, String msg, String cc,
			boolean attachment) {

		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", LawSuitTrackerConstants.mailHost);
		properties.put("mail.smtp.port", LawSuitTrackerConstants.mailPort);
		Session session = Session.getInstance(properties, null);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromMail));
			if (toMails.indexOf(',') == -1)
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMails));
			else {
				String[] uni_mail = toMails.split(",");
				for (int i = 0; i < uni_mail.length; i++)
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(uni_mail[i]));
			}
			if (cc.indexOf(',') == -1)
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			else {
				String[] uni_mail1 = cc.split(",");
				for (int i = 0; i < uni_mail1.length; i++)
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(uni_mail1[i]));
			}

			Multipart multipart = new MimeMultipart();
			File directory = new File("D://LMMSDOCS//");
			String[] files = directory.list();

			message.addRecipient(Message.RecipientType.BCC, new InternetAddress(LawSuitTrackerConstants.devMail));
			message.setSubject(sub);

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(msg, "text/html;charset=UTF-8");
			System.out.println("in Email");
			if (attachment) {

				for (String file : files) {
					String fileLocation = directory + "/" + file;
					addAttachment(multipart, fileLocation, file);
				}
			}
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			Transport transport = session.getTransport("smtp");
			transport.connect();
			Transport.send(message);
			//FileUtils.cleanDirectory(directory);
			System.out.println("success");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}

	}

	private void addAttachment(final Multipart multipart, final String filepath, final String filename)
			throws MessagingException {
		DataSource source = new FileDataSource(filepath);
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		multipart.addBodyPart(messageBodyPart);
	}

}
