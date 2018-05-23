package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class SerializerFactory
{
  public static final String FactoriesProperty = "com.sun.org.apache.xml.internal.serialize.factories";
  private static final Map<String, SerializerFactory> _factories = Collections.synchronizedMap(new HashMap());
  
  public SerializerFactory() {}
  
  public static void registerSerializerFactory(SerializerFactory paramSerializerFactory)
  {
    synchronized (_factories)
    {
      String str = paramSerializerFactory.getSupportedMethod();
      _factories.put(str, paramSerializerFactory);
    }
  }
  
  public static SerializerFactory getSerializerFactory(String paramString)
  {
    return (SerializerFactory)_factories.get(paramString);
  }
  
  protected abstract String getSupportedMethod();
  
  public abstract Serializer makeSerializer(OutputFormat paramOutputFormat);
  
  public abstract Serializer makeSerializer(Writer paramWriter, OutputFormat paramOutputFormat);
  
  public abstract Serializer makeSerializer(OutputStream paramOutputStream, OutputFormat paramOutputFormat)
    throws UnsupportedEncodingException;
  
  static
  {
    Object localObject = new SerializerFactoryImpl("xml");
    registerSerializerFactory((SerializerFactory)localObject);
    localObject = new SerializerFactoryImpl("html");
    registerSerializerFactory((SerializerFactory)localObject);
    localObject = new SerializerFactoryImpl("xhtml");
    registerSerializerFactory((SerializerFactory)localObject);
    localObject = new SerializerFactoryImpl("text");
    registerSerializerFactory((SerializerFactory)localObject);
    String str1 = SecuritySupport.getSystemProperty("com.sun.org.apache.xml.internal.serialize.factories");
    if (str1 != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, " ;,:");
      while (localStringTokenizer.hasMoreTokens())
      {
        String str2 = localStringTokenizer.nextToken();
        try
        {
          localObject = (SerializerFactory)ObjectFactory.newInstance(str2, true);
          if (_factories.containsKey(((SerializerFactory)localObject).getSupportedMethod())) {
            _factories.put(((SerializerFactory)localObject).getSupportedMethod(), localObject);
          }
        }
        catch (Exception localException) {}
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serialize\SerializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */