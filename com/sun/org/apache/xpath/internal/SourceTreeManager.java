package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SourceTreeManager
{
  private Vector m_sourceTree = new Vector();
  URIResolver m_uriResolver;
  
  public SourceTreeManager() {}
  
  public void reset()
  {
    m_sourceTree = new Vector();
  }
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    m_uriResolver = paramURIResolver;
  }
  
  public URIResolver getURIResolver()
  {
    return m_uriResolver;
  }
  
  public String findURIFromDoc(int paramInt)
  {
    int i = m_sourceTree.size();
    for (int j = 0; j < i; j++)
    {
      SourceTree localSourceTree = (SourceTree)m_sourceTree.elementAt(j);
      if (paramInt == m_root) {
        return m_url;
      }
    }
    return null;
  }
  
  public Source resolveURI(String paramString1, String paramString2, SourceLocator paramSourceLocator)
    throws TransformerException, IOException
  {
    Object localObject = null;
    if (null != m_uriResolver) {
      localObject = m_uriResolver.resolve(paramString2, paramString1);
    }
    if (null == localObject)
    {
      String str = SystemIDResolver.getAbsoluteURI(paramString2, paramString1);
      localObject = new StreamSource(str);
    }
    return (Source)localObject;
  }
  
  public void removeDocumentFromCache(int paramInt)
  {
    if (-1 == paramInt) {
      return;
    }
    for (int i = m_sourceTree.size() - 1; i >= 0; i--)
    {
      SourceTree localSourceTree = (SourceTree)m_sourceTree.elementAt(i);
      if ((localSourceTree != null) && (m_root == paramInt))
      {
        m_sourceTree.removeElementAt(i);
        return;
      }
    }
  }
  
  public void putDocumentInCache(int paramInt, Source paramSource)
  {
    int i = getNode(paramSource);
    if (-1 != i)
    {
      if (i != paramInt) {
        throw new RuntimeException("Programmer's Error!  putDocumentInCache found reparse of doc: " + paramSource.getSystemId());
      }
      return;
    }
    if (null != paramSource.getSystemId()) {
      m_sourceTree.addElement(new SourceTree(paramInt, paramSource.getSystemId()));
    }
  }
  
  public int getNode(Source paramSource)
  {
    String str = paramSource.getSystemId();
    if (null == str) {
      return -1;
    }
    int i = m_sourceTree.size();
    for (int j = 0; j < i; j++)
    {
      SourceTree localSourceTree = (SourceTree)m_sourceTree.elementAt(j);
      if (str.equals(m_url)) {
        return m_root;
      }
    }
    return -1;
  }
  
  public int getSourceTree(String paramString1, String paramString2, SourceLocator paramSourceLocator, XPathContext paramXPathContext)
    throws TransformerException
  {
    try
    {
      Source localSource = resolveURI(paramString1, paramString2, paramSourceLocator);
      return getSourceTree(localSource, paramSourceLocator, paramXPathContext);
    }
    catch (IOException localIOException)
    {
      throw new TransformerException(localIOException.getMessage(), paramSourceLocator, localIOException);
    }
  }
  
  public int getSourceTree(Source paramSource, SourceLocator paramSourceLocator, XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = getNode(paramSource);
    if (-1 != i) {
      return i;
    }
    i = parseToNode(paramSource, paramSourceLocator, paramXPathContext);
    if (-1 != i) {
      putDocumentInCache(i, paramSource);
    }
    return i;
  }
  
  public int parseToNode(Source paramSource, SourceLocator paramSourceLocator, XPathContext paramXPathContext)
    throws TransformerException
  {
    try
    {
      Object localObject = paramXPathContext.getOwnerObject();
      DTM localDTM;
      if ((null != localObject) && ((localObject instanceof DTMWSFilter))) {
        localDTM = paramXPathContext.getDTM(paramSource, false, (DTMWSFilter)localObject, false, true);
      } else {
        localDTM = paramXPathContext.getDTM(paramSource, false, null, false, true);
      }
      return localDTM.getDocument();
    }
    catch (Exception localException)
    {
      throw new TransformerException(localException.getMessage(), paramSourceLocator, localException);
    }
  }
  
  public static XMLReader getXMLReader(Source paramSource, SourceLocator paramSourceLocator)
    throws TransformerException
  {
    try
    {
      XMLReader localXMLReader = (paramSource instanceof SAXSource) ? ((SAXSource)paramSource).getXMLReader() : null;
      if (null == localXMLReader)
      {
        try
        {
          SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
          localSAXParserFactory.setNamespaceAware(true);
          SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
          localXMLReader = localSAXParser.getXMLReader();
        }
        catch (ParserConfigurationException localParserConfigurationException)
        {
          throw new SAXException(localParserConfigurationException);
        }
        catch (FactoryConfigurationError localFactoryConfigurationError)
        {
          throw new SAXException(localFactoryConfigurationError.toString());
        }
        catch (NoSuchMethodError localNoSuchMethodError) {}catch (AbstractMethodError localAbstractMethodError) {}
        if (null == localXMLReader) {
          localXMLReader = XMLReaderFactory.createXMLReader();
        }
      }
      try
      {
        localXMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      }
      catch (SAXException localSAXException2) {}
      return localXMLReader;
    }
    catch (SAXException localSAXException1)
    {
      throw new TransformerException(localSAXException1.getMessage(), paramSourceLocator, localSAXException1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\SourceTreeManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */