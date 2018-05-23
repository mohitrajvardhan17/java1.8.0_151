package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import com.sun.corba.se.impl.io.ValueUtility;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import org.omg.CORBA.ValueMember;

public class ObjectStreamClass_1_3_1
  implements Serializable
{
  public static final long kDefaultUID = -1L;
  private static Object[] noArgsList = new Object[0];
  private static Class<?>[] noTypesList = new Class[0];
  private static Hashtable translatedFields;
  private static ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];
  private String name;
  private ObjectStreamClass_1_3_1 superclass;
  private boolean serializable;
  private boolean externalizable;
  private ObjectStreamField[] fields;
  private Class<?> ofClass;
  boolean forProxyClass;
  private long suid = -1L;
  private String suidStr = null;
  private long actualSuid = -1L;
  private String actualSuidStr = null;
  int primBytes;
  int objFields;
  private Object lock = new Object();
  private boolean hasWriteObjectMethod;
  private boolean hasExternalizableBlockData;
  Method writeObjectMethod;
  Method readObjectMethod;
  private transient Method writeReplaceObjectMethod;
  private transient Method readResolveObjectMethod;
  private ObjectStreamClass_1_3_1 localClassDesc;
  private static final long serialVersionUID = -6120832682080437368L;
  public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
  private static Comparator compareClassByName = new CompareClassByName(null);
  private static Comparator compareMemberByName = new CompareMemberByName(null);
  
  static final ObjectStreamClass_1_3_1 lookup(Class<?> paramClass)
  {
    ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = lookupInternal(paramClass);
    if ((localObjectStreamClass_1_3_1.isSerializable()) || (localObjectStreamClass_1_3_1.isExternalizable())) {
      return localObjectStreamClass_1_3_1;
    }
    return null;
  }
  
  static ObjectStreamClass_1_3_1 lookupInternal(Class<?> paramClass)
  {
    ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_11 = null;
    synchronized (descriptorFor)
    {
      localObjectStreamClass_1_3_11 = findDescriptorFor(paramClass);
      if (localObjectStreamClass_1_3_11 != null) {
        return localObjectStreamClass_1_3_11;
      }
      boolean bool1 = Serializable.class.isAssignableFrom(paramClass);
      ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_12 = null;
      if (bool1)
      {
        Class localClass = paramClass.getSuperclass();
        if (localClass != null) {
          localObjectStreamClass_1_3_12 = lookup(localClass);
        }
      }
      boolean bool2 = false;
      if (bool1)
      {
        bool2 = ((localObjectStreamClass_1_3_12 != null) && (localObjectStreamClass_1_3_12.isExternalizable())) || (Externalizable.class.isAssignableFrom(paramClass));
        if (bool2) {
          bool1 = false;
        }
      }
      localObjectStreamClass_1_3_11 = new ObjectStreamClass_1_3_1(paramClass, localObjectStreamClass_1_3_12, bool1, bool2);
    }
    localObjectStreamClass_1_3_11.init();
    return localObjectStreamClass_1_3_11;
  }
  
  public final String getName()
  {
    return name;
  }
  
  public static final long getSerialVersionUID(Class<?> paramClass)
  {
    ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = lookup(paramClass);
    if (localObjectStreamClass_1_3_1 != null) {
      return localObjectStreamClass_1_3_1.getSerialVersionUID();
    }
    return 0L;
  }
  
  public final long getSerialVersionUID()
  {
    return suid;
  }
  
  public final String getSerialVersionUIDStr()
  {
    if (suidStr == null) {
      suidStr = Long.toHexString(suid).toUpperCase();
    }
    return suidStr;
  }
  
  public static final long getActualSerialVersionUID(Class<?> paramClass)
  {
    ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = lookup(paramClass);
    if (localObjectStreamClass_1_3_1 != null) {
      return localObjectStreamClass_1_3_1.getActualSerialVersionUID();
    }
    return 0L;
  }
  
  public final long getActualSerialVersionUID()
  {
    return actualSuid;
  }
  
  public final String getActualSerialVersionUIDStr()
  {
    if (actualSuidStr == null) {
      actualSuidStr = Long.toHexString(actualSuid).toUpperCase();
    }
    return actualSuidStr;
  }
  
  public final Class<?> forClass()
  {
    return ofClass;
  }
  
  public ObjectStreamField[] getFields()
  {
    if (fields.length > 0)
    {
      ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[fields.length];
      System.arraycopy(fields, 0, arrayOfObjectStreamField, 0, fields.length);
      return arrayOfObjectStreamField;
    }
    return fields;
  }
  
  public boolean hasField(ValueMember paramValueMember)
  {
    for (int i = 0; i < fields.length; i++) {
      try
      {
        if ((fields[i].getName().equals(name)) && (fields[i].getSignature().equals(ValueUtility.getSignature(paramValueMember)))) {
          return true;
        }
      }
      catch (Throwable localThrowable) {}
    }
    return false;
  }
  
  final ObjectStreamField[] getFieldsNoCopy()
  {
    return fields;
  }
  
  public final ObjectStreamField getField(String paramString)
  {
    for (int i = fields.length - 1; i >= 0; i--) {
      if (paramString.equals(fields[i].getName())) {
        return fields[i];
      }
    }
    return null;
  }
  
  public Serializable writeReplace(Serializable paramSerializable)
  {
    if (writeReplaceObjectMethod != null) {
      try
      {
        return (Serializable)writeReplaceObjectMethod.invoke(paramSerializable, noArgsList);
      }
      catch (Throwable localThrowable)
      {
        throw new RuntimeException(localThrowable.getMessage());
      }
    }
    return paramSerializable;
  }
  
  public Object readResolve(Object paramObject)
  {
    if (readResolveObjectMethod != null) {
      try
      {
        return readResolveObjectMethod.invoke(paramObject, noArgsList);
      }
      catch (Throwable localThrowable)
      {
        throw new RuntimeException(localThrowable.getMessage());
      }
    }
    return paramObject;
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(name);
    localStringBuffer.append(": static final long serialVersionUID = ");
    localStringBuffer.append(Long.toString(suid));
    localStringBuffer.append("L;");
    return localStringBuffer.toString();
  }
  
  private ObjectStreamClass_1_3_1(Class<?> paramClass, ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1, boolean paramBoolean1, boolean paramBoolean2)
  {
    ofClass = paramClass;
    if (Proxy.isProxyClass(paramClass)) {
      forProxyClass = true;
    }
    name = paramClass.getName();
    superclass = paramObjectStreamClass_1_3_1;
    serializable = paramBoolean1;
    if (!forProxyClass) {
      externalizable = paramBoolean2;
    }
    insertDescriptorFor(this);
  }
  
  private void init()
  {
    synchronized (lock)
    {
      final Class localClass = ofClass;
      if (fields != null) {
        return;
      }
      if ((!serializable) || (externalizable) || (forProxyClass) || (name.equals("java.lang.String")))
      {
        fields = NO_FIELDS;
      }
      else if (serializable)
      {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            try
            {
              Field localField1 = localClass.getDeclaredField("serialPersistentFields");
              localField1.setAccessible(true);
              java.io.ObjectStreamField[] arrayOfObjectStreamField = (java.io.ObjectStreamField[])localField1.get(localClass);
              int k = localField1.getModifiers();
              if ((Modifier.isPrivate(k)) && (Modifier.isStatic(k)) && (Modifier.isFinal(k))) {
                fields = ((ObjectStreamField[])ObjectStreamClass_1_3_1.translateFields((Object[])localField1.get(localClass)));
              }
            }
            catch (NoSuchFieldException localNoSuchFieldException1)
            {
              fields = null;
            }
            catch (IllegalAccessException localIllegalAccessException)
            {
              fields = null;
            }
            catch (IllegalArgumentException localIllegalArgumentException)
            {
              fields = null;
            }
            catch (ClassCastException localClassCastException)
            {
              fields = null;
            }
            if (fields == null)
            {
              Field[] arrayOfField = localClass.getDeclaredFields();
              int j = 0;
              ObjectStreamField[] arrayOfObjectStreamField1 = new ObjectStreamField[arrayOfField.length];
              for (int m = 0; m < arrayOfField.length; m++)
              {
                int n = arrayOfField[m].getModifiers();
                if ((!Modifier.isStatic(n)) && (!Modifier.isTransient(n))) {
                  arrayOfObjectStreamField1[(j++)] = new ObjectStreamField(arrayOfField[m]);
                }
              }
              fields = new ObjectStreamField[j];
              System.arraycopy(arrayOfObjectStreamField1, 0, fields, 0, j);
            }
            else
            {
              for (int i = fields.length - 1; i >= 0; i--) {
                try
                {
                  Field localField2 = localClass.getDeclaredField(fields[i].getName());
                  if (fields[i].getType() == localField2.getType()) {
                    fields[i].setField(localField2);
                  }
                }
                catch (NoSuchFieldException localNoSuchFieldException2) {}
              }
            }
            return null;
          }
        });
        if (fields.length > 1) {
          Arrays.sort(fields);
        }
        computeFieldInfo();
      }
      if (isNonSerializable()) {
        suid = 0L;
      } else {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            int i;
            if (forProxyClass) {
              suid = 0L;
            } else {
              try
              {
                Field localField = localClass.getDeclaredField("serialVersionUID");
                i = localField.getModifiers();
                if ((Modifier.isStatic(i)) && (Modifier.isFinal(i)))
                {
                  localField.setAccessible(true);
                  suid = localField.getLong(localClass);
                }
                else
                {
                  suid = ObjectStreamClass.getSerialVersionUID(localClass);
                }
              }
              catch (NoSuchFieldException localNoSuchFieldException)
              {
                suid = ObjectStreamClass.getSerialVersionUID(localClass);
              }
              catch (IllegalAccessException localIllegalAccessException)
              {
                suid = ObjectStreamClass.getSerialVersionUID(localClass);
              }
            }
            try
            {
              writeReplaceObjectMethod = localClass.getDeclaredMethod("writeReplace", ObjectStreamClass_1_3_1.noTypesList);
              if (Modifier.isStatic(writeReplaceObjectMethod.getModifiers())) {
                writeReplaceObjectMethod = null;
              } else {
                writeReplaceObjectMethod.setAccessible(true);
              }
            }
            catch (NoSuchMethodException localNoSuchMethodException1) {}
            try
            {
              readResolveObjectMethod = localClass.getDeclaredMethod("readResolve", ObjectStreamClass_1_3_1.noTypesList);
              if (Modifier.isStatic(readResolveObjectMethod.getModifiers())) {
                readResolveObjectMethod = null;
              } else {
                readResolveObjectMethod.setAccessible(true);
              }
            }
            catch (NoSuchMethodException localNoSuchMethodException2) {}
            if ((serializable) && (!forProxyClass))
            {
              try
              {
                Class[] arrayOfClass1 = { ObjectOutputStream.class };
                writeObjectMethod = localClass.getDeclaredMethod("writeObject", arrayOfClass1);
                hasWriteObjectMethod = true;
                i = writeObjectMethod.getModifiers();
                if ((!Modifier.isPrivate(i)) || (Modifier.isStatic(i)))
                {
                  writeObjectMethod = null;
                  hasWriteObjectMethod = false;
                }
              }
              catch (NoSuchMethodException localNoSuchMethodException3) {}
              try
              {
                Class[] arrayOfClass2 = { ObjectInputStream.class };
                readObjectMethod = localClass.getDeclaredMethod("readObject", arrayOfClass2);
                i = readObjectMethod.getModifiers();
                if ((!Modifier.isPrivate(i)) || (Modifier.isStatic(i))) {
                  readObjectMethod = null;
                }
              }
              catch (NoSuchMethodException localNoSuchMethodException4) {}
            }
            return null;
          }
        });
      }
      actualSuid = computeStructuralUID(this, localClass);
    }
  }
  
  ObjectStreamClass_1_3_1(String paramString, long paramLong)
  {
    name = paramString;
    suid = paramLong;
    superclass = null;
  }
  
  private static Object[] translateFields(Object[] paramArrayOfObject)
    throws NoSuchFieldException
  {
    try
    {
      java.io.ObjectStreamField[] arrayOfObjectStreamField = (java.io.ObjectStreamField[])paramArrayOfObject;
      Object[] arrayOfObject1 = null;
      if (translatedFields == null) {
        translatedFields = new Hashtable();
      }
      arrayOfObject1 = (Object[])translatedFields.get(arrayOfObjectStreamField);
      if (arrayOfObject1 != null) {
        return arrayOfObject1;
      }
      Class localClass = ObjectStreamField.class;
      arrayOfObject1 = (Object[])Array.newInstance(localClass, paramArrayOfObject.length);
      Object[] arrayOfObject2 = new Object[2];
      Class[] arrayOfClass = { String.class, Class.class };
      Constructor localConstructor = localClass.getDeclaredConstructor(arrayOfClass);
      for (int i = arrayOfObjectStreamField.length - 1; i >= 0; i--)
      {
        arrayOfObject2[0] = arrayOfObjectStreamField[i].getName();
        arrayOfObject2[1] = arrayOfObjectStreamField[i].getType();
        arrayOfObject1[i] = localConstructor.newInstance(arrayOfObject2);
      }
      translatedFields.put(arrayOfObjectStreamField, arrayOfObject1);
      return (Object[])arrayOfObject1;
    }
    catch (Throwable localThrowable)
    {
      throw new NoSuchFieldException();
    }
  }
  
  static boolean compareClassNames(String paramString1, String paramString2, char paramChar)
  {
    int i = paramString1.lastIndexOf(paramChar);
    if (i < 0) {
      i = 0;
    }
    int j = paramString2.lastIndexOf(paramChar);
    if (j < 0) {
      j = 0;
    }
    return paramString1.regionMatches(false, i, paramString2, j, paramString1.length() - i);
  }
  
  final boolean typeEquals(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
  {
    return (suid == suid) && (compareClassNames(name, name, '.'));
  }
  
  final void setSuperclass(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
  {
    superclass = paramObjectStreamClass_1_3_1;
  }
  
  final ObjectStreamClass_1_3_1 getSuperclass()
  {
    return superclass;
  }
  
  final boolean hasWriteObject()
  {
    return hasWriteObjectMethod;
  }
  
  final boolean isCustomMarshaled()
  {
    return (hasWriteObject()) || (isExternalizable());
  }
  
  boolean hasExternalizableBlockDataMode()
  {
    return hasExternalizableBlockData;
  }
  
  final ObjectStreamClass_1_3_1 localClassDescriptor()
  {
    return localClassDesc;
  }
  
  boolean isSerializable()
  {
    return serializable;
  }
  
  boolean isExternalizable()
  {
    return externalizable;
  }
  
  boolean isNonSerializable()
  {
    return (!externalizable) && (!serializable);
  }
  
  private void computeFieldInfo()
  {
    primBytes = 0;
    objFields = 0;
    for (int i = 0; i < fields.length; i++) {
      switch (fields[i].getTypeCode())
      {
      case 'B': 
      case 'Z': 
        primBytes += 1;
        break;
      case 'C': 
      case 'S': 
        primBytes += 2;
        break;
      case 'F': 
      case 'I': 
        primBytes += 4;
        break;
      case 'D': 
      case 'J': 
        primBytes += 8;
        break;
      case 'L': 
      case '[': 
        objFields += 1;
      }
    }
  }
  
  private static long computeStructuralUID(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1, Class<?> paramClass)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try
    {
      if ((!Serializable.class.isAssignableFrom(paramClass)) || (paramClass.isInterface())) {
        return 0L;
      }
      if (Externalizable.class.isAssignableFrom(paramClass)) {
        return 1L;
      }
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
      DigestOutputStream localDigestOutputStream = new DigestOutputStream(localByteArrayOutputStream, localMessageDigest);
      DataOutputStream localDataOutputStream = new DataOutputStream(localDigestOutputStream);
      Class localClass = paramClass.getSuperclass();
      if (localClass != null) {
        localDataOutputStream.writeLong(computeStructuralUID(lookup(localClass), localClass));
      }
      if (paramObjectStreamClass_1_3_1.hasWriteObject()) {
        localDataOutputStream.writeInt(2);
      } else {
        localDataOutputStream.writeInt(1);
      }
      ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass_1_3_1.getFields();
      int i = 0;
      for (int j = 0; j < arrayOfObjectStreamField.length; j++) {
        if (arrayOfObjectStreamField[j].getField() != null) {
          i++;
        }
      }
      Field[] arrayOfField = new Field[i];
      int k = 0;
      int m = 0;
      while (k < arrayOfObjectStreamField.length)
      {
        if (arrayOfObjectStreamField[k].getField() != null) {
          arrayOfField[(m++)] = arrayOfObjectStreamField[k].getField();
        }
        k++;
      }
      if (arrayOfField.length > 1) {
        Arrays.sort(arrayOfField, compareMemberByName);
      }
      for (k = 0; k < arrayOfField.length; k++)
      {
        Field localField = arrayOfField[k];
        int i1 = localField.getModifiers();
        localDataOutputStream.writeUTF(localField.getName());
        localDataOutputStream.writeUTF(getSignature(localField.getType()));
      }
      localDataOutputStream.flush();
      byte[] arrayOfByte = localMessageDigest.digest();
      for (int n = 0; n < Math.min(8, arrayOfByte.length); n++) {
        l += ((arrayOfByte[n] & 0xFF) << n * 8);
      }
    }
    catch (IOException localIOException)
    {
      l = -1L;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new SecurityException(localNoSuchAlgorithmException.getMessage());
    }
    return l;
  }
  
  static String getSignature(Class<?> paramClass)
  {
    String str = null;
    if (paramClass.isArray())
    {
      Object localObject = paramClass;
      int i = 0;
      while (((Class)localObject).isArray())
      {
        i++;
        localObject = ((Class)localObject).getComponentType();
      }
      StringBuffer localStringBuffer = new StringBuffer();
      for (int j = 0; j < i; j++) {
        localStringBuffer.append("[");
      }
      localStringBuffer.append(getSignature((Class)localObject));
      str = localStringBuffer.toString();
    }
    else if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        str = "I";
      } else if (paramClass == Byte.TYPE) {
        str = "B";
      } else if (paramClass == Long.TYPE) {
        str = "J";
      } else if (paramClass == Float.TYPE) {
        str = "F";
      } else if (paramClass == Double.TYPE) {
        str = "D";
      } else if (paramClass == Short.TYPE) {
        str = "S";
      } else if (paramClass == Character.TYPE) {
        str = "C";
      } else if (paramClass == Boolean.TYPE) {
        str = "Z";
      } else if (paramClass == Void.TYPE) {
        str = "V";
      }
    }
    else
    {
      str = "L" + paramClass.getName().replace('.', '/') + ";";
    }
    return str;
  }
  
  static String getSignature(Method paramMethod)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      localStringBuffer.append(getSignature(arrayOfClass[i]));
    }
    localStringBuffer.append(")");
    localStringBuffer.append(getSignature(paramMethod.getReturnType()));
    return localStringBuffer.toString();
  }
  
  static String getSignature(Constructor paramConstructor)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      localStringBuffer.append(getSignature(arrayOfClass[i]));
    }
    localStringBuffer.append(")V");
    return localStringBuffer.toString();
  }
  
  private static ObjectStreamClass_1_3_1 findDescriptorFor(Class<?> paramClass)
  {
    int i = paramClass.hashCode();
    int j = (i & 0x7FFFFFFF) % descriptorFor.length;
    ObjectStreamClassEntry localObjectStreamClassEntry1;
    while (((localObjectStreamClassEntry1 = descriptorFor[j]) != null) && (localObjectStreamClassEntry1.get() == null)) {
      descriptorFor[j] = next;
    }
    ObjectStreamClassEntry localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
    while (localObjectStreamClassEntry1 != null)
    {
      ObjectStreamClass_1_3_1 localObjectStreamClass_1_3_1 = (ObjectStreamClass_1_3_1)localObjectStreamClassEntry1.get();
      if (localObjectStreamClass_1_3_1 == null)
      {
        next = next;
      }
      else
      {
        if (ofClass == paramClass) {
          return localObjectStreamClass_1_3_1;
        }
        localObjectStreamClassEntry2 = localObjectStreamClassEntry1;
      }
      localObjectStreamClassEntry1 = next;
    }
    return null;
  }
  
  private static void insertDescriptorFor(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
  {
    if (findDescriptorFor(ofClass) != null) {
      return;
    }
    int i = ofClass.hashCode();
    int j = (i & 0x7FFFFFFF) % descriptorFor.length;
    ObjectStreamClassEntry localObjectStreamClassEntry = new ObjectStreamClassEntry(paramObjectStreamClass_1_3_1);
    next = descriptorFor[j];
    descriptorFor[j] = localObjectStreamClassEntry;
  }
  
  private static Field[] getDeclaredFields(Class paramClass)
  {
    (Field[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return val$clz.getDeclaredFields();
      }
    });
  }
  
  private static class CompareClassByName
    implements Comparator
  {
    private CompareClassByName() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      Class localClass1 = (Class)paramObject1;
      Class localClass2 = (Class)paramObject2;
      return localClass1.getName().compareTo(localClass2.getName());
    }
  }
  
  private static class CompareMemberByName
    implements Comparator
  {
    private CompareMemberByName() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      String str1 = ((Member)paramObject1).getName();
      String str2 = ((Member)paramObject2).getName();
      if ((paramObject1 instanceof Method))
      {
        str1 = str1 + ObjectStreamClass_1_3_1.getSignature((Method)paramObject1);
        str2 = str2 + ObjectStreamClass_1_3_1.getSignature((Method)paramObject2);
      }
      else if ((paramObject1 instanceof Constructor))
      {
        str1 = str1 + ObjectStreamClass_1_3_1.getSignature((Constructor)paramObject1);
        str2 = str2 + ObjectStreamClass_1_3_1.getSignature((Constructor)paramObject2);
      }
      return str1.compareTo(str2);
    }
  }
  
  private static class MethodSignature
    implements Comparator
  {
    Member member;
    String signature;
    
    static MethodSignature[] removePrivateAndSort(Member[] paramArrayOfMember)
    {
      int i = 0;
      for (int j = 0; j < paramArrayOfMember.length; j++) {
        if (!Modifier.isPrivate(paramArrayOfMember[j].getModifiers())) {
          i++;
        }
      }
      MethodSignature[] arrayOfMethodSignature = new MethodSignature[i];
      int k = 0;
      for (int m = 0; m < paramArrayOfMember.length; m++) {
        if (!Modifier.isPrivate(paramArrayOfMember[m].getModifiers()))
        {
          arrayOfMethodSignature[k] = new MethodSignature(paramArrayOfMember[m]);
          k++;
        }
      }
      if (k > 0) {
        Arrays.sort(arrayOfMethodSignature, arrayOfMethodSignature[0]);
      }
      return arrayOfMethodSignature;
    }
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      if (paramObject1 == paramObject2) {
        return 0;
      }
      MethodSignature localMethodSignature1 = (MethodSignature)paramObject1;
      MethodSignature localMethodSignature2 = (MethodSignature)paramObject2;
      int i;
      if (isConstructor())
      {
        i = signature.compareTo(signature);
      }
      else
      {
        i = member.getName().compareTo(member.getName());
        if (i == 0) {
          i = signature.compareTo(signature);
        }
      }
      return i;
    }
    
    private final boolean isConstructor()
    {
      return member instanceof Constructor;
    }
    
    private MethodSignature(Member paramMember)
    {
      member = paramMember;
      if (isConstructor()) {
        signature = ObjectStreamClass_1_3_1.getSignature((Constructor)paramMember);
      } else {
        signature = ObjectStreamClass_1_3_1.getSignature((Method)paramMember);
      }
    }
  }
  
  private static class ObjectStreamClassEntry
  {
    ObjectStreamClassEntry next;
    private ObjectStreamClass_1_3_1 c;
    
    ObjectStreamClassEntry(ObjectStreamClass_1_3_1 paramObjectStreamClass_1_3_1)
    {
      c = paramObjectStreamClass_1_3_1;
    }
    
    public Object get()
    {
      return c;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\ObjectStreamClass_1_3_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */