/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 */

import java.util.Properties;
import java.util.Vector;

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.BusinessInfos;
import org.apache.juddi.datatype.response.BusinessList;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.proxy.RegistryProxy;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindBusinessSample
{
  public static void main(String[] args) throws Exception
  {
    // Option #1 (grab proxy property values from juddi.properties in classpath)
    //IRegistry registry = new RegistryProxy();

    // Option #2 (import proxy property values from a specified properties file)
  	//Properties props = new Properties();
  	//props.load(new FileInputStream(args[0]));
  	//IRegistry registry = new RegistryProxy(props);

    // Option #3 (explicitly set the proxy property values)
    Properties props = new Properties();
    props.setProperty(RegistryProxy.ADMIN_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/admin");
    props.setProperty(RegistryProxy.INQUIRY_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/inquiry");
    props.setProperty(RegistryProxy.PUBLISH_ENDPOINT_PROPERTY_NAME,"http://localhost:8080/juddi/publish");
    props.setProperty(RegistryProxy.TRANSPORT_CLASS_PROPERTY_NAME,"org.apache.juddi.proxy.AxisTransport");
    props.setProperty(RegistryProxy.SECURITY_PROVIDER_PROPERTY_NAME,"com.sun.net.ssl.internal.ssl.Provider");
    props.setProperty(RegistryProxy.PROTOCOL_HANDLER_PROPERTY_NAME,"com.sun.net.ssl.internal.www.protocol");
    IRegistry registry = new RegistryProxy(props);

    // Option #4 (Microsoft Test Site)
//    Properties props = new Properties();
//    props.setProperty(RegistryProxy.INQUIRY_ENDPOINT_PROPERTY_NAME,"http://test.uddi.microsoft.com/inquire");
//    props.setProperty(RegistryProxy.PUBLISH_ENDPOINT_PROPERTY_NAME,"https://test.uddi.microsoft.com/publish");
//    props.setProperty(RegistryProxy.TRANSPORT_CLASS_PROPERTY_NAME,"org.apache.juddi.proxy.AxisTransport");
//    props.setProperty(RegistryProxy.SECURITY_PROVIDER_PROPERTY_NAME,"com.sun.net.ssl.internal.ssl.Provider");
//    props.setProperty(RegistryProxy.PROTOCOL_HANDLER_PROPERTY_NAME,"com.sun.net.ssl.internal.www.protocol");
//    props.setProperty(RegistryProxy.HTTP_PROXY_SET_PROPERTY_NAME,"false");
//    props.setProperty(RegistryProxy.HTTP_PROXY_HOST_PROPERTY_NAME,"na6v13a01.fmr.com");
//    props.setProperty(RegistryProxy.HTTP_PROXY_PORT_PROPERTY_NAME,"8000");
//    IRegistry registry = new RegistryProxy(props);

    try
    {
      Vector inNames = new Vector();
      inNames.add(new Name(args[0]));

      BusinessList list = registry.findBusiness(inNames,null,null,null,null,null,100);
      BusinessInfos infos = list.getBusinessInfos();

      Vector businesses = infos.getBusinessInfoVector();

      if (businesses != null)
      {
        for (int i=0; i<businesses.size(); i++)
        {
          BusinessInfo info = (BusinessInfo)businesses.elementAt(i);
          Vector outNames = info.getNameVector();
          for (int j=0; j<outNames.size(); j++)
          {
            Name name = (Name)outNames.elementAt(j);
            System.out.println(name.getValue());
          }
        }
      }
    }
    catch (RegistryException regex)
    {
      DispositionReport dispReport = regex.getDispositionReport();
      Vector resultVector = dispReport.getResultVector();
      if (resultVector != null)
      {
        Result result = (Result)resultVector.elementAt(0);
        int errNo = result.getErrno();
        System.out.println("Errno:   "+errNo);

        ErrInfo errInfo = result.getErrInfo();
        if (errInfo != null)
        {
          System.out.println("ErrCode: "+errInfo.getErrCode());
          System.out.println("ErrMsg:  "+errInfo.getErrMsg());
        }
      }
    }

    System.out.println("\ndone.");
  }
}