package org.apache.juddi.v3.tck;

/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

import com.sun.xml.stream.xerces.util.URI;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class JUDDI_091_RMISubscriptionListenerIntegrationTest
{
	private static Log logger = LogFactory.getLog(JUDDI_091_RMISubscriptionListenerIntegrationTest.class);

	private static TckTModel tckTModel                    = null;
	private static TckBusiness tckBusiness                = null;
	private static TckBusinessService tckBusinessService  = null;
	private static TckSubscriptionListenerRMI rmiSubscriptionListener = null;
	
	private static String authInfoJoe = null;
	private static UDDIClerkManager manager;
	private static UDDISubscriptionListenerImpl rmiSubscriptionListenerService = null;
	private static Registry registry;

	@AfterClass
	public static void stopManager() throws ConfigurationException {
		manager.stop();
		//shutting down the TCK SubscriptionListener
		//re
	}
	
	@BeforeClass
	public static void startManager() throws ConfigurationException {
			
		try {
			//bring up the RMISubscriptionListener
			URI rmiEndPoint = new URI("rmi://localhost:9876/tck/rmisubscriptionlistener");
			registry = LocateRegistry.createRegistry(rmiEndPoint.getPort());
			String path = rmiEndPoint.getPath();
			
			//starting the service
			rmiSubscriptionListenerService = new UDDISubscriptionListenerImpl(0);
			//binding to the RMI Registry
			registry.bind(path,rmiSubscriptionListenerService);
			
			//double check that the service is bound in the local Registry
			Registry registry2 = LocateRegistry.getRegistry(rmiEndPoint.getHost(), rmiEndPoint.getPort());
			registry2.lookup(rmiEndPoint.getPath());
           
            
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			Assert.fail();
		}

		manager  = new UDDIClerkManager();
		manager.start();
		
		logger.debug("Getting auth tokens..");
		try {
			 
			 Transport transport = manager.getTransport();
        	 UDDISecurityPortType security = transport.getUDDISecurityService();
        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
        	 Assert.assertNotNull(authInfoJoe);
        	 UDDISubscriptionPortType subscription = transport.getUDDISubscriptionService();
        	 
        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
        	 tckTModel  = new TckTModel(publication, inquiry);
        	 tckBusiness = new TckBusiness(publication, inquiry);
        	 tckBusinessService = new TckBusinessService(publication, inquiry);
        	 rmiSubscriptionListener = new TckSubscriptionListenerRMI(subscription, publication);	
        	  
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@Test
	public void joePublisher() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			rmiSubscriptionListener.saveService(authInfoJoe);
			
			rmiSubscriptionListener.saveNotifierSubscription(authInfoJoe);

			tckBusinessService.updateJoePublisherService(authInfoJoe, "foo");
			
            //waiting up to 100 seconds for the listener to notice the change.
			String test="";
			for (int i=0; i<200; i++) {
				Thread.sleep(500);
				System.out.print(".");
				if (UDDISubscriptionListenerImpl.notificationCount > 0) {
					break;
				} else {
					System.out.print(test);
				}
			}
			if (UDDISubscriptionListenerImpl.notificationCount == 0) {
				Assert.fail("No Notification was sent");
			}
			if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Service One</name>")) {
				Assert.fail("Notification does not contain the correct service");
			}
			
		} catch (Exception e) {
			e.printStackTrace();

			Assert.fail();
		} finally {
			
			rmiSubscriptionListener.deleteNotifierSubscription(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
    
}
