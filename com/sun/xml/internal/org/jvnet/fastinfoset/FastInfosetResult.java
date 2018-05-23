package com.sun.xml.internal.org.jvnet.fastinfoset;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentSerializer;
import java.io.OutputStream;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class FastInfosetResult
  extends SAXResult
{
  OutputStream _outputStream;
  
  public FastInfosetResult(OutputStream paramOutputStream)
  {
    _outputStream = paramOutputStream;
  }
  
  public ContentHandler getHandler()
  {
    Object localObject = super.getHandler();
    if (localObject == null)
    {
      localObject = new SAXDocumentSerializer();
      setHandler((ContentHandler)localObject);
    }
    ((SAXDocumentSerializer)localObject).setOutputStream(_outputStream);
    return (ContentHandler)localObject;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    return (LexicalHandler)getHandler();
  }
  
  public OutputStream getOutputStream()
  {
    return _outputStream;
  }
  
  public void setOutputStream(OutputStream paramOutputStream)
  {
    _outputStream = paramOutputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\FastInfosetResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */