package javax.xml.transform.stax;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public class StAXResult
  implements Result
{
  public static final String FEATURE = "http://javax.xml.transform.stax.StAXResult/feature";
  private XMLEventWriter xmlEventWriter = null;
  private XMLStreamWriter xmlStreamWriter = null;
  private String systemId = null;
  
  public StAXResult(XMLEventWriter paramXMLEventWriter)
  {
    if (paramXMLEventWriter == null) {
      throw new IllegalArgumentException("StAXResult(XMLEventWriter) with XMLEventWriter == null");
    }
    xmlEventWriter = paramXMLEventWriter;
  }
  
  public StAXResult(XMLStreamWriter paramXMLStreamWriter)
  {
    if (paramXMLStreamWriter == null) {
      throw new IllegalArgumentException("StAXResult(XMLStreamWriter) with XMLStreamWriter == null");
    }
    xmlStreamWriter = paramXMLStreamWriter;
  }
  
  public XMLEventWriter getXMLEventWriter()
  {
    return xmlEventWriter;
  }
  
  public XMLStreamWriter getXMLStreamWriter()
  {
    return xmlStreamWriter;
  }
  
  public void setSystemId(String paramString)
  {
    throw new UnsupportedOperationException("StAXResult#setSystemId(systemId) cannot set the system identifier for a StAXResult");
  }
  
  public String getSystemId()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\stax\StAXResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */