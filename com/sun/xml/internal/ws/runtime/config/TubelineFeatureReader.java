package com.sun.xml.internal.ws.runtime.config;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.config.metro.dev.FeatureReader;
import com.sun.xml.internal.ws.config.metro.util.ParserUtil;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.ws.WebServiceException;

public class TubelineFeatureReader
  implements FeatureReader
{
  private static final Logger LOGGER = Logger.getLogger(TubelineFeatureReader.class);
  private static final QName NAME_ATTRIBUTE_NAME = new QName("name");
  
  public TubelineFeatureReader() {}
  
  public TubelineFeature parse(XMLEventReader paramXMLEventReader)
    throws WebServiceException
  {
    try
    {
      StartElement localStartElement = paramXMLEventReader.nextEvent().asStartElement();
      boolean bool = true;
      Iterator localIterator = localStartElement.getAttributes();
      while (localIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)localIterator.next();
        QName localQName = localAttribute.getName();
        if (ENABLED_ATTRIBUTE_NAME.equals(localQName)) {
          bool = ParserUtil.parseBooleanValue(localAttribute.getValue());
        } else if (!NAME_ATTRIBUTE_NAME.equals(localQName)) {
          throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException("Unexpected attribute")));
        }
      }
      return parseFactories(bool, localStartElement, paramXMLEventReader);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", localXMLStreamException)));
    }
  }
  
  private TubelineFeature parseFactories(boolean paramBoolean, StartElement paramStartElement, XMLEventReader paramXMLEventReader)
    throws WebServiceException
  {
    int i = 0;
    while (paramXMLEventReader.hasNext()) {
      try
      {
        XMLEvent localXMLEvent = paramXMLEventReader.nextEvent();
        switch (localXMLEvent.getEventType())
        {
        case 5: 
          break;
        case 4: 
          if (!localXMLEvent.asCharacters().isWhiteSpace()) {
            throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException("No character data allowed, was " + localXMLEvent.asCharacters())));
          }
          break;
        case 1: 
          i++;
          break;
        case 2: 
          i--;
          if (i < 0)
          {
            EndElement localEndElement = localXMLEvent.asEndElement();
            if (!paramStartElement.getName().equals(localEndElement.getName())) {
              throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException("End element does not match " + localEndElement)));
            }
          }
          break;
        case 3: 
        default: 
          throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException("Unexpected event, was " + localXMLEvent)));
        }
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", localXMLStreamException)));
      }
    }
    return new TubelineFeature(paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubelineFeatureReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */