package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

final class WSDLGeneratorExtensionFacade
  extends WSDLGeneratorExtension
{
  private final WSDLGeneratorExtension[] extensions;
  
  WSDLGeneratorExtensionFacade(WSDLGeneratorExtension... paramVarArgs)
  {
    assert (paramVarArgs != null);
    extensions = paramVarArgs;
  }
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.start(paramWSDLGenExtnContext);
    }
  }
  
  public void end(@NotNull WSDLGenExtnContext paramWSDLGenExtnContext)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.end(paramWSDLGenExtnContext);
    }
  }
  
  public void addDefinitionsExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addDefinitionsExtension(paramTypedXmlWriter);
    }
  }
  
  public void addServiceExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addServiceExtension(paramTypedXmlWriter);
    }
  }
  
  public void addPortExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addPortExtension(paramTypedXmlWriter);
    }
  }
  
  public void addPortTypeExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addPortTypeExtension(paramTypedXmlWriter);
    }
  }
  
  public void addBindingExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addBindingExtension(paramTypedXmlWriter);
    }
  }
  
  public void addOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addOperationExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addBindingOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addBindingOperationExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addInputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addInputMessageExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addOutputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addOutputMessageExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addOperationInputExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addOperationOutputExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addBindingOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addBindingOperationInputExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addBindingOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addBindingOperationOutputExtension(paramTypedXmlWriter, paramJavaMethod);
    }
  }
  
  public void addBindingOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addBindingOperationFaultExtension(paramTypedXmlWriter, paramJavaMethod, paramCheckedException);
    }
  }
  
  public void addFaultMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addFaultMessageExtension(paramTypedXmlWriter, paramJavaMethod, paramCheckedException);
    }
  }
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException)
  {
    for (WSDLGeneratorExtension localWSDLGeneratorExtension : extensions) {
      localWSDLGeneratorExtension.addOperationFaultExtension(paramTypedXmlWriter, paramJavaMethod, paramCheckedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\WSDLGeneratorExtensionFacade.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */