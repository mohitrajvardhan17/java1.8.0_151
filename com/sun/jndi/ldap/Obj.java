package com.sun.jndi.ldap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.spi.DirStateFactory.Result;
import javax.naming.spi.DirectoryManager;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

final class Obj
{
  static VersionHelper helper = ;
  static final String[] JAVA_ATTRIBUTES = { "objectClass", "javaSerializedData", "javaClassName", "javaFactory", "javaCodeBase", "javaReferenceAddress", "javaClassNames", "javaRemoteLocation" };
  static final int OBJECT_CLASS = 0;
  static final int SERIALIZED_DATA = 1;
  static final int CLASSNAME = 2;
  static final int FACTORY = 3;
  static final int CODEBASE = 4;
  static final int REF_ADDR = 5;
  static final int TYPENAME = 6;
  @Deprecated
  private static final int REMOTE_LOC = 7;
  static final String[] JAVA_OBJECT_CLASSES = { "javaContainer", "javaObject", "javaNamingReference", "javaSerializedObject", "javaMarshalledObject" };
  static final String[] JAVA_OBJECT_CLASSES_LOWER = { "javacontainer", "javaobject", "javanamingreference", "javaserializedobject", "javamarshalledobject" };
  static final int STRUCTURAL = 0;
  static final int BASE_OBJECT = 1;
  static final int REF_OBJECT = 2;
  static final int SER_OBJECT = 3;
  static final int MAR_OBJECT = 4;
  
  private Obj() {}
  
