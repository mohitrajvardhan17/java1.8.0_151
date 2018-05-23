package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultIORToSocketInfoImpl
  implements IORToSocketInfo
{
  public DefaultIORToSocketInfoImpl() {}
  
  public List getSocketInfo(IOR paramIOR)
  {
    ArrayList localArrayList = new ArrayList();
    IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)paramIOR.getProfile().getTaggedProfileTemplate();
    IIOPAddress localIIOPAddress = localIIOPProfileTemplate.getPrimaryAddress();
    String str = localIIOPAddress.getHost().toLowerCase();
    int i = localIIOPAddress.getPort();
    SocketInfo localSocketInfo = createSocketInfo(str, i);
    localArrayList.add(localSocketInfo);
    Iterator localIterator = localIIOPProfileTemplate.iteratorById(3);
    while (localIterator.hasNext())
    {
      AlternateIIOPAddressComponent localAlternateIIOPAddressComponent = (AlternateIIOPAddressComponent)localIterator.next();
      str = localAlternateIIOPAddressComponent.getAddress().getHost().toLowerCase();
      i = localAlternateIIOPAddressComponent.getAddress().getPort();
      localSocketInfo = createSocketInfo(str, i);
      localArrayList.add(localSocketInfo);
    }
    return localArrayList;
  }
  
  private SocketInfo createSocketInfo(final String paramString, final int paramInt)
  {
    new SocketInfo()
    {
      public String getType()
      {
        return "IIOP_CLEAR_TEXT";
      }
      
      public String getHost()
      {
        return paramString;
      }
      
      public int getPort()
      {
        return paramInt;
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\DefaultIORToSocketInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */