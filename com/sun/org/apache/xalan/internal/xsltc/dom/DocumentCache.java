package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class DocumentCache
  implements DOMCache
{
  private int _size;
  private Map<String, CachedDocument> _references;
  private String[] _URIs;
  private int _count;
  private int _current;
  private SAXParser _parser;
  private XMLReader _reader;
  private XSLTCDTMManager _dtmManager;
  private static final int REFRESH_INTERVAL = 1000;
  
  public DocumentCache(int paramInt)
    throws SAXException
  {
    this(paramInt, null);
    try
    {
      _dtmManager = XSLTCDTMManager.createNewDTMManagerInstance();
    }
    catch (Exception localException)
    {
      throw new SAXException(localException);
    }
  }
  
  public DocumentCache(int paramInt, XSLTCDTMManager paramXSLTCDTMManager)
    throws SAXException
  {
    _dtmManager = paramXSLTCDTMManager;
    _count = 0;
    _current = 0;
    _size = paramInt;
    _references = new HashMap(_size + 2);
    _URIs = new String[_size];
    try
    {
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      try
      {
        localSAXParserFactory.setFeature("http://xml.org/sax/features/namespaces", true);
      }
      catch (Exception localException)
      {
        localSAXParserFactory.setNamespaceAware(true);
      }
      _parser = localSAXParserFactory.newSAXParser();
      _reader = _parser.getXMLReader();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      BasisLibrary.runTimeError("NAMESPACES_SUPPORT_ERR");
    }
  }
  
  private final long getLastModified(String paramString)
  {
    try
    {
      URL localURL = new URL(paramString);
      URLConnection localURLConnection = localURL.openConnection();
      long l = localURLConnection.getLastModified();
      if ((l == 0L) && ("file".equals(localURL.getProtocol())))
      {
        File localFile = new File(URLDecoder.decode(localURL.getFile()));
        l = localFile.lastModified();
      }
      return l;
    }
    catch (Exception localException) {}
    return System.currentTimeMillis();
  }
  
  private CachedDocument lookupDocument(String paramString)
  {
    return (CachedDocument)_references.get(paramString);
  }
  
  private synchronized void insertDocument(String paramString, CachedDocument paramCachedDocument)
  {
    if (_count < _size)
    {
      _URIs[(_count++)] = paramString;
      _current = 0;
    }
    else
    {
      _references.remove(_URIs[_current]);
      _URIs[_current] = paramString;
      if (++_current >= _size) {
        _current = 0;
      }
    }
    _references.put(paramString, paramCachedDocument);
  }
  
  private synchronized void replaceDocument(String paramString, CachedDocument paramCachedDocument)
  {
    if (paramCachedDocument == null) {
      insertDocument(paramString, paramCachedDocument);
    } else {
      _references.put(paramString, paramCachedDocument);
    }
  }
  
  public DOM retrieveDocument(String paramString1, String paramString2, Translet paramTranslet)
  {
    String str = paramString2;
    if ((paramString1 != null) && (!paramString1.equals(""))) {
      try
      {
        str = SystemIDResolver.getAbsoluteURI(str, paramString1);
      }
      catch (TransformerException localTransformerException) {}
    }
    CachedDocument localCachedDocument;
    if ((localCachedDocument = lookupDocument(str)) == null)
    {
      localCachedDocument = new CachedDocument(str);
      if (localCachedDocument == null) {
        return null;
      }
      localCachedDocument.setLastModified(getLastModified(str));
      insertDocument(str, localCachedDocument);
    }
    else
    {
      long l1 = System.currentTimeMillis();
      long l2 = localCachedDocument.getLastChecked();
      localCachedDocument.setLastChecked(l1);
      if (l1 > l2 + 1000L)
      {
        localCachedDocument.setLastChecked(l1);
        long l3 = getLastModified(str);
        if (l3 > localCachedDocument.getLastModified())
        {
          localCachedDocument = new CachedDocument(str);
          if (localCachedDocument == null) {
            return null;
          }
          localCachedDocument.setLastModified(getLastModified(str));
          replaceDocument(str, localCachedDocument);
        }
      }
    }
    DOM localDOM = localCachedDocument.getDocument();
    if (localDOM == null) {
      return null;
    }
    localCachedDocument.incAccessCount();
    AbstractTranslet localAbstractTranslet = (AbstractTranslet)paramTranslet;
    localAbstractTranslet.prepassDocument(localDOM);
    return localCachedDocument.getDocument();
  }
  
  public void getStatistics(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("<h2>DOM cache statistics</h2><center><table border=\"2\"><tr><td><b>Document URI</b></td><td><center><b>Build time</b></center></td><td><center><b>Access count</b></center></td><td><center><b>Last accessed</b></center></td><td><center><b>Last modified</b></center></td></tr>");
    for (int i = 0; i < _count; i++)
    {
      CachedDocument localCachedDocument = (CachedDocument)_references.get(_URIs[i]);
      paramPrintWriter.print("<tr><td><a href=\"" + _URIs[i] + "\"><font size=-1>" + _URIs[i] + "</font></a></td>");
      paramPrintWriter.print("<td><center>" + localCachedDocument.getLatency() + "ms</center></td>");
      paramPrintWriter.print("<td><center>" + localCachedDocument.getAccessCount() + "</center></td>");
      paramPrintWriter.print("<td><center>" + new Date(localCachedDocument.getLastReferenced()) + "</center></td>");
      paramPrintWriter.print("<td><center>" + new Date(localCachedDocument.getLastModified()) + "</center></td>");
      paramPrintWriter.println("</tr>");
    }
    paramPrintWriter.println("</table></center>");
  }
  
  public final class CachedDocument
  {
    private long _firstReferenced;
    private long _lastReferenced;
    private long _accessCount;
    private long _lastModified;
    private long _lastChecked;
    private long _buildTime;
    private DOMEnhancedForDTM _dom = null;
    
    public CachedDocument(String paramString)
    {
      long l = System.currentTimeMillis();
      _firstReferenced = l;
      _lastReferenced = l;
      _accessCount = 0L;
      loadDocument(paramString);
      _buildTime = (System.currentTimeMillis() - l);
    }
    
    public void loadDocument(String paramString)
    {
      try
      {
        long l1 = System.currentTimeMillis();
        _dom = ((DOMEnhancedForDTM)_dtmManager.getDTM(new SAXSource(_reader, new InputSource(paramString)), false, null, true, false));
        _dom.setDocumentURI(paramString);
        long l2 = System.currentTimeMillis() - l1;
        if (_buildTime > 0L) {
          _buildTime = (_buildTime + l2 >>> 1);
        } else {
          _buildTime = l2;
        }
      }
      catch (Exception localException)
      {
        _dom = null;
      }
    }
    
    public DOM getDocument()
    {
      return _dom;
    }
    
    public long getFirstReferenced()
    {
      return _firstReferenced;
    }
    
    public long getLastReferenced()
    {
      return _lastReferenced;
    }
    
    public long getAccessCount()
    {
      return _accessCount;
    }
    
    public void incAccessCount()
    {
      _accessCount += 1L;
    }
    
    public long getLastModified()
    {
      return _lastModified;
    }
    
    public void setLastModified(long paramLong)
    {
      _lastModified = paramLong;
    }
    
    public long getLatency()
    {
      return _buildTime;
    }
    
    public long getLastChecked()
    {
      return _lastChecked;
    }
    
    public void setLastChecked(long paramLong)
    {
      _lastChecked = paramLong;
    }
    
    public long getEstimatedSize()
    {
      if (_dom != null) {
        return _dom.getSize() << 5;
      }
      return 0L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\DocumentCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */