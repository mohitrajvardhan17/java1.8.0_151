package com.sun.xml.internal.ws.util;

public final class RuntimeVersion
{
  public static final Version VERSION;
  
  public RuntimeVersion() {}
  
  public String getVersion()
  {
    return VERSION.toString();
  }
  
  /* Error */
  static
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_0
    //   2: ldc 2
    //   4: ldc 1
    //   6: invokevirtual 42	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   9: astore_1
    //   10: aload_1
    //   11: invokestatic 40	com/sun/xml/internal/ws/util/Version:create	(Ljava/io/InputStream;)Lcom/sun/xml/internal/ws/util/Version;
    //   14: astore_0
    //   15: aload_1
    //   16: ifnull +30 -> 46
    //   19: aload_1
    //   20: invokevirtual 41	java/io/InputStream:close	()V
    //   23: goto +23 -> 46
    //   26: astore_2
    //   27: goto +19 -> 46
    //   30: astore_3
    //   31: aload_1
    //   32: ifnull +12 -> 44
    //   35: aload_1
    //   36: invokevirtual 41	java/io/InputStream:close	()V
    //   39: goto +5 -> 44
    //   42: astore 4
    //   44: aload_3
    //   45: athrow
    //   46: aload_0
    //   47: ifnonnull +10 -> 57
    //   50: aconst_null
    //   51: invokestatic 40	com/sun/xml/internal/ws/util/Version:create	(Ljava/io/InputStream;)Lcom/sun/xml/internal/ws/util/Version;
    //   54: goto +4 -> 58
    //   57: aload_0
    //   58: putstatic 38	com/sun/xml/internal/ws/util/RuntimeVersion:VERSION	Lcom/sun/xml/internal/ws/util/Version;
    //   61: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   1	57	0	localVersion	Version
    //   9	27	1	localInputStream	java.io.InputStream
    //   26	1	2	localIOException1	java.io.IOException
    //   30	15	3	localObject	Object
    //   42	1	4	localIOException2	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   19	23	26	java/io/IOException
    //   10	15	30	finally
    //   35	39	42	java/io/IOException
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\RuntimeVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */