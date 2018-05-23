package com.sun.org.apache.xml.internal.security.encryption;

import org.w3c.dom.Attr;

public abstract interface CipherReference
{
  public abstract String getURI();
  
  public abstract Attr getURIAsAttr();
  
  public abstract Transforms getTransforms();
  
  public abstract void setTransforms(Transforms paramTransforms);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\CipherReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */