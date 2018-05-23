package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;

public abstract class OldObjectKeyTemplateBase
  extends ObjectKeyTemplateBase
{
  public OldObjectKeyTemplateBase(ORB paramORB, int paramInt1, int paramInt2, int paramInt3, String paramString, ObjectAdapterId paramObjectAdapterId)
  {
    super(paramORB, paramInt1, paramInt2, paramInt3, paramString, paramObjectAdapterId);
    if (paramInt1 == -1347695874) {
      setORBVersion(ORBVersionFactory.getOLD());
    } else if (paramInt1 == -1347695873) {
      setORBVersion(ORBVersionFactory.getNEW());
    } else {
      throw wrapper.badMagic(new Integer(paramInt1));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\OldObjectKeyTemplateBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */