package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;

public final class XSLTCSource
  implements Source
{
  private String _systemId = null;
  private Source _source = null;
  private ThreadLocal _dom = new ThreadLocal();
  
  public XSLTCSource(String paramString)
  {
    _systemId = paramString;
  }
  
  public XSLTCSource(Source paramSource)
  {
    _source = paramSource;
  }
  
  public void setSystemId(String paramString)
  {
    _systemId = paramString;
    if (_source != null) {
      _source.setSystemId(paramString);
    }
  }
  
  public String getSystemId()
  {
    if (_source != null) {
      return _source.getSystemId();
    }
    return _systemId;
  }
  
  protected DOM getDOM(XSLTCDTMManager paramXSLTCDTMManager, AbstractTranslet paramAbstractTranslet)
    throws SAXException
  {
    SAXImpl localSAXImpl = (SAXImpl)_dom.get();
    if (localSAXImpl != null)
    {
      if (paramXSLTCDTMManager != null) {
        localSAXImpl.migrateTo(paramXSLTCDTMManager);
      }
    }
    else
    {
      Object localObject1 = _source;
      if (localObject1 == null) {
        if ((_systemId != null) && (_systemId.length() > 0))
        {
          localObject1 = new StreamSource(_systemId);
        }
        else
        {
          localObject2 = new ErrorMsg("XSLTC_SOURCE_ERR");
          throw new SAXException(((ErrorMsg)localObject2).toString());
        }
      }
      Object localObject2 = null;
      if ((paramAbstractTranslet != null) && ((paramAbstractTranslet instanceof StripFilter))) {
        localObject2 = new DOMWSFilter(paramAbstractTranslet);
      }
      boolean bool = paramAbstractTranslet != null ? paramAbstractTranslet.hasIdCall() : false;
      if (paramXSLTCDTMManager == null) {
        paramXSLTCDTMManager = XSLTCDTMManager.newInstance();
      }
      localSAXImpl = (SAXImpl)paramXSLTCDTMManager.getDTM((Source)localObject1, true, (DTMWSFilter)localObject2, false, false, bool);
      String str = getSystemId();
      if (str != null) {
        localSAXImpl.setDocumentURI(str);
      }
      _dom.set(localSAXImpl);
    }
    return localSAXImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\XSLTCSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */