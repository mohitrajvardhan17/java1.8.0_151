package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ExsltBase
{
  public ExsltBase() {}
  
  protected static String toString(Node paramNode)
  {
    if ((paramNode instanceof DTMNodeProxy)) {
      return ((DTMNodeProxy)paramNode).getStringValue();
    }
    String str = paramNode.getNodeValue();
    if (str == null)
    {
      NodeList localNodeList = paramNode.getChildNodes();
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < localNodeList.getLength(); i++)
      {
        Node localNode = localNodeList.item(i);
        localStringBuffer.append(toString(localNode));
      }
      return localStringBuffer.toString();
    }
    return str;
  }
  
  protected static double toNumber(Node paramNode)
  {
    double d = 0.0D;
    String str = toString(paramNode);
    try
    {
      d = Double.valueOf(str).doubleValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      d = NaN.0D;
    }
    return d;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */