package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.BuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;
import com.sun.xml.internal.bind.v2.util.FlattenIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

class TypeInfoSetImpl<T, C, F, M>
  implements TypeInfoSet<T, C, F, M>
{
  @XmlTransient
  public final Navigator<T, C, F, M> nav;
  @XmlTransient
  public final AnnotationReader<T, C, F, M> reader;
  private final Map<T, BuiltinLeafInfo<T, C>> builtins = new LinkedHashMap();
  private final Map<C, EnumLeafInfoImpl<T, C, F, M>> enums = new LinkedHashMap();
  private final Map<T, ArrayInfoImpl<T, C, F, M>> arrays = new LinkedHashMap();
  @XmlJavaTypeAdapter(RuntimeUtil.ToStringAdapter.class)
  private final Map<C, ClassInfoImpl<T, C, F, M>> beans = new LinkedHashMap();
  @XmlTransient
  private final Map<C, ClassInfoImpl<T, C, F, M>> beansView = Collections.unmodifiableMap(beans);
  private final Map<C, Map<QName, ElementInfoImpl<T, C, F, M>>> elementMappings = new LinkedHashMap();
  private final Iterable<? extends ElementInfoImpl<T, C, F, M>> allElements = new Iterable()
  {
    public Iterator<ElementInfoImpl<T, C, F, M>> iterator()
    {
      return new FlattenIterator(elementMappings.values());
    }
  };
  private final NonElement<T, C> anyType;
  private Map<String, Map<String, String>> xmlNsCache;
  
  public TypeInfoSetImpl(Navigator<T, C, F, M> paramNavigator, AnnotationReader<T, C, F, M> paramAnnotationReader, Map<T, ? extends BuiltinLeafInfoImpl<T, C>> paramMap)
  {
    nav = paramNavigator;
    reader = paramAnnotationReader;
    builtins.putAll(paramMap);
    anyType = createAnyType();
    Iterator localIterator = RuntimeUtil.primitiveToBox.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      builtins.put(paramNavigator.getPrimitive((Class)localEntry.getKey()), paramMap.get(paramNavigator.ref((Class)localEntry.getValue())));
    }
    elementMappings.put(null, new LinkedHashMap());
  }
  
  protected NonElement<T, C> createAnyType()
  {
    return new AnyTypeImpl(nav);
  }
  
  public Navigator<T, C, F, M> getNavigator()
  {
    return nav;
  }
  
  public void add(ClassInfoImpl<T, C, F, M> paramClassInfoImpl)
  {
    beans.put(paramClassInfoImpl.getClazz(), paramClassInfoImpl);
  }
  
  public void add(EnumLeafInfoImpl<T, C, F, M> paramEnumLeafInfoImpl)
  {
    enums.put(clazz, paramEnumLeafInfoImpl);
  }
  
  public void add(ArrayInfoImpl<T, C, F, M> paramArrayInfoImpl)
  {
    arrays.put(paramArrayInfoImpl.getType(), paramArrayInfoImpl);
  }
  
  public NonElement<T, C> getTypeInfo(T paramT)
  {
    paramT = nav.erasure(paramT);
    LeafInfo localLeafInfo = (LeafInfo)builtins.get(paramT);
    if (localLeafInfo != null) {
      return localLeafInfo;
    }
    if (nav.isArray(paramT)) {
      return (NonElement)arrays.get(paramT);
    }
    Object localObject = nav.asDecl(paramT);
    if (localObject == null) {
      return null;
    }
    return getClassInfo(localObject);
  }
  
  public NonElement<T, C> getAnyTypeInfo()
  {
    return anyType;
  }
  
  public NonElement<T, C> getTypeInfo(Ref<T, C> paramRef)
  {
    assert (!valueList);
    Object localObject = nav.asDecl(type);
    if ((localObject != null) && (reader.getClassAnnotation(XmlRegistry.class, localObject, null) != null)) {
      return null;
    }
    return getTypeInfo(type);
  }
  
  public Map<C, ? extends ClassInfoImpl<T, C, F, M>> beans()
  {
    return beansView;
  }
  
  public Map<T, ? extends BuiltinLeafInfo<T, C>> builtins()
  {
    return builtins;
  }
  
  public Map<C, ? extends EnumLeafInfoImpl<T, C, F, M>> enums()
  {
    return enums;
  }
  
  public Map<? extends T, ? extends ArrayInfoImpl<T, C, F, M>> arrays()
  {
    return arrays;
  }
  
  public NonElement<T, C> getClassInfo(C paramC)
  {
    LeafInfo localLeafInfo = (LeafInfo)builtins.get(nav.use(paramC));
    if (localLeafInfo != null) {
      return localLeafInfo;
    }
    localLeafInfo = (LeafInfo)enums.get(paramC);
    if (localLeafInfo != null) {
      return localLeafInfo;
    }
    if (nav.asDecl(Object.class).equals(paramC)) {
      return anyType;
    }
    return (NonElement)beans.get(paramC);
  }
  
  public ElementInfoImpl<T, C, F, M> getElementInfo(C paramC, QName paramQName)
  {
    while (paramC != null)
    {
      Map localMap = (Map)elementMappings.get(paramC);
      if (localMap != null)
      {
        ElementInfoImpl localElementInfoImpl = (ElementInfoImpl)localMap.get(paramQName);
        if (localElementInfoImpl != null) {
          return localElementInfoImpl;
        }
      }
      paramC = nav.getSuperClass(paramC);
    }
    return (ElementInfoImpl)((Map)elementMappings.get(null)).get(paramQName);
  }
  
  public final void add(ElementInfoImpl<T, C, F, M> paramElementInfoImpl, ModelBuilder<T, C, F, M> paramModelBuilder)
  {
    Object localObject1 = null;
    if (paramElementInfoImpl.getScope() != null) {
      localObject1 = paramElementInfoImpl.getScope().getClazz();
    }
    Object localObject2 = (Map)elementMappings.get(localObject1);
    if (localObject2 == null) {
      elementMappings.put(localObject1, localObject2 = new LinkedHashMap());
    }
    ElementInfoImpl localElementInfoImpl = (ElementInfoImpl)((Map)localObject2).put(paramElementInfoImpl.getElementName(), paramElementInfoImpl);
    if (localElementInfoImpl != null)
    {
      QName localQName = paramElementInfoImpl.getElementName();
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.CONFLICTING_XML_ELEMENT_MAPPING.format(new Object[] { localQName.getNamespaceURI(), localQName.getLocalPart() }), paramElementInfoImpl, localElementInfoImpl));
    }
  }
  
  public Map<QName, ? extends ElementInfoImpl<T, C, F, M>> getElementMappings(C paramC)
  {
    return (Map)elementMappings.get(paramC);
  }
  
  public Iterable<? extends ElementInfoImpl<T, C, F, M>> getAllElements()
  {
    return allElements;
  }
  
  public Map<String, String> getXmlNs(String paramString)
  {
    if (xmlNsCache == null)
    {
      xmlNsCache = new HashMap();
      localObject1 = beans().values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        ClassInfoImpl localClassInfoImpl = (ClassInfoImpl)((Iterator)localObject1).next();
        XmlSchema localXmlSchema = (XmlSchema)reader.getPackageAnnotation(XmlSchema.class, localClassInfoImpl.getClazz(), null);
        if (localXmlSchema != null)
        {
          String str = localXmlSchema.namespace();
          Object localObject2 = (Map)xmlNsCache.get(str);
          if (localObject2 == null) {
            xmlNsCache.put(str, localObject2 = new HashMap());
          }
          for (XmlNs localXmlNs : localXmlSchema.xmlns()) {
            ((Map)localObject2).put(localXmlNs.prefix(), localXmlNs.namespaceURI());
          }
        }
      }
    }
    Object localObject1 = (Map)xmlNsCache.get(paramString);
    if (localObject1 != null) {
      return (Map<String, String>)localObject1;
    }
    return Collections.emptyMap();
  }
  
  public Map<String, String> getSchemaLocations()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = beans().values().iterator();
    while (localIterator.hasNext())
    {
      ClassInfoImpl localClassInfoImpl = (ClassInfoImpl)localIterator.next();
      XmlSchema localXmlSchema = (XmlSchema)reader.getPackageAnnotation(XmlSchema.class, localClassInfoImpl.getClazz(), null);
      if (localXmlSchema != null)
      {
        String str = localXmlSchema.location();
        if (!str.equals("##generate")) {
          localHashMap.put(localXmlSchema.namespace(), str);
        }
      }
    }
    return localHashMap;
  }
  
  public final XmlNsForm getElementFormDefault(String paramString)
  {
    Iterator localIterator = beans().values().iterator();
    while (localIterator.hasNext())
    {
      ClassInfoImpl localClassInfoImpl = (ClassInfoImpl)localIterator.next();
      XmlSchema localXmlSchema = (XmlSchema)reader.getPackageAnnotation(XmlSchema.class, localClassInfoImpl.getClazz(), null);
      if ((localXmlSchema != null) && (localXmlSchema.namespace().equals(paramString)))
      {
        XmlNsForm localXmlNsForm = localXmlSchema.elementFormDefault();
        if (localXmlNsForm != XmlNsForm.UNSET) {
          return localXmlNsForm;
        }
      }
    }
    return XmlNsForm.UNSET;
  }
  
  public final XmlNsForm getAttributeFormDefault(String paramString)
  {
    Iterator localIterator = beans().values().iterator();
    while (localIterator.hasNext())
    {
      ClassInfoImpl localClassInfoImpl = (ClassInfoImpl)localIterator.next();
      XmlSchema localXmlSchema = (XmlSchema)reader.getPackageAnnotation(XmlSchema.class, localClassInfoImpl.getClazz(), null);
      if ((localXmlSchema != null) && (localXmlSchema.namespace().equals(paramString)))
      {
        XmlNsForm localXmlNsForm = localXmlSchema.attributeFormDefault();
        if (localXmlNsForm != XmlNsForm.UNSET) {
          return localXmlNsForm;
        }
      }
    }
    return XmlNsForm.UNSET;
  }
  
  public void dump(Result paramResult)
    throws JAXBException
  {
    JAXBContext localJAXBContext = JAXBContext.newInstance(new Class[] { getClass() });
    Marshaller localMarshaller = localJAXBContext.createMarshaller();
    localMarshaller.marshal(this, paramResult);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\TypeInfoSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */