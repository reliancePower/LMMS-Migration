package com.lawsuittracker.util;

public class LawSuitTrackerConstants {
	public static String authHeader 			= "Basic UlBvd2VyRGFzaGJvYXJkOlJQb3dlckAyMDE4JA==";
	public static String ldapPrimURL 			= "LDAP://10.211.253.142:389"; 
	//public static String ldapSecURL 			= "LDAP://10.8.51.136:389"; 
	public static String ldapSecURL 			= "LDAP://10.211.253.11:389"; 
	public static String status			    	= "status";
	public static String fechInitMaster			= "pr_fetch_All_Masters";
	public static String fetchCompanyMaster 	= "pr_fetch_for_Company";
	public static String homePageMaster 		= "pr_home_page_info";
	public static String updateCaseProc 		= "pr_update_case";
	public static String pushNotifProc	 		= "pr_push_notification_update";
	public static String caseAddEditProc		= "pr_case_add_edit";
	public static String loginCheckProc			= "pr_login_check";
	public static String userRegMaster			= "pr_fetchMasterUserRegis";
	public static String userManagProc			= "pr_user_management";
	public static String updateHearingProc		= "pr_hearing_update";
	public static String loginCheckProcForMob 	= "pr_login_check_mobile_app";
	public static String deviceReqProc			= "pr_device_request";
	public static String noticeManagProc        = "pr_notice_management";
	public static String pvOpnProc        		= "pr_pv_integration";
	public static String alertNotificationProc  = "pr_alert_notification";
	public static String manageForumProc  		= "pr_manage_forum";
	public static String fechMasterForAllProc  	= "pr_manage_master";
	public static String managenotesProc  		= "pr_manage_notes";
	
	
	
	public static String RestAPIUserID			= "RPowerDashboard";
	public static String RestAPIPassword		= "RPower@2018$";
	
	//Mongo DB Client
	
	//public static String MongoDBHost			= "10.185.1.103";
	public static String MongoDBHost			= "10.211.253.19";
	public static int MongoDBPort				= 27017;
	public static String MongoDBName			="LMMS";
	public static String MongoAuthDBName		="admin";
	public static String MongoDBUserName		= "myUserAdmin";
	public static String MongoDBPassword		= "abc123";		
	
	//Production Links
//public static String MongoCollection		= "LMMSDocs";
//public static String MongoNoticesCollection	= "LMMS_Notice_Docs";
	
	//Always comment the below 2 lines during production deployment
	//public static String MongoCollection			= "LMMSDEV_DOCS";
	//public static String MongoNoticesCollection		= "LMMSDEV_Notice_Docs";
	
	//Always comment the below 2 lines during production deployment
		public static String MongoCollection			= "LMMSSB_DOCS";
		public static String MongoNoticesCollection		= "LMMSSB_Notice_Docs";
	
	
	
	//Email Server
	
	//public static String fromMail 				= "LMMS.Admin@relianceada.com";
	//public static String fromUpdateMail			= "LMMS.Update@relianceada.com";
	public static String fromMail 				= "LMMS.Admin@reliancegroupindia.com";
	public static String fromUpdateMail			= "LMMS.Alerts@reliancegroupindia.com";
	//public static String mailHost 				= "10.8.53.82";
	public static String mailHost 				= "10.8.61.84";
	//public static String mailHost 				= "10.211.252.131";
	//public static String mailHost 			= "10.8.53.98";
	public static String mailPort 				= "25";
	//public static String mailPort 				= "25000";
	
	//Group Mail IDs
	
	public static String adminMail 				= "Joji.Joseph@reliancegroupindia.com";
	//public static String adminMail 			= "Dhivya.Delphina@relianceada.com";
	//public static String devMail 				= "Dhivya.Delphina@relianceada.com";
	public static String devMail 				= "Joji.Joseph@reliancegroupindia.com";
	
	//Proxy Servers
	
//	public static String proxyHost 				= "10.8.49.233";
//	public static int proxyPort 				= 1352;
public static String proxyHost 				= "10.125.170.80";
	public static int proxyPort 				= 8080;


	
}
