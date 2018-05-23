package org.xml.sax.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class XMLReaderFactory
{
  private static final String property = "org.xml.sax.driver";
  private static SecuritySupport ss = new SecuritySupport();
  private static String _clsFromJar = null;
  private static boolean _jarread = false;
  
  private XMLReaderFactory() {}
  
  public static XMLReader createXMLReader()
    throws SAXException
  {
    String str1 = null;
    ClassLoader localClassLoader = ss.getContextClassLoader();
    try
    {
      str1 = ss.getSystemProperty("org.xml.sax.driver");
    }
    catch (RuntimeException localRuntimeException) {}
    if (str1 == null)
    {
      if (!_jarread)
      {
        _jarread = true;
        String str2 = "META-INF/services/org.xml.sax.driver";
        try
        {
          InputStream localInputStream;
          if (localClassLoader != null)
          {
            localInputStream = ss.getResourceAsStream(localClassLoader, str2);
            if (localInputStream == null)
            {
              localClassLoader = null;
              localInputStream = ss.getResourceAsStream(localClassLoader, str2);
            }
          }
          else
          {
            localInputStream = ss.getResourceAsStream(localClassLoader, str2);
          }
          if (localInputStream != null)
          {
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "UTF8"));
            _clsFromJar = localBufferedReader.readLine();
            localInputStream.close();
          }
        }
        catch (Exception localException2) {}
      }
      str1 = _clsFromJar;
    }
    if (str1 == null) {
      str1 = "com.sun.org.apache.xerces.internal.parsers.SAXParser";
    }
    if (str1 != null) {
      return loadClass(localClassLoader, str1);
    }
    try
    {
      return new ParserAdapter(ParserFactory.makeParser());
    }
    catch (Exception localException1)
    {
      throw new SAXException("Can't create default XMLReader; is system property org.xml.sax.driver set?");
    }
  }
  
  public static XMLReader createXMLReader(String paramString)
    throws SAXException
  {
    return loadClass(ss.getContextClassLoader(), paramString);
  }
  
  private static XMLReader loadClass(ClassLoader paramClassLoader, String paramString)
    throws SAXException
  {
    try
    {
      return (XMLReader)NewInstance.newInstance(paramClassLoader, paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new SAXException("SAX2 driver class " + paramString + " not found", localClassNotFoundException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new SAXException("SAX2 driver class " + paramString + " found but cannot be loaded", localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new SAXException("SAX2 driver class " + paramString + " loaded but cannot be instantiated (no empty public constructor?)", localInstantiationException);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new SAXException("SAX2 driver class " + paramString + " does not implement XMLReader", localClassCastException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\helpers\XMLReaderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */