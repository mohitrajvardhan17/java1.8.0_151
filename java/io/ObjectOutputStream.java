package java.io;

import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetBooleanAction;

public class ObjectOutputStream
  extends OutputStream
  implements ObjectOutput, ObjectStreamConstants
{
  private final BlockDataOutputStream bout;
  private final HandleTable handles;
  private final ReplaceTable subs;
  private int protocol = 2;
  private int depth;
  private byte[] primVals;
  private final boolean enableOverride;
  private boolean enableReplace;
  private SerialCallbackContext curContext;
  private PutFieldImpl curPut;
  private final DebugTraceInfoStack debugInfoStack;
  private static final boolean extendedDebugInfo = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.io.serialization.extendedDebugInfo"))).booleanValue();
  
  public ObjectOutputStream(OutputStream paramOutputStream)
    throws IOException
  {
    verifySubclass();
    bout = new BlockDataOutputStream(paramOutputStream);
    handles = new HandleTable(10, 3.0F);
    subs = new ReplaceTable(10, 3.0F);
    enableOverride = false;
    writeStreamHeader();
    bout.setBlockDataMode(true);
    if (extendedDebugInfo) {
      debugInfoStack = new DebugTraceInfoStack();
    } else {
      debugInfoStack = null;
    }
  }
  
  protected ObjectOutputStream()
    throws IOException, SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
    }
    bout = null;
    handles = null;
    subs = null;
    enableOverride = true;
    debugInfoStack = null;
  }
  
  public void useProtocolVersion(int paramInt)
    throws IOException
  {
    if (handles.size() != 0) {
      throw new IllegalStateException("stream non-empty");
    }
    switch (paramInt)
    {
    case 1: 
    case 2: 
      protocol = paramInt;
      break;
    default: 
      throw new IllegalArgumentException("unknown version: " + paramInt);
    }
  }
  
  public final void writeObject(Object paramObject)
    throws IOException
  {
    if (enableOverride)
    {
      writeObjectOverride(paramObject);
      return;
    }
    try
    {
      writeObject0(paramObject, false);
    }
    catch (IOException localIOException)
    {
      if (depth == 0) {
        writeFatalException(localIOException);
      }
      throw localIOException;
    }
  }
  
  protected void writeObjectOverride(Object paramObject)
    throws IOException
  {}
  
  public void writeUnshared(Object paramObject)
    throws IOException
  {
    try
    {
      writeObject0(paramObject, true);
    }
    catch (IOException localIOException)
    {
      if (depth == 0) {
        writeFatalException(localIOException);
      }
      throw localIOException;
    }
  }
  
  public void defaultWriteObject()
    throws IOException
  {
    SerialCallbackContext localSerialCallbackContext = curContext;
    if (localSerialCallbackContext == null) {
      throw new NotActiveException("not in call to writeObject");
    }
    Object localObject = localSerialCallbackContext.getObj();
    ObjectStreamClass localObjectStreamClass = localSerialCallbackContext.getDesc();
    bout.setBlockDataMode(false);
    defaultWriteFields(localObject, localObjectStreamClass);
    bout.setBlockDataMode(true);
  }
  
  public PutField putFields()
    throws IOException
  {
    if (curPut == null)
    {
      SerialCallbackContext localSerialCallbackContext = curContext;
      if (localSerialCallbackContext == null) {
        throw new NotActiveException("not in call to writeObject");
      }
      Object localObject = localSerialCallbackContext.getObj();
      ObjectStreamClass localObjectStreamClass = localSerialCallbackContext.getDesc();
      curPut = new PutFieldImpl(localObjectStreamClass);
    }
    return curPut;
  }
  
  public void writeFields()
    throws IOException
  {
    if (curPut == null) {
      throw new NotActiveException("no current PutField object");
    }
    bout.setBlockDataMode(false);
    curPut.writeFields();
    bout.setBlockDataMode(true);
  }
  
  public void reset()
    throws IOException
  {
    if (depth != 0) {
      throw new IOException("stream active");
    }
    bout.setBlockDataMode(false);
    bout.writeByte(121);
    clear();
    bout.setBlockDataMode(true);
  }
  
  protected void annotateClass(Class<?> paramClass)
    throws IOException
  {}
  
  protected void annotateProxyClass(Class<?> paramClass)
    throws IOException
  {}
  
  protected Object replaceObject(Object paramObject)
    throws IOException
  {
    return paramObject;
  }
  
  protected boolean enableReplaceObject(boolean paramBoolean)
    throws SecurityException
  {
    if (paramBoolean == enableReplace) {
      return paramBoolean;
    }
    if (paramBoolean)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SUBSTITUTION_PERMISSION);
      }
    }
    enableReplace = paramBoolean;
    return !enableReplace;
  }
  
  protected void writeStreamHeader()
    throws IOException
  {
    bout.writeShort(44269);
    bout.writeShort(5);
  }
  
  protected void writeClassDescriptor(ObjectStreamClass paramObjectStreamClass)
    throws IOException
  {
    paramObjectStreamClass.writeNonProxy(this);
  }
  
  public void write(int paramInt)
    throws IOException
  {
    bout.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    bout.write(paramArrayOfByte, 0, paramArrayOfByte.length, false);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    int i = paramInt1 + paramInt2;
    if ((paramInt1 < 0) || (paramInt2 < 0) || (i > paramArrayOfByte.length) || (i < 0)) {
      throw new IndexOutOfBoundsException();
    }
    bout.write(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public void flush()
    throws IOException
  {
    bout.flush();
  }
  
  protected void drain()
    throws IOException
  {
    bout.drain();
  }
  
  public void close()
    throws IOException
  {
    flush();
    clear();
    bout.close();
  }
  
  public void writeBoolean(boolean paramBoolean)
    throws IOException
  {
    bout.writeBoolean(paramBoolean);
  }
  
  public void writeByte(int paramInt)
    throws IOException
  {
    bout.writeByte(paramInt);
  }
  
  public void writeShort(int paramInt)
    throws IOException
  {
    bout.writeShort(paramInt);
  }
  
  public void writeChar(int paramInt)
    throws IOException
  {
    bout.writeChar(paramInt);
  }
  
  public void writeInt(int paramInt)
    throws IOException
  {
    bout.writeInt(paramInt);
  }
  
  public void writeLong(long paramLong)
    throws IOException
  {
    bout.writeLong(paramLong);
  }
  
  public void writeFloat(float paramFloat)
    throws IOException
  {
    bout.writeFloat(paramFloat);
  }
  
  public void writeDouble(double paramDouble)
    throws IOException
  {
    bout.writeDouble(paramDouble);
  }
  
  public void writeBytes(String paramString)
    throws IOException
  {
    bout.writeBytes(paramString);
  }
  
  public void writeChars(String paramString)
    throws IOException
  {
    bout.writeChars(paramString);
  }
  
  public void writeUTF(String paramString)
    throws IOException
  {
    bout.writeUTF(paramString);
  }
  
  int getProtocolVersion()
  {
    return protocol;
  }
  
  void writeTypeString(String paramString)
    throws IOException
  {
    if (paramString == null)
    {
      writeNull();
    }
    else
    {
      int i;
      if ((i = handles.lookup(paramString)) != -1) {
        writeHandle(i);
      } else {
        writeString(paramString, false);
      }
    }
  }
  
  private void verifySubclass()
  {
    Class localClass = getClass();
    if (localClass == ObjectOutputStream.class) {
      return;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      return;
    }
    ObjectStreamClass.processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
    ObjectStreamClass.WeakClassKey localWeakClassKey = new ObjectStreamClass.WeakClassKey(localClass, Caches.subclassAuditsQueue);
    Boolean localBoolean = (Boolean)Caches.subclassAudits.get(localWeakClassKey);
    if (localBoolean == null)
    {
      localBoolean = Boolean.valueOf(auditSubclass(localClass));
      Caches.subclassAudits.putIfAbsent(localWeakClassKey, localBoolean);
    }
    if (localBoolean.booleanValue()) {
      return;
    }
    localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
  }
  
  private static boolean auditSubclass(Class<?> paramClass)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        Class localClass = val$subcl;
        while (localClass != ObjectOutputStream.class) {
          try
          {
            localClass.getDeclaredMethod("writeUnshared", new Class[] { Object.class });
            return Boolean.FALSE;
          }
          catch (NoSuchMethodException localNoSuchMethodException1)
          {
            try
            {
              localClass.getDeclaredMethod("putFields", (Class[])null);
              return Boolean.FALSE;
            }
            catch (NoSuchMethodException localNoSuchMethodException2)
            {
              localClass = localClass.getSuperclass();
            }
          }
        }
        return Boolean.TRUE;
      }
    });
    return localBoolean.booleanValue();
  }
  
  private void clear()
  {
    subs.clear();
    handles.clear();
  }
  
  private void writeObject0(Object paramObject, boolean paramBoolean)
    throws IOException
  {
    boolean bool = bout.setBlockDataMode(false);
    depth += 1;
    try
    {
      if ((paramObject = subs.lookup(paramObject)) == null)
      {
        writeNull();
        return;
      }
      int i;
      if ((!paramBoolean) && ((i = handles.lookup(paramObject)) != -1))
      {
        writeHandle(i);
        return;
      }
      if ((paramObject instanceof Class))
      {
        writeClass((Class)paramObject, paramBoolean);
        return;
      }
      if ((paramObject instanceof ObjectStreamClass))
      {
        writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
        return;
      }
      Object localObject1 = paramObject;
      ObjectStreamClass localObjectStreamClass;
      Object localObject3;
      for (Object localObject2 = paramObject.getClass();; localObject2 = localObject3)
      {
        localObjectStreamClass = ObjectStreamClass.lookup((Class)localObject2, true);
        if ((!localObjectStreamClass.hasWriteReplaceMethod()) || ((paramObject = localObjectStreamClass.invokeWriteReplace(paramObject)) == null) || ((localObject3 = paramObject.getClass()) == localObject2)) {
          break;
        }
      }
      if (enableReplace)
      {
        localObject3 = replaceObject(paramObject);
        if ((localObject3 != paramObject) && (localObject3 != null))
        {
          localObject2 = localObject3.getClass();
          localObjectStreamClass = ObjectStreamClass.lookup((Class)localObject2, true);
        }
        paramObject = localObject3;
      }
      if (paramObject != localObject1)
      {
        subs.assign(localObject1, paramObject);
        if (paramObject == null)
        {
          writeNull();
          return;
        }
        if ((!paramBoolean) && ((i = handles.lookup(paramObject)) != -1))
        {
          writeHandle(i);
          return;
        }
        if ((paramObject instanceof Class))
        {
          writeClass((Class)paramObject, paramBoolean);
          return;
        }
        if ((paramObject instanceof ObjectStreamClass))
        {
          writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
          return;
        }
      }
      if ((paramObject instanceof String))
      {
        writeString((String)paramObject, paramBoolean);
      }
      else if (((Class)localObject2).isArray())
      {
        writeArray(paramObject, localObjectStreamClass, paramBoolean);
      }
      else if ((paramObject instanceof Enum))
      {
        writeEnum((Enum)paramObject, localObjectStreamClass, paramBoolean);
      }
      else if ((paramObject instanceof Serializable))
      {
        writeOrdinaryObject(paramObject, localObjectStreamClass, paramBoolean);
      }
      else
      {
        if (extendedDebugInfo) {
          throw new NotSerializableException(((Class)localObject2).getName() + "\n" + debugInfoStack.toString());
        }
        throw new NotSerializableException(((Class)localObject2).getName());
      }
    }
    finally
    {
      depth -= 1;
      bout.setBlockDataMode(bool);
    }
  }
  
  private void writeNull()
    throws IOException
  {
    bout.writeByte(112);
  }
  
  private void writeHandle(int paramInt)
    throws IOException
  {
    bout.writeByte(113);
    bout.writeInt(8257536 + paramInt);
  }
  
  private void writeClass(Class<?> paramClass, boolean paramBoolean)
    throws IOException
  {
    bout.writeByte(118);
    writeClassDesc(ObjectStreamClass.lookup(paramClass, true), false);
    handles.assign(paramBoolean ? null : paramClass);
  }
  
  private void writeClassDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
    throws IOException
  {
    if (paramObjectStreamClass == null)
    {
      writeNull();
    }
    else
    {
      int i;
      if ((!paramBoolean) && ((i = handles.lookup(paramObjectStreamClass)) != -1)) {
        writeHandle(i);
      } else if (paramObjectStreamClass.isProxy()) {
        writeProxyDesc(paramObjectStreamClass, paramBoolean);
      } else {
        writeNonProxyDesc(paramObjectStreamClass, paramBoolean);
      }
    }
  }
  
  private boolean isCustomSubclass()
  {
    return getClass().getClassLoader() != ObjectOutputStream.class.getClassLoader();
  }
  
  private void writeProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
    throws IOException
  {
    bout.writeByte(125);
    handles.assign(paramBoolean ? null : paramObjectStreamClass);
    Class localClass = paramObjectStreamClass.forClass();
    Class[] arrayOfClass = localClass.getInterfaces();
    bout.writeInt(arrayOfClass.length);
    for (int i = 0; i < arrayOfClass.length; i++) {
      bout.writeUTF(arrayOfClass[i].getName());
    }
    bout.setBlockDataMode(true);
    if ((localClass != null) && (isCustomSubclass())) {
      ReflectUtil.checkPackageAccess(localClass);
    }
    annotateProxyClass(localClass);
    bout.setBlockDataMode(false);
    bout.writeByte(120);
    writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
  }
  
  private void writeNonProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
    throws IOException
  {
    bout.writeByte(114);
    handles.assign(paramBoolean ? null : paramObjectStreamClass);
    if (protocol == 1) {
      paramObjectStreamClass.writeNonProxy(this);
    } else {
      writeClassDescriptor(paramObjectStreamClass);
    }
    Class localClass = paramObjectStreamClass.forClass();
    bout.setBlockDataMode(true);
    if ((localClass != null) && (isCustomSubclass())) {
      ReflectUtil.checkPackageAccess(localClass);
    }
    annotateClass(localClass);
    bout.setBlockDataMode(false);
    bout.writeByte(120);
    writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
  }
  
  private void writeString(String paramString, boolean paramBoolean)
    throws IOException
  {
    handles.assign(paramBoolean ? null : paramString);
    long l = bout.getUTFLength(paramString);
    if (l <= 65535L)
    {
      bout.writeByte(116);
      bout.writeUTF(paramString, l);
    }
    else
    {
      bout.writeByte(124);
      bout.writeLongUTF(paramString, l);
    }
  }
  
  private void writeArray(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
    throws IOException
  {
    bout.writeByte(117);
    writeClassDesc(paramObjectStreamClass, false);
    handles.assign(paramBoolean ? null : paramObject);
    Class localClass = paramObjectStreamClass.forClass().getComponentType();
    Object localObject1;
    if (localClass.isPrimitive())
    {
      if (localClass == Integer.TYPE)
      {
        localObject1 = (int[])paramObject;
        bout.writeInt(localObject1.length);
        bout.writeInts((int[])localObject1, 0, localObject1.length);
      }
      else if (localClass == Byte.TYPE)
      {
        localObject1 = (byte[])paramObject;
        bout.writeInt(localObject1.length);
        bout.write((byte[])localObject1, 0, localObject1.length, true);
      }
      else if (localClass == Long.TYPE)
      {
        localObject1 = (long[])paramObject;
        bout.writeInt(localObject1.length);
        bout.writeLongs((long[])localObject1, 0, localObject1.length);
      }
      else if (localClass == Float.TYPE)
      {
        localObject1 = (float[])paramObject;
        bout.writeInt(localObject1.length);
        bout.writeFloats((float[])localObject1, 0, localObject1.length);
      }
      else if (localClass == Double.TYPE)
      {
        localObject1 = (double[])paramObject;
        bout.writeInt(localObject1.length);
        bout.writeDoubles((double[])localObject1, 0, localObject1.length);
      }
      else if (localClass == Short.TYPE)
      {
        localObject1 = (short[])paramObject;
        bout.writeInt(localObject1.length);
        bout.writeShorts((short[])localObject1, 0, localObject1.length);
      }
      else if (localClass == Character.TYPE)
      {
        localObject1 = (char[])paramObject;
        bout.writeInt(localObject1.length);
        bout.writeChars((char[])localObject1, 0, localObject1.length);
      }
      else if (localClass == Boolean.TYPE)
      {
        localObject1 = (boolean[])paramObject;
        bout.writeInt(localObject1.length);
        bout.writeBooleans((boolean[])localObject1, 0, localObject1.length);
      }
      else
      {
        throw new InternalError();
      }
    }
    else
    {
      localObject1 = (Object[])paramObject;
      int i = localObject1.length;
      bout.writeInt(i);
      if (extendedDebugInfo) {
        debugInfoStack.push("array (class \"" + paramObject.getClass().getName() + "\", size: " + i + ")");
      }
      try
      {
        for (int j = 0; j < i; j++)
        {
          if (extendedDebugInfo) {
            debugInfoStack.push("element of array (index: " + j + ")");
          }
          try
          {
            writeObject0(localObject1[j], false);
          }
          finally {}
        }
      }
      finally
      {
        if (extendedDebugInfo) {
          debugInfoStack.pop();
        }
      }
    }
  }
  
  private void writeEnum(Enum<?> paramEnum, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
    throws IOException
  {
    bout.writeByte(126);
    ObjectStreamClass localObjectStreamClass = paramObjectStreamClass.getSuperDesc();
    writeClassDesc(localObjectStreamClass.forClass() == Enum.class ? paramObjectStreamClass : localObjectStreamClass, false);
    handles.assign(paramBoolean ? null : paramEnum);
    writeString(paramEnum.name(), false);
  }
  
  private void writeOrdinaryObject(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean)
    throws IOException
  {
    if (extendedDebugInfo) {
      debugInfoStack.push((depth == 1 ? "root " : "") + "object (class \"" + paramObject.getClass().getName() + "\", " + paramObject.toString() + ")");
    }
    try
    {
      paramObjectStreamClass.checkSerialize();
      bout.writeByte(115);
      writeClassDesc(paramObjectStreamClass, false);
      handles.assign(paramBoolean ? null : paramObject);
      if ((paramObjectStreamClass.isExternalizable()) && (!paramObjectStreamClass.isProxy())) {
        writeExternalData((Externalizable)paramObject);
      } else {
        writeSerialData(paramObject, paramObjectStreamClass);
      }
    }
    finally
    {
      if (extendedDebugInfo) {
        debugInfoStack.pop();
      }
    }
  }
  
  private void writeExternalData(Externalizable paramExternalizable)
    throws IOException
  {
    PutFieldImpl localPutFieldImpl = curPut;
    curPut = null;
    if (extendedDebugInfo) {
      debugInfoStack.push("writeExternal data");
    }
    SerialCallbackContext localSerialCallbackContext = curContext;
    try
    {
      curContext = null;
      if (protocol == 1)
      {
        paramExternalizable.writeExternal(this);
      }
      else
      {
        bout.setBlockDataMode(true);
        paramExternalizable.writeExternal(this);
        bout.setBlockDataMode(false);
        bout.writeByte(120);
      }
    }
    finally
    {
      curContext = localSerialCallbackContext;
      if (extendedDebugInfo) {
        debugInfoStack.pop();
      }
    }
    curPut = localPutFieldImpl;
  }
  
  private void writeSerialData(Object paramObject, ObjectStreamClass paramObjectStreamClass)
    throws IOException
  {
    ObjectStreamClass.ClassDataSlot[] arrayOfClassDataSlot = paramObjectStreamClass.getClassDataLayout();
    for (int i = 0; i < arrayOfClassDataSlot.length; i++)
    {
      ObjectStreamClass localObjectStreamClass = desc;
      if (localObjectStreamClass.hasWriteObjectMethod())
      {
        PutFieldImpl localPutFieldImpl = curPut;
        curPut = null;
        SerialCallbackContext localSerialCallbackContext = curContext;
        if (extendedDebugInfo) {
          debugInfoStack.push("custom writeObject data (class \"" + localObjectStreamClass.getName() + "\")");
        }
        try
        {
          curContext = new SerialCallbackContext(paramObject, localObjectStreamClass);
          bout.setBlockDataMode(true);
          localObjectStreamClass.invokeWriteObject(paramObject, this);
          bout.setBlockDataMode(false);
          bout.writeByte(120);
        }
        finally
        {
          curContext.setUsed();
          curContext = localSerialCallbackContext;
          if (extendedDebugInfo) {
            debugInfoStack.pop();
          }
        }
        curPut = localPutFieldImpl;
      }
      else
      {
        defaultWriteFields(paramObject, localObjectStreamClass);
      }
    }
  }
  
  private void defaultWriteFields(Object paramObject, ObjectStreamClass paramObjectStreamClass)
    throws IOException
  {
    Class localClass = paramObjectStreamClass.forClass();
    if ((localClass != null) && (paramObject != null) && (!localClass.isInstance(paramObject))) {
      throw new ClassCastException();
    }
    paramObjectStreamClass.checkDefaultSerialize();
    int i = paramObjectStreamClass.getPrimDataSize();
    if ((primVals == null) || (primVals.length < i)) {
      primVals = new byte[i];
    }
    paramObjectStreamClass.getPrimFieldValues(paramObject, primVals);
    bout.write(primVals, 0, i, false);
    ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields(false);
    Object[] arrayOfObject = new Object[paramObjectStreamClass.getNumObjFields()];
    int j = arrayOfObjectStreamField.length - arrayOfObject.length;
    paramObjectStreamClass.getObjFieldValues(paramObject, arrayOfObject);
    for (int k = 0; k < arrayOfObject.length; k++)
    {
      if (extendedDebugInfo) {
        debugInfoStack.push("field (class \"" + paramObjectStreamClass.getName() + "\", name: \"" + arrayOfObjectStreamField[(j + k)].getName() + "\", type: \"" + arrayOfObjectStreamField[(j + k)].getType() + "\")");
      }
      try
      {
        writeObject0(arrayOfObject[k], arrayOfObjectStreamField[(j + k)].isUnshared());
      }
      finally
      {
        if (extendedDebugInfo) {
          debugInfoStack.pop();
        }
      }
    }
  }
  
  /* Error */
  private void writeFatalException(IOException paramIOException)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 560	java/io/ObjectOutputStream:clear	()V
    //   4: aload_0
    //   5: getfield 538	java/io/ObjectOutputStream:bout	Ljava/io/ObjectOutputStream$BlockDataOutputStream;
    //   8: iconst_0
    //   9: invokevirtual 601	java/io/ObjectOutputStream$BlockDataOutputStream:setBlockDataMode	(Z)Z
    //   12: istore_2
    //   13: aload_0
    //   14: getfield 538	java/io/ObjectOutputStream:bout	Ljava/io/ObjectOutputStream$BlockDataOutputStream;
    //   17: bipush 123
    //   19: invokevirtual 595	java/io/ObjectOutputStream$BlockDataOutputStream:writeByte	(I)V
    //   22: aload_0
    //   23: aload_1
    //   24: iconst_0
    //   25: invokespecial 580	java/io/ObjectOutputStream:writeObject0	(Ljava/lang/Object;Z)V
    //   28: aload_0
    //   29: invokespecial 560	java/io/ObjectOutputStream:clear	()V
    //   32: aload_0
    //   33: getfield 538	java/io/ObjectOutputStream:bout	Ljava/io/ObjectOutputStream$BlockDataOutputStream;
    //   36: iload_2
    //   37: invokevirtual 601	java/io/ObjectOutputStream$BlockDataOutputStream:setBlockDataMode	(Z)Z
    //   40: pop
    //   41: goto +15 -> 56
    //   44: astore_3
    //   45: aload_0
    //   46: getfield 538	java/io/ObjectOutputStream:bout	Ljava/io/ObjectOutputStream$BlockDataOutputStream;
    //   49: iload_2
    //   50: invokevirtual 601	java/io/ObjectOutputStream$BlockDataOutputStream:setBlockDataMode	(Z)Z
    //   53: pop
    //   54: aload_3
    //   55: athrow
    //   56: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	ObjectOutputStream
    //   0	57	1	paramIOException	IOException
    //   12	38	2	bool	boolean
    //   44	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	32	44	finally
  }
  
  private static native void floatsToBytes(float[] paramArrayOfFloat, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static native void doublesToBytes(double[] paramArrayOfDouble, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static class BlockDataOutputStream
    extends OutputStream
    implements DataOutput
  {
    private static final int MAX_BLOCK_SIZE = 1024;
    private static final int MAX_HEADER_SIZE = 5;
    private static final int CHAR_BUF_SIZE = 256;
    private final byte[] buf = new byte['Ѐ'];
    private final byte[] hbuf = new byte[5];
    private final char[] cbuf = new char['Ā'];
    private boolean blkmode = false;
    private int pos = 0;
    private final OutputStream out;
    private final DataOutputStream dout;
    
    BlockDataOutputStream(OutputStream paramOutputStream)
    {
      out = paramOutputStream;
      dout = new DataOutputStream(this);
    }
    
    boolean setBlockDataMode(boolean paramBoolean)
      throws IOException
    {
      if (blkmode == paramBoolean) {
        return blkmode;
      }
      drain();
      blkmode = paramBoolean;
      return !blkmode;
    }
    
    boolean getBlockDataMode()
    {
      return blkmode;
    }
    
    public void write(int paramInt)
      throws IOException
    {
      if (pos >= 1024) {
        drain();
      }
      buf[(pos++)] = ((byte)paramInt);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      write(paramArrayOfByte, 0, paramArrayOfByte.length, false);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      write(paramArrayOfByte, paramInt1, paramInt2, false);
    }
    
    public void flush()
      throws IOException
    {
      drain();
      out.flush();
    }
    
    public void close()
      throws IOException
    {
      flush();
      out.close();
    }
    
    void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
      throws IOException
    {
      if ((!paramBoolean) && (!blkmode))
      {
        drain();
        out.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      while (paramInt2 > 0)
      {
        if (pos >= 1024) {
          drain();
        }
        if ((paramInt2 >= 1024) && (!paramBoolean) && (pos == 0))
        {
          writeBlockHeader(1024);
          out.write(paramArrayOfByte, paramInt1, 1024);
          paramInt1 += 1024;
          paramInt2 -= 1024;
        }
        else
        {
          int i = Math.min(paramInt2, 1024 - pos);
          System.arraycopy(paramArrayOfByte, paramInt1, buf, pos, i);
          pos += i;
          paramInt1 += i;
          paramInt2 -= i;
        }
      }
    }
    
    void drain()
      throws IOException
    {
      if (pos == 0) {
        return;
      }
      if (blkmode) {
        writeBlockHeader(pos);
      }
      out.write(buf, 0, pos);
      pos = 0;
    }
    
    private void writeBlockHeader(int paramInt)
      throws IOException
    {
      if (paramInt <= 255)
      {
        hbuf[0] = 119;
        hbuf[1] = ((byte)paramInt);
        out.write(hbuf, 0, 2);
      }
      else
      {
        hbuf[0] = 122;
        Bits.putInt(hbuf, 1, paramInt);
        out.write(hbuf, 0, 5);
      }
    }
    
    public void writeBoolean(boolean paramBoolean)
      throws IOException
    {
      if (pos >= 1024) {
        drain();
      }
      Bits.putBoolean(buf, pos++, paramBoolean);
    }
    
    public void writeByte(int paramInt)
      throws IOException
    {
      if (pos >= 1024) {
        drain();
      }
      buf[(pos++)] = ((byte)paramInt);
    }
    
    public void writeChar(int paramInt)
      throws IOException
    {
      if (pos + 2 <= 1024)
      {
        Bits.putChar(buf, pos, (char)paramInt);
        pos += 2;
      }
      else
      {
        dout.writeChar(paramInt);
      }
    }
    
    public void writeShort(int paramInt)
      throws IOException
    {
      if (pos + 2 <= 1024)
      {
        Bits.putShort(buf, pos, (short)paramInt);
        pos += 2;
      }
      else
      {
        dout.writeShort(paramInt);
      }
    }
    
    public void writeInt(int paramInt)
      throws IOException
    {
      if (pos + 4 <= 1024)
      {
        Bits.putInt(buf, pos, paramInt);
        pos += 4;
      }
      else
      {
        dout.writeInt(paramInt);
      }
    }
    
    public void writeFloat(float paramFloat)
      throws IOException
    {
      if (pos + 4 <= 1024)
      {
        Bits.putFloat(buf, pos, paramFloat);
        pos += 4;
      }
      else
      {
        dout.writeFloat(paramFloat);
      }
    }
    
    public void writeLong(long paramLong)
      throws IOException
    {
      if (pos + 8 <= 1024)
      {
        Bits.putLong(buf, pos, paramLong);
        pos += 8;
      }
      else
      {
        dout.writeLong(paramLong);
      }
    }
    
    public void writeDouble(double paramDouble)
      throws IOException
    {
      if (pos + 8 <= 1024)
      {
        Bits.putDouble(buf, pos, paramDouble);
        pos += 8;
      }
      else
      {
        dout.writeDouble(paramDouble);
      }
    }
    
    public void writeBytes(String paramString)
      throws IOException
    {
      int i = paramString.length();
      int j = 0;
      int k = 0;
      int m = 0;
      while (m < i)
      {
        if (j >= k)
        {
          j = 0;
          k = Math.min(i - m, 256);
          paramString.getChars(m, m + k, cbuf, 0);
        }
        if (pos >= 1024) {
          drain();
        }
        int n = Math.min(k - j, 1024 - pos);
        int i1 = pos + n;
        while (pos < i1) {
          buf[(pos++)] = ((byte)cbuf[(j++)]);
        }
        m += n;
      }
    }
    
    public void writeChars(String paramString)
      throws IOException
    {
      int i = paramString.length();
      int j = 0;
      while (j < i)
      {
        int k = Math.min(i - j, 256);
        paramString.getChars(j, j + k, cbuf, 0);
        writeChars(cbuf, 0, k);
        j += k;
      }
    }
    
    public void writeUTF(String paramString)
      throws IOException
    {
      writeUTF(paramString, getUTFLength(paramString));
    }
    
    void writeBooleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt1 + paramInt2;
      while (paramInt1 < i)
      {
        if (pos >= 1024) {
          drain();
        }
        int j = Math.min(i, paramInt1 + (1024 - pos));
        while (paramInt1 < j) {
          Bits.putBoolean(buf, pos++, paramArrayOfBoolean[(paramInt1++)]);
        }
      }
    }
    
    void writeChars(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = 1022;
      int j = paramInt1 + paramInt2;
      while (paramInt1 < j) {
        if (pos <= i)
        {
          int k = 1024 - pos >> 1;
          int m = Math.min(j, paramInt1 + k);
          while (paramInt1 < m)
          {
            Bits.putChar(buf, pos, paramArrayOfChar[(paramInt1++)]);
            pos += 2;
          }
        }
        else
        {
          dout.writeChar(paramArrayOfChar[(paramInt1++)]);
        }
      }
    }
    
    void writeShorts(short[] paramArrayOfShort, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = 1022;
      int j = paramInt1 + paramInt2;
      while (paramInt1 < j) {
        if (pos <= i)
        {
          int k = 1024 - pos >> 1;
          int m = Math.min(j, paramInt1 + k);
          while (paramInt1 < m)
          {
            Bits.putShort(buf, pos, paramArrayOfShort[(paramInt1++)]);
            pos += 2;
          }
        }
        else
        {
          dout.writeShort(paramArrayOfShort[(paramInt1++)]);
        }
      }
    }
    
    void writeInts(int[] paramArrayOfInt, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = 1020;
      int j = paramInt1 + paramInt2;
      while (paramInt1 < j) {
        if (pos <= i)
        {
          int k = 1024 - pos >> 2;
          int m = Math.min(j, paramInt1 + k);
          while (paramInt1 < m)
          {
            Bits.putInt(buf, pos, paramArrayOfInt[(paramInt1++)]);
            pos += 4;
          }
        }
        else
        {
          dout.writeInt(paramArrayOfInt[(paramInt1++)]);
        }
      }
    }
    
    void writeFloats(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = 1020;
      int j = paramInt1 + paramInt2;
      while (paramInt1 < j) {
        if (pos <= i)
        {
          int k = 1024 - pos >> 2;
          int m = Math.min(j - paramInt1, k);
          ObjectOutputStream.floatsToBytes(paramArrayOfFloat, paramInt1, buf, pos, m);
          paramInt1 += m;
          pos += (m << 2);
        }
        else
        {
          dout.writeFloat(paramArrayOfFloat[(paramInt1++)]);
        }
      }
    }
    
    void writeLongs(long[] paramArrayOfLong, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = 1016;
      int j = paramInt1 + paramInt2;
      while (paramInt1 < j) {
        if (pos <= i)
        {
          int k = 1024 - pos >> 3;
          int m = Math.min(j, paramInt1 + k);
          while (paramInt1 < m)
          {
            Bits.putLong(buf, pos, paramArrayOfLong[(paramInt1++)]);
            pos += 8;
          }
        }
        else
        {
          dout.writeLong(paramArrayOfLong[(paramInt1++)]);
        }
      }
    }
    
    void writeDoubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = 1016;
      int j = paramInt1 + paramInt2;
      while (paramInt1 < j) {
        if (pos <= i)
        {
          int k = 1024 - pos >> 3;
          int m = Math.min(j - paramInt1, k);
          ObjectOutputStream.doublesToBytes(paramArrayOfDouble, paramInt1, buf, pos, m);
          paramInt1 += m;
          pos += (m << 3);
        }
        else
        {
          dout.writeDouble(paramArrayOfDouble[(paramInt1++)]);
        }
      }
    }
    
    long getUTFLength(String paramString)
    {
      int i = paramString.length();
      long l = 0L;
      int j = 0;
      while (j < i)
      {
        int k = Math.min(i - j, 256);
        paramString.getChars(j, j + k, cbuf, 0);
        for (int m = 0; m < k; m++)
        {
          int n = cbuf[m];
          if ((n >= 1) && (n <= 127)) {
            l += 1L;
          } else if (n > 2047) {
            l += 3L;
          } else {
            l += 2L;
          }
        }
        j += k;
      }
      return l;
    }
    
    void writeUTF(String paramString, long paramLong)
      throws IOException
    {
      if (paramLong > 65535L) {
        throw new UTFDataFormatException();
      }
      writeShort((int)paramLong);
      if (paramLong == paramString.length()) {
        writeBytes(paramString);
      } else {
        writeUTFBody(paramString);
      }
    }
    
    void writeLongUTF(String paramString)
      throws IOException
    {
      writeLongUTF(paramString, getUTFLength(paramString));
    }
    
    void writeLongUTF(String paramString, long paramLong)
      throws IOException
    {
      writeLong(paramLong);
      if (paramLong == paramString.length()) {
        writeBytes(paramString);
      } else {
        writeUTFBody(paramString);
      }
    }
    
    private void writeUTFBody(String paramString)
      throws IOException
    {
      int i = 1021;
      int j = paramString.length();
      int k = 0;
      while (k < j)
      {
        int m = Math.min(j - k, 256);
        paramString.getChars(k, k + m, cbuf, 0);
        for (int n = 0; n < m; n++)
        {
          int i1 = cbuf[n];
          if (pos <= i)
          {
            if ((i1 <= 127) && (i1 != 0))
            {
              buf[(pos++)] = ((byte)i1);
            }
            else if (i1 > 2047)
            {
              buf[(pos + 2)] = ((byte)(0x80 | i1 >> 0 & 0x3F));
              buf[(pos + 1)] = ((byte)(0x80 | i1 >> 6 & 0x3F));
              buf[(pos + 0)] = ((byte)(0xE0 | i1 >> 12 & 0xF));
              pos += 3;
            }
            else
            {
              buf[(pos + 1)] = ((byte)(0x80 | i1 >> 0 & 0x3F));
              buf[(pos + 0)] = ((byte)(0xC0 | i1 >> 6 & 0x1F));
              pos += 2;
            }
          }
          else if ((i1 <= 127) && (i1 != 0))
          {
            write(i1);
          }
          else if (i1 > 2047)
          {
            write(0xE0 | i1 >> 12 & 0xF);
            write(0x80 | i1 >> 6 & 0x3F);
            write(0x80 | i1 >> 0 & 0x3F);
          }
          else
          {
            write(0xC0 | i1 >> 6 & 0x1F);
            write(0x80 | i1 >> 0 & 0x3F);
          }
        }
        k += m;
      }
    }
  }
  
  private static class Caches
  {
    static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
    static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
    
    private Caches() {}
  }
  
  private static class DebugTraceInfoStack
  {
    private final List<String> stack = new ArrayList();
    
    DebugTraceInfoStack() {}
    
    void clear()
    {
      stack.clear();
    }
    
    void pop()
    {
      stack.remove(stack.size() - 1);
    }
    
    void push(String paramString)
    {
      stack.add("\t- " + paramString);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (!stack.isEmpty()) {
        for (int i = stack.size(); i > 0; i--) {
          localStringBuilder.append((String)stack.get(i - 1) + (i != 1 ? "\n" : ""));
        }
      }
      return localStringBuilder.toString();
    }
  }
  
  private static class HandleTable
  {
    private int size;
    private int threshold;
    private final float loadFactor;
    private int[] spine;
    private int[] next;
    private Object[] objs;
    
    HandleTable(int paramInt, float paramFloat)
    {
      loadFactor = paramFloat;
      spine = new int[paramInt];
      next = new int[paramInt];
      objs = new Object[paramInt];
      threshold = ((int)(paramInt * paramFloat));
      clear();
    }
    
    int assign(Object paramObject)
    {
      if (size >= next.length) {
        growEntries();
      }
      if (size >= threshold) {
        growSpine();
      }
      insert(paramObject, size);
      return size++;
    }
    
    int lookup(Object paramObject)
    {
      if (size == 0) {
        return -1;
      }
      int i = hash(paramObject) % spine.length;
      for (int j = spine[i]; j >= 0; j = next[j]) {
        if (objs[j] == paramObject) {
          return j;
        }
      }
      return -1;
    }
    
    void clear()
    {
      Arrays.fill(spine, -1);
      Arrays.fill(objs, 0, size, null);
      size = 0;
    }
    
    int size()
    {
      return size;
    }
    
    private void insert(Object paramObject, int paramInt)
    {
      int i = hash(paramObject) % spine.length;
      objs[paramInt] = paramObject;
      next[paramInt] = spine[i];
      spine[i] = paramInt;
    }
    
    private void growSpine()
    {
      spine = new int[(spine.length << 1) + 1];
      threshold = ((int)(spine.length * loadFactor));
      Arrays.fill(spine, -1);
      for (int i = 0; i < size; i++) {
        insert(objs[i], i);
      }
    }
    
    private void growEntries()
    {
      int i = (next.length << 1) + 1;
      int[] arrayOfInt = new int[i];
      System.arraycopy(next, 0, arrayOfInt, 0, size);
      next = arrayOfInt;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(objs, 0, arrayOfObject, 0, size);
      objs = arrayOfObject;
    }
    
    private int hash(Object paramObject)
    {
      return System.identityHashCode(paramObject) & 0x7FFFFFFF;
    }
  }
  
  public static abstract class PutField
  {
    public PutField() {}
    
    public abstract void put(String paramString, boolean paramBoolean);
    
    public abstract void put(String paramString, byte paramByte);
    
    public abstract void put(String paramString, char paramChar);
    
    public abstract void put(String paramString, short paramShort);
    
    public abstract void put(String paramString, int paramInt);
    
    public abstract void put(String paramString, long paramLong);
    
    public abstract void put(String paramString, float paramFloat);
    
    public abstract void put(String paramString, double paramDouble);
    
    public abstract void put(String paramString, Object paramObject);
    
    @Deprecated
    public abstract void write(ObjectOutput paramObjectOutput)
      throws IOException;
  }
  
  private class PutFieldImpl
    extends ObjectOutputStream.PutField
  {
    private final ObjectStreamClass desc;
    private final byte[] primVals;
    private final Object[] objVals;
    
    PutFieldImpl(ObjectStreamClass paramObjectStreamClass)
    {
      desc = paramObjectStreamClass;
      primVals = new byte[paramObjectStreamClass.getPrimDataSize()];
      objVals = new Object[paramObjectStreamClass.getNumObjFields()];
    }
    
    public void put(String paramString, boolean paramBoolean)
    {
      Bits.putBoolean(primVals, getFieldOffset(paramString, Boolean.TYPE), paramBoolean);
    }
    
    public void put(String paramString, byte paramByte)
    {
      primVals[getFieldOffset(paramString, Byte.TYPE)] = paramByte;
    }
    
    public void put(String paramString, char paramChar)
    {
      Bits.putChar(primVals, getFieldOffset(paramString, Character.TYPE), paramChar);
    }
    
    public void put(String paramString, short paramShort)
    {
      Bits.putShort(primVals, getFieldOffset(paramString, Short.TYPE), paramShort);
    }
    
    public void put(String paramString, int paramInt)
    {
      Bits.putInt(primVals, getFieldOffset(paramString, Integer.TYPE), paramInt);
    }
    
    public void put(String paramString, float paramFloat)
    {
      Bits.putFloat(primVals, getFieldOffset(paramString, Float.TYPE), paramFloat);
    }
    
    public void put(String paramString, long paramLong)
    {
      Bits.putLong(primVals, getFieldOffset(paramString, Long.TYPE), paramLong);
    }
    
    public void put(String paramString, double paramDouble)
    {
      Bits.putDouble(primVals, getFieldOffset(paramString, Double.TYPE), paramDouble);
    }
    
    public void put(String paramString, Object paramObject)
    {
      objVals[getFieldOffset(paramString, Object.class)] = paramObject;
    }
    
    public void write(ObjectOutput paramObjectOutput)
      throws IOException
    {
      if (ObjectOutputStream.this != paramObjectOutput) {
        throw new IllegalArgumentException("wrong stream");
      }
      paramObjectOutput.write(primVals, 0, primVals.length);
      ObjectStreamField[] arrayOfObjectStreamField = desc.getFields(false);
      int i = arrayOfObjectStreamField.length - objVals.length;
      for (int j = 0; j < objVals.length; j++)
      {
        if (arrayOfObjectStreamField[(i + j)].isUnshared()) {
          throw new IOException("cannot write unshared object");
        }
        paramObjectOutput.writeObject(objVals[j]);
      }
    }
    
    void writeFields()
      throws IOException
    {
      bout.write(primVals, 0, primVals.length, false);
      ObjectStreamField[] arrayOfObjectStreamField = desc.getFields(false);
      int i = arrayOfObjectStreamField.length - objVals.length;
      for (int j = 0; j < objVals.length; j++)
      {
        if (ObjectOutputStream.extendedDebugInfo) {
          debugInfoStack.push("field (class \"" + desc.getName() + "\", name: \"" + arrayOfObjectStreamField[(i + j)].getName() + "\", type: \"" + arrayOfObjectStreamField[(i + j)].getType() + "\")");
        }
        try
        {
          ObjectOutputStream.this.writeObject0(objVals[j], arrayOfObjectStreamField[(i + j)].isUnshared());
        }
        finally
        {
          if (ObjectOutputStream.extendedDebugInfo) {
            debugInfoStack.pop();
          }
        }
      }
    }
    
    private int getFieldOffset(String paramString, Class<?> paramClass)
    {
      ObjectStreamField localObjectStreamField = desc.getField(paramString, paramClass);
      if (localObjectStreamField == null) {
        throw new IllegalArgumentException("no such field " + paramString + " with type " + paramClass);
      }
      return localObjectStreamField.getOffset();
    }
  }
  
  private static class ReplaceTable
  {
    private final ObjectOutputStream.HandleTable htab;
    private Object[] reps;
    
    ReplaceTable(int paramInt, float paramFloat)
    {
      htab = new ObjectOutputStream.HandleTable(paramInt, paramFloat);
      reps = new Object[paramInt];
    }
    
    void assign(Object paramObject1, Object paramObject2)
    {
      int i = htab.assign(paramObject1);
      while (i >= reps.length) {
        grow();
      }
      reps[i] = paramObject2;
    }
    
    Object lookup(Object paramObject)
    {
      int i = htab.lookup(paramObject);
      return i >= 0 ? reps[i] : paramObject;
    }
    
    void clear()
    {
      Arrays.fill(reps, 0, htab.size(), null);
      htab.clear();
    }
    
    int size()
    {
      return htab.size();
    }
    
    private void grow()
    {
      Object[] arrayOfObject = new Object[(reps.length << 1) + 1];
      System.arraycopy(reps, 0, arrayOfObject, 0, reps.length);
      reps = arrayOfObject;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\ObjectOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */