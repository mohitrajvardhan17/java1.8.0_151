package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

public class JAXBWrapperAccessor
  extends WrapperAccessor
{
  protected Class<?> contentClass;
  protected HashMap<Object, Class> elementDeclaredTypes;
  
  public JAXBWrapperAccessor(Class<?> paramClass)
  {
    contentClass = paramClass;
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    HashMap localHashMap3 = new HashMap();
    HashMap localHashMap4 = new HashMap();
    HashMap localHashMap5 = new HashMap();
    HashMap localHashMap6 = new HashMap();
    HashMap localHashMap7 = new HashMap();
    HashMap localHashMap8 = new HashMap();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    for (localObject2 : contentClass.getMethods())
    {
      if (PropertySetterBase.setterPattern((Method)localObject2))
      {
        localObject3 = ((Method)localObject2).getName().substring(3, ((Method)localObject2).getName().length()).toLowerCase();
        localHashMap3.put(localObject3, localObject2);
      }
      if (PropertyGetterBase.getterPattern((Method)localObject2))
      {
        localObject3 = ((Method)localObject2).getName();
        localObject4 = ((String)localObject3).startsWith("is") ? ((String)localObject3).substring(2, ((Method)localObject2).getName().length()).toLowerCase() : ((String)localObject3).substring(3, ((Method)localObject2).getName().length()).toLowerCase();
        localHashMap6.put(localObject4, localObject2);
      }
    }
    ??? = new HashSet();
    Iterator localIterator = getAllFields(contentClass).iterator();
    while (localIterator.hasNext())
    {
      Field localField = (Field)localIterator.next();
      localObject2 = (XmlElementWrapper)localField.getAnnotation(XmlElementWrapper.class);
      localObject3 = (XmlElement)localField.getAnnotation(XmlElement.class);
      localObject4 = (XmlElementRef)localField.getAnnotation(XmlElementRef.class);
      String str1 = localField.getName().toLowerCase();
      String str2 = "";
      String str3 = localField.getName();
      if (localObject2 != null)
      {
        str2 = ((XmlElementWrapper)localObject2).namespace();
        if ((((XmlElementWrapper)localObject2).name() != null) && (!((XmlElementWrapper)localObject2).name().equals("")) && (!((XmlElementWrapper)localObject2).name().equals("##default"))) {
          str3 = ((XmlElementWrapper)localObject2).name();
        }
      }
      else if (localObject3 != null)
      {
        str2 = ((XmlElement)localObject3).namespace();
        if ((((XmlElement)localObject3).name() != null) && (!((XmlElement)localObject3).name().equals("")) && (!((XmlElement)localObject3).name().equals("##default"))) {
          str3 = ((XmlElement)localObject3).name();
        }
      }
      else if (localObject4 != null)
      {
        str2 = ((XmlElementRef)localObject4).namespace();
        if ((((XmlElementRef)localObject4).name() != null) && (!((XmlElementRef)localObject4).name().equals("")) && (!((XmlElementRef)localObject4).name().equals("##default"))) {
          str3 = ((XmlElementRef)localObject4).name();
        }
      }
      if (((HashSet)???).contains(str3)) {
        elementLocalNameCollision = true;
      } else {
        ((HashSet)???).add(str3);
      }
      QName localQName = new QName(str2, str3);
      if ((localField.getType().equals(JAXBElement.class)) && ((localField.getGenericType() instanceof ParameterizedType)))
      {
        localObject5 = ((ParameterizedType)localField.getGenericType()).getActualTypeArguments()[0];
        if ((localObject5 instanceof Class))
        {
          localHashMap7.put(localQName, (Class)localObject5);
          localHashMap8.put(str3, (Class)localObject5);
        }
        else if ((localObject5 instanceof GenericArrayType))
        {
          localObject6 = ((GenericArrayType)localObject5).getGenericComponentType();
          if ((localObject6 instanceof Class))
          {
            localObject7 = Array.newInstance((Class)localObject6, 0).getClass();
            localHashMap7.put(localQName, localObject7);
            localHashMap8.put(str3, localObject7);
          }
        }
      }
      if ((str1.startsWith("_")) && (!str3.startsWith("_"))) {
        str1 = str1.substring(1);
      }
      Object localObject5 = (Method)localHashMap3.get(str1);
      Object localObject6 = (Method)localHashMap6.get(str1);
      Object localObject7 = createPropertySetter(localField, (Method)localObject5);
      PropertyGetter localPropertyGetter = createPropertyGetter(localField, (Method)localObject6);
      localHashMap1.put(localQName, localObject7);
      localHashMap2.put(str3, localObject7);
      localHashMap4.put(localQName, localPropertyGetter);
      localHashMap5.put(str3, localPropertyGetter);
    }
    if (elementLocalNameCollision)
    {
      propertySetters = localHashMap1;
      propertyGetters = localHashMap4;
      elementDeclaredTypes = localHashMap7;
    }
    else
    {
      propertySetters = localHashMap2;
      propertyGetters = localHashMap5;
      elementDeclaredTypes = localHashMap8;
    }
  }
  
  protected static List<Field> getAllFields(Class<?> paramClass)
  {
    ArrayList localArrayList = new ArrayList();
    while (!Object.class.equals(paramClass))
    {
      localArrayList.addAll(Arrays.asList(getDeclaredFields(paramClass)));
      paramClass = paramClass.getSuperclass();
    }
    return localArrayList;
  }
  
  protected static Field[] getDeclaredFields(Class<?> paramClass)
  {
    try
    {
      System.getSecurityManager() == null ? paramClass.getDeclaredFields() : (Field[])AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Field[] run()
          throws IllegalAccessException
        {
          return val$clz.getDeclaredFields();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      localPrivilegedActionException.printStackTrace();
    }
    return null;
  }
  
  protected static PropertyGetter createPropertyGetter(Field paramField, Method paramMethod)
  {
    if ((!paramField.isAccessible()) && (paramMethod != null))
    {
      MethodGetter localMethodGetter = new MethodGetter(paramMethod);
      if (localMethodGetter.getType().toString().equals(paramField.getType().toString())) {
        return localMethodGetter;
      }
    }
    return new FieldGetter(paramField);
  }
  
  protected static PropertySetter createPropertySetter(Field paramField, Method paramMethod)
  {
    if ((!paramField.isAccessible()) && (paramMethod != null))
    {
      MethodSetter localMethodSetter = new MethodSetter(paramMethod);
      if (localMethodSetter.getType().toString().equals(paramField.getType().toString())) {
        return localMethodSetter;
      }
    }
    return new FieldSetter(paramField);
  }
  
  private Class getElementDeclaredType(QName paramQName)
  {
    String str = elementLocalNameCollision ? paramQName : paramQName.getLocalPart();
    return (Class)elementDeclaredTypes.get(str);
  }
  
  public PropertyAccessor getPropertyAccessor(String paramString1, String paramString2)
  {
    final QName localQName = new QName(paramString1, paramString2);
    final PropertySetter localPropertySetter = getPropertySetter(localQName);
    final PropertyGetter localPropertyGetter = getPropertyGetter(localQName);
    final boolean bool1 = localPropertySetter.getType().equals(JAXBElement.class);
    final boolean bool2 = List.class.isAssignableFrom(localPropertySetter.getType());
    final Class localClass = bool1 ? getElementDeclaredType(localQName) : null;
    new PropertyAccessor()
    {
      public Object get(Object paramAnonymousObject)
        throws DatabindingException
      {
        Object localObject;
        if (bool1)
        {
          JAXBElement localJAXBElement = (JAXBElement)localPropertyGetter.get(paramAnonymousObject);
          localObject = localJAXBElement == null ? null : localJAXBElement.getValue();
        }
        else
        {
          localObject = localPropertyGetter.get(paramAnonymousObject);
        }
        if ((localObject == null) && (bool2))
        {
          localObject = new ArrayList();
          set(paramAnonymousObject, localObject);
        }
        return localObject;
      }
      
      public void set(Object paramAnonymousObject1, Object paramAnonymousObject2)
        throws DatabindingException
      {
        if (bool1)
        {
          JAXBElement localJAXBElement = new JAXBElement(localQName, localClass, contentClass, paramAnonymousObject2);
          localPropertySetter.set(paramAnonymousObject1, localJAXBElement);
        }
        else
        {
          localPropertySetter.set(paramAnonymousObject1, paramAnonymousObject2);
        }
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\JAXBWrapperAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */