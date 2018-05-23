package com.sun.org.apache.xerces.internal.xs;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.ls.LSInput;

public abstract interface XSLoader
{
  public abstract DOMConfiguration getConfig();
  
  public abstract XSModel loadURIList(StringList paramStringList);
  
  public abstract XSModel loadInputList(LSInputList paramLSInputList);
  
  public abstract XSModel loadURI(String paramString);
  
  public abstract XSModel load(LSInput paramLSInput);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */