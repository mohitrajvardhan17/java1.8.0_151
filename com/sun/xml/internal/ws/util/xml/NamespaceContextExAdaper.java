package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextExAdaper
  implements NamespaceContextEx
{
  private final NamespaceContext nsContext;
  
  public NamespaceContextExAdaper(NamespaceContext paramNamespaceContext)
  {
    nsContext = paramNamespaceContext;
  }
  
  public Iterator<NamespaceContextEx.Binding> iterator()
  {
    throw new UnsupportedOperationException();
  }
  
  public String getNamespaceURI(String paramString)
  {
    return nsContext.getNamespaceURI(paramString);
  }
  
  public String getPrefix(String paramString)
  {
    return nsContext.getPrefix(paramString);
  }
  
  public Iterator getPrefixes(String paramString)
  {
    return nsContext.getPrefixes(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\NamespaceContextExAdaper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */