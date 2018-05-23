package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.Remote;
import java.util.Hashtable;
import javax.rmi.CORBA.ClassDesc;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.ValueBase;

public class RepositoryId
{
  private static final byte[] IDL_IDENTIFIER_CHARS = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1 };
  private static final long serialVersionUID = 123456789L;
  private static String defaultServerURL = null;
  private static boolean useCodebaseOnly = false;
  private static IdentityHashtable classToRepStr;
  private static IdentityHashtable classIDLToRepStr;
  private static IdentityHashtable classSeqToRepStr;
  private static final IdentityHashtable repStrToByteArray;
  private static Hashtable repStrToClass;
  private String repId = null;
  private boolean isSupportedFormat = true;
  private String typeString = null;
  private String versionString = null;
  private boolean isSequence = false;
  private boolean isRMIValueType = false;
  private boolean isIDLType = false;
  private String completeClassName = null;
  private String unqualifiedName = null;
  private String definedInId = null;
  private Class clazz = null;
  private String suid = null;
  private String actualSuid = null;
  private long suidLong = -1L;
  private long actualSuidLong = -1L;
  private static final String kSequenceKeyword = "seq";
  private static final String kValuePrefix = "RMI:";
  private static final String kIDLPrefix = "IDL:";
  private static final String kIDLNamePrefix = "omg.org/";
  private static final String kIDLClassnamePrefix = "org.omg.";
  private static final String kSequencePrefix = "[";
  private static final String kCORBAPrefix = "CORBA/";
  private static final String kArrayPrefix = "RMI:[CORBA/";
  private static final int kValuePrefixLength;
  private static final int kIDLPrefixLength;
  private static final int kSequencePrefixLength;
  private static final String kInterfaceHashCode = ":0000000000000000";
  private static final String kInterfaceOnlyHashStr = "0000000000000000";
  private static final String kExternalizableHashStr = "0000000000000001";
  public static final int kInitialValueTag = 2147483392;
  public static final int kNoTypeInfo = 0;
  public static final int kSingleRepTypeInfo = 2;
  public static final int kPartialListTypeInfo = 6;
  public static final int kChunkedMask = 8;
  public static final int kPreComputed_StandardRMIUnchunked;
  public static final int kPreComputed_CodeBaseRMIUnchunked;
  public static final int kPreComputed_StandardRMIChunked;
  public static final int kPreComputed_CodeBaseRMIChunked;
  public static final int kPreComputed_StandardRMIUnchunked_NoRep;
  public static final int kPreComputed_CodeBaseRMIUnchunked_NoRep;
  public static final int kPreComputed_StandardRMIChunked_NoRep;
  public static final int kPreComputed_CodeBaseRMIChunked_NoRep;
  public static final String kWStringValueVersion = "1.0";
  public static final String kWStringValueHash = ":1.0";
  public static final String kWStringStubValue = "WStringValue";
  public static final String kWStringTypeStr = "omg.org/CORBA/WStringValue";
  public static final String kWStringValueRepID = "IDL:omg.org/CORBA/WStringValue:1.0";
  public static final String kAnyRepID = "IDL:omg.org/CORBA/Any";
  public static final String kClassDescValueHash;
  public static final String kClassDescStubValue = "ClassDesc";
  public static final String kClassDescTypeStr = "javax.rmi.CORBA.ClassDesc";
  public static final String kClassDescValueRepID;
  public static final String kObjectValueHash = ":1.0";
  public static final String kObjectStubValue = "Object";
  public static final String kSequenceValueHash = ":1.0";
  public static final String kPrimitiveSequenceValueHash = ":0000000000000000";
  public static final String kSerializableValueHash = ":1.0";
  public static final String kSerializableStubValue = "Serializable";
  public static final String kExternalizableValueHash = ":1.0";
  public static final String kExternalizableStubValue = "Externalizable";
  public static final String kRemoteValueHash = "";
  public static final String kRemoteStubValue = "";
  public static final String kRemoteTypeStr = "";
  public static final String kRemoteValueRepID = "";
  private static final Hashtable kSpecialArrayTypeStrings;
  private static final Hashtable kSpecialCasesRepIDs;
  private static final Hashtable kSpecialCasesStubValues;
  private static final Hashtable kSpecialCasesVersions;
  private static final Hashtable kSpecialCasesClasses;
  private static final Hashtable kSpecialCasesArrayPrefix;
  private static final Hashtable kSpecialPrimitives;
  private static final byte[] ASCII_HEX = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  public static final RepositoryIdCache cache = new RepositoryIdCache();
  public static final String kjava_rmi_Remote = createForAnyType(Remote.class);
  public static final String korg_omg_CORBA_Object = createForAnyType(org.omg.CORBA.Object.class);
  public static final Class[] kNoParamTypes = new Class[0];
  public static final Object[] kNoArgs = new Object[0];
  
  RepositoryId() {}
  
  RepositoryId(String paramString)
  {
    init(paramString);
  }
  
  RepositoryId init(String paramString)
  {
    repId = paramString;
    if (paramString.length() == 0)
    {
      clazz = Remote.class;
      typeString = "";
      isRMIValueType = true;
      suid = "0000000000000000";
      return this;
    }
    if (paramString.equals("IDL:omg.org/CORBA/WStringValue:1.0"))
    {
      clazz = String.class;
      typeString = "omg.org/CORBA/WStringValue";
      isIDLType = true;
      completeClassName = "java.lang.String";
      versionString = "1.0";
      return this;
    }
    String str = convertFromISOLatin1(paramString);
    int i = str.indexOf(':');
    if (i == -1) {
      throw new IllegalArgumentException("RepsitoryId must have the form <type>:<body>");
    }
    int j = str.indexOf(':', i + 1);
    if (j == -1) {
      versionString = "";
    } else {
      versionString = str.substring(j);
    }
    if (str.startsWith("IDL:"))
    {
      typeString = str.substring(kIDLPrefixLength, str.indexOf(':', kIDLPrefixLength));
      isIDLType = true;
      if (typeString.startsWith("omg.org/")) {
        completeClassName = ("org.omg." + typeString.substring("omg.org/".length()).replace('/', '.'));
      } else {
        completeClassName = typeString.replace('/', '.');
      }
    }
    else if (str.startsWith("RMI:"))
    {
      typeString = str.substring(kValuePrefixLength, str.indexOf(':', kValuePrefixLength));
      isRMIValueType = true;
      if (versionString.indexOf('.') == -1)
      {
        actualSuid = versionString.substring(1);
        suid = actualSuid;
        if (actualSuid.indexOf(':') != -1)
        {
          int k = actualSuid.indexOf(':') + 1;
          suid = actualSuid.substring(k);
          actualSuid = actualSuid.substring(0, k - 1);
        }
      }
    }
    else
    {
      isSupportedFormat = false;
      typeString = "";
    }
    if (typeString.startsWith("[")) {
      isSequence = true;
    }
    return this;
  }
  
  public final String getUnqualifiedName()
  {
    if (unqualifiedName == null)
    {
      String str = getClassName();
      int i = str.lastIndexOf('.');
      if (i == -1)
      {
        unqualifiedName = str;
        definedInId = "IDL::1.0";
      }
      else
      {
        unqualifiedName = str.substring(i);
        definedInId = ("IDL:" + str.substring(0, i).replace('.', '/') + ":1.0");
      }
    }
    return unqualifiedName;
  }
  
  public final String getDefinedInId()
  {
    if (definedInId == null) {
      getUnqualifiedName();
    }
    return definedInId;
  }
  
  public final String getTypeString()
  {
    return typeString;
  }
  
  public final String getVersionString()
  {
    return versionString;
  }
  
  public final String getSerialVersionUID()
  {
    return suid;
  }
  
  public final String getActualSerialVersionUID()
  {
    return actualSuid;
  }
  
  public final long getSerialVersionUIDAsLong()
  {
    return suidLong;
  }
  
  public final long getActualSerialVersionUIDAsLong()
  {
    return actualSuidLong;
  }
  
  public final boolean isRMIValueType()
  {
    return isRMIValueType;
  }
  
  public final boolean isIDLType()
  {
    return isIDLType;
  }
  
  public final String getRepositoryId()
  {
    return repId;
  }
  
  /* Error */
  public static byte[] getByteArray(String paramString)
  {
    // Byte code:
    //   0: getstatic 545	com/sun/corba/se/impl/util/RepositoryId:repStrToByteArray	Lcom/sun/corba/se/impl/util/IdentityHashtable;
    //   3: dup
    //   4: astore_1
    //   5: monitorenter
    //   6: getstatic 545	com/sun/corba/se/impl/util/RepositoryId:repStrToByteArray	Lcom/sun/corba/se/impl/util/IdentityHashtable;
    //   9: aload_0
    //   10: invokevirtual 574	com/sun/corba/se/impl/util/IdentityHashtable:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   13: checkcast 352	[B
    //   16: checkcast 352	[B
    //   19: aload_1
    //   20: monitorexit
    //   21: areturn
    //   22: astore_2
    //   23: aload_1
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	paramString	String
    //   4	20	1	Ljava/lang/Object;	Object
    //   22	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	21	22	finally
    //   22	25	22	finally
  }
  
  public static void setByteArray(String paramString, byte[] paramArrayOfByte)
  {
    synchronized (repStrToByteArray)
    {
      repStrToByteArray.put(paramString, paramArrayOfByte);
    }
  }
  
  public final boolean isSequence()
  {
    return isSequence;
  }
  
  public final boolean isSupportedFormat()
  {
    return isSupportedFormat;
  }
  
  public final String getClassName()
  {
    if (isRMIValueType) {
      return typeString;
    }
    if (isIDLType) {
      return completeClassName;
    }
    return null;
  }
  
  public final Class getAnyClassFromType()
    throws ClassNotFoundException
  {
    try
    {
      return getClassFromType();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Class localClass = (Class)repStrToClass.get(repId);
      if (localClass != null) {
        return localClass;
      }
      throw localClassNotFoundException;
    }
  }
  
  public final Class getClassFromType()
    throws ClassNotFoundException
  {
    if (clazz != null) {
      return clazz;
    }
    Class localClass = (Class)kSpecialCasesClasses.get(getClassName());
    if (localClass != null)
    {
      clazz = localClass;
      return localClass;
    }
    try
    {
      return Util.loadClass(getClassName(), null, null);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (defaultServerURL != null) {
        try
        {
          return getClassFromType(defaultServerURL);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          throw localClassNotFoundException;
        }
      }
      throw localClassNotFoundException;
    }
  }
  
  public final Class getClassFromType(Class paramClass, String paramString)
    throws ClassNotFoundException
  {
    if (clazz != null) {
      return clazz;
    }
    Class localClass = (Class)kSpecialCasesClasses.get(getClassName());
    if (localClass != null)
    {
      clazz = localClass;
      return localClass;
    }
    ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
    return Utility.loadClassOfType(getClassName(), paramString, localClassLoader, paramClass, localClassLoader);
  }
  
  public final Class getClassFromType(String paramString)
    throws ClassNotFoundException, MalformedURLException
  {
    return Util.loadClass(getClassName(), paramString, null);
  }
  
  public final String toString()
  {
    return repId;
  }
  
  public static boolean useFullValueDescription(Class paramClass, String paramString)
    throws IOException
  {
    String str = createForAnyType(paramClass);
    if (str.equals(paramString)) {
      return false;
    }
    RepositoryId localRepositoryId1;
    RepositoryId localRepositoryId2;
    synchronized (cache)
    {
      localRepositoryId1 = cache.getId(paramString);
      localRepositoryId2 = cache.getId(str);
    }
    if ((localRepositoryId1.isRMIValueType()) && (localRepositoryId2.isRMIValueType()))
    {
      if (!localRepositoryId1.getSerialVersionUID().equals(localRepositoryId2.getSerialVersionUID()))
      {
        ??? = "Mismatched serialization UIDs : Source (Rep. ID" + localRepositoryId2 + ") = " + localRepositoryId2.getSerialVersionUID() + " whereas Target (Rep. ID " + paramString + ") = " + localRepositoryId1.getSerialVersionUID();
        throw new IOException((String)???);
      }
      return true;
    }
    throw new IOException("The repository ID is not of an RMI value type (Expected ID = " + str + "; Received ID = " + paramString + ")");
  }
  
  private static String createHashString(Serializable paramSerializable)
  {
    return createHashString(paramSerializable.getClass());
  }
  
  private static String createHashString(Class paramClass)
  {
    if ((paramClass.isInterface()) || (!Serializable.class.isAssignableFrom(paramClass))) {
      return ":0000000000000000";
    }
    long l1 = ObjectStreamClass.getActualSerialVersionUID(paramClass);
    String str1 = null;
    if (l1 == 0L) {
      str1 = "0000000000000000";
    } else if (l1 == 1L) {
      str1 = "0000000000000001";
    }
    for (str1 = Long.toHexString(l1).toUpperCase(); str1.length() < 16; str1 = "0" + str1) {}
    long l2 = ObjectStreamClass.getSerialVersionUID(paramClass);
    String str2 = null;
    if (l2 == 0L) {
      str2 = "0000000000000000";
    } else if (l2 == 1L) {
      str2 = "0000000000000001";
    }
    for (str2 = Long.toHexString(l2).toUpperCase(); str2.length() < 16; str2 = "0" + str2) {}
    str1 = str1 + ":" + str2;
    return ":" + str1;
  }
  
  public static String createSequenceRepID(Object paramObject)
  {
    return createSequenceRepID(paramObject.getClass());
  }
  
  public static String createSequenceRepID(Class paramClass)
  {
    synchronized (classSeqToRepStr)
    {
      String str = (String)classSeqToRepStr.get(paramClass);
      if (str != null) {
        return str;
      }
      Class localClass1 = paramClass;
      Class localClass2 = null;
      int i = 0;
      while ((localClass2 = paramClass.getComponentType()) != null)
      {
        i++;
        paramClass = localClass2;
      }
      if (paramClass.isPrimitive())
      {
        str = "RMI:" + localClass1.getName() + ":0000000000000000";
      }
      else
      {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append("RMI:");
        while (i-- > 0) {
          localStringBuffer.append("[");
        }
        localStringBuffer.append("L");
        localStringBuffer.append(convertToISOLatin1(paramClass.getName()));
        localStringBuffer.append(";");
        localStringBuffer.append(createHashString(paramClass));
        str = localStringBuffer.toString();
      }
      classSeqToRepStr.put(localClass1, str);
      return str;
    }
  }
  
  public static String createForSpecialCase(Class paramClass)
  {
    if (paramClass.isArray()) {
      return createSequenceRepID(paramClass);
    }
    return (String)kSpecialCasesRepIDs.get(paramClass);
  }
  
  public static String createForSpecialCase(Serializable paramSerializable)
  {
    Class localClass = paramSerializable.getClass();
    if (localClass.isArray()) {
      return createSequenceRepID(paramSerializable);
    }
    return createForSpecialCase(localClass);
  }
  
  public static String createForJavaType(Serializable paramSerializable)
    throws TypeMismatchException
  {
    synchronized (classToRepStr)
    {
      String str = createForSpecialCase(paramSerializable);
      if (str != null) {
        return str;
      }
      Class localClass = paramSerializable.getClass();
      str = (String)classToRepStr.get(localClass);
      if (str != null) {
        return str;
      }
      str = "RMI:" + convertToISOLatin1(localClass.getName()) + createHashString(localClass);
      classToRepStr.put(localClass, str);
      repStrToClass.put(str, localClass);
      return str;
    }
  }
  
  public static String createForJavaType(Class paramClass)
    throws TypeMismatchException
  {
    synchronized (classToRepStr)
    {
      String str = createForSpecialCase(paramClass);
      if (str != null) {
        return str;
      }
      str = (String)classToRepStr.get(paramClass);
      if (str != null) {
        return str;
      }
      str = "RMI:" + convertToISOLatin1(paramClass.getName()) + createHashString(paramClass);
      classToRepStr.put(paramClass, str);
      repStrToClass.put(str, paramClass);
      return str;
    }
  }
  
  public static String createForIDLType(Class paramClass, int paramInt1, int paramInt2)
    throws TypeMismatchException
  {
    synchronized (classIDLToRepStr)
    {
      String str = (String)classIDLToRepStr.get(paramClass);
      if (str != null) {
        return str;
      }
      str = "IDL:" + convertToISOLatin1(paramClass.getName()).replace('.', '/') + ":" + paramInt1 + "." + paramInt2;
      classIDLToRepStr.put(paramClass, str);
      return str;
    }
  }
  
  private static String getIdFromHelper(Class paramClass)
  {
    try
    {
      Class localClass = Utility.loadClassForClass(paramClass.getName() + "Helper", null, paramClass.getClassLoader(), paramClass, paramClass.getClassLoader());
      Method localMethod = localClass.getDeclaredMethod("id", kNoParamTypes);
      return (String)localMethod.invoke(null, kNoArgs);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new MARSHAL(localClassNotFoundException.toString());
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new MARSHAL(localNoSuchMethodException.toString());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new MARSHAL(localInvocationTargetException.toString());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new MARSHAL(localIllegalAccessException.toString());
    }
  }
  
  public static String createForAnyType(Class paramClass)
  {
    try
    {
      if (paramClass.isArray()) {
        return createSequenceRepID(paramClass);
      }
      if (IDLEntity.class.isAssignableFrom(paramClass)) {
        try
        {
          return getIdFromHelper(paramClass);
        }
        catch (Throwable localThrowable)
        {
          return createForIDLType(paramClass, 1, 0);
        }
      }
      return createForJavaType(paramClass);
    }
    catch (TypeMismatchException localTypeMismatchException) {}
    return null;
  }
  
  public static boolean isAbstractBase(Class paramClass)
  {
    return (paramClass.isInterface()) && (IDLEntity.class.isAssignableFrom(paramClass)) && (!ValueBase.class.isAssignableFrom(paramClass)) && (!org.omg.CORBA.Object.class.isAssignableFrom(paramClass));
  }
  
  public static boolean isAnyRequired(Class paramClass)
  {
    return (paramClass == Object.class) || (paramClass == Serializable.class) || (paramClass == Externalizable.class);
  }
  
  public static long fromHex(String paramString)
  {
    if (paramString.startsWith("0x")) {
      return Long.valueOf(paramString.substring(2), 16).longValue();
    }
    return Long.valueOf(paramString, 16).longValue();
  }
  
  public static String convertToISOLatin1(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return paramString;
    }
    StringBuffer localStringBuffer = null;
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if ((k > 255) || (IDL_IDENTIFIER_CHARS[k] == 0))
      {
        if (localStringBuffer == null) {
          localStringBuffer = new StringBuffer(paramString.substring(0, j));
        }
        localStringBuffer.append("\\U" + (char)ASCII_HEX[((k & 0xF000) >>> 12)] + (char)ASCII_HEX[((k & 0xF00) >>> 8)] + (char)ASCII_HEX[((k & 0xF0) >>> 4)] + (char)ASCII_HEX[(k & 0xF)]);
      }
      else if (localStringBuffer != null)
      {
        localStringBuffer.append(k);
      }
    }
    if (localStringBuffer != null) {
      paramString = localStringBuffer.toString();
    }
    return paramString;
  }
  
  private static String convertFromISOLatin1(String paramString)
  {
    int i = -1;
    StringBuffer localStringBuffer = new StringBuffer(paramString);
    while ((i = localStringBuffer.toString().indexOf("\\U")) != -1)
    {
      String str = "0000" + localStringBuffer.toString().substring(i + 2, i + 6);
      byte[] arrayOfByte = new byte[(str.length() - 4) / 2];
      int j = 4;
      for (int k = 0; j < str.length(); k++)
      {
        arrayOfByte[k] = ((byte)(Utility.hexOf(str.charAt(j)) << 4 & 0xF0));
        int tmp111_109 = k;
        byte[] tmp111_107 = arrayOfByte;
        tmp111_107[tmp111_109] = ((byte)(tmp111_107[tmp111_109] | (byte)(Utility.hexOf(str.charAt(j + 1)) << 0 & 0xF)));
        j += 2;
      }
      localStringBuffer = new StringBuffer(delete(localStringBuffer.toString(), i, i + 6));
      localStringBuffer.insert(i, (char)arrayOfByte[1]);
    }
    return localStringBuffer.toString();
  }
  
  private static String delete(String paramString, int paramInt1, int paramInt2)
  {
    return paramString.substring(0, paramInt1) + paramString.substring(paramInt2, paramString.length());
  }
  
  private static String replace(String paramString1, String paramString2, String paramString3)
  {
    int i = 0;
    for (i = paramString1.indexOf(paramString2); i != -1; i = paramString1.indexOf(paramString2))
    {
      String str1 = paramString1.substring(0, i);
      String str2 = paramString1.substring(i + paramString2.length());
      paramString1 = new String(str1 + paramString3 + str2);
    }
    return paramString1;
  }
  
  public static int computeValueTag(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    int i = 2147483392;
    if (paramBoolean1) {
      i |= 0x1;
    }
    i |= paramInt;
    if (paramBoolean2) {
      i |= 0x8;
    }
    return i;
  }
  
  public static boolean isCodeBasePresent(int paramInt)
  {
    return (paramInt & 0x1) == 1;
  }
  
  public static int getTypeInfo(int paramInt)
  {
    return paramInt & 0x6;
  }
  
  public static boolean isChunkedEncoding(int paramInt)
  {
    return (paramInt & 0x8) != 0;
  }
  
  public static String getServerURL()
  {
    return defaultServerURL;
  }
  
  static
  {
    if (defaultServerURL == null) {
      defaultServerURL = JDKBridge.getLocalCodebase();
    }
    useCodebaseOnly = JDKBridge.useCodebaseOnly();
    classToRepStr = new IdentityHashtable();
    classIDLToRepStr = new IdentityHashtable();
    classSeqToRepStr = new IdentityHashtable();
    repStrToByteArray = new IdentityHashtable();
    repStrToClass = new Hashtable();
    kValuePrefixLength = "RMI:".length();
    kIDLPrefixLength = "IDL:".length();
    kSequencePrefixLength = "[".length();
    kPreComputed_StandardRMIUnchunked = computeValueTag(false, 2, false);
    kPreComputed_CodeBaseRMIUnchunked = computeValueTag(true, 2, false);
    kPreComputed_StandardRMIChunked = computeValueTag(false, 2, true);
    kPreComputed_CodeBaseRMIChunked = computeValueTag(true, 2, true);
    kPreComputed_StandardRMIUnchunked_NoRep = computeValueTag(false, 0, false);
    kPreComputed_CodeBaseRMIUnchunked_NoRep = computeValueTag(true, 0, false);
    kPreComputed_StandardRMIChunked_NoRep = computeValueTag(false, 0, true);
    kPreComputed_CodeBaseRMIChunked_NoRep = computeValueTag(true, 0, true);
    kClassDescValueHash = ":" + Long.toHexString(ObjectStreamClass.getActualSerialVersionUID(ClassDesc.class)).toUpperCase() + ":" + Long.toHexString(ObjectStreamClass.getSerialVersionUID(ClassDesc.class)).toUpperCase();
    kClassDescValueRepID = "RMI:javax.rmi.CORBA.ClassDesc" + kClassDescValueHash;
    kSpecialArrayTypeStrings = new Hashtable();
    kSpecialArrayTypeStrings.put("CORBA.WStringValue", new StringBuffer(String.class.getName()));
    kSpecialArrayTypeStrings.put("javax.rmi.CORBA.ClassDesc", new StringBuffer(Class.class.getName()));
    kSpecialArrayTypeStrings.put("CORBA.Object", new StringBuffer(Remote.class.getName()));
    kSpecialCasesRepIDs = new Hashtable();
    kSpecialCasesRepIDs.put(String.class, "IDL:omg.org/CORBA/WStringValue:1.0");
    kSpecialCasesRepIDs.put(Class.class, kClassDescValueRepID);
    kSpecialCasesRepIDs.put(Remote.class, "");
    kSpecialCasesStubValues = new Hashtable();
    kSpecialCasesStubValues.put(String.class, "WStringValue");
    kSpecialCasesStubValues.put(Class.class, "ClassDesc");
    kSpecialCasesStubValues.put(Object.class, "Object");
    kSpecialCasesStubValues.put(Serializable.class, "Serializable");
    kSpecialCasesStubValues.put(Externalizable.class, "Externalizable");
    kSpecialCasesStubValues.put(Remote.class, "");
    kSpecialCasesVersions = new Hashtable();
    kSpecialCasesVersions.put(String.class, ":1.0");
    kSpecialCasesVersions.put(Class.class, kClassDescValueHash);
    kSpecialCasesVersions.put(Object.class, ":1.0");
    kSpecialCasesVersions.put(Serializable.class, ":1.0");
    kSpecialCasesVersions.put(Externalizable.class, ":1.0");
    kSpecialCasesVersions.put(Remote.class, "");
    kSpecialCasesClasses = new Hashtable();
    kSpecialCasesClasses.put("omg.org/CORBA/WStringValue", String.class);
    kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
    kSpecialCasesClasses.put("", Remote.class);
    kSpecialCasesClasses.put("org.omg.CORBA.WStringValue", String.class);
    kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
    kSpecialCasesArrayPrefix = new Hashtable();
    kSpecialCasesArrayPrefix.put(String.class, "RMI:[CORBA/");
    kSpecialCasesArrayPrefix.put(Class.class, "RMI:[javax/rmi/CORBA/");
    kSpecialCasesArrayPrefix.put(Object.class, "RMI:[java/lang/");
    kSpecialCasesArrayPrefix.put(Serializable.class, "RMI:[java/io/");
    kSpecialCasesArrayPrefix.put(Externalizable.class, "RMI:[java/io/");
    kSpecialCasesArrayPrefix.put(Remote.class, "RMI:[CORBA/");
    kSpecialPrimitives = new Hashtable();
    kSpecialPrimitives.put("int", "long");
    kSpecialPrimitives.put("long", "longlong");
    kSpecialPrimitives.put("byte", "octet");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\RepositoryId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */