package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.Messages;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.xml.transform.TransformerException;

final class CharInfo
{
  private HashMap m_charToString = new HashMap();
  public static final String HTML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.HTMLEntities";
  public static final String XML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.XMLEntities";
  public static final char S_HORIZONAL_TAB = '\t';
  public static final char S_LINEFEED = '\n';
  public static final char S_CARRIAGERETURN = '\r';
  final boolean onlyQuotAmpLtGt;
  private static final int ASCII_MAX = 128;
  private boolean[] isSpecialAttrASCII = new boolean[''];
  private boolean[] isSpecialTextASCII = new boolean[''];
  private boolean[] isCleanTextASCII = new boolean[''];
  private int[] array_of_bits = createEmptySetOfIntegers(65535);
  private static final int SHIFT_PER_WORD = 5;
  private static final int LOW_ORDER_BITMASK = 31;
  private int firstWordNotUsed;
  private static HashMap m_getCharInfoCache = new HashMap();
  
  private CharInfo(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, false);
  }
  
  private CharInfo(String paramString1, String paramString2, boolean paramBoolean)
  {
    ResourceBundle localResourceBundle = null;
    boolean bool = true;
    try
    {
      if (paramBoolean)
      {
        localResourceBundle = PropertyResourceBundle.getBundle(paramString1);
      }
      else
      {
        ClassLoader localClassLoader = SecuritySupport.getContextClassLoader();
        if (localClassLoader != null) {
          localResourceBundle = PropertyResourceBundle.getBundle(paramString1, Locale.getDefault(), localClassLoader);
        }
      }
    }
    catch (Exception localException1) {}
    Object localObject1;
    String str1;
    Object localObject2;
    if (localResourceBundle != null)
    {
      localObject1 = localResourceBundle.getKeys();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        str1 = (String)((Enumeration)localObject1).nextElement();
        localObject2 = localResourceBundle.getString(str1);
        int j = Integer.parseInt((String)localObject2);
        defineEntity(str1, (char)j);
        if (extraEntity(j)) {
          bool = false;
        }
      }
      set(10);
      set(13);
    }
    else
    {
      localObject1 = null;
      str1 = null;
      try
      {
        if (paramBoolean)
        {
          localObject1 = CharInfo.class.getResourceAsStream(paramString1);
        }
        else
        {
          localObject2 = SecuritySupport.getContextClassLoader();
          if (localObject2 != null) {
            try
            {
              localObject1 = ((ClassLoader)localObject2).getResourceAsStream(paramString1);
            }
            catch (Exception localException4)
            {
              str1 = localException4.getMessage();
            }
          }
          if (localObject1 == null) {
            try
            {
              URL localURL = new URL(paramString1);
              localObject1 = localURL.openStream();
            }
            catch (Exception localException5)
            {
              str1 = localException5.getMessage();
            }
          }
        }
        if (localObject1 == null) {
          throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_FIND", new Object[] { paramString1, str1 }));
        }
        try
        {
          localObject2 = new BufferedReader(new InputStreamReader((InputStream)localObject1, "UTF-8"));
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          localObject2 = new BufferedReader(new InputStreamReader((InputStream)localObject1));
        }
        String str2 = ((BufferedReader)localObject2).readLine();
        while (str2 != null) {
          if ((str2.length() == 0) || (str2.charAt(0) == '#'))
          {
            str2 = ((BufferedReader)localObject2).readLine();
          }
          else
          {
            int k = str2.indexOf(' ');
            if (k > 1)
            {
              String str3 = str2.substring(0, k);
              k++;
              if (k < str2.length())
              {
                String str4 = str2.substring(k);
                k = str4.indexOf(' ');
                if (k > 0) {
                  str4 = str4.substring(0, k);
                }
                int m = Integer.parseInt(str4);
                defineEntity(str3, (char)m);
                if (extraEntity(m)) {
                  bool = false;
                }
              }
            }
            str2 = ((BufferedReader)localObject2).readLine();
          }
        }
        ((InputStream)localObject1).close();
        set(10);
        set(13);
        if (localObject1 != null) {
          try
          {
            ((InputStream)localObject1).close();
          }
          catch (Exception localException2) {}
        }
        i = 0;
      }
      catch (Exception localException3)
      {
        throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_LOAD", new Object[] { paramString1, localException3.toString(), paramString1, localException3.toString() }));
      }
      finally
      {
        if (localObject1 != null) {
          try
          {
            ((InputStream)localObject1).close();
          }
          catch (Exception localException6) {}
        }
      }
    }
    while (i < 128)
    {
      if (((32 > i) && (10 != i) && (13 != i) && (9 != i)) || ((!get(i)) || (34 == i)))
      {
        isCleanTextASCII[i] = true;
        isSpecialTextASCII[i] = false;
      }
      else
      {
        isCleanTextASCII[i] = false;
        isSpecialTextASCII[i] = true;
      }
      i++;
    }
    onlyQuotAmpLtGt = bool;
    for (int i = 0; i < 128; i++) {
      isSpecialAttrASCII[i] = get(i);
    }
    if ("xml".equals(paramString2)) {
      isSpecialAttrASCII[9] = true;
    }
  }
  
  private void defineEntity(String paramString, char paramChar)
  {
    StringBuilder localStringBuilder = new StringBuilder("&");
    localStringBuilder.append(paramString);
    localStringBuilder.append(';');
    String str = localStringBuilder.toString();
    defineChar2StringMapping(str, paramChar);
  }
  
  String getOutputStringForChar(char paramChar)
  {
    CharKey localCharKey = new CharKey();
    localCharKey.setChar(paramChar);
    return (String)m_charToString.get(localCharKey);
  }
  
  final boolean isSpecialAttrChar(int paramInt)
  {
    if (paramInt < 128) {
      return isSpecialAttrASCII[paramInt];
    }
    return get(paramInt);
  }
  
  final boolean isSpecialTextChar(int paramInt)
  {
    if (paramInt < 128) {
      return isSpecialTextASCII[paramInt];
    }
    return get(paramInt);
  }
  
  final boolean isTextASCIIClean(int paramInt)
  {
    return isCleanTextASCII[paramInt];
  }
  
  static CharInfo getCharInfoInternal(String paramString1, String paramString2)
  {
    CharInfo localCharInfo = (CharInfo)m_getCharInfoCache.get(paramString1);
    if (localCharInfo != null) {
      return localCharInfo;
    }
    localCharInfo = new CharInfo(paramString1, paramString2, true);
    m_getCharInfoCache.put(paramString1, localCharInfo);
    return localCharInfo;
  }
  
  static CharInfo getCharInfo(String paramString1, String paramString2)
  {
    try
    {
      return new CharInfo(paramString1, paramString2, false);
    }
    catch (Exception localException)
    {
      String str;
      if (paramString1.indexOf(':') < 0) {
        str = SystemIDResolver.getAbsoluteURIFromRelative(paramString1);
      } else {
        try
        {
          str = SystemIDResolver.getAbsoluteURI(paramString1, null);
        }
        catch (TransformerException localTransformerException)
        {
          throw new WrappedRuntimeException(localTransformerException);
        }
      }
      return new CharInfo(str, paramString2, false);
    }
  }
  
  private static int arrayIndex(int paramInt)
  {
    return paramInt >> 5;
  }
  
  private static int bit(int paramInt)
  {
    int i = 1 << (paramInt & 0x1F);
    return i;
  }
  
  private int[] createEmptySetOfIntegers(int paramInt)
  {
    firstWordNotUsed = 0;
    int[] arrayOfInt = new int[arrayIndex(paramInt - 1) + 1];
    return arrayOfInt;
  }
  
  private final void set(int paramInt)
  {
    setASCIIdirty(paramInt);
    int i = paramInt >> 5;
    int j = i + 1;
    if (firstWordNotUsed < j) {
      firstWordNotUsed = j;
    }
    array_of_bits[i] |= 1 << (paramInt & 0x1F);
  }
  
  private final boolean get(int paramInt)
  {
    boolean bool = false;
    int i = paramInt >> 5;
    if (i < firstWordNotUsed) {
      bool = (array_of_bits[i] & 1 << (paramInt & 0x1F)) != 0;
    }
    return bool;
  }
  
  private boolean extraEntity(int paramInt)
  {
    boolean bool = false;
    if (paramInt < 128) {
      switch (paramInt)
      {
      case 34: 
      case 38: 
      case 60: 
      case 62: 
        break;
      default: 
        bool = true;
      }
    }
    return bool;
  }
  
  private void setASCIIdirty(int paramInt)
  {
    if ((0 <= paramInt) && (paramInt < 128))
    {
      isCleanTextASCII[paramInt] = false;
      isSpecialTextASCII[paramInt] = true;
    }
  }
  
  private void setASCIIclean(int paramInt)
  {
    if ((0 <= paramInt) && (paramInt < 128))
    {
      isCleanTextASCII[paramInt] = true;
      isSpecialTextASCII[paramInt] = false;
    }
  }
  
  private void defineChar2StringMapping(String paramString, char paramChar)
  {
    CharKey localCharKey = new CharKey(paramChar);
    m_charToString.put(localCharKey, paramString);
    set(paramChar);
  }
  
  private static class CharKey
  {
    private char m_char;
    
    public CharKey(char paramChar)
    {
      m_char = paramChar;
    }
    
    public CharKey() {}
    
    public final void setChar(char paramChar)
    {
      m_char = paramChar;
    }
    
    public final int hashCode()
    {
      return m_char;
    }
    
    public final boolean equals(Object paramObject)
    {
      return m_char == m_char;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\CharInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */