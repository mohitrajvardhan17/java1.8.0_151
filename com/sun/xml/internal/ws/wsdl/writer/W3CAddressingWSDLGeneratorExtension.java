package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLGeneratorExtension
  extends WSDLGeneratorExtension
{
  private boolean enabled;
  private boolean required = false;
  private static final Logger LOGGER = Logger.getLogger(W3CAddressingWSDLGeneratorExtension.class.getName());
  
  public W3CAddressingWSDLGeneratorExtension() {}
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext)
  {
    WSBinding localWSBinding = paramWSDLGenExtnContext.getBinding();
    TypedXmlWriter localTypedXmlWriter = paramWSDLGenExtnContext.getRoot();
    enabled = localWSBinding.isFeatureEnabled(AddressingFeature.class);
    if (!enabled) {
      return;
    }
    AddressingFeature localAddressingFeature = (AddressingFeature)localWSBinding.getFeature(AddressingFeature.class);
    required = localAddressingFeature.isRequired();
    localTypedXmlWriter._namespace(W3CwsdlNsUri, AddressingVersion.W3C.getWsdlPrefix());
  }
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    if (!enabled) {
      return;
    }
    Action localAction = (Action)paramJavaMethod.getSEIMethod().getAnnotation(Action.class);
    if ((localAction != null) && (!localAction.input().equals("")))
    {
      addAttribute(paramTypedXmlWriter, localAction.input());
    }
    else
    {
      String str1 = paramJavaMethod.getBinding().getSOAPAction();
      if ((str1 == null) || (str1.equals("")))
      {
        String str2 = getDefaultAction(paramJavaMethod);
        addAttribute(paramTypedXmlWriter, str2);
      }
    }
  }
  
  protected static final String getDefaultAction(JavaMethod paramJavaMethod)
  {
    String str1 = paramJavaMethod.getOwner().getTargetNamespace();
    String str2 = "/";
    try
    {
      URI localURI = new URI(str1);
      if (localURI.getScheme().equalsIgnoreCase("urn")) {
        str2 = ":";
      }
    }
    catch (URISyntaxException localURISyntaxException)
    {
      LOGGER.warning("TargetNamespace of WebService is not a valid URI");
    }
    if (str1.endsWith(str2)) {
      str1 = str1.substring(0, str1.length() - 1);
    }
    String str3 = paramJavaMethod.getOperationName() + "Request";
    return str1 + str2 + paramJavaMethod.getOwner().getPortTypeName().getLocalPart() + str2 + str3;
  }
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    if (!enabled) {
      return;
    }
    Action localAction = (Action)paramJavaMethod.getSEIMethod().getAnnotation(Action.class);
    if ((localAction != null) && (!localAction.output().equals(""))) {
      addAttribute(paramTypedXmlWriter, localAction.output());
    }
  }
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException)
  {
    if (!enabled) {
      return;
    }
    Action localAction = (Action)paramJavaMethod.getSEIMethod().getAnnotation(Action.class);
    Class[] arrayOfClass = paramJavaMethod.getSEIMethod().getExceptionTypes();
    if (arrayOfClass == null) {
      return;
    }
    if ((localAction != null) && (localAction.fault() != null)) {
      for (FaultAction localFaultAction : localAction.fault()) {
        if (localFaultAction.className().getName().equals(paramCheckedException.getExceptionClass().getName()))
        {
          if (localFaultAction.value().equals("")) {
            return;
          }
          addAttribute(paramTypedXmlWriter, localFaultAction.value());
          return;
        }
      }
    }
  }
  
  private void addAttribute(TypedXmlWriter paramTypedXmlWriter, String paramString)
  {
    paramTypedXmlWriter._attribute(W3CwsdlActionTag, paramString);
  }
  
  public void addBindingExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    if (!enabled) {
      return;
    }
    paramTypedXmlWriter._element(W3CwsdlExtensionTag, UsingAddressing.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\W3CAddressingWSDLGeneratorExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */