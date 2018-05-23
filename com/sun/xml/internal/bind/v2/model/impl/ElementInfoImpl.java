package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.runtime.SwaRefAdapter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.activation.MimeType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

class ElementInfoImpl<T, C, F, M>
  extends TypeInfoImpl<T, C, F, M>
  implements ElementInfo<T, C>
{
  private final QName tagName;
  private final NonElement<T, C> contentType;
  private final T tOfJAXBElementT;
  private final T elementType;
  private final ClassInfo<T, C> scope;
  private final XmlElementDecl anno;
  private ElementInfoImpl<T, C, F, M> substitutionHead;
  private FinalArrayList<ElementInfoImpl<T, C, F, M>> substitutionMembers;
  private final M method;
  private final Adapter<T, C> adapter;
  private final boolean isCollection;
  private final ID id;
  private final ElementInfoImpl<T, C, F, M>.PropertyImpl property;
  private final MimeType expectedMimeType;
  private final boolean inlineBinary;
  private final QName schemaType;
  
  public ElementInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, RegistryInfoImpl<T, C, F, M> paramRegistryInfoImpl, M paramM)
    throws IllegalAnnotationException
  {
    super(paramModelBuilder, paramRegistryInfoImpl);
    method = paramM;
    anno = ((XmlElementDecl)reader().getMethodAnnotation(XmlElementDecl.class, paramM, this));
    assert (anno != null);
    assert ((anno instanceof Locatable));
    elementType = nav().getReturnType(paramM);
    Object localObject1 = nav().getBaseClass(elementType, nav().asDecl(JAXBElement.class));
    if (localObject1 == null) {
      throw new IllegalAnnotationException(Messages.XML_ELEMENT_MAPPING_ON_NON_IXMLELEMENT_METHOD.format(new Object[] { nav().getMethodName(paramM) }), anno);
    }
    tagName = parseElementName(anno);
    Object[] arrayOfObject = nav().getMethodParameters(paramM);
    Adapter localAdapter = null;
    Object localObject3;
    if (arrayOfObject.length > 0)
    {
      localObject2 = (XmlJavaTypeAdapter)reader().getMethodAnnotation(XmlJavaTypeAdapter.class, paramM, this);
      if (localObject2 != null)
      {
        localAdapter = new Adapter((XmlJavaTypeAdapter)localObject2, reader(), nav());
      }
      else
      {
        localObject3 = (XmlAttachmentRef)reader().getMethodAnnotation(XmlAttachmentRef.class, paramM, this);
        if (localObject3 != null)
        {
          TODO.prototype("in Annotation Processing swaRefAdapter isn't avaialble, so this returns null");
          localAdapter = new Adapter(owner.nav.asDecl(SwaRefAdapter.class), owner.nav);
        }
      }
    }
    adapter = localAdapter;
    tOfJAXBElementT = (arrayOfObject.length > 0 ? arrayOfObject[0] : nav().getTypeArgument(localObject1, 0));
    if (adapter == null)
    {
      localObject2 = nav().getBaseClass(tOfJAXBElementT, nav().asDecl(List.class));
      if (localObject2 == null)
      {
        isCollection = false;
        contentType = paramModelBuilder.getTypeInfo(tOfJAXBElementT, this);
      }
      else
      {
        isCollection = true;
        contentType = paramModelBuilder.getTypeInfo(nav().getTypeArgument(localObject2, 0), this);
      }
    }
    else
    {
      contentType = paramModelBuilder.getTypeInfo(adapter.defaultType, this);
      isCollection = false;
    }
    Object localObject2 = reader().getClassValue(anno, "scope");
    if (nav().isSameType(localObject2, nav().ref(XmlElementDecl.GLOBAL.class)))
    {
      scope = null;
    }
    else
    {
      localObject3 = paramModelBuilder.getClassInfo(nav().asDecl(localObject2), this);
      if (!(localObject3 instanceof ClassInfo)) {
        throw new IllegalAnnotationException(Messages.SCOPE_IS_NOT_COMPLEXTYPE.format(new Object[] { nav().getTypeName(localObject2) }), anno);
      }
      scope = ((ClassInfo)localObject3);
    }
    id = calcId();
    property = createPropertyImpl();
    expectedMimeType = Util.calcExpectedMediaType(property, paramModelBuilder);
    inlineBinary = reader().hasMethodAnnotation(XmlInlineBinaryData.class, method);
    schemaType = Util.calcSchemaType(reader(), property, registryClass, getContentInMemoryType(), this);
  }
  
  final QName parseElementName(XmlElementDecl paramXmlElementDecl)
  {
    String str1 = paramXmlElementDecl.name();
    String str2 = paramXmlElementDecl.namespace();
    if (str2.equals("##default"))
    {
      XmlSchema localXmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, nav().getDeclaringClassForMethod(method), this);
      if (localXmlSchema != null) {
        str2 = localXmlSchema.namespace();
      } else {
        str2 = builder.defaultNsUri;
      }
    }
    return new QName(str2.intern(), str1.intern());
  }
  
  protected ElementInfoImpl<T, C, F, M>.PropertyImpl createPropertyImpl()
  {
    return new PropertyImpl();
  }
  
  public ElementPropertyInfo<T, C> getProperty()
  {
    return property;
  }
  
  public NonElement<T, C> getContentType()
  {
    return contentType;
  }
  
  public T getContentInMemoryType()
  {
    if (adapter == null) {
      return (T)tOfJAXBElementT;
    }
    return (T)adapter.customType;
  }
  
  public QName getElementName()
  {
    return tagName;
  }
  
  public T getType()
  {
    return (T)elementType;
  }
  
  /**
   * @deprecated
   */
  public final boolean canBeReferencedByIDREF()
  {
    return false;
  }
  
  private ID calcId()
  {
    if (reader().hasMethodAnnotation(XmlID.class, method)) {
      return ID.ID;
    }
    if (reader().hasMethodAnnotation(XmlIDREF.class, method)) {
      return ID.IDREF;
    }
    return ID.NONE;
  }
  
  public ClassInfo<T, C> getScope()
  {
    return scope;
  }
  
  public ElementInfo<T, C> getSubstitutionHead()
  {
    return substitutionHead;
  }
  
  public Collection<? extends ElementInfoImpl<T, C, F, M>> getSubstitutionMembers()
  {
    if (substitutionMembers == null) {
      return Collections.emptyList();
    }
    return substitutionMembers;
  }
  
  void link()
  {
    if (anno.substitutionHeadName().length() != 0)
    {
      QName localQName = new QName(anno.substitutionHeadNamespace(), anno.substitutionHeadName());
      substitutionHead = owner.getElementInfo(null, localQName);
      if (substitutionHead == null) {
        builder.reportError(new IllegalAnnotationException(Messages.NON_EXISTENT_ELEMENT_MAPPING.format(new Object[] { localQName.getNamespaceURI(), localQName.getLocalPart() }), anno));
      } else {
        substitutionHead.addSubstitutionMember(this);
      }
    }
    else
    {
      substitutionHead = null;
    }
    super.link();
  }
  
  private void addSubstitutionMember(ElementInfoImpl<T, C, F, M> paramElementInfoImpl)
  {
    if (substitutionMembers == null) {
      substitutionMembers = new FinalArrayList();
    }
    substitutionMembers.add(paramElementInfoImpl);
  }
  
  public Location getLocation()
  {
    return nav().getMethodLocation(method);
  }
  
  protected class PropertyImpl
    implements ElementPropertyInfo<T, C>, TypeRef<T, C>, AnnotationSource
  {
    protected PropertyImpl() {}
    
    public NonElement<T, C> getTarget()
    {
      return contentType;
    }
    
    public QName getTagName()
    {
      return tagName;
    }
    
    public List<? extends TypeRef<T, C>> getTypes()
    {
      return Collections.singletonList(this);
    }
    
    public List<? extends NonElement<T, C>> ref()
    {
      return Collections.singletonList(contentType);
    }
    
    public QName getXmlName()
    {
      return tagName;
    }
    
    public boolean isCollectionRequired()
    {
      return false;
    }
    
    public boolean isCollectionNillable()
    {
      return true;
    }
    
    public boolean isNillable()
    {
      return true;
    }
    
    public String getDefaultValue()
    {
      String str = anno.defaultValue();
      if (str.equals("\000")) {
        return null;
      }
      return str;
    }
    
    public ElementInfoImpl<T, C, F, M> parent()
    {
      return ElementInfoImpl.this;
    }
    
    public String getName()
    {
      return "value";
    }
    
    public String displayName()
    {
      return "JAXBElement#value";
    }
    
    public boolean isCollection()
    {
      return isCollection;
    }
    
    public boolean isValueList()
    {
      return isCollection;
    }
    
    public boolean isRequired()
    {
      return true;
    }
    
    public PropertyKind kind()
    {
      return PropertyKind.ELEMENT;
    }
    
    public Adapter<T, C> getAdapter()
    {
      return adapter;
    }
    
    public ID id()
    {
      return id;
    }
    
    public MimeType getExpectedMimeType()
    {
      return expectedMimeType;
    }
    
    public QName getSchemaType()
    {
      return schemaType;
    }
    
    public boolean inlineBinaryData()
    {
      return inlineBinary;
    }
    
    public PropertyInfo<T, C> getSource()
    {
      return this;
    }
    
    public <A extends Annotation> A readAnnotation(Class<A> paramClass)
    {
      return reader().getMethodAnnotation(paramClass, method, ElementInfoImpl.this);
    }
    
    public boolean hasAnnotation(Class<? extends Annotation> paramClass)
    {
      return reader().hasMethodAnnotation(paramClass, method);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ElementInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */