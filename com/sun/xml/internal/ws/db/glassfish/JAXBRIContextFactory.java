package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.ContextFactory;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class JAXBRIContextFactory
  extends BindingContextFactory
{
  public JAXBRIContextFactory() {}
  
  public BindingContext newContext(JAXBContext paramJAXBContext)
  {
    return new JAXBRIContextWrapper((JAXBRIContext)paramJAXBContext, null);
  }
  
  public BindingContext newContext(BindingInfo paramBindingInfo)
  {
    Class[] arrayOfClass = (Class[])paramBindingInfo.contentClasses().toArray(new Class[paramBindingInfo.contentClasses().size()]);
    for (int i = 0; i < arrayOfClass.length; i++) {
      if (WrapperComposite.class.equals(arrayOfClass[i])) {
        arrayOfClass[i] = CompositeStructure.class;
      }
    }
    Map localMap1 = typeInfoMappings(paramBindingInfo.typeInfos());
    Map localMap2 = paramBindingInfo.subclassReplacements();
    String str = paramBindingInfo.getDefaultNamespace();
    Boolean localBoolean = (Boolean)paramBindingInfo.properties().get("c14nSupport");
    RuntimeAnnotationReader localRuntimeAnnotationReader = (RuntimeAnnotationReader)paramBindingInfo.properties().get("com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader");
    JAXBContextFactory localJAXBContextFactory = (JAXBContextFactory)paramBindingInfo.properties().get(JAXBContextFactory.class.getName());
    try
    {
      JAXBRIContext localJAXBRIContext = localJAXBContextFactory != null ? localJAXBContextFactory.createJAXBContext(paramBindingInfo.getSEIModel(), toList(arrayOfClass), toList(localMap1.values())) : ContextFactory.createContext(arrayOfClass, localMap1.values(), localMap2, str, localBoolean != null ? localBoolean.booleanValue() : false, localRuntimeAnnotationReader, false, false, false);
      return new JAXBRIContextWrapper(localJAXBRIContext, localMap1);
    }
    catch (Exception localException)
    {
      throw new DatabindingException(localException);
    }
  }
  
  private <T> List<T> toList(T[] paramArrayOfT)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(Arrays.asList(paramArrayOfT));
    return localArrayList;
  }
  
  private <T> List<T> toList(Collection<T> paramCollection)
  {
    if ((paramCollection instanceof List)) {
      return (List)paramCollection;
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(paramCollection);
    return localArrayList;
  }
  
  private Map<TypeInfo, TypeReference> typeInfoMappings(Collection<TypeInfo> paramCollection)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      TypeInfo localTypeInfo = (TypeInfo)localIterator.next();
      Type localType = WrapperComposite.class.equals(type) ? CompositeStructure.class : type;
      TypeReference localTypeReference = new TypeReference(tagName, localType, annotations);
      localHashMap.put(localTypeInfo, localTypeReference);
    }
    return localHashMap;
  }
  
  protected BindingContext getContext(Marshaller paramMarshaller)
  {
    return newContext(((MarshallerImpl)paramMarshaller).getContext());
  }
  
  protected boolean isFor(String paramString)
  {
    return (paramString.equals("glassfish.jaxb")) || (paramString.equals(getClass().getName())) || (paramString.equals("com.sun.xml.internal.bind.v2.runtime"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\db\glassfish\JAXBRIContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */