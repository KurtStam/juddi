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

import java.util.Vector;

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.Registry;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveBusinessSample
{
  public static void main(String[] args)
  {
    Registry registry = new RegistryProxy();

    try
    {
      // execute a GetAuthToken request
      AuthToken authToken = registry.getAuthToken("sviens","password");
      AuthInfo authInfo = authToken.getAuthInfo();

      // generate a Name Vector
      Vector nameVector = new Vector();
      nameVector.add(new Name("Fidelity Investments"));
      nameVector.add(new Name("FISC"));
      nameVector.add(new Name("FEB"));
      nameVector.add(new Name("FCAT"));

      // generate a BusinessEntity
      BusinessEntity businessEntity = new BusinessEntity();
      businessEntity.setBusinessKey(null);
      businessEntity.setNameVector(nameVector);

      // generate a BusinessEntity Vector
      Vector businessVector = new Vector();
      businessVector.add(businessEntity);

      // execute the SaveBusiness request
      BusinessDetail detail = registry.saveBusiness(authInfo,businessVector);

      System.out.println("businesses saved = "+detail.getBusinessEntityVector().size());
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}