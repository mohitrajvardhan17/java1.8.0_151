package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.Limit;
import java.util.HashMap;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLReaderManager
{
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
  private static final XMLReaderManager m_singletonManager = new XMLReaderManager();
  private static final String property = "org.xml.sax.driver";
  private static SAXParserFactory m_parserFactory;
  private ThreadLocal m_readers;
  private HashMap m_inUse;
  private boolean m_useServicesMechanism = true;
  private boolean _secureProcessing;
  private String _accessExternalDTD = "all";
  private XMLSecurityManager _xmlSecurityManager;
  
  private XMLReaderManager() {}
  
  public static XMLReaderManager getInstance(boolean paramBoolean)
  {
    m_singletonManager.setServicesMechnism(paramBoolean);
    return m_singletonManager;
  }
  
  public synchronized XMLReader getXMLReader()
    throws SAXException
  {
    if (m_readers == null) {
      m_readers = new ThreadLocal();
    }
    if (m_inUse == null) {
      m_inUse = new HashMap();
    }
    XMLReader localXMLReader = (XMLReader)m_readers.get();
    int i = localXMLReader != null ? 1 : 0;
    String str1 = SecuritySupport.getSystemProperty("org.xml.sax.driver");
    if ((i != 0) && (m_inUse.get(localXMLReader) != Boolean.TRUE) && ((str1 == null) || (localXMLReader.getClass().getName().equals(str1))))
    {
      m_inUse.put(localXMLReader, Boolean.TRUE);
    }
    else
    {
      try
      {
        try
        {
          localXMLReader = XMLReaderFactory.createXMLReader();
          try
          {
            localXMLReader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", _secureProcessing);
          }
          catch (SAXNotRecognizedException localSAXNotRecognizedException)
          {
            XMLSecurityManager.printWarning(localXMLReader.getClass().getName(), "http://javax.xml.XMLConstants/feature/secure-processing", localSAXNotRecognizedException);
          }
        }
        catch (Exception localException)
        {
          try
          {
            if (m_parserFactory == null)
            {
              m_parserFactory = FactoryImpl.getSAXFactory(m_useServicesMechanism);
              m_parserFactory.setNamespaceAware(true);
            }
            localXMLReader = m_parserFactory.newSAXParser().getXMLReader();
          }
          catch (ParserConfigurationException localParserConfigurationException2)
          {
            throw localParserConfigurationException2;
          }
        }
        try
        {
          localXMLReader.setFeature("http://xml.org/sax/features/namespaces", true);
          localXMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        }
        catch (SAXException localSAXException1) {}
      }
      catch (ParserConfigurationException localParserConfigurationException1)
      {
        throw new SAXException(localParserConfigurationException1);
      }
      catch (FactoryConfigurationError localFactoryConfigurationError)
      {
        throw new SAXException(localFactoryConfigurationError.toString());
      }
      catch (NoSuchMethodError localNoSuchMethodError) {}catch (AbstractMethodError localAbstractMethodError) {}
      if (i == 0)
      {
        m_readers.set(localXMLReader);
        m_inUse.put(localXMLReader, Boolean.TRUE);
      }
    }
    try
    {
      localXMLReader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", _accessExternalDTD);
    }
    catch (SAXException localSAXException2)
    {
      XMLSecurityManager.printWarning(localXMLReader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", localSAXException2);
    }
    String str2 = "";
    try
    {
      if (_xmlSecurityManager != null)
      {
        for (XMLSecurityManager.Limit localLimit : XMLSecurityManager.Limit.values())
        {
          str2 = localLimit.apiProperty();
          localXMLReader.setProperty(str2, _xmlSecurityManager.getLimitValueAsString(localLimit));
        }
        if (_xmlSecurityManager.printEntityCountInfo())
        {
          str2 = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
          localXMLReader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
        }
      }
    }
    catch (SAXException localSAXException3)
    {
      XMLSecurityManager.printWarning(localXMLReader.getClass().getName(), str2, localSAXException3);
    }
    return localXMLReader;
  }
  
  public synchronized void releaseXMLReader(XMLReader paramXMLReader)
  {
    if ((m_readers.get() == paramXMLReader) && (paramXMLReader != null)) {
      m_inUse.remove(paramXMLReader);
    }
  }
  
  public boolean useServicesMechnism()
  {
    return m_useServicesMechanism;
  }
  
  public void setServicesMechnism(boolean paramBoolean)
  {
    m_useServicesMechanism = paramBoolean;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
  {
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      _secureProcessing = paramBoolean;
    }
  }
  
  public Object getProperty(String paramString)
  {
    if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
      return _accessExternalDTD;
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      return _xmlSecurityManager;
    }
    return null;
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
      _accessExternalDTD = ((String)paramObject);
    } else if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      _xmlSecurityManager = ((XMLSecurityManager)paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\XMLReaderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */