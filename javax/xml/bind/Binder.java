package javax.xml.bind;

import javax.xml.validation.Schema;

public abstract class Binder<XmlNode>
{
  public Binder() {}
  
  public abstract Object unmarshal(XmlNode paramXmlNode)
    throws JAXBException;
  
  public abstract <T> JAXBElement<T> unmarshal(XmlNode paramXmlNode, Class<T> paramClass)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, XmlNode paramXmlNode)
    throws JAXBException;
  
  public abstract XmlNode getXMLNode(Object paramObject);
  
  public abstract Object getJAXBNode(XmlNode paramXmlNode);
  
  public abstract XmlNode updateXML(Object paramObject)
    throws JAXBException;
  
  public abstract XmlNode updateXML(Object paramObject, XmlNode paramXmlNode)
    throws JAXBException;
  
  public abstract Object updateJAXB(XmlNode paramXmlNode)
    throws JAXBException;
  
  public abstract void setSchema(Schema paramSchema);
  
  public abstract Schema getSchema();
  
  public abstract void setEventHandler(ValidationEventHandler paramValidationEventHandler)
    throws JAXBException;
  
  public abstract ValidationEventHandler getEventHandler()
    throws JAXBException;
  
  public abstract void setProperty(String paramString, Object paramObject)
    throws PropertyException;
  
  public abstract Object getProperty(String paramString)
    throws PropertyException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\Binder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */