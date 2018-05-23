package com.sun.org.apache.xalan.internal.xsltc;

import org.xml.sax.SAXException;

public final class TransletException
  extends SAXException
{
  static final long serialVersionUID = -878916829521217293L;
  
  public TransletException()
  {
    super("Translet error");
  }
  
  public TransletException(Exception paramException)
  {
    super(paramException.toString());
    initCause(paramException);
  }
  
  public TransletException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\TransletException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */