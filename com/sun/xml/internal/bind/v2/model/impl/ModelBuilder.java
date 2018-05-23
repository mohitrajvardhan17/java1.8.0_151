package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.ClassLocatable;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.RegistryInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

public class ModelBuilder<T, C, F, M>
  implements ModelBuilderI<T, C, F, M>
{
  private static final Logger logger = Logger.getLogger(ModelBuilder.class.getName());
  final TypeInfoSetImpl<T, C, F, M> typeInfoSet;
  public final AnnotationReader<T, C, F, M> reader;
  public final Navigator<T, C, F, M> nav;
  private final Map<QName, TypeInfo> typeNames = new HashMap();
  public final String defaultNsUri;
  final Map<String, RegistryInfoImpl<T, C, F, M>> registries = new HashMap();
  private final Map<C, C> subclassReplacements;
  private ErrorHandler errorHandler;
  private boolean hadError;
  public boolean hasSwaRef;
  private final ErrorHandler proxyErrorHandler = new ErrorHandler()
  {
    public void error(IllegalAnnotationException paramAnonymousIllegalAnnotationException)
    {
      reportError(paramAnonymousIllegalAnnotationException);
    }
  };
  private boolean linked;
  
  public ModelBuilder(AnnotationReader<T, C, F, M> paramAnnotationReader, Navigator<T, C, F, M> paramNavigator, Map<C, C> paramMap, String paramString)
  {
    reader = paramAnnotationReader;
    nav = paramNavigator;
    subclassReplacements = paramMap;
    if (paramString == null) {
      paramString = "";
    }
    defaultNsUri = paramString;
    paramAnnotationReader.setErrorHandler(proxyErrorHandler);
    typeInfoSet = createTypeInfoSet();
  }
  
  protected TypeInfoSetImpl<T, C, F, M> createTypeInfoSet()
  {
    return new TypeInfoSetImpl(nav, reader, BuiltinLeafInfoImpl.createLeaves(nav));
  }
  
  public NonElement<T, C> getClassInfo(C paramC, Locatable paramLocatable)
  {
    return getClassInfo(paramC, false, paramLocatable);
  }
  
  public NonElement<T, C> getClassInfo(C paramC, boolean paramBoolean, Locatable paramLocatable)
  {
    assert (paramC != null);
    Object localObject1 = typeInfoSet.getClassInfo(paramC);
    if (localObject1 != null) {
      return (NonElement<T, C>)localObject1;
    }
    Object localObject2;
    Object localObject3;
    if (nav.isEnum(paramC))
    {
      EnumLeafInfoImpl localEnumLeafInfoImpl = createEnumLeafInfo(paramC, paramLocatable);
      typeInfoSet.add(localEnumLeafInfoImpl);
      localObject1 = localEnumLeafInfoImpl;
      addTypeName((NonElement)localObject1);
    }
    else
    {
      boolean bool = subclassReplacements.containsKey(paramC);
      if ((bool) && (!paramBoolean))
      {
        localObject1 = getClassInfo(subclassReplacements.get(paramC), paramLocatable);
      }
      else if ((reader.hasClassAnnotation(paramC, XmlTransient.class)) || (bool))
      {
        localObject1 = getClassInfo(nav.getSuperClass(paramC), paramBoolean, new ClassLocatable(paramLocatable, paramC, nav));
      }
      else
      {
        localObject2 = createClassInfo(paramC, paramLocatable);
        typeInfoSet.add((ClassInfoImpl)localObject2);
        Iterator localIterator = ((ClassInfoImpl)localObject2).getProperties().iterator();
        while (localIterator.hasNext())
        {
          PropertyInfo localPropertyInfo = (PropertyInfo)localIterator.next();
          if (localPropertyInfo.kind() == PropertyKind.REFERENCE)
          {
            addToRegistry(paramC, (Locatable)localPropertyInfo);
            localObject3 = getParametrizedTypes(localPropertyInfo);
            if (localObject3 != null) {
              for (Object localObject5 : localObject3) {
                if (localObject5 != paramC) {
                  addToRegistry(localObject5, (Locatable)localPropertyInfo);
                }
              }
            }
          }
          localObject3 = localPropertyInfo.ref().iterator();
          while (((Iterator)localObject3).hasNext()) {
            ??? = (TypeInfo)((Iterator)localObject3).next();
          }
        }
        ((ClassInfoImpl)localObject2).getBaseClass();
        localObject1 = localObject2;
        addTypeName((NonElement)localObject1);
      }
    }
    XmlSeeAlso localXmlSeeAlso = (XmlSeeAlso)reader.getClassAnnotation(XmlSeeAlso.class, paramC, paramLocatable);
    if (localXmlSeeAlso != null) {
      for (localObject3 : reader.getClassArrayValue(localXmlSeeAlso, "value")) {
        getTypeInfo(localObject3, (Locatable)localXmlSeeAlso);
      }
    }
    return (NonElement<T, C>)localObject1;
  }
  
  private void addToRegistry(C paramC, Locatable paramLocatable)
  {
    String str = nav.getPackageName(paramC);
    if (!registries.containsKey(str))
    {
      Object localObject = nav.loadObjectFactory(paramC, str);
      if (localObject != null) {
        addRegistry(localObject, paramLocatable);
      }
    }
  }
  
  private Class[] getParametrizedTypes(PropertyInfo paramPropertyInfo)
  {
    try
    {
      Type localType = ((RuntimePropertyInfo)paramPropertyInfo).getIndividualType();
      if ((localType instanceof ParameterizedType))
      {
        ParameterizedType localParameterizedType = (ParameterizedType)localType;
        if (localParameterizedType.getRawType() == JAXBElement.class)
        {
          Type[] arrayOfType = localParameterizedType.getActualTypeArguments();
          Class[] arrayOfClass = new Class[arrayOfType.length];
          for (int i = 0; i < arrayOfType.length; i++) {
            arrayOfClass[i] = ((Class)arrayOfType[i]);
          }
          return arrayOfClass;
        }
      }
    }
    catch (Exception localException)
    {
      logger.log(Level.FINE, "Error in ModelBuilder.getParametrizedTypes. " + localException.getMessage());
    }
    return null;
  }
  
  private void addTypeName(NonElement<T, C> paramNonElement)
  {
    QName localQName = paramNonElement.getTypeName();
    if (localQName == null) {
      return;
    }
    TypeInfo localTypeInfo = (TypeInfo)typeNames.put(localQName, paramNonElement);
    if (localTypeInfo != null) {
      reportError(new IllegalAnnotationException(Messages.CONFLICTING_XML_TYPE_MAPPING.format(new Object[] { paramNonElement.getTypeName() }), localTypeInfo, paramNonElement));
    }
  }
  
  public NonElement<T, C> getTypeInfo(T paramT, Locatable paramLocatable)
  {
    NonElement localNonElement = typeInfoSet.getTypeInfo(paramT);
    if (localNonElement != null) {
      return localNonElement;
    }
    if (nav.isArray(paramT))
    {
      localObject = createArrayInfo(paramLocatable, paramT);
      addTypeName((NonElement)localObject);
      typeInfoSet.add((ArrayInfoImpl)localObject);
      return (NonElement<T, C>)localObject;
    }
    Object localObject = nav.asDecl(paramT);
    assert (localObject != null) : (paramT.toString() + " must be a leaf, but we failed to recognize it.");
    return getClassInfo(localObject, paramLocatable);
  }
  
  public NonElement<T, C> getTypeInfo(Ref<T, C> paramRef)
  {
    assert (!valueList);
    Object localObject = nav.asDecl(type);
    if ((localObject != null) && (reader.getClassAnnotation(XmlRegistry.class, localObject, null) != null))
    {
      if (!registries.containsKey(nav.getPackageName(localObject))) {
        addRegistry(localObject, null);
      }
      return null;
    }
    return getTypeInfo(type, null);
  }
  
  protected EnumLeafInfoImpl<T, C, F, M> createEnumLeafInfo(C paramC, Locatable paramLocatable)
  {
    return new EnumLeafInfoImpl(this, paramLocatable, paramC, nav.use(paramC));
  }
  
  protected ClassInfoImpl<T, C, F, M> createClassInfo(C paramC, Locatable paramLocatable)
  {
    return new ClassInfoImpl(this, paramLocatable, paramC);
  }
  
  protected ElementInfoImpl<T, C, F, M> createElementInfo(RegistryInfoImpl<T, C, F, M> paramRegistryInfoImpl, M paramM)
    throws IllegalAnnotationException
  {
    return new ElementInfoImpl(this, paramRegistryInfoImpl, paramM);
  }
  
  protected ArrayInfoImpl<T, C, F, M> createArrayInfo(Locatable paramLocatable, T paramT)
  {
    return new ArrayInfoImpl(this, paramLocatable, paramT);
  }
  
  public RegistryInfo<T, C> addRegistry(C paramC, Locatable paramLocatable)
  {
    return new RegistryInfoImpl(this, paramLocatable, paramC);
  }
  
  public RegistryInfo<T, C> getRegistry(String paramString)
  {
    return (RegistryInfo)registries.get(paramString);
  }
  
  public TypeInfoSet<T, C, F, M> link()
  {
    assert (!linked);
    linked = true;
    Iterator localIterator = typeInfoSet.getAllElements().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (ElementInfoImpl)localIterator.next();
      ((ElementInfoImpl)localObject).link();
    }
    localIterator = typeInfoSet.beans().values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (ClassInfoImpl)localIterator.next();
      ((ClassInfoImpl)localObject).link();
    }
    localIterator = typeInfoSet.enums().values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (EnumLeafInfoImpl)localIterator.next();
      ((EnumLeafInfoImpl)localObject).link();
    }
    if (hadError) {
      return null;
    }
    return typeInfoSet;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    errorHandler = paramErrorHandler;
  }
  
  public final void reportError(IllegalAnnotationException paramIllegalAnnotationException)
  {
    hadError = true;
    if (errorHandler != null) {
      errorHandler.error(paramIllegalAnnotationException);
    }
  }
  
  public boolean isReplaced(C paramC)
  {
    return subclassReplacements.containsKey(paramC);
  }
  
  public Navigator<T, C, F, M> getNavigator()
  {
    return nav;
  }
  
  public AnnotationReader<T, C, F, M> getReader()
  {
    return reader;
  }
  
  static
  {
    try
    {
      Object localObject = null;
      ((XmlSchema)localObject).location();
    }
    catch (NullPointerException localNullPointerException) {}catch (NoSuchMethodError localNoSuchMethodError1)
    {
      Messages localMessages;
      if (SecureLoader.getClassClassLoader(XmlSchema.class) == null) {
        localMessages = Messages.INCOMPATIBLE_API_VERSION_MUSTANG;
      } else {
        localMessages = Messages.INCOMPATIBLE_API_VERSION;
      }
      throw new LinkageError(localMessages.format(new Object[] { Which.which(XmlSchema.class), Which.which(ModelBuilder.class) }));
    }
    try
    {
      WhiteSpaceProcessor.isWhiteSpace("xyz");
    }
    catch (NoSuchMethodError localNoSuchMethodError2)
    {
      throw new LinkageError(Messages.RUNNING_WITH_1_0_RUNTIME.format(new Object[] { Which.which(WhiteSpaceProcessor.class), Which.which(ModelBuilder.class) }));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ModelBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */