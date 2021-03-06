package javax.xml.transform.sax;

import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class SAXResult
  implements Result
{
  public static final String FEATURE = "http://javax.xml.transform.sax.SAXResult/feature";
  private ContentHandler handler;
  private LexicalHandler lexhandler;
  private String systemId;
  
  public SAXResult() {}
  
  public SAXResult(ContentHandler paramContentHandler)
  {
    setHandler(paramContentHandler);
  }
  
  public void setHandler(ContentHandler paramContentHandler)
  {
    handler = paramContentHandler;
  }
  
  public ContentHandler getHandler()
  {
    return handler;
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    lexhandler = paramLexicalHandler;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    return lexhandler;
  }
  
  public void setSystemId(String paramString)
  {
    systemId = paramString;
  }
  
  public String getSystemId()
  {
    return systemId;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\sax\SAXResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */