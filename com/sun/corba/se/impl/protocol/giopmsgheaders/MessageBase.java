package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDRInputStream_1_0;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.AddressingDispositionException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;
import org.omg.CORBA.SystemException;
import org.omg.IOP.TaggedProfile;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public abstract class MessageBase
  implements Message
{
  public byte[] giopHeader;
  private ByteBuffer byteBuffer;
  private int threadPoolToUse;
  byte encodingVersion = 0;
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");
  
  public MessageBase() {}
  
  public static String typeToString(int paramInt)
  {
    return typeToString((byte)paramInt);
  }
  
  public static String typeToString(byte paramByte)
  {
    String str = paramByte + "/";
    switch (paramByte)
    {
    case 0: 
      str = str + "GIOPRequest";
      break;
    case 1: 
      str = str + "GIOPReply";
      break;
    case 2: 
      str = str + "GIOPCancelRequest";
      break;
    case 3: 
      str = str + "GIOPLocateRequest";
      break;
    case 4: 
      str = str + "GIOPLocateReply";
      break;
    case 5: 
      str = str + "GIOPCloseConnection";
      break;
    case 6: 
      str = str + "GIOPMessageError";
      break;
    case 7: 
      str = str + "GIOPFragment";
      break;
    default: 
      str = str + "Unknown";
    }
    return str;
  }
  
  public static MessageBase readGIOPMessage(ORB paramORB, CorbaConnection paramCorbaConnection)
  {
    MessageBase localMessageBase = readGIOPHeader(paramORB, paramCorbaConnection);
    localMessageBase = (MessageBase)readGIOPBody(paramORB, paramCorbaConnection, localMessageBase);
    return localMessageBase;
  }
  
  public static MessageBase readGIOPHeader(ORB paramORB, CorbaConnection paramCorbaConnection)
  {
    Object localObject1 = null;
    ReadTimeouts localReadTimeouts = paramORB.getORBData().getTransportTCPReadTimeouts();
    ByteBuffer localByteBuffer1 = null;
    try
    {
      localByteBuffer1 = paramCorbaConnection.read(12, 0, 12, localReadTimeouts.get_max_giop_header_time_to_wait());
    }
    catch (IOException localIOException)
    {
      throw wrapper.ioexceptionWhenReadingConnection(localIOException);
    }
    if (giopDebugFlag)
    {
      dprint(".readGIOPHeader: " + typeToString(localByteBuffer1.get(7)));
      dprint(".readGIOPHeader: GIOP header is: ");
      ByteBuffer localByteBuffer2 = localByteBuffer1.asReadOnlyBuffer();
      localByteBuffer2.position(0).limit(12);
      ByteBufferWithInfo localByteBufferWithInfo = new ByteBufferWithInfo(paramORB, localByteBuffer2);
      buflen = 12;
      CDRInputStream_1_0.printBuffer(localByteBufferWithInfo);
    }
    int i = localByteBuffer1.get(0) << 24 & 0xFF000000;
    int j = localByteBuffer1.get(1) << 16 & 0xFF0000;
    int k = localByteBuffer1.get(2) << 8 & 0xFF00;
    int m = localByteBuffer1.get(3) << 0 & 0xFF;
    int n = i | j | k | m;
    if (n != 1195986768) {
      throw wrapper.giopMagicError(CompletionStatus.COMPLETED_MAYBE);
    }
    byte b = 0;
    if ((localByteBuffer1.get(4) == 13) && (localByteBuffer1.get(5) <= 1) && (localByteBuffer1.get(5) > 0) && (paramORB.getORBData().isJavaSerializationEnabled()))
    {
      b = localByteBuffer1.get(5);
      localByteBuffer1.put(4, (byte)1);
      localByteBuffer1.put(5, (byte)2);
    }
    GIOPVersion localGIOPVersion = paramORB.getORBData().getGIOPVersion();
    if (giopDebugFlag)
    {
      dprint(".readGIOPHeader: Message GIOP version: " + localByteBuffer1.get(4) + '.' + localByteBuffer1.get(5));
      dprint(".readGIOPHeader: ORB Max GIOP Version: " + localGIOPVersion);
    }
    if (((localByteBuffer1.get(4) > localGIOPVersion.getMajor()) || ((localByteBuffer1.get(4) == localGIOPVersion.getMajor()) && (localByteBuffer1.get(5) > localGIOPVersion.getMinor()))) && (localByteBuffer1.get(7) != 6)) {
      throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    AreFragmentsAllowed(localByteBuffer1.get(4), localByteBuffer1.get(5), localByteBuffer1.get(6), localByteBuffer1.get(7));
    switch (localByteBuffer1.get(7))
    {
    case 0: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: creating RequestMessage");
      }
      if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0)) {
        localObject1 = new RequestMessage_1_0(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1)) {
        localObject1 = new RequestMessage_1_1(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2)) {
        localObject1 = new RequestMessage_1_2(paramORB);
      } else {
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
      break;
    case 3: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: creating LocateRequestMessage");
      }
      if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0)) {
        localObject1 = new LocateRequestMessage_1_0(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1)) {
        localObject1 = new LocateRequestMessage_1_1(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2)) {
        localObject1 = new LocateRequestMessage_1_2(paramORB);
      } else {
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
      break;
    case 2: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: creating CancelRequestMessage");
      }
      if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0)) {
        localObject1 = new CancelRequestMessage_1_0();
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1)) {
        localObject1 = new CancelRequestMessage_1_1();
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2)) {
        localObject1 = new CancelRequestMessage_1_2();
      } else {
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
      break;
    case 1: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: creating ReplyMessage");
      }
      if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0)) {
        localObject1 = new ReplyMessage_1_0(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1)) {
        localObject1 = new ReplyMessage_1_1(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2)) {
        localObject1 = new ReplyMessage_1_2(paramORB);
      } else {
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
      break;
    case 4: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: creating LocateReplyMessage");
      }
      if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0)) {
        localObject1 = new LocateReplyMessage_1_0(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1)) {
        localObject1 = new LocateReplyMessage_1_1(paramORB);
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2)) {
        localObject1 = new LocateReplyMessage_1_2(paramORB);
      } else {
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
      break;
    case 5: 
    case 6: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: creating Message for CloseConnection or MessageError");
      }
      if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0)) {
        localObject1 = new Message_1_0();
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1)) {
        localObject1 = new Message_1_1();
      } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2)) {
        localObject1 = new Message_1_1();
      } else {
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
      break;
    case 7: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: creating FragmentMessage");
      }
      if ((localByteBuffer1.get(4) != 1) || (localByteBuffer1.get(5) != 0)) {
        if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1)) {
          localObject1 = new FragmentMessage_1_1();
        } else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2)) {
          localObject1 = new FragmentMessage_1_2();
        } else {
          throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
        }
      }
      break;
    default: 
      if (giopDebugFlag) {
        dprint(".readGIOPHeader: UNKNOWN MESSAGE TYPE: " + localByteBuffer1.get(7));
      }
      throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    Object localObject2;
    if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0))
    {
      localObject2 = (Message_1_0)localObject1;
      magic = n;
      GIOP_version = new GIOPVersion(localByteBuffer1.get(4), localByteBuffer1.get(5));
      byte_order = (localByteBuffer1.get(6) == 1);
      threadPoolToUse = 0;
      message_type = localByteBuffer1.get(7);
      message_size = (readSize(localByteBuffer1.get(8), localByteBuffer1.get(9), localByteBuffer1.get(10), localByteBuffer1.get(11), ((Message_1_0)localObject2).isLittleEndian()) + 12);
    }
    else
    {
      localObject2 = (Message_1_1)localObject1;
      magic = n;
      GIOP_version = new GIOPVersion(localByteBuffer1.get(4), localByteBuffer1.get(5));
      flags = ((byte)(localByteBuffer1.get(6) & 0x3));
      threadPoolToUse = (localByteBuffer1.get(6) >>> 2 & 0x3F);
      message_type = localByteBuffer1.get(7);
      message_size = (readSize(localByteBuffer1.get(8), localByteBuffer1.get(9), localByteBuffer1.get(10), localByteBuffer1.get(11), ((Message_1_1)localObject2).isLittleEndian()) + 12);
    }
    if (giopDebugFlag)
    {
      dprint(".readGIOPHeader: header construction complete.");
      localObject2 = localByteBuffer1.asReadOnlyBuffer();
      byte[] arrayOfByte = new byte[12];
      ((ByteBuffer)localObject2).position(0).limit(12);
      ((ByteBuffer)localObject2).get(arrayOfByte, 0, arrayOfByte.length);
      giopHeader = arrayOfByte;
    }
    ((MessageBase)localObject1).setByteBuffer(localByteBuffer1);
    ((MessageBase)localObject1).setEncodingVersion(b);
    return (MessageBase)localObject1;
  }
  
  public static Message readGIOPBody(ORB paramORB, CorbaConnection paramCorbaConnection, Message paramMessage)
  {
    ReadTimeouts localReadTimeouts = paramORB.getORBData().getTransportTCPReadTimeouts();
    ByteBuffer localByteBuffer1 = paramMessage.getByteBuffer();
    localByteBuffer1.position(12);
    int i = paramMessage.getSize() - 12;
    try
    {
      localByteBuffer1 = paramCorbaConnection.read(localByteBuffer1, 12, i, localReadTimeouts.get_max_time_to_wait());
    }
    catch (IOException localIOException)
    {
      throw wrapper.ioexceptionWhenReadingConnection(localIOException);
    }
    paramMessage.setByteBuffer(localByteBuffer1);
    if (giopDebugFlag)
    {
      dprint(".readGIOPBody: received message:");
      ByteBuffer localByteBuffer2 = localByteBuffer1.asReadOnlyBuffer();
      localByteBuffer2.position(0).limit(paramMessage.getSize());
      ByteBufferWithInfo localByteBufferWithInfo = new ByteBufferWithInfo(paramORB, localByteBuffer2);
      CDRInputStream_1_0.printBuffer(localByteBufferWithInfo);
    }
    return paramMessage;
  }
  
  private static RequestMessage createRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, boolean paramBoolean, byte[] paramArrayOfByte, String paramString, ServiceContexts paramServiceContexts, Principal paramPrincipal)
  {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
      return new RequestMessage_1_0(paramORB, paramServiceContexts, paramInt, paramBoolean, paramArrayOfByte, paramString, paramPrincipal);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
      return new RequestMessage_1_1(paramORB, paramServiceContexts, paramInt, paramBoolean, new byte[] { 0, 0, 0 }, paramArrayOfByte, paramString, paramPrincipal);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_2))
    {
      byte b = 3;
      if (paramBoolean) {
        b = 3;
      } else {
        b = 0;
      }
      TargetAddress localTargetAddress = new TargetAddress();
      localTargetAddress.object_key(paramArrayOfByte);
      RequestMessage_1_2 localRequestMessage_1_2 = new RequestMessage_1_2(paramORB, paramInt, b, new byte[] { 0, 0, 0 }, localTargetAddress, paramString, paramServiceContexts);
      localRequestMessage_1_2.setEncodingVersion(paramByte);
      return localRequestMessage_1_2;
    }
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static RequestMessage createRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, boolean paramBoolean, IOR paramIOR, short paramShort, String paramString, ServiceContexts paramServiceContexts, Principal paramPrincipal)
  {
    Object localObject1 = null;
    IIOPProfile localIIOPProfile = paramIOR.getProfile();
    Object localObject2;
    byte b;
    Object localObject3;
    if (paramShort == 0)
    {
      localIIOPProfile = paramIOR.getProfile();
      ObjectKey localObjectKey = localIIOPProfile.getObjectKey();
      localObject2 = localObjectKey.getBytes(paramORB);
      localObject1 = createRequest(paramORB, paramGIOPVersion, paramByte, paramInt, paramBoolean, (byte[])localObject2, paramString, paramServiceContexts, paramPrincipal);
    }
    else
    {
      if (!paramGIOPVersion.equals(GIOPVersion.V1_2)) {
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
      b = 3;
      if (paramBoolean) {
        b = 3;
      } else {
        b = 0;
      }
      localObject2 = new TargetAddress();
      if (paramShort == 1)
      {
        localIIOPProfile = paramIOR.getProfile();
        ((TargetAddress)localObject2).profile(localIIOPProfile.getIOPProfile());
      }
      else if (paramShort == 2)
      {
        localObject3 = new IORAddressingInfo(0, paramIOR.getIOPIOR());
        ((TargetAddress)localObject2).ior((IORAddressingInfo)localObject3);
      }
      else
      {
        throw wrapper.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO);
      }
      localObject1 = new RequestMessage_1_2(paramORB, paramInt, b, new byte[] { 0, 0, 0 }, (TargetAddress)localObject2, paramString, paramServiceContexts);
      ((RequestMessage)localObject1).setEncodingVersion(paramByte);
    }
    if (paramGIOPVersion.supportsIORIIOPProfileComponents())
    {
      b = 0;
      localObject2 = (IIOPProfileTemplate)localIIOPProfile.getTaggedProfileTemplate();
      localObject3 = ((IIOPProfileTemplate)localObject2).iteratorById(1398099457);
      int i;
      if (((Iterator)localObject3).hasNext()) {
        i = ((RequestPartitioningComponent)((Iterator)localObject3).next()).getRequestPartitioningId();
      }
      if ((i < 0) || (i > 63)) {
        throw wrapper.invalidRequestPartitioningId(new Integer(i), new Integer(0), new Integer(63));
      }
      ((RequestMessage)localObject1).setThreadPoolToUse(i);
    }
    return (RequestMessage)localObject1;
  }
  
  public static ReplyMessage createReply(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt1, int paramInt2, ServiceContexts paramServiceContexts, IOR paramIOR)
  {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
      return new ReplyMessage_1_0(paramORB, paramServiceContexts, paramInt1, paramInt2, paramIOR);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
      return new ReplyMessage_1_1(paramORB, paramServiceContexts, paramInt1, paramInt2, paramIOR);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_2))
    {
      ReplyMessage_1_2 localReplyMessage_1_2 = new ReplyMessage_1_2(paramORB, paramInt1, paramInt2, paramServiceContexts, paramIOR);
      localReplyMessage_1_2.setEncodingVersion(paramByte);
      return localReplyMessage_1_2;
    }
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static LocateRequestMessage createLocateRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, byte[] paramArrayOfByte)
  {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
      return new LocateRequestMessage_1_0(paramORB, paramInt, paramArrayOfByte);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
      return new LocateRequestMessage_1_1(paramORB, paramInt, paramArrayOfByte);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_2))
    {
      TargetAddress localTargetAddress = new TargetAddress();
      localTargetAddress.object_key(paramArrayOfByte);
      LocateRequestMessage_1_2 localLocateRequestMessage_1_2 = new LocateRequestMessage_1_2(paramORB, paramInt, localTargetAddress);
      localLocateRequestMessage_1_2.setEncodingVersion(paramByte);
      return localLocateRequestMessage_1_2;
    }
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static LocateReplyMessage createLocateReply(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt1, int paramInt2, IOR paramIOR)
  {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
      return new LocateReplyMessage_1_0(paramORB, paramInt1, paramInt2, paramIOR);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
      return new LocateReplyMessage_1_1(paramORB, paramInt1, paramInt2, paramIOR);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_2))
    {
      LocateReplyMessage_1_2 localLocateReplyMessage_1_2 = new LocateReplyMessage_1_2(paramORB, paramInt1, paramInt2, paramIOR);
      localLocateReplyMessage_1_2.setEncodingVersion(paramByte);
      return localLocateReplyMessage_1_2;
    }
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static CancelRequestMessage createCancelRequest(GIOPVersion paramGIOPVersion, int paramInt)
  {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
      return new CancelRequestMessage_1_0(paramInt);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
      return new CancelRequestMessage_1_1(paramInt);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
      return new CancelRequestMessage_1_2(paramInt);
    }
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static Message createCloseConnection(GIOPVersion paramGIOPVersion)
  {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
      return new Message_1_0(1195986768, false, (byte)5, 0);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
      return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)5, 0);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
      return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)5, 0);
    }
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static Message createMessageError(GIOPVersion paramGIOPVersion)
  {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
      return new Message_1_0(1195986768, false, (byte)6, 0);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
      return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)6, 0);
    }
    if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
      return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)6, 0);
    }
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static FragmentMessage createFragmentMessage(GIOPVersion paramGIOPVersion)
  {
    return null;
  }
  
  public static int getRequestId(Message paramMessage)
  {
    switch (paramMessage.getType())
    {
    case 0: 
      return ((RequestMessage)paramMessage).getRequestId();
    case 1: 
      return ((ReplyMessage)paramMessage).getRequestId();
    case 3: 
      return ((LocateRequestMessage)paramMessage).getRequestId();
    case 4: 
      return ((LocateReplyMessage)paramMessage).getRequestId();
    case 2: 
      return ((CancelRequestMessage)paramMessage).getRequestId();
    case 7: 
      return ((FragmentMessage)paramMessage).getRequestId();
    }
    throw wrapper.illegalGiopMsgType(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static void setFlag(ByteBuffer paramByteBuffer, int paramInt)
  {
    int i = paramByteBuffer.get(6);
    i = (byte)(i | paramInt);
    paramByteBuffer.put(6, i);
  }
  
  public static void clearFlag(byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[6] = ((byte)(paramArrayOfByte[6] & (0xFF ^ paramInt)));
  }
  
  private static void AreFragmentsAllowed(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    if ((paramByte1 == 1) && (paramByte2 == 0) && (paramByte4 == 7)) {
      throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
    }
    if ((paramByte3 & 0x2) == 2) {
      switch (paramByte4)
      {
      case 2: 
      case 5: 
      case 6: 
        throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
      case 3: 
      case 4: 
        if ((paramByte1 == 1) && (paramByte2 == 1)) {
          throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
        }
        break;
      }
    }
  }
  
  static ObjectKey extractObjectKey(byte[] paramArrayOfByte, ORB paramORB)
  {
    try
    {
      if (paramArrayOfByte != null)
      {
        ObjectKey localObjectKey = paramORB.getObjectKeyFactory().create(paramArrayOfByte);
        if (localObjectKey != null) {
          return localObjectKey;
        }
      }
    }
    catch (Exception localException) {}
    throw wrapper.invalidObjectKey();
  }
  
  static ObjectKey extractObjectKey(TargetAddress paramTargetAddress, ORB paramORB)
  {
    int i = paramORB.getORBData().getGIOPTargetAddressPreference();
    int j = paramTargetAddress.discriminator();
    switch (i)
    {
    case 0: 
      if (j != 0) {
        throw new AddressingDispositionException((short)0);
      }
      break;
    case 1: 
      if (j != 1) {
        throw new AddressingDispositionException((short)1);
      }
      break;
    case 2: 
      if (j != 2) {
        throw new AddressingDispositionException((short)2);
      }
      break;
    case 3: 
      break;
    default: 
      throw wrapper.orbTargetAddrPreferenceInExtractObjectkeyInvalid();
    }
    try
    {
      Object localObject1;
      TaggedProfile localTaggedProfile;
      Object localObject2;
      switch (j)
      {
      case 0: 
        byte[] arrayOfByte = paramTargetAddress.object_key();
        if (arrayOfByte != null)
        {
          localObject1 = paramORB.getObjectKeyFactory().create(arrayOfByte);
          if (localObject1 != null) {
            return (ObjectKey)localObject1;
          }
        }
        break;
      case 1: 
        localObject1 = null;
        localTaggedProfile = paramTargetAddress.profile();
        if (localTaggedProfile != null)
        {
          localObject1 = IIOPFactories.makeIIOPProfile(paramORB, localTaggedProfile);
          localObject2 = ((IIOPProfile)localObject1).getObjectKey();
          if (localObject2 != null) {
            return (ObjectKey)localObject2;
          }
        }
        break;
      case 2: 
        localObject2 = paramTargetAddress.ior();
        if (localObject2 != null)
        {
          localTaggedProfile = ior.profiles[selected_profile_index];
          localObject1 = IIOPFactories.makeIIOPProfile(paramORB, localTaggedProfile);
          ObjectKey localObjectKey = ((IIOPProfile)localObject1).getObjectKey();
          if (localObjectKey != null) {
            return localObjectKey;
          }
        }
        break;
      }
    }
    catch (Exception localException) {}
    throw wrapper.invalidObjectKey();
  }
  
  private static int readSize(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, boolean paramBoolean)
  {
    int i;
    int j;
    int k;
    int m;
    if (!paramBoolean)
    {
      i = paramByte1 << 24 & 0xFF000000;
      j = paramByte2 << 16 & 0xFF0000;
      k = paramByte3 << 8 & 0xFF00;
      m = paramByte4 << 0 & 0xFF;
    }
    else
    {
      i = paramByte4 << 24 & 0xFF000000;
      j = paramByte3 << 16 & 0xFF0000;
      k = paramByte2 << 8 & 0xFF00;
      m = paramByte1 << 0 & 0xFF;
    }
    return i | j | k | m;
  }
  
  static void nullCheck(Object paramObject)
  {
    if (paramObject == null) {
      throw wrapper.nullNotAllowed();
    }
  }
  
  static SystemException getSystemException(String paramString1, int paramInt, CompletionStatus paramCompletionStatus, String paramString2, ORBUtilSystemException paramORBUtilSystemException)
  {
    SystemException localSystemException = null;
    try
    {
      Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(paramString1);
      if (paramString2 == null)
      {
        localSystemException = (SystemException)localClass.newInstance();
      }
      else
      {
        Class[] arrayOfClass = { String.class };
        Constructor localConstructor = localClass.getConstructor(arrayOfClass);
        Object[] arrayOfObject = { paramString2 };
        localSystemException = (SystemException)localConstructor.newInstance(arrayOfObject);
      }
    }
    catch (Exception localException)
    {
      throw paramORBUtilSystemException.badSystemExceptionInReply(CompletionStatus.COMPLETED_MAYBE, localException);
    }
    minor = paramInt;
    completed = paramCompletionStatus;
    return localSystemException;
  }
  
  public void callback(MessageHandler paramMessageHandler)
    throws IOException
  {
    paramMessageHandler.handleInput(this);
  }
  
  public ByteBuffer getByteBuffer()
  {
    return byteBuffer;
  }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer)
  {
    byteBuffer = paramByteBuffer;
  }
  
  public int getThreadPoolToUse()
  {
    return threadPoolToUse;
  }
  
  public byte getEncodingVersion()
  {
    return encodingVersion;
  }
  
  public void setEncodingVersion(byte paramByte)
  {
    encodingVersion = paramByte;
  }
  
  private static void dprint(String paramString)
  {
    ORBUtility.dprint("MessageBase", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\MessageBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */