package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.List;
import javax.xml.bind.JAXBException;

public abstract interface JAXBContextFactory
{
  public static final JAXBContextFactory DEFAULT = new JAXBContextFactory()
  {
    @NotNull
    public JAXBRIContext createJAXBContext(@NotNull SEIModel paramAnonymousSEIModel, @NotNull List<Class> paramAnonymousList, @NotNull List<TypeReference> paramAnonymousList1)
      throws JAXBException
    {
      return JAXBRIContext.newInstance((Class[])paramAnonymousList.toArray(new Class[paramAnonymousList.size()]), paramAnonymousList1, null, paramAnonymousSEIModel.getTargetNamespace(), false, null);
    }
  };
  
  @NotNull
  public abstract JAXBRIContext createJAXBContext(@NotNull SEIModel paramSEIModel, @NotNull List<Class> paramList, @NotNull List<TypeReference> paramList1)
    throws JAXBException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\JAXBContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */