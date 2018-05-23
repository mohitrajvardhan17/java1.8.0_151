package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class XNodeSetForDOM
  extends XNodeSet
{
  static final long serialVersionUID = -8396190713754624640L;
  Object m_origObj;
  
  public XNodeSetForDOM(Node paramNode, DTMManager paramDTMManager)
  {
    m_dtmMgr = paramDTMManager;
    m_origObj = paramNode;
    int i = paramDTMManager.getDTMHandleFromNode(paramNode);
    setObject(new NodeSetDTM(paramDTMManager));
    ((NodeSetDTM)m_obj).addNode(i);
  }
  
  public XNodeSetForDOM(XNodeSet paramXNodeSet)
  {
    super(paramXNodeSet);
    if ((paramXNodeSet instanceof XNodeSetForDOM)) {
      m_origObj = m_origObj;
    }
  }
  
  public XNodeSetForDOM(NodeList paramNodeList, XPathContext paramXPathContext)
  {
    m_dtmMgr = paramXPathContext.getDTMManager();
    m_origObj = paramNodeList;
    NodeSetDTM localNodeSetDTM = new NodeSetDTM(paramNodeList, paramXPathContext);
    m_last = localNodeSetDTM.getLength();
    setObject(localNodeSetDTM);
  }
  
  public XNodeSetForDOM(NodeIterator paramNodeIterator, XPathContext paramXPathContext)
  {
    m_dtmMgr = paramXPathContext.getDTMManager();
    m_origObj = paramNodeIterator;
    NodeSetDTM localNodeSetDTM = new NodeSetDTM(paramNodeIterator, paramXPathContext);
    m_last = localNodeSetDTM.getLength();
    setObject(localNodeSetDTM);
  }
  
  public Object object()
  {
    return m_origObj;
  }
  
  public NodeIterator nodeset()
    throws TransformerException
  {
    return (m_origObj instanceof NodeIterator) ? (NodeIterator)m_origObj : super.nodeset();
  }
  
  public NodeList nodelist()
    throws TransformerException
  {
    return (m_origObj instanceof NodeList) ? (NodeList)m_origObj : super.nodelist();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XNodeSetForDOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */