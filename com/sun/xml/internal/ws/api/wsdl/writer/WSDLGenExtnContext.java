package com.sun.xml.internal.ws.api.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.server.Container;

public class WSDLGenExtnContext
{
  private final TypedXmlWriter root;
  private final SEIModel model;
  private final WSBinding binding;
  private final Container container;
  private final Class endpointClass;
  
  public WSDLGenExtnContext(@NotNull TypedXmlWriter paramTypedXmlWriter, @NotNull SEIModel paramSEIModel, @NotNull WSBinding paramWSBinding, @Nullable Container paramContainer, @NotNull Class paramClass)
  {
    root = paramTypedXmlWriter;
    model = paramSEIModel;
    binding = paramWSBinding;
    container = paramContainer;
    endpointClass = paramClass;
  }
  
  public TypedXmlWriter getRoot()
  {
    return root;
  }
  
  public SEIModel getModel()
  {
    return model;
  }
  
  public WSBinding getBinding()
  {
    return binding;
  }
  
  public Container getContainer()
  {
    return container;
  }
  
  public Class getEndpointClass()
  {
    return endpointClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\wsdl\writer\WSDLGenExtnContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */