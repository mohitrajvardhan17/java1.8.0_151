package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.DOMHeader;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

abstract class MUTube
  extends AbstractFilterTubeImpl
{
  private static final String MU_FAULT_DETAIL_LOCALPART = "NotUnderstood";
  private static final QName MU_HEADER_DETAIL = new QName(SOAP_12nsUri, "NotUnderstood");
  protected static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.soap.decoder");
  private static final String MUST_UNDERSTAND_FAULT_MESSAGE_STRING = "One or more mandatory SOAP header blocks not understood";
  protected final SOAPVersion soapVersion;
  protected SOAPBindingImpl binding;
  
  protected MUTube(WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramTube);
    if (!(paramWSBinding instanceof SOAPBinding)) {
      throw new WebServiceException("MUPipe should n't be used for bindings other than SOAP.");
    }
    binding = ((SOAPBindingImpl)paramWSBinding);
    soapVersion = paramWSBinding.getSOAPVersion();
  }
  
  protected MUTube(MUTube paramMUTube, TubeCloner paramTubeCloner)
  {
    super(paramMUTube, paramTubeCloner);
    binding = binding;
    soapVersion = soapVersion;
  }
  
  public final Set<QName> getMisUnderstoodHeaders(MessageHeaders paramMessageHeaders, Set<String> paramSet, Set<QName> paramSet1)
  {
    return paramMessageHeaders.getNotUnderstoodHeaders(paramSet, paramSet1, binding);
  }
  
  final SOAPFaultException createMUSOAPFaultException(Set<QName> paramSet)
  {
    try
    {
      SOAPFault localSOAPFault = soapVersion.getSOAPFactory().createFault("One or more mandatory SOAP header blocks not understood", soapVersion.faultCodeMustUnderstand);
      localSOAPFault.setFaultString("MustUnderstand headers:" + paramSet + " are not understood");
      return new SOAPFaultException(localSOAPFault);
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  final Message createMUSOAPFaultMessage(Set<QName> paramSet)
  {
    try
    {
      String str = "One or more mandatory SOAP header blocks not understood";
      if (soapVersion == SOAPVersion.SOAP_11) {
        str = "MustUnderstand headers:" + paramSet + " are not understood";
      }
      Message localMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, str, soapVersion.faultCodeMustUnderstand);
      if (soapVersion == SOAPVersion.SOAP_12) {
        addHeader(localMessage, paramSet);
      }
      return localMessage;
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  private static void addHeader(Message paramMessage, Set<QName> paramSet)
    throws SOAPException
  {
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      QName localQName = (QName)localIterator.next();
      SOAPElement localSOAPElement = SOAPVersion.SOAP_12.getSOAPFactory().createElement(MU_HEADER_DETAIL);
      localSOAPElement.addNamespaceDeclaration("abc", localQName.getNamespaceURI());
      localSOAPElement.setAttribute("qname", "abc:" + localQName.getLocalPart());
      DOMHeader localDOMHeader = new DOMHeader(localSOAPElement);
      paramMessage.getHeaders().add(localDOMHeader);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\protocol\soap\MUTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */