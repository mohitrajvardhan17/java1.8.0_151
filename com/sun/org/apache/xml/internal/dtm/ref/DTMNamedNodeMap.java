package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DTMNamedNodeMap
  implements NamedNodeMap
{
  DTM dtm;
  int element;
  short m_count = -1;
  
  public DTMNamedNodeMap(DTM paramDTM, int paramInt)
  {
    dtm = paramDTM;
    element = paramInt;
  }
  
  public int getLength()
  {
    if (m_count == -1)
    {
      short s = 0;
      for (int i = dtm.getFirstAttribute(element); i != -1; i = dtm.getNextAttribute(i)) {
        s = (short)(s + 1);
      }
      m_count = s;
    }
    return m_count;
  }
  
  public Node getNamedItem(String paramString)
  {
    for (int i = dtm.getFirstAttribute(element); i != -1; i = dtm.getNextAttribute(i)) {
      if (dtm.getNodeName(i).equals(paramString)) {
        return dtm.getNode(i);
      }
    }
    return null;
  }
  
  public Node item(int paramInt)
  {
    int i = 0;
    for (int j = dtm.getFirstAttribute(element); j != -1; j = dtm.getNextAttribute(j))
    {
      if (i == paramInt) {
        return dtm.getNode(j);
      }
      i++;
    }
    return null;
  }
  
  public Node setNamedItem(Node paramNode)
  {
    throw new DTMException((short)7);
  }
  
  public Node removeNamedItem(String paramString)
  {
    throw new DTMException((short)7);
  }
  
  public Node getNamedItemNS(String paramString1, String paramString2)
  {
    Node localNode = null;
    for (int i = dtm.getFirstAttribute(element); i != -1; i = dtm.getNextAttribute(i)) {
      if (paramString2.equals(dtm.getLocalName(i)))
      {
        String str = dtm.getNamespaceURI(i);
        if (((paramString1 == null) && (str == null)) || ((paramString1 != null) && (paramString1.equals(str))))
        {
          localNode = dtm.getNode(i);
          break;
        }
      }
    }
    return localNode;
  }
  
  public Node setNamedItemNS(Node paramNode)
    throws DOMException
  {
    throw new DTMException((short)7);
  }
  
  public Node removeNamedItemNS(String paramString1, String paramString2)
    throws DOMException
  {
    throw new DTMException((short)7);
  }
  
  public class DTMException
    extends DOMException
  {
    static final long serialVersionUID = -8290238117162437678L;
    
    public DTMException(short paramShort, String paramString)
    {
      super(paramString);
    }
    
    public DTMException(short paramShort)
    {
      super("");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNamedNodeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */