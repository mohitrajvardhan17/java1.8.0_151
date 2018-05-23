package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLParserExtension
  extends WSDLParserExtension
{
  protected static final String COLON_DELIMITER = ":";
  protected static final String SLASH_DELIMITER = "/";
  
  public W3CAddressingWSDLParserExtension() {}
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader)
  {
    return addressibleElement(paramXMLStreamReader, paramEditableWSDLBoundPortType);
  }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader)
  {
    return addressibleElement(paramXMLStreamReader, paramEditableWSDLPort);
  }
  
  private boolean addressibleElement(XMLStreamReader paramXMLStreamReader, WSDLFeaturedObject paramWSDLFeaturedObject)
  {
    QName localQName = paramXMLStreamReader.getName();
    if (localQName.equals(W3CwsdlExtensionTag))
    {
      String str = paramXMLStreamReader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
      paramWSDLFeaturedObject.addFeature(new AddressingFeature(true, Boolean.parseBoolean(str)));
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return true;
    }
    return false;
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    EditableWSDLBoundOperation localEditableWSDLBoundOperation = paramEditableWSDLBoundOperation;
    QName localQName = paramXMLStreamReader.getName();
    if (localQName.equals(W3CwsdlAnonymousTag))
    {
      try
      {
        String str = paramXMLStreamReader.getElementText();
        if ((str == null) || (str.trim().equals(""))) {
          throw new WebServiceException("Null values not permitted in wsaw:Anonymous.");
        }
        if (str.equals("optional")) {
          localEditableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
        } else if (str.equals("required")) {
          localEditableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.required);
        } else if (str.equals("prohibited")) {
          localEditableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.prohibited);
        } else {
          throw new WebServiceException("wsaw:Anonymous value \"" + str + "\" not understood.");
        }
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
      return true;
    }
    return false;
  }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader)
  {
    String str = ParserUtil.getAttribute(paramXMLStreamReader, getWsdlActionTag());
    if (str != null)
    {
      paramEditableWSDLInput.setAction(str);
      paramEditableWSDLInput.setDefaultAction(false);
    }
  }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader)
  {
    String str = ParserUtil.getAttribute(paramXMLStreamReader, getWsdlActionTag());
    if (str != null)
    {
      paramEditableWSDLOutput.setAction(str);
      paramEditableWSDLOutput.setDefaultAction(false);
    }
  }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader)
  {
    String str = ParserUtil.getAttribute(paramXMLStreamReader, getWsdlActionTag());
    if (str != null)
    {
      paramEditableWSDLFault.setAction(str);
      paramEditableWSDLFault.setDefaultAction(false);
    }
  }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    EditableWSDLModel localEditableWSDLModel = paramWSDLParserExtensionContext.getWSDLModel();
    Iterator localIterator1 = localEditableWSDLModel.getServices().values().iterator();
    while (localIterator1.hasNext())
    {
      EditableWSDLService localEditableWSDLService = (EditableWSDLService)localIterator1.next();
      Iterator localIterator2 = localEditableWSDLService.getPorts().iterator();
      while (localIterator2.hasNext())
      {
        EditableWSDLPort localEditableWSDLPort = (EditableWSDLPort)localIterator2.next();
        EditableWSDLBoundPortType localEditableWSDLBoundPortType = localEditableWSDLPort.getBinding();
        populateActions(localEditableWSDLBoundPortType);
        patchAnonymousDefault(localEditableWSDLBoundPortType);
      }
    }
  }
  
  protected String getNamespaceURI()
  {
    return W3CwsdlNsUri;
  }
  
  protected QName getWsdlActionTag()
  {
    return W3CwsdlActionTag;
  }
  
  private void populateActions(EditableWSDLBoundPortType paramEditableWSDLBoundPortType)
  {
    EditableWSDLPortType localEditableWSDLPortType = paramEditableWSDLBoundPortType.getPortType();
    Iterator localIterator1 = localEditableWSDLPortType.getOperations().iterator();
    while (localIterator1.hasNext())
    {
      EditableWSDLOperation localEditableWSDLOperation = (EditableWSDLOperation)localIterator1.next();
      EditableWSDLBoundOperation localEditableWSDLBoundOperation = paramEditableWSDLBoundPortType.get(localEditableWSDLOperation.getName());
      if (localEditableWSDLBoundOperation == null)
      {
        localEditableWSDLOperation.getInput().setAction(defaultInputAction(localEditableWSDLOperation));
      }
      else
      {
        String str = localEditableWSDLBoundOperation.getSOAPAction();
        if ((localEditableWSDLOperation.getInput().getAction() == null) || (localEditableWSDLOperation.getInput().getAction().equals(""))) {
          if ((str != null) && (!str.equals(""))) {
            localEditableWSDLOperation.getInput().setAction(str);
          } else {
            localEditableWSDLOperation.getInput().setAction(defaultInputAction(localEditableWSDLOperation));
          }
        }
        if (localEditableWSDLOperation.getOutput() != null)
        {
          if ((localEditableWSDLOperation.getOutput().getAction() == null) || (localEditableWSDLOperation.getOutput().getAction().equals(""))) {
            localEditableWSDLOperation.getOutput().setAction(defaultOutputAction(localEditableWSDLOperation));
          }
          if ((localEditableWSDLOperation.getFaults() != null) && (localEditableWSDLOperation.getFaults().iterator().hasNext()))
          {
            Iterator localIterator2 = localEditableWSDLOperation.getFaults().iterator();
            while (localIterator2.hasNext())
            {
              EditableWSDLFault localEditableWSDLFault = (EditableWSDLFault)localIterator2.next();
              if ((localEditableWSDLFault.getAction() == null) || (localEditableWSDLFault.getAction().equals(""))) {
                localEditableWSDLFault.setAction(defaultFaultAction(localEditableWSDLFault.getName(), localEditableWSDLOperation));
              }
            }
          }
        }
      }
    }
  }
  
  protected void patchAnonymousDefault(EditableWSDLBoundPortType paramEditableWSDLBoundPortType)
  {
    Iterator localIterator = paramEditableWSDLBoundPortType.getBindingOperations().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLBoundOperation localEditableWSDLBoundOperation = (EditableWSDLBoundOperation)localIterator.next();
      if (localEditableWSDLBoundOperation.getAnonymous() == null) {
        localEditableWSDLBoundOperation.setAnonymous(WSDLBoundOperation.ANONYMOUS.optional);
      }
    }
  }
  
  private String defaultInputAction(EditableWSDLOperation paramEditableWSDLOperation)
  {
    return buildAction(paramEditableWSDLOperation.getInput().getName(), paramEditableWSDLOperation, false);
  }
  
  private String defaultOutputAction(EditableWSDLOperation paramEditableWSDLOperation)
  {
    return buildAction(paramEditableWSDLOperation.getOutput().getName(), paramEditableWSDLOperation, false);
  }
  
  private String defaultFaultAction(String paramString, EditableWSDLOperation paramEditableWSDLOperation)
  {
    return buildAction(paramString, paramEditableWSDLOperation, true);
  }
  
  protected static final String buildAction(String paramString, EditableWSDLOperation paramEditableWSDLOperation, boolean paramBoolean)
  {
    String str1 = paramEditableWSDLOperation.getName().getNamespaceURI();
    String str2 = "/";
    if (!str1.startsWith("http")) {
      str2 = ":";
    }
    if (str1.endsWith(str2)) {
      str1 = str1.substring(0, str1.length() - 1);
    }
    if (paramEditableWSDLOperation.getPortTypeName() == null) {
      throw new WebServiceException("\"" + paramEditableWSDLOperation.getName() + "\" operation's owning portType name is null.");
    }
    return str1 + str2 + paramEditableWSDLOperation.getPortTypeName().getLocalPart() + str2 + (paramBoolean ? paramEditableWSDLOperation.getName().getLocalPart() + str2 + "Fault" + str2 : "") + paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\W3CAddressingWSDLParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */