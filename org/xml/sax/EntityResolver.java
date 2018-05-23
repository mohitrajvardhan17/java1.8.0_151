package org.xml.sax;

import java.io.IOException;

public abstract interface EntityResolver
{
  public abstract InputSource resolveEntity(String paramString1, String paramString2)
    throws SAXException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\EntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */