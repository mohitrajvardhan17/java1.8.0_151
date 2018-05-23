package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.protocol.NotLocalLocalCRDImpl;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CorbaContactInfoListImpl
  implements CorbaContactInfoList
{
  protected ORB orb;
  protected LocalClientRequestDispatcher LocalClientRequestDispatcher;
  protected IOR targetIOR;
  protected IOR effectiveTargetIOR;
  protected List effectiveTargetIORContactInfoList;
  protected ContactInfo primaryContactInfo;
  
  public CorbaContactInfoListImpl(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public CorbaContactInfoListImpl(ORB paramORB, IOR paramIOR)
  {
    this(paramORB);
    setTargetIOR(paramIOR);
  }
  
  public synchronized Iterator iterator()
  {
    createContactInfoList();
    return new CorbaContactInfoListIteratorImpl(orb, this, primaryContactInfo, effectiveTargetIORContactInfoList);
  }
  
  public synchronized void setTargetIOR(IOR paramIOR)
  {
    targetIOR = paramIOR;
    setEffectiveTargetIOR(paramIOR);
  }
  
  public synchronized IOR getTargetIOR()
  {
    return targetIOR;
  }
  
  public synchronized void setEffectiveTargetIOR(IOR paramIOR)
  {
    effectiveTargetIOR = paramIOR;
    effectiveTargetIORContactInfoList = null;
    if ((primaryContactInfo != null) && (orb.getORBData().getIIOPPrimaryToContactInfo() != null)) {
      orb.getORBData().getIIOPPrimaryToContactInfo().reset(primaryContactInfo);
    }
    primaryContactInfo = null;
    setLocalSubcontract();
  }
  
  public synchronized IOR getEffectiveTargetIOR()
  {
    return effectiveTargetIOR;
  }
  
  public synchronized LocalClientRequestDispatcher getLocalClientRequestDispatcher()
  {
    return LocalClientRequestDispatcher;
  }
  
  public synchronized int hashCode()
  {
    return targetIOR.hashCode();
  }
  
  protected void createContactInfoList()
  {
    if (effectiveTargetIORContactInfoList != null) {
      return;
    }
    effectiveTargetIORContactInfoList = new ArrayList();
    IIOPProfile localIIOPProfile = effectiveTargetIOR.getProfile();
    String str = ((IIOPProfileTemplate)localIIOPProfile.getTaggedProfileTemplate()).getPrimaryAddress().getHost().toLowerCase();
    int i = ((IIOPProfileTemplate)localIIOPProfile.getTaggedProfileTemplate()).getPrimaryAddress().getPort();
    primaryContactInfo = createContactInfo("IIOP_CLEAR_TEXT", str, i);
    if (localIIOPProfile.isLocal())
    {
      SharedCDRContactInfoImpl localSharedCDRContactInfoImpl = new SharedCDRContactInfoImpl(orb, this, effectiveTargetIOR, orb.getORBData().getGIOPAddressDisposition());
      effectiveTargetIORContactInfoList.add(localSharedCDRContactInfoImpl);
    }
    else
    {
      addRemoteContactInfos(effectiveTargetIOR, effectiveTargetIORContactInfoList);
    }
  }
  
  protected void addRemoteContactInfos(IOR paramIOR, List paramList)
  {
    List localList = orb.getORBData().getIORToSocketInfo().getSocketInfo(paramIOR);
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      SocketInfo localSocketInfo = (SocketInfo)localIterator.next();
      String str1 = localSocketInfo.getType();
      String str2 = localSocketInfo.getHost().toLowerCase();
      int i = localSocketInfo.getPort();
      ContactInfo localContactInfo = createContactInfo(str1, str2, i);
      paramList.add(localContactInfo);
    }
  }
  
  protected ContactInfo createContactInfo(String paramString1, String paramString2, int paramInt)
  {
    return new SocketOrChannelContactInfoImpl(orb, this, effectiveTargetIOR, orb.getORBData().getGIOPAddressDisposition(), paramString1, paramString2, paramInt);
  }
  
  protected void setLocalSubcontract()
  {
    if (!effectiveTargetIOR.getProfile().isLocal())
    {
      LocalClientRequestDispatcher = new NotLocalLocalCRDImpl();
      return;
    }
    int i = effectiveTargetIOR.getProfile().getObjectKeyTemplate().getSubcontractId();
    LocalClientRequestDispatcherFactory localLocalClientRequestDispatcherFactory = orb.getRequestDispatcherRegistry().getLocalClientRequestDispatcherFactory(i);
    LocalClientRequestDispatcher = localLocalClientRequestDispatcherFactory.create(i, effectiveTargetIOR);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\CorbaContactInfoListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */