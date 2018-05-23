package com.sun.org.apache.xml.internal.dtm.ref;

import javax.xml.transform.SourceLocator;

public class NodeLocator
  implements SourceLocator
{
  protected String m_publicId;
  protected String m_systemId;
  protected int m_lineNumber;
  protected int m_columnNumber;
  
  public NodeLocator(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    m_publicId = paramString1;
    m_systemId = paramString2;
    m_lineNumber = paramInt1;
    m_columnNumber = paramInt2;
  }
  
  public String getPublicId()
  {
    return m_publicId;
  }
  
  public String getSystemId()
  {
    return m_systemId;
  }
  
  public int getLineNumber()
  {
    return m_lineNumber;
  }
  
  public int getColumnNumber()
  {
    return m_columnNumber;
  }
  
  public String toString()
  {
    return "file '" + m_systemId + "', line #" + m_lineNumber + ", column #" + m_columnNumber;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\NodeLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */