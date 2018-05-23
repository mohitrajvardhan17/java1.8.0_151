package com.sun.xml.internal.ws.server;

import com.sun.org.glassfish.gmbal.Description;
import com.sun.org.glassfish.gmbal.InheritedAttributes;
import com.sun.org.glassfish.gmbal.ManagedData;

@ManagedData
@Description("WebServiceFeature")
@InheritedAttributes({@com.sun.org.glassfish.gmbal.InheritedAttribute(methodName="getID", description="unique id for this feature"), @com.sun.org.glassfish.gmbal.InheritedAttribute(methodName="isEnabled", description="true if this feature is enabled")})
abstract interface DummyWebServiceFeature {}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\DummyWebServiceFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */