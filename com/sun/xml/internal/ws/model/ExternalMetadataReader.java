package com.sun.xml.internal.ws.model;

import com.oracle.xmlns.internal.webservices.jaxws_databinding.ExistingAnnotationsType;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaMethod;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaMethod.JavaParams;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaParam;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaWsdlMappingType;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaWsdlMappingType.JavaMethods;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.ObjectFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ExternalMetadataReader
  extends ReflectAnnotationReader
{
  private static final String NAMESPACE_WEBLOGIC_WSEE_DATABINDING = "http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding";
  private static final String NAMESPACE_JAXWS_RI_EXTERNAL_METADATA = "http://xmlns.oracle.com/webservices/jaxws-databinding";
  private Map<String, JavaWsdlMappingType> readers = new HashMap();
  
  public ExternalMetadataReader(Collection<File> paramCollection, Collection<String> paramCollection1, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2)
  {
    Iterator localIterator;
    Object localObject;
    JavaWsdlMappingType localJavaWsdlMappingType;
    if (paramCollection != null)
    {
      localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        localObject = (File)localIterator.next();
        try
        {
          String str1 = Util.documentRootNamespace(newSource((File)localObject), paramBoolean2);
          localJavaWsdlMappingType = parseMetadata(paramBoolean1, newSource((File)localObject), str1, paramBoolean2);
          readers.put(localJavaWsdlMappingType.getJavaTypeName(), localJavaWsdlMappingType);
        }
        catch (Exception localException1)
        {
          throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { ((File)localObject).getAbsolutePath() });
        }
      }
    }
    if (paramCollection1 != null)
    {
      localIterator = paramCollection1.iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        try
        {
          String str2 = Util.documentRootNamespace(newSource((String)localObject, paramClassLoader), paramBoolean2);
          localJavaWsdlMappingType = parseMetadata(paramBoolean1, newSource((String)localObject, paramClassLoader), str2, paramBoolean2);
          readers.put(localJavaWsdlMappingType.getJavaTypeName(), localJavaWsdlMappingType);
        }
        catch (Exception localException2)
        {
          throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { localObject });
        }
      }
    }
  }
  
  private StreamSource newSource(String paramString, ClassLoader paramClassLoader)
  {
    InputStream localInputStream = paramClassLoader.getResourceAsStream(paramString);
    return new StreamSource(localInputStream);
  }
  
  private JavaWsdlMappingType parseMetadata(boolean paramBoolean1, StreamSource paramStreamSource, String paramString, boolean paramBoolean2)
    throws JAXBException, IOException, TransformerException
  {
    if ("http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding".equals(paramString)) {
      return Util.transformAndRead(paramStreamSource, paramBoolean2);
    }
    if ("http://xmlns.oracle.com/webservices/jaxws-databinding".equals(paramString)) {
      return Util.read(paramStreamSource, paramBoolean1, paramBoolean2);
    }
    throw new RuntimeModelerException("runtime.modeler.external.metadata.unsupported.schema", new Object[] { paramString, Arrays.asList(new String[] { "http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding", "http://xmlns.oracle.com/webservices/jaxws-databinding" }).toString() });
  }
  
  private StreamSource newSource(File paramFile)
  {
    try
    {
      return new StreamSource(new FileInputStream(paramFile));
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { paramFile.getAbsolutePath() });
    }
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass, Class<?> paramClass1)
  {
    JavaWsdlMappingType localJavaWsdlMappingType = reader(paramClass1);
    return localJavaWsdlMappingType == null ? super.getAnnotation(paramClass, paramClass1) : (Annotation)Util.annotation(localJavaWsdlMappingType, paramClass);
  }
  
  private JavaWsdlMappingType reader(Class<?> paramClass)
  {
    return (JavaWsdlMappingType)readers.get(paramClass.getName());
  }
  
  Annotation[] getAnnotations(List<Object> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (Annotation.class.isInstance(localObject)) {
        localArrayList.add(Annotation.class.cast(localObject));
      }
    }
    return (Annotation[])localArrayList.toArray(new Annotation[localArrayList.size()]);
  }
  
  public Annotation[] getAnnotations(final Class<?> paramClass)
  {
    Merger local1 = new Merger(reader(paramClass))
    {
      Annotation[] reflection()
      {
        return ExternalMetadataReader.this.getAnnotations(paramClass);
      }
      
      Annotation[] external()
      {
        return getAnnotations(reader.getClassAnnotation());
      }
    };
    return (Annotation[])local1.merge();
  }
  
  public Annotation[] getAnnotations(final Method paramMethod)
  {
    Merger local2 = new Merger(reader(paramMethod.getDeclaringClass()))
    {
      Annotation[] reflection()
      {
        return ExternalMetadataReader.this.getAnnotations(paramMethod);
      }
      
      Annotation[] external()
      {
        JavaMethod localJavaMethod = getJavaMethod(paramMethod, reader);
        return localJavaMethod == null ? new Annotation[0] : getAnnotations(localJavaMethod.getMethodAnnotation());
      }
    };
    return (Annotation[])local2.merge();
  }
  
  public <A extends Annotation> A getAnnotation(final Class<A> paramClass, final Method paramMethod)
  {
    Merger local3 = new Merger(reader(paramMethod.getDeclaringClass()))
    {
      Annotation reflection()
      {
        return ExternalMetadataReader.this.getAnnotation(paramClass, paramMethod);
      }
      
      Annotation external()
      {
        JavaMethod localJavaMethod = getJavaMethod(paramMethod, reader);
        return (Annotation)ExternalMetadataReader.Util.annotation(localJavaMethod, paramClass);
      }
    };
    return (Annotation)local3.merge();
  }
  
  public Annotation[][] getParameterAnnotations(final Method paramMethod)
  {
    Merger local4 = new Merger(reader(paramMethod.getDeclaringClass()))
    {
      Annotation[][] reflection()
      {
        return ExternalMetadataReader.this.getParameterAnnotations(paramMethod);
      }
      
      Annotation[][] external()
      {
        JavaMethod localJavaMethod = getJavaMethod(paramMethod, reader);
        Annotation[][] arrayOfAnnotation = paramMethod.getParameterAnnotations();
        for (int i = 0; i < paramMethod.getParameterTypes().length; i++) {
          if (localJavaMethod != null)
          {
            JavaParam localJavaParam = (JavaParam)localJavaMethod.getJavaParams().getJavaParam().get(i);
            arrayOfAnnotation[i] = getAnnotations(localJavaParam.getParamAnnotation());
          }
        }
        return arrayOfAnnotation;
      }
    };
    return (Annotation[][])local4.merge();
  }
  
  public void getProperties(Map<String, Object> paramMap, Class<?> paramClass)
  {
    JavaWsdlMappingType localJavaWsdlMappingType = reader(paramClass);
    if ((localJavaWsdlMappingType == null) || (ExistingAnnotationsType.MERGE.equals(localJavaWsdlMappingType.getExistingAnnotations()))) {
      super.getProperties(paramMap, paramClass);
    }
  }
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod)
  {
    JavaWsdlMappingType localJavaWsdlMappingType = reader(paramMethod.getDeclaringClass());
    if ((localJavaWsdlMappingType == null) || (ExistingAnnotationsType.MERGE.equals(localJavaWsdlMappingType.getExistingAnnotations()))) {
      super.getProperties(paramMap, paramMethod);
    }
    if (localJavaWsdlMappingType != null)
    {
      JavaMethod localJavaMethod = getJavaMethod(paramMethod, localJavaWsdlMappingType);
      Element[] arrayOfElement = Util.annotation(localJavaMethod);
      paramMap.put("eclipselink-oxm-xml.xml-element", findXmlElement(arrayOfElement));
    }
  }
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod, int paramInt)
  {
    JavaWsdlMappingType localJavaWsdlMappingType = reader(paramMethod.getDeclaringClass());
    if ((localJavaWsdlMappingType == null) || (ExistingAnnotationsType.MERGE.equals(localJavaWsdlMappingType.getExistingAnnotations()))) {
      super.getProperties(paramMap, paramMethod, paramInt);
    }
    if (localJavaWsdlMappingType != null)
    {
      JavaMethod localJavaMethod = getJavaMethod(paramMethod, localJavaWsdlMappingType);
      if (localJavaMethod == null) {
        return;
      }
      JavaParam localJavaParam = (JavaParam)localJavaMethod.getJavaParams().getJavaParam().get(paramInt);
      Element[] arrayOfElement = Util.annotation(localJavaParam);
      paramMap.put("eclipselink-oxm-xml.xml-element", findXmlElement(arrayOfElement));
    }
  }
  
  JavaMethod getJavaMethod(Method paramMethod, JavaWsdlMappingType paramJavaWsdlMappingType)
  {
    JavaWsdlMappingType.JavaMethods localJavaMethods = paramJavaWsdlMappingType.getJavaMethods();
    if (localJavaMethods == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = localJavaMethods.getJavaMethod().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (JavaMethod)((Iterator)localObject1).next();
      if (paramMethod.getName().equals(((JavaMethod)localObject2).getName())) {
        localArrayList.add(localObject2);
      }
    }
    if (localArrayList.isEmpty()) {
      return null;
    }
    if (localArrayList.size() == 1) {
      return (JavaMethod)localArrayList.get(0);
    }
    localObject1 = paramMethod.getParameterTypes();
    Object localObject2 = localArrayList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      JavaMethod localJavaMethod = (JavaMethod)((Iterator)localObject2).next();
      JavaMethod.JavaParams localJavaParams = localJavaMethod.getJavaParams();
      if ((localJavaParams != null) && (localJavaParams.getJavaParam() != null) && (localJavaParams.getJavaParam().size() == localObject1.length))
      {
        int i = 0;
        for (int j = 0; j < localObject1.length; j++)
        {
          JavaParam localJavaParam = (JavaParam)localJavaParams.getJavaParam().get(j);
          if (localObject1[j].getName().equals(localJavaParam.getJavaType())) {
            i++;
          }
        }
        if (i == localObject1.length) {
          return localJavaMethod;
        }
      }
    }
    return null;
  }
  
  Element findXmlElement(Element[] paramArrayOfElement)
  {
    if (paramArrayOfElement == null) {
      return null;
    }
    for (Element localElement : paramArrayOfElement)
    {
      if (localElement.getLocalName().equals("java-type")) {
        return localElement;
      }
      if (localElement.getLocalName().equals("xml-element")) {
        return localElement;
      }
    }
    return null;
  }
  
  static abstract class Merger<T>
  {
    JavaWsdlMappingType reader;
    
    Merger(JavaWsdlMappingType paramJavaWsdlMappingType)
    {
      reader = paramJavaWsdlMappingType;
    }
    
    abstract T reflection();
    
    abstract T external();
    
    T merge()
    {
      Object localObject1 = reflection();
      if (reader == null) {
        return (T)localObject1;
      }
      Object localObject2 = external();
      if (!ExistingAnnotationsType.MERGE.equals(reader.getExistingAnnotations())) {
        return (T)localObject2;
      }
      if ((localObject1 instanceof Annotation)) {
        return doMerge((Annotation)localObject1, (Annotation)localObject2);
      }
      if ((localObject1 instanceof Annotation[][])) {
        return doMerge((Annotation[][])localObject1, (Annotation[][])localObject2);
      }
      return doMerge((Annotation[])localObject1, (Annotation[])localObject2);
    }
    
    private Annotation doMerge(Annotation paramAnnotation1, Annotation paramAnnotation2)
    {
      return paramAnnotation2 != null ? paramAnnotation2 : paramAnnotation1;
    }
    
    private Annotation[][] doMerge(Annotation[][] paramArrayOfAnnotation1, Annotation[][] paramArrayOfAnnotation2)
    {
      for (int i = 0; i < paramArrayOfAnnotation1.length; i++) {
        paramArrayOfAnnotation1[i] = doMerge(paramArrayOfAnnotation1[i], paramArrayOfAnnotation2.length > i ? paramArrayOfAnnotation2[i] : null);
      }
      return paramArrayOfAnnotation1;
    }
    
    private Annotation[] doMerge(Annotation[] paramArrayOfAnnotation1, Annotation[] paramArrayOfAnnotation2)
    {
      HashMap localHashMap = new HashMap();
      Object localObject2;
      if (paramArrayOfAnnotation1 != null) {
        for (localObject2 : paramArrayOfAnnotation1) {
          localHashMap.put(((Annotation)localObject2).annotationType().getName(), localObject2);
        }
      }
      if (paramArrayOfAnnotation2 != null) {
        for (localObject2 : paramArrayOfAnnotation2) {
          localHashMap.put(((Annotation)localObject2).annotationType().getName(), localObject2);
        }
      }
      ??? = localHashMap.values();
      ??? = ((Collection)???).size();
      return ??? == 0 ? null : (Annotation[])((Collection)???).toArray(new Annotation[???]);
    }
  }
  
  static class Util
  {
    private static final String DATABINDING_XSD = "jaxws-databinding.xsd";
    private static final String TRANSLATE_NAMESPACES_XSL = "jaxws-databinding-translate-namespaces.xml";
    static Schema schema;
    static JAXBContext jaxbContext = createJaxbContext(false);
    
    Util() {}
    
    private static URL getResource()
    {
      ClassLoader localClassLoader = Util.class.getClassLoader();
      return localClassLoader != null ? localClassLoader.getResource("jaxws-databinding.xsd") : ClassLoader.getSystemResource("jaxws-databinding.xsd");
    }
    
    private static JAXBContext createJaxbContext(boolean paramBoolean)
    {
      Class[] arrayOfClass = { ObjectFactory.class };
      try
      {
        if (paramBoolean)
        {
          HashMap localHashMap = new HashMap();
          localHashMap.put("com.sun.xml.internal.bind.disableXmlSecurity", Boolean.valueOf(paramBoolean));
          return JAXBContext.newInstance(arrayOfClass, localHashMap);
        }
        return JAXBContext.newInstance(arrayOfClass);
      }
      catch (JAXBException localJAXBException)
      {
        localJAXBException.printStackTrace();
      }
      return null;
    }
    
    public static JavaWsdlMappingType read(Source paramSource, boolean paramBoolean1, boolean paramBoolean2)
      throws IOException, JAXBException
    {
      JAXBContext localJAXBContext = jaxbContext(paramBoolean2);
      try
      {
        Unmarshaller localUnmarshaller1 = localJAXBContext.createUnmarshaller();
        if (paramBoolean1)
        {
          if (schema == null) {}
          localUnmarshaller1.setSchema(schema);
        }
        localObject1 = localUnmarshaller1.unmarshal(paramSource);
        return getJavaWsdlMapping(localObject1);
      }
      catch (JAXBException localJAXBException)
      {
        Object localObject1 = new URL(paramSource.getSystemId());
        StreamSource localStreamSource = new StreamSource(((URL)localObject1).openStream());
        Unmarshaller localUnmarshaller2 = localJAXBContext.createUnmarshaller();
        if (paramBoolean1)
        {
          if (schema == null) {}
          localUnmarshaller2.setSchema(schema);
        }
        Object localObject2 = localUnmarshaller2.unmarshal(localStreamSource);
        return getJavaWsdlMapping(localObject2);
      }
    }
    
    private static JAXBContext jaxbContext(boolean paramBoolean)
    {
      return paramBoolean ? createJaxbContext(true) : jaxbContext;
    }
    
    public static JavaWsdlMappingType transformAndRead(Source paramSource, boolean paramBoolean)
      throws TransformerException, JAXBException
    {
      StreamSource localStreamSource = new StreamSource(Util.class.getResourceAsStream("jaxws-databinding-translate-namespaces.xml"));
      JAXBResult localJAXBResult = new JAXBResult(jaxbContext(paramBoolean));
      TransformerFactory localTransformerFactory = XmlUtil.newTransformerFactory(!paramBoolean);
      Transformer localTransformer = localTransformerFactory.newTemplates(localStreamSource).newTransformer();
      localTransformer.transform(paramSource, localJAXBResult);
      return getJavaWsdlMapping(localJAXBResult.getResult());
    }
    
    static JavaWsdlMappingType getJavaWsdlMapping(Object paramObject)
    {
      Object localObject = (paramObject instanceof JAXBElement) ? ((JAXBElement)paramObject).getValue() : paramObject;
      if ((localObject instanceof JavaWsdlMappingType)) {
        return (JavaWsdlMappingType)localObject;
      }
      return null;
    }
    
    static <T> T findInstanceOf(Class<T> paramClass, List<Object> paramList)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (paramClass.isInstance(localObject)) {
          return (T)paramClass.cast(localObject);
        }
      }
      return null;
    }
    
    public static <T> T annotation(JavaWsdlMappingType paramJavaWsdlMappingType, Class<T> paramClass)
    {
      if ((paramJavaWsdlMappingType == null) || (paramJavaWsdlMappingType.getClassAnnotation() == null)) {
        return null;
      }
      return (T)findInstanceOf(paramClass, paramJavaWsdlMappingType.getClassAnnotation());
    }
    
    public static <T> T annotation(JavaMethod paramJavaMethod, Class<T> paramClass)
    {
      if ((paramJavaMethod == null) || (paramJavaMethod.getMethodAnnotation() == null)) {
        return null;
      }
      return (T)findInstanceOf(paramClass, paramJavaMethod.getMethodAnnotation());
    }
    
    public static <T> T annotation(JavaParam paramJavaParam, Class<T> paramClass)
    {
      if ((paramJavaParam == null) || (paramJavaParam.getParamAnnotation() == null)) {
        return null;
      }
      return (T)findInstanceOf(paramClass, paramJavaParam.getParamAnnotation());
    }
    
    public static Element[] annotation(JavaMethod paramJavaMethod)
    {
      if ((paramJavaMethod == null) || (paramJavaMethod.getMethodAnnotation() == null)) {
        return null;
      }
      return findElements(paramJavaMethod.getMethodAnnotation());
    }
    
    public static Element[] annotation(JavaParam paramJavaParam)
    {
      if ((paramJavaParam == null) || (paramJavaParam.getParamAnnotation() == null)) {
        return null;
      }
      return findElements(paramJavaParam.getParamAnnotation());
    }
    
    private static Element[] findElements(List<Object> paramList)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if ((localObject instanceof Element)) {
          localArrayList.add((Element)localObject);
        }
      }
      return (Element[])localArrayList.toArray(new Element[localArrayList.size()]);
    }
    
    static String documentRootNamespace(Source paramSource, boolean paramBoolean)
      throws XMLStreamException
    {
      XMLInputFactory localXMLInputFactory = XmlUtil.newXMLInputFactory(!paramBoolean);
      XMLStreamReader localXMLStreamReader = localXMLInputFactory.createXMLStreamReader(paramSource);
      XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
      String str = localXMLStreamReader.getName().getNamespaceURI();
      XMLStreamReaderUtil.close(localXMLStreamReader);
      return str;
    }
    
    static
    {
      SchemaFactory localSchemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
      try
      {
        URL localURL = getResource();
        if (localURL != null) {
          schema = localSchemaFactory.newSchema(localURL);
        }
      }
      catch (SAXException localSAXException) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\ExternalMetadataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */