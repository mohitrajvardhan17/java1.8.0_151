package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.Node;

public abstract interface PrefixResolver
{
  public abstract String getNamespaceForPrefix(String paramString);
  
  public abstract String getNamespaceForPrefix(String paramString, Node paramNode);
  
  public abstract String getBaseIdentifier();
  
  public abstract boolean handlesNullPrefixes();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\PrefixResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */