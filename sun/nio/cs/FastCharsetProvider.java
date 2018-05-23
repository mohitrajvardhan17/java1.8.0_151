package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FastCharsetProvider
  extends CharsetProvider
{
  private Map<String, String> classMap;
  private Map<String, String> aliasMap;
  private Map<String, Charset> cache;
  private String packagePrefix;
  
  protected FastCharsetProvider(String paramString, Map<String, String> paramMap1, Map<String, String> paramMap2, Map<String, Charset> paramMap)
  {
    packagePrefix = paramString;
    aliasMap = paramMap1;
    classMap = paramMap2;
    cache = paramMap;
  }
  
  private String canonicalize(String paramString)
  {
    String str = (String)aliasMap.get(paramString);
    return str != null ? str : paramString;
  }
  
  private static String toLower(String paramString)
  {
    int i = paramString.length();
    int j = 1;
    for (int k = 0; k < i; k++)
    {
      m = paramString.charAt(k);
      if ((m - 65 | 90 - m) >= 0)
      {
        j = 0;
        break;
      }
    }
    if (j != 0) {
      return paramString;
    }
    char[] arrayOfChar = new char[i];
    for (int m = 0; m < i; m++)
    {
      int n = paramString.charAt(m);
      if ((n - 65 | 90 - n) >= 0) {
        arrayOfChar[m] = ((char)(n + 32));
      } else {
        arrayOfChar[m] = ((char)n);
      }
    }
    return new String(arrayOfChar);
  }
  
  private Charset lookup(String paramString)
  {
    String str1 = canonicalize(toLower(paramString));
    Object localObject = (Charset)cache.get(str1);
    if (localObject != null) {
      return (Charset)localObject;
    }
    String str2 = (String)classMap.get(str1);
    if (str2 == null) {
      return null;
    }
    if (str2.equals("US_ASCII"))
    {
      localObject = new US_ASCII();
      cache.put(str1, localObject);
      return (Charset)localObject;
    }
    try
    {
      Class localClass = Class.forName(packagePrefix + "." + str2, true, getClass().getClassLoader());
      localObject = (Charset)localClass.newInstance();
      cache.put(str1, localObject);
      return (Charset)localObject;
    }
    catch (ClassNotFoundException|IllegalAccessException|InstantiationException localClassNotFoundException) {}
    return null;
  }
  
  /* Error */
  public final Charset charsetForName(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_2
    //   3: monitorenter
    //   4: aload_0
    //   5: aload_0
    //   6: aload_1
    //   7: invokespecial 127	sun/nio/cs/FastCharsetProvider:canonicalize	(Ljava/lang/String;)Ljava/lang/String;
    //   10: invokespecial 129	sun/nio/cs/FastCharsetProvider:lookup	(Ljava/lang/String;)Ljava/nio/charset/Charset;
    //   13: aload_2
    //   14: monitorexit
    //   15: areturn
    //   16: astore_3
    //   17: aload_2
    //   18: monitorexit
    //   19: aload_3
    //   20: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	21	0	this	FastCharsetProvider
    //   0	21	1	paramString	String
    //   2	16	2	Ljava/lang/Object;	Object
    //   16	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	15	16	finally
    //   16	19	16	finally
  }
  
  public final Iterator<Charset> charsets()
  {
    new Iterator()
    {
      Iterator<String> i = classMap.keySet().iterator();
      
      public boolean hasNext()
      {
        return i.hasNext();
      }
      
      public Charset next()
      {
        String str = (String)i.next();
        return FastCharsetProvider.this.lookup(str);
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\FastCharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */