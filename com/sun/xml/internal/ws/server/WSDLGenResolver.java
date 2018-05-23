package com.sun.xml.internal.ws.server;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.server.SDDocument.Schema;
import com.sun.xml.internal.ws.api.server.SDDocument.WSDL;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

final class WSDLGenResolver
  implements WSDLResolver
{
  private final List<SDDocumentImpl> docs;
  private final List<SDDocumentSource> newDocs = new ArrayList();
  private SDDocumentSource concreteWsdlSource;
  private SDDocumentImpl abstractWsdl;
  private SDDocumentImpl concreteWsdl;
  private final Map<String, List<SDDocumentImpl>> nsMapping = new HashMap();
  private final QName serviceName;
  private final QName portTypeName;
  
  public WSDLGenResolver(@NotNull List<SDDocumentImpl> paramList, QName paramQName1, QName paramQName2)
  {
    docs = paramList;
    serviceName = paramQName1;
    portTypeName = paramQName2;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      SDDocumentImpl localSDDocumentImpl = (SDDocumentImpl)localIterator.next();
      Object localObject1;
      if (localSDDocumentImpl.isWSDL())
      {
        localObject1 = (SDDocument.WSDL)localSDDocumentImpl;
        if (((SDDocument.WSDL)localObject1).hasPortType()) {
          abstractWsdl = localSDDocumentImpl;
        }
      }
      if (localSDDocumentImpl.isSchema())
      {
        localObject1 = (SDDocument.Schema)localSDDocumentImpl;
        Object localObject2 = (List)nsMapping.get(((SDDocument.Schema)localObject1).getTargetNamespace());
        if (localObject2 == null)
        {
          localObject2 = new ArrayList();
          nsMapping.put(((SDDocument.Schema)localObject1).getTargetNamespace(), localObject2);
        }
        ((List)localObject2).add(localSDDocumentImpl);
      }
    }
  }
  
  public Result getWSDL(String paramString)
  {
    URL localURL = createURL(paramString);
    MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    localMutableXMLStreamBuffer.setSystemId(localURL.toExternalForm());
    concreteWsdlSource = SDDocumentSource.create(localURL, localMutableXMLStreamBuffer);
    newDocs.add(concreteWsdlSource);
    XMLStreamBufferResult localXMLStreamBufferResult = new XMLStreamBufferResult(localMutableXMLStreamBuffer);
    localXMLStreamBufferResult.setSystemId(paramString);
    return localXMLStreamBufferResult;
  }
  
  private URL createURL(String paramString)
  {
    try
    {
      return new URL("file:///" + paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new WebServiceException(localMalformedURLException);
    }
  }
  
  public Result getAbstractWSDL(Holder<String> paramHolder)
  {
    if (abstractWsdl != null)
    {
      value = abstractWsdl.getURL().toString();
      return null;
    }
    URL localURL = createURL((String)value);
    MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    localMutableXMLStreamBuffer.setSystemId(localURL.toExternalForm());
    SDDocumentSource localSDDocumentSource = SDDocumentSource.create(localURL, localMutableXMLStreamBuffer);
    newDocs.add(localSDDocumentSource);
    XMLStreamBufferResult localXMLStreamBufferResult = new XMLStreamBufferResult(localMutableXMLStreamBuffer);
    localXMLStreamBufferResult.setSystemId((String)value);
    return localXMLStreamBufferResult;
  }
  
  public Result getSchemaOutput(String paramString, Holder<String> paramHolder)
  {
    List localList = (List)nsMapping.get(paramString);
    if (localList != null)
    {
      if (localList.size() > 1) {
        throw new ServerRtException("server.rt.err", new Object[] { "More than one schema for the target namespace " + paramString });
      }
      value = ((SDDocumentImpl)localList.get(0)).getURL().toExternalForm();
      return null;
    }
    URL localURL = createURL((String)value);
    MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    localMutableXMLStreamBuffer.setSystemId(localURL.toExternalForm());
    SDDocumentSource localSDDocumentSource = SDDocumentSource.create(localURL, localMutableXMLStreamBuffer);
    newDocs.add(localSDDocumentSource);
    XMLStreamBufferResult localXMLStreamBufferResult = new XMLStreamBufferResult(localMutableXMLStreamBuffer);
    localXMLStreamBufferResult.setSystemId((String)value);
    return localXMLStreamBufferResult;
  }
  
  public SDDocumentImpl updateDocs()
  {
    Iterator localIterator = newDocs.iterator();
    while (localIterator.hasNext())
    {
      SDDocumentSource localSDDocumentSource = (SDDocumentSource)localIterator.next();
      SDDocumentImpl localSDDocumentImpl = SDDocumentImpl.create(localSDDocumentSource, serviceName, portTypeName);
      if (localSDDocumentSource == concreteWsdlSource) {
        concreteWsdl = localSDDocumentImpl;
      }
      docs.add(localSDDocumentImpl);
    }
    return concreteWsdl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\WSDLGenResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */