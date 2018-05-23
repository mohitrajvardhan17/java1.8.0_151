package com.sun.org.apache.xpath.internal;

import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;

public abstract interface WhitespaceStrippingElementMatcher
{
  public abstract boolean shouldStripWhiteSpace(XPathContext paramXPathContext, Element paramElement)
    throws TransformerException;
  
  public abstract boolean canStripWhiteSpace();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\WhitespaceStrippingElementMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */