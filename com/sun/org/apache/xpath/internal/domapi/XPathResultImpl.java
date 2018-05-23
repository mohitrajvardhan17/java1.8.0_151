package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathResult;

class XPathResultImpl
  implements XPathResult, EventListener
{
  private final XObject m_resultObj;
  private final XPath m_xpath;
  private final short m_resultType;
  private boolean m_isInvalidIteratorState = false;
  private final Node m_contextNode;
  private NodeIterator m_iterator = null;
  private NodeList m_list = null;
  
  XPathResultImpl(short paramShort, XObject paramXObject, Node paramNode, XPath paramXPath)
  {
    String str1;
    if (!isValidType(paramShort))
    {
      str1 = XPATHMessages.createXPATHMessage("ER_INVALID_XPATH_TYPE", new Object[] { new Integer(paramShort) });
      throw new XPathException((short)2, str1);
    }
    if (null == paramXObject)
    {
      str1 = XPATHMessages.createXPATHMessage("ER_EMPTY_XPATH_RESULT", null);
      throw new XPathException((short)1, str1);
    }
    m_resultObj = paramXObject;
    m_contextNode = paramNode;
    m_xpath = paramXPath;
    if (paramShort == 0) {
      m_resultType = getTypeFromXObject(paramXObject);
    } else {
      m_resultType = paramShort;
    }
    if ((m_resultType == 5) || (m_resultType == 4)) {
      addEventListener();
    }
    String str2;
    if ((m_resultType == 5) || (m_resultType == 4) || (m_resultType == 8) || (m_resultType == 9)) {
      try
      {
        m_iterator = m_resultObj.nodeset();
      }
      catch (TransformerException localTransformerException1)
      {
        str2 = XPATHMessages.createXPATHMessage("ER_INCOMPATIBLE_TYPES", new Object[] { m_xpath.getPatternString(), getTypeString(getTypeFromXObject(m_resultObj)), getTypeString(m_resultType) });
        throw new XPathException((short)2, str2);
      }
    } else if ((m_resultType == 6) || (m_resultType == 7)) {
      try
      {
        m_list = m_resultObj.nodelist();
      }
      catch (TransformerException localTransformerException2)
      {
        str2 = XPATHMessages.createXPATHMessage("ER_INCOMPATIBLE_TYPES", new Object[] { m_xpath.getPatternString(), getTypeString(getTypeFromXObject(m_resultObj)), getTypeString(m_resultType) });
        throw new XPathException((short)2, str2);
      }
    }
  }
  
  public short getResultType()
  {
    return m_resultType;
  }
  
  public double getNumberValue()
    throws XPathException
  {
    if (getResultType() != 1)
    {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_XPATHRESULTTYPE_TO_NUMBER", new Object[] { m_xpath.getPatternString(), getTypeString(m_resultType) });
      throw new XPathException((short)2, str);
    }
    try
    {
      return m_resultObj.num();
    }
    catch (Exception localException)
    {
      throw new XPathException((short)2, localException.getMessage());
    }
  }
  
  public String getStringValue()
    throws XPathException
  {
    if (getResultType() != 2)
    {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_TO_STRING", new Object[] { m_xpath.getPatternString(), m_resultObj.getTypeString() });
      throw new XPathException((short)2, str);
    }
    try
    {
      return m_resultObj.str();
    }
    catch (Exception localException)
    {
      throw new XPathException((short)2, localException.getMessage());
    }
  }
  
  public boolean getBooleanValue()
    throws XPathException
  {
    if (getResultType() != 3)
    {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_TO_BOOLEAN", new Object[] { m_xpath.getPatternString(), getTypeString(m_resultType) });
      throw new XPathException((short)2, str);
    }
    try
    {
      return m_resultObj.bool();
    }
    catch (TransformerException localTransformerException)
    {
      throw new XPathException((short)2, localTransformerException.getMessage());
    }
  }
  
  public Node getSingleNodeValue()
    throws XPathException
  {
    if ((m_resultType != 8) && (m_resultType != 9))
    {
      localObject = XPATHMessages.createXPATHMessage("ER_CANT_CONVERT_TO_SINGLENODE", new Object[] { m_xpath.getPatternString(), getTypeString(m_resultType) });
      throw new XPathException((short)2, (String)localObject);
    }
    Object localObject = null;
    try
    {
      localObject = m_resultObj.nodeset();
    }
    catch (TransformerException localTransformerException)
    {
      throw new XPathException((short)2, localTransformerException.getMessage());
    }
    if (null == localObject) {
      return null;
    }
    Node localNode = ((NodeIterator)localObject).nextNode();
    if (isNamespaceNode(localNode)) {
      return new XPathNamespaceImpl(localNode);
    }
    return localNode;
  }
  
  public boolean getInvalidIteratorState()
  {
    return m_isInvalidIteratorState;
  }
  
  public int getSnapshotLength()
    throws XPathException
  {
    if ((m_resultType != 6) && (m_resultType != 7))
    {
      String str = XPATHMessages.createXPATHMessage("ER_CANT_GET_SNAPSHOT_LENGTH", new Object[] { m_xpath.getPatternString(), getTypeString(m_resultType) });
      throw new XPathException((short)2, str);
    }
    return m_list.getLength();
  }
  
  public Node iterateNext()
    throws XPathException, DOMException
  {
    if ((m_resultType != 4) && (m_resultType != 5))
    {
      localObject = XPATHMessages.createXPATHMessage("ER_NON_ITERATOR_TYPE", new Object[] { m_xpath.getPatternString(), getTypeString(m_resultType) });
      throw new XPathException((short)2, (String)localObject);
    }
    if (getInvalidIteratorState())
    {
      localObject = XPATHMessages.createXPATHMessage("ER_DOC_MUTATED", null);
      throw new DOMException((short)11, (String)localObject);
    }
    Object localObject = m_iterator.nextNode();
    if (null == localObject) {
      removeEventListener();
    }
    if (isNamespaceNode((Node)localObject)) {
      return new XPathNamespaceImpl((Node)localObject);
    }
    return (Node)localObject;
  }
  
  public Node snapshotItem(int paramInt)
    throws XPathException
  {
    if ((m_resultType != 6) && (m_resultType != 7))
    {
      localObject = XPATHMessages.createXPATHMessage("ER_NON_SNAPSHOT_TYPE", new Object[] { m_xpath.getPatternString(), getTypeString(m_resultType) });
      throw new XPathException((short)2, (String)localObject);
    }
    Object localObject = m_list.item(paramInt);
    if (isNamespaceNode((Node)localObject)) {
      return new XPathNamespaceImpl((Node)localObject);
    }
    return (Node)localObject;
  }
  
  static boolean isValidType(short paramShort)
  {
    switch (paramShort)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
      return true;
    }
    return false;
  }
  
  public void handleEvent(Event paramEvent)
  {
    if (paramEvent.getType().equals("DOMSubtreeModified"))
    {
      m_isInvalidIteratorState = true;
      removeEventListener();
    }
  }
  
  private String getTypeString(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return "ANY_TYPE";
    case 8: 
      return "ANY_UNORDERED_NODE_TYPE";
    case 3: 
      return "BOOLEAN";
    case 9: 
      return "FIRST_ORDERED_NODE_TYPE";
    case 1: 
      return "NUMBER_TYPE";
    case 5: 
      return "ORDERED_NODE_ITERATOR_TYPE";
    case 7: 
      return "ORDERED_NODE_SNAPSHOT_TYPE";
    case 2: 
      return "STRING_TYPE";
    case 4: 
      return "UNORDERED_NODE_ITERATOR_TYPE";
    case 6: 
      return "UNORDERED_NODE_SNAPSHOT_TYPE";
    }
    return "#UNKNOWN";
  }
  
  private short getTypeFromXObject(XObject paramXObject)
  {
    switch (paramXObject.getType())
    {
    case 1: 
      return 3;
    case 4: 
      return 4;
    case 2: 
      return 1;
    case 3: 
      return 2;
    case 5: 
      return 4;
    case -1: 
      return 0;
    }
    return 0;
  }
  
  private boolean isNamespaceNode(Node paramNode)
  {
    return (null != paramNode) && (paramNode.getNodeType() == 2) && ((paramNode.getNodeName().startsWith("xmlns:")) || (paramNode.getNodeName().equals("xmlns")));
  }
  
  private void addEventListener()
  {
    if ((m_contextNode instanceof EventTarget)) {
      ((EventTarget)m_contextNode).addEventListener("DOMSubtreeModified", this, true);
    }
  }
  
  private void removeEventListener()
  {
    if ((m_contextNode instanceof EventTarget)) {
      ((EventTarget)m_contextNode).removeEventListener("DOMSubtreeModified", this, true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */