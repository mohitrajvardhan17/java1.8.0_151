package com.sun.xml.internal.ws.encoding.xml;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;

public class XMLPropertyBag
  extends BasePropertySet
{
  private String contentType;
  private static final BasePropertySet.PropertyMap model = parse(XMLPropertyBag.class);
  
  public XMLPropertyBag() {}
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
  
  @PropertySet.Property({"com.sun.jaxws.rest.contenttype"})
  public String getXMLContentType()
  {
    return contentType;
  }
  
  public void setXMLContentType(String paramString)
  {
    contentType = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\xml\XMLPropertyBag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */