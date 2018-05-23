package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IdentifiableBase;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Iterator;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TaggedProfileHelper;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public class IIOPProfileImpl
  extends IdentifiableBase
  implements IIOPProfile
{
  private ORB orb;
  private IORSystemException wrapper;
  private ObjectId oid;
  private IIOPProfileTemplate proftemp;
  private ObjectKeyTemplate oktemp;
  protected String codebase = null;
  protected boolean cachedCodebase = false;
  private boolean checkedIsLocal = false;
  private boolean cachedIsLocal = false;
  private GIOPVersion giopVersion = null;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof IIOPProfileImpl)) {
      return false;
    }
    IIOPProfileImpl localIIOPProfileImpl = (IIOPProfileImpl)paramObject;
    return (oid.equals(oid)) && (proftemp.equals(proftemp)) && (oktemp.equals(oktemp));
  }
  
  public int hashCode()
  {
    return oid.hashCode() ^ proftemp.hashCode() ^ oktemp.hashCode();
  }
  
  public ObjectId getObjectId()
  {
    return oid;
  }
  
  public TaggedProfileTemplate getTaggedProfileTemplate()
  {
    return proftemp;
  }
  
  public ObjectKeyTemplate getObjectKeyTemplate()
  {
    return oktemp;
  }
  
  private IIOPProfileImpl(ORB paramORB)
  {
    orb = paramORB;
    wrapper = IORSystemException.get(paramORB, "oa.ior");
  }
  
  public IIOPProfileImpl(ORB paramORB, ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, IIOPProfileTemplate paramIIOPProfileTemplate)
  {
    this(paramORB);
    oktemp = paramObjectKeyTemplate;
    oid = paramObjectId;
    proftemp = paramIIOPProfileTemplate;
  }
  
  public IIOPProfileImpl(InputStream paramInputStream)
  {
    this((ORB)paramInputStream.orb());
    init(paramInputStream);
  }
  
  public IIOPProfileImpl(ORB paramORB, org.omg.IOP.TaggedProfile paramTaggedProfile)
  {
    this(paramORB);
    if ((paramTaggedProfile == null) || (tag != 0) || (profile_data == null)) {
      throw wrapper.invalidTaggedProfile();
    }
    EncapsInputStream localEncapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(paramORB, profile_data, profile_data.length);
    localEncapsInputStream.consumeEndian();
    init(localEncapsInputStream);
  }
  
  private void init(InputStream paramInputStream)
  {
    GIOPVersion localGIOPVersion = new GIOPVersion();
    localGIOPVersion.read(paramInputStream);
    IIOPAddressImpl localIIOPAddressImpl = new IIOPAddressImpl(paramInputStream);
    byte[] arrayOfByte = EncapsulationUtility.readOctets(paramInputStream);
    ObjectKey localObjectKey = orb.getObjectKeyFactory().create(arrayOfByte);
    oktemp = localObjectKey.getTemplate();
    oid = localObjectKey.getId();
    proftemp = IIOPFactories.makeIIOPProfileTemplate(orb, localGIOPVersion, localIIOPAddressImpl);
    if (localGIOPVersion.getMinor() > 0) {
      EncapsulationUtility.readIdentifiableSequence(proftemp, orb.getTaggedComponentFactoryFinder(), paramInputStream);
    }
    if (uncachedGetCodeBase() == null)
    {
      JavaCodebaseComponent localJavaCodebaseComponent = LocalCodeBaseSingletonHolder.comp;
      if (localJavaCodebaseComponent != null)
      {
        if (localGIOPVersion.getMinor() > 0) {
          proftemp.add(localJavaCodebaseComponent);
        }
        codebase = localJavaCodebaseComponent.getURLs();
      }
      cachedCodebase = true;
    }
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    proftemp.write(oktemp, oid, paramOutputStream);
  }
  
  public int getId()
  {
    return proftemp.getId();
  }
  
  public boolean isEquivalent(com.sun.corba.se.spi.ior.TaggedProfile paramTaggedProfile)
  {
    if (!(paramTaggedProfile instanceof IIOPProfile)) {
      return false;
    }
    IIOPProfile localIIOPProfile = (IIOPProfile)paramTaggedProfile;
    return (oid.equals(localIIOPProfile.getObjectId())) && (proftemp.isEquivalent(localIIOPProfile.getTaggedProfileTemplate())) && (oktemp.equals(localIIOPProfile.getObjectKeyTemplate()));
  }
  
  public ObjectKey getObjectKey()
  {
    ObjectKey localObjectKey = IORFactories.makeObjectKey(oktemp, oid);
    return localObjectKey;
  }
  
  public org.omg.IOP.TaggedProfile getIOPProfile()
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream(orb);
    localEncapsOutputStream.write_long(getId());
    write(localEncapsOutputStream);
    InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
    return TaggedProfileHelper.read(localInputStream);
  }
  
  private String uncachedGetCodeBase()
  {
    Iterator localIterator = proftemp.iteratorById(25);
    if (localIterator.hasNext())
    {
      JavaCodebaseComponent localJavaCodebaseComponent = (JavaCodebaseComponent)localIterator.next();
      return localJavaCodebaseComponent.getURLs();
    }
    return null;
  }
  
  public synchronized String getCodebase()
  {
    if (!cachedCodebase)
    {
      cachedCodebase = true;
      codebase = uncachedGetCodeBase();
    }
    return codebase;
  }
  
  public ORBVersion getORBVersion()
  {
    return oktemp.getORBVersion();
  }
  
  public synchronized boolean isLocal()
  {
    if (!checkedIsLocal)
    {
      checkedIsLocal = true;
      String str = proftemp.getPrimaryAddress().getHost();
      cachedIsLocal = ((orb.isLocalHost(str)) && (orb.isLocalServerId(oktemp.getSubcontractId(), oktemp.getServerId())) && (orb.getLegacyServerSocketManager().legacyIsLocalServerPort(proftemp.getPrimaryAddress().getPort())));
    }
    return cachedIsLocal;
  }
  
  public Object getServant()
  {
    if (!isLocal()) {
      return null;
    }
    RequestDispatcherRegistry localRequestDispatcherRegistry = orb.getRequestDispatcherRegistry();
    ObjectAdapterFactory localObjectAdapterFactory = localRequestDispatcherRegistry.getObjectAdapterFactory(oktemp.getSubcontractId());
    ObjectAdapterId localObjectAdapterId = oktemp.getObjectAdapterId();
    ObjectAdapter localObjectAdapter = null;
    try
    {
      localObjectAdapter = localObjectAdapterFactory.find(localObjectAdapterId);
    }
    catch (SystemException localSystemException)
    {
      wrapper.getLocalServantFailure(localSystemException, localObjectAdapterId.toString());
      return null;
    }
    byte[] arrayOfByte = oid.getId();
    org.omg.CORBA.Object localObject = localObjectAdapter.getLocalServant(arrayOfByte);
    return localObject;
  }
  
  public synchronized GIOPVersion getGIOPVersion()
  {
    return proftemp.getGIOPVersion();
  }
  
  public void makeImmutable()
  {
    proftemp.makeImmutable();
  }
  
  private static class LocalCodeBaseSingletonHolder
  {
    public static JavaCodebaseComponent comp;
    
    private LocalCodeBaseSingletonHolder() {}
    
    static
    {
      String str = JDKBridge.getLocalCodebase();
      if (str == null) {
        comp = null;
      } else {
        comp = IIOPFactories.makeJavaCodebaseComponent(str);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPProfileImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */