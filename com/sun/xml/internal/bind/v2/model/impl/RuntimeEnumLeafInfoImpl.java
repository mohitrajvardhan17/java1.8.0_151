package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.annotation.FieldLocatable;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class RuntimeEnumLeafInfoImpl<T extends Enum<T>, B>
  extends EnumLeafInfoImpl<Type, Class, Field, Method>
  implements RuntimeEnumLeafInfo, Transducer<T>
{
  private final Transducer<B> baseXducer;
  private final Map<B, T> parseMap = new HashMap();
  private final Map<T, B> printMap;
  
  public Transducer<T> getTransducer()
  {
    return this;
  }
  
  RuntimeEnumLeafInfoImpl(RuntimeModelBuilder paramRuntimeModelBuilder, Locatable paramLocatable, Class<T> paramClass)
  {
    super(paramRuntimeModelBuilder, paramLocatable, paramClass, paramClass);
    printMap = new EnumMap(paramClass);
    baseXducer = ((RuntimeNonElement)baseType).getTransducer();
  }
  
  public RuntimeEnumConstantImpl createEnumConstant(String paramString1, String paramString2, Field paramField, EnumConstantImpl<Type, Class, Field, Method> paramEnumConstantImpl)
  {
    Enum localEnum;
    try
    {
      try
      {
        paramField.setAccessible(true);
      }
      catch (SecurityException localSecurityException) {}
      localEnum = (Enum)paramField.get(null);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalAccessError(localIllegalAccessException.getMessage());
    }
    Object localObject = null;
    try
    {
      localObject = baseXducer.parse(paramString2);
    }
    catch (Exception localException)
    {
      builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ENUM_VALUE.format(new Object[] { paramString2, ((Type)baseType.getType()).toString() }), localException, new FieldLocatable(this, paramField, nav())));
    }
    parseMap.put(localObject, localEnum);
    printMap.put(localEnum, localObject);
    return new RuntimeEnumConstantImpl(this, paramString1, paramString2, paramEnumConstantImpl);
  }
  
  public QName[] getTypeNames()
  {
    return new QName[] { getTypeName() };
  }
  
  public boolean isDefault()
  {
    return false;
  }
  
  public Class getClazz()
  {
    return (Class)clazz;
  }
  
  public boolean useNamespace()
  {
    return baseXducer.useNamespace();
  }
  
  public void declareNamespace(T paramT, XMLSerializer paramXMLSerializer)
    throws AccessorException
  {
    baseXducer.declareNamespace(printMap.get(paramT), paramXMLSerializer);
  }
  
  public CharSequence print(T paramT)
    throws AccessorException
  {
    return baseXducer.print(printMap.get(paramT));
  }
  
  public T parse(CharSequence paramCharSequence)
    throws AccessorException, SAXException
  {
    Object localObject = baseXducer.parse(paramCharSequence);
    if (tokenStringType) {
      localObject = ((String)localObject).trim();
    }
    return (Enum)parseMap.get(localObject);
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, T paramT, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    baseXducer.writeText(paramXMLSerializer, printMap.get(paramT), paramString);
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, T paramT, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    baseXducer.writeLeafElement(paramXMLSerializer, paramName, printMap.get(paramT), paramString);
  }
  
  public QName getTypeName(T paramT)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeEnumLeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */