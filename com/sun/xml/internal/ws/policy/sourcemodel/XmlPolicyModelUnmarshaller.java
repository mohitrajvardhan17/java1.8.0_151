package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XmlPolicyModelUnmarshaller
  extends PolicyModelUnmarshaller
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelUnmarshaller.class);
  
  protected XmlPolicyModelUnmarshaller() {}
  
  public PolicySourceModel unmarshalModel(Object paramObject)
    throws PolicyException
  {
    XMLEventReader localXMLEventReader = createXMLEventReader(paramObject);
    PolicySourceModel localPolicySourceModel = null;
    while (localXMLEventReader.hasNext()) {
      try
      {
        XMLEvent localXMLEvent = localXMLEventReader.peek();
        switch (localXMLEvent.getEventType())
        {
        case 5: 
        case 7: 
          localXMLEventReader.nextEvent();
          break;
        case 4: 
          processCharacters(ModelNode.Type.POLICY, localXMLEvent.asCharacters(), null);
          localXMLEventReader.nextEvent();
          break;
        case 1: 
          if (NamespaceVersion.resolveAsToken(localXMLEvent.asStartElement().getName()) == XmlToken.Policy)
          {
            StartElement localStartElement = localXMLEventReader.nextEvent().asStartElement();
            localPolicySourceModel = initializeNewModel(localStartElement);
            unmarshalNodeContent(localPolicySourceModel.getNamespaceVersion(), localPolicySourceModel.getRootNode(), localStartElement.getName(), localXMLEventReader);
          }
          else
          {
            throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST())));
          }
          break;
        case 2: 
        case 3: 
        case 6: 
        default: 
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST())));
        }
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), localXMLStreamException)));
      }
    }
    return localPolicySourceModel;
  }
  
  protected PolicySourceModel createSourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    return PolicySourceModel.createPolicySourceModel(paramNamespaceVersion, paramString1, paramString2);
  }
  
  private PolicySourceModel initializeNewModel(StartElement paramStartElement)
    throws PolicyException, XMLStreamException
  {
    NamespaceVersion localNamespaceVersion = NamespaceVersion.resolveVersion(paramStartElement.getName().getNamespaceURI());
    Attribute localAttribute1 = getAttributeByName(paramStartElement, localNamespaceVersion.asQName(XmlToken.Name));
    Attribute localAttribute2 = getAttributeByName(paramStartElement, PolicyConstants.XML_ID);
    Attribute localAttribute3 = getAttributeByName(paramStartElement, PolicyConstants.WSU_ID);
    if (localAttribute3 == null) {
      localAttribute3 = localAttribute2;
    } else if (localAttribute2 != null) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED())));
    }
    PolicySourceModel localPolicySourceModel = createSourceModel(localNamespaceVersion, localAttribute3 == null ? null : localAttribute3.getValue(), localAttribute1 == null ? null : localAttribute1.getValue());
    return localPolicySourceModel;
  }
  
  private ModelNode addNewChildNode(NamespaceVersion paramNamespaceVersion, ModelNode paramModelNode, StartElement paramStartElement)
    throws PolicyException
  {
    QName localQName = paramStartElement.getName();
    ModelNode localModelNode;
    if (paramModelNode.getType() == ModelNode.Type.ASSERTION_PARAMETER_NODE)
    {
      localModelNode = paramModelNode.createChildAssertionParameterNode();
    }
    else
    {
      XmlToken localXmlToken = NamespaceVersion.resolveAsToken(localQName);
      switch (localXmlToken)
      {
      case Policy: 
        localModelNode = paramModelNode.createChildPolicyNode();
        break;
      case All: 
        localModelNode = paramModelNode.createChildAllNode();
        break;
      case ExactlyOne: 
        localModelNode = paramModelNode.createChildExactlyOneNode();
        break;
      case PolicyReference: 
        Attribute localAttribute1 = getAttributeByName(paramStartElement, paramNamespaceVersion.asQName(XmlToken.Uri));
        if (localAttribute1 == null) {
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND())));
        }
        try
        {
          URI localURI1 = new URI(localAttribute1.getValue());
          Attribute localAttribute2 = getAttributeByName(paramStartElement, paramNamespaceVersion.asQName(XmlToken.Digest));
          PolicyReferenceData localPolicyReferenceData;
          if (localAttribute2 == null)
          {
            localPolicyReferenceData = new PolicyReferenceData(localURI1);
          }
          else
          {
            Attribute localAttribute3 = getAttributeByName(paramStartElement, paramNamespaceVersion.asQName(XmlToken.DigestAlgorithm));
            URI localURI2 = null;
            if (localAttribute3 != null) {
              localURI2 = new URI(localAttribute3.getValue());
            }
            localPolicyReferenceData = new PolicyReferenceData(localURI1, localAttribute2.getValue(), localURI2);
          }
          localModelNode = paramModelNode.createChildPolicyReferenceNode(localPolicyReferenceData);
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI(), localURISyntaxException)));
        }
      default: 
        if (paramModelNode.isDomainSpecific()) {
          localModelNode = paramModelNode.createChildAssertionParameterNode();
        } else {
          localModelNode = paramModelNode.createChildAssertionNode();
        }
        break;
      }
    }
    return localModelNode;
  }
  
  private void parseAssertionData(NamespaceVersion paramNamespaceVersion, String paramString, ModelNode paramModelNode, StartElement paramStartElement)
    throws IllegalArgumentException, PolicyException
  {
    HashMap localHashMap = new HashMap();
    boolean bool1 = false;
    boolean bool2 = false;
    Iterator localIterator = paramStartElement.getAttributes();
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject1 = (Attribute)localIterator.next();
      localObject2 = ((Attribute)localObject1).getName();
      if (localHashMap.containsKey(localObject2)) {
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(((Attribute)localObject1).getName(), paramStartElement.getName()))));
      }
      if (paramNamespaceVersion.asQName(XmlToken.Optional).equals(localObject2)) {
        bool1 = parseBooleanValue(((Attribute)localObject1).getValue());
      } else if (paramNamespaceVersion.asQName(XmlToken.Ignorable).equals(localObject2)) {
        bool2 = parseBooleanValue(((Attribute)localObject1).getValue());
      } else {
        localHashMap.put(localObject2, ((Attribute)localObject1).getValue());
      }
    }
    Object localObject1 = new AssertionData(paramStartElement.getName(), paramString, localHashMap, paramModelNode.getType(), bool1, bool2);
    if (((AssertionData)localObject1).containsAttribute(PolicyConstants.VISIBILITY_ATTRIBUTE))
    {
      localObject2 = ((AssertionData)localObject1).getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE);
      if (!"private".equals(localObject2)) {
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(localObject2))));
      }
    }
    paramModelNode.setOrReplaceNodeData((AssertionData)localObject1);
  }
  
  private Attribute getAttributeByName(StartElement paramStartElement, QName paramQName)
  {
    Object localObject = paramStartElement.getAttributeByName(paramQName);
    if (localObject == null)
    {
      String str = paramQName.getLocalPart();
      Iterator localIterator = paramStartElement.getAttributes();
      while (localIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)localIterator.next();
        QName localQName = localAttribute.getName();
        int i = (localQName.equals(paramQName)) || ((localQName.getLocalPart().equals(str)) && ((localQName.getPrefix() == null) || ("".equals(localQName.getPrefix())))) ? 1 : 0;
        if (i != 0)
        {
          localObject = localAttribute;
          break;
        }
      }
    }
    return (Attribute)localObject;
  }
  
  private String unmarshalNodeContent(NamespaceVersion paramNamespaceVersion, ModelNode paramModelNode, QName paramQName, XMLEventReader paramXMLEventReader)
    throws PolicyException
  {
    StringBuilder localStringBuilder = null;
    while (paramXMLEventReader.hasNext()) {
      try
      {
        XMLEvent localXMLEvent = paramXMLEventReader.nextEvent();
        switch (localXMLEvent.getEventType())
        {
        case 5: 
          break;
        case 4: 
          localStringBuilder = processCharacters(paramModelNode.getType(), localXMLEvent.asCharacters(), localStringBuilder);
          break;
        case 2: 
          checkEndTagName(paramQName, localXMLEvent.asEndElement());
          break;
        case 1: 
          StartElement localStartElement = localXMLEvent.asStartElement();
          ModelNode localModelNode = addNewChildNode(paramNamespaceVersion, paramModelNode, localStartElement);
          String str = unmarshalNodeContent(paramNamespaceVersion, localModelNode, localStartElement.getName(), paramXMLEventReader);
          if (localModelNode.isDomainSpecific()) {
            parseAssertionData(paramNamespaceVersion, str, localModelNode, localStartElement);
          }
          break;
        case 3: 
        default: 
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED())));
        }
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), localXMLStreamException)));
      }
    }
    return localStringBuilder == null ? null : localStringBuilder.toString().trim();
  }
  
  private XMLEventReader createXMLEventReader(Object paramObject)
    throws PolicyException
  {
    if ((paramObject instanceof XMLEventReader)) {
      return (XMLEventReader)paramObject;
    }
    if (!(paramObject instanceof Reader)) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(paramObject.getClass().getName()))));
    }
    try
    {
      return XMLInputFactory.newInstance().createXMLEventReader((Reader)paramObject);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE(), localXMLStreamException)));
    }
  }
  
  private void checkEndTagName(QName paramQName, EndElement paramEndElement)
    throws PolicyException
  {
    QName localQName = paramEndElement.getName();
    if (!paramQName.equals(localQName)) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(paramQName, localQName))));
    }
  }
  
  private StringBuilder processCharacters(ModelNode.Type paramType, Characters paramCharacters, StringBuilder paramStringBuilder)
    throws PolicyException
  {
    if (paramCharacters.isWhiteSpace()) {
      return paramStringBuilder;
    }
    StringBuilder localStringBuilder = paramStringBuilder == null ? new StringBuilder() : paramStringBuilder;
    String str = paramCharacters.getData();
    if ((paramType == ModelNode.Type.ASSERTION) || (paramType == ModelNode.Type.ASSERTION_PARAMETER_NODE)) {
      return localStringBuilder.append(str);
    }
    throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE(paramType, str))));
  }
  
  private boolean parseBooleanValue(String paramString)
    throws PolicyException
  {
    if (("true".equals(paramString)) || ("1".equals(paramString))) {
      return true;
    }
    if (("false".equals(paramString)) || ("0".equals(paramString))) {
      return false;
    }
    throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0095_INVALID_BOOLEAN_VALUE(paramString))));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\XmlPolicyModelUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */