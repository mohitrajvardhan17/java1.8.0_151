package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.corba.PrincipalImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.CacheTable;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdInterface;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.RepositoryIdCache;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueBase;
import org.omg.CORBA.portable.ValueFactory;

public class CDRInputStream_1_0
  extends CDRInputStreamBase
  implements RestorableInputStream
{
  private static final String kReadMethod = "read";
  private static final int maxBlockLength = 2147483392;
  protected BufferManagerRead bufferManagerRead;
  protected ByteBufferWithInfo bbwi;
  private boolean debug = false;
  protected boolean littleEndian;
  protected com.sun.corba.se.spi.orb.ORB orb;
  protected ORBUtilSystemException wrapper;
  protected OMGSystemException omgWrapper;
  protected ValueHandler valueHandler = null;
  private CacheTable valueCache = null;
  private CacheTable repositoryIdCache = null;
  private CacheTable codebaseCache = null;
  protected int blockLength = 2147483392;
  protected int end_flag = 0;
  private int chunkedValueNestingLevel = 0;
  protected int valueIndirection = 0;
  protected int stringIndirection = 0;
  protected boolean isChunked = false;
  private RepositoryIdUtility repIdUtil;
  private RepositoryIdStrings repIdStrs;
  private CodeSetConversion.BTCConverter charConverter;
  private CodeSetConversion.BTCConverter wcharConverter;
  private boolean specialNoOptionalDataState = false;
  private static final String _id = "IDL:omg.org/CORBA/DataInputStream:1.0";
  private static final String[] _ids = { "IDL:omg.org/CORBA/DataInputStream:1.0" };
  protected MarkAndResetHandler markAndResetHandler = null;
  
  public CDRInputStream_1_0() {}
  
  public CDRInputStreamBase dup()
  {
    CDRInputStreamBase localCDRInputStreamBase = null;
    try
    {
      localCDRInputStreamBase = (CDRInputStreamBase)getClass().newInstance();
    }
    catch (Exception localException)
    {
      throw wrapper.couldNotDuplicateCdrInputStream(localException);
    }
    localCDRInputStreamBase.init(orb, bbwi.byteBuffer, bbwi.buflen, littleEndian, bufferManagerRead);
    bbwi.position(bbwi.position());
    bbwi.byteBuffer.limit(bbwi.buflen);
    return localCDRInputStreamBase;
  }
  
  public void init(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, BufferManagerRead paramBufferManagerRead)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.encoding");
    omgWrapper = OMGSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.encoding");
    littleEndian = paramBoolean;
    bufferManagerRead = paramBufferManagerRead;
    bbwi = new ByteBufferWithInfo(paramORB, paramByteBuffer, 0);
    bbwi.buflen = paramInt;
    bbwi.byteBuffer.limit(bbwi.buflen);
    markAndResetHandler = bufferManagerRead.getMarkAndResetHandler();
    debug = transportDebugFlag;
  }
  
  void performORBVersionSpecificInit()
  {
    createRepositoryIdHandlers();
  }
  
  private final void createRepositoryIdHandlers()
  {
    repIdUtil = RepositoryIdFactory.getRepIdUtility();
    repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_0;
  }
  
  void setHeaderPadding(boolean paramBoolean)
  {
    throw wrapper.giopVersionError();
  }
  
  protected final int computeAlignment(int paramInt1, int paramInt2)
  {
    if (paramInt2 > 1)
    {
      int i = paramInt1 & paramInt2 - 1;
      if (i != 0) {
        return paramInt2 - i;
      }
    }
    return 0;
  }
  
  public int getSize()
  {
    return bbwi.position();
  }
  
  protected void checkBlockLength(int paramInt1, int paramInt2)
  {
    if (!isChunked) {
      return;
    }
    if (specialNoOptionalDataState) {
      throw omgWrapper.rmiiiopOptionalDataIncompatible1();
    }
    int i = 0;
    if (blockLength == get_offset())
    {
      blockLength = 2147483392;
      start_block();
      if (blockLength == 2147483392) {
        i = 1;
      }
    }
    else if (blockLength < get_offset())
    {
      throw wrapper.chunkOverflow();
    }
    int j = computeAlignment(bbwi.position(), paramInt1) + paramInt2;
    if ((blockLength != 2147483392) && (blockLength < get_offset() + j)) {
      throw omgWrapper.rmiiiopOptionalDataIncompatible2();
    }
    if (i != 0)
    {
      int k = read_long();
      bbwi.position(bbwi.position() - 4);
      if (k < 0) {
        throw omgWrapper.rmiiiopOptionalDataIncompatible3();
      }
    }
  }
  
  protected void alignAndCheck(int paramInt1, int paramInt2)
  {
    checkBlockLength(paramInt1, paramInt2);
    int i = computeAlignment(bbwi.position(), paramInt1);
    bbwi.position(bbwi.position() + i);
    if (bbwi.position() + paramInt2 > bbwi.buflen) {
      grow(paramInt1, paramInt2);
    }
  }
  
  protected void grow(int paramInt1, int paramInt2)
  {
    bbwi.needed = paramInt2;
    bbwi = bufferManagerRead.underflow(bbwi);
  }
  
  public final void consumeEndian()
  {
    littleEndian = read_boolean();
  }
  
  public final double read_longdouble()
  {
    throw wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public final boolean read_boolean()
  {
    return read_octet() != 0;
  }
  
  public final char read_char()
  {
    alignAndCheck(1, 1);
    return getConvertedChars(1, getCharConverter())[0];
  }
  
  public char read_wchar()
  {
    if (ORBUtility.isForeignORB(orb)) {
      throw wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
    }
    alignAndCheck(2, 2);
    int j;
    int i;
    if (littleEndian)
    {
      j = bbwi.byteBuffer.get(bbwi.position()) & 0xFF;
      bbwi.position(bbwi.position() + 1);
      i = bbwi.byteBuffer.get(bbwi.position()) & 0xFF;
      bbwi.position(bbwi.position() + 1);
    }
    else
    {
      i = bbwi.byteBuffer.get(bbwi.position()) & 0xFF;
      bbwi.position(bbwi.position() + 1);
      j = bbwi.byteBuffer.get(bbwi.position()) & 0xFF;
      bbwi.position(bbwi.position() + 1);
    }
    return (char)((i << 8) + (j << 0));
  }
  
  public final byte read_octet()
  {
    alignAndCheck(1, 1);
    byte b = bbwi.byteBuffer.get(bbwi.position());
    bbwi.position(bbwi.position() + 1);
    return b;
  }
  
  public final short read_short()
  {
    alignAndCheck(2, 2);
    int j;
    int i;
    if (littleEndian)
    {
      j = bbwi.byteBuffer.get(bbwi.position()) << 0 & 0xFF;
      bbwi.position(bbwi.position() + 1);
      i = bbwi.byteBuffer.get(bbwi.position()) << 8 & 0xFF00;
      bbwi.position(bbwi.position() + 1);
    }
    else
    {
      i = bbwi.byteBuffer.get(bbwi.position()) << 8 & 0xFF00;
      bbwi.position(bbwi.position() + 1);
      j = bbwi.byteBuffer.get(bbwi.position()) << 0 & 0xFF;
      bbwi.position(bbwi.position() + 1);
    }
    return (short)(i | j);
  }
  
  public final short read_ushort()
  {
    return read_short();
  }
  
  public final int read_long()
  {
    alignAndCheck(4, 4);
    int n = bbwi.position();
    int m;
    int k;
    int j;
    int i;
    if (littleEndian)
    {
      m = bbwi.byteBuffer.get(n++) & 0xFF;
      k = bbwi.byteBuffer.get(n++) & 0xFF;
      j = bbwi.byteBuffer.get(n++) & 0xFF;
      i = bbwi.byteBuffer.get(n++) & 0xFF;
    }
    else
    {
      i = bbwi.byteBuffer.get(n++) & 0xFF;
      j = bbwi.byteBuffer.get(n++) & 0xFF;
      k = bbwi.byteBuffer.get(n++) & 0xFF;
      m = bbwi.byteBuffer.get(n++) & 0xFF;
    }
    bbwi.position(n);
    return i << 24 | j << 16 | k << 8 | m;
  }
  
  public final int read_ulong()
  {
    return read_long();
  }
  
  public final long read_longlong()
  {
    alignAndCheck(8, 8);
    long l2;
    long l1;
    if (littleEndian)
    {
      l2 = read_long() & 0xFFFFFFFF;
      l1 = read_long() << 32;
    }
    else
    {
      l1 = read_long() << 32;
      l2 = read_long() & 0xFFFFFFFF;
    }
    return l1 | l2;
  }
  
  public final long read_ulonglong()
  {
    return read_longlong();
  }
  
  public final float read_float()
  {
    return Float.intBitsToFloat(read_long());
  }
  
  public final double read_double()
  {
    return Double.longBitsToDouble(read_longlong());
  }
  
  protected final void checkForNegativeLength(int paramInt)
  {
    if (paramInt < 0) {
      throw wrapper.negativeStringLength(CompletionStatus.COMPLETED_MAYBE, new Integer(paramInt));
    }
  }
  
  protected final String readStringOrIndirection(boolean paramBoolean)
  {
    int i = read_long();
    if (paramBoolean)
    {
      if (i == -1) {
        return null;
      }
      stringIndirection = (get_offset() - 4);
    }
    checkForNegativeLength(i);
    return internalReadString(i);
  }
  
  private final String internalReadString(int paramInt)
  {
    if (paramInt == 0) {
      return new String("");
    }
    char[] arrayOfChar = getConvertedChars(paramInt - 1, getCharConverter());
    read_octet();
    return new String(arrayOfChar, 0, getCharConverter().getNumChars());
  }
  
  public final String read_string()
  {
    return readStringOrIndirection(false);
  }
  
  public String read_wstring()
  {
    if (ORBUtility.isForeignORB(orb)) {
      throw wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
    }
    int i = read_long();
    if (i == 0) {
      return new String("");
    }
    checkForNegativeLength(i);
    i--;
    char[] arrayOfChar = new char[i];
    for (int j = 0; j < i; j++) {
      arrayOfChar[j] = read_wchar();
    }
    read_wchar();
    return new String(arrayOfChar);
  }
  
  public final void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw wrapper.nullParam();
    }
    if (paramInt2 == 0) {
      return;
    }
    alignAndCheck(1, 1);
    int i = paramInt1;
    while (i < paramInt2 + paramInt1)
    {
      int j = bbwi.buflen - bbwi.position();
      if (j <= 0)
      {
        grow(1, 1);
        j = bbwi.buflen - bbwi.position();
      }
      int m = paramInt2 + paramInt1 - i;
      int k = m < j ? m : j;
      for (int n = 0; n < k; n++) {
        paramArrayOfByte[(i + n)] = bbwi.byteBuffer.get(bbwi.position() + n);
      }
      bbwi.position(bbwi.position() + k);
      i += k;
    }
  }
  
  public Principal read_Principal()
  {
    int i = read_long();
    byte[] arrayOfByte = new byte[i];
    read_octet_array(arrayOfByte, 0, i);
    PrincipalImpl localPrincipalImpl = new PrincipalImpl();
    localPrincipalImpl.name(arrayOfByte);
    return localPrincipalImpl;
  }
  
  public TypeCode read_TypeCode()
  {
    TypeCodeImpl localTypeCodeImpl = new TypeCodeImpl(orb);
    localTypeCodeImpl.read_value(parent);
    return localTypeCodeImpl;
  }
  
  public Any read_any()
  {
    Any localAny = orb.create_any();
    TypeCodeImpl localTypeCodeImpl = new TypeCodeImpl(orb);
    try
    {
      localTypeCodeImpl.read_value(parent);
    }
    catch (MARSHAL localMARSHAL)
    {
      if (localTypeCodeImpl.kind().value() != 29) {
        throw localMARSHAL;
      }
      dprintThrowable(localMARSHAL);
    }
    localAny.read_value(parent, localTypeCodeImpl);
    return localAny;
  }
  
  public org.omg.CORBA.Object read_Object()
  {
    return read_Object(null);
  }
  
  public org.omg.CORBA.Object read_Object(Class paramClass)
  {
    IOR localIOR = IORFactories.makeIOR(parent);
    if (localIOR.isNil()) {
      return null;
    }
    PresentationManager.StubFactoryFactory localStubFactoryFactory = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
    String str1 = localIOR.getProfile().getCodebase();
    PresentationManager.StubFactory localStubFactory = null;
    if (paramClass == null)
    {
      RepositoryId localRepositoryId = RepositoryId.cache.getId(localIOR.getTypeId());
      String str2 = localRepositoryId.getClassName();
      boolean bool2 = localRepositoryId.isIDLType();
      if ((str2 == null) || (str2.equals(""))) {
        localStubFactory = null;
      } else {
        try
        {
          localStubFactory = localStubFactoryFactory.createStubFactory(str2, bool2, str1, (Class)null, (ClassLoader)null);
        }
        catch (Exception localException)
        {
          localStubFactory = null;
        }
      }
    }
    else if (StubAdapter.isStubClass(paramClass))
    {
      localStubFactory = PresentationDefaults.makeStaticStubFactory(paramClass);
    }
    else
    {
      boolean bool1 = IDLEntity.class.isAssignableFrom(paramClass);
      localStubFactory = localStubFactoryFactory.createStubFactory(paramClass.getName(), bool1, str1, paramClass, paramClass.getClassLoader());
    }
    return internalIORToObject(localIOR, localStubFactory, orb);
  }
  
  public static org.omg.CORBA.Object internalIORToObject(IOR paramIOR, PresentationManager.StubFactory paramStubFactory, com.sun.corba.se.spi.orb.ORB paramORB)
  {
    ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    Object localObject1 = paramIOR.getProfile().getServant();
    if (localObject1 != null)
    {
      if ((localObject1 instanceof Tie))
      {
        localObject2 = paramIOR.getProfile().getCodebase();
        localObject3 = (org.omg.CORBA.Object)Utility.loadStub((Tie)localObject1, paramStubFactory, (String)localObject2, false);
        if (localObject3 != null) {
          return (org.omg.CORBA.Object)localObject3;
        }
        throw localORBUtilSystemException.readObjectException();
      }
      if ((localObject1 instanceof org.omg.CORBA.Object))
      {
        if (!(localObject1 instanceof InvokeHandler)) {
          return (org.omg.CORBA.Object)localObject1;
        }
      }
      else {
        throw localORBUtilSystemException.badServantReadObject();
      }
    }
    Object localObject2 = ORBUtility.makeClientDelegate(paramIOR);
    Object localObject3 = null;
    try
    {
      localObject3 = paramStubFactory.makeStub();
    }
    catch (Throwable localThrowable)
    {
      localORBUtilSystemException.stubCreateError(localThrowable);
      if ((localThrowable instanceof ThreadDeath)) {
        throw ((ThreadDeath)localThrowable);
      }
      localObject3 = new CORBAObjectImpl();
    }
    StubAdapter.setDelegate(localObject3, (Delegate)localObject2);
    return (org.omg.CORBA.Object)localObject3;
  }
  
  public Object read_abstract_interface()
  {
    return read_abstract_interface(null);
  }
  
  public Object read_abstract_interface(Class paramClass)
  {
    boolean bool = read_boolean();
    if (bool) {
      return read_Object(paramClass);
    }
    return read_value();
  }
  
  public Serializable read_value()
  {
    return read_value((Class)null);
  }
  
  private Serializable handleIndirection()
  {
    int i = read_long() + get_offset() - 4;
    if ((valueCache != null) && (valueCache.containsVal(i)))
    {
      Serializable localSerializable = (Serializable)valueCache.getKey(i);
      return localSerializable;
    }
    throw new IndirectionException(i);
  }
  
  private String readRepositoryIds(int paramInt, Class paramClass, String paramString)
  {
    return readRepositoryIds(paramInt, paramClass, paramString, null);
  }
  
  private String readRepositoryIds(int paramInt, Class paramClass, String paramString, BoxedValueHelper paramBoxedValueHelper)
  {
    switch (repIdUtil.getTypeInfo(paramInt))
    {
    case 0: 
      if (paramClass == null)
      {
        if (paramString != null) {
          return paramString;
        }
        if (paramBoxedValueHelper != null) {
          return paramBoxedValueHelper.get_id();
        }
        throw wrapper.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_MAYBE);
      }
      return repIdStrs.createForAnyType(paramClass);
    case 2: 
      return read_repositoryId();
    case 6: 
      return read_repositoryIds();
    }
    throw wrapper.badValueTag(CompletionStatus.COMPLETED_MAYBE, Integer.toHexString(paramInt));
  }
  
  public Serializable read_value(Class paramClass)
  {
    int i = readValueTag();
    if (i == 0) {
      return null;
    }
    if (i == -1) {
      return handleIndirection();
    }
    int j = get_offset() - 4;
    boolean bool = isChunked;
    isChunked = repIdUtil.isChunkedEncoding(i);
    Object localObject = null;
    String str1 = null;
    if (repIdUtil.isCodeBasePresent(i)) {
      str1 = read_codebase_URL();
    }
    String str2 = readRepositoryIds(i, paramClass, null);
    start_block();
    end_flag -= 1;
    if (isChunked) {
      chunkedValueNestingLevel -= 1;
    }
    if (str2.equals(repIdStrs.getWStringValueRepId()))
    {
      localObject = read_wstring();
    }
    else if (str2.equals(repIdStrs.getClassDescValueRepId()))
    {
      localObject = readClass();
    }
    else
    {
      Class localClass = paramClass;
      if ((paramClass == null) || (!str2.equals(repIdStrs.createForAnyType(paramClass)))) {
        localClass = getClassFromString(str2, str1, paramClass);
      }
      if (localClass == null) {
        throw wrapper.couldNotFindClass(CompletionStatus.COMPLETED_MAYBE, new ClassNotFoundException());
      }
      if ((localClass != null) && (IDLEntity.class.isAssignableFrom(localClass))) {
        localObject = readIDLValue(j, str2, localClass, str1);
      } else {
        try
        {
          if (valueHandler == null) {
            valueHandler = ORBUtility.createValueHandler();
          }
          localObject = valueHandler.readValue(parent, j, localClass, str2, getCodeBase());
        }
        catch (SystemException localSystemException)
        {
          throw localSystemException;
        }
        catch (Exception localException)
        {
          throw wrapper.valuehandlerReadException(CompletionStatus.COMPLETED_MAYBE, localException);
        }
        catch (Error localError)
        {
          throw wrapper.valuehandlerReadError(CompletionStatus.COMPLETED_MAYBE, localError);
        }
      }
    }
    handleEndOfValue();
    readEndTag();
    if (valueCache == null) {
      valueCache = new CacheTable(orb, false);
    }
    valueCache.put(localObject, j);
    isChunked = bool;
    start_block();
    return (Serializable)localObject;
  }
  
  public Serializable read_value(BoxedValueHelper paramBoxedValueHelper)
  {
    int i = readValueTag();
    if (i == 0) {
      return null;
    }
    if (i == -1)
    {
      j = read_long() + get_offset() - 4;
      if ((valueCache != null) && (valueCache.containsVal(j)))
      {
        Serializable localSerializable = (Serializable)valueCache.getKey(j);
        return localSerializable;
      }
      throw new IndirectionException(j);
    }
    int j = get_offset() - 4;
    boolean bool = isChunked;
    isChunked = repIdUtil.isChunkedEncoding(i);
    Object localObject = null;
    String str1 = null;
    if (repIdUtil.isCodeBasePresent(i)) {
      str1 = read_codebase_URL();
    }
    String str2 = readRepositoryIds(i, null, null, paramBoxedValueHelper);
    if (!str2.equals(paramBoxedValueHelper.get_id())) {
      paramBoxedValueHelper = Utility.getHelper(null, str1, str2);
    }
    start_block();
    end_flag -= 1;
    if (isChunked) {
      chunkedValueNestingLevel -= 1;
    }
    if ((paramBoxedValueHelper instanceof ValueHelper))
    {
      localObject = readIDLValueWithHelper((ValueHelper)paramBoxedValueHelper, j);
    }
    else
    {
      valueIndirection = j;
      localObject = paramBoxedValueHelper.read_value(parent);
    }
    handleEndOfValue();
    readEndTag();
    if (valueCache == null) {
      valueCache = new CacheTable(orb, false);
    }
    valueCache.put(localObject, j);
    isChunked = bool;
    start_block();
    return (Serializable)localObject;
  }
  
  private boolean isCustomType(ValueHelper paramValueHelper)
  {
    try
    {
      TypeCode localTypeCode = paramValueHelper.get_type();
      int i = localTypeCode.kind().value();
      if (i == 29) {
        return localTypeCode.type_modifier() == 1;
      }
    }
    catch (BadKind localBadKind)
    {
      throw wrapper.badKind(localBadKind);
    }
    return false;
  }
  
  public Serializable read_value(Serializable paramSerializable)
  {
    if (valueCache == null) {
      valueCache = new CacheTable(orb, false);
    }
    valueCache.put(paramSerializable, valueIndirection);
    if ((paramSerializable instanceof StreamableValue)) {
      ((StreamableValue)paramSerializable)._read(parent);
    } else if ((paramSerializable instanceof CustomValue)) {
      ((CustomValue)paramSerializable).unmarshal(parent);
    }
    return paramSerializable;
  }
  
  public Serializable read_value(String paramString)
  {
    int i = readValueTag();
    if (i == 0) {
      return null;
    }
    if (i == -1)
    {
      j = read_long() + get_offset() - 4;
      if ((valueCache != null) && (valueCache.containsVal(j)))
      {
        Serializable localSerializable1 = (Serializable)valueCache.getKey(j);
        return localSerializable1;
      }
      throw new IndirectionException(j);
    }
    int j = get_offset() - 4;
    boolean bool = isChunked;
    isChunked = repIdUtil.isChunkedEncoding(i);
    Serializable localSerializable2 = null;
    String str1 = null;
    if (repIdUtil.isCodeBasePresent(i)) {
      str1 = read_codebase_URL();
    }
    String str2 = readRepositoryIds(i, null, paramString);
    ValueFactory localValueFactory = Utility.getFactory(null, str1, orb, str2);
    start_block();
    end_flag -= 1;
    if (isChunked) {
      chunkedValueNestingLevel -= 1;
    }
    valueIndirection = j;
    localSerializable2 = localValueFactory.read_value(parent);
    handleEndOfValue();
    readEndTag();
    if (valueCache == null) {
      valueCache = new CacheTable(orb, false);
    }
    valueCache.put(localSerializable2, j);
    isChunked = bool;
    start_block();
    return (Serializable)localSerializable2;
  }
  
  private Class readClass()
  {
    String str1 = null;
    String str2 = null;
    if ((orb == null) || (ORBVersionFactory.getFOREIGN().equals(orb.getORBVersion())) || (ORBVersionFactory.getNEWER().compareTo(orb.getORBVersion()) <= 0))
    {
      str1 = (String)read_value(String.class);
      str2 = (String)read_value(String.class);
    }
    else
    {
      str2 = (String)read_value(String.class);
      str1 = (String)read_value(String.class);
    }
    if (debug) {
      dprint("readClass codebases: " + str1 + " rep Id: " + str2);
    }
    Class localClass = null;
    RepositoryIdInterface localRepositoryIdInterface = repIdStrs.getFromString(str2);
    try
    {
      localClass = localRepositoryIdInterface.getClassFromType(str1);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw wrapper.cnfeReadClass(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException, localRepositoryIdInterface.getClassName());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, localMalformedURLException, localRepositoryIdInterface.getClassName(), str1);
    }
    return localClass;
  }
  
  private Object readIDLValueWithHelper(ValueHelper paramValueHelper, int paramInt)
  {
    Method localMethod;
    try
    {
      Class[] arrayOfClass = { InputStream.class, paramValueHelper.get_class() };
      localMethod = paramValueHelper.getClass().getDeclaredMethod("read", arrayOfClass);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Serializable localSerializable = paramValueHelper.read_value(parent);
      return localSerializable;
    }
    Object localObject = null;
    try
    {
      localObject = paramValueHelper.get_class().newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      throw wrapper.couldNotInstantiateHelper(localInstantiationException, paramValueHelper.get_class());
    }
    catch (IllegalAccessException localIllegalAccessException1)
    {
      return paramValueHelper.read_value(parent);
    }
    if (valueCache == null) {
      valueCache = new CacheTable(orb, false);
    }
    valueCache.put(localObject, paramInt);
    if (((localObject instanceof CustomMarshal)) && (isCustomType(paramValueHelper)))
    {
      ((CustomMarshal)localObject).unmarshal(parent);
      return localObject;
    }
    try
    {
      Object[] arrayOfObject = { parent, localObject };
      localMethod.invoke(paramValueHelper, arrayOfObject);
      return localObject;
    }
    catch (IllegalAccessException localIllegalAccessException2)
    {
      throw wrapper.couldNotInvokeHelperReadMethod(localIllegalAccessException2, paramValueHelper.get_class());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw wrapper.couldNotInvokeHelperReadMethod(localInvocationTargetException, paramValueHelper.get_class());
    }
  }
  
  private Object readBoxedIDLEntity(Class paramClass, String paramString)
  {
    Class localClass1 = null;
    try
    {
      ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
      localClass1 = Utility.loadClassForClass(paramClass.getName() + "Helper", paramString, localClassLoader, paramClass, localClassLoader);
      final Class localClass2 = localClass1;
      final Class[] arrayOfClass = { InputStream.class };
      Method localMethod = null;
      try
      {
        localMethod = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws NoSuchMethodException
          {
            return localClass2.getDeclaredMethod("read", arrayOfClass);
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((NoSuchMethodException)localPrivilegedActionException.getException());
      }
      Object[] arrayOfObject = { parent };
      return localMethod.invoke(null, arrayOfObject);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw wrapper.couldNotInvokeHelperReadMethod(localClassNotFoundException, localClass1);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw wrapper.couldNotInvokeHelperReadMethod(localNoSuchMethodException, localClass1);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw wrapper.couldNotInvokeHelperReadMethod(localIllegalAccessException, localClass1);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw wrapper.couldNotInvokeHelperReadMethod(localInvocationTargetException, localClass1);
    }
  }
  
  private Object readIDLValue(int paramInt, String paramString1, Class paramClass, String paramString2)
  {
    ValueFactory localValueFactory;
    try
    {
      localValueFactory = Utility.getFactory(paramClass, paramString2, orb, paramString1);
    }
    catch (MARSHAL localMARSHAL)
    {
      if ((!StreamableValue.class.isAssignableFrom(paramClass)) && (!CustomValue.class.isAssignableFrom(paramClass)) && (ValueBase.class.isAssignableFrom(paramClass)))
      {
        BoxedValueHelper localBoxedValueHelper = Utility.getHelper(paramClass, paramString2, paramString1);
        if ((localBoxedValueHelper instanceof ValueHelper)) {
          return readIDLValueWithHelper((ValueHelper)localBoxedValueHelper, paramInt);
        }
        return localBoxedValueHelper.read_value(parent);
      }
      return readBoxedIDLEntity(paramClass, paramString2);
    }
    valueIndirection = paramInt;
    return localValueFactory.read_value(parent);
  }
  
  private void readEndTag()
  {
    if (isChunked)
    {
      int i = read_long();
      if (i >= 0) {
        throw wrapper.positiveEndTag(CompletionStatus.COMPLETED_MAYBE, new Integer(i), new Integer(get_offset() - 4));
      }
      if ((orb == null) || (ORBVersionFactory.getFOREIGN().equals(orb.getORBVersion())) || (ORBVersionFactory.getNEWER().compareTo(orb.getORBVersion()) <= 0))
      {
        if (i < chunkedValueNestingLevel) {
          throw wrapper.unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_MAYBE, new Integer(i), new Integer(chunkedValueNestingLevel));
        }
        if (i != chunkedValueNestingLevel) {
          bbwi.position(bbwi.position() - 4);
        }
      }
      else if (i != end_flag)
      {
        bbwi.position(bbwi.position() - 4);
      }
      chunkedValueNestingLevel += 1;
    }
    end_flag += 1;
  }
  
  protected int get_offset()
  {
    return bbwi.position();
  }
  
  private void start_block()
  {
    if (!isChunked) {
      return;
    }
    blockLength = 2147483392;
    blockLength = read_long();
    if ((blockLength > 0) && (blockLength < 2147483392))
    {
      blockLength += get_offset();
    }
    else
    {
      blockLength = 2147483392;
      bbwi.position(bbwi.position() - 4);
    }
  }
  
  private void handleEndOfValue()
  {
    if (!isChunked) {
      return;
    }
    while (blockLength != 2147483392)
    {
      end_block();
      start_block();
    }
    int i = read_long();
    bbwi.position(bbwi.position() - 4);
    if (i < 0) {
      return;
    }
    if ((i == 0) || (i >= 2147483392))
    {
      read_value();
      handleEndOfValue();
    }
    else
    {
      throw wrapper.couldNotSkipBytes(CompletionStatus.COMPLETED_MAYBE, new Integer(i), new Integer(get_offset()));
    }
  }
  
  private void end_block()
  {
    if (blockLength != 2147483392) {
      if (blockLength == get_offset()) {
        blockLength = 2147483392;
      } else if (blockLength > get_offset()) {
        skipToOffset(blockLength);
      } else {
        throw wrapper.badChunkLength(new Integer(blockLength), new Integer(get_offset()));
      }
    }
  }
  
  private int readValueTag()
  {
    return read_long();
  }
  
  public org.omg.CORBA.ORB orb()
  {
    return orb;
  }
  
  public final void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfBoolean[(i + paramInt1)] = read_boolean();
    }
  }
  
  public final void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfChar[(i + paramInt1)] = read_char();
    }
  }
  
  public final void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfChar[(i + paramInt1)] = read_wchar();
    }
  }
  
  public final void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfShort[(i + paramInt1)] = read_short();
    }
  }
  
  public final void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    read_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public final void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfInt[(i + paramInt1)] = read_long();
    }
  }
  
  public final void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    read_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfLong[(i + paramInt1)] = read_longlong();
    }
  }
  
  public final void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    read_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public final void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfFloat[(i + paramInt1)] = read_float();
    }
  }
  
  public final void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfDouble[(i + paramInt1)] = read_double();
    }
  }
  
  public final void read_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfAny[(i + paramInt1)] = read_any();
    }
  }
  
  private String read_repositoryIds()
  {
    int i = read_long();
    if (i == -1)
    {
      j = read_long() + get_offset() - 4;
      if ((repositoryIdCache != null) && (repositoryIdCache.containsOrderedVal(j))) {
        return (String)repositoryIdCache.getKey(j);
      }
      throw wrapper.unableToLocateRepIdArray(new Integer(j));
    }
    int j = get_offset();
    String str = read_repositoryId();
    if (repositoryIdCache == null) {
      repositoryIdCache = new CacheTable(orb, false);
    }
    repositoryIdCache.put(str, j);
    for (int k = 1; k < i; k++) {
      read_repositoryId();
    }
    return str;
  }
  
  private final String read_repositoryId()
  {
    String str = readStringOrIndirection(true);
    if (str == null)
    {
      int i = read_long() + get_offset() - 4;
      if ((repositoryIdCache != null) && (repositoryIdCache.containsOrderedVal(i))) {
        return (String)repositoryIdCache.getKey(i);
      }
      throw wrapper.badRepIdIndirection(CompletionStatus.COMPLETED_MAYBE, new Integer(bbwi.position()));
    }
    if (repositoryIdCache == null) {
      repositoryIdCache = new CacheTable(orb, false);
    }
    repositoryIdCache.put(str, stringIndirection);
    return str;
  }
  
  private final String read_codebase_URL()
  {
    String str = readStringOrIndirection(true);
    if (str == null)
    {
      int i = read_long() + get_offset() - 4;
      if ((codebaseCache != null) && (codebaseCache.containsVal(i))) {
        return (String)codebaseCache.getKey(i);
      }
      throw wrapper.badCodebaseIndirection(CompletionStatus.COMPLETED_MAYBE, new Integer(bbwi.position()));
    }
    if (codebaseCache == null) {
      codebaseCache = new CacheTable(orb, false);
    }
    codebaseCache.put(str, stringIndirection);
    return str;
  }
  
  public Object read_Abstract()
  {
    return read_abstract_interface();
  }
  
  public Serializable read_Value()
  {
    return read_value();
  }
  
  public void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2)
  {
    read_any_array(value, paramInt1, paramInt2);
  }
  
  public void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2)
  {
    read_boolean_array(value, paramInt1, paramInt2);
  }
  
  public void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2)
  {
    read_char_array(value, paramInt1, paramInt2);
  }
  
  public void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2)
  {
    read_wchar_array(value, paramInt1, paramInt2);
  }
  
  public void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2)
  {
    read_octet_array(value, paramInt1, paramInt2);
  }
  
  public void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2)
  {
    read_short_array(value, paramInt1, paramInt2);
  }
  
  public void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2)
  {
    read_ushort_array(value, paramInt1, paramInt2);
  }
  
  public void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2)
  {
    read_long_array(value, paramInt1, paramInt2);
  }
  
  public void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2)
  {
    read_ulong_array(value, paramInt1, paramInt2);
  }
  
  public void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2)
  {
    read_ulonglong_array(value, paramInt1, paramInt2);
  }
  
  public void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2)
  {
    read_longlong_array(value, paramInt1, paramInt2);
  }
  
  public void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2)
  {
    read_float_array(value, paramInt1, paramInt2);
  }
  
  public void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2)
  {
    read_double_array(value, paramInt1, paramInt2);
  }
  
  public BigDecimal read_fixed(short paramShort1, short paramShort2)
  {
    StringBuffer localStringBuffer = read_fixed_buffer();
    if (paramShort1 != localStringBuffer.length()) {
      throw wrapper.badFixed(new Integer(paramShort1), new Integer(localStringBuffer.length()));
    }
    localStringBuffer.insert(paramShort1 - paramShort2, '.');
    return new BigDecimal(localStringBuffer.toString());
  }
  
  public BigDecimal read_fixed()
  {
    return new BigDecimal(read_fixed_buffer().toString());
  }
  
  private StringBuffer read_fixed_buffer()
  {
    StringBuffer localStringBuffer = new StringBuffer(64);
    int m = 0;
    int n = 1;
    while (n != 0)
    {
      int i = read_octet();
      int j = (i & 0xF0) >> 4;
      int k = i & 0xF;
      if ((m != 0) || (j != 0))
      {
        localStringBuffer.append(Character.forDigit(j, 10));
        m = 1;
      }
      if (k == 12)
      {
        if (m == 0) {
          return new StringBuffer("0.0");
        }
        n = 0;
      }
      else if (k == 13)
      {
        localStringBuffer.insert(0, '-');
        n = 0;
      }
      else
      {
        localStringBuffer.append(Character.forDigit(k, 10));
        m = 1;
      }
    }
    return localStringBuffer;
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
    System.out.println("----- Input Buffer -----");
    System.out.println();
    System.out.println("Current position: " + paramByteBufferWithInfo.position());
    System.out.println("Total length : " + buflen);
    System.out.println();
    try
    {
      char[] arrayOfChar = new char[16];
      for (int i = 0; i < buflen; i += 16)
      {
        for (int j = 0; (j < 16) && (j + i < buflen); j++)
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
        for (int k = 0; (k < 16) && (k + i < buflen); k++) {
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
    System.out.println("------------------------");
  }
  
  public ByteBuffer getByteBuffer()
  {
    ByteBuffer localByteBuffer = null;
    if (bbwi != null) {
      localByteBuffer = bbwi.byteBuffer;
    }
    return localByteBuffer;
  }
  
  public int getBufferLength()
  {
    return bbwi.buflen;
  }
  
  public void setBufferLength(int paramInt)
  {
    bbwi.buflen = paramInt;
    bbwi.byteBuffer.limit(bbwi.buflen);
  }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    bbwi = paramByteBufferWithInfo;
  }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer)
  {
    bbwi.byteBuffer = paramByteBuffer;
  }
  
  public int getIndex()
  {
    return bbwi.position();
  }
  
  public void setIndex(int paramInt)
  {
    bbwi.position(paramInt);
  }
  
  public boolean isLittleEndian()
  {
    return littleEndian;
  }
  
  public void orb(org.omg.CORBA.ORB paramORB)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
  }
  
  public BufferManagerRead getBufferManager()
  {
    return bufferManagerRead;
  }
  
  private void skipToOffset(int paramInt)
  {
    int i = paramInt - get_offset();
    int j = 0;
    while (j < i)
    {
      int k = bbwi.buflen - bbwi.position();
      if (k <= 0)
      {
        grow(1, 1);
        k = bbwi.buflen - bbwi.position();
      }
      int n = i - j;
      int m = n < k ? n : k;
      bbwi.position(bbwi.position() + m);
      j += m;
    }
  }
  
  public Object createStreamMemento()
  {
    return new StreamMemento();
  }
  
  public void restoreInternalState(Object paramObject)
  {
    StreamMemento localStreamMemento = (StreamMemento)paramObject;
    blockLength = blockLength_;
    end_flag = end_flag_;
    chunkedValueNestingLevel = chunkedValueNestingLevel_;
    valueIndirection = valueIndirection_;
    stringIndirection = stringIndirection_;
    isChunked = isChunked_;
    valueHandler = valueHandler_;
    specialNoOptionalDataState = specialNoOptionalDataState_;
    bbwi = bbwi_;
  }
  
  public int getPosition()
  {
    return get_offset();
  }
  
  public void mark(int paramInt)
  {
    markAndResetHandler.mark(this);
  }
  
  public void reset()
  {
    markAndResetHandler.reset();
  }
  
  CodeBase getCodeBase()
  {
    return parent.getCodeBase();
  }
  
  private Class getClassFromString(String paramString1, String paramString2, Class paramClass)
  {
    RepositoryIdInterface localRepositoryIdInterface = repIdStrs.getFromString(paramString1);
    try
    {
      return localRepositoryIdInterface.getClassFromType(paramClass, paramString2);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      try
      {
        if (getCodeBase() == null) {
          return null;
        }
        paramString2 = getCodeBase().implementation(paramString1);
        if (paramString2 == null) {
          return null;
        }
        return localRepositoryIdInterface.getClassFromType(paramClass, paramString2);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        dprintThrowable(localClassNotFoundException2);
        return null;
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, localMalformedURLException, paramString1, paramString2);
    }
  }
  
  private Class getClassFromString(String paramString1, String paramString2)
  {
    RepositoryIdInterface localRepositoryIdInterface = repIdStrs.getFromString(paramString1);
    for (int i = 0; i < 3; i++) {
      try
      {
        switch (i)
        {
        case 0: 
          return localRepositoryIdInterface.getClassFromType();
        case 1: 
          break;
        case 2: 
          paramString2 = getCodeBase().implementation(paramString1);
        }
        if (paramString2 != null) {
          return localRepositoryIdInterface.getClassFromType(paramString2);
        }
      }
      catch (ClassNotFoundException localClassNotFoundException) {}catch (MalformedURLException localMalformedURLException)
      {
        throw wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, localMalformedURLException, paramString1, paramString2);
      }
    }
    dprint("getClassFromString failed with rep id " + paramString1 + " and codebase " + paramString2);
    return null;
  }
  
  char[] getConvertedChars(int paramInt, CodeSetConversion.BTCConverter paramBTCConverter)
  {
    if (bbwi.buflen - bbwi.position() >= paramInt)
    {
      if (bbwi.byteBuffer.hasArray())
      {
        arrayOfByte = bbwi.byteBuffer.array();
      }
      else
      {
        arrayOfByte = new byte[bbwi.buflen];
        for (int i = 0; i < bbwi.buflen; i++) {
          arrayOfByte[i] = bbwi.byteBuffer.get(i);
        }
      }
      char[] arrayOfChar = paramBTCConverter.getChars(arrayOfByte, bbwi.position(), paramInt);
      bbwi.position(bbwi.position() + paramInt);
      return arrayOfChar;
    }
    byte[] arrayOfByte = new byte[paramInt];
    read_octet_array(arrayOfByte, 0, arrayOfByte.length);
    return paramBTCConverter.getChars(arrayOfByte, 0, paramInt);
  }
  
  protected CodeSetConversion.BTCConverter getCharConverter()
  {
    if (charConverter == null) {
      charConverter = parent.createCharBTCConverter();
    }
    return charConverter;
  }
  
  protected CodeSetConversion.BTCConverter getWCharConverter()
  {
    if (wcharConverter == null) {
      wcharConverter = parent.createWCharBTCConverter();
    }
    return wcharConverter;
  }
  
  protected void dprintThrowable(Throwable paramThrowable)
  {
    if ((debug) && (paramThrowable != null)) {
      paramThrowable.printStackTrace();
    }
  }
  
  protected void dprint(String paramString)
  {
    if (debug) {
      ORBUtility.dprint(this, paramString);
    }
  }
  
  void alignOnBoundary(int paramInt)
  {
    int i = computeAlignment(bbwi.position(), paramInt);
    if (bbwi.position() + i <= bbwi.buflen) {
      bbwi.position(bbwi.position() + i);
    }
  }
  
  public void resetCodeSetConverters()
  {
    charConverter = null;
    wcharConverter = null;
  }
  
  public void start_value()
  {
    int i = readValueTag();
    if (i == 0)
    {
      specialNoOptionalDataState = true;
      return;
    }
    if (i == -1) {
      throw wrapper.customWrapperIndirection(CompletionStatus.COMPLETED_MAYBE);
    }
    if (repIdUtil.isCodeBasePresent(i)) {
      throw wrapper.customWrapperWithCodebase(CompletionStatus.COMPLETED_MAYBE);
    }
    if (repIdUtil.getTypeInfo(i) != 2) {
      throw wrapper.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_MAYBE);
    }
    read_repositoryId();
    start_block();
    end_flag -= 1;
    chunkedValueNestingLevel -= 1;
  }
  
  public void end_value()
  {
    if (specialNoOptionalDataState)
    {
      specialNoOptionalDataState = false;
      return;
    }
    handleEndOfValue();
    readEndTag();
    start_block();
  }
  
  public void close()
    throws IOException
  {
    getBufferManager().close(bbwi);
    if ((bbwi != null) && (getByteBuffer() != null))
    {
      MessageMediator localMessageMediator = parent.getMessageMediator();
      if (localMessageMediator != null)
      {
        localObject = (CDROutputObject)localMessageMediator.getOutputObject();
        if ((localObject != null) && (((CDROutputObject)localObject).isSharing(getByteBuffer())))
        {
          ((CDROutputObject)localObject).setByteBuffer(null);
          ((CDROutputObject)localObject).setByteBufferWithInfo(null);
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
      ((ByteBufferPool)localObject).releaseByteBuffer(bbwi.byteBuffer);
      bbwi.byteBuffer = null;
      bbwi = null;
    }
  }
  
  protected class StreamMemento
  {
    private int blockLength_ = blockLength;
    private int end_flag_ = end_flag;
    private int chunkedValueNestingLevel_ = chunkedValueNestingLevel;
    private int valueIndirection_ = valueIndirection;
    private int stringIndirection_ = stringIndirection;
    private boolean isChunked_ = isChunked;
    private ValueHandler valueHandler_ = valueHandler;
    private ByteBufferWithInfo bbwi_ = new ByteBufferWithInfo(bbwi);
    private boolean specialNoOptionalDataState_ = specialNoOptionalDataState;
    
    public StreamMemento() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */