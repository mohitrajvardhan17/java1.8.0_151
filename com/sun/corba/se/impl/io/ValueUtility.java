package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.RepositoryIdCache;
import com.sun.org.omg.CORBA.AttributeDescription;
import com.sun.org.omg.CORBA.Initializer;
import com.sun.org.omg.CORBA.OperationDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA._IDLTypeStub;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import java.util.Iterator;
import java.util.Stack;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public class ValueUtility
{
  public static final short PRIVATE_MEMBER = 0;
  public static final short PUBLIC_MEMBER = 1;
  private static final String[] primitiveConstants = { null, null, "S", "I", "S", "I", "F", "D", "Z", "C", "B", null, null, null, null, null, null, null, null, null, null, null, null, "J", "J", "D", "C", null, null, null, null, null, null };
  
  public ValueUtility() {}
  
  public static String getSignature(ValueMember paramValueMember)
    throws ClassNotFoundException
  {
    if ((type.kind().value() == 30) || (type.kind().value() == 29) || (type.kind().value() == 14))
    {
      Class localClass = RepositoryId.cache.getId(id).getClassFromType();
      return ObjectStreamClass.getSignature(localClass);
    }
    return primitiveConstants[type.kind().value()];
  }
  
  public static FullValueDescription translate(ORB paramORB, ObjectStreamClass paramObjectStreamClass, ValueHandler paramValueHandler)
  {
    FullValueDescription localFullValueDescription = new FullValueDescription();
    Class localClass1 = paramObjectStreamClass.forClass();
    ValueHandlerImpl localValueHandlerImpl = (ValueHandlerImpl)paramValueHandler;
    String str = localValueHandlerImpl.createForAnyType(localClass1);
    name = localValueHandlerImpl.getUnqualifiedName(str);
    if (name == null) {
      name = "";
    }
    id = localValueHandlerImpl.getRMIRepositoryID(localClass1);
    if (id == null) {
      id = "";
    }
    is_abstract = ObjectStreamClassCorbaExt.isAbstractInterface(localClass1);
    is_custom = ((paramObjectStreamClass.hasWriteObject()) || (paramObjectStreamClass.isExternalizable()));
    defined_in = localValueHandlerImpl.getDefinedInId(str);
    if (defined_in == null) {
      defined_in = "";
    }
    version = localValueHandlerImpl.getSerialVersionUID(str);
    if (version == null) {
      version = "";
    }
    operations = new OperationDescription[0];
    attributes = new AttributeDescription[0];
    IdentityKeyValueStack localIdentityKeyValueStack = new IdentityKeyValueStack(null);
    members = translateMembers(paramORB, paramObjectStreamClass, paramValueHandler, localIdentityKeyValueStack);
    initializers = new Initializer[0];
    Class[] arrayOfClass = paramObjectStreamClass.forClass().getInterfaces();
    int i = 0;
    supported_interfaces = new String[arrayOfClass.length];
    for (int j = 0; j < arrayOfClass.length; j++)
    {
      supported_interfaces[j] = localValueHandlerImpl.createForAnyType(arrayOfClass[j]);
      if ((!Remote.class.isAssignableFrom(arrayOfClass[j])) || (!Modifier.isPublic(arrayOfClass[j].getModifiers()))) {
        i++;
      }
    }
    abstract_base_values = new String[i];
    for (j = 0; j < arrayOfClass.length; j++) {
      if ((!Remote.class.isAssignableFrom(arrayOfClass[j])) || (!Modifier.isPublic(arrayOfClass[j].getModifiers()))) {
        abstract_base_values[j] = localValueHandlerImpl.createForAnyType(arrayOfClass[j]);
      }
    }
    is_truncatable = false;
    Class localClass2 = paramObjectStreamClass.forClass().getSuperclass();
    if (Serializable.class.isAssignableFrom(localClass2)) {
      base_value = localValueHandlerImpl.getRMIRepositoryID(localClass2);
    } else {
      base_value = "";
    }
    type = paramORB.get_primitive_tc(TCKind.tk_value);
    return localFullValueDescription;
  }
  
  private static ValueMember[] translateMembers(ORB paramORB, ObjectStreamClass paramObjectStreamClass, ValueHandler paramValueHandler, IdentityKeyValueStack paramIdentityKeyValueStack)
  {
    ValueHandlerImpl localValueHandlerImpl = (ValueHandlerImpl)paramValueHandler;
    ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields();
    int i = arrayOfObjectStreamField.length;
    ValueMember[] arrayOfValueMember = new ValueMember[i];
    for (int j = 0; j < i; j++)
    {
      String str = localValueHandlerImpl.getRMIRepositoryID(arrayOfObjectStreamField[j].getClazz());
      arrayOfValueMember[j] = new ValueMember();
      name = arrayOfObjectStreamField[j].getName();
      id = str;
      defined_in = localValueHandlerImpl.getDefinedInId(str);
      version = "1.0";
      type_def = new _IDLTypeStub();
      if (arrayOfObjectStreamField[j].getField() == null)
      {
        access = 0;
      }
      else
      {
        int k = arrayOfObjectStreamField[j].getField().getModifiers();
        if (Modifier.isPublic(k)) {
          access = 1;
        } else {
          access = 0;
        }
      }
      switch (arrayOfObjectStreamField[j].getTypeCode())
      {
      case 'B': 
        type = paramORB.get_primitive_tc(TCKind.tk_octet);
        break;
      case 'C': 
        type = paramORB.get_primitive_tc(localValueHandlerImpl.getJavaCharTCKind());
        break;
      case 'F': 
        type = paramORB.get_primitive_tc(TCKind.tk_float);
        break;
      case 'D': 
        type = paramORB.get_primitive_tc(TCKind.tk_double);
        break;
      case 'I': 
        type = paramORB.get_primitive_tc(TCKind.tk_long);
        break;
      case 'J': 
        type = paramORB.get_primitive_tc(TCKind.tk_longlong);
        break;
      case 'S': 
        type = paramORB.get_primitive_tc(TCKind.tk_short);
        break;
      case 'Z': 
        type = paramORB.get_primitive_tc(TCKind.tk_boolean);
        break;
      case 'E': 
      case 'G': 
      case 'H': 
      case 'K': 
      case 'L': 
      case 'M': 
      case 'N': 
      case 'O': 
      case 'P': 
      case 'Q': 
      case 'R': 
      case 'T': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case 'Y': 
      default: 
        type = createTypeCodeForClassInternal(paramORB, arrayOfObjectStreamField[j].getClazz(), localValueHandlerImpl, paramIdentityKeyValueStack);
        id = localValueHandlerImpl.createForAnyType(arrayOfObjectStreamField[j].getType());
      }
    }
    return arrayOfValueMember;
  }
  
  private static boolean exists(String paramString, String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramString.equals(paramArrayOfString[i])) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isAssignableFrom(String paramString, FullValueDescription paramFullValueDescription, CodeBase paramCodeBase)
  {
    if (exists(paramString, supported_interfaces)) {
      return true;
    }
    if (paramString.equals(id)) {
      return true;
    }
    if ((base_value != null) && (!base_value.equals("")))
    {
      FullValueDescription localFullValueDescription = paramCodeBase.meta(base_value);
      return isAssignableFrom(paramString, localFullValueDescription, paramCodeBase);
    }
    return false;
  }
  
  public static TypeCode createTypeCodeForClass(ORB paramORB, Class paramClass, ValueHandler paramValueHandler)
  {
    IdentityKeyValueStack localIdentityKeyValueStack = new IdentityKeyValueStack(null);
    TypeCode localTypeCode = createTypeCodeForClassInternal(paramORB, paramClass, paramValueHandler, localIdentityKeyValueStack);
    return localTypeCode;
  }
  
  private static TypeCode createTypeCodeForClassInternal(ORB paramORB, Class paramClass, ValueHandler paramValueHandler, IdentityKeyValueStack paramIdentityKeyValueStack)
  {
    TypeCode localTypeCode = null;
    String str = (String)paramIdentityKeyValueStack.get(paramClass);
    if (str != null) {
      return paramORB.create_recursive_tc(str);
    }
    str = paramValueHandler.getRMIRepositoryID(paramClass);
    if (str == null) {
      str = "";
    }
    paramIdentityKeyValueStack.push(paramClass, str);
    localTypeCode = createTypeCodeInternal(paramORB, paramClass, paramValueHandler, str, paramIdentityKeyValueStack);
    paramIdentityKeyValueStack.pop();
    return localTypeCode;
  }
  
  private static TypeCode createTypeCodeInternal(ORB paramORB, Class paramClass, ValueHandler paramValueHandler, String paramString, IdentityKeyValueStack paramIdentityKeyValueStack)
  {
    if (paramClass.isArray())
    {
      localObject = paramClass.getComponentType();
      TypeCode localTypeCode1;
      if (((Class)localObject).isPrimitive()) {
        localTypeCode1 = getPrimitiveTypeCodeForClass(paramORB, (Class)localObject, paramValueHandler);
      } else {
        localTypeCode1 = createTypeCodeForClassInternal(paramORB, (Class)localObject, paramValueHandler, paramIdentityKeyValueStack);
      }
      localTypeCode2 = paramORB.create_sequence_tc(0, localTypeCode1);
      return paramORB.create_value_box_tc(paramString, "Sequence", localTypeCode2);
    }
    if (paramClass == String.class)
    {
      localObject = paramORB.create_string_tc(0);
      return paramORB.create_value_box_tc(paramString, "StringValue", (TypeCode)localObject);
    }
    if (Remote.class.isAssignableFrom(paramClass)) {
      return paramORB.get_primitive_tc(TCKind.tk_objref);
    }
    if (org.omg.CORBA.Object.class.isAssignableFrom(paramClass)) {
      return paramORB.get_primitive_tc(TCKind.tk_objref);
    }
    Object localObject = ObjectStreamClass.lookup(paramClass);
    if (localObject == null) {
      return paramORB.create_value_box_tc(paramString, "Value", paramORB.get_primitive_tc(TCKind.tk_value));
    }
    short s = ((ObjectStreamClass)localObject).isCustomMarshaled() ? 1 : 0;
    TypeCode localTypeCode2 = null;
    Class localClass = paramClass.getSuperclass();
    if ((localClass != null) && (Serializable.class.isAssignableFrom(localClass))) {
      localTypeCode2 = createTypeCodeForClassInternal(paramORB, localClass, paramValueHandler, paramIdentityKeyValueStack);
    }
    ValueMember[] arrayOfValueMember = translateMembers(paramORB, (ObjectStreamClass)localObject, paramValueHandler, paramIdentityKeyValueStack);
    return paramORB.create_value_tc(paramString, paramClass.getName(), s, localTypeCode2, arrayOfValueMember);
  }
  
  public static TypeCode getPrimitiveTypeCodeForClass(ORB paramORB, Class paramClass, ValueHandler paramValueHandler)
  {
    if (paramClass == Integer.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_long);
    }
    if (paramClass == Byte.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_octet);
    }
    if (paramClass == Long.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_longlong);
    }
    if (paramClass == Float.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_float);
    }
    if (paramClass == Double.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_double);
    }
    if (paramClass == Short.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_short);
    }
    if (paramClass == Character.TYPE) {
      return paramORB.get_primitive_tc(((ValueHandlerImpl)paramValueHandler).getJavaCharTCKind());
    }
    if (paramClass == Boolean.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_boolean);
    }
    return paramORB.get_primitive_tc(TCKind.tk_any);
  }
  
  static
  {
    SharedSecrets.setJavaCorbaAccess(new JavaCorbaAccess()
    {
      public ValueHandlerImpl newValueHandlerImpl()
      {
        return ValueHandlerImpl.getInstance();
      }
      
      public Class<?> loadClass(String paramAnonymousString)
        throws ClassNotFoundException
      {
        if (Thread.currentThread().getContextClassLoader() != null) {
          return Thread.currentThread().getContextClassLoader().loadClass(paramAnonymousString);
        }
        return ClassLoader.getSystemClassLoader().loadClass(paramAnonymousString);
      }
    });
  }
  
  private static class IdentityKeyValueStack
  {
    Stack pairs = null;
    
    private IdentityKeyValueStack() {}
    
    Object get(Object paramObject)
    {
      if (pairs == null) {
        return null;
      }
      Iterator localIterator = pairs.iterator();
      while (localIterator.hasNext())
      {
        KeyValuePair localKeyValuePair = (KeyValuePair)localIterator.next();
        if (key == paramObject) {
          return value;
        }
      }
      return null;
    }
    
    void push(Object paramObject1, Object paramObject2)
    {
      if (pairs == null) {
        pairs = new Stack();
      }
      pairs.push(new KeyValuePair(paramObject1, paramObject2));
    }
    
    void pop()
    {
      pairs.pop();
    }
    
    private static class KeyValuePair
    {
      Object key;
      Object value;
      
      KeyValuePair(Object paramObject1, Object paramObject2)
      {
        key = paramObject1;
        value = paramObject2;
      }
      
      boolean equals(KeyValuePair paramKeyValuePair)
      {
        return key == key;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\io\ValueUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */