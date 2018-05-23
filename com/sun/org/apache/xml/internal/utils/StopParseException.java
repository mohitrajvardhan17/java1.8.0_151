package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.SAXException;

public class StopParseException
  extends SAXException
{
  static final long serialVersionUID = 210102479218258961L;
  
  StopParseException()
  {
    super("Stylesheet PIs found, stop the parse");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StopParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */