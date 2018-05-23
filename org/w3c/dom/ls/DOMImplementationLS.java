package org.w3c.dom.ls;

import org.w3c.dom.DOMException;

public abstract interface DOMImplementationLS
{
  public static final short MODE_SYNCHRONOUS = 1;
  public static final short MODE_ASYNCHRONOUS = 2;
  
  public abstract LSParser createLSParser(short paramShort, String paramString)
    throws DOMException;
  
  public abstract LSSerializer createLSSerializer();
  
  public abstract LSInput createLSInput();
  
  public abstract LSOutput createLSOutput();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ls\DOMImplementationLS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */