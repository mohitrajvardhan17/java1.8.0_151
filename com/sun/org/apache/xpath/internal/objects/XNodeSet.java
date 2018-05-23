package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.axes.NodeSequence;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class XNodeSet
  extends NodeSequence
{
  static final long serialVersionUID = 1916026368035639667L;
  static final LessThanComparator S_LT = new LessThanComparator();
  static final LessThanOrEqualComparator S_LTE = new LessThanOrEqualComparator();
  static final GreaterThanComparator S_GT = new GreaterThanComparator();
  static final GreaterThanOrEqualComparator S_GTE = new GreaterThanOrEqualComparator();
  static final EqualComparator S_EQ = new EqualComparator();
  static final NotEqualComparator S_NEQ = new NotEqualComparator();
  
  protected XNodeSet() {}
  
  public XNodeSet(DTMIterator paramDTMIterator)
  {
    if ((paramDTMIterator instanceof XNodeSet))
    {
      XNodeSet localXNodeSet = (XNodeSet)paramDTMIterator;
      setIter(m_iter);
      m_dtmMgr = m_dtmMgr;
      m_last = m_last;
      if (!localXNodeSet.hasCache()) {
        localXNodeSet.setShouldCacheNodes(true);
      }
      setObject(localXNodeSet.getIteratorCache());
    }
    else
    {
      setIter(paramDTMIterator);
    }
  }
  
  public XNodeSet(XNodeSet paramXNodeSet)
  {
    setIter(m_iter);
    m_dtmMgr = m_dtmMgr;
    m_last = m_last;
    if (!paramXNodeSet.hasCache()) {
      paramXNodeSet.setShouldCacheNodes(true);
    }
    setObject(m_obj);
  }
  
  public XNodeSet(DTMManager paramDTMManager)
  {
    this(-1, paramDTMManager);
  }
  
  public XNodeSet(int paramInt, DTMManager paramDTMManager)
  {
    super(new NodeSetDTM(paramDTMManager));
    m_dtmMgr = paramDTMManager;
    if (-1 != paramInt)
    {
      ((NodeSetDTM)m_obj).addNode(paramInt);
      m_last = 1;
    }
    else
    {
      m_last = 0;
    }
  }
  
  public int getType()
  {
    return 4;
  }
  
  public String getTypeString()
  {
    return "#NODESET";
  }
  
  public double getNumberFromNode(int paramInt)
  {
    XMLString localXMLString = m_dtmMgr.getDTM(paramInt).getStringValue(paramInt);
    return localXMLString.toDouble();
  }
  
  public double num()
  {
    int i = item(0);
    return i != -1 ? getNumberFromNode(i) : NaN.0D;
  }
  
  public double numWithSideEffects()
  {
    int i = nextNode();
    return i != -1 ? getNumberFromNode(i) : NaN.0D;
  }
  
  public boolean bool()
  {
    return item(0) != -1;
  }
  
  public boolean boolWithSideEffects()
  {
    return nextNode() != -1;
  }
  
  public XMLString getStringFromNode(int paramInt)
  {
    if (-1 != paramInt) {
      return m_dtmMgr.getDTM(paramInt).getStringValue(paramInt);
    }
    return XString.EMPTYSTRING;
  }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler)
    throws SAXException
  {
    int i = item(0);
    if (i != -1) {
      m_dtmMgr.getDTM(i).dispatchCharactersEvents(i, paramContentHandler, false);
    }
  }
  
  public XMLString xstr()
  {
    int i = item(0);
    return i != -1 ? getStringFromNode(i) : XString.EMPTYSTRING;
  }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer)
  {
    XString localXString = (XString)xstr();
    localXString.appendToFsb(paramFastStringBuffer);
  }
  
  public String str()
  {
    int i = item(0);
    return i != -1 ? getStringFromNode(i).toString() : "";
  }
  
  public Object object()
  {
    if (null == m_obj) {
      return this;
    }
    return m_obj;
  }
  
  public NodeIterator nodeset()
    throws TransformerException
  {
    return new DTMNodeIterator(iter());
  }
  
  public NodeList nodelist()
    throws TransformerException
  {
    DTMNodeList localDTMNodeList = new DTMNodeList(this);
    XNodeSet localXNodeSet = (XNodeSet)localDTMNodeList.getDTMIterator();
    SetVector(localXNodeSet.getVector());
    return localDTMNodeList;
  }
  
  public DTMIterator iterRaw()
  {
    return this;
  }
  
  public void release(DTMIterator paramDTMIterator) {}
  
  public DTMIterator iter()
  {
    try
    {
      if (hasCache()) {
        return cloneWithReset();
      }
      return this;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException.getMessage());
    }
  }
  
  public XObject getFresh()
  {
    try
    {
      if (hasCache()) {
        return (XObject)cloneWithReset();
      }
      return this;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException.getMessage());
    }
  }
  
  public NodeSetDTM mutableNodeset()
  {
    NodeSetDTM localNodeSetDTM;
    if ((m_obj instanceof NodeSetDTM))
    {
      localNodeSetDTM = (NodeSetDTM)m_obj;
    }
    else
    {
      localNodeSetDTM = new NodeSetDTM(iter());
      setObject(localNodeSetDTM);
      setCurrentPos(0);
    }
    return localNodeSetDTM;
  }
  
  public boolean compare(XObject paramXObject, Comparator paramComparator)
    throws TransformerException
  {
    boolean bool = false;
    int i = paramXObject.getType();
    if (4 == i)
    {
      DTMIterator localDTMIterator1 = iterRaw();
      DTMIterator localDTMIterator2 = ((XNodeSet)paramXObject).iterRaw();
      Vector localVector = null;
      int j;
      while (-1 != (j = localDTMIterator1.nextNode()))
      {
        XMLString localXMLString2 = getStringFromNode(j);
        int n;
        if (null == localVector)
        {
          while (-1 != (n = localDTMIterator2.nextNode()))
          {
            XMLString localXMLString3 = getStringFromNode(n);
            if (paramComparator.compareStrings(localXMLString2, localXMLString3))
            {
              bool = true;
              break;
            }
            if (null == localVector) {
              localVector = new Vector();
            }
            localVector.addElement(localXMLString3);
          }
        }
        else
        {
          n = localVector.size();
          for (int i1 = 0; i1 < n; i1++) {
            if (paramComparator.compareStrings(localXMLString2, (XMLString)localVector.elementAt(i1)))
            {
              bool = true;
              break;
            }
          }
        }
      }
      localDTMIterator1.reset();
      localDTMIterator2.reset();
    }
    else if (1 == i)
    {
      double d1 = bool() ? 1.0D : 0.0D;
      double d3 = paramXObject.num();
      bool = paramComparator.compareNumbers(d1, d3);
    }
    else
    {
      Object localObject;
      if (2 == i)
      {
        localObject = iterRaw();
        double d2 = paramXObject.num();
        int m;
        while (-1 != (m = ((DTMIterator)localObject).nextNode()))
        {
          double d4 = getNumberFromNode(m);
          if (paramComparator.compareNumbers(d4, d2))
          {
            bool = true;
            break;
          }
        }
        ((DTMIterator)localObject).reset();
      }
      else
      {
        DTMIterator localDTMIterator3;
        int k;
        XMLString localXMLString1;
        if (5 == i)
        {
          localObject = paramXObject.xstr();
          localDTMIterator3 = iterRaw();
          while (-1 != (k = localDTMIterator3.nextNode()))
          {
            localXMLString1 = getStringFromNode(k);
            if (paramComparator.compareStrings(localXMLString1, (XMLString)localObject))
            {
              bool = true;
              break;
            }
          }
          localDTMIterator3.reset();
        }
        else if (3 == i)
        {
          localObject = paramXObject.xstr();
          localDTMIterator3 = iterRaw();
          while (-1 != (k = localDTMIterator3.nextNode()))
          {
            localXMLString1 = getStringFromNode(k);
            if (paramComparator.compareStrings(localXMLString1, (XMLString)localObject))
            {
              bool = true;
              break;
            }
          }
          localDTMIterator3.reset();
        }
        else
        {
          bool = paramComparator.compareNumbers(num(), paramXObject.num());
        }
      }
    }
    return bool;
  }
  
  public boolean lessThan(XObject paramXObject)
    throws TransformerException
  {
    return compare(paramXObject, S_LT);
  }
  
  public boolean lessThanOrEqual(XObject paramXObject)
    throws TransformerException
  {
    return compare(paramXObject, S_LTE);
  }
  
  public boolean greaterThan(XObject paramXObject)
    throws TransformerException
  {
    return compare(paramXObject, S_GT);
  }
  
  public boolean greaterThanOrEqual(XObject paramXObject)
    throws TransformerException
  {
    return compare(paramXObject, S_GTE);
  }
  
  public boolean equals(XObject paramXObject)
  {
    try
    {
      return compare(paramXObject, S_EQ);
    }
    catch (TransformerException localTransformerException)
    {
      throw new WrappedRuntimeException(localTransformerException);
    }
  }
  
  public boolean notEquals(XObject paramXObject)
    throws TransformerException
  {
    return compare(paramXObject, S_NEQ);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XNodeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */