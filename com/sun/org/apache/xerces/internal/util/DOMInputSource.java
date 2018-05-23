package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.Node;

public final class DOMInputSource
  extends XMLInputSource
{
  private Node fNode;
  
  public DOMInputSource()
  {
    this(null);
  }
  
  public DOMInputSource(Node paramNode)
  {
    super(null, getSystemIdFromNode(paramNode), null);
    fNode = paramNode;
  }
  
  public DOMInputSource(Node paramNode, String paramString)
  {
    super(null, paramString, null);
    fNode = paramNode;
  }
  
  public Node getNode()
  {
    return fNode;
  }
  
  public void setNode(Node paramNode)
  {
    fNode = paramNode;
  }
  
  private static String getSystemIdFromNode(Node paramNode)
  {
    if (paramNode != null) {
      try
      {
        return paramNode.getBaseURI();
      }
      catch (NoSuchMethodError localNoSuchMethodError)
      {
        return null;
      }
      catch (Exception localException)
      {
        return null;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\DOMInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */