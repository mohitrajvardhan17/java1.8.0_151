package com.sun.org.apache.xml.internal.serializer;

final class ElemContext
{
  final int m_currentElemDepth;
  ElemDesc m_elementDesc = null;
  String m_elementLocalName = null;
  String m_elementName = null;
  String m_elementURI = null;
  boolean m_isCdataSection;
  boolean m_isRaw = false;
  private ElemContext m_next;
  final ElemContext m_prev;
  boolean m_startTagOpen = false;
  
  ElemContext()
  {
    m_prev = this;
    m_currentElemDepth = 0;
  }
  
  private ElemContext(ElemContext paramElemContext)
  {
    m_prev = paramElemContext;
    m_currentElemDepth += 1;
  }
  
  final ElemContext pop()
  {
    return m_prev;
  }
  
  final ElemContext push()
  {
    ElemContext localElemContext = m_next;
    if (localElemContext == null)
    {
      localElemContext = new ElemContext(this);
      m_next = localElemContext;
    }
    m_startTagOpen = true;
    return localElemContext;
  }
  
  final ElemContext push(String paramString1, String paramString2, String paramString3)
  {
    ElemContext localElemContext = m_next;
    if (localElemContext == null)
    {
      localElemContext = new ElemContext(this);
      m_next = localElemContext;
    }
    m_elementName = paramString3;
    m_elementLocalName = paramString2;
    m_elementURI = paramString1;
    m_isCdataSection = false;
    m_startTagOpen = true;
    return localElemContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ElemContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */