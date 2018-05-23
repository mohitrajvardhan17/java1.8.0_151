package com.sun.xml.internal.ws.policy.sourcemodel.attach;

import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelUnmarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ExternalAttachmentsUnmarshaller
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ExternalAttachmentsUnmarshaller.class);
  public static final URI BINDING_ID;
  public static final URI BINDING_OPERATION_ID;
  public static final URI BINDING_OPERATION_INPUT_ID;
  public static final URI BINDING_OPERATION_OUTPUT_ID;
  public static final URI BINDING_OPERATION_FAULT_ID;
  private static final QName POLICY_ATTACHMENT = new QName("http://www.w3.org/ns/ws-policy", "PolicyAttachment");
  private static final QName APPLIES_TO = new QName("http://www.w3.org/ns/ws-policy", "AppliesTo");
  private static final QName POLICY = new QName("http://www.w3.org/ns/ws-policy", "Policy");
  private static final QName URI = new QName("http://www.w3.org/ns/ws-policy", "URI");
  private static final QName POLICIES = new QName("http://java.sun.com/xml/ns/metro/management", "Policies");
  private static final ContextClassloaderLocal<XMLInputFactory> XML_INPUT_FACTORY = new ContextClassloaderLocal()
  {
    protected XMLInputFactory initialValue()
      throws Exception
    {
      return XMLInputFactory.newInstance();
    }
  };
  private static final PolicyModelUnmarshaller POLICY_UNMARSHALLER = PolicyModelUnmarshaller.getXmlUnmarshaller();
  private final Map<URI, Policy> map = new HashMap();
  private URI currentUri = null;
  private Policy currentPolicy = null;
  
  public ExternalAttachmentsUnmarshaller() {}
  
  public static Map<URI, Policy> unmarshal(Reader paramReader)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramReader });
    try
    {
      XMLEventReader localXMLEventReader = ((XMLInputFactory)XML_INPUT_FACTORY.get()).createXMLEventReader(paramReader);
      ExternalAttachmentsUnmarshaller localExternalAttachmentsUnmarshaller = new ExternalAttachmentsUnmarshaller();
      Map localMap = localExternalAttachmentsUnmarshaller.unmarshal(localXMLEventReader, null);
      LOGGER.exiting(localMap);
      return Collections.unmodifiableMap(localMap);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0086_FAILED_CREATE_READER(paramReader)), localXMLStreamException));
    }
  }
  
  private Map<URI, Policy> unmarshal(XMLEventReader paramXMLEventReader, StartElement paramStartElement)
    throws PolicyException
  {
    XMLEvent localXMLEvent = null;
    while (paramXMLEventReader.hasNext()) {
      try
      {
        localXMLEvent = paramXMLEventReader.peek();
        switch (localXMLEvent.getEventType())
        {
        case 5: 
        case 7: 
          paramXMLEventReader.nextEvent();
          break;
        case 4: 
          processCharacters(localXMLEvent.asCharacters(), paramStartElement, map);
          paramXMLEventReader.nextEvent();
          break;
        case 2: 
          processEndTag(localXMLEvent.asEndElement(), paramStartElement);
          paramXMLEventReader.nextEvent();
          return map;
        case 1: 
          StartElement localStartElement = localXMLEvent.asStartElement();
          processStartTag(localStartElement, paramStartElement, paramXMLEventReader, map);
          break;
        case 8: 
          return map;
        case 3: 
        case 6: 
        default: 
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0087_UNKNOWN_EVENT(localXMLEvent))));
        }
      }
      catch (XMLStreamException localXMLStreamException)
      {
        Location localLocation = localXMLEvent == null ? null : localXMLEvent.getLocation();
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(localLocation)), localXMLStreamException));
      }
    }
    return map;
  }
  
  private void processStartTag(StartElement paramStartElement1, StartElement paramStartElement2, XMLEventReader paramXMLEventReader, Map<URI, Policy> paramMap)
    throws PolicyException
  {
    try
    {
      QName localQName1 = paramStartElement1.getName();
      if (paramStartElement2 == null)
      {
        if (!localQName1.equals(POLICIES)) {
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<Policies>", localQName1, paramStartElement1.getLocation()))));
        }
      }
      else
      {
        QName localQName2 = paramStartElement2.getName();
        if (localQName2.equals(POLICIES))
        {
          if (!localQName1.equals(POLICY_ATTACHMENT)) {
            throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<PolicyAttachment>", localQName1, paramStartElement1.getLocation()))));
          }
        }
        else if (localQName2.equals(POLICY_ATTACHMENT))
        {
          if (localQName1.equals(POLICY))
          {
            readPolicy(paramXMLEventReader);
            return;
          }
          if (!localQName1.equals(APPLIES_TO)) {
            throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<AppliesTo> or <Policy>", localQName1, paramStartElement1.getLocation()))));
          }
        }
        else if (localQName2.equals(APPLIES_TO))
        {
          if (!localQName1.equals(URI)) {
            throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<URI>", localQName1, paramStartElement1.getLocation()))));
          }
        }
        else
        {
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0090_UNEXPECTED_ELEMENT(localQName1, paramStartElement1.getLocation()))));
        }
      }
      paramXMLEventReader.nextEvent();
      unmarshal(paramXMLEventReader, paramStartElement1);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(paramStartElement1.getLocation()), localXMLStreamException)));
    }
  }
  
  private void readPolicy(XMLEventReader paramXMLEventReader)
    throws PolicyException
  {
    PolicySourceModel localPolicySourceModel = POLICY_UNMARSHALLER.unmarshalModel(paramXMLEventReader);
    PolicyModelTranslator localPolicyModelTranslator = PolicyModelTranslator.getTranslator();
    Policy localPolicy = localPolicyModelTranslator.translate(localPolicySourceModel);
    if (currentUri != null)
    {
      map.put(currentUri, localPolicy);
      currentUri = null;
      currentPolicy = null;
    }
    else
    {
      currentPolicy = localPolicy;
    }
  }
  
  private void processEndTag(EndElement paramEndElement, StartElement paramStartElement)
    throws PolicyException
  {
    checkEndTagName(paramStartElement.getName(), paramEndElement);
  }
  
  private void checkEndTagName(QName paramQName, EndElement paramEndElement)
    throws PolicyException
  {
    QName localQName = paramEndElement.getName();
    if (!paramQName.equals(localQName)) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0091_END_ELEMENT_NO_MATCH(paramQName, paramEndElement, paramEndElement.getLocation()))));
    }
  }
  
  private void processCharacters(Characters paramCharacters, StartElement paramStartElement, Map<URI, Policy> paramMap)
    throws PolicyException
  {
    if (paramCharacters.isWhiteSpace()) {
      return;
    }
    String str = paramCharacters.getData();
    if ((paramStartElement != null) && (URI.equals(paramStartElement.getName())))
    {
      processUri(paramCharacters, paramMap);
      return;
    }
    throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0092_CHARACTER_DATA_UNEXPECTED(paramStartElement, str, paramCharacters.getLocation()))));
  }
  
  private void processUri(Characters paramCharacters, Map<URI, Policy> paramMap)
    throws PolicyException
  {
    String str = paramCharacters.getData().trim();
    try
    {
      URI localURI = new URI(str);
      if (currentPolicy != null)
      {
        paramMap.put(localURI, currentPolicy);
        currentUri = null;
        currentPolicy = null;
      }
      else
      {
        currentUri = localURI;
      }
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0093_INVALID_URI(str, paramCharacters.getLocation())), localURISyntaxException));
    }
  }
  
  static
  {
    try
    {
      BINDING_ID = new URI("urn:uuid:c9bef600-0d7a-11de-abc1-0002a5d5c51b");
      BINDING_OPERATION_ID = new URI("urn:uuid:62e66b60-0d7b-11de-a1a2-0002a5d5c51b");
      BINDING_OPERATION_INPUT_ID = new URI("urn:uuid:730d8d20-0d7b-11de-84e9-0002a5d5c51b");
      BINDING_OPERATION_OUTPUT_ID = new URI("urn:uuid:85b0f980-0d7b-11de-8e9d-0002a5d5c51b");
      BINDING_OPERATION_FAULT_ID = new URI("urn:uuid:917cb060-0d7b-11de-9e80-0002a5d5c51b");
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0094_INVALID_URN()), localURISyntaxException));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\attach\ExternalAttachmentsUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */