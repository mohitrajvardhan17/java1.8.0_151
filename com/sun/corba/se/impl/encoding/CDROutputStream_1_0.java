package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.CacheTable;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Principal;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueBase;

public class CDROutputStream_1_0
  extends CDROutputStreamBase
{
  private static final int INDIRECTION_TAG = -1;
  protected boolean littleEndian;
  protected BufferManagerWrite bufferManagerWrite;
  ByteBufferWithInfo bbwi;
  protected com.sun.corba.se.spi.orb.ORB orb;
  protected ORBUtilSystemException wrapper;
  protected boolean debug = false;
  protected int blockSizeIndex = -1;
  protected int blockSizePosition = 0;
  protected byte streamFormatVersion;
  private static final int DEFAULT_BUFFER_SIZE = 1024;
  private static final String kWriteMethod = "write";
  private CacheTable codebaseCache = null;
  private CacheTable valueCache = null;
  private CacheTable repositoryIdCache = null;
  private int end_flag = 0;
  private int chunkedValueNestingLevel = 0;
  private boolean mustChunk = false;
  protected boolean inBlock = false;
  private int end_flag_position = 0;
  private int end_flag_index = 0;
  private ValueHandler valueHandler = null;
  private RepositoryIdUtility repIdUtil;
  private RepositoryIdStrings repIdStrs;
  private CodeSetConversion.CTBConverter charConverter;
  private CodeSetConversion.CTBConverter wcharConverter;
  private static final String _id = "IDL:omg.org/CORBA/DataOutputStream:1.0";
  private static final String[] _ids = { "IDL:omg.org/CORBA/DataOutputStream:1.0" };
  
  public CDROutputStream_1_0() {}
  
  public void init(org.omg.CORBA.ORB paramORB, boolean paramBoolean1, BufferManagerWrite paramBufferManagerWrite, byte paramByte, boolean paramBoolean2)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
    debug = orb.transportDebugFlag;
    littleEndian = paramBoolean1;
    bufferManagerWrite = paramBufferManagerWrite;
    bbwi = new ByteBufferWithInfo(paramORB, paramBufferManagerWrite, paramBoolean2);
    streamFormatVersion = paramByte;
    createRepositoryIdHandlers();
  }
  
  public void init(org.omg.CORBA.ORB paramORB, boolean paramBoolean, BufferManagerWrite paramBufferManagerWrite, byte paramByte)
  {
    init(paramORB, paramBoolean, paramBufferManagerWrite, paramByte, true);
  }
  
  private final void createRepositoryIdHandlers()
  {
    repIdUtil = RepositoryIdFactory.getRepIdUtility();
    repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
  }
  
  public BufferManagerWrite getBufferManager()
  {
    return bufferManagerWrite;
  }
  
  public byte[] toByteArray()
  {
    byte[] arrayOfByte = new byte[bbwi.position()];
    for (int i = 0; i < bbwi.position(); i++) {
      arrayOfByte[i] = bbwi.byteBuffer.get(i);
    }
    return arrayOfByte;
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_0;
  }
  
  void setHeaderPadding(boolean paramBoolean)
  {
    throw wrapper.giopVersionError();
  }
  
  protected void handleSpecialChunkBegin(int paramInt) {}
  
  protected void handleSpecialChunkEnd() {}
  
  protected final int computeAlignment(int paramInt)
  {
    if (paramInt > 1)
    {
      int i = bbwi.position() & paramInt - 1;
      if (i != 0) {
        return paramInt - i;
      }
    }
    return 0;
  }
  
  protected void alignAndReserve(int paramInt1, int paramInt2)
  {
    bbwi.position(bbwi.position() + computeAlignment(paramInt1));
    if (bbwi.position() + paramInt2 > bbwi.buflen) {
      grow(paramInt1, paramInt2);
    }
  }
  
  protected void grow(int paramInt1, int paramInt2)
  {
    bbwi.needed = paramInt2;
    bufferManagerWrite.overflow(bbwi);
  }
  
  public final void putEndian()
    throws SystemException
  {
    write_boolean(littleEndian);
  }
  
  public final boolean littleEndian()
  {
    return littleEndian;
  }
  
  void freeInternalCaches()
  {
    if (codebaseCache != null) {
      codebaseCache.done();
    }
    if (valueCache != null) {
      valueCache.done();
    }
    if (repositoryIdCache != null) {
      repositoryIdCache.done();
    }
  }
  
  public final void write_longdouble(double paramDouble)
  {
    throw wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void write_octet(byte paramByte)
  {
    alignAndReserve(1, 1);
    bbwi.byteBuffer.put(bbwi.position(), paramByte);
    bbwi.position(bbwi.position() + 1);
  }
  
  public final void write_boolean(boolean paramBoolean)
  {
    write_octet((byte)(paramBoolean ? 1 : 0));
  }
  
  public void write_char(char paramChar)
  {
    CodeSetConversion.CTBConverter localCTBConverter = getCharConverter();
    localCTBConverter.convert(paramChar);
    if (localCTBConverter.getNumBytes() > 1) {
      throw wrapper.invalidSingleCharCtb(CompletionStatus.COMPLETED_MAYBE);
    }
    write_octet(localCTBConverter.getBytes()[0]);
  }
  
  private final void writeLittleEndianWchar(char paramChar)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(paramChar & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(paramChar >>> '\b' & 0xFF));
    bbwi.position(bbwi.position() + 2);
  }
  
  private final void writeBigEndianWchar(char paramChar)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(paramChar >>> '\b' & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(paramChar & 0xFF));
    bbwi.position(bbwi.position() + 2);
  }
  
  private final void writeLittleEndianShort(short paramShort)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(paramShort & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(paramShort >>> 8 & 0xFF));
    bbwi.position(bbwi.position() + 2);
  }
  
  private final void writeBigEndianShort(short paramShort)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(paramShort >>> 8 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(paramShort & 0xFF));
    bbwi.position(bbwi.position() + 2);
  }
  
  private final void writeLittleEndianLong(int paramInt)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(paramInt & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(paramInt >>> 8 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 2, (byte)(paramInt >>> 16 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 3, (byte)(paramInt >>> 24 & 0xFF));
    bbwi.position(bbwi.position() + 4);
  }
  
  private final void writeBigEndianLong(int paramInt)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(paramInt >>> 24 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(paramInt >>> 16 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 2, (byte)(paramInt >>> 8 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 3, (byte)(paramInt & 0xFF));
    bbwi.position(bbwi.position() + 4);
  }
  
  private final void writeLittleEndianLongLong(long paramLong)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(int)(paramLong & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(int)(paramLong >>> 8 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 2, (byte)(int)(paramLong >>> 16 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 3, (byte)(int)(paramLong >>> 24 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 4, (byte)(int)(paramLong >>> 32 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 5, (byte)(int)(paramLong >>> 40 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 6, (byte)(int)(paramLong >>> 48 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 7, (byte)(int)(paramLong >>> 56 & 0xFF));
    bbwi.position(bbwi.position() + 8);
  }
  
  private final void writeBigEndianLongLong(long paramLong)
  {
    bbwi.byteBuffer.put(bbwi.position(), (byte)(int)(paramLong >>> 56 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 1, (byte)(int)(paramLong >>> 48 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 2, (byte)(int)(paramLong >>> 40 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 3, (byte)(int)(paramLong >>> 32 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 4, (byte)(int)(paramLong >>> 24 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 5, (byte)(int)(paramLong >>> 16 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 6, (byte)(int)(paramLong >>> 8 & 0xFF));
    bbwi.byteBuffer.put(bbwi.position() + 7, (byte)(int)(paramLong & 0xFF));
    bbwi.position(bbwi.position() + 8);
  }
  
  public void write_wchar(char paramChar)
  {
    if (ORBUtility.isForeignORB(orb)) {
      throw wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
    }
    alignAndReserve(2, 2);
    if (littleEndian) {
      writeLittleEndianWchar(paramChar);
    } else {
      writeBigEndianWchar(paramChar);
    }
  }
  
  public void write_short(short paramShort)
  {
    alignAndReserve(2, 2);
    if (littleEndian) {
      writeLittleEndianShort(paramShort);
    } else {
      writeBigEndianShort(paramShort);
    }
  }
  
  public final void write_ushort(short paramShort)
  {
    write_short(paramShort);
  }
  
  public void write_long(int paramInt)
  {
    alignAndReserve(4, 4);
    if (littleEndian) {
      writeLittleEndianLong(paramInt);
    } else {
      writeBigEndianLong(paramInt);
    }
  }
  
  public final void write_ulong(int paramInt)
  {
    write_long(paramInt);
  }
  
  public void write_longlong(long paramLong)
  {
    alignAndReserve(8, 8);
    if (littleEndian) {
      writeLittleEndianLongLong(paramLong);
    } else {
      writeBigEndianLongLong(paramLong);
    }
  }
  
  public final void write_ulonglong(long paramLong)
  {
    write_longlong(paramLong);
  }
  
  public final void write_float(float paramFloat)
  {
    write_long(Float.floatToIntBits(paramFloat));
  }
  
  public final void write_double(double paramDouble)
  {
    write_longlong(Double.doubleToLongBits(paramDouble));
  }
  
  public void write_string(String paramString)
  {
    writeString(paramString);
  }
  
  protected int writeString(String paramString)
  {
    if (paramString == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    CodeSetConversion.CTBConverter localCTBConverter = getCharConverter();
    localCTBConverter.convert(paramString);
    int i = localCTBConverter.getNumBytes() + 1;
    handleSpecialChunkBegin(computeAlignment(4) + 4 + i);
    write_long(i);
    int j = get_offset() - 4;
    internalWriteOctetArray(localCTBConverter.getBytes(), 0, localCTBConverter.getNumBytes());
    write_octet((byte)0);
    handleSpecialChunkEnd();
    return j;
  }
  
  public void write_wstring(String paramString)
  {
    if (paramString == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    if (ORBUtility.isForeignORB(orb)) {
      throw wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
    }
    int i = paramString.length() + 1;
    handleSpecialChunkBegin(4 + i * 2 + computeAlignment(4));
    write_long(i);
    for (int j = 0; j < i - 1; j++) {
      write_wchar(paramString.charAt(j));
    }
    write_short((short)0);
    handleSpecialChunkEnd();
  }
  
  void internalWriteOctetArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    int j = 1;
    while (i < paramInt2 + paramInt1)
    {
      if ((bbwi.position() + 1 > bbwi.buflen) || (j != 0))
      {
        j = 0;
        alignAndReserve(1, 1);
      }
      int k = bbwi.buflen - bbwi.position();
      int n = paramInt2 + paramInt1 - i;
      int m = n < k ? n : k;
      for (int i1 = 0; i1 < m; i1++) {
        bbwi.byteBuffer.put(bbwi.position() + i1, paramArrayOfByte[(i + i1)]);
      }
      bbwi.position(bbwi.position() + m);
      i += m;
    }
  }
  
  public final void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(paramInt2);
    internalWriteOctetArray(paramArrayOfByte, paramInt1, paramInt2);
    handleSpecialChunkEnd();
  }
  
  public void write_Principal(Principal paramPrincipal)
  {
    write_long(paramPrincipal.name().length);
    write_octet_array(paramPrincipal.name(), 0, paramPrincipal.name().length);
  }
  
  public void write_any(Any paramAny)
  {
    if (paramAny == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    write_TypeCode(paramAny.type());
    paramAny.write_value(parent);
  }
  
  public void write_TypeCode(TypeCode paramTypeCode)
  {
    if (paramTypeCode == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    TypeCodeImpl localTypeCodeImpl;
    if ((paramTypeCode instanceof TypeCodeImpl)) {
      localTypeCodeImpl = (TypeCodeImpl)paramTypeCode;
    } else {
      localTypeCodeImpl = new TypeCodeImpl(orb, paramTypeCode);
    }
    localTypeCodeImpl.write_value(parent);
  }
  
  public void write_Object(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null)
    {
      localIOR = IORFactories.makeIOR(orb);
      localIOR.write(parent);
      return;
    }
    if ((paramObject instanceof LocalObject)) {
      throw wrapper.writeLocalObject(CompletionStatus.COMPLETED_MAYBE);
    }
    IOR localIOR = ORBUtility.connectAndGetIOR(orb, paramObject);
    localIOR.write(parent);
  }
  
  public void write_abstract_interface(Object paramObject)
  {
    boolean bool = false;
    org.omg.CORBA.Object localObject = null;
    if ((paramObject != null) && ((paramObject instanceof org.omg.CORBA.Object)))
    {
      localObject = (org.omg.CORBA.Object)paramObject;
      bool = true;
    }
    write_boolean(bool);
    if (bool) {
      write_Object(localObject);
    } else {
      try
      {
        write_value((Serializable)paramObject);
      }
      catch (ClassCastException localClassCastException)
      {
        if ((paramObject instanceof Serializable)) {
          throw localClassCastException;
        }
        ORBUtility.throwNotSerializableForCorba(paramObject.getClass().getName());
      }
    }
  }
  
  public void write_value(Serializable paramSerializable, Class paramClass)
  {
    write_value(paramSerializable);
  }
  
  private void writeWStringValue(String paramString)
  {
    int i = writeValueTag(mustChunk, true, null);
    write_repositoryId(repIdStrs.getWStringValueRepId());
    updateIndirectionTable(i, paramString, paramString);
    if (mustChunk)
    {
      start_block();
      end_flag -= 1;
      chunkedValueNestingLevel -= 1;
    }
    else
    {
      end_flag -= 1;
    }
    write_wstring(paramString);
    if (mustChunk) {
      end_block();
    }
    writeEndTag(mustChunk);
  }
  
  private void writeArray(Serializable paramSerializable, Class paramClass)
  {
    if (valueHandler == null) {
      valueHandler = ORBUtility.createValueHandler();
    }
    int i = writeValueTag(mustChunk, true, Util.getCodebase(paramClass));
    write_repositoryId(repIdStrs.createSequenceRepID(paramClass));
    updateIndirectionTable(i, paramSerializable, paramSerializable);
    if (mustChunk)
    {
      start_block();
      end_flag -= 1;
      chunkedValueNestingLevel -= 1;
    }
    else
    {
      end_flag -= 1;
    }
    if ((valueHandler instanceof ValueHandlerMultiFormat))
    {
      ValueHandlerMultiFormat localValueHandlerMultiFormat = (ValueHandlerMultiFormat)valueHandler;
      localValueHandlerMultiFormat.writeValue(parent, paramSerializable, streamFormatVersion);
    }
    else
    {
      valueHandler.writeValue(parent, paramSerializable);
    }
    if (mustChunk) {
      end_block();
    }
    writeEndTag(mustChunk);
  }
  
  private void writeValueBase(ValueBase paramValueBase, Class paramClass)
  {
    mustChunk = true;
    int i = writeValueTag(true, true, Util.getCodebase(paramClass));
    String str = paramValueBase._truncatable_ids()[0];
    write_repositoryId(str);
    updateIndirectionTable(i, paramValueBase, paramValueBase);
    start_block();
    end_flag -= 1;
    chunkedValueNestingLevel -= 1;
    writeIDLValue(paramValueBase, str);
    end_block();
    writeEndTag(true);
  }
  
  private void writeRMIIIOPValueType(Serializable paramSerializable, Class paramClass)
  {
    if (valueHandler == null) {
      valueHandler = ORBUtility.createValueHandler();
    }
    Serializable localSerializable = paramSerializable;
    paramSerializable = valueHandler.writeReplace(localSerializable);
    if (paramSerializable == null)
    {
      write_long(0);
      return;
    }
    if (paramSerializable != localSerializable)
    {
      if ((valueCache != null) && (valueCache.containsKey(paramSerializable)))
      {
        writeIndirection(-1, valueCache.getVal(paramSerializable));
        return;
      }
      paramClass = paramSerializable.getClass();
    }
    if ((mustChunk) || (valueHandler.isCustomMarshaled(paramClass))) {
      mustChunk = true;
    }
    int i = writeValueTag(mustChunk, true, Util.getCodebase(paramClass));
    write_repositoryId(repIdStrs.createForJavaType(paramClass));
    updateIndirectionTable(i, paramSerializable, localSerializable);
    if (mustChunk)
    {
      end_flag -= 1;
      chunkedValueNestingLevel -= 1;
      start_block();
    }
    else
    {
      end_flag -= 1;
    }
    if ((valueHandler instanceof ValueHandlerMultiFormat))
    {
      ValueHandlerMultiFormat localValueHandlerMultiFormat = (ValueHandlerMultiFormat)valueHandler;
      localValueHandlerMultiFormat.writeValue(parent, paramSerializable, streamFormatVersion);
    }
    else
    {
      valueHandler.writeValue(parent, paramSerializable);
    }
    if (mustChunk) {
      end_block();
    }
    writeEndTag(mustChunk);
  }
  
  public void write_value(Serializable paramSerializable, String paramString)
  {
    if (paramSerializable == null)
    {
      write_long(0);
      return;
    }
    if ((valueCache != null) && (valueCache.containsKey(paramSerializable)))
    {
      writeIndirection(-1, valueCache.getVal(paramSerializable));
      return;
    }
    Class localClass = paramSerializable.getClass();
    boolean bool = mustChunk;
    if (mustChunk) {
      mustChunk = true;
    }
    if (inBlock) {
      end_block();
    }
    if (localClass.isArray()) {
      writeArray(paramSerializable, localClass);
    } else if ((paramSerializable instanceof ValueBase)) {
      writeValueBase((ValueBase)paramSerializable, localClass);
    } else if (shouldWriteAsIDLEntity(paramSerializable)) {
      writeIDLEntity((IDLEntity)paramSerializable);
    } else if ((paramSerializable instanceof String)) {
      writeWStringValue((String)paramSerializable);
    } else if ((paramSerializable instanceof Class)) {
      writeClass(paramString, (Class)paramSerializable);
    } else {
      writeRMIIIOPValueType(paramSerializable, localClass);
    }
    mustChunk = bool;
    if (mustChunk) {
      start_block();
    }
  }
  
  public void write_value(Serializable paramSerializable)
  {
    write_value(paramSerializable, (String)null);
  }
  
  public void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper)
  {
    if (paramSerializable == null)
    {
      write_long(0);
      return;
    }
    if ((valueCache != null) && (valueCache.containsKey(paramSerializable)))
    {
      writeIndirection(-1, valueCache.getVal(paramSerializable));
      return;
    }
    boolean bool = mustChunk;
    int i = 0;
    int j;
    if ((paramBoxedValueHelper instanceof ValueHelper))
    {
      try
      {
        j = ((ValueHelper)paramBoxedValueHelper).get_type().type_modifier();
      }
      catch (BadKind localBadKind)
      {
        j = 0;
      }
      if (((paramSerializable instanceof CustomMarshal)) && (j == 1))
      {
        i = 1;
        mustChunk = true;
      }
      if (j == 3) {
        mustChunk = true;
      }
    }
    if (mustChunk)
    {
      if (inBlock) {
        end_block();
      }
      j = writeValueTag(true, orb.getORBData().useRepId(), Util.getCodebase(paramSerializable.getClass()));
      if (orb.getORBData().useRepId()) {
        write_repositoryId(paramBoxedValueHelper.get_id());
      }
      updateIndirectionTable(j, paramSerializable, paramSerializable);
      start_block();
      end_flag -= 1;
      chunkedValueNestingLevel -= 1;
      if (i != 0) {
        ((CustomMarshal)paramSerializable).marshal(parent);
      } else {
        paramBoxedValueHelper.write_value(parent, paramSerializable);
      }
      end_block();
      writeEndTag(true);
    }
    else
    {
      j = writeValueTag(false, orb.getORBData().useRepId(), Util.getCodebase(paramSerializable.getClass()));
      if (orb.getORBData().useRepId()) {
        write_repositoryId(paramBoxedValueHelper.get_id());
      }
      updateIndirectionTable(j, paramSerializable, paramSerializable);
      end_flag -= 1;
      paramBoxedValueHelper.write_value(parent, paramSerializable);
      writeEndTag(false);
    }
    mustChunk = bool;
    if (mustChunk) {
      start_block();
    }
  }
  
  public int get_offset()
  {
    return bbwi.position();
  }
  
  public void start_block()
  {
    if (debug) {
      dprint("CDROutputStream_1_0 start_block, position" + bbwi.position());
    }
    write_long(0);
    inBlock = true;
    blockSizePosition = get_offset();
    blockSizeIndex = bbwi.position();
    if (debug) {
      dprint("CDROutputStream_1_0 start_block, blockSizeIndex " + blockSizeIndex);
    }
  }
  
  protected void writeLongWithoutAlign(int paramInt)
  {
    if (littleEndian) {
      writeLittleEndianLong(paramInt);
    } else {
      writeBigEndianLong(paramInt);
    }
  }
  
  public void end_block()
  {
    if (debug) {
      dprint("CDROutputStream_1_0.java end_block");
    }
    if (!inBlock) {
      return;
    }
    if (debug) {
      dprint("CDROutputStream_1_0.java end_block, in a block");
    }
    inBlock = false;
    if (get_offset() == blockSizePosition)
    {
      bbwi.position(bbwi.position() - 4);
      blockSizeIndex = -1;
      blockSizePosition = -1;
      return;
    }
    int i = bbwi.position();
    bbwi.position(blockSizeIndex - 4);
    writeLongWithoutAlign(i - blockSizeIndex);
    bbwi.position(i);
    blockSizeIndex = -1;
    blockSizePosition = -1;
  }
  
  public org.omg.CORBA.ORB orb()
  {
    return orb;
  }
  
  public final void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    if (paramArrayOfBoolean == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(paramInt2);
    for (int i = 0; i < paramInt2; i++) {
      write_boolean(paramArrayOfBoolean[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public final void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(paramInt2);
    for (int i = 0; i < paramInt2; i++) {
      write_char(paramArrayOfChar[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(computeAlignment(2) + paramInt2 * 2);
    for (int i = 0; i < paramInt2; i++) {
      write_wchar(paramArrayOfChar[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public final void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    if (paramArrayOfShort == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(computeAlignment(2) + paramInt2 * 2);
    for (int i = 0; i < paramInt2; i++) {
      write_short(paramArrayOfShort[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public final void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    write_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public final void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramArrayOfInt == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(computeAlignment(4) + paramInt2 * 4);
    for (int i = 0; i < paramInt2; i++) {
      write_long(paramArrayOfInt[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public final void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    write_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    if (paramArrayOfLong == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(computeAlignment(8) + paramInt2 * 8);
    for (int i = 0; i < paramInt2; i++) {
      write_longlong(paramArrayOfLong[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public final void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    write_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public final void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramArrayOfFloat == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(computeAlignment(4) + paramInt2 * 4);
    for (int i = 0; i < paramInt2; i++) {
      write_float(paramArrayOfFloat[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public final void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    if (paramArrayOfDouble == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    handleSpecialChunkBegin(computeAlignment(8) + paramInt2 * 8);
    for (int i = 0; i < paramInt2; i++) {
      write_double(paramArrayOfDouble[(paramInt1 + i)]);
    }
    handleSpecialChunkEnd();
  }
  
  public void write_string_array(String[] paramArrayOfString, int paramInt1, int paramInt2)
  {
    if (paramArrayOfString == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    for (int i = 0; i < paramInt2; i++) {
      write_string(paramArrayOfString[(paramInt1 + i)]);
    }
  }
  
  public void write_wstring_array(String[] paramArrayOfString, int paramInt1, int paramInt2)
  {
    if (paramArrayOfString == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    for (int i = 0; i < paramInt2; i++) {
      write_wstring(paramArrayOfString[(paramInt1 + i)]);
    }
  }
  
  public final void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_any(paramArrayOfAny[(paramInt1 + i)]);
    }
  }
  
  public void writeTo(java.io.OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = null;
    if (bbwi.byteBuffer.hasArray())
    {
      arrayOfByte = bbwi.byteBuffer.array();
    }
    else
    {
      int i = bbwi.position();
      arrayOfByte = new byte[i];
      for (int j = 0; j < i; j++) {
        arrayOfByte[j] = bbwi.byteBuffer.get(j);
      }
    }
    paramOutputStream.write(arrayOfByte, 0, bbwi.position());
  }
  
  public void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    byte[] arrayOfByte = null;
    if (bbwi.byteBuffer.hasArray())
    {
      arrayOfByte = bbwi.byteBuffer.array();
    }
    else
    {
      int i = bbwi.position();
      arrayOfByte = new byte[i];
      for (int j = 0; j < i; j++) {
        arrayOfByte[j] = bbwi.byteBuffer.get(j);
      }
    }
    paramOutputStream.write_long(bbwi.position());
    paramOutputStream.write_octet_array(arrayOfByte, 0, bbwi.position());
  }
  
  public final int getSize()
  {
    return bbwi.position();
  }
  
  public int getIndex()
  {
    return bbwi.position();
  }
  
  public boolean isLittleEndian()
  {
    return littleEndian;
  }
  
  public void setIndex(int paramInt)
  {
    bbwi.position(paramInt);
  }
  
  public ByteBufferWithInfo getByteBufferWithInfo()
  {
    return bbwi;
  }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    bbwi = paramByteBufferWithInfo;
  }
  
  public ByteBuffer getByteBuffer()
  {
    ByteBuffer localByteBuffer = null;
    if (bbwi != null) {
      localByteBuffer = bbwi.byteBuffer;
    }
    return localByteBuffer;
  }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer)
  {
    bbwi.byteBuffer = paramByteBuffer;
  }
  
  private final void updateIndirectionTable(int paramInt, Object paramObject1, Object paramObject2)
  {
    if (valueCache == null) {
      valueCache = new CacheTable(orb, true);
    }
    valueCache.put(paramObject1, paramInt);
    if (paramObject2 != paramObject1) {
      valueCache.put(paramObject2, paramInt);
    }
  }
  
  private final void write_repositoryId(String paramString)
  {
    if ((repositoryIdCache != null) && (repositoryIdCache.containsKey(paramString)))
    {
      writeIndirection(-1, repositoryIdCache.getVal(paramString));
      return;
    }
    int i = writeString(paramString);
    if (repositoryIdCache == null) {
      repositoryIdCache = new CacheTable(orb, true);
    }
    repositoryIdCache.put(paramString, i);
  }
  
  private void write_codebase(String paramString, int paramInt)
  {
    if ((codebaseCache != null) && (codebaseCache.containsKey(paramString)))
    {
      writeIndirection(-1, codebaseCache.getVal(paramString));
    }
    else
    {
      write_string(paramString);
      if (codebaseCache == null) {
        codebaseCache = new CacheTable(orb, true);
      }
      codebaseCache.put(paramString, paramInt);
    }
  }
  
  private final int writeValueTag(boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    int i = 0;
    if ((paramBoolean1) && (!paramBoolean2))
    {
      if (paramString == null)
      {
        write_long(repIdUtil.getStandardRMIChunkedNoRepStrId());
        i = get_offset() - 4;
      }
      else
      {
        write_long(repIdUtil.getCodeBaseRMIChunkedNoRepStrId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      }
    }
    else if ((paramBoolean1) && (paramBoolean2))
    {
      if (paramString == null)
      {
        write_long(repIdUtil.getStandardRMIChunkedId());
        i = get_offset() - 4;
      }
      else
      {
        write_long(repIdUtil.getCodeBaseRMIChunkedId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      }
    }
    else if ((!paramBoolean1) && (!paramBoolean2))
    {
      if (paramString == null)
      {
        write_long(repIdUtil.getStandardRMIUnchunkedNoRepStrId());
        i = get_offset() - 4;
      }
      else
      {
        write_long(repIdUtil.getCodeBaseRMIUnchunkedNoRepStrId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      }
    }
    else if ((!paramBoolean1) && (paramBoolean2)) {
      if (paramString == null)
      {
        write_long(repIdUtil.getStandardRMIUnchunkedId());
        i = get_offset() - 4;
      }
      else
      {
        write_long(repIdUtil.getCodeBaseRMIUnchunkedId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      }
    }
    return i;
  }
  
  private void writeIDLValue(Serializable paramSerializable, String paramString)
  {
    if ((paramSerializable instanceof StreamableValue))
    {
      ((StreamableValue)paramSerializable)._write(parent);
    }
    else if ((paramSerializable instanceof CustomValue))
    {
      ((CustomValue)paramSerializable).marshal(parent);
    }
    else
    {
      BoxedValueHelper localBoxedValueHelper = Utility.getHelper(paramSerializable.getClass(), null, paramString);
      int i = 0;
      if (((localBoxedValueHelper instanceof ValueHelper)) && ((paramSerializable instanceof CustomMarshal))) {
        try
        {
          if (((ValueHelper)localBoxedValueHelper).get_type().type_modifier() == 1) {
            i = 1;
          }
        }
        catch (BadKind localBadKind)
        {
          throw wrapper.badTypecodeForCustomValue(CompletionStatus.COMPLETED_MAYBE, localBadKind);
        }
      }
      if (i != 0) {
        ((CustomMarshal)paramSerializable).marshal(parent);
      } else {
        localBoxedValueHelper.write_value(parent, paramSerializable);
      }
    }
  }
  
  private void writeEndTag(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if ((get_offset() == end_flag_position) && (bbwi.position() == end_flag_index)) {
        bbwi.position(bbwi.position() - 4);
      }
      writeNestingLevel();
      end_flag_index = bbwi.position();
      end_flag_position = get_offset();
      chunkedValueNestingLevel += 1;
    }
    end_flag += 1;
  }
  
  private void writeNestingLevel()
  {
    if ((orb == null) || (ORBVersionFactory.getFOREIGN().equals(orb.getORBVersion())) || (ORBVersionFactory.getNEWER().compareTo(orb.getORBVersion()) <= 0)) {
      write_long(chunkedValueNestingLevel);
    } else {
      write_long(end_flag);
    }
  }
  
  private void writeClass(String paramString, Class paramClass)
  {
    if (paramString == null) {
      paramString = repIdStrs.getClassDescValueRepId();
    }
    int i = writeValueTag(mustChunk, true, null);
    updateIndirectionTable(i, paramClass, paramClass);
    write_repositoryId(paramString);
    if (mustChunk)
    {
      start_block();
      end_flag -= 1;
      chunkedValueNestingLevel -= 1;
    }
    else
    {
      end_flag -= 1;
    }
    writeClassBody(paramClass);
    if (mustChunk) {
      end_block();
    }
    writeEndTag(mustChunk);
  }
  
  private void writeClassBody(Class paramClass)
  {
    if ((orb == null) || (ORBVersionFactory.getFOREIGN().equals(orb.getORBVersion())) || (ORBVersionFactory.getNEWER().compareTo(orb.getORBVersion()) <= 0))
    {
      write_value(Util.getCodebase(paramClass));
      write_value(repIdStrs.createForAnyType(paramClass));
    }
    else
    {
      write_value(repIdStrs.createForAnyType(paramClass));
      write_value(Util.getCodebase(paramClass));
    }
  }
  
  private boolean shouldWriteAsIDLEntity(Serializable paramSerializable)
  {
    return ((paramSerializable instanceof IDLEntity)) && (!(paramSerializable instanceof ValueBase)) && (!(paramSerializable instanceof org.omg.CORBA.Object));
  }
  
  private void writeIDLEntity(IDLEntity paramIDLEntity)
  {
    mustChunk = true;
    String str1 = repIdStrs.createForJavaType(paramIDLEntity);
    Class localClass1 = paramIDLEntity.getClass();
    String str2 = Util.getCodebase(localClass1);
    int i = writeValueTag(true, true, str2);
    updateIndirectionTable(i, paramIDLEntity, paramIDLEntity);
    write_repositoryId(str1);
    end_flag -= 1;
    chunkedValueNestingLevel -= 1;
    start_block();
    try
    {
      ClassLoader localClassLoader = localClass1 == null ? null : localClass1.getClassLoader();
      final Class localClass2 = Utility.loadClassForClass(localClass1.getName() + "Helper", str2, localClassLoader, localClass1, localClassLoader);
      final Class[] arrayOfClass = { org.omg.CORBA.portable.OutputStream.class, localClass1 };
      Method localMethod = null;
      try
      {
        localMethod = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws NoSuchMethodException
          {
            return localClass2.getDeclaredMethod("write", arrayOfClass);
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((NoSuchMethodException)localPrivilegedActionException.getException());
      }
      Object[] arrayOfObject = { parent, paramIDLEntity };
      localMethod.invoke(null, arrayOfObject);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, localNoSuchMethodException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, localInvocationTargetException);
    }
    end_block();
    writeEndTag(true);
  }
  
  public void write_Abstract(Object paramObject)
  {
    write_abstract_interface(paramObject);
  }
  
  public void write_Value(Serializable paramSerializable)
  {
    write_value(paramSerializable);
  }
  
  public void write_fixed(BigDecimal paramBigDecimal, short paramShort1, short paramShort2)
  {
    String str1 = paramBigDecimal.toString();
    if ((str1.charAt(0) == '-') || (str1.charAt(0) == '+')) {
      str1 = str1.substring(1);
    }
    int i = str1.indexOf('.');
    String str2;
    String str3;
    if (i == -1)
    {
      str2 = str1;
      str3 = null;
    }
    else if (i == 0)
    {
      str2 = null;
      str3 = str1;
    }
    else
    {
      str2 = str1.substring(0, i);
      str3 = str1.substring(i + 1);
    }
    StringBuffer localStringBuffer = new StringBuffer(paramShort1);
    if (str3 != null) {
      localStringBuffer.append(str3);
    }
    while (localStringBuffer.length() < paramShort2) {
      localStringBuffer.append('0');
    }
    if (str2 != null) {
      localStringBuffer.insert(0, str2);
    }
    while (localStringBuffer.length() < paramShort1) {
      localStringBuffer.insert(0, '0');
    }
    write_fixed(localStringBuffer.toString(), paramBigDecimal.signum());
  }
  
  public void write_fixed(BigDecimal paramBigDecimal)
  {
    write_fixed(paramBigDecimal.toString(), paramBigDecimal.signum());
  }
  
  public void write_fixed(String paramString, int paramInt)
  {
    int i = paramString.length();
    int j = 0;
    int m = 0;
    char c;
    for (int n = 0; n < i; n++)
    {
      c = paramString.charAt(n);
      if ((c != '-') && (c != '+') && (c != '.')) {
        m++;
      }
    }
    byte b;
    for (n = 0; n < i; n++)
    {
      c = paramString.charAt(n);
      if ((c != '-') && (c != '+') && (c != '.'))
      {
        int k = (byte)Character.digit(c, 10);
        if (k == -1) {
          throw wrapper.badDigitInFixed(CompletionStatus.COMPLETED_MAYBE);
        }
        if (m % 2 == 0)
        {
          j = (byte)(j | k);
          write_octet(j);
          j = 0;
        }
        else
        {
          b = (byte)(j | k << 4);
        }
        m--;
      }
    }
    if (paramInt == -1) {
      b = (byte)(b | 0xD);
    } else {
      b = (byte)(b | 0xC);
    }
    write_octet(b);
  }
  
  public String[] _truncatable_ids()
  {
    if (_ids == null) {
      return null;
    }
    return (String[])_ids.clone();
  }
  
  public void printBuffer()
  {
    printBuffer(bbwi);
  }
  
  public static void printBuffer(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    System.out.println("+++++++ Output Buffer ++++++++");
    System.out.println();
    System.out.println("Current position: " + paramByteBufferWithInfo.position());
    System.out.println("Total length : " + buflen);
    System.out.println();
    char[] arrayOfChar = new char[16];
    try
    {
      for (int i = 0; i < paramByteBufferWithInfo.position(); i += 16)
      {
        for (int j = 0; (j < 16) && (j + i < paramByteBufferWithInfo.position()); j++)
        {
          k = byteBuffer.get(i + j);
          if (k < 0) {
            k = 256 + k;
          }
          String str = Integer.toHexString(k);
          if (str.length() == 1) {
            str = "0" + str;
          }
          System.out.print(str + " ");
        }
        while (j < 16)
        {
          System.out.print("   ");
          j++;
        }
        for (int k = 0; (k < 16) && (k + i < paramByteBufferWithInfo.position()); k++) {
          if (ORBUtility.isPrintable((char)byteBuffer.get(i + k))) {
            arrayOfChar[k] = ((char)byteBuffer.get(i + k));
          } else {
            arrayOfChar[k] = '.';
          }
        }
        System.out.println(new String(arrayOfChar, 0, k));
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    System.out.println("++++++++++++++++++++++++++++++");
  }
  
  public void writeIndirection(int paramInt1, int paramInt2)
  {
    handleSpecialChunkBegin(computeAlignment(4) + 8);
    write_long(paramInt1);
    write_long(paramInt2 - parent.getRealIndex(get_offset()));
    handleSpecialChunkEnd();
  }
  
  protected CodeSetConversion.CTBConverter getCharConverter()
  {
    if (charConverter == null) {
      charConverter = parent.createCharCTBConverter();
    }
    return charConverter;
  }
  
  protected CodeSetConversion.CTBConverter getWCharConverter()
  {
    if (wcharConverter == null) {
      wcharConverter = parent.createWCharCTBConverter();
    }
    return wcharConverter;
  }
  
  protected void dprint(String paramString)
  {
    if (debug) {
      ORBUtility.dprint(this, paramString);
    }
  }
  
  void alignOnBoundary(int paramInt)
  {
    alignAndReserve(paramInt, 0);
  }
  
  public void start_value(String paramString)
  {
    if (debug) {
      dprint("start_value w/ rep id " + paramString + " called at pos " + get_offset() + " position " + bbwi.position());
    }
    if (inBlock) {
      end_block();
    }
    writeValueTag(true, true, null);
    write_repositoryId(paramString);
    end_flag -= 1;
    chunkedValueNestingLevel -= 1;
    start_block();
  }
  
  public void end_value()
  {
    if (debug) {
      dprint("end_value called at pos " + get_offset() + " position " + bbwi.position());
    }
    end_block();
    writeEndTag(true);
    if (debug) {
      dprint("mustChunk is " + mustChunk);
    }
    if (mustChunk) {
      start_block();
    }
  }
  
  public void close()
    throws IOException
  {
    getBufferManager().close();
    if ((getByteBufferWithInfo() != null) && (getByteBuffer() != null))
    {
      MessageMediator localMessageMediator = parent.getMessageMediator();
      if (localMessageMediator != null)
      {
        localObject = (CDRInputObject)localMessageMediator.getInputObject();
        if ((localObject != null) && (((CDRInputObject)localObject).isSharing(getByteBuffer())))
        {
          ((CDRInputObject)localObject).setByteBuffer(null);
          ((CDRInputObject)localObject).setByteBufferWithInfo(null);
        }
      }
      Object localObject = orb.getByteBufferPool();
      if (debug)
      {
        int i = System.identityHashCode(bbwi.byteBuffer);
        StringBuffer localStringBuffer = new StringBuffer(80);
        localStringBuffer.append(".close - releasing ByteBuffer id (");
        localStringBuffer.append(i).append(") to ByteBufferPool.");
        String str = localStringBuffer.toString();
        dprint(str);
      }
      ((ByteBufferPool)localObject).releaseByteBuffer(getByteBuffer());
      bbwi.byteBuffer = null;
      bbwi = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStream_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */