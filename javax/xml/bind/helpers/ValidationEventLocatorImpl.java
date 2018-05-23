package javax.xml.bind.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class ValidationEventLocatorImpl
  implements ValidationEventLocator
{
  private URL url = null;
  private int offset = -1;
  private int lineNumber = -1;
  private int columnNumber = -1;
  private Object object = null;
  private Node node = null;
  
  public ValidationEventLocatorImpl() {}
  
  public ValidationEventLocatorImpl(Locator paramLocator)
  {
    if (paramLocator == null) {
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "loc"));
    }
    url = toURL(paramLocator.getSystemId());
    columnNumber = paramLocator.getColumnNumber();
    lineNumber = paramLocator.getLineNumber();
  }
  
  public ValidationEventLocatorImpl(SAXParseException paramSAXParseException)
  {
    if (paramSAXParseException == null) {
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "e"));
    }
    url = toURL(paramSAXParseException.getSystemId());
    columnNumber = paramSAXParseException.getColumnNumber();
    lineNumber = paramSAXParseException.getLineNumber();
  }
  
  public ValidationEventLocatorImpl(Node paramNode)
  {
    if (paramNode == null) {
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "_node"));
    }
    node = paramNode;
  }
  
  public ValidationEventLocatorImpl(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "_object"));
    }
    object = paramObject;
  }
  
  private static URL toURL(String paramString)
  {
    try
    {
      return new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException) {}
    return null;
  }
  
  public URL getURL()
  {
    return url;
  }
  
  public void setURL(URL paramURL)
  {
    url = paramURL;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public void setOffset(int paramInt)
  {
    offset = paramInt;
  }
  
  public int getLineNumber()
  {
    return lineNumber;
  }
  
  public void setLineNumber(int paramInt)
  {
    lineNumber = paramInt;
  }
  
  public int getColumnNumber()
  {
    return columnNumber;
  }
  
  public void setColumnNumber(int paramInt)
  {
    columnNumber = paramInt;
  }
  
  public Object getObject()
  {
    return object;
  }
  
  public void setObject(Object paramObject)
  {
    object = paramObject;
  }
  
  public Node getNode()
  {
    return node;
  }
  
  public void setNode(Node paramNode)
  {
    node = paramNode;
  }
  
  public String toString()
  {
    return MessageFormat.format("[node={0},object={1},url={2},line={3},col={4},offset={5}]", new Object[] { getNode(), getObject(), getURL(), String.valueOf(getLineNumber()), String.valueOf(getColumnNumber()), String.valueOf(getOffset()) });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\helpers\ValidationEventLocatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */