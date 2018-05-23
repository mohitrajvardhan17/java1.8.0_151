package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import javax.activation.MimeType;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.namespace.QName;

abstract class PropertyInfoImpl<T, C, F, M>
  implements PropertyInfo<T, C>, Locatable, Comparable<PropertyInfoImpl>
{
  protected final PropertySeed<T, C, F, M> seed;
  private final boolean isCollection;
  private final ID id;
  private final MimeType expectedMimeType;
  private final boolean inlineBinary;
  private final QName schemaType;
  protected final ClassInfoImpl<T, C, F, M> parent;
  private final Adapter<T, C> adapter;
  
  protected PropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed)
  {
    seed = paramPropertySeed;
    parent = paramClassInfoImpl;
    if (paramClassInfoImpl == null) {
      throw new AssertionError();
    }
    MimeType localMimeType = Util.calcExpectedMediaType(seed, builder);
    if ((localMimeType != null) && (!kindcanHaveXmlMimeType))
    {
      builder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_ANNOTATION.format(new Object[] { XmlMimeType.class.getName() }), seed.readAnnotation(XmlMimeType.class)));
      localMimeType = null;
    }
    expectedMimeType = localMimeType;
    inlineBinary = seed.hasAnnotation(XmlInlineBinaryData.class);
    Object localObject1 = seed.getRawType();
    XmlJavaTypeAdapter localXmlJavaTypeAdapter = getApplicableAdapter(localObject1);
    if (localXmlJavaTypeAdapter != null)
    {
      isCollection = false;
      adapter = new Adapter(localXmlJavaTypeAdapter, reader(), nav());
    }
    else
    {
      isCollection = ((nav().isSubClassOf(localObject1, nav().ref(Collection.class))) || (nav().isArrayButNotByteArray(localObject1)));
      localXmlJavaTypeAdapter = getApplicableAdapter(getIndividualType());
      if (localXmlJavaTypeAdapter == null)
      {
        XmlAttachmentRef localXmlAttachmentRef = (XmlAttachmentRef)seed.readAnnotation(XmlAttachmentRef.class);
        if (localXmlAttachmentRef != null)
        {
          builder.hasSwaRef = true;
          adapter = new Adapter(nav().asDecl(SwaRefAdapter.class), nav());
        }
        else
        {
          adapter = null;
          localXmlJavaTypeAdapter = (XmlJavaTypeAdapter)seed.readAnnotation(XmlJavaTypeAdapter.class);
          if (localXmlJavaTypeAdapter != null)
          {
            Object localObject2 = reader().getClassValue(localXmlJavaTypeAdapter, "value");
            builder.reportError(new IllegalAnnotationException(Messages.UNMATCHABLE_ADAPTER.format(new Object[] { nav().getTypeName(localObject2), nav().getTypeName(localObject1) }), localXmlJavaTypeAdapter));
          }
        }
      }
      else
      {
        adapter = new Adapter(localXmlJavaTypeAdapter, reader(), nav());
      }
    }
    id = calcId();
    schemaType = Util.calcSchemaType(reader(), seed, clazz, getIndividualType(), this);
  }
  
  public ClassInfoImpl<T, C, F, M> parent()
  {
    return parent;
  }
  
  protected final Navigator<T, C, F, M> nav()
  {
    return parent.nav();
  }
  
  protected final AnnotationReader<T, C, F, M> reader()
  {
    return parent.reader();
  }
  
  public T getRawType()
  {
    return (T)seed.getRawType();
  }
  
  public T getIndividualType()
  {
    if (adapter != null) {
      return (T)adapter.defaultType;
    }
    Object localObject1 = getRawType();
    if (!isCollection()) {
      return (T)localObject1;
    }
    if (nav().isArrayButNotByteArray(localObject1)) {
      return (T)nav().getComponentType(localObject1);
    }
    Object localObject2 = nav().getBaseClass(localObject1, nav().asDecl(Collection.class));
    if (nav().isParameterizedType(localObject2)) {
      return (T)nav().getTypeArgument(localObject2, 0);
    }
    return (T)nav().ref(Object.class);
  }
  
  public final String getName()
  {
    return seed.getName();
  }
  
  private boolean isApplicable(XmlJavaTypeAdapter paramXmlJavaTypeAdapter, T paramT)
  {
    if (paramXmlJavaTypeAdapter == null) {
      return false;
    }
    Object localObject1 = reader().getClassValue(paramXmlJavaTypeAdapter, "type");
    if (nav().isSameType(paramT, localObject1)) {
      return true;
    }
    Object localObject2 = reader().getClassValue(paramXmlJavaTypeAdapter, "value");
    Object localObject3 = nav().getBaseClass(localObject2, nav().asDecl(XmlAdapter.class));
    if (!nav().isParameterizedType(localObject3)) {
      return true;
    }
    Object localObject4 = nav().getTypeArgument(localObject3, 1);
    return nav().isSubClassOf(paramT, localObject4);
  }
  
  private XmlJavaTypeAdapter getApplicableAdapter(T paramT)
  {
    XmlJavaTypeAdapter localXmlJavaTypeAdapter1 = (XmlJavaTypeAdapter)seed.readAnnotation(XmlJavaTypeAdapter.class);
    if ((localXmlJavaTypeAdapter1 != null) && (isApplicable(localXmlJavaTypeAdapter1, paramT))) {
      return localXmlJavaTypeAdapter1;
    }
    XmlJavaTypeAdapters localXmlJavaTypeAdapters = (XmlJavaTypeAdapters)reader().getPackageAnnotation(XmlJavaTypeAdapters.class, parent.clazz, seed);
    if (localXmlJavaTypeAdapters != null) {
      for (XmlJavaTypeAdapter localXmlJavaTypeAdapter2 : localXmlJavaTypeAdapters.value()) {
        if (isApplicable(localXmlJavaTypeAdapter2, paramT)) {
          return localXmlJavaTypeAdapter2;
        }
      }
    }
    localXmlJavaTypeAdapter1 = (XmlJavaTypeAdapter)reader().getPackageAnnotation(XmlJavaTypeAdapter.class, parent.clazz, seed);
    if (isApplicable(localXmlJavaTypeAdapter1, paramT)) {
      return localXmlJavaTypeAdapter1;
    }
    ??? = nav().asDecl(paramT);
    if (??? != null)
    {
      localXmlJavaTypeAdapter1 = (XmlJavaTypeAdapter)reader().getClassAnnotation(XmlJavaTypeAdapter.class, ???, seed);
      if ((localXmlJavaTypeAdapter1 != null) && (isApplicable(localXmlJavaTypeAdapter1, paramT))) {
        return localXmlJavaTypeAdapter1;
      }
    }
    return null;
  }
  
  public Adapter<T, C> getAdapter()
  {
    return adapter;
  }
  
  public final String displayName()
  {
    return nav().getClassName(parent.getClazz()) + '#' + getName();
  }
  
  public final ID id()
  {
    return id;
  }
  
  private ID calcId()
  {
    if (seed.hasAnnotation(XmlID.class))
    {
      if (!nav().isSameType(getIndividualType(), nav().ref(String.class))) {
        parent.builder.reportError(new IllegalAnnotationException(Messages.ID_MUST_BE_STRING.format(new Object[] { getName() }), seed));
      }
      return ID.ID;
    }
    if (seed.hasAnnotation(XmlIDREF.class)) {
      return ID.IDREF;
    }
    return ID.NONE;
  }
  
  public final MimeType getExpectedMimeType()
  {
    return expectedMimeType;
  }
  
  public final boolean inlineBinaryData()
  {
    return inlineBinary;
  }
  
  public final QName getSchemaType()
  {
    return schemaType;
  }
  
  public final boolean isCollection()
  {
    return isCollection;
  }
  
  protected void link()
  {
    if (id == ID.IDREF)
    {
      Iterator localIterator = ref().iterator();
      while (localIterator.hasNext())
      {
        TypeInfo localTypeInfo = (TypeInfo)localIterator.next();
        if (!localTypeInfo.canBeReferencedByIDREF()) {
          parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_IDREF.format(new Object[] { parent.builder.nav.getTypeName(localTypeInfo.getType()) }), this));
        }
      }
    }
  }
  
  public Locatable getUpstream()
  {
    return parent;
  }
  
  public Location getLocation()
  {
    return seed.getLocation();
  }
  
  protected final QName calcXmlName(XmlElement paramXmlElement)
  {
    if (paramXmlElement != null) {
      return calcXmlName(paramXmlElement.namespace(), paramXmlElement.name());
    }
    return calcXmlName("##default", "##default");
  }
  
  protected final QName calcXmlName(XmlElementWrapper paramXmlElementWrapper)
  {
    if (paramXmlElementWrapper != null) {
      return calcXmlName(paramXmlElementWrapper.namespace(), paramXmlElementWrapper.name());
    }
    return calcXmlName("##default", "##default");
  }
  
  private QName calcXmlName(String paramString1, String paramString2)
  {
    
    if ((paramString2.length() == 0) || (paramString2.equals("##default"))) {
      paramString2 = seed.getName();
    }
    if (paramString1.equals("##default"))
    {
      XmlSchema localXmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, parent.getClazz(), this);
      if (localXmlSchema != null) {
        switch (localXmlSchema.elementFormDefault())
        {
        case QUALIFIED: 
          QName localQName = parent.getTypeName();
          if (localQName != null) {
            paramString1 = localQName.getNamespaceURI();
          } else {
            paramString1 = localXmlSchema.namespace();
          }
          if (paramString1.length() == 0) {
            paramString1 = parent.builder.defaultNsUri;
          }
          break;
        case UNQUALIFIED: 
        case UNSET: 
          paramString1 = "";
        }
      } else {
        paramString1 = "";
      }
    }
    return new QName(paramString1.intern(), paramString2.intern());
  }
  
  public int compareTo(PropertyInfoImpl paramPropertyInfoImpl)
  {
    return getName().compareTo(paramPropertyInfoImpl.getName());
  }
  
  public final <A extends Annotation> A readAnnotation(Class<A> paramClass)
  {
    return seed.readAnnotation(paramClass);
  }
  
  public final boolean hasAnnotation(Class<? extends Annotation> paramClass)
  {
    return seed.hasAnnotation(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\PropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */