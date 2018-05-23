package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;

public final class ObjectType
  extends ReferenceType
{
  private String class_name;
  
  public ObjectType(String paramString)
  {
    super((byte)14, "L" + paramString.replace('.', '/') + ";");
    class_name = paramString.replace('/', '.');
  }
  
  public String getClassName()
  {
    return class_name;
  }
  
  public int hashCode()
  {
    return class_name.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject instanceof ObjectType) ? class_name.equals(class_name) : false;
  }
  
  public boolean referencesClass()
  {
    JavaClass localJavaClass = Repository.lookupClass(class_name);
    if (localJavaClass == null) {
      return false;
    }
    return localJavaClass.isClass();
  }
  
  public boolean referencesInterface()
  {
    JavaClass localJavaClass = Repository.lookupClass(class_name);
    if (localJavaClass == null) {
      return false;
    }
    return !localJavaClass.isClass();
  }
  
  public boolean subclassOf(ObjectType paramObjectType)
  {
    if ((referencesInterface()) || (paramObjectType.referencesInterface())) {
      return false;
    }
    return Repository.instanceOf(class_name, class_name);
  }
  
  public boolean accessibleTo(ObjectType paramObjectType)
  {
    JavaClass localJavaClass1 = Repository.lookupClass(class_name);
    if (localJavaClass1.isPublic()) {
      return true;
    }
    JavaClass localJavaClass2 = Repository.lookupClass(class_name);
    return localJavaClass2.getPackageName().equals(localJavaClass1.getPackageName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ObjectType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */