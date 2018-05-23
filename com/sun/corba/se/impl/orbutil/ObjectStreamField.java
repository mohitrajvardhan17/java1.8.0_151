package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Field;

class ObjectStreamField
  implements Comparable
{
  private String name;
  private char type;
  private Field field;
  private String typeString;
  private Class clazz;
  private String signature;
  private long fieldID = -1L;
  
  ObjectStreamField(String paramString, Class paramClass)
  {
    name = paramString;
    clazz = paramClass;
    if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        type = 'I';
      } else if (paramClass == Byte.TYPE) {
        type = 'B';
      } else if (paramClass == Long.TYPE) {
        type = 'J';
      } else if (paramClass == Float.TYPE) {
        type = 'F';
      } else if (paramClass == Double.TYPE) {
        type = 'D';
      } else if (paramClass == Short.TYPE) {
        type = 'S';
      } else if (paramClass == Character.TYPE) {
        type = 'C';
      } else if (paramClass == Boolean.TYPE) {
        type = 'Z';
      }
    }
    else if (paramClass.isArray())
    {
      type = '[';
      typeString = ObjectStreamClass_1_3_1.getSignature(paramClass);
    }
    else
    {
      type = 'L';
      typeString = ObjectStreamClass_1_3_1.getSignature(paramClass);
    }
    if (typeString != null) {
      signature = typeString;
    } else {
      signature = String.valueOf(type);
    }
  }
  
  ObjectStreamField(Field paramField)
  {
    this(paramField.getName(), paramField.getType());
    field = paramField;
  }
  
  ObjectStreamField(String paramString1, char paramChar, Field paramField, String paramString2)
  {
    name = paramString1;
    type = paramChar;
    field = paramField;
    typeString = paramString2;
    if (typeString != null) {
      signature = typeString;
    } else {
      signature = String.valueOf(type);
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public Class getType()
  {
    if (clazz != null) {
      return clazz;
    }
    switch (type)
    {
    case 'B': 
      clazz = Byte.TYPE;
      break;
    case 'C': 
      clazz = Character.TYPE;
      break;
    case 'S': 
      clazz = Short.TYPE;
      break;
    case 'I': 
      clazz = Integer.TYPE;
      break;
    case 'J': 
      clazz = Long.TYPE;
      break;
    case 'F': 
      clazz = Float.TYPE;
      break;
    case 'D': 
      clazz = Double.TYPE;
      break;
    case 'Z': 
      clazz = Boolean.TYPE;
      break;
    case 'L': 
    case '[': 
      clazz = Object.class;
    }
    return clazz;
  }
  
  public char getTypeCode()
  {
    return type;
  }
  
  public String getTypeString()
  {
    return typeString;
  }
  
  Field getField()
  {
    return field;
  }
  
  void setField(Field paramField)
  {
    field = paramField;
    fieldID = -1L;
  }
  
  ObjectStreamField() {}
  
  public boolean isPrimitive()
  {
    return (type != '[') && (type != 'L');
  }
  
  public int compareTo(Object paramObject)
  {
    ObjectStreamField localObjectStreamField = (ObjectStreamField)paramObject;
    int i = typeString == null ? 1 : 0;
    int j = typeString == null ? 1 : 0;
    if (i != j) {
      return i != 0 ? -1 : 1;
    }
    return name.compareTo(name);
  }
  
  public boolean typeEquals(ObjectStreamField paramObjectStreamField)
  {
    if ((paramObjectStreamField == null) || (type != type)) {
      return false;
    }
    if ((typeString == null) && (typeString == null)) {
      return true;
    }
    return ObjectStreamClass_1_3_1.compareClassNames(typeString, typeString, '/');
  }
  
  public String getSignature()
  {
    return signature;
  }
  
  public String toString()
  {
    if (typeString != null) {
      return typeString + " " + name;
    }
    return type + " " + name;
  }
  
  public Class getClazz()
  {
    return clazz;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\ObjectStreamField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */