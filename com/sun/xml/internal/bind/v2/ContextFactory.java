package com.sun.xml.internal.bind.v2;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.JAXBContextBuilder;
import com.sun.xml.internal.bind.v2.util.TypeCast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class ContextFactory
{
  public static final String USE_JAXB_PROPERTIES = "_useJAXBProperties";
  
  public ContextFactory() {}
  
  public static JAXBContext createContext(Class[] paramArrayOfClass, Map<String, Object> paramMap)
    throws JAXBException
  {
    if (paramMap == null) {
      paramMap = Collections.emptyMap();
    } else {
      paramMap = new HashMap(paramMap);
    }
    String str = (String)getPropertyValue(paramMap, "com.sun.xml.internal.bind.defaultNamespaceRemap", String.class);
    Boolean localBoolean1 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.c14n", Boolean.class);
    if (localBoolean1 == null) {
      localBoolean1 = Boolean.valueOf(false);
    }
    Boolean localBoolean2 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.disableXmlSecurity", Boolean.class);
    if (localBoolean2 == null) {
      localBoolean2 = Boolean.valueOf(false);
    }
    Boolean localBoolean3 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.treatEverythingNillable", Boolean.class);
    if (localBoolean3 == null) {
      localBoolean3 = Boolean.valueOf(false);
    }
    Boolean localBoolean4 = (Boolean)getPropertyValue(paramMap, "retainReferenceToInfo", Boolean.class);
    if (localBoolean4 == null) {
      localBoolean4 = Boolean.valueOf(false);
    }
    Boolean localBoolean5 = (Boolean)getPropertyValue(paramMap, "supressAccessorWarnings", Boolean.class);
    if (localBoolean5 == null) {
      localBoolean5 = Boolean.valueOf(false);
    }
    Boolean localBoolean6 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.improvedXsiTypeHandling", Boolean.class);
    if (localBoolean6 == null)
    {
      localObject1 = Util.getSystemProperty("com.sun.xml.internal.bind.improvedXsiTypeHandling");
      if (localObject1 == null) {
        localBoolean6 = Boolean.valueOf(true);
      } else {
        localBoolean6 = Boolean.valueOf((String)localObject1);
      }
    }
    Object localObject1 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.XmlAccessorFactory", Boolean.class);
    if (localObject1 == null)
    {
      localObject1 = Boolean.valueOf(false);
      Util.getClassLogger().log(Level.FINE, "Property com.sun.xml.internal.bind.XmlAccessorFactoryis not active.  Using JAXB's implementation");
    }
    RuntimeAnnotationReader localRuntimeAnnotationReader = (RuntimeAnnotationReader)getPropertyValue(paramMap, JAXBRIContext.ANNOTATION_READER, RuntimeAnnotationReader.class);
    Object localObject2 = (Collection)getPropertyValue(paramMap, "com.sun.xml.internal.bind.typeReferences", Collection.class);
    if (localObject2 == null) {
      localObject2 = Collections.emptyList();
    }
    Map localMap;
    try
    {
      localMap = TypeCast.checkedCast((Map)getPropertyValue(paramMap, "com.sun.xml.internal.bind.subclassReplacements", Map.class), Class.class, Class.class);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new JAXBException(Messages.INVALID_TYPE_IN_MAP.format(new Object[0]), localClassCastException);
    }
    if (!paramMap.isEmpty()) {
      throw new JAXBException(Messages.UNSUPPORTED_PROPERTY.format(new Object[] { paramMap.keySet().iterator().next() }));
    }
    JAXBContextImpl.JAXBContextBuilder localJAXBContextBuilder = new JAXBContextImpl.JAXBContextBuilder();
    localJAXBContextBuilder.setClasses(paramArrayOfClass);
    localJAXBContextBuilder.setTypeRefs((Collection)localObject2);
    localJAXBContextBuilder.setSubclassReplacements(localMap);
    localJAXBContextBuilder.setDefaultNsUri(str);
    localJAXBContextBuilder.setC14NSupport(localBoolean1.booleanValue());
    localJAXBContextBuilder.setAnnotationReader(localRuntimeAnnotationReader);
    localJAXBContextBuilder.setXmlAccessorFactorySupport(((Boolean)localObject1).booleanValue());
    localJAXBContextBuilder.setAllNillable(localBoolean3.booleanValue());
    localJAXBContextBuilder.setRetainPropertyInfo(localBoolean4.booleanValue());
    localJAXBContextBuilder.setSupressAccessorWarnings(localBoolean5.booleanValue());
    localJAXBContextBuilder.setImprovedXsiTypeHandling(localBoolean6.booleanValue());
    localJAXBContextBuilder.setDisableSecurityProcessing(localBoolean2.booleanValue());
    return localJAXBContextBuilder.build();
  }
  
  private static <T> T getPropertyValue(Map<String, Object> paramMap, String paramString, Class<T> paramClass)
    throws JAXBException
  {
    Object localObject = paramMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    paramMap.remove(paramString);
    if (!paramClass.isInstance(localObject)) {
      throw new JAXBException(Messages.INVALID_PROPERTY_VALUE.format(new Object[] { paramString, localObject }));
    }
    return (T)paramClass.cast(localObject);
  }
  
  @Deprecated
  public static JAXBRIContext createContext(Class[] paramArrayOfClass, Collection<TypeReference> paramCollection, Map<Class, Class> paramMap, String paramString, boolean paramBoolean1, RuntimeAnnotationReader paramRuntimeAnnotationReader, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    throws JAXBException
  {
    return createContext(paramArrayOfClass, paramCollection, paramMap, paramString, paramBoolean1, paramRuntimeAnnotationReader, paramBoolean2, paramBoolean3, paramBoolean4, false);
  }
  
  @Deprecated
  public static JAXBRIContext createContext(Class[] paramArrayOfClass, Collection<TypeReference> paramCollection, Map<Class, Class> paramMap, String paramString, boolean paramBoolean1, RuntimeAnnotationReader paramRuntimeAnnotationReader, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
    throws JAXBException
  {
    JAXBContextImpl.JAXBContextBuilder localJAXBContextBuilder = new JAXBContextImpl.JAXBContextBuilder();
    localJAXBContextBuilder.setClasses(paramArrayOfClass);
    localJAXBContextBuilder.setTypeRefs(paramCollection);
    localJAXBContextBuilder.setSubclassReplacements(paramMap);
    localJAXBContextBuilder.setDefaultNsUri(paramString);
    localJAXBContextBuilder.setC14NSupport(paramBoolean1);
    localJAXBContextBuilder.setAnnotationReader(paramRuntimeAnnotationReader);
    localJAXBContextBuilder.setXmlAccessorFactorySupport(paramBoolean2);
    localJAXBContextBuilder.setAllNillable(paramBoolean3);
    localJAXBContextBuilder.setRetainPropertyInfo(paramBoolean4);
    localJAXBContextBuilder.setImprovedXsiTypeHandling(paramBoolean5);
    return localJAXBContextBuilder.build();
  }
  
  public static JAXBContext createContext(String paramString, ClassLoader paramClassLoader, Map<String, Object> paramMap)
    throws JAXBException
  {
    FinalArrayList localFinalArrayList = new FinalArrayList();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ":");
    while (localStringTokenizer.hasMoreTokens())
    {
      int j;
      int i = j = 0;
      String str = localStringTokenizer.nextToken();
      try
      {
        Class localClass = paramClassLoader.loadClass(str + ".ObjectFactory");
        localFinalArrayList.add(localClass);
        i = 1;
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      List localList;
      try
      {
        localList = loadIndexedClasses(str, paramClassLoader);
      }
      catch (IOException localIOException)
      {
        throw new JAXBException(localIOException);
      }
      if (localList != null)
      {
        localFinalArrayList.addAll(localList);
        j = 1;
      }
      if ((i == 0) && (j == 0)) {
        throw new JAXBException(Messages.BROKEN_CONTEXTPATH.format(new Object[] { str }));
      }
    }
    return createContext((Class[])localFinalArrayList.toArray(new Class[localFinalArrayList.size()]), paramMap);
  }
  
  private static List<Class> loadIndexedClasses(String paramString, ClassLoader paramClassLoader)
    throws IOException, JAXBException
  {
    String str1 = paramString.replace('.', '/') + "/jaxb.index";
    InputStream localInputStream = paramClassLoader.getResourceAsStream(str1);
    if (localInputStream == null) {
      return null;
    }
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "UTF-8"));
    try
    {
      FinalArrayList localFinalArrayList1 = new FinalArrayList();
      String str2 = localBufferedReader.readLine();
      while (str2 != null)
      {
        str2 = str2.trim();
        if ((str2.startsWith("#")) || (str2.length() == 0))
        {
          str2 = localBufferedReader.readLine();
        }
        else
        {
          if (str2.endsWith(".class")) {
            throw new JAXBException(Messages.ILLEGAL_ENTRY.format(new Object[] { str2 }));
          }
          try
          {
            localFinalArrayList1.add(paramClassLoader.loadClass(paramString + '.' + str2));
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            throw new JAXBException(Messages.ERROR_LOADING_CLASS.format(new Object[] { str2, str1 }), localClassNotFoundException);
          }
          str2 = localBufferedReader.readLine();
        }
      }
      FinalArrayList localFinalArrayList2 = localFinalArrayList1;
      return localFinalArrayList2;
    }
    finally
    {
      localBufferedReader.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\ContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */