package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.org.omg.SendingContext.CodeBase;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;

public class ServiceContexts
{
  private static final int JAVAIDL_ALIGN_SERVICE_ID = -1106033203;
  private ORB orb;
  private Map scMap;
  private boolean addAlignmentOnWrite;
  private CodeBase codeBase;
  private GIOPVersion giopVersion;
  private ORBUtilSystemException wrapper;
  
  private static boolean isDebugging(OutputStream paramOutputStream)
  {
    ORB localORB = (ORB)paramOutputStream.orb();
    if (localORB == null) {
      return false;
    }
    return serviceContextDebugFlag;
  }
  
  private static boolean isDebugging(InputStream paramInputStream)
  {
    ORB localORB = (ORB)paramInputStream.orb();
    if (localORB == null) {
      return false;
    }
    return serviceContextDebugFlag;
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint(this, paramString);
  }
  
  public static void writeNullServiceContext(OutputStream paramOutputStream)
  {
    if (isDebugging(paramOutputStream)) {
      ORBUtility.dprint("ServiceContexts", "Writing null service context");
    }
    paramOutputStream.write_long(0);
  }
  
  private void createMapFromInputStream(InputStream paramInputStream)
  {
    orb = ((ORB)paramInputStream.orb());
    if (orb.serviceContextDebugFlag) {
      dprint("Constructing ServiceContexts from input stream");
    }
    int i = paramInputStream.read_long();
    if (orb.serviceContextDebugFlag) {
      dprint("Number of service contexts = " + i);
    }
    for (int j = 0; j < i; j++)
    {
      int k = paramInputStream.read_long();
      if (orb.serviceContextDebugFlag) {
        dprint("Reading service context id " + k);
      }
      byte[] arrayOfByte = OctetSeqHelper.read(paramInputStream);
      if (orb.serviceContextDebugFlag) {
        dprint("Service context" + k + " length: " + arrayOfByte.length);
      }
      scMap.put(new Integer(k), arrayOfByte);
    }
  }
  
  public ServiceContexts(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    addAlignmentOnWrite = false;
    scMap = new HashMap();
    giopVersion = paramORB.getORBData().getGIOPVersion();
    codeBase = null;
  }
  
  public ServiceContexts(InputStream paramInputStream)
  {
    this((ORB)paramInputStream.orb());
    codeBase = ((CDRInputStream)paramInputStream).getCodeBase();
    createMapFromInputStream(paramInputStream);
    giopVersion = ((CDRInputStream)paramInputStream).getGIOPVersion();
  }
  
  private ServiceContext unmarshal(Integer paramInteger, byte[] paramArrayOfByte)
  {
    ServiceContextRegistry localServiceContextRegistry = orb.getServiceContextRegistry();
    ServiceContextData localServiceContextData = localServiceContextRegistry.findServiceContextData(paramInteger.intValue());
    Object localObject = null;
    if (localServiceContextData == null)
    {
      if (orb.serviceContextDebugFlag) {
        dprint("Could not find ServiceContextData for " + paramInteger + " using UnknownServiceContext");
      }
      localObject = new UnknownServiceContext(paramInteger.intValue(), paramArrayOfByte);
    }
    else
    {
      if (orb.serviceContextDebugFlag) {
        dprint("Found " + localServiceContextData);
      }
      EncapsInputStream localEncapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(orb, paramArrayOfByte, paramArrayOfByte.length, giopVersion, codeBase);
      localEncapsInputStream.consumeEndian();
      localObject = localServiceContextData.makeServiceContext(localEncapsInputStream, giopVersion);
      if (localObject == null) {
        throw wrapper.svcctxUnmarshalError(CompletionStatus.COMPLETED_MAYBE);
      }
    }
    return (ServiceContext)localObject;
  }
  
  public void addAlignmentPadding()
  {
    addAlignmentOnWrite = true;
  }
  
  public void write(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion)
  {
    if (isDebugging(paramOutputStream))
    {
      dprint("Writing service contexts to output stream");
      Utility.printStackTrace();
    }
    int i = scMap.size();
    if (addAlignmentOnWrite)
    {
      if (isDebugging(paramOutputStream)) {
        dprint("Adding alignment padding");
      }
      i++;
    }
    if (isDebugging(paramOutputStream)) {
      dprint("Service context has " + i + " components");
    }
    paramOutputStream.write_long(i);
    writeServiceContextsInOrder(paramOutputStream, paramGIOPVersion);
    if (addAlignmentOnWrite)
    {
      if (isDebugging(paramOutputStream)) {
        dprint("Writing alignment padding");
      }
      paramOutputStream.write_long(-1106033203);
      paramOutputStream.write_long(4);
      paramOutputStream.write_octet((byte)0);
      paramOutputStream.write_octet((byte)0);
      paramOutputStream.write_octet((byte)0);
      paramOutputStream.write_octet((byte)0);
    }
    if (isDebugging(paramOutputStream)) {
      dprint("Service context writing complete");
    }
  }
  
  private void writeServiceContextsInOrder(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion)
  {
    Integer localInteger1 = new Integer(9);
    Object localObject = scMap.remove(localInteger1);
    Iterator localIterator = scMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger2 = (Integer)localIterator.next();
      writeMapEntry(paramOutputStream, localInteger2, scMap.get(localInteger2), paramGIOPVersion);
    }
    if (localObject != null)
    {
      writeMapEntry(paramOutputStream, localInteger1, localObject, paramGIOPVersion);
      scMap.put(localInteger1, localObject);
    }
  }
  
  private void writeMapEntry(OutputStream paramOutputStream, Integer paramInteger, Object paramObject, GIOPVersion paramGIOPVersion)
  {
    if ((paramObject instanceof byte[]))
    {
      if (isDebugging(paramOutputStream)) {
        dprint("Writing service context bytes for id " + paramInteger);
      }
      OctetSeqHelper.write(paramOutputStream, (byte[])paramObject);
    }
    else
    {
      ServiceContext localServiceContext = (ServiceContext)paramObject;
      if (isDebugging(paramOutputStream)) {
        dprint("Writing service context " + localServiceContext);
      }
      localServiceContext.write(paramOutputStream, paramGIOPVersion);
    }
  }
  
  public void put(ServiceContext paramServiceContext)
  {
    Integer localInteger = new Integer(paramServiceContext.getId());
    scMap.put(localInteger, paramServiceContext);
  }
  
  public void delete(int paramInt)
  {
    delete(new Integer(paramInt));
  }
  
  public void delete(Integer paramInteger)
  {
    scMap.remove(paramInteger);
  }
  
  public ServiceContext get(int paramInt)
  {
    return get(new Integer(paramInt));
  }
  
  public ServiceContext get(Integer paramInteger)
  {
    Object localObject = scMap.get(paramInteger);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof byte[]))
    {
      ServiceContext localServiceContext = unmarshal(paramInteger, (byte[])localObject);
      scMap.put(paramInteger, localServiceContext);
      return localServiceContext;
    }
    return (ServiceContext)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\ServiceContexts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */