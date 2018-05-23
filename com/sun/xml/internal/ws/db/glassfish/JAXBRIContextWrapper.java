package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

class JAXBRIContextWrapper
  implements BindingContext
{
  private Map<TypeInfo, TypeReference> typeRefs;
  private Map<TypeReference, TypeInfo> typeInfos;
  private JAXBRIContext context;
  
  JAXBRIContextWrapper(JAXBRIContext paramJAXBRIContext, Map<TypeInfo, TypeReference> paramMap)
  {
    context = paramJAXBRIContext;
    typeRefs = paramMap;
    if (paramMap != null)
    {
      typeInfos = new HashMap();
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        TypeInfo localTypeInfo = (TypeInfo)localIterator.next();
        typeInfos.put(typeRefs.get(localTypeInfo), localTypeInfo);
      }
    }
  }
  
  TypeReference typeReference(TypeInfo paramTypeInfo)
  {
    return typeRefs != null ? (TypeReference)typeRefs.get(paramTypeInfo) : null;
  }
  
  TypeInfo typeInfo(TypeReference paramTypeReference)
  {
    return typeInfos != null ? (TypeInfo)typeInfos.get(paramTypeReference) : null;
  }
  
  public Marshaller createMarshaller()
    throws JAXBException
  {
    return context.createMarshaller();
  }
  
  public Unmarshaller createUnmarshaller()
    throws JAXBException
  {
    return context.createUnmarshaller();
  }
  
  public void generateSchema(SchemaOutputResolver paramSchemaOutputResolver)
    throws IOException
  {
    context.generateSchema(paramSchemaOutputResolver);
  }
  
  public String getBuildId()
  {
    return context.getBuildId();
  }
  
  public QName getElementName(Class paramClass)
    throws JAXBException
  {
    return context.getElementName(paramClass);
  }
  
  public QName getElementName(Object paramObject)
    throws JAXBException
  {
    return context.getElementName(paramObject);
  }
  
  public <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> paramClass, String paramString1, String paramString2)
    throws JAXBException
  {
    return new RawAccessorWrapper(context.getElementPropertyAccessor(paramClass, paramString1, paramString2));
  }
  
  public List<String> getKnownNamespaceURIs()
  {
    return context.getKnownNamespaceURIs();
  }
  
  public RuntimeTypeInfoSet getRuntimeTypeInfoSet()
  {
    return context.getRuntimeTypeInfoSet();
  }
  
  public QName getTypeName(TypeReference paramTypeReference)
  {
    return context.getTypeName(paramTypeReference);
  }
  
  public int hashCode()
  {
    return context.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    JAXBRIContextWrapper localJAXBRIContextWrapper = (JAXBRIContextWrapper)paramObject;
    return (context == context) || ((context != null) && (context.equals(context)));
  }
  
  public boolean hasSwaRef()
  {
    return context.hasSwaRef();
  }
  
  public String toString()
  {
    return JAXBRIContextWrapper.class.getName() + " : " + context.toString();
  }
  
  public XMLBridge createBridge(TypeInfo paramTypeInfo)
  {
    TypeReference localTypeReference = (TypeReference)typeRefs.get(paramTypeInfo);
    Bridge localBridge = context.createBridge(localTypeReference);
    return WrapperComposite.class.equals(type) ? new WrapperBridge(this, localBridge) : new BridgeWrapper(this, localBridge);
  }
  
  public JAXBContext getJAXBContext()
  {
    return context;
  }
  
  public QName getTypeName(TypeInfo paramTypeInfo)
  {
    TypeReference localTypeReference = (TypeReference)typeRefs.get(paramTypeInfo);
    return context.getTypeName(localTypeReference);
  }
  
  public XMLBridge createFragmentBridge()
  {
    return new MarshallerBridge((JAXBContextImpl)context);
  }
  
  public Object newWrapperInstace(Class<?> paramClass)
    throws InstantiationException, IllegalAccessException
  {
    return paramClass.newInstance();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\db\glassfish\JAXBRIContextWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */