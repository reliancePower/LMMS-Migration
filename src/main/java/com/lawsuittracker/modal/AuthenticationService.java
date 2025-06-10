package com.lawsuittracker.modal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

public class AuthenticationService {
	public boolean authenticate(String authCredentials, String webservice) {
		boolean authenticationStatus = false;
		if (null == authCredentials)
			return false;
		// header value format will be "Basic encodedstring" for Basic
		// authentication. Example "Basic YWRtaW46YWRtaW4="
		// user name - LawSuitTrackerConstants.RestAPIUserID
		// password  - LawSuitTrackerConstants.RestAPIPassword
		final String encodedUserPassword = authCredentials.replaceFirst("Basic"
				+ " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.decodeBase64(encodedUserPassword);				
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final StringTokenizer tokenizer = new StringTokenizer(
				usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();	
		
		Properties props = new Properties();
    	InputStream inputStream = AuthenticationService.class.getClassLoader().getResourceAsStream("/credentials.properties");
    	try {
			props.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally {
    		try {
    			if(inputStream != null)
    				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if("rest".equals(webservice)) {
		 authenticationStatus = props.getProperty("USERNAME").equals(username)
				&& props.getProperty("PASSWORD").equals(password);
    	}else if("pvApi".equals(webservice)) {
    		 authenticationStatus = props.getProperty("PVUSERNAME").equals(username)
    				&& props.getProperty("PVPASSWORD").equals(password);
    	}
		return authenticationStatus;
	}
}