  private static Attributes encodeObject(char paramChar, Object paramObject, Attributes paramAttributes, Attribute paramAttribute, boolean paramBoolean)
    throws NamingException
  {
    int i = (paramAttribute.size() == 0) || ((paramAttribute.size() == 1) && (paramAttribute.contains("top"))) ? 1 : 0;
    if (i != 0) {
      paramAttribute.add(JAVA_OBJECT_CLASSES[0]);
    }
    if ((paramObject instanceof Referenceable))
    {
      paramAttribute.add(JAVA_OBJECT_CLASSES[1]);
      paramAttribute.add(JAVA_OBJECT_CLASSES[2]);
      if (!paramBoolean) {
        paramAttributes = (Attributes)paramAttributes.clone();
      }
      paramAttributes.put(paramAttribute);
      return encodeReference(paramChar, ((Referenceable)paramObject).getReference(), paramAttributes, paramObject);
    }
    if ((paramObject instanceof Reference))
    {
      paramAttribute.add(JAVA_OBJECT_CLASSES[1]);
      paramAttribute.add(JAVA_OBJECT_CLASSES[2]);
      if (!paramBoolean) {
        paramAttributes = (Attributes)paramAttributes.clone();
      }
      paramAttributes.put(paramAttribute);
      return encodeReference(paramChar, (Reference)paramObject, paramAttributes, null);
    }
    if ((paramObject instanceof Serializable))
    {
      paramAttribute.add(JAVA_OBJECT_CLASSES[1]);
      if ((!paramAttribute.contains(JAVA_OBJECT_CLASSES[4])) && (!paramAttribute.contains(JAVA_OBJECT_CLASSES_LOWER[4]))) {
        paramAttribute.add(JAVA_OBJECT_CLASSES[3]);
      }
      if (!paramBoolean) {
        paramAttributes = (Attributes)paramAttributes.clone();
      }
      paramAttributes.put(paramAttribute);
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[1], serializeObject(paramObject)));
      if (paramAttributes.get(JAVA_ATTRIBUTES[2]) == null) {
        paramAttributes.put(JAVA_ATTRIBUTES[2], paramObject.getClass().getName());
      }
      if (paramAttributes.get(JAVA_ATTRIBUTES[6]) == null)
      {
        Attribute localAttribute = LdapCtxFactory.createTypeNameAttr(paramObject.getClass());
        if (localAttribute != null) {
          paramAttributes.put(localAttribute);
        }
      }
    }
    else if (!(paramObject instanceof DirContext))
    {
      throw new IllegalArgumentException("can only bind Referenceable, Serializable, DirContext");
    }
    return paramAttributes;
  }
  
  private static String[] getCodebases(Attribute paramAttribute)
    throws NamingException
  {
    if (paramAttribute == null) {
      return null;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer((String)paramAttribute.get());
    Vector localVector = new Vector(10);
    while (localStringTokenizer.hasMoreTokens()) {
      localVector.addElement(localStringTokenizer.nextToken());
    }
    String[] arrayOfString = new String[localVector.size()];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = ((String)localVector.elementAt(i));
    }
    return arrayOfString;
  }
  
  static Object decodeObject(Attributes paramAttributes)
    throws NamingException
  {
    String[] arrayOfString = getCodebases(paramAttributes.get(JAVA_ATTRIBUTES[4]));
    try
    {
      if ((localAttribute = paramAttributes.get(JAVA_ATTRIBUTES[1])) != null)
      {
        ClassLoader localClassLoader = helper.getURLClassLoader(arrayOfString);
        return deserializeObject((byte[])localAttribute.get(), localClassLoader);
      }
      if ((localAttribute = paramAttributes.get(JAVA_ATTRIBUTES[7])) != null) {
        return decodeRmiObject((String)paramAttributes.get(JAVA_ATTRIBUTES[2]).get(), (String)localAttribute.get(), arrayOfString);
      }
      Attribute localAttribute = paramAttributes.get(JAVA_ATTRIBUTES[0]);
      if ((localAttribute != null) && ((localAttribute.contains(JAVA_OBJECT_CLASSES[2])) || (localAttribute.contains(JAVA_OBJECT_CLASSES_LOWER[2])))) {
        return decodeReference(paramAttributes, arrayOfString);
      }
      return null;
    }
    catch (IOException localIOException)
    {
      NamingException localNamingException = new NamingException();
      localNamingException.setRootCause(localIOException);
      throw localNamingException;
    }
  }
  
  private static Attributes encodeReference(char paramChar, Reference paramReference, Attributes paramAttributes, Object paramObject)
    throws NamingException
  {
    if (paramReference == null) {
      return paramAttributes;
    }
    String str;
    if ((str = paramReference.getClassName()) != null) {
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[2], str));
    }
    if ((str = paramReference.getFactoryClassName()) != null) {
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[3], str));
    }
    if ((str = paramReference.getFactoryClassLocation()) != null) {
      paramAttributes.put(new BasicAttribute(JAVA_ATTRIBUTES[4], str));
    }
    if ((paramObject != null) && (paramAttributes.get(JAVA_ATTRIBUTES[6]) != null))
    {
      Attribute localAttribute = LdapCtxFactory.createTypeNameAttr(paramObject.getClass());
      if (localAttribute != null) {
        paramAttributes.put(localAttribute);
      }
    }
    int i = paramReference.size();
    if (i > 0)
    {
      BasicAttribute localBasicAttribute = new BasicAttribute(JAVA_ATTRIBUTES[5]);
      BASE64Encoder localBASE64Encoder = null;
      for (int j = 0; j < i; j++)
      {
        RefAddr localRefAddr = paramReference.get(j);
        if ((localRefAddr instanceof StringRefAddr))
        {
          localBasicAttribute.add("" + paramChar + j + paramChar + localRefAddr.getType() + paramChar + localRefAddr.getContent());
        }
        else
        {
          if (localBASE64Encoder == null) {
            localBASE64Encoder = new BASE64Encoder();
          }
          localBasicAttribute.add("" + paramChar + j + paramChar + localRefAddr.getType() + paramChar + paramChar + localBASE64Encoder.encodeBuffer(serializeObject(localRefAddr)));
        }
      }
      paramAttributes.put(localBasicAttribute);
    }
    return paramAttributes;
  }
  
  private static Object decodeRmiObject(String paramString1, String paramString2, String[] paramArrayOfString)
    throws NamingException
  {
    return new Reference(paramString1, new StringRefAddr("URL", paramString2));
  }
  
  private static Reference decodeReference(Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException, IOException
  {
    String str2 = null;
    Attribute localAttribute;
    String str1;
    if ((localAttribute = paramAttributes.get(JAVA_ATTRIBUTES[2])) != null) {
      str1 = (String)localAttribute.get();
    } else {
      throw new InvalidAttributesException(JAVA_ATTRIBUTES[2] + " attribute is required");
    }
    if ((localAttribute = paramAttributes.get(JAVA_ATTRIBUTES[3])) != null) {
      str2 = (String)localAttribute.get();
    }
    Reference localReference = new Reference(str1, str2, paramArrayOfString != null ? paramArrayOfString[0] : null);
    if ((localAttribute = paramAttributes.get(JAVA_ATTRIBUTES[5])) != null)
    {
      BASE64Decoder localBASE64Decoder = null;
      ClassLoader localClassLoader = helper.getURLClassLoader(paramArrayOfString);
      Vector localVector = new Vector();
      localVector.setSize(localAttribute.size());
      NamingEnumeration localNamingEnumeration = localAttribute.getAll();
      while (localNamingEnumeration.hasMore())
      {
        String str3 = (String)localNamingEnumeration.next();
        if (str3.length() == 0) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty attribute value");
        }
        char c = str3.charAt(0);
        int i = 1;
        int j;
        if ((j = str3.indexOf(c, i)) < 0) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - separator '" + c + "'not found");
        }
        String str4;
        if ((str4 = str3.substring(i, j)) == null) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr position");
        }
        int k;
        try
        {
          k = Integer.parseInt(str4);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - RefAddr position not an integer");
        }
        i = j + 1;
        if ((j = str3.indexOf(c, i)) < 0) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - RefAddr type not found");
        }
        String str5;
        if ((str5 = str3.substring(i, j)) == null) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[5] + " attribute - empty RefAddr type");
        }
        i = j + 1;
        if (i == str3.length())
        {
          localVector.setElementAt(new StringRefAddr(str5, null), k);
        }
        else if (str3.charAt(i) == c)
        {
          i++;
          if (localBASE64Decoder == null) {
            localBASE64Decoder = new BASE64Decoder();
          }
          RefAddr localRefAddr = (RefAddr)deserializeObject(localBASE64Decoder.decodeBuffer(str3.substring(i)), localClassLoader);
          localVector.setElementAt(localRefAddr, k);
        }
        else
        {
          localVector.setElementAt(new StringRefAddr(str5, str3.substring(i)), k);
        }
      }
      for (int m = 0; m < localVector.size(); m++) {
        localReference.add((RefAddr)localVector.elementAt(m));
      }
    }
    return localReference;
  }
  
  private static byte[] serializeObject(Object paramObject)
    throws NamingException
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localObject1 = new ObjectOutputStream(localByteArrayOutputStream);
      Object localObject2 = null;
      try
      {
        ((ObjectOutputStream)localObject1).writeObject(paramObject);
      }
      catch (Throwable localThrowable2)
      {
        localObject2 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localObject1 != null) {
          if (localObject2 != null) {
            try
            {
              ((ObjectOutputStream)localObject1).close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject2).addSuppressed(localThrowable3);
            }
          } else {
            ((ObjectOutputStream)localObject1).close();
          }
        }
      }
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException)
    {
      Object localObject1 = new NamingException();
      ((NamingException)localObject1).setRootCause(localIOException);
      throw ((Throwable)localObject1);
    }
  }
  
  /* Error */
  private static Object deserializeObject(byte[] paramArrayOfByte, ClassLoader paramClassLoader)
    throws NamingException
  {
    // Byte code:
    //   0: new 222	java/io/ByteArrayInputStream
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 391	java/io/ByteArrayInputStream:<init>	([B)V
    //   8: astore_2
    //   9: aload_1
    //   10: ifnonnull +14 -> 24
    //   13: new 225	java/io/ObjectInputStream
    //   16: dup
    //   17: aload_2
    //   18: invokespecial 395	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   21: goto +12 -> 33
    //   24: new 220	com/sun/jndi/ldap/Obj$LoaderInputStream
    //   27: dup
    //   28: aload_2
    //   29: aload_1
    //   30: invokespecial 388	com/sun/jndi/ldap/Obj$LoaderInputStream:<init>	(Ljava/io/InputStream;Ljava/lang/ClassLoader;)V
    //   33: astore_3
    //   34: aconst_null
    //   35: astore 4
    //   37: aload_3
    //   38: invokevirtual 396	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   41: astore 5
    //   43: aload_3
    //   44: ifnull +31 -> 75
    //   47: aload 4
    //   49: ifnull +22 -> 71
    //   52: aload_3
    //   53: invokevirtual 394	java/io/ObjectInputStream:close	()V
    //   56: goto +19 -> 75
    //   59: astore 6
    //   61: aload 4
    //   63: aload 6
    //   65: invokevirtual 416	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   68: goto +7 -> 75
    //   71: aload_3
    //   72: invokevirtual 394	java/io/ObjectInputStream:close	()V
    //   75: aload 5
    //   77: areturn
    //   78: astore 5
    //   80: aload 5
    //   82: astore 4
    //   84: aload 5
    //   86: athrow
    //   87: astore 7
    //   89: aload_3
    //   90: ifnull +31 -> 121
    //   93: aload 4
    //   95: ifnull +22 -> 117
    //   98: aload_3
    //   99: invokevirtual 394	java/io/ObjectInputStream:close	()V
    //   102: goto +19 -> 121
    //   105: astore 8
    //   107: aload 4
    //   109: aload 8
    //   111: invokevirtual 416	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   114: goto +7 -> 121
    //   117: aload_3
    //   118: invokevirtual 394	java/io/ObjectInputStream:close	()V
    //   121: aload 7
    //   123: athrow
    //   124: astore_3
    //   125: new 241	javax/naming/NamingException
    //   128: dup
    //   129: invokespecial 427	javax/naming/NamingException:<init>	()V
    //   132: astore 4
    //   134: aload 4
    //   136: aload_3
    //   137: invokevirtual 428	javax/naming/NamingException:setRootCause	(Ljava/lang/Throwable;)V
    //   140: aload 4
    //   142: athrow
    //   143: astore_2
    //   144: new 241	javax/naming/NamingException
    //   147: dup
    //   148: invokespecial 427	javax/naming/NamingException:<init>	()V
    //   151: astore_3
    //   152: aload_3
    //   153: aload_2
    //   154: invokevirtual 428	javax/naming/NamingException:setRootCause	(Ljava/lang/Throwable;)V
    //   157: aload_3
    //   158: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	159	0	paramArrayOfByte	byte[]
    //   0	159	1	paramClassLoader	ClassLoader
    //   8	21	2	localByteArrayInputStream	java.io.ByteArrayInputStream
    //   143	11	2	localIOException	IOException
    //   33	85	3	localLoaderInputStream	LoaderInputStream
    //   124	13	3	localClassNotFoundException	ClassNotFoundException
    //   151	7	3	localNamingException	NamingException
    //   35	106	4	localObject1	Object
    //   41	35	5	localObject2	Object
    //   78	7	5	localThrowable1	Throwable
    //   59	5	6	localThrowable2	Throwable
    //   87	35	7	localObject3	Object
    //   105	5	8	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   52	56	59	java/lang/Throwable
    //   37	43	78	java/lang/Throwable
    //   37	43	87	finally
    //   78	89	87	finally
    //   98	102	105	java/lang/Throwable
    //   9	75	124	java/lang/ClassNotFoundException
    //   78	124	124	java/lang/ClassNotFoundException
    //   0	75	143	java/io/IOException
    //   78	143	143	java/io/IOException
  }
  
  static Attributes determineBindAttrs(char paramChar, Object paramObject, Attributes paramAttributes, boolean paramBoolean, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    DirStateFactory.Result localResult = DirectoryManager.getStateToBind(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
    paramObject = localResult.getObject();
    paramAttributes = localResult.getAttributes();
    if (paramObject == null) {
      return paramAttributes;
    }
    if ((paramAttributes == null) && ((paramObject instanceof DirContext)))
    {
      paramBoolean = true;
      paramAttributes = ((DirContext)paramObject).getAttributes("");
    }
    int i = 0;
    Object localObject;
    if ((paramAttributes == null) || (paramAttributes.size() == 0))
    {
      paramAttributes = new BasicAttributes(true);
      paramBoolean = true;
      localObject = new BasicAttribute("objectClass", "top");
    }
    else
    {
      localObject = paramAttributes.get("objectClass");
      if ((localObject == null) && (!paramAttributes.isCaseIgnored())) {
        localObject = paramAttributes.get("objectclass");
      }
      if (localObject == null) {
        localObject = new BasicAttribute("objectClass", "top");
      } else if ((i != 0) || (!paramBoolean)) {
        localObject = (Attribute)((Attribute)localObject).clone();
      }
    }
    paramAttributes = encodeObject(paramChar, paramObject, paramAttributes, (Attribute)localObject, paramBoolean);
    return paramAttributes;
  }
  
  private static final class LoaderInputStream
    extends ObjectInputStream
  {
    private ClassLoader classLoader;
    
    LoaderInputStream(InputStream paramInputStream, ClassLoader paramClassLoader)
      throws IOException
    {
      super();
      classLoader = paramClassLoader;
    }
    
    protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
      throws IOException, ClassNotFoundException
    {
      try
      {
        return classLoader.loadClass(paramObjectStreamClass.getName());
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      return super.resolveClass(paramObjectStreamClass);
    }
    
    protected Class<?> resolveProxyClass(String[] paramArrayOfString)
      throws IOException, ClassNotFoundException
    {
      ClassLoader localClassLoader = null;
      int i = 0;
      Class[] arrayOfClass = new Class[paramArrayOfString.length];
      for (int j = 0; j < paramArrayOfString.length; j++)
      {
        Class localClass = Class.forName(paramArrayOfString[j], false, classLoader);
        if ((localClass.getModifiers() & 0x1) == 0) {
          if (i != 0)
          {
            if (localClassLoader != localClass.getClassLoader()) {
              throw new IllegalAccessError("conflicting non-public interface class loaders");
            }
          }
          else
          {
            localClassLoader = localClass.getClassLoader();
            i = 1;
          }
        }
        arrayOfClass[j] = localClass;
      }
      try
      {
        return Proxy.getProxyClass(i != 0 ? localClassLoader : classLoader, arrayOfClass);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new ClassNotFoundException(null, localIllegalArgumentException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\Obj.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */