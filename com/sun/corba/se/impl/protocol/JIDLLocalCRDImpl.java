package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.portable.ServantObject;

public class JIDLLocalCRDImpl
  extends LocalClientRequestDispatcherBase
{
  protected ServantObject servant;
  
  public JIDLLocalCRDImpl(ORB paramORB, int paramInt, IOR paramIOR)
  {
    super(paramORB, paramInt, paramIOR);
  }
  
  public ServantObject servant_preinvoke(org.omg.CORBA.Object paramObject, String paramString, Class paramClass)
  {
    if (!checkForCompatibleServant(servant, paramClass)) {
      return null;
    }
    return servant;
  }
  
  public void servant_postinvoke(org.omg.CORBA.Object paramObject, ServantObject paramServantObject) {}
  
  public void setServant(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Tie)))
    {
      servant = new ServantObject();
      servant.servant = ((Tie)paramObject).getTarget();
    }
    else
    {
      servant = null;
    }
  }
  
  public void unexport()
  {
    servant = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\JIDLLocalCRDImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */