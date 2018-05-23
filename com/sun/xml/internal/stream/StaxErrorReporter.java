package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;

public class StaxErrorReporter
  extends XMLErrorReporter
{
  protected XMLReporter fXMLReporter = null;
  
  public StaxErrorReporter(PropertyManager paramPropertyManager)
  {
    putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter());
    reset(paramPropertyManager);
  }
  
  public StaxErrorReporter()
  {
    putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter());
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    fXMLReporter = ((XMLReporter)paramPropertyManager.getProperty("javax.xml.stream.reporter"));
  }
  
  public String reportError(XMLLocator paramXMLLocator, String paramString1, String paramString2, Object[] paramArrayOfObject, short paramShort)
    throws XNIException
  {
    MessageFormatter localMessageFormatter = getMessageFormatter(paramString1);
    String str;
    if (localMessageFormatter != null)
    {
      str = localMessageFormatter.formatMessage(fLocale, paramString2, paramArrayOfObject);
    }
    else
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(paramString1);
      localStringBuffer.append('#');
      localStringBuffer.append(paramString2);
      int i = paramArrayOfObject != null ? paramArrayOfObject.length : 0;
      if (i > 0)
      {
        localStringBuffer.append('?');
        for (int j = 0; j < i; j++)
        {
          localStringBuffer.append(paramArrayOfObject[j]);
          if (j < i - 1) {
            localStringBuffer.append('&');
          }
        }
      }
      str = localStringBuffer.toString();
    }
    switch (paramShort)
    {
    case 0: 
      try
      {
        if (fXMLReporter != null) {
          fXMLReporter.report(str, "WARNING", null, convertToStaxLocation(paramXMLLocator));
        }
      }
      catch (XMLStreamException localXMLStreamException1)
      {
        throw new XNIException(localXMLStreamException1);
      }
    case 1: 
      try
      {
        if (fXMLReporter != null) {
          fXMLReporter.report(str, "ERROR", null, convertToStaxLocation(paramXMLLocator));
        }
      }
      catch (XMLStreamException localXMLStreamException2)
      {
        throw new XNIException(localXMLStreamException2);
      }
    case 2: 
      if (!fContinueAfterFatalError) {
        throw new XNIException(str);
      }
      break;
    }
    return str;
  }
  
  Location convertToStaxLocation(final XMLLocator paramXMLLocator)
  {
    new Location()
    {
      public int getColumnNumber()
      {
        return paramXMLLocator.getColumnNumber();
      }
      
      public int getLineNumber()
      {
        return paramXMLLocator.getLineNumber();
      }
      
      public String getPublicId()
      {
        return paramXMLLocator.getPublicId();
      }
      
      public String getSystemId()
      {
        return paramXMLLocator.getLiteralSystemId();
      }
      
      public int getCharacterOffset()
      {
        return paramXMLLocator.getCharacterOffset();
      }
      
      public String getLocationURI()
      {
        return "";
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\StaxErrorReporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */