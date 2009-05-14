/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.portlets.server.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.model.ServiceBinding;
import org.apache.juddi.portlets.client.service.InquiryResponse;
import org.apache.juddi.portlets.client.service.InquiryService;
import org.apache.juddi.portlets.client.service.NotifyResponse;
import org.apache.juddi.portlets.client.service.NotifyService;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.i18n.EntityForLang;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 *
 */
public class NotifyServiceImpl extends RemoteServiceServlet implements NotifyService {
	private Logger logger = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private Transport transport = null;
	
	public NotifyServiceImpl() {
		super();	
	}
	
	private Transport getTransport() 
		throws ConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (transport==null) {
			String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	        Class<?> transportClass = Loader.loadClass(clazz);
	   	 	transport = (Transport) transportClass.newInstance(); 
		}
		return transport;
	}

	public NotifyResponse getSubscriptionNotifications(String authToken) 
	{
		HttpServletRequest request = this.getThreadLocalRequest();
		String lang = request.getLocale().getLanguage();

		NotifySubscriptionListener nsl = new NotifySubscriptionListener();
		nsl.setAuthInfo(authToken);
		NotifyResponse response = new NotifyResponse();
		//logger.debug("BusinessDetail " + getBusinessDetail + " sending businessDetail request..");
		try {
        	 //UDDISubscriptionListenerPortType notifyService = getTransport().getSubscriptionService().getSubscriptionResults();
        	 /*
        	 BusinessDetail businessDetail = notifyService.

        	 for (BusinessEntity businessEntity : businessDetail.getBusinessEntity()) {
        		 Business business = new Business(
        				 businessEntity.getBusinessKey(),
        				 EntityForLang.get(businessEntity.getName(),lang).getValue(),
        				 EntityForLang.get(businessEntity.getDescription(),lang).getValue());
        		 for (BusinessService businessService : businessEntity.getBusinessServices().getBusinessService()) {
        			 Service service = new Service(
        					 businessService.getServiceKey(),
        					 EntityForLang.get(businessService.getName(),lang).getValue(),
        					 EntityForLang.get(businessService.getDescription(),lang).getValue());
        			 business.getServices().add(service);
        		 }
        		 response.setBusiness(business);
        	 */
        	 
        	 response.setSuccess(true);
	     } catch (Exception e) {
	    	 logger.error("Could not obtain token. " + e.getMessage(), e);
	    	 response.setSuccess(false);
	    	 response.setMessage(e.getMessage());
	    	 response.setErrorCode("102");
	     }  catch (Throwable t) {
	    	 logger.error("Could not obtain token. " + t.getMessage(), t);
	    	 response.setSuccess(false);
	    	 response.setMessage(t.getMessage());
	    	 response.setErrorCode("102");
	     } 
		 return response;
	}	
}
