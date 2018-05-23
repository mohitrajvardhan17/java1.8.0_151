package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathNSResolver;

class XPathNSResolverImpl
  extends PrefixResolverDefault
  implements XPathNSResolver
{
  public XPathNSResolverImpl(Node paramNode)
  {
    super(paramNode);
  }
  
  public String lookupNamespaceURI(String paramString)
  {
    return super.getNamespaceForPrefix(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathNSResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */