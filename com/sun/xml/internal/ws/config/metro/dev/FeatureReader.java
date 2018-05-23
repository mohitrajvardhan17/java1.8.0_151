package com.sun.xml.internal.ws.config.metro.dev;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public abstract interface FeatureReader<T extends WebServiceFeature>
{
  public static final QName ENABLED_ATTRIBUTE_NAME = new QName("enabled");
  
  public abstract T parse(XMLEventReader paramXMLEventReader)
    throws WebServiceException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\config\metro\dev\FeatureReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */