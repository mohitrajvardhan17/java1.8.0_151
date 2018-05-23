package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Pool;
import com.sun.istack.internal.Pool.Impl;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.bind.api.ErrorListener;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext.State;
import com.sun.xml.internal.bind.v2.schemagen.XmlSchemaGenerator;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.util.QNameMap.Entry;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.txw2.output.ResultFactory;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class JAXBContextImpl
  extends JAXBRIContext
{
  private final Map<TypeReference, Bridge> bridges = new LinkedHashMap();
  private static DocumentBuilder db;
  private final QNameMap<JaxBeanInfo> rootMap = new QNameMap();
  private final HashMap<QName, JaxBeanInfo> typeMap = new HashMap();
  private final Map<Class, JaxBeanInfo> beanInfoMap = new LinkedHashMap();
  protected Map<RuntimeTypeInfo, JaxBeanInfo> beanInfos = new LinkedHashMap();
  private final Map<Class, Map<QName, ElementBeanInfoImpl>> elements = new LinkedHashMap();
  public final Pool<Marshaller> marshallerPool = new Pool.Impl()
  {
    @NotNull
    protected Marshaller create()
    {
      return createMarshaller();
    }
  };
  public final Pool<Unmarshaller> unmarshallerPool = new Pool.Impl()
  {
    @NotNull
    protected Unmarshaller create()
    {
      return createUnmarshaller();
    }
  };
  public NameBuilder nameBuilder = new NameBuilder();
  public final NameList nameList;
  private final String defaultNsUri;
  private final Class[] classes;
  protected final boolean c14nSupport;
  public final boolean xmlAccessorFactorySupport;
  public final boolean allNillable;
  public final boolean retainPropertyInfo;
  public final boolean supressAccessorWarnings;
  public final boolean improvedXsiTypeHandling;
  public final boolean disableSecurityProcessing;
  private WeakReference<RuntimeTypeInfoSet> typeInfoSetCache;
  @NotNull
  private RuntimeAnnotationReader annotationReader;
  private boolean hasSwaRef;
  @NotNull
  private final Map<Class, Class> subclassReplacements;
  public final boolean fastBoot;
  private Set<XmlNs> xmlNsSet = null;
  private Encoded[] utf8nameTable;
  private static final Comparator<QName> QNAME_COMPARATOR = new Comparator()
  {
    public int compare(QName paramAnonymousQName1, QName paramAnonymousQName2)
    {
      int i = paramAnonymousQName1.getLocalPart().compareTo(paramAnonymousQName2.getLocalPart());
      if (i != 0) {
        return i;
      }
      return paramAnonymousQName1.getNamespaceURI().compareTo(paramAnonymousQName2.getNamespaceURI());
    }
  };
  
  public Set<XmlNs> getXmlNsSet()
  {
    return xmlNsSet;
  }
  
  private JAXBContextImpl(JAXBContextBuilder paramJAXBContextBuilder)
    throws JAXBException
  {
    defaultNsUri = defaultNsUri;
    retainPropertyInfo = retainPropertyInfo;
    annotationReader = annotationReader;
    subclassReplacements = subclassReplacements;
    c14nSupport = c14nSupport;
    classes = classes;
    xmlAccessorFactorySupport = xmlAccessorFactorySupport;
    allNillable = allNillable;
    supressAccessorWarnings = supressAccessorWarnings;
    improvedXsiTypeHandling = improvedXsiTypeHandling;
    disableSecurityProcessing = disableSecurityProcessing;
    Collection localCollection = typeRefs;
    boolean bool;
    try
    {
      bool = Boolean.getBoolean(JAXBContextImpl.class.getName() + ".fastBoot");
    }
    catch (SecurityException localSecurityException)
    {
      bool = false;
    }
    fastBoot = bool;
    RuntimeTypeInfoSet localRuntimeTypeInfoSet = getTypeInfoSet();
    elements.put(null, new LinkedHashMap());
    Object localObject1 = RuntimeBuiltinLeafInfoImpl.builtinBeanInfos.iterator();
    Object localObject3;
    Object localObject4;
    Object localObject5;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (RuntimeBuiltinLeafInfo)((Iterator)localObject1).next();
      localObject3 = new LeafBeanInfoImpl(this, (RuntimeLeafInfo)localObject2);
      beanInfoMap.put(((RuntimeBuiltinLeafInfo)localObject2).getClazz(), localObject3);
      localObject4 = ((LeafBeanInfoImpl)localObject3).getTypeNames().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (QName)((Iterator)localObject4).next();
        typeMap.put(localObject5, localObject3);
      }
    }
    localObject1 = localRuntimeTypeInfoSet.enums().values().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (RuntimeEnumLeafInfo)((Iterator)localObject1).next();
      localObject3 = getOrCreate((RuntimeEnumLeafInfo)localObject2);
      localObject4 = ((JaxBeanInfo)localObject3).getTypeNames().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (QName)((Iterator)localObject4).next();
        typeMap.put(localObject5, localObject3);
      }
      if (((RuntimeEnumLeafInfo)localObject2).isElement()) {
        rootMap.put(((RuntimeEnumLeafInfo)localObject2).getElementName(), localObject3);
      }
    }
    localObject1 = localRuntimeTypeInfoSet.arrays().values().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (RuntimeArrayInfo)((Iterator)localObject1).next();
      localObject3 = getOrCreate((RuntimeArrayInfo)localObject2);
      localObject4 = ((JaxBeanInfo)localObject3).getTypeNames().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (QName)((Iterator)localObject4).next();
        typeMap.put(localObject5, localObject3);
      }
    }
    localObject1 = localRuntimeTypeInfoSet.beans().entrySet().iterator();
    Object localObject6;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      localObject3 = getOrCreate((RuntimeClassInfo)((Map.Entry)localObject2).getValue());
      localObject4 = (XmlSchema)annotationReader.getPackageAnnotation(XmlSchema.class, ((Map.Entry)localObject2).getKey(), null);
      if ((localObject4 != null) && (((XmlSchema)localObject4).xmlns() != null) && (((XmlSchema)localObject4).xmlns().length > 0))
      {
        if (xmlNsSet == null) {
          xmlNsSet = new HashSet();
        }
        xmlNsSet.addAll(Arrays.asList(((XmlSchema)localObject4).xmlns()));
      }
      if (((ClassBeanInfoImpl)localObject3).isElement()) {
        rootMap.put(((RuntimeClassInfo)((Map.Entry)localObject2).getValue()).getElementName(), localObject3);
      }
      localObject5 = ((ClassBeanInfoImpl)localObject3).getTypeNames().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject6 = (QName)((Iterator)localObject5).next();
        typeMap.put(localObject6, localObject3);
      }
    }
    localObject1 = localRuntimeTypeInfoSet.getAllElements().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (RuntimeElementInfo)((Iterator)localObject1).next();
      localObject3 = getOrCreate((RuntimeElementInfo)localObject2);
      if (((RuntimeElementInfo)localObject2).getScope() == null) {
        rootMap.put(((RuntimeElementInfo)localObject2).getElementName(), localObject3);
      }
      localObject4 = ((RuntimeElementInfo)localObject2).getScope();
      localObject5 = localObject4 == null ? null : (Class)((RuntimeClassInfo)localObject4).getClazz();
      localObject6 = (Map)elements.get(localObject5);
      if (localObject6 == null)
      {
        localObject6 = new LinkedHashMap();
        elements.put(localObject5, localObject6);
      }
      ((Map)localObject6).put(((RuntimeElementInfo)localObject2).getElementName(), localObject3);
    }
    beanInfoMap.put(JAXBElement.class, new ElementBeanInfoImpl(this));
    beanInfoMap.put(CompositeStructure.class, new CompositeStructureBeanInfo(this));
    getOrCreate(localRuntimeTypeInfoSet.getAnyTypeInfo());
    localObject1 = beanInfos.values().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (JaxBeanInfo)((Iterator)localObject1).next();
      ((JaxBeanInfo)localObject2).link(this);
    }
    localObject1 = RuntimeUtil.primitiveToBox.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      beanInfoMap.put(((Map.Entry)localObject2).getKey(), beanInfoMap.get(((Map.Entry)localObject2).getValue()));
    }
    localObject1 = localRuntimeTypeInfoSet.getNavigator();
    Object localObject2 = localCollection.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (TypeReference)((Iterator)localObject2).next();
      localObject4 = (XmlJavaTypeAdapter)((TypeReference)localObject3).get(XmlJavaTypeAdapter.class);
      localObject5 = null;
      localObject6 = (XmlList)((TypeReference)localObject3).get(XmlList.class);
      Class localClass = (Class)((Navigator)localObject1).erasure(type);
      if (localObject4 != null) {
        localObject5 = new Adapter(((XmlJavaTypeAdapter)localObject4).value(), (Navigator)localObject1);
      }
      if (((TypeReference)localObject3).get(XmlAttachmentRef.class) != null)
      {
        localObject5 = new Adapter(SwaRefAdapter.class, (Navigator)localObject1);
        hasSwaRef = true;
      }
      if (localObject5 != null) {
        localClass = (Class)((Navigator)localObject1).erasure(defaultType);
      }
      Name localName = nameBuilder.createElementName(tagName);
      Object localObject7;
      if (localObject6 == null) {
        localObject7 = new BridgeImpl(this, localName, getBeanInfo(localClass, true), (TypeReference)localObject3);
      } else {
        localObject7 = new BridgeImpl(this, localName, new ValueListBeanInfoImpl(this, localClass), (TypeReference)localObject3);
      }
      if (localObject5 != null) {
        localObject7 = new BridgeAdapter((InternalBridge)localObject7, (Class)adapterType);
      }
      bridges.put(localObject3, localObject7);
    }
    nameList = nameBuilder.conclude();
    localObject2 = beanInfos.values().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (JaxBeanInfo)((Iterator)localObject2).next();
      ((JaxBeanInfo)localObject3).wrapUp();
    }
    nameBuilder = null;
    beanInfos = null;
  }
  
  public boolean hasSwaRef()
  {
    return hasSwaRef;
  }
  
  public RuntimeTypeInfoSet getRuntimeTypeInfoSet()
  {
    try
    {
      return getTypeInfoSet();
    }
    catch (IllegalAnnotationsException localIllegalAnnotationsException)
    {
      throw new AssertionError(localIllegalAnnotationsException);
    }
  }
  
  public RuntimeTypeInfoSet getTypeInfoSet()
    throws IllegalAnnotationsException
  {
    if (typeInfoSetCache != null)
    {
      localObject1 = (RuntimeTypeInfoSet)typeInfoSetCache.get();
      if (localObject1 != null) {
        return (RuntimeTypeInfoSet)localObject1;
      }
    }
    Object localObject1 = new RuntimeModelBuilder(this, annotationReader, subclassReplacements, defaultNsUri);
    IllegalAnnotationsException.Builder localBuilder = new IllegalAnnotationsException.Builder();
    ((RuntimeModelBuilder)localObject1).setErrorHandler(localBuilder);
    for (Object localObject3 : classes) {
      if (localObject3 != CompositeStructure.class) {
        ((RuntimeModelBuilder)localObject1).getTypeInfo(new Ref(localObject3));
      }
    }
    hasSwaRef |= hasSwaRef;
    ??? = ((RuntimeModelBuilder)localObject1).link();
    localBuilder.check();
    assert (??? != null) : "if no error was reported, the link must be a success";
    typeInfoSetCache = new WeakReference(???);
    return (RuntimeTypeInfoSet)???;
  }
  
  public ElementBeanInfoImpl getElement(Class paramClass, QName paramQName)
  {
    Map localMap = (Map)elements.get(paramClass);
    if (localMap != null)
    {
      ElementBeanInfoImpl localElementBeanInfoImpl = (ElementBeanInfoImpl)localMap.get(paramQName);
      if (localElementBeanInfoImpl != null) {
        return localElementBeanInfoImpl;
      }
    }
    localMap = (Map)elements.get(null);
    return (ElementBeanInfoImpl)localMap.get(paramQName);
  }
  
  private ElementBeanInfoImpl getOrCreate(RuntimeElementInfo paramRuntimeElementInfo)
  {
    JaxBeanInfo localJaxBeanInfo = (JaxBeanInfo)beanInfos.get(paramRuntimeElementInfo);
    if (localJaxBeanInfo != null) {
      return (ElementBeanInfoImpl)localJaxBeanInfo;
    }
    return new ElementBeanInfoImpl(this, paramRuntimeElementInfo);
  }
  
  protected JaxBeanInfo getOrCreate(RuntimeEnumLeafInfo paramRuntimeEnumLeafInfo)
  {
    Object localObject = (JaxBeanInfo)beanInfos.get(paramRuntimeEnumLeafInfo);
    if (localObject != null) {
      return (JaxBeanInfo)localObject;
    }
    localObject = new LeafBeanInfoImpl(this, paramRuntimeEnumLeafInfo);
    beanInfoMap.put(jaxbType, localObject);
    return (JaxBeanInfo)localObject;
  }
  
  protected ClassBeanInfoImpl getOrCreate(RuntimeClassInfo paramRuntimeClassInfo)
  {
    ClassBeanInfoImpl localClassBeanInfoImpl = (ClassBeanInfoImpl)beanInfos.get(paramRuntimeClassInfo);
    if (localClassBeanInfoImpl != null) {
      return localClassBeanInfoImpl;
    }
    localClassBeanInfoImpl = new ClassBeanInfoImpl(this, paramRuntimeClassInfo);
    beanInfoMap.put(jaxbType, localClassBeanInfoImpl);
    return localClassBeanInfoImpl;
  }
  
  protected JaxBeanInfo getOrCreate(RuntimeArrayInfo paramRuntimeArrayInfo)
  {
    Object localObject = (JaxBeanInfo)beanInfos.get(paramRuntimeArrayInfo);
    if (localObject != null) {
      return (JaxBeanInfo)localObject;
    }
    localObject = new ArrayBeanInfoImpl(this, paramRuntimeArrayInfo);
    beanInfoMap.put(paramRuntimeArrayInfo.getType(), localObject);
    return (JaxBeanInfo)localObject;
  }
  
  public JaxBeanInfo getOrCreate(RuntimeTypeInfo paramRuntimeTypeInfo)
  {
    if ((paramRuntimeTypeInfo instanceof RuntimeElementInfo)) {
      return getOrCreate((RuntimeElementInfo)paramRuntimeTypeInfo);
    }
    if ((paramRuntimeTypeInfo instanceof RuntimeClassInfo)) {
      return getOrCreate((RuntimeClassInfo)paramRuntimeTypeInfo);
    }
    Object localObject;
    if ((paramRuntimeTypeInfo instanceof RuntimeLeafInfo))
    {
      localObject = (JaxBeanInfo)beanInfos.get(paramRuntimeTypeInfo);
      assert (localObject != null);
      return (JaxBeanInfo)localObject;
    }
    if ((paramRuntimeTypeInfo instanceof RuntimeArrayInfo)) {
      return getOrCreate((RuntimeArrayInfo)paramRuntimeTypeInfo);
    }
    if (paramRuntimeTypeInfo.getType() == Object.class)
    {
      localObject = (JaxBeanInfo)beanInfoMap.get(Object.class);
      if (localObject == null)
      {
        localObject = new AnyTypeBeanInfo(this, paramRuntimeTypeInfo);
        beanInfoMap.put(Object.class, localObject);
      }
      return (JaxBeanInfo)localObject;
    }
    throw new IllegalArgumentException();
  }
  
  public final JaxBeanInfo getBeanInfo(Object paramObject)
  {
    for (Object localObject1 = paramObject.getClass(); localObject1 != Object.class; localObject1 = ((Class)localObject1).getSuperclass())
    {
      JaxBeanInfo localJaxBeanInfo1 = (JaxBeanInfo)beanInfoMap.get(localObject1);
      if (localJaxBeanInfo1 != null) {
        return localJaxBeanInfo1;
      }
    }
    if ((paramObject instanceof org.w3c.dom.Element)) {
      return (JaxBeanInfo)beanInfoMap.get(Object.class);
    }
    for (Object localObject2 : paramObject.getClass().getInterfaces())
    {
      JaxBeanInfo localJaxBeanInfo2 = (JaxBeanInfo)beanInfoMap.get(localObject2);
      if (localJaxBeanInfo2 != null) {
        return localJaxBeanInfo2;
      }
    }
    return null;
  }
  
  public final JaxBeanInfo getBeanInfo(Object paramObject, boolean paramBoolean)
    throws JAXBException
  {
    JaxBeanInfo localJaxBeanInfo = getBeanInfo(paramObject);
    if (localJaxBeanInfo != null) {
      return localJaxBeanInfo;
    }
    if (paramBoolean)
    {
      if ((paramObject instanceof Document)) {
        throw new JAXBException(Messages.ELEMENT_NEEDED_BUT_FOUND_DOCUMENT.format(new Object[] { paramObject.getClass() }));
      }
      throw new JAXBException(Messages.UNKNOWN_CLASS.format(new Object[] { paramObject.getClass() }));
    }
    return null;
  }
  
  public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> paramClass)
  {
    return (JaxBeanInfo)beanInfoMap.get(paramClass);
  }
  
  public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> paramClass, boolean paramBoolean)
    throws JAXBException
  {
    JaxBeanInfo localJaxBeanInfo = getBeanInfo(paramClass);
    if (localJaxBeanInfo != null) {
      return localJaxBeanInfo;
    }
    if (paramBoolean) {
      throw new JAXBException(paramClass.getName() + " is not known to this context");
    }
    return null;
  }
  
  public final Loader selectRootLoader(UnmarshallingContext.State paramState, TagName paramTagName)
  {
    JaxBeanInfo localJaxBeanInfo = (JaxBeanInfo)rootMap.get(uri, local);
    if (localJaxBeanInfo == null) {
      return null;
    }
    return localJaxBeanInfo.getLoader(this, true);
  }
  
  public JaxBeanInfo getGlobalType(QName paramQName)
  {
    return (JaxBeanInfo)typeMap.get(paramQName);
  }
  
  public String getNearestTypeName(QName paramQName)
  {
    String[] arrayOfString = new String[typeMap.size()];
    int i = 0;
    Object localObject = typeMap.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      QName localQName = (QName)((Iterator)localObject).next();
      if (localQName.getLocalPart().equals(paramQName.getLocalPart())) {
        return localQName.toString();
      }
      arrayOfString[(i++)] = localQName.toString();
    }
    localObject = EditDistance.findNearest(paramQName.toString(), arrayOfString);
    if (EditDistance.editDistance((String)localObject, paramQName.toString()) > 10) {
      return null;
    }
    return (String)localObject;
  }
  
  public Set<QName> getValidRootNames()
  {
    TreeSet localTreeSet = new TreeSet(QNAME_COMPARATOR);
    Iterator localIterator = rootMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      QNameMap.Entry localEntry = (QNameMap.Entry)localIterator.next();
      localTreeSet.add(localEntry.createQName());
    }
    return localTreeSet;
  }
  
  public synchronized Encoded[] getUTF8NameTable()
  {
    if (utf8nameTable == null)
    {
      Encoded[] arrayOfEncoded = new Encoded[nameList.localNames.length];
      for (int i = 0; i < arrayOfEncoded.length; i++)
      {
        Encoded localEncoded = new Encoded(nameList.localNames[i]);
        localEncoded.compact();
        arrayOfEncoded[i] = localEncoded;
      }
      utf8nameTable = arrayOfEncoded;
    }
    return utf8nameTable;
  }
  
  public int getNumberOfLocalNames()
  {
    return nameList.localNames.length;
  }
  
  public int getNumberOfElementNames()
  {
    return nameList.numberOfElementNames;
  }
  
  public int getNumberOfAttributeNames()
  {
    return nameList.numberOfAttributeNames;
  }
  
  static Transformer createTransformer(boolean paramBoolean)
  {
    try
    {
      SAXTransformerFactory localSAXTransformerFactory = (SAXTransformerFactory)XmlFactory.createTransformerFactory(paramBoolean);
      return localSAXTransformerFactory.newTransformer();
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      throw new Error(localTransformerConfigurationException);
    }
  }
  
  public static TransformerHandler createTransformerHandler(boolean paramBoolean)
  {
    try
    {
      SAXTransformerFactory localSAXTransformerFactory = (SAXTransformerFactory)XmlFactory.createTransformerFactory(paramBoolean);
      return localSAXTransformerFactory.newTransformerHandler();
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      throw new Error(localTransformerConfigurationException);
    }
  }
  
  static Document createDom(boolean paramBoolean)
  {
    synchronized (JAXBContextImpl.class)
    {
      if (db == null) {
        try
        {
          DocumentBuilderFactory localDocumentBuilderFactory = XmlFactory.createDocumentBuilderFactory(paramBoolean);
          db = localDocumentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException localParserConfigurationException)
        {
          throw new FactoryConfigurationError(localParserConfigurationException);
        }
      }
      return db.newDocument();
    }
  }
  
  public MarshallerImpl createMarshaller()
  {
    return new MarshallerImpl(this, null);
  }
  
  public UnmarshallerImpl createUnmarshaller()
  {
    return new UnmarshallerImpl(this, null);
  }
  
  public Validator createValidator()
  {
    throw new UnsupportedOperationException(Messages.NOT_IMPLEMENTED_IN_2_0.format(new Object[0]));
  }
  
  public JAXBIntrospector createJAXBIntrospector()
  {
    new JAXBIntrospector()
    {
      public boolean isElement(Object paramAnonymousObject)
      {
        return getElementName(paramAnonymousObject) != null;
      }
      
      public QName getElementName(Object paramAnonymousObject)
      {
        try
        {
          return JAXBContextImpl.this.getElementName(paramAnonymousObject);
        }
        catch (JAXBException localJAXBException) {}
        return null;
      }
    };
  }
  
  private NonElement<Type, Class> getXmlType(RuntimeTypeInfoSet paramRuntimeTypeInfoSet, TypeReference paramTypeReference)
  {
    if (paramTypeReference == null) {
      throw new IllegalArgumentException();
    }
    XmlJavaTypeAdapter localXmlJavaTypeAdapter = (XmlJavaTypeAdapter)paramTypeReference.get(XmlJavaTypeAdapter.class);
    XmlList localXmlList = (XmlList)paramTypeReference.get(XmlList.class);
    Ref localRef = new Ref(annotationReader, paramRuntimeTypeInfoSet.getNavigator(), type, localXmlJavaTypeAdapter, localXmlList);
    return paramRuntimeTypeInfoSet.getTypeInfo(localRef);
  }
  
  public void generateEpisode(Result paramResult)
  {
    if (paramResult == null) {
      throw new IllegalArgumentException();
    }
    createSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(paramResult));
  }
  
  public void generateSchema(SchemaOutputResolver paramSchemaOutputResolver)
    throws IOException
  {
    if (paramSchemaOutputResolver == null) {
      throw new IOException(Messages.NULL_OUTPUT_RESOLVER.format(new Object[0]));
    }
    final SAXParseException[] arrayOfSAXParseException1 = new SAXParseException[1];
    final SAXParseException[] arrayOfSAXParseException2 = new SAXParseException[1];
    createSchemaGenerator().write(paramSchemaOutputResolver, new ErrorListener()
    {
      public void error(SAXParseException paramAnonymousSAXParseException)
      {
        arrayOfSAXParseException1[0] = paramAnonymousSAXParseException;
      }
      
      public void fatalError(SAXParseException paramAnonymousSAXParseException)
      {
        arrayOfSAXParseException1[0] = paramAnonymousSAXParseException;
      }
      
      public void warning(SAXParseException paramAnonymousSAXParseException)
      {
        arrayOfSAXParseException2[0] = paramAnonymousSAXParseException;
      }
      
      public void info(SAXParseException paramAnonymousSAXParseException) {}
    });
    IOException localIOException;
    if (arrayOfSAXParseException1[0] != null)
    {
      localIOException = new IOException(Messages.FAILED_TO_GENERATE_SCHEMA.format(new Object[0]));
      localIOException.initCause(arrayOfSAXParseException1[0]);
      throw localIOException;
    }
    if (arrayOfSAXParseException2[0] != null)
    {
      localIOException = new IOException(Messages.ERROR_PROCESSING_SCHEMA.format(new Object[0]));
      localIOException.initCause(arrayOfSAXParseException2[0]);
      throw localIOException;
    }
  }
  
  private XmlSchemaGenerator<Type, Class, Field, Method> createSchemaGenerator()
  {
    RuntimeTypeInfoSet localRuntimeTypeInfoSet;
    try
    {
      localRuntimeTypeInfoSet = getTypeInfoSet();
    }
    catch (IllegalAnnotationsException localIllegalAnnotationsException)
    {
      throw new AssertionError(localIllegalAnnotationsException);
    }
    XmlSchemaGenerator localXmlSchemaGenerator = new XmlSchemaGenerator(localRuntimeTypeInfoSet.getNavigator(), localRuntimeTypeInfoSet);
    HashSet localHashSet = new HashSet();
    Iterator localIterator = localRuntimeTypeInfoSet.getAllElements().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (RuntimeElementInfo)localIterator.next();
      localHashSet.add(((RuntimeElementInfo)localObject).getElementName());
    }
    localIterator = localRuntimeTypeInfoSet.beans().values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (RuntimeClassInfo)localIterator.next();
      if (((RuntimeClassInfo)localObject).isElement()) {
        localHashSet.add(((RuntimeClassInfo)localObject).asElement().getElementName());
      }
    }
    localIterator = bridges.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject = (TypeReference)localIterator.next();
      if (!localHashSet.contains(tagName)) {
        if ((type == Void.TYPE) || (type == Void.class))
        {
          localXmlSchemaGenerator.add(tagName, false, null);
        }
        else if (type != CompositeStructure.class)
        {
          NonElement localNonElement = getXmlType(localRuntimeTypeInfoSet, (TypeReference)localObject);
          localXmlSchemaGenerator.add(tagName, !localRuntimeTypeInfoSet.getNavigator().isPrimitive(type), localNonElement);
        }
      }
    }
    return localXmlSchemaGenerator;
  }
  
  public QName getTypeName(TypeReference paramTypeReference)
  {
    try
    {
      NonElement localNonElement = getXmlType(getTypeInfoSet(), paramTypeReference);
      if (localNonElement == null) {
        throw new IllegalArgumentException();
      }
      return localNonElement.getTypeName();
    }
    catch (IllegalAnnotationsException localIllegalAnnotationsException)
    {
      throw new AssertionError(localIllegalAnnotationsException);
    }
  }
  
  public <T> Binder<T> createBinder(Class<T> paramClass)
  {
    if (paramClass == Node.class) {
      return createBinder();
    }
    return super.createBinder(paramClass);
  }
  
  public Binder<Node> createBinder()
  {
    return new BinderImpl(this, new DOMScanner());
  }
  
  public QName getElementName(Object paramObject)
    throws JAXBException
  {
    JaxBeanInfo localJaxBeanInfo = getBeanInfo(paramObject, true);
    if (!localJaxBeanInfo.isElement()) {
      return null;
    }
    return new QName(localJaxBeanInfo.getElementNamespaceURI(paramObject), localJaxBeanInfo.getElementLocalName(paramObject));
  }
  
  public QName getElementName(Class paramClass)
    throws JAXBException
  {
    JaxBeanInfo localJaxBeanInfo = getBeanInfo(paramClass, true);
    if (!localJaxBeanInfo.isElement()) {
      return null;
    }
    return new QName(localJaxBeanInfo.getElementNamespaceURI(paramClass), localJaxBeanInfo.getElementLocalName(paramClass));
  }
  
  public Bridge createBridge(TypeReference paramTypeReference)
  {
    return (Bridge)bridges.get(paramTypeReference);
  }
  
  @NotNull
  public BridgeContext createBridgeContext()
  {
    return new BridgeContextImpl(this);
  }
  
  public RawAccessor getElementPropertyAccessor(Class paramClass, String paramString1, String paramString2)
    throws JAXBException
  {
    JaxBeanInfo localJaxBeanInfo = getBeanInfo(paramClass, true);
    if (!(localJaxBeanInfo instanceof ClassBeanInfoImpl)) {
      throw new JAXBException(paramClass + " is not a bean");
    }
    for (ClassBeanInfoImpl localClassBeanInfoImpl = (ClassBeanInfoImpl)localJaxBeanInfo; localClassBeanInfoImpl != null; localClassBeanInfoImpl = superClazz) {
      for (Property localProperty : properties)
      {
        final Accessor localAccessor = localProperty.getElementPropertyAccessor(paramString1, paramString2);
        if (localAccessor != null) {
          new RawAccessor()
          {
            public Object get(Object paramAnonymousObject)
              throws AccessorException
            {
              return localAccessor.getUnadapted(paramAnonymousObject);
            }
            
            public void set(Object paramAnonymousObject1, Object paramAnonymousObject2)
              throws AccessorException
            {
              localAccessor.setUnadapted(paramAnonymousObject1, paramAnonymousObject2);
            }
          };
        }
      }
    }
    throw new JAXBException(new QName(paramString1, paramString2) + " is not a valid property on " + paramClass);
  }
  
  public List<String> getKnownNamespaceURIs()
  {
    return Arrays.asList(nameList.namespaceURIs);
  }
  
  public String getBuildId()
  {
    Package localPackage = getClass().getPackage();
    if (localPackage == null) {
      return null;
    }
    return localPackage.getImplementationVersion();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(Which.which(getClass()) + " Build-Id: " + getBuildId());
    localStringBuilder.append("\nClasses known to this context:\n");
    TreeSet localTreeSet = new TreeSet();
    Iterator localIterator = beanInfoMap.keySet().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Class)localIterator.next();
      localTreeSet.add(((Class)localObject).getName());
    }
    localIterator = localTreeSet.iterator();
    while (localIterator.hasNext())
    {
      localObject = (String)localIterator.next();
      localStringBuilder.append("  ").append((String)localObject).append('\n');
    }
    return localStringBuilder.toString();
  }
  
  public String getXMIMEContentType(Object paramObject)
  {
    JaxBeanInfo localJaxBeanInfo = getBeanInfo(paramObject);
    if (!(localJaxBeanInfo instanceof ClassBeanInfoImpl)) {
      return null;
    }
    ClassBeanInfoImpl localClassBeanInfoImpl = (ClassBeanInfoImpl)localJaxBeanInfo;
    for (Property localProperty : properties) {
      if ((localProperty instanceof AttributeProperty))
      {
        AttributeProperty localAttributeProperty = (AttributeProperty)localProperty;
        if (attName.equals("http://www.w3.org/2005/05/xmlmime", "contentType")) {
          try
          {
            return (String)xacc.print(paramObject);
          }
          catch (AccessorException localAccessorException)
          {
            return null;
          }
          catch (SAXException localSAXException)
          {
            return null;
          }
          catch (ClassCastException localClassCastException)
          {
            return null;
          }
        }
      }
    }
    return null;
  }
  
  public JAXBContextImpl createAugmented(Class<?> paramClass)
    throws JAXBException
  {
    Class[] arrayOfClass = new Class[classes.length + 1];
    System.arraycopy(classes, 0, arrayOfClass, 0, classes.length);
    arrayOfClass[classes.length] = paramClass;
    JAXBContextBuilder localJAXBContextBuilder = new JAXBContextBuilder(this);
    localJAXBContextBuilder.setClasses(arrayOfClass);
    return localJAXBContextBuilder.build();
  }
  
  public static class JAXBContextBuilder
  {
    private boolean retainPropertyInfo = false;
    private boolean supressAccessorWarnings = false;
    private String defaultNsUri = "";
    @NotNull
    private RuntimeAnnotationReader annotationReader = new RuntimeInlineAnnotationReader();
    @NotNull
    private Map<Class, Class> subclassReplacements = Collections.emptyMap();
    private boolean c14nSupport = false;
    private Class[] classes;
    private Collection<TypeReference> typeRefs;
    private boolean xmlAccessorFactorySupport = false;
    private boolean allNillable;
    private boolean improvedXsiTypeHandling = true;
    private boolean disableSecurityProcessing = true;
    
    public JAXBContextBuilder() {}
    
    public JAXBContextBuilder(JAXBContextImpl paramJAXBContextImpl)
    {
      supressAccessorWarnings = supressAccessorWarnings;
      retainPropertyInfo = retainPropertyInfo;
      defaultNsUri = defaultNsUri;
      annotationReader = annotationReader;
      subclassReplacements = subclassReplacements;
      c14nSupport = c14nSupport;
      classes = classes;
      typeRefs = bridges.keySet();
      xmlAccessorFactorySupport = xmlAccessorFactorySupport;
      allNillable = allNillable;
      disableSecurityProcessing = disableSecurityProcessing;
    }
    
    public JAXBContextBuilder setRetainPropertyInfo(boolean paramBoolean)
    {
      retainPropertyInfo = paramBoolean;
      return this;
    }
    
    public JAXBContextBuilder setSupressAccessorWarnings(boolean paramBoolean)
    {
      supressAccessorWarnings = paramBoolean;
      return this;
    }
    
    public JAXBContextBuilder setC14NSupport(boolean paramBoolean)
    {
      c14nSupport = paramBoolean;
      return this;
    }
    
    public JAXBContextBuilder setXmlAccessorFactorySupport(boolean paramBoolean)
    {
      xmlAccessorFactorySupport = paramBoolean;
      return this;
    }
    
    public JAXBContextBuilder setDefaultNsUri(String paramString)
    {
      defaultNsUri = paramString;
      return this;
    }
    
    public JAXBContextBuilder setAllNillable(boolean paramBoolean)
    {
      allNillable = paramBoolean;
      return this;
    }
    
    public JAXBContextBuilder setClasses(Class[] paramArrayOfClass)
    {
      classes = paramArrayOfClass;
      return this;
    }
    
    public JAXBContextBuilder setAnnotationReader(RuntimeAnnotationReader paramRuntimeAnnotationReader)
    {
      annotationReader = paramRuntimeAnnotationReader;
      return this;
    }
    
    public JAXBContextBuilder setSubclassReplacements(Map<Class, Class> paramMap)
    {
      subclassReplacements = paramMap;
      return this;
    }
    
    public JAXBContextBuilder setTypeRefs(Collection<TypeReference> paramCollection)
    {
      typeRefs = paramCollection;
      return this;
    }
    
    public JAXBContextBuilder setImprovedXsiTypeHandling(boolean paramBoolean)
    {
      improvedXsiTypeHandling = paramBoolean;
      return this;
    }
    
    public JAXBContextBuilder setDisableSecurityProcessing(boolean paramBoolean)
    {
      disableSecurityProcessing = paramBoolean;
      return this;
    }
    
    public JAXBContextImpl build()
      throws JAXBException
    {
      if (defaultNsUri == null) {
        defaultNsUri = "";
      }
      if (subclassReplacements == null) {
        subclassReplacements = Collections.emptyMap();
      }
      if (annotationReader == null) {
        annotationReader = new RuntimeInlineAnnotationReader();
      }
      if (typeRefs == null) {
        typeRefs = Collections.emptyList();
      }
      return new JAXBContextImpl(this, null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\JAXBContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */