package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceFactoryHelper;

public class ObjectReferenceFactoryImpl
  extends ObjectReferenceProducerBase
  implements ObjectReferenceFactory, StreamableValue
{
  private transient IORTemplateList iorTemplates;
  public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0";
  
  public ObjectReferenceFactoryImpl(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    super((ORB)paramInputStream.orb());
    _read(paramInputStream);
  }
  
  public ObjectReferenceFactoryImpl(ORB paramORB, IORTemplateList paramIORTemplateList)
  {
    super(paramORB);
    iorTemplates = paramIORTemplateList;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ObjectReferenceFactoryImpl)) {
      return false;
    }
    ObjectReferenceFactoryImpl localObjectReferenceFactoryImpl = (ObjectReferenceFactoryImpl)paramObject;
    return (iorTemplates != null) && (iorTemplates.equals(iorTemplates));
  }
  
  public int hashCode()
  {
    return iorTemplates.hashCode();
  }
  
  public String[] _truncatable_ids()
  {
    return new String[] { "IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0" };
  }
  
  public TypeCode _type()
  {
    return ObjectReferenceFactoryHelper.type();
  }
  
  public void _read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream;
    iorTemplates = IORFactories.makeIORTemplateList(localInputStream);
  }
  
  public void _write(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    org.omg.CORBA_2_3.portable.OutputStream localOutputStream = (org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream;
    iorTemplates.write(localOutputStream);
  }
  
  public IORFactory getIORFactory()
  {
    return iorTemplates;
  }
  
  public IORTemplateList getIORTemplateList()
  {
    return iorTemplates;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectReferenceFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */