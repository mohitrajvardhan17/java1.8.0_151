package sun.nio.cs;

import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import sun.misc.ASCIICaseInsensitiveComparator;

public class AbstractCharsetProvider
  extends CharsetProvider
{
  private Map<String, String> classMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  private Map<String, String> aliasMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  private Map<String, String[]> aliasNameMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  private Map<String, SoftReference<Charset>> cache = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  private String packagePrefix;
  
  protected AbstractCharsetProvider()
  {
    packagePrefix = "sun.nio.cs";
  }
  
  protected AbstractCharsetProvider(String paramString)
  {
    packagePrefix = paramString;
  }
  
  private static <K, V> void put(Map<K, V> paramMap, K paramK, V paramV)
  {
    if (!paramMap.containsKey(paramK)) {
      paramMap.put(paramK, paramV);
    }
  }
  
  private static <K, V> void remove(Map<K, V> paramMap, K paramK)
  {
    Object localObject = paramMap.remove(paramK);
    assert (localObject != null);
  }
  
  protected void charset(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    synchronized (this)
    {
      put(classMap, paramString1, paramString2);
      for (int i = 0; i < paramArrayOfString.length; i++) {
        put(aliasMap, paramArrayOfString[i], paramString1);
      }
      put(aliasNameMap, paramString1, paramArrayOfString);
      cache.clear();
    }
  }
  
  protected void deleteCharset(String paramString, String[] paramArrayOfString)
  {
    synchronized (this)
    {
      remove(classMap, paramString);
      for (int i = 0; i < paramArrayOfString.length; i++) {
        remove(aliasMap, paramArrayOfString[i]);
      }
      remove(aliasNameMap, paramString);
      cache.clear();
    }
  }
  
  protected void init() {}
  
  private String canonicalize(String paramString)
  {
    String str = (String)aliasMap.get(paramString);
    return str != null ? str : paramString;
  }
  
  private Charset lookup(String paramString)
  {
    SoftReference localSoftReference = (SoftReference)cache.get(paramString);
    if (localSoftReference != null)
    {
      localObject = (Charset)localSoftReference.get();
      if (localObject != null) {
        return (Charset)localObject;
      }
    }
    Object localObject = (String)classMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      Class localClass = Class.forName(packagePrefix + "." + (String)localObject, true, getClass().getClassLoader());
      Charset localCharset = (Charset)localClass.newInstance();
      cache.put(paramString, new SoftReference(localCharset));
      return localCharset;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return null;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      return null;
    }
    catch (InstantiationException localInstantiationException) {}
    return null;
  }
  
  public final Charset charsetForName(String paramString)
  {
    synchronized (this)
    {
      init();
      return lookup(canonicalize(paramString));
    }
  }
  
  public final Iterator<Charset> charsets()
  {
    final ArrayList localArrayList;
    synchronized (this)
    {
      init();
      localArrayList = new ArrayList(classMap.keySet());
    }
    new Iterator()
    {
      Iterator<String> i = localArrayList.iterator();
      
      public boolean hasNext()
      {
        return i.hasNext();
      }
      
      /* Error */
      public Charset next()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 55	sun/nio/cs/AbstractCharsetProvider$1:i	Ljava/util/Iterator;
        //   4: invokeinterface 63 1 0
        //   9: checkcast 28	java/lang/String
        //   12: astore_1
        //   13: aload_0
        //   14: getfield 56	sun/nio/cs/AbstractCharsetProvider$1:this$0	Lsun/nio/cs/AbstractCharsetProvider;
        //   17: dup
        //   18: astore_2
        //   19: monitorenter
        //   20: aload_0
        //   21: getfield 56	sun/nio/cs/AbstractCharsetProvider$1:this$0	Lsun/nio/cs/AbstractCharsetProvider;
        //   24: aload_1
        //   25: invokestatic 60	sun/nio/cs/AbstractCharsetProvider:access$000	(Lsun/nio/cs/AbstractCharsetProvider;Ljava/lang/String;)Ljava/nio/charset/Charset;
        //   28: aload_2
        //   29: monitorexit
        //   30: areturn
        //   31: astore_3
        //   32: aload_2
        //   33: monitorexit
        //   34: aload_3
        //   35: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	36	0	this	1
        //   12	13	1	str	String
        //   18	15	2	Ljava/lang/Object;	Object
        //   31	4	3	localObject1	Object
        // Exception table:
        //   from	to	target	type
        //   20	30	31	finally
        //   31	34	31	finally
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public final String[] aliases(String paramString)
  {
    synchronized (this)
    {
      init();
      return (String[])aliasNameMap.get(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\AbstractCharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */