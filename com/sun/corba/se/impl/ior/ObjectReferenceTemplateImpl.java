package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ObjectReferenceTemplateHelper;

public class ObjectReferenceTemplateImpl
  extends ObjectReferenceProducerBase
  implements ObjectReferenceTemplate, StreamableValue
{
  private transient IORTemplate iorTemplate;
  public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0";
  
  public ObjectReferenceTemplateImpl(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    super((ORB)paramInputStream.orb());
    _read(paramInputStream);
  }
  
  public ObjectReferenceTemplateImpl(ORB paramORB, IORTemplate paramIORTemplate)
  {
    super(paramORB);
    iorTemplate = paramIORTemplate;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ObjectReferenceTemplateImpl)) {
      return false;
    }
    ObjectReferenceTemplateImpl localObjectReferenceTemplateImpl = (ObjectReferenceTemplateImpl)paramObject;
    return (iorTemplate != null) && (iorTemplate.equals(iorTemplate));
  }
  
  public int hashCode()
  {
    return iorTemplate.hashCode();
  }
  
  public String[] _truncatable_ids()
  {
    return new String[] { "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0" };
  }
  
  public TypeCode _type()
  {
    return ObjectReferenceTemplateHelper.type();
  }
  
  public void _read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream;
    iorTemplate = IORFactories.makeIORTemplate(localInputStream);
    orb = ((ORB)localInputStream.orb());
  }
  
  public void _write(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    org.omg.CORBA_2_3.portable.OutputStream localOutputStream = (org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream;
    iorTemplate.write(localOutputStream);
  }
  
  public String server_id()
  {
    int i = iorTemplate.getObjectKeyTemplate().getServerId();
    return Integer.toString(i);
  }
  
  public String orb_id()
  {
    return iorTemplate.getObjectKeyTemplate().getORBId();
  }
  
  public String[] adapter_name()
  {
    ObjectAdapterId localObjectAdapterId = iorTemplate.getObjectKeyTemplate().getObjectAdapterId();
    return localObjectAdapterId.getAdapterName();
  }
  
  public IORFactory getIORFactory()
  {
    return iorTemplate;
  }
  
  public IORTemplateList getIORTemplateList()
  {
    IORTemplateList localIORTemplateList = IORFactories.makeIORTemplateList();
    localIORTemplateList.add(iorTemplate);
    localIORTemplateList.makeImmutable();
    return localIORTemplateList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectReferenceTemplateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */