package com.sun.xml.internal.org.jvnet.fastinfoset;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import java.io.InputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FastInfosetSource
  extends SAXSource
{
  public FastInfosetSource(InputStream paramInputStream)
  {
    super(new InputSource(paramInputStream));
  }
  
  public XMLReader getXMLReader()
  {
    Object localObject = super.getXMLReader();
    if (localObject == null)
    {
      localObject = new SAXDocumentParser();
      setXMLReader((XMLReader)localObject);
    }
    ((SAXDocumentParser)localObject).setInputStream(getInputStream());
    return (XMLReader)localObject;
  }
  
  public InputStream getInputStream()
  {
    return getInputSource().getByteStream();
  }
  
  public void setInputStream(InputStream paramInputStream)
  {
    setInputSource(new InputSource(paramInputStream));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\FastInfosetSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */