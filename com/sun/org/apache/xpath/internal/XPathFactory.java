package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;

public abstract interface XPathFactory
{
  public abstract XPath create(String paramString, SourceLocator paramSourceLocator, PrefixResolver paramPrefixResolver, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\XPathFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */