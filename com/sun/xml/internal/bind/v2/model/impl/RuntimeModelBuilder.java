package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.FilterTransducer;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.InlineBinaryTransducer;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.MimeTypedTransducer;
import com.sun.xml.internal.bind.v2.runtime.SchemaTypeTransducer;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.xml.sax.SAXException;

public class RuntimeModelBuilder
  extends ModelBuilder<Type, Class, Field, Method>
{
  @Nullable
  public final JAXBContextImpl context;
  
  public RuntimeModelBuilder(JAXBContextImpl paramJAXBContextImpl, RuntimeAnnotationReader paramRuntimeAnnotationReader, Map<Class, Class> paramMap, String paramString)
  {
    super(paramRuntimeAnnotationReader, Utils.REFLECTION_NAVIGATOR, paramMap, paramString);
    context = paramJAXBContextImpl;
  }
  
  public RuntimeNonElement getClassInfo(Class paramClass, Locatable paramLocatable)
  {
    return (RuntimeNonElement)super.getClassInfo(paramClass, paramLocatable);
  }
  
  public RuntimeNonElement getClassInfo(Class paramClass, boolean paramBoolean, Locatable paramLocatable)
  {
    return (RuntimeNonElement)super.getClassInfo(paramClass, paramBoolean, paramLocatable);
  }
  
  protected RuntimeEnumLeafInfoImpl createEnumLeafInfo(Class paramClass, Locatable paramLocatable)
  {
    return new RuntimeEnumLeafInfoImpl(this, paramLocatable, paramClass);
  }
  
  protected RuntimeClassInfoImpl createClassInfo(Class paramClass, Locatable paramLocatable)
  {
    return new RuntimeClassInfoImpl(this, paramLocatable, paramClass);
  }
  
  public RuntimeElementInfoImpl createElementInfo(RegistryInfoImpl<Type, Class, Field, Method> paramRegistryInfoImpl, Method paramMethod)
    throws IllegalAnnotationException
  {
    return new RuntimeElementInfoImpl(this, paramRegistryInfoImpl, paramMethod);
  }
  
  public RuntimeArrayInfoImpl createArrayInfo(Locatable paramLocatable, Type paramType)
  {
    return new RuntimeArrayInfoImpl(this, paramLocatable, (Class)paramType);
  }
  
  protected RuntimeTypeInfoSetImpl createTypeInfoSet()
  {
    return new RuntimeTypeInfoSetImpl(reader);
  }
  
  public RuntimeTypeInfoSet link()
  {
    return (RuntimeTypeInfoSet)super.link();
  }
  
  public static Transducer createTransducer(RuntimeNonElementRef paramRuntimeNonElementRef)
  {
    Object localObject = paramRuntimeNonElementRef.getTarget().getTransducer();
    RuntimePropertyInfo localRuntimePropertyInfo = paramRuntimeNonElementRef.getSource();
    ID localID = localRuntimePropertyInfo.id();
    if (localID == ID.IDREF) {
      return RuntimeBuiltinLeafInfoImpl.STRING;
    }
    if (localID == ID.ID) {
      localObject = new IDTransducerImpl((Transducer)localObject);
    }
    MimeType localMimeType = localRuntimePropertyInfo.getExpectedMimeType();
    if (localMimeType != null) {
      localObject = new MimeTypedTransducer((Transducer)localObject, localMimeType);
    }
    if (localRuntimePropertyInfo.inlineBinaryData()) {
      localObject = new InlineBinaryTransducer((Transducer)localObject);
    }
    if (localRuntimePropertyInfo.getSchemaType() != null)
    {
      if (localRuntimePropertyInfo.getSchemaType().equals(createXSSimpleType())) {
        return RuntimeBuiltinLeafInfoImpl.STRING;
      }
      localObject = new SchemaTypeTransducer((Transducer)localObject, localRuntimePropertyInfo.getSchemaType());
    }
    return (Transducer)localObject;
  }
  
  private static QName createXSSimpleType()
  {
    return new QName("http://www.w3.org/2001/XMLSchema", "anySimpleType");
  }
  
  private static final class IDTransducerImpl<ValueT>
    extends FilterTransducer<ValueT>
  {
    public IDTransducerImpl(Transducer<ValueT> paramTransducer)
    {
      super();
    }
    
    public ValueT parse(CharSequence paramCharSequence)
      throws AccessorException, SAXException
    {
      String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
      UnmarshallingContext.getInstance().addToIdTable(str);
      return (ValueT)core.parse(str);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeModelBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */