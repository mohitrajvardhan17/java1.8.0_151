package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlType.DEFAULT;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

public class ClassInfoImpl<T, C, F, M>
  extends TypeInfoImpl<T, C, F, M>
  implements ClassInfo<T, C>, Element<T, C>
{
  protected final C clazz;
  private final QName elementName;
  private final QName typeName;
  private FinalArrayList<PropertyInfoImpl<T, C, F, M>> properties;
  private String[] propOrder;
  private ClassInfoImpl<T, C, F, M> baseClass;
  private boolean baseClassComputed = false;
  private boolean hasSubClasses = false;
  protected PropertySeed<T, C, F, M> attributeWildcard;
  private M factoryMethod = null;
  private static final SecondaryAnnotation[] SECONDARY_ANNOTATIONS;
  private static final Annotation[] EMPTY_ANNOTATIONS;
  private static final HashMap<Class, Integer> ANNOTATION_NUMBER_MAP;
  private static final String[] DEFAULT_ORDER = new String[0];
  
  ClassInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, Locatable paramLocatable, C paramC)
  {
    super(paramModelBuilder, paramLocatable);
    clazz = paramC;
    assert (paramC != null);
    elementName = parseElementName(paramC);
    XmlType localXmlType = (XmlType)reader().getClassAnnotation(XmlType.class, paramC, this);
    typeName = parseTypeName(paramC, localXmlType);
    if (localXmlType != null)
    {
      localObject = localXmlType.propOrder();
      if (localObject.length == 0) {
        propOrder = null;
      } else if (localObject[0].length() == 0) {
        propOrder = DEFAULT_ORDER;
      } else {
        propOrder = ((String[])localObject);
      }
    }
    else
    {
      propOrder = DEFAULT_ORDER;
    }
    Object localObject = (XmlAccessorOrder)reader().getPackageAnnotation(XmlAccessorOrder.class, paramC, this);
    if ((localObject != null) && (((XmlAccessorOrder)localObject).value() == XmlAccessOrder.UNDEFINED)) {
      propOrder = null;
    }
    localObject = (XmlAccessorOrder)reader().getClassAnnotation(XmlAccessorOrder.class, paramC, this);
    if ((localObject != null) && (((XmlAccessorOrder)localObject).value() == XmlAccessOrder.UNDEFINED)) {
      propOrder = null;
    }
    if (nav().isInterface(paramC)) {
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INTERFACE.format(new Object[] { nav().getClassName(paramC) }), this));
    }
    if ((!hasFactoryConstructor(localXmlType)) && (!nav().hasDefaultConstructor(paramC))) {
      if (nav().isInnerClass(paramC)) {
        paramModelBuilder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INNER_CLASS.format(new Object[] { nav().getClassName(paramC) }), this));
      } else if (elementName != null) {
        paramModelBuilder.reportError(new IllegalAnnotationException(Messages.NO_DEFAULT_CONSTRUCTOR.format(new Object[] { nav().getClassName(paramC) }), this));
      }
    }
  }
  
  public ClassInfoImpl<T, C, F, M> getBaseClass()
  {
    if (!baseClassComputed)
    {
      Object localObject = nav().getSuperClass(clazz);
      if ((localObject == null) || (localObject == nav().asDecl(Object.class)))
      {
        baseClass = null;
      }
      else
      {
        NonElement localNonElement = builder.getClassInfo(localObject, true, this);
        if ((localNonElement instanceof ClassInfoImpl))
        {
          baseClass = ((ClassInfoImpl)localNonElement);
          baseClass.hasSubClasses = true;
        }
        else
        {
          baseClass = null;
        }
      }
      baseClassComputed = true;
    }
    return baseClass;
  }
  
  public final Element<T, C> getSubstitutionHead()
  {
    for (ClassInfoImpl localClassInfoImpl = getBaseClass(); (localClassInfoImpl != null) && (!localClassInfoImpl.isElement()); localClassInfoImpl = localClassInfoImpl.getBaseClass()) {}
    return localClassInfoImpl;
  }
  
  public final C getClazz()
  {
    return (C)clazz;
  }
  
  /**
   * @deprecated
   */
  public ClassInfoImpl<T, C, F, M> getScope()
  {
    return null;
  }
  
  public final T getType()
  {
    return (T)nav().use(clazz);
  }
  
  public boolean canBeReferencedByIDREF()
  {
    Object localObject = getProperties().iterator();
    while (((Iterator)localObject).hasNext())
    {
      PropertyInfo localPropertyInfo = (PropertyInfo)((Iterator)localObject).next();
      if (localPropertyInfo.id() == ID.ID) {
        return true;
      }
    }
    localObject = getBaseClass();
    if (localObject != null) {
      return ((ClassInfoImpl)localObject).canBeReferencedByIDREF();
    }
    return false;
  }
  
  public final String getName()
  {
    return nav().getClassName(clazz);
  }
  
  public <A extends Annotation> A readAnnotation(Class<A> paramClass)
  {
    return reader().getClassAnnotation(paramClass, clazz, this);
  }
  
  public Element<T, C> asElement()
  {
    if (isElement()) {
      return this;
    }
    return null;
  }
  
  public List<? extends PropertyInfo<T, C>> getProperties()
  {
    if (properties != null) {
      return properties;
    }
    XmlAccessType localXmlAccessType = getAccessType();
    properties = new FinalArrayList();
    findFieldProperties(clazz, localXmlAccessType);
    findGetterSetterProperties(localXmlAccessType);
    if ((propOrder == DEFAULT_ORDER) || (propOrder == null))
    {
      localObject1 = getAccessorOrder();
      if (localObject1 == XmlAccessOrder.ALPHABETICAL) {
        Collections.sort(properties);
      }
    }
    else
    {
      localObject1 = new PropertySorter();
      localObject2 = properties.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (PropertyInfoImpl)((Iterator)localObject2).next();
        ((PropertySorter)localObject1).checkedGet((PropertyInfoImpl)localObject3);
      }
      Collections.sort(properties, (Comparator)localObject1);
      ((PropertySorter)localObject1).checkUnusedProperties();
    }
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = properties.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      PropertyInfoImpl localPropertyInfoImpl = (PropertyInfoImpl)((Iterator)localObject3).next();
      switch (localPropertyInfoImpl.kind())
      {
      case ELEMENT: 
      case REFERENCE: 
      case MAP: 
        localObject2 = localPropertyInfoImpl;
        break;
      case VALUE: 
        if (localObject1 != null) {
          builder.reportError(new IllegalAnnotationException(Messages.MULTIPLE_VALUE_PROPERTY.format(new Object[0]), (Locatable)localObject1, localPropertyInfoImpl));
        }
        if (getBaseClass() != null) {
          builder.reportError(new IllegalAnnotationException(Messages.XMLVALUE_IN_DERIVED_TYPE.format(new Object[0]), localPropertyInfoImpl));
        }
        localObject1 = localPropertyInfoImpl;
        break;
      case ATTRIBUTE: 
        break;
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
    }
    if ((localObject2 != null) && (localObject1 != null)) {
      builder.reportError(new IllegalAnnotationException(Messages.ELEMENT_AND_VALUE_PROPERTY.format(new Object[0]), (Locatable)localObject1, (Locatable)localObject2));
    }
    return properties;
  }
  
  private void findFieldProperties(C paramC, XmlAccessType paramXmlAccessType)
  {
    Object localObject1 = nav().getSuperClass(paramC);
    if (shouldRecurseSuperClass(localObject1)) {
      findFieldProperties(localObject1, paramXmlAccessType);
    }
    Iterator localIterator = nav().getDeclaredFields(paramC).iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = localIterator.next();
      Annotation[] arrayOfAnnotation = reader().getAllFieldAnnotations(localObject2, this);
      boolean bool = reader().hasFieldAnnotation(OverrideAnnotationOf.class, localObject2);
      if (nav().isTransient(localObject2))
      {
        if (hasJAXBAnnotation(arrayOfAnnotation)) {
          builder.reportError(new IllegalAnnotationException(Messages.TRANSIENT_FIELD_NOT_BINDABLE.format(new Object[] { nav().getFieldName(localObject2) }), getSomeJAXBAnnotation(arrayOfAnnotation)));
        }
      }
      else if (nav().isStaticField(localObject2))
      {
        if (hasJAXBAnnotation(arrayOfAnnotation)) {
          addProperty(createFieldSeed(localObject2), arrayOfAnnotation, false);
        }
      }
      else
      {
        if ((paramXmlAccessType == XmlAccessType.FIELD) || ((paramXmlAccessType == XmlAccessType.PUBLIC_MEMBER) && (nav().isPublicField(localObject2))) || (hasJAXBAnnotation(arrayOfAnnotation))) {
          if (bool)
          {
            for (Object localObject3 = getBaseClass(); (localObject3 != null) && (((ClassInfo)localObject3).getProperty("content") == null); localObject3 = ((ClassInfo)localObject3).getBaseClass()) {}
            DummyPropertyInfo localDummyPropertyInfo = (DummyPropertyInfo)((ClassInfo)localObject3).getProperty("content");
            PropertySeed localPropertySeed = createFieldSeed(localObject2);
            localDummyPropertyInfo.addType(createReferenceProperty(localPropertySeed));
          }
          else
          {
            addProperty(createFieldSeed(localObject2), arrayOfAnnotation, false);
          }
        }
        checkFieldXmlLocation(localObject2);
      }
    }
  }
  
  public final boolean hasValueProperty()
  {
    ClassInfoImpl localClassInfoImpl = getBaseClass();
    if ((localClassInfoImpl != null) && (localClassInfoImpl.hasValueProperty())) {
      return true;
    }
    Iterator localIterator = getProperties().iterator();
    while (localIterator.hasNext())
    {
      PropertyInfo localPropertyInfo = (PropertyInfo)localIterator.next();
      if ((localPropertyInfo instanceof ValuePropertyInfo)) {
        return true;
      }
    }
    return false;
  }
  
  public PropertyInfo<T, C> getProperty(String paramString)
  {
    Iterator localIterator = getProperties().iterator();
    while (localIterator.hasNext())
    {
      PropertyInfo localPropertyInfo = (PropertyInfo)localIterator.next();
      if (localPropertyInfo.getName().equals(paramString)) {
        return localPropertyInfo;
      }
    }
    return null;
  }
  
  protected void checkFieldXmlLocation(F paramF) {}
  
  private <T extends Annotation> T getClassOrPackageAnnotation(Class<T> paramClass)
  {
    Annotation localAnnotation = reader().getClassAnnotation(paramClass, clazz, this);
    if (localAnnotation != null) {
      return localAnnotation;
    }
    return reader().getPackageAnnotation(paramClass, clazz, this);
  }
  
  private XmlAccessType getAccessType()
  {
    XmlAccessorType localXmlAccessorType = (XmlAccessorType)getClassOrPackageAnnotation(XmlAccessorType.class);
    if (localXmlAccessorType != null) {
      return localXmlAccessorType.value();
    }
    return XmlAccessType.PUBLIC_MEMBER;
  }
  
  private XmlAccessOrder getAccessorOrder()
  {
    XmlAccessorOrder localXmlAccessorOrder = (XmlAccessorOrder)getClassOrPackageAnnotation(XmlAccessorOrder.class);
    if (localXmlAccessorOrder != null) {
      return localXmlAccessorOrder.value();
    }
    return XmlAccessOrder.UNDEFINED;
  }
  
  public boolean hasProperties()
  {
    return !properties.isEmpty();
  }
  
  private static <T> T pickOne(T... paramVarArgs)
  {
    for (T ? : paramVarArgs) {
      if (? != null) {
        return ?;
      }
    }
    return null;
  }
  
  private static <T> List<T> makeSet(T... paramVarArgs)
  {
    FinalArrayList localFinalArrayList = new FinalArrayList();
    for (T ? : paramVarArgs) {
      if (? != null) {
        localFinalArrayList.add(?);
      }
    }
    return localFinalArrayList;
  }
  
  private void checkConflict(Annotation paramAnnotation1, Annotation paramAnnotation2)
    throws ClassInfoImpl.DuplicateException
  {
    assert (paramAnnotation2 != null);
    if (paramAnnotation1 != null) {
      throw new DuplicateException(paramAnnotation1, paramAnnotation2);
    }
  }
  
  private void addProperty(PropertySeed<T, C, F, M> paramPropertySeed, Annotation[] paramArrayOfAnnotation, boolean paramBoolean)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject5 = null;
    Object localObject6 = null;
    Object localObject7 = null;
    Object localObject8 = null;
    Object localObject9 = null;
    Object localObject10 = null;
    Object localObject11 = null;
    int i = 0;
    try
    {
      for (Annotation localAnnotation1 : paramArrayOfAnnotation)
      {
        Integer localInteger = (Integer)ANNOTATION_NUMBER_MAP.get(localAnnotation1.annotationType());
        if (localInteger != null) {
          switch (localInteger.intValue())
          {
          case 0: 
            checkConflict((Annotation)localObject1, localAnnotation1);
            localObject1 = (XmlTransient)localAnnotation1;
            break;
          case 1: 
            checkConflict((Annotation)localObject2, localAnnotation1);
            localObject2 = (XmlAnyAttribute)localAnnotation1;
            break;
          case 2: 
            checkConflict((Annotation)localObject3, localAnnotation1);
            localObject3 = (XmlAttribute)localAnnotation1;
            break;
          case 3: 
            checkConflict((Annotation)localObject4, localAnnotation1);
            localObject4 = (XmlValue)localAnnotation1;
            break;
          case 4: 
            checkConflict((Annotation)localObject5, localAnnotation1);
            localObject5 = (XmlElement)localAnnotation1;
            break;
          case 5: 
            checkConflict((Annotation)localObject6, localAnnotation1);
            localObject6 = (XmlElements)localAnnotation1;
            break;
          case 6: 
            checkConflict((Annotation)localObject7, localAnnotation1);
            localObject7 = (XmlElementRef)localAnnotation1;
            break;
          case 7: 
            checkConflict((Annotation)localObject8, localAnnotation1);
            localObject8 = (XmlElementRefs)localAnnotation1;
            break;
          case 8: 
            checkConflict((Annotation)localObject9, localAnnotation1);
            localObject9 = (XmlAnyElement)localAnnotation1;
            break;
          case 9: 
            checkConflict((Annotation)localObject10, localAnnotation1);
            localObject10 = (XmlMixed)localAnnotation1;
            break;
          case 10: 
            checkConflict((Annotation)localObject11, localAnnotation1);
            localObject11 = (OverrideAnnotationOf)localAnnotation1;
            break;
          default: 
            i |= 1 << localInteger.intValue() - 20;
          }
        }
      }
      ??? = null;
      ??? = 0;
      if (localObject1 != null)
      {
        ??? = PropertyGroup.TRANSIENT;
        ???++;
      }
      if (localObject2 != null)
      {
        ??? = PropertyGroup.ANY_ATTRIBUTE;
        ???++;
      }
      if (localObject3 != null)
      {
        ??? = PropertyGroup.ATTRIBUTE;
        ???++;
      }
      if (localObject4 != null)
      {
        ??? = PropertyGroup.VALUE;
        ???++;
      }
      if ((localObject5 != null) || (localObject6 != null))
      {
        ??? = PropertyGroup.ELEMENT;
        ???++;
      }
      if ((localObject7 != null) || (localObject8 != null) || (localObject9 != null) || (localObject10 != null) || (localObject11 != null))
      {
        ??? = PropertyGroup.ELEMENT_REF;
        ???++;
      }
      Object localObject13;
      if (??? > 1)
      {
        localObject13 = makeSet(new Annotation[] { localObject1, localObject2, localObject3, localObject4, (Annotation)pickOne(new Annotation[] { localObject5, localObject6 }), (Annotation)pickOne(new Annotation[] { localObject7, localObject8, localObject9 }) });
        throw new ConflictException((List)localObject13);
      }
      if (??? == null)
      {
        assert (??? == 0);
        if ((nav().isSubClassOf(paramPropertySeed.getRawType(), nav().ref(Map.class))) && (!paramPropertySeed.hasAnnotation(XmlJavaTypeAdapter.class))) {
          ??? = PropertyGroup.MAP;
        } else {
          ??? = PropertyGroup.ELEMENT;
        }
      }
      else if ((((PropertyGroup)???).equals(PropertyGroup.ELEMENT)) && (nav().isSubClassOf(paramPropertySeed.getRawType(), nav().ref(Map.class))) && (!paramPropertySeed.hasAnnotation(XmlJavaTypeAdapter.class)))
      {
        ??? = PropertyGroup.MAP;
      }
      if ((i & allowedsecondaryAnnotations) != 0)
      {
        for (SecondaryAnnotation localSecondaryAnnotation : SECONDARY_ANNOTATIONS) {
          if (!((PropertyGroup)???).allows(localSecondaryAnnotation)) {
            for (Class localClass : members)
            {
              Annotation localAnnotation2 = paramPropertySeed.readAnnotation(localClass);
              if (localAnnotation2 != null)
              {
                builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_NOT_ALLOWED.format(new Object[] { localClass.getSimpleName() }), localAnnotation2));
                return;
              }
            }
          }
        }
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      switch (???)
      {
      case TRANSIENT: 
        return;
      case ANY_ATTRIBUTE: 
        if (attributeWildcard != null)
        {
          builder.reportError(new IllegalAnnotationException(Messages.TWO_ATTRIBUTE_WILDCARDS.format(new Object[] { nav().getClassName(getClazz()) }), (Annotation)localObject2, attributeWildcard));
          return;
        }
        attributeWildcard = paramPropertySeed;
        if (inheritsAttributeWildcard())
        {
          builder.reportError(new IllegalAnnotationException(Messages.SUPER_CLASS_HAS_WILDCARD.format(new Object[0]), (Annotation)localObject2, getInheritedAttributeWildcard()));
          return;
        }
        if (!nav().isSubClassOf(paramPropertySeed.getRawType(), nav().ref(Map.class)))
        {
          builder.reportError(new IllegalAnnotationException(Messages.INVALID_ATTRIBUTE_WILDCARD_TYPE.format(new Object[] { nav().getTypeName(paramPropertySeed.getRawType()) }), (Annotation)localObject2, getInheritedAttributeWildcard()));
          return;
        }
        return;
      case ATTRIBUTE: 
        properties.add(createAttributeProperty(paramPropertySeed));
        return;
      case VALUE: 
        properties.add(createValueProperty(paramPropertySeed));
        return;
      case ELEMENT: 
        properties.add(createElementProperty(paramPropertySeed));
        return;
      case ELEMENT_REF: 
        properties.add(createReferenceProperty(paramPropertySeed));
        return;
      case MAP: 
        properties.add(createMapProperty(paramPropertySeed));
        return;
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (ConflictException localConflictException)
    {
      List localList = annotations;
      builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(getClazz()) + '#' + paramPropertySeed.getName(), ((Annotation)localList.get(0)).annotationType().getName(), ((Annotation)localList.get(1)).annotationType().getName() }), (Annotation)localList.get(0), (Annotation)localList.get(1)));
    }
    catch (DuplicateException localDuplicateException)
    {
      builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(new Object[] { a1.annotationType().getName() }), a1, a2));
    }
  }
  
  protected ReferencePropertyInfoImpl<T, C, F, M> createReferenceProperty(PropertySeed<T, C, F, M> paramPropertySeed)
  {
    return new ReferencePropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected AttributePropertyInfoImpl<T, C, F, M> createAttributeProperty(PropertySeed<T, C, F, M> paramPropertySeed)
  {
    return new AttributePropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected ValuePropertyInfoImpl<T, C, F, M> createValueProperty(PropertySeed<T, C, F, M> paramPropertySeed)
  {
    return new ValuePropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected ElementPropertyInfoImpl<T, C, F, M> createElementProperty(PropertySeed<T, C, F, M> paramPropertySeed)
  {
    return new ElementPropertyInfoImpl(this, paramPropertySeed);
  }
  
  protected MapPropertyInfoImpl<T, C, F, M> createMapProperty(PropertySeed<T, C, F, M> paramPropertySeed)
  {
    return new MapPropertyInfoImpl(this, paramPropertySeed);
  }
  
  private void findGetterSetterProperties(XmlAccessType paramXmlAccessType)
  {
    LinkedHashMap localLinkedHashMap1 = new LinkedHashMap();
    LinkedHashMap localLinkedHashMap2 = new LinkedHashMap();
    Object localObject1 = clazz;
    do
    {
      collectGetterSetters(clazz, localLinkedHashMap1, localLinkedHashMap2);
      localObject1 = nav().getSuperClass(localObject1);
    } while (shouldRecurseSuperClass(localObject1));
    TreeSet localTreeSet = new TreeSet(localLinkedHashMap1.keySet());
    localTreeSet.retainAll(localLinkedHashMap2.keySet());
    resurrect(localLinkedHashMap1, localTreeSet);
    resurrect(localLinkedHashMap2, localTreeSet);
    Iterator localIterator = localTreeSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject2 = localLinkedHashMap1.get(str);
      Object localObject3 = localLinkedHashMap2.get(str);
      Annotation[] arrayOfAnnotation1 = localObject2 != null ? reader().getAllMethodAnnotations(localObject2, new MethodLocatable(this, localObject2, nav())) : EMPTY_ANNOTATIONS;
      Annotation[] arrayOfAnnotation2 = localObject3 != null ? reader().getAllMethodAnnotations(localObject3, new MethodLocatable(this, localObject3, nav())) : EMPTY_ANNOTATIONS;
      int i = (hasJAXBAnnotation(arrayOfAnnotation1)) || (hasJAXBAnnotation(arrayOfAnnotation2)) ? 1 : 0;
      int j = 0;
      if (i == 0) {
        j = (localObject2 != null) && (nav().isOverriding(localObject2, localObject1)) && (localObject3 != null) && (nav().isOverriding(localObject3, localObject1)) ? 1 : 0;
      }
      if (((paramXmlAccessType == XmlAccessType.PROPERTY) && (j == 0)) || ((paramXmlAccessType == XmlAccessType.PUBLIC_MEMBER) && (isConsideredPublic(localObject2)) && (isConsideredPublic(localObject3)) && (j == 0)) || (i != 0)) {
        if ((localObject2 != null) && (localObject3 != null) && (!nav().isSameType(nav().getReturnType(localObject2), nav().getMethodParameters(localObject3)[0])))
        {
          builder.reportError(new IllegalAnnotationException(Messages.GETTER_SETTER_INCOMPATIBLE_TYPE.format(new Object[] { nav().getTypeName(nav().getReturnType(localObject2)), nav().getTypeName(nav().getMethodParameters(localObject3)[0]) }), new MethodLocatable(this, localObject2, nav()), new MethodLocatable(this, localObject3, nav())));
        }
        else
        {
          Annotation[] arrayOfAnnotation3;
          if (arrayOfAnnotation1.length == 0)
          {
            arrayOfAnnotation3 = arrayOfAnnotation2;
          }
          else if (arrayOfAnnotation2.length == 0)
          {
            arrayOfAnnotation3 = arrayOfAnnotation1;
          }
          else
          {
            arrayOfAnnotation3 = new Annotation[arrayOfAnnotation1.length + arrayOfAnnotation2.length];
            System.arraycopy(arrayOfAnnotation1, 0, arrayOfAnnotation3, 0, arrayOfAnnotation1.length);
            System.arraycopy(arrayOfAnnotation2, 0, arrayOfAnnotation3, arrayOfAnnotation1.length, arrayOfAnnotation2.length);
          }
          addProperty(createAccessorSeed(localObject2, localObject3), arrayOfAnnotation3, false);
        }
      }
    }
    localLinkedHashMap1.keySet().removeAll(localTreeSet);
    localLinkedHashMap2.keySet().removeAll(localTreeSet);
  }
  
  private void collectGetterSetters(C paramC, Map<String, M> paramMap1, Map<String, M> paramMap2)
  {
    Object localObject1 = nav().getSuperClass(paramC);
    if (shouldRecurseSuperClass(localObject1)) {
      collectGetterSetters(localObject1, paramMap1, paramMap2);
    }
    Collection localCollection = nav().getDeclaredMethods(paramC);
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    Iterator localIterator = localCollection.iterator();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    while (localIterator.hasNext())
    {
      localObject2 = localIterator.next();
      int i = 0;
      if (!nav().isBridgeMethod(localObject2))
      {
        localObject3 = nav().getMethodName(localObject2);
        int j = nav().getMethodParameters(localObject2).length;
        if (nav().isStaticMethod(localObject2))
        {
          ensureNoAnnotation(localObject2);
        }
        else
        {
          localObject4 = getPropertyNameFromGetMethod((String)localObject3);
          if ((localObject4 != null) && (j == 0))
          {
            paramMap1.put(localObject4, localObject2);
            i = 1;
          }
          localObject4 = getPropertyNameFromSetMethod((String)localObject3);
          if ((localObject4 != null) && (j == 1))
          {
            localObject5 = (List)localLinkedHashMap.get(localObject4);
            if (null == localObject5)
            {
              localObject5 = new ArrayList();
              localLinkedHashMap.put(localObject4, localObject5);
            }
            ((List)localObject5).add(localObject2);
            i = 1;
          }
          if (i == 0) {
            ensureNoAnnotation(localObject2);
          }
        }
      }
    }
    localIterator = paramMap1.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (Map.Entry)localIterator.next();
      String str = (String)((Map.Entry)localObject2).getKey();
      localObject3 = ((Map.Entry)localObject2).getValue();
      List localList = (List)localLinkedHashMap.remove(str);
      if (null != localList)
      {
        localObject4 = nav().getReturnType(localObject3);
        localObject5 = localList.iterator();
        while (((Iterator)localObject5).hasNext())
        {
          Object localObject6 = ((Iterator)localObject5).next();
          Object localObject7 = nav().getMethodParameters(localObject6)[0];
          if (nav().isSameType(localObject7, localObject4))
          {
            paramMap2.put(str, localObject6);
            break;
          }
        }
      }
    }
    localIterator = localLinkedHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (Map.Entry)localIterator.next();
      paramMap2.put(((Map.Entry)localObject2).getKey(), ((List)((Map.Entry)localObject2).getValue()).get(0));
    }
  }
  
  private boolean shouldRecurseSuperClass(C paramC)
  {
    return (paramC != null) && ((builder.isReplaced(paramC)) || (reader().hasClassAnnotation(paramC, XmlTransient.class)));
  }
  
  private boolean isConsideredPublic(M paramM)
  {
    return (paramM == null) || (nav().isPublicMethod(paramM));
  }
  
  private void resurrect(Map<String, M> paramMap, Set<String> paramSet)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (!paramSet.contains(localEntry.getKey())) {
        if (hasJAXBAnnotation(reader().getAllMethodAnnotations(localEntry.getValue(), this))) {
          paramSet.add(localEntry.getKey());
        }
      }
    }
  }
  
  private void ensureNoAnnotation(M paramM)
  {
    Annotation[] arrayOfAnnotation1 = reader().getAllMethodAnnotations(paramM, this);
    for (Annotation localAnnotation : arrayOfAnnotation1) {
      if (isJAXBAnnotation(localAnnotation))
      {
        builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_ON_WRONG_METHOD.format(new Object[0]), localAnnotation));
        return;
      }
    }
  }
  
  private static boolean isJAXBAnnotation(Annotation paramAnnotation)
  {
    return ANNOTATION_NUMBER_MAP.containsKey(paramAnnotation.annotationType());
  }
  
  private static boolean hasJAXBAnnotation(Annotation[] paramArrayOfAnnotation)
  {
    return getSomeJAXBAnnotation(paramArrayOfAnnotation) != null;
  }
  
  private static Annotation getSomeJAXBAnnotation(Annotation[] paramArrayOfAnnotation)
  {
    for (Annotation localAnnotation : paramArrayOfAnnotation) {
      if (isJAXBAnnotation(localAnnotation)) {
        return localAnnotation;
      }
    }
    return null;
  }
  
  private static String getPropertyNameFromGetMethod(String paramString)
  {
    if ((paramString.startsWith("get")) && (paramString.length() > 3)) {
      return paramString.substring(3);
    }
    if ((paramString.startsWith("is")) && (paramString.length() > 2)) {
      return paramString.substring(2);
    }
    return null;
  }
  
  private static String getPropertyNameFromSetMethod(String paramString)
  {
    if ((paramString.startsWith("set")) && (paramString.length() > 3)) {
      return paramString.substring(3);
    }
    return null;
  }
  
  protected PropertySeed<T, C, F, M> createFieldSeed(F paramF)
  {
    return new FieldPropertySeed(this, paramF);
  }
  
  protected PropertySeed<T, C, F, M> createAccessorSeed(M paramM1, M paramM2)
  {
    return new GetterSetterPropertySeed(this, paramM1, paramM2);
  }
  
  public final boolean isElement()
  {
    return elementName != null;
  }
  
  public boolean isAbstract()
  {
    return nav().isAbstract(clazz);
  }
  
  public boolean isOrdered()
  {
    return propOrder != null;
  }
  
  public final boolean isFinal()
  {
    return nav().isFinal(clazz);
  }
  
  public final boolean hasSubClasses()
  {
    return hasSubClasses;
  }
  
  public final boolean hasAttributeWildcard()
  {
    return (declaresAttributeWildcard()) || (inheritsAttributeWildcard());
  }
  
  public final boolean inheritsAttributeWildcard()
  {
    return getInheritedAttributeWildcard() != null;
  }
  
  public final boolean declaresAttributeWildcard()
  {
    return attributeWildcard != null;
  }
  
  private PropertySeed<T, C, F, M> getInheritedAttributeWildcard()
  {
    for (ClassInfoImpl localClassInfoImpl = getBaseClass(); localClassInfoImpl != null; localClassInfoImpl = localClassInfoImpl.getBaseClass()) {
      if (attributeWildcard != null) {
        return attributeWildcard;
      }
    }
    return null;
  }
  
  public final QName getElementName()
  {
    return elementName;
  }
  
  public final QName getTypeName()
  {
    return typeName;
  }
  
  public final boolean isSimpleType()
  {
    List localList = getProperties();
    if (localList.size() != 1) {
      return false;
    }
    return ((PropertyInfo)localList.get(0)).kind() == PropertyKind.VALUE;
  }
  
  void link()
  {
    getProperties();
    HashMap localHashMap = new HashMap();
    Iterator localIterator = properties.iterator();
    while (localIterator.hasNext())
    {
      PropertyInfoImpl localPropertyInfoImpl1 = (PropertyInfoImpl)localIterator.next();
      localPropertyInfoImpl1.link();
      PropertyInfoImpl localPropertyInfoImpl2 = (PropertyInfoImpl)localHashMap.put(localPropertyInfoImpl1.getName(), localPropertyInfoImpl1);
      if (localPropertyInfoImpl2 != null) {
        builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_COLLISION.format(new Object[] { localPropertyInfoImpl1.getName() }), localPropertyInfoImpl1, localPropertyInfoImpl2));
      }
    }
    super.link();
  }
  
  public Location getLocation()
  {
    return nav().getClassLocation(clazz);
  }
  
  private boolean hasFactoryConstructor(XmlType paramXmlType)
  {
    if (paramXmlType == null) {
      return false;
    }
    String str = paramXmlType.factoryMethod();
    Object localObject1 = reader().getClassValue(paramXmlType, "factoryClass");
    if (str.length() > 0)
    {
      if (nav().isSameType(localObject1, nav().ref(XmlType.DEFAULT.class))) {
        localObject1 = nav().use(clazz);
      }
      Iterator localIterator = nav().getDeclaredMethods(nav().asDecl(localObject1)).iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = localIterator.next();
        if ((nav().getMethodName(localObject2).equals(str)) && (nav().isSameType(nav().getReturnType(localObject2), nav().use(clazz))) && (nav().getMethodParameters(localObject2).length == 0) && (nav().isStaticMethod(localObject2)))
        {
          factoryMethod = localObject2;
          break;
        }
      }
      if (factoryMethod == null) {
        builder.reportError(new IllegalAnnotationException(Messages.NO_FACTORY_METHOD.format(new Object[] { nav().getClassName(nav().asDecl(localObject1)), str }), this));
      }
    }
    else if (!nav().isSameType(localObject1, nav().ref(XmlType.DEFAULT.class)))
    {
      builder.reportError(new IllegalAnnotationException(Messages.FACTORY_CLASS_NEEDS_FACTORY_METHOD.format(new Object[] { nav().getClassName(nav().asDecl(localObject1)) }), this));
    }
    return factoryMethod != null;
  }
  
  public Method getFactoryMethod()
  {
    return (Method)factoryMethod;
  }
  
  public String toString()
  {
    return "ClassInfo(" + clazz + ')';
  }
  
  static
  {
    SECONDARY_ANNOTATIONS = SecondaryAnnotation.values();
    EMPTY_ANNOTATIONS = new Annotation[0];
    ANNOTATION_NUMBER_MAP = new HashMap();
    Class[] arrayOfClass1 = { XmlTransient.class, XmlAnyAttribute.class, XmlAttribute.class, XmlValue.class, XmlElement.class, XmlElements.class, XmlElementRef.class, XmlElementRefs.class, XmlAnyElement.class, XmlMixed.class, OverrideAnnotationOf.class };
    HashMap localHashMap = ANNOTATION_NUMBER_MAP;
    for (Class localClass1 : arrayOfClass1) {
      localHashMap.put(localClass1, Integer.valueOf(localHashMap.size()));
    }
    int i = 20;
    for (SecondaryAnnotation localSecondaryAnnotation : SECONDARY_ANNOTATIONS)
    {
      for (Class localClass2 : members) {
        localHashMap.put(localClass2, Integer.valueOf(i));
      }
      i++;
    }
  }
  
  private static final class ConflictException
    extends Exception
  {
    final List<Annotation> annotations;
    
    public ConflictException(List<Annotation> paramList)
    {
      annotations = paramList;
    }
  }
  
  private static final class DuplicateException
    extends Exception
  {
    final Annotation a1;
    final Annotation a2;
    
    public DuplicateException(Annotation paramAnnotation1, Annotation paramAnnotation2)
    {
      a1 = paramAnnotation1;
      a2 = paramAnnotation2;
    }
  }
  
  private static enum PropertyGroup
  {
    TRANSIENT(new boolean[] { false, false, false, false, false, false }),  ANY_ATTRIBUTE(new boolean[] { true, false, false, false, false, false }),  ATTRIBUTE(new boolean[] { true, true, true, false, true, true }),  VALUE(new boolean[] { true, true, true, false, true, true }),  ELEMENT(new boolean[] { true, true, true, true, true, true }),  ELEMENT_REF(new boolean[] { true, false, false, true, false, false }),  MAP(new boolean[] { false, false, false, true, false, false });
    
    final int allowedsecondaryAnnotations;
    
    private PropertyGroup(boolean... paramVarArgs)
    {
      int j = 0;
      assert (paramVarArgs.length == ClassInfoImpl.SECONDARY_ANNOTATIONS.length);
      for (int k = 0; k < paramVarArgs.length; k++) {
        if (paramVarArgs[k] != 0) {
          j |= SECONDARY_ANNOTATIONSbitMask;
        }
      }
      allowedsecondaryAnnotations = (j ^ 0xFFFFFFFF);
    }
    
    boolean allows(ClassInfoImpl.SecondaryAnnotation paramSecondaryAnnotation)
    {
      return (allowedsecondaryAnnotations & bitMask) == 0;
    }
  }
  
  private final class PropertySorter
    extends HashMap<String, Integer>
    implements Comparator<PropertyInfoImpl>
  {
    PropertyInfoImpl[] used = new PropertyInfoImpl[propOrder.length];
    private Set<String> collidedNames;
    
    PropertySorter()
    {
      super();
      for (String str : propOrder) {
        if (put(str, Integer.valueOf(size())) != null) {
          builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ENTRY_IN_PROP_ORDER.format(new Object[] { str }), ClassInfoImpl.this));
        }
      }
    }
    
    public int compare(PropertyInfoImpl paramPropertyInfoImpl1, PropertyInfoImpl paramPropertyInfoImpl2)
    {
      int i = checkedGet(paramPropertyInfoImpl1);
      int j = checkedGet(paramPropertyInfoImpl2);
      return i - j;
    }
    
    private int checkedGet(PropertyInfoImpl paramPropertyInfoImpl)
    {
      Integer localInteger = (Integer)get(paramPropertyInfoImpl.getName());
      if (localInteger == null)
      {
        if (kindisOrdered) {
          builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_MISSING_FROM_ORDER.format(new Object[] { paramPropertyInfoImpl.getName() }), paramPropertyInfoImpl));
        }
        localInteger = Integer.valueOf(size());
        put(paramPropertyInfoImpl.getName(), localInteger);
      }
      int i = localInteger.intValue();
      if (i < used.length)
      {
        if ((used[i] != null) && (used[i] != paramPropertyInfoImpl))
        {
          if (collidedNames == null) {
            collidedNames = new HashSet();
          }
          if (collidedNames.add(paramPropertyInfoImpl.getName())) {
            builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_PROPERTIES.format(new Object[] { paramPropertyInfoImpl.getName() }), paramPropertyInfoImpl, used[i]));
          }
        }
        used[i] = paramPropertyInfoImpl;
      }
      return localInteger.intValue();
    }
    
    public void checkUnusedProperties()
    {
      for (int i = 0; i < used.length; i++) {
        if (used[i] == null)
        {
          String str1 = propOrder[i];
          String str2 = EditDistance.findNearest(str1, new AbstractList()
          {
            public String get(int paramAnonymousInt)
            {
              return ((PropertyInfoImpl)properties.get(paramAnonymousInt)).getName();
            }
            
            public int size()
            {
              return properties.size();
            }
          });
          boolean bool = i > properties.size() - 1 ? false : ((PropertyInfoImpl)properties.get(i)).hasAnnotation(OverrideAnnotationOf.class);
          if (!bool) {
            builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_ORDER_CONTAINS_UNUSED_ENTRY.format(new Object[] { str1, str2 }), ClassInfoImpl.this));
          }
        }
      }
    }
  }
  
  private static enum SecondaryAnnotation
  {
    JAVA_TYPE(1, new Class[] { XmlJavaTypeAdapter.class }),  ID_IDREF(2, new Class[] { XmlID.class, XmlIDREF.class }),  BINARY(4, new Class[] { XmlInlineBinaryData.class, XmlMimeType.class, XmlAttachmentRef.class }),  ELEMENT_WRAPPER(8, new Class[] { XmlElementWrapper.class }),  LIST(16, new Class[] { XmlList.class }),  SCHEMA_TYPE(32, new Class[] { XmlSchemaType.class });
    
    final int bitMask;
    final Class<? extends Annotation>[] members;
    
    private SecondaryAnnotation(int paramInt, Class<? extends Annotation>... paramVarArgs)
    {
      bitMask = paramInt;
      members = paramVarArgs;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ClassInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */