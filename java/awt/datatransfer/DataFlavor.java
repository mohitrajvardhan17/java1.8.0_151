package java.awt.datatransfer;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.DataTransferer.DataFlavorComparator;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class DataFlavor
  implements Externalizable, Cloneable
{
  private static final long serialVersionUID = 8367026044764648243L;
  private static final Class<InputStream> ioInputStreamClass = InputStream.class;
  public static final DataFlavor stringFlavor = createConstant(String.class, "Unicode String");
  public static final DataFlavor imageFlavor = createConstant("image/x-java-image; class=java.awt.Image", "Image");
  @Deprecated
  public static final DataFlavor plainTextFlavor = createConstant("text/plain; charset=unicode; class=java.io.InputStream", "Plain Text");
  public static final String javaSerializedObjectMimeType = "application/x-java-serialized-object";
  public static final DataFlavor javaFileListFlavor = createConstant("application/x-java-file-list;class=java.util.List", null);
  public static final String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
  public static final String javaRemoteObjectMimeType = "application/x-java-remote-object";
  public static DataFlavor selectionHtmlFlavor = initHtmlDataFlavor("selection");
  public static DataFlavor fragmentHtmlFlavor = initHtmlDataFlavor("fragment");
  public static DataFlavor allHtmlFlavor = initHtmlDataFlavor("all");
  private static Comparator<DataFlavor> textFlavorComparator;
  transient int atom;
  MimeType mimeType;
  private String humanPresentableName;
  private Class<?> representationClass;
  
  protected static final Class<?> tryToLoadClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    ReflectUtil.checkPackageAccess(paramString);
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
      }
      ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
      try
      {
        return Class.forName(paramString, true, localClassLoader);
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        localClassLoader = Thread.currentThread().getContextClassLoader();
        if (localClassLoader != null) {
          try
          {
            return Class.forName(paramString, true, localClassLoader);
          }
          catch (ClassNotFoundException localClassNotFoundException2) {}
        }
      }
      return Class.forName(paramString, true, paramClassLoader);
    }
    catch (SecurityException localSecurityException) {}
  }
  
  private static DataFlavor createConstant(Class<?> paramClass, String paramString)
  {
    try
    {
      return new DataFlavor(paramClass, paramString);
    }
    catch (Exception localException) {}
    return null;
  }
  
  private static DataFlavor createConstant(String paramString1, String paramString2)
  {
    try
    {
      return new DataFlavor(paramString1, paramString2);
    }
    catch (Exception localException) {}
    return null;
  }
  
  private static DataFlavor initHtmlDataFlavor(String paramString)
  {
    try
    {
      return new DataFlavor("text/html; class=java.lang.String;document=" + paramString + ";charset=Unicode");
    }
    catch (Exception localException) {}
    return null;
  }
  
  public DataFlavor() {}
  
  private DataFlavor(String paramString1, String paramString2, MimeTypeParameterList paramMimeTypeParameterList, Class<?> paramClass, String paramString3)
  {
    if (paramString1 == null) {
      throw new NullPointerException("primaryType");
    }
    if (paramString2 == null) {
      throw new NullPointerException("subType");
    }
    if (paramClass == null) {
      throw new NullPointerException("representationClass");
    }
    if (paramMimeTypeParameterList == null) {
      paramMimeTypeParameterList = new MimeTypeParameterList();
    }
    paramMimeTypeParameterList.set("class", paramClass.getName());
    if (paramString3 == null)
    {
      paramString3 = paramMimeTypeParameterList.get("humanPresentableName");
      if (paramString3 == null) {
        paramString3 = paramString1 + "/" + paramString2;
      }
    }
    try
    {
      mimeType = new MimeType(paramString1, paramString2, paramMimeTypeParameterList);
    }
    catch (MimeTypeParseException localMimeTypeParseException)
    {
      throw new IllegalArgumentException("MimeType Parse Exception: " + localMimeTypeParseException.getMessage());
    }
    representationClass = paramClass;
    humanPresentableName = paramString3;
    mimeType.removeParameter("humanPresentableName");
  }
  
  public DataFlavor(Class<?> paramClass, String paramString)
  {
    this("application", "x-java-serialized-object", null, paramClass, paramString);
    if (paramClass == null) {
      throw new NullPointerException("representationClass");
    }
  }
  
  public DataFlavor(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("mimeType");
    }
    try
    {
      initialize(paramString1, paramString2, getClass().getClassLoader());
    }
    catch (MimeTypeParseException localMimeTypeParseException)
    {
      throw new IllegalArgumentException("failed to parse:" + paramString1);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new IllegalArgumentException("can't find specified class: " + localClassNotFoundException.getMessage());
    }
  }
  
  public DataFlavor(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    if (paramString1 == null) {
      throw new NullPointerException("mimeType");
    }
    try
    {
      initialize(paramString1, paramString2, paramClassLoader);
    }
    catch (MimeTypeParseException localMimeTypeParseException)
    {
      throw new IllegalArgumentException("failed to parse:" + paramString1);
    }
  }
  
  public DataFlavor(String paramString)
    throws ClassNotFoundException
  {
    if (paramString == null) {
      throw new NullPointerException("mimeType");
    }
    try
    {
      initialize(paramString, null, getClass().getClassLoader());
    }
    catch (MimeTypeParseException localMimeTypeParseException)
    {
      throw new IllegalArgumentException("failed to parse:" + paramString);
    }
  }
  
  private void initialize(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws MimeTypeParseException, ClassNotFoundException
  {
    if (paramString1 == null) {
      throw new NullPointerException("mimeType");
    }
    mimeType = new MimeType(paramString1);
    String str = getParameter("class");
    if (str == null)
    {
      if ("application/x-java-serialized-object".equals(mimeType.getBaseType())) {
        throw new IllegalArgumentException("no representation class specified for:" + paramString1);
      }
      representationClass = InputStream.class;
    }
    else
    {
      representationClass = tryToLoadClass(str, paramClassLoader);
    }
    mimeType.setParameter("class", representationClass.getName());
    if (paramString2 == null)
    {
      paramString2 = mimeType.getParameter("humanPresentableName");
      if (paramString2 == null) {
        paramString2 = mimeType.getPrimaryType() + "/" + mimeType.getSubType();
      }
    }
    humanPresentableName = paramString2;
    mimeType.removeParameter("humanPresentableName");
  }
  
  public String toString()
  {
    String str = getClass().getName();
    str = str + "[" + paramString() + "]";
    return str;
  }
  
  private String paramString()
  {
    String str = "";
    str = str + "mimetype=";
    if (mimeType == null) {
      str = str + "null";
    } else {
      str = str + mimeType.getBaseType();
    }
    str = str + ";representationclass=";
    if (representationClass == null) {
      str = str + "null";
    } else {
      str = str + representationClass.getName();
    }
    if ((DataTransferer.isFlavorCharsetTextType(this)) && ((isRepresentationClassInputStream()) || (isRepresentationClassByteBuffer()) || (byte[].class.equals(representationClass)))) {
      str = str + ";charset=" + DataTransferer.getTextCharset(this);
    }
    return str;
  }
  
  public static final DataFlavor getTextPlainUnicodeFlavor()
  {
    String str = null;
    DataTransferer localDataTransferer = DataTransferer.getInstance();
    if (localDataTransferer != null) {
      str = localDataTransferer.getDefaultUnicodeEncoding();
    }
    return new DataFlavor("text/plain;charset=" + str + ";class=java.io.InputStream", "Plain Text");
  }
  
  public static final DataFlavor selectBestTextFlavor(DataFlavor[] paramArrayOfDataFlavor)
  {
    if ((paramArrayOfDataFlavor == null) || (paramArrayOfDataFlavor.length == 0)) {
      return null;
    }
    if (textFlavorComparator == null) {
      textFlavorComparator = new TextFlavorComparator();
    }
    DataFlavor localDataFlavor = (DataFlavor)Collections.max(Arrays.asList(paramArrayOfDataFlavor), textFlavorComparator);
    if (!localDataFlavor.isFlavorTextType()) {
      return null;
    }
    return localDataFlavor;
  }
  
  public Reader getReaderForText(Transferable paramTransferable)
    throws UnsupportedFlavorException, IOException
  {
    Object localObject1 = paramTransferable.getTransferData(this);
    if (localObject1 == null) {
      throw new IllegalArgumentException("getTransferData() returned null");
    }
    if ((localObject1 instanceof Reader)) {
      return (Reader)localObject1;
    }
    if ((localObject1 instanceof String)) {
      return new StringReader((String)localObject1);
    }
    if ((localObject1 instanceof CharBuffer))
    {
      localObject2 = (CharBuffer)localObject1;
      int i = ((CharBuffer)localObject2).remaining();
      char[] arrayOfChar = new char[i];
      ((CharBuffer)localObject2).get(arrayOfChar, 0, i);
      return new CharArrayReader(arrayOfChar);
    }
    if ((localObject1 instanceof char[])) {
      return new CharArrayReader((char[])localObject1);
    }
    Object localObject2 = null;
    if ((localObject1 instanceof InputStream))
    {
      localObject2 = (InputStream)localObject1;
    }
    else if ((localObject1 instanceof ByteBuffer))
    {
      localObject3 = (ByteBuffer)localObject1;
      int j = ((ByteBuffer)localObject3).remaining();
      byte[] arrayOfByte = new byte[j];
      ((ByteBuffer)localObject3).get(arrayOfByte, 0, j);
      localObject2 = new ByteArrayInputStream(arrayOfByte);
    }
    else if ((localObject1 instanceof byte[]))
    {
      localObject2 = new ByteArrayInputStream((byte[])localObject1);
    }
    if (localObject2 == null) {
      throw new IllegalArgumentException("transfer data is not Reader, String, CharBuffer, char array, InputStream, ByteBuffer, or byte array");
    }
    Object localObject3 = getParameter("charset");
    return localObject3 == null ? new InputStreamReader((InputStream)localObject2) : new InputStreamReader((InputStream)localObject2, (String)localObject3);
  }
  
  public String getMimeType()
  {
    return mimeType != null ? mimeType.toString() : null;
  }
  
  public Class<?> getRepresentationClass()
  {
    return representationClass;
  }
  
  public String getHumanPresentableName()
  {
    return humanPresentableName;
  }
  
  public String getPrimaryType()
  {
    return mimeType != null ? mimeType.getPrimaryType() : null;
  }
  
  public String getSubType()
  {
    return mimeType != null ? mimeType.getSubType() : null;
  }
  
  public String getParameter(String paramString)
  {
    if (paramString.equals("humanPresentableName")) {
      return humanPresentableName;
    }
    return mimeType != null ? mimeType.getParameter(paramString) : null;
  }
  
  public void setHumanPresentableName(String paramString)
  {
    humanPresentableName = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof DataFlavor)) && (equals((DataFlavor)paramObject));
  }
  
  public boolean equals(DataFlavor paramDataFlavor)
  {
    if (paramDataFlavor == null) {
      return false;
    }
    if (this == paramDataFlavor) {
      return true;
    }
    if (!Objects.equals(getRepresentationClass(), paramDataFlavor.getRepresentationClass())) {
      return false;
    }
    if (mimeType == null)
    {
      if (mimeType != null) {
        return false;
      }
    }
    else
    {
      if (!mimeType.match(mimeType)) {
        return false;
      }
      if ("text".equals(getPrimaryType()))
      {
        String str1;
        String str2;
        if ((DataTransferer.doesSubtypeSupportCharset(this)) && (representationClass != null) && (!isStandardTextRepresentationClass()))
        {
          str1 = DataTransferer.canonicalName(getParameter("charset"));
          str2 = DataTransferer.canonicalName(paramDataFlavor.getParameter("charset"));
          if (!Objects.equals(str1, str2)) {
            return false;
          }
        }
        if ("html".equals(getSubType()))
        {
          str1 = getParameter("document");
          str2 = paramDataFlavor.getParameter("document");
          if (!Objects.equals(str1, str2)) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  @Deprecated
  public boolean equals(String paramString)
  {
    if ((paramString == null) || (mimeType == null)) {
      return false;
    }
    return isMimeTypeEqual(paramString);
  }
  
  public int hashCode()
  {
    int i = 0;
    if (representationClass != null) {
      i += representationClass.hashCode();
    }
    if (mimeType != null)
    {
      String str1 = mimeType.getPrimaryType();
      if (str1 != null) {
        i += str1.hashCode();
      }
      if ("text".equals(str1))
      {
        String str2;
        if ((DataTransferer.doesSubtypeSupportCharset(this)) && (representationClass != null) && (!isStandardTextRepresentationClass()))
        {
          str2 = DataTransferer.canonicalName(getParameter("charset"));
          if (str2 != null) {
            i += str2.hashCode();
          }
        }
        if ("html".equals(getSubType()))
        {
          str2 = getParameter("document");
          if (str2 != null) {
            i += str2.hashCode();
          }
        }
      }
    }
    return i;
  }
  
  public boolean match(DataFlavor paramDataFlavor)
  {
    return equals(paramDataFlavor);
  }
  
  public boolean isMimeTypeEqual(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("mimeType");
    }
    if (mimeType == null) {
      return false;
    }
    try
    {
      return mimeType.match(new MimeType(paramString));
    }
    catch (MimeTypeParseException localMimeTypeParseException) {}
    return false;
  }
  
  public final boolean isMimeTypeEqual(DataFlavor paramDataFlavor)
  {
    return isMimeTypeEqual(mimeType);
  }
  
  private boolean isMimeTypeEqual(MimeType paramMimeType)
  {
    if (mimeType == null) {
      return paramMimeType == null;
    }
    return mimeType.match(paramMimeType);
  }
  
  private boolean isStandardTextRepresentationClass()
  {
    return (isRepresentationClassReader()) || (String.class.equals(representationClass)) || (isRepresentationClassCharBuffer()) || (char[].class.equals(representationClass));
  }
  
  public boolean isMimeTypeSerializedObject()
  {
    return isMimeTypeEqual("application/x-java-serialized-object");
  }
  
  public final Class<?> getDefaultRepresentationClass()
  {
    return ioInputStreamClass;
  }
  
  public final String getDefaultRepresentationClassAsString()
  {
    return getDefaultRepresentationClass().getName();
  }
  
  public boolean isRepresentationClassInputStream()
  {
    return ioInputStreamClass.isAssignableFrom(representationClass);
  }
  
  public boolean isRepresentationClassReader()
  {
    return Reader.class.isAssignableFrom(representationClass);
  }
  
  public boolean isRepresentationClassCharBuffer()
  {
    return CharBuffer.class.isAssignableFrom(representationClass);
  }
  
  public boolean isRepresentationClassByteBuffer()
  {
    return ByteBuffer.class.isAssignableFrom(representationClass);
  }
  
  public boolean isRepresentationClassSerializable()
  {
    return Serializable.class.isAssignableFrom(representationClass);
  }
  
  public boolean isRepresentationClassRemote()
  {
    return DataTransferer.isRemote(representationClass);
  }
  
  public boolean isFlavorSerializedObjectType()
  {
    return (isRepresentationClassSerializable()) && (isMimeTypeEqual("application/x-java-serialized-object"));
  }
  
  public boolean isFlavorRemoteObjectType()
  {
    return (isRepresentationClassRemote()) && (isRepresentationClassSerializable()) && (isMimeTypeEqual("application/x-java-remote-object"));
  }
  
  public boolean isFlavorJavaFileListType()
  {
    if ((mimeType == null) || (representationClass == null)) {
      return false;
    }
    return (List.class.isAssignableFrom(representationClass)) && (mimeType.match(javaFileListFlavormimeType));
  }
  
  public boolean isFlavorTextType()
  {
    return (DataTransferer.isFlavorCharsetTextType(this)) || (DataTransferer.isFlavorNoncharsetTextType(this));
  }
  
  public synchronized void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    if (mimeType != null)
    {
      mimeType.setParameter("humanPresentableName", humanPresentableName);
      paramObjectOutput.writeObject(mimeType);
      mimeType.removeParameter("humanPresentableName");
    }
    else
    {
      paramObjectOutput.writeObject(null);
    }
    paramObjectOutput.writeObject(representationClass);
  }
  
  public synchronized void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    String str = null;
    mimeType = ((MimeType)paramObjectInput.readObject());
    if (mimeType != null)
    {
      humanPresentableName = mimeType.getParameter("humanPresentableName");
      mimeType.removeParameter("humanPresentableName");
      str = mimeType.getParameter("class");
      if (str == null) {
        throw new IOException("no class parameter specified in: " + mimeType);
      }
    }
    try
    {
      representationClass = ((Class)paramObjectInput.readObject());
    }
    catch (OptionalDataException localOptionalDataException)
    {
      if ((!eof) || (length != 0)) {
        throw localOptionalDataException;
      }
      if (str != null) {
        representationClass = tryToLoadClass(str, getClass().getClassLoader());
      }
    }
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    Object localObject = super.clone();
    if (mimeType != null) {
      mimeType = ((MimeType)mimeType.clone());
    }
    return localObject;
  }
  
  @Deprecated
  protected String normalizeMimeTypeParameter(String paramString1, String paramString2)
  {
    return paramString2;
  }
  
  @Deprecated
  protected String normalizeMimeType(String paramString)
  {
    return paramString;
  }
  
  static class TextFlavorComparator
    extends DataTransferer.DataFlavorComparator
  {
    TextFlavorComparator() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      DataFlavor localDataFlavor1 = (DataFlavor)paramObject1;
      DataFlavor localDataFlavor2 = (DataFlavor)paramObject2;
      if (localDataFlavor1.isFlavorTextType())
      {
        if (localDataFlavor2.isFlavorTextType()) {
          return super.compare(paramObject1, paramObject2);
        }
        return 1;
      }
      if (localDataFlavor2.isFlavorTextType()) {
        return -1;
      }
      return 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\DataFlavor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */