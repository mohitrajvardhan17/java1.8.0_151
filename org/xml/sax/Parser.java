package org.xml.sax;

import java.io.IOException;
import java.util.Locale;

/**
 * @deprecated
 */
public abstract interface Parser
{
  public abstract void setLocale(Locale paramLocale)
    throws SAXException;
  
  public abstract void setEntityResolver(EntityResolver paramEntityResolver);
  
  public abstract void setDTDHandler(DTDHandler paramDTDHandler);
  
  public abstract void setDocumentHandler(DocumentHandler paramDocumentHandler);
  
  public abstract void setErrorHandler(ErrorHandler paramErrorHandler);
  
  public abstract void parse(InputSource paramInputSource)
    throws SAXException, IOException;
  
  public abstract void parse(String paramString)
    throws SAXException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */