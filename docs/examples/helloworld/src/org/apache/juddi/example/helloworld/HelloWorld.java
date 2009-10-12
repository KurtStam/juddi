package org.apache.juddi.examples.helloworld;

import org.apache.log4j.helpers.Loader;

import org.uddi.api_v3.*;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDISecurityPortType;

public class HelloWorld {
	private Transport transport = null;
	private static UDDISecurityPortType security = null;

	public HelloWorld() {
                try {
	                String clazz = UDDIClerkManager.getClientConfig().getNodes().get("default").getProxyTransport();
			Class<?> transportClass = Loader.loadClass(clazz);
			if (transportClass!=null) {
				Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance("default");
				security = transport.getUDDISecurityService();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void getAuthToken() {
		GetAuthToken getAuthToken = new GetAuthToken();
		getAuthToken.setUserID("root");
		getAuthToken.setCred("");
		try {
			AuthToken authToken = security.getAuthToken(getAuthToken);
			System.out.println ("AUTHTOKEN = " 
				+ authToken.getAuthInfo());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		

	public static void main (String args[]) {
		HelloWorld hw = new HelloWorld();
		hw.getAuthToken();	
	}
}