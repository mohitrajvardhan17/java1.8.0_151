package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.State;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.State;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import java.util.HashMap;
import javax.xml.stream.XMLResolver;

public class PropertyManager
{
  public static final String STAX_NOTATIONS = "javax.xml.stream.notations";
  public static final String STAX_ENTITIES = "javax.xml.stream.entities";
  private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  HashMap supportedProps = new HashMap();
  private XMLSecurityManager fSecurityManager;
  private XMLSecurityPropertyManager fSecurityPropertyMgr;
  public static final int CONTEXT_READER = 1;
  public static final int CONTEXT_WRITER = 2;
  
  public PropertyManager(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      initConfigurableReaderProperties();
      break;
    case 2: 
      initWriterProps();
    }
  }
  
  public PropertyManager(PropertyManager paramPropertyManager)
  {
    HashMap localHashMap = paramPropertyManager.getProperties();
    supportedProps.putAll(localHashMap);
    fSecurityManager = ((XMLSecurityManager)getProperty("http://apache.org/xml/properties/security-manager"));
    fSecurityPropertyMgr = ((XMLSecurityPropertyManager)getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
  }
  
  private HashMap getProperties()
  {
    return supportedProps;
  }
  
  private void initConfigurableReaderProperties()
  {
    supportedProps.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
    supportedProps.put("javax.xml.stream.isValidating", Boolean.FALSE);
    supportedProps.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
    supportedProps.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
    supportedProps.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
    supportedProps.put("javax.xml.stream.supportDTD", Boolean.TRUE);
    supportedProps.put("javax.xml.stream.reporter", null);
    supportedProps.put("javax.xml.stream.resolver", null);
    supportedProps.put("javax.xml.stream.allocator", null);
    supportedProps.put("javax.xml.stream.notations", null);
    supportedProps.put("http://xml.org/sax/features/string-interning", new Boolean(true));
    supportedProps.put("http://apache.org/xml/features/allow-java-encodings", new Boolean(true));
    supportedProps.put("add-namespacedecl-as-attrbiute", Boolean.FALSE);
    supportedProps.put("http://java.sun.com/xml/stream/properties/reader-in-defined-state", new Boolean(true));
    supportedProps.put("reuse-instance", new Boolean(true));
    supportedProps.put("http://java.sun.com/xml/stream/properties/report-cdata-event", new Boolean(false));
    supportedProps.put("http://java.sun.com/xml/stream/properties/ignore-external-dtd", Boolean.FALSE);
    supportedProps.put("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", new Boolean(false));
    supportedProps.put("http://apache.org/xml/features/warn-on-duplicate-entitydef", new Boolean(false));
    supportedProps.put("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", new Boolean(false));
    fSecurityManager = new XMLSecurityManager(true);
    supportedProps.put("http://apache.org/xml/properties/security-manager", fSecurityManager);
    fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    supportedProps.put("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
  }
  
  private void initWriterProps()
  {
    supportedProps.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
    supportedProps.put("escapeCharacters", Boolean.TRUE);
    supportedProps.put("reuse-instance", new Boolean(true));
  }
  
  public boolean containsProperty(String paramString)
  {
    return (supportedProps.containsKey(paramString)) || ((fSecurityManager != null) && (fSecurityManager.getIndex(paramString) > -1)) || ((fSecurityPropertyMgr != null) && (fSecurityPropertyMgr.getIndex(paramString) > -1));
  }
  
  public Object getProperty(String paramString)
  {
    return supportedProps.get(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    String str = null;
    if ((paramString == "javax.xml.stream.isNamespaceAware") || (paramString.equals("javax.xml.stream.isNamespaceAware"))) {
      str = "http://apache.org/xml/features/namespaces";
    } else if ((paramString == "javax.xml.stream.isValidating") || (paramString.equals("javax.xml.stream.isValidating")))
    {
      if (((paramObject instanceof Boolean)) && (((Boolean)paramObject).booleanValue())) {
        throw new IllegalArgumentException("true value of isValidating not supported");
      }
    }
    else if ((paramString == "http://xml.org/sax/features/string-interning") || (paramString.equals("http://xml.org/sax/features/string-interning")))
    {
      if (((paramObject instanceof Boolean)) && (!((Boolean)paramObject).booleanValue())) {
        throw new IllegalArgumentException("false value of http://xml.org/sax/features/string-interningfeature is not supported");
      }
    }
    else if ((paramString == "javax.xml.stream.resolver") || (paramString.equals("javax.xml.stream.resolver"))) {
      supportedProps.put("http://apache.org/xml/properties/internal/stax-entity-resolver", new StaxEntityResolverWrapper((XMLResolver)paramObject));
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
    {
      fSecurityManager = XMLSecurityManager.convert(paramObject, fSecurityManager);
      supportedProps.put("http://apache.org/xml/properties/security-manager", fSecurityManager);
      return;
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      if (paramObject == null) {
        fSecurityPropertyMgr = new XMLSecurityPropertyManager();
      } else {
        fSecurityPropertyMgr = ((XMLSecurityPropertyManager)paramObject);
      }
      supportedProps.put("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
      return;
    }
    if (((fSecurityManager == null) || (!fSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject))) && ((fSecurityPropertyMgr == null) || (!fSecurityPropertyMgr.setValue(paramString, XMLSecurityPropertyManager.State.APIPROPERTY, paramObject)))) {
      supportedProps.put(paramString, paramObject);
    }
    if (str != null) {
      supportedProps.put(str, paramObject);
    }
  }
  
  public String toString()
  {
    return supportedProps.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\PropertyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */