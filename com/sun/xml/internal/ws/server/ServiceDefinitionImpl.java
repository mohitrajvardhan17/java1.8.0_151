package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServiceDefinitionImpl
  implements ServiceDefinition, SDDocumentResolver
{
  private final List<SDDocumentImpl> docs;
  private final Map<String, SDDocumentImpl> bySystemId;
  @NotNull
  private final SDDocumentImpl primaryWsdl;
  WSEndpointImpl<?> owner;
  final List<SDDocumentFilter> filters = new ArrayList();
  
  public ServiceDefinitionImpl(List<SDDocumentImpl> paramList, @NotNull SDDocumentImpl paramSDDocumentImpl)
  {
    assert (paramList.contains(paramSDDocumentImpl));
    docs = paramList;
    primaryWsdl = paramSDDocumentImpl;
    bySystemId = new HashMap(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      SDDocumentImpl localSDDocumentImpl = (SDDocumentImpl)localIterator.next();
      bySystemId.put(localSDDocumentImpl.getURL().toExternalForm(), localSDDocumentImpl);
      localSDDocumentImpl.setFilters(filters);
      localSDDocumentImpl.setResolver(this);
    }
  }
  
  void setOwner(WSEndpointImpl<?> paramWSEndpointImpl)
  {
    assert ((paramWSEndpointImpl != null) && (owner == null));
    owner = paramWSEndpointImpl;
  }
  
  @NotNull
  public SDDocument getPrimary()
  {
    return primaryWsdl;
  }
  
  public void addFilter(SDDocumentFilter paramSDDocumentFilter)
  {
    filters.add(paramSDDocumentFilter);
  }
  
  public Iterator<SDDocument> iterator()
  {
    return docs.iterator();
  }
  
  public SDDocument resolve(String paramString)
  {
    return (SDDocument)bySystemId.get(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\ServiceDefinitionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */