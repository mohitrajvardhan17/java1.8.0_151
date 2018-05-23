package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.net.URL;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

public abstract interface LocatorEx
  extends Locator
{
  public abstract ValidationEventLocator getLocation();
  
  public static final class Snapshot
    implements LocatorEx, ValidationEventLocator
  {
    private final int columnNumber;
    private final int lineNumber;
    private final int offset;
    private final String systemId;
    private final String publicId;
    private final URL url;
    private final Object object;
    private final Node node;
    
    public Snapshot(LocatorEx paramLocatorEx)
    {
      columnNumber = paramLocatorEx.getColumnNumber();
      lineNumber = paramLocatorEx.getLineNumber();
      systemId = paramLocatorEx.getSystemId();
      publicId = paramLocatorEx.getPublicId();
      ValidationEventLocator localValidationEventLocator = paramLocatorEx.getLocation();
      offset = localValidationEventLocator.getOffset();
      url = localValidationEventLocator.getURL();
      object = localValidationEventLocator.getObject();
      node = localValidationEventLocator.getNode();
    }
    
    public Object getObject()
    {
      return object;
    }
    
    public Node getNode()
    {
      return node;
    }
    
    public int getOffset()
    {
      return offset;
    }
    
    public URL getURL()
    {
      return url;
    }
    
    public int getColumnNumber()
    {
      return columnNumber;
    }
    
    public int getLineNumber()
    {
      return lineNumber;
    }
    
    public String getSystemId()
    {
      return systemId;
    }
    
    public String getPublicId()
    {
      return publicId;
    }
    
    public ValidationEventLocator getLocation()
    {
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\LocatorEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */