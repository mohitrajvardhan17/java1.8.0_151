package com.sun.org.apache.xml.internal.utils;

public class NSInfo
{
  public String m_namespace;
  public boolean m_hasXMLNSAttrs;
  public boolean m_hasProcessedNS;
  public int m_ancestorHasXMLNSAttrs;
  public static final int ANCESTORXMLNSUNPROCESSED = 0;
  public static final int ANCESTORHASXMLNS = 1;
  public static final int ANCESTORNOXMLNS = 2;
  
  public NSInfo(boolean paramBoolean1, boolean paramBoolean2)
  {
    m_hasProcessedNS = paramBoolean1;
    m_hasXMLNSAttrs = paramBoolean2;
    m_namespace = null;
    m_ancestorHasXMLNSAttrs = 0;
  }
  
  public NSInfo(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    m_hasProcessedNS = paramBoolean1;
    m_hasXMLNSAttrs = paramBoolean2;
    m_ancestorHasXMLNSAttrs = paramInt;
    m_namespace = null;
  }
  
  public NSInfo(String paramString, boolean paramBoolean)
  {
    m_hasProcessedNS = true;
    m_hasXMLNSAttrs = paramBoolean;
    m_namespace = paramString;
    m_ancestorHasXMLNSAttrs = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\NSInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */