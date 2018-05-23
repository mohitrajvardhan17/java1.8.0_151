package javax.xml.bind;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public abstract interface Marshaller
{
  public static final String JAXB_ENCODING = "jaxb.encoding";
  public static final String JAXB_FORMATTED_OUTPUT = "jaxb.formatted.output";
  public static final String JAXB_SCHEMA_LOCATION = "jaxb.schemaLocation";
  public static final String JAXB_NO_NAMESPACE_SCHEMA_LOCATION = "jaxb.noNamespaceSchemaLocation";
  public static final String JAXB_FRAGMENT = "jaxb.fragment";
  
  public abstract void marshal(Object paramObject, Result paramResult)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, OutputStream paramOutputStream)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, File paramFile)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, Writer paramWriter)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, ContentHandler paramContentHandler)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, Node paramNode)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException;
  
  public abstract void marshal(Object paramObject, XMLEventWriter paramXMLEventWriter)
    throws JAXBException;
  
  public abstract Node getNode(Object paramObject)
    throws JAXBException;
  
  public abstract void setProperty(String paramString, Object paramObject)
    throws PropertyException;
  
  public abstract Object getProperty(String paramString)
    throws PropertyException;
  
  public abstract void setEventHandler(ValidationEventHandler paramValidationEventHandler)
    throws JAXBException;
  
  public abstract ValidationEventHandler getEventHandler()
    throws JAXBException;
  
  public abstract void setAdapter(XmlAdapter paramXmlAdapter);
  
  public abstract <A extends XmlAdapter> void setAdapter(Class<A> paramClass, A paramA);
  
  public abstract <A extends XmlAdapter> A getAdapter(Class<A> paramClass);
  
  public abstract void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller);
  
  public abstract AttachmentMarshaller getAttachmentMarshaller();
  
  public abstract void setSchema(Schema paramSchema);
  
  public abstract Schema getSchema();
  
  public abstract void setListener(Listener paramListener);
  
  public abstract Listener getListener();
  
  public static abstract class Listener
  {
    public Listener() {}
    
    public void beforeMarshal(Object paramObject) {}
    
    public void afterMarshal(Object paramObject) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\Marshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */