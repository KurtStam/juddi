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
package org.apache.juddi.handler;

import java.util.Date;
import java.util.Vector;

import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.subscription.Subscription;
import org.apache.juddi.datatype.subscription.SubscriptionFilter;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * SubscriptionHandler
 *
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class SubscriptionHandler extends AbstractHandler
{
  public static final String TAG_NAME = "subscription";

  private HandlerMaker maker = null;

  protected SubscriptionHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    Subscription obj = new Subscription();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setSubscriptionKey(element.getAttribute("subscriptionKey"));

    // Text Node Value
    // [none]

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,NameHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      // TODO (steve) fill out SubscriptoinHandler.unmarshal()
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Subscription subscription = (Subscription)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String subscriptionKey = subscription.getSubscriptionKey();
    if (subscriptionKey != null)
      element.setAttribute("subscriptionKey",subscriptionKey);

    // TODO (steve) fill out SubscriptoinHandler.marshal()
    
    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(SubscriptionHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Subscription subscription = new Subscription();
    subscription.setSubscriptionKey("uuid:269855db-62eb-4862-8e5a-1b06f2753038");
    subscription.setBindingKey(new BindingKey(""));
    subscription.setBrief(true);
    subscription.setExpiresAfter(new Date());
    subscription.setMaxEntities(100);
    subscription.setNotificationInterval("");
    subscription.setSubscriptionFilter(new SubscriptionFilter());

    System.out.println();

    RegistryObject regObject = subscription;
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);

    System.out.println();

    regObject = handler.unmarshal(child);
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);
  }
}