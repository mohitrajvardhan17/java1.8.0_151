package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;

public final class DTMXRTreeFrag
{
  private DTM m_dtm;
  private int m_dtmIdentity = -1;
  private XPathContext m_xctxt;
  
  public DTMXRTreeFrag(int paramInt, XPathContext paramXPathContext)
  {
    m_xctxt = paramXPathContext;
    m_dtmIdentity = paramInt;
    m_dtm = paramXPathContext.getDTM(paramInt);
  }
  
  public final void destruct()
  {
    m_dtm = null;
    m_xctxt = null;
  }
  
  final DTM getDTM()
  {
    return m_dtm;
  }
  
  public final int getDTMIdentity()
  {
    return m_dtmIdentity;
  }
  
  final XPathContext getXPathContext()
  {
    return m_xctxt;
  }
  
  public final int hashCode()
  {
    return m_dtmIdentity;
  }
  
  public final boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DTMXRTreeFrag)) {
      return m_dtmIdentity == ((DTMXRTreeFrag)paramObject).getDTMIdentity();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\DTMXRTreeFrag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */