package com.sun.xml.internal.ws.policy.privateutil;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class PolicyUtils
{
  private PolicyUtils() {}
  
  public static class Collections
  {
    public Collections() {}
    
    public static <E, T extends Collection<? extends E>, U extends Collection<? extends E>> Collection<Collection<E>> combine(U paramU, Collection<T> paramCollection, boolean paramBoolean)
    {
      ArrayList localArrayList = null;
      if ((paramCollection == null) || (paramCollection.isEmpty()))
      {
        if (paramU != null)
        {
          localArrayList = new ArrayList(1);
          localArrayList.add(new ArrayList(paramU));
        }
        return localArrayList;
      }
      LinkedList localLinkedList1 = new LinkedList();
      if ((paramU != null) && (!paramU.isEmpty())) {
        localLinkedList1.addAll(paramU);
      }
      int i = 1;
      LinkedList localLinkedList2 = new LinkedList();
      Object localObject1 = paramCollection.iterator();
      int k;
      while (((Iterator)localObject1).hasNext())
      {
        Collection localCollection1 = (Collection)((Iterator)localObject1).next();
        k = localCollection1.size();
        if (k == 0)
        {
          if (!paramBoolean) {
            return null;
          }
        }
        else if (k == 1)
        {
          localLinkedList1.addAll(localCollection1);
        }
        else
        {
          localLinkedList2.offer(localCollection1);
          i *= k;
        }
      }
      localArrayList = new ArrayList(i);
      localArrayList.add(localLinkedList1);
      if (i > 1) {
        while ((localObject1 = (Collection)localLinkedList2.poll()) != null)
        {
          int j = localArrayList.size();
          k = j * ((Collection)localObject1).size();
          int m = 0;
          Iterator localIterator = ((Collection)localObject1).iterator();
          while (localIterator.hasNext())
          {
            Object localObject2 = localIterator.next();
            for (int n = 0; n < j; n++)
            {
              Collection localCollection2 = (Collection)localArrayList.get(m);
              if (m + j < k) {
                localArrayList.add(new LinkedList(localCollection2));
              }
              localCollection2.add(localObject2);
              m++;
            }
          }
        }
      }
      return localArrayList;
    }
  }
  
  public static class Commons
  {
    public Commons() {}
    
    public static String getStackMethodName(int paramInt)
    {
      StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
      String str;
      if (arrayOfStackTraceElement.length > paramInt + 1) {
        str = arrayOfStackTraceElement[paramInt].getMethodName();
      } else {
        str = "UNKNOWN METHOD";
      }
      return str;
    }
    
    public static String getCallerMethodName()
    {
      String str = getStackMethodName(5);
      if (str.equals("invoke0")) {
        str = getStackMethodName(4);
      }
      return str;
    }
  }
  
  public static class Comparison
  {
    public static final Comparator<QName> QNAME_COMPARATOR = new Comparator()
    {
      public int compare(QName paramAnonymousQName1, QName paramAnonymousQName2)
      {
        if ((paramAnonymousQName1 == paramAnonymousQName2) || (paramAnonymousQName1.equals(paramAnonymousQName2))) {
          return 0;
        }
        int i = paramAnonymousQName1.getNamespaceURI().compareTo(paramAnonymousQName2.getNamespaceURI());
        if (i != 0) {
          return i;
        }
        return paramAnonymousQName1.getLocalPart().compareTo(paramAnonymousQName2.getLocalPart());
      }
    };
    
    public Comparison() {}
    
    public static int compareBoolean(boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = paramBoolean1 ? 1 : 0;
      int j = paramBoolean2 ? 1 : 0;
      return i - j;
    }
    
    public static int compareNullableStrings(String paramString1, String paramString2)
    {
      return paramString2 == null ? 1 : paramString1 == null ? -1 : paramString2 == null ? 0 : paramString1.compareTo(paramString2);
    }
  }
  
  public static class ConfigFile
  {
    public ConfigFile() {}
    
    public static String generateFullName(String paramString)
      throws PolicyException
    {
      if (paramString != null)
      {
        StringBuffer localStringBuffer = new StringBuffer("wsit-");
        localStringBuffer.append(paramString).append(".xml");
        return localStringBuffer.toString();
      }
      throw new PolicyException(LocalizationMessages.WSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL());
    }
    
    public static URL loadFromContext(String paramString, Object paramObject)
    {
      return (URL)PolicyUtils.Reflection.invoke(paramObject, "getResource", URL.class, new Object[] { paramString });
    }
    
    public static URL loadFromClasspath(String paramString)
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader == null) {
        return ClassLoader.getSystemResource(paramString);
      }
      return localClassLoader.getResource(paramString);
    }
  }
  
  public static class IO
  {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(IO.class);
    
    public IO() {}
    
    public static void closeResource(Closeable paramCloseable)
    {
      if (paramCloseable != null) {
        try
        {
          paramCloseable.close();
        }
        catch (IOException localIOException)
        {
          LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(paramCloseable.toString()), localIOException);
        }
      }
    }
    
    public static void closeResource(XMLStreamReader paramXMLStreamReader)
    {
      if (paramXMLStreamReader != null) {
        try
        {
          paramXMLStreamReader.close();
        }
        catch (XMLStreamException localXMLStreamException)
        {
          LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(paramXMLStreamReader.toString()), localXMLStreamException);
        }
      }
    }
  }
  
  static class Reflection
  {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(Reflection.class);
    
    Reflection() {}
    
    static <T> T invoke(Object paramObject, String paramString, Class<T> paramClass, Object... paramVarArgs)
      throws RuntimePolicyUtilsException
    {
      Class[] arrayOfClass;
      if ((paramVarArgs != null) && (paramVarArgs.length > 0))
      {
        arrayOfClass = new Class[paramVarArgs.length];
        int i = 0;
        for (Object localObject : paramVarArgs) {
          arrayOfClass[(i++)] = localObject.getClass();
        }
      }
      else
      {
        arrayOfClass = null;
      }
      return (T)invoke(paramObject, paramString, paramClass, paramVarArgs, arrayOfClass);
    }
    
    public static <T> T invoke(Object paramObject, String paramString, Class<T> paramClass, Object[] paramArrayOfObject, Class[] paramArrayOfClass)
      throws RuntimePolicyUtilsException
    {
      try
      {
        Method localMethod = paramObject.getClass().getMethod(paramString, paramArrayOfClass);
        Object localObject = MethodUtil.invoke(paramObject, localMethod, paramArrayOfObject);
        return (T)paramClass.cast(localObject);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(paramObject, paramArrayOfObject, paramString), localIllegalArgumentException)));
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(paramObject, paramArrayOfObject, paramString), localInvocationTargetException)));
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(paramObject, paramArrayOfObject, paramString), localIllegalAccessException.getCause())));
      }
      catch (SecurityException localSecurityException)
      {
        throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(paramObject, paramArrayOfObject, paramString), localSecurityException)));
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(paramObject, paramArrayOfObject, paramString), localNoSuchMethodException)));
      }
    }
    
    private static String createExceptionMessage(Object paramObject, Object[] paramArrayOfObject, String paramString)
    {
      return LocalizationMessages.WSP_0061_METHOD_INVOCATION_FAILED(paramObject.getClass().getName(), paramString, paramArrayOfObject == null ? null : Arrays.asList(paramArrayOfObject).toString());
    }
  }
  
  public static class Rfc2396
  {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtils.Reflection.class);
    
    public Rfc2396() {}
    
    public static String unquote(String paramString)
    {
      if (null == paramString) {
        return null;
      }
      byte[] arrayOfByte = new byte[paramString.length()];
      int i = 0;
      for (int n = 0; n < paramString.length(); n++)
      {
        int j = paramString.charAt(n);
        if (37 == j)
        {
          if (n + 2 >= paramString.length()) {
            throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(paramString)), false));
          }
          int k = Character.digit(paramString.charAt(++n), 16);
          int m = Character.digit(paramString.charAt(++n), 16);
          if ((0 > k) || (0 > m)) {
            throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(paramString)), false));
          }
          arrayOfByte[(i++)] = ((byte)(k * 16 + m));
        }
        else
        {
          arrayOfByte[(i++)] = ((byte)j);
        }
      }
      try
      {
        return new String(arrayOfByte, 0, i, "utf-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw ((RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(paramString), localUnsupportedEncodingException)));
      }
    }
  }
  
  public static class ServiceProvider
  {
    public ServiceProvider() {}
    
    public static <T> T[] load(Class<T> paramClass, ClassLoader paramClassLoader)
    {
      return ServiceFinder.find(paramClass, paramClassLoader).toArray();
    }
    
    public static <T> T[] load(Class<T> paramClass)
    {
      return ServiceFinder.find(paramClass).toArray();
    }
  }
  
  public static class Text
  {
    public static final String NEW_LINE = System.getProperty("line.separator");
    
    public Text() {}
    
    public static String createIndent(int paramInt)
    {
      char[] arrayOfChar = new char[paramInt * 4];
      Arrays.fill(arrayOfChar, ' ');
      return String.valueOf(arrayOfChar);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\privateutil\PolicyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */