package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class ObjectKeyFactoryImpl
  implements ObjectKeyFactory
{
  public static final int MAGIC_BASE = -1347695874;
  public static final int JAVAMAGIC_OLD = -1347695874;
  public static final int JAVAMAGIC_NEW = -1347695873;
  public static final int JAVAMAGIC_NEWER = -1347695872;
  public static final int MAX_MAGIC = -1347695872;
  public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;
  private final ORB orb;
  private IORSystemException wrapper;
  private Handler fullKey = new Handler()
  {
    public ObjectKeyTemplate handle(int paramAnonymousInt1, int paramAnonymousInt2, InputStream paramAnonymousInputStream, OctetSeqHolder paramAnonymousOctetSeqHolder)
    {
      Object localObject = null;
      if ((paramAnonymousInt2 >= 32) && (paramAnonymousInt2 <= 63))
      {
        if (paramAnonymousInt1 >= -1347695872) {
          localObject = new POAObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
        } else {
          localObject = new OldPOAObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
        }
      }
      else if ((paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < 32)) {
        if (paramAnonymousInt1 >= -1347695872) {
          localObject = new JIDLObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
        } else {
          localObject = new OldJIDLObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream, paramAnonymousOctetSeqHolder);
        }
      }
      return (ObjectKeyTemplate)localObject;
    }
  };
  private Handler oktempOnly = new Handler()
  {
    public ObjectKeyTemplate handle(int paramAnonymousInt1, int paramAnonymousInt2, InputStream paramAnonymousInputStream, OctetSeqHolder paramAnonymousOctetSeqHolder)
    {
      Object localObject = null;
      if ((paramAnonymousInt2 >= 32) && (paramAnonymousInt2 <= 63))
      {
        if (paramAnonymousInt1 >= -1347695872) {
          localObject = new POAObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
        } else {
          localObject = new OldPOAObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
        }
      }
      else if ((paramAnonymousInt2 >= 0) && (paramAnonymousInt2 < 32)) {
        if (paramAnonymousInt1 >= -1347695872) {
          localObject = new JIDLObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
        } else {
          localObject = new OldJIDLObjectKeyTemplate(orb, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInputStream);
        }
      }
      return (ObjectKeyTemplate)localObject;
    }
  };
  
  public ObjectKeyFactoryImpl(ORB paramORB)
  {
    orb = paramORB;
    wrapper = IORSystemException.get(paramORB, "oa.ior");
  }
  
  private boolean validMagic(int paramInt)
  {
    return (paramInt >= -1347695874) && (paramInt <= -1347695872);
  }
  
  private ObjectKeyTemplate create(InputStream paramInputStream, Handler paramHandler, OctetSeqHolder paramOctetSeqHolder)
  {
    ObjectKeyTemplate localObjectKeyTemplate = null;
    try
    {
      paramInputStream.mark(0);
      int i = paramInputStream.read_long();
      if (validMagic(i))
      {
        int j = paramInputStream.read_long();
        localObjectKeyTemplate = paramHandler.handle(i, j, paramInputStream, paramOctetSeqHolder);
      }
    }
    catch (MARSHAL localMARSHAL) {}
    if (localObjectKeyTemplate == null) {
      try
      {
        paramInputStream.reset();
      }
      catch (IOException localIOException) {}
    }
    return localObjectKeyTemplate;
  }
  
  public ObjectKey create(byte[] paramArrayOfByte)
  {
    OctetSeqHolder localOctetSeqHolder = new OctetSeqHolder();
    EncapsInputStream localEncapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(orb, paramArrayOfByte, paramArrayOfByte.length);
    Object localObject = create(localEncapsInputStream, fullKey, localOctetSeqHolder);
    if (localObject == null) {
      localObject = new WireObjectKeyTemplate(localEncapsInputStream, localOctetSeqHolder);
    }
    ObjectIdImpl localObjectIdImpl = new ObjectIdImpl(value);
    return new ObjectKeyImpl((ObjectKeyTemplate)localObject, localObjectIdImpl);
  }
  
  public ObjectKeyTemplate createTemplate(InputStream paramInputStream)
  {
    Object localObject = create(paramInputStream, oktempOnly, null);
    if (localObject == null) {
      localObject = new WireObjectKeyTemplate(orb);
    }
    return (ObjectKeyTemplate)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectKeyFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */