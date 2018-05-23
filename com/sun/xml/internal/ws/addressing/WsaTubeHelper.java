package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class WsaTubeHelper
{
  protected SEIModel seiModel;
  protected WSDLPort wsdlPort;
  protected WSBinding binding;
  protected final SOAPVersion soapVer;
  protected final AddressingVersion addVer;
  
  public WsaTubeHelper(WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort)
  {
    binding = paramWSBinding;
    wsdlPort = paramWSDLPort;
    seiModel = paramSEIModel;
    soapVer = paramWSBinding.getSOAPVersion();
    addVer = paramWSBinding.getAddressingVersion();
  }
  
  public String getFaultAction(Packet paramPacket1, Packet paramPacket2)
  {
    String str = null;
    if (seiModel != null) {
      str = getFaultActionFromSEIModel(paramPacket1, paramPacket2);
    }
    if (str != null) {
      return str;
    }
    str = addVer.getDefaultFaultAction();
    if (wsdlPort != null)
    {
      WSDLOperationMapping localWSDLOperationMapping = paramPacket1.getWSDLOperationMapping();
      if (localWSDLOperationMapping != null)
      {
        WSDLBoundOperation localWSDLBoundOperation = localWSDLOperationMapping.getWSDLBoundOperation();
        return getFaultAction(localWSDLBoundOperation, paramPacket2);
      }
    }
    return str;
  }
  
  String getFaultActionFromSEIModel(Packet paramPacket1, Packet paramPacket2)
  {
    String str1 = null;
    if ((seiModel == null) || (wsdlPort == null)) {
      return str1;
    }
    try
    {
      SOAPMessage localSOAPMessage = paramPacket2.getMessage().copy().readAsSOAPMessage();
      if (localSOAPMessage == null) {
        return str1;
      }
      if (localSOAPMessage.getSOAPBody() == null) {
        return str1;
      }
      if (localSOAPMessage.getSOAPBody().getFault() == null) {
        return str1;
      }
      Detail localDetail = localSOAPMessage.getSOAPBody().getFault().getDetail();
      if (localDetail == null) {
        return str1;
      }
      String str2 = localDetail.getFirstChild().getNamespaceURI();
      String str3 = localDetail.getFirstChild().getLocalName();
      WSDLOperationMapping localWSDLOperationMapping = paramPacket1.getWSDLOperationMapping();
      Object localObject = localWSDLOperationMapping != null ? (JavaMethodImpl)localWSDLOperationMapping.getJavaMethod() : null;
      if (localObject != null)
      {
        Iterator localIterator = ((JavaMethodImpl)localObject).getCheckedExceptions().iterator();
        while (localIterator.hasNext())
        {
          CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator.next();
          if ((getDetailTypetagName.getLocalPart().equals(str3)) && (getDetailTypetagName.getNamespaceURI().equals(str2))) {
            return localCheckedExceptionImpl.getFaultAction();
          }
        }
      }
      return str1;
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  String getFaultAction(@Nullable WSDLBoundOperation paramWSDLBoundOperation, Packet paramPacket)
  {
    String str1 = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), addVer, soapVer);
    if (str1 != null) {
      return str1;
    }
    str1 = addVer.getDefaultFaultAction();
    if (paramWSDLBoundOperation == null) {
      return str1;
    }
    try
    {
      SOAPMessage localSOAPMessage = paramPacket.getMessage().copy().readAsSOAPMessage();
      if (localSOAPMessage == null) {
        return str1;
      }
      if (localSOAPMessage.getSOAPBody() == null) {
        return str1;
      }
      if (localSOAPMessage.getSOAPBody().getFault() == null) {
        return str1;
      }
      Detail localDetail = localSOAPMessage.getSOAPBody().getFault().getDetail();
      if (localDetail == null) {
        return str1;
      }
      String str2 = localDetail.getFirstChild().getNamespaceURI();
      String str3 = localDetail.getFirstChild().getLocalName();
      WSDLOperation localWSDLOperation = paramWSDLBoundOperation.getOperation();
      WSDLFault localWSDLFault = localWSDLOperation.getFault(new QName(str2, str3));
      if (localWSDLFault == null) {
        return str1;
      }
      str1 = localWSDLFault.getAction();
      return str1;
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  public String getInputAction(Packet paramPacket)
  {
    String str = null;
    if (wsdlPort != null)
    {
      WSDLOperationMapping localWSDLOperationMapping = paramPacket.getWSDLOperationMapping();
      if (localWSDLOperationMapping != null)
      {
        WSDLBoundOperation localWSDLBoundOperation = localWSDLOperationMapping.getWSDLBoundOperation();
        WSDLOperation localWSDLOperation = localWSDLBoundOperation.getOperation();
        str = localWSDLOperation.getInput().getAction();
      }
    }
    return str;
  }
  
  public String getEffectiveInputAction(Packet paramPacket)
  {
    if ((soapAction != null) && (!soapAction.equals(""))) {
      return soapAction;
    }
    String str;
    if (wsdlPort != null)
    {
      WSDLOperationMapping localWSDLOperationMapping = paramPacket.getWSDLOperationMapping();
      if (localWSDLOperationMapping != null)
      {
        WSDLBoundOperation localWSDLBoundOperation = localWSDLOperationMapping.getWSDLBoundOperation();
        WSDLOperation localWSDLOperation = localWSDLBoundOperation.getOperation();
        str = localWSDLOperation.getInput().getAction();
      }
      else
      {
        str = soapAction;
      }
    }
    else
    {
      str = soapAction;
    }
    return str;
  }
  
  public boolean isInputActionDefault(Packet paramPacket)
  {
    if (wsdlPort == null) {
      return false;
    }
    WSDLOperationMapping localWSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (localWSDLOperationMapping == null) {
      return false;
    }
    WSDLBoundOperation localWSDLBoundOperation = localWSDLOperationMapping.getWSDLBoundOperation();
    WSDLOperation localWSDLOperation = localWSDLBoundOperation.getOperation();
    return localWSDLOperation.getInput().isDefaultAction();
  }
  
  public String getSOAPAction(Packet paramPacket)
  {
    String str = "";
    if ((paramPacket == null) || (paramPacket.getMessage() == null)) {
      return str;
    }
    if (wsdlPort == null) {
      return str;
    }
    WSDLOperationMapping localWSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (localWSDLOperationMapping == null) {
      return str;
    }
    WSDLBoundOperation localWSDLBoundOperation = localWSDLOperationMapping.getWSDLBoundOperation();
    str = localWSDLBoundOperation.getSOAPAction();
    return str;
  }
  
  public String getOutputAction(Packet paramPacket)
  {
    String str = null;
    WSDLOperationMapping localWSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (localWSDLOperationMapping != null)
    {
      JavaMethod localJavaMethod = localWSDLOperationMapping.getJavaMethod();
      if (localJavaMethod != null)
      {
        localObject = (JavaMethodImpl)localJavaMethod;
        if ((localObject != null) && (((JavaMethodImpl)localObject).getOutputAction() != null) && (!((JavaMethodImpl)localObject).getOutputAction().equals(""))) {
          return ((JavaMethodImpl)localObject).getOutputAction();
        }
      }
      Object localObject = localWSDLOperationMapping.getWSDLBoundOperation();
      if (localObject != null) {
        return getOutputAction((WSDLBoundOperation)localObject);
      }
    }
    return str;
  }
  
  String getOutputAction(@Nullable WSDLBoundOperation paramWSDLBoundOperation)
  {
    String str = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
    if (paramWSDLBoundOperation != null)
    {
      WSDLOutput localWSDLOutput = paramWSDLBoundOperation.getOperation().getOutput();
      if (localWSDLOutput != null) {
        str = localWSDLOutput.getAction();
      }
    }
    return str;
  }
  
  public SOAPFault createInvalidAddressingHeaderFault(InvalidAddressingHeaderException paramInvalidAddressingHeaderException, AddressingVersion paramAddressingVersion)
  {
    QName localQName1 = paramInvalidAddressingHeaderException.getProblemHeader();
    QName localQName2 = paramInvalidAddressingHeaderException.getSubsubcode();
    QName localQName3 = invalidMapTag;
    String str = String.format(paramAddressingVersion.getInvalidMapText(), new Object[] { localQName1, localQName2 });
    try
    {
      SOAPFactory localSOAPFactory;
      SOAPFault localSOAPFault;
      if (soapVer == SOAPVersion.SOAP_12)
      {
        localSOAPFactory = SOAPVersion.SOAP_12.getSOAPFactory();
        localSOAPFault = localSOAPFactory.createFault();
        localSOAPFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
        localSOAPFault.appendFaultSubcode(localQName3);
        localSOAPFault.appendFaultSubcode(localQName2);
        getInvalidMapDetail(localQName1, localSOAPFault.addDetail());
      }
      else
      {
        localSOAPFactory = SOAPVersion.SOAP_11.getSOAPFactory();
        localSOAPFault = localSOAPFactory.createFault();
        localSOAPFault.setFaultCode(localQName2);
      }
      localSOAPFault.setFaultString(str);
      return localSOAPFault;
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  public SOAPFault newMapRequiredFault(MissingAddressingHeaderException paramMissingAddressingHeaderException)
  {
    QName localQName1 = addVer.mapRequiredTag;
    QName localQName2 = addVer.mapRequiredTag;
    String str = addVer.getMapRequiredText();
    try
    {
      SOAPFactory localSOAPFactory;
      SOAPFault localSOAPFault;
      if (soapVer == SOAPVersion.SOAP_12)
      {
        localSOAPFactory = SOAPVersion.SOAP_12.getSOAPFactory();
        localSOAPFault = localSOAPFactory.createFault();
        localSOAPFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
        localSOAPFault.appendFaultSubcode(localQName1);
        localSOAPFault.appendFaultSubcode(localQName2);
        getMapRequiredDetail(paramMissingAddressingHeaderException.getMissingHeaderQName(), localSOAPFault.addDetail());
      }
      else
      {
        localSOAPFactory = SOAPVersion.SOAP_11.getSOAPFactory();
        localSOAPFault = localSOAPFactory.createFault();
        localSOAPFault.setFaultCode(localQName2);
      }
      localSOAPFault.setFaultString(str);
      return localSOAPFault;
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  public abstract void getProblemActionDetail(String paramString, Element paramElement);
  
  public abstract void getInvalidMapDetail(QName paramQName, Element paramElement);
  
  public abstract void getMapRequiredDetail(QName paramQName, Element paramElement);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\WsaTubeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */