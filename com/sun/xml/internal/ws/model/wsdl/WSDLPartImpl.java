package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPartImpl
  extends AbstractObjectImpl
  implements EditableWSDLPart
{
  private final String name;
  private ParameterBinding binding;
  private int index;
  private final WSDLPartDescriptor descriptor;
  
  public WSDLPartImpl(XMLStreamReader paramXMLStreamReader, String paramString, int paramInt, WSDLPartDescriptor paramWSDLPartDescriptor)
  {
    super(paramXMLStreamReader);
    name = paramString;
    binding = ParameterBinding.UNBOUND;
    index = paramInt;
    descriptor = paramWSDLPartDescriptor;
  }
  
  public String getName()
  {
    return name;
  }
  
  public ParameterBinding getBinding()
  {
    return binding;
  }
  
  public void setBinding(ParameterBinding paramParameterBinding)
  {
    binding = paramParameterBinding;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public WSDLPartDescriptor getDescriptor()
  {
    return descriptor;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPartImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */