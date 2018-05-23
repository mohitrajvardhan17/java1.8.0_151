package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.Utility;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.ValueInputStream;
import sun.corba.Bridge;

public class IIOPInputStream
  extends InputStreamHook
{
  private static Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Object run()
    {
      return Bridge.get();
    }
  });
  private static OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
  private static UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  private ValueMember[] defaultReadObjectFVDMembers = null;
  private org.omg.CORBA_2_3.portable.InputStream orbStream;
  private CodeBase cbSender;
  private ValueHandlerImpl vhandler;
  private Object currentObject = null;
  private ObjectStreamClass currentClassDesc = null;
  private Class currentClass = null;
  private int recursionDepth = 0;
  private int simpleReadDepth = 0;
  ActiveRecursionManager activeRecursionMgr = new ActiveRecursionManager();
  private IOException abortIOException = null;
  private ClassNotFoundException abortClassNotFoundException = null;
  private Vector callbacks;
  ObjectStreamClass[] classdesc;
  Class[] classes;
  int spClass;
  private static final String kEmptyStr = "";
  public static final TypeCode kRemoteTypeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
  public static final TypeCode kValueTypeCode = ORB.init().get_primitive_tc(TCKind.tk_value);
  private static final boolean useFVDOnly = false;
  private byte streamFormatVersion;
  private static final Constructor OPT_DATA_EXCEPTION_CTOR = getOptDataExceptionCtor();
  private Object[] readObjectArgList = { this };
  
  private static Constructor getOptDataExceptionCtor()
  {
    try
    {
      Constructor localConstructor = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws NoSuchMethodException, SecurityException
        {
          Constructor localConstructor = OptionalDataException.class.getDeclaredConstructor(new Class[] { Boolean.TYPE });
          localConstructor.setAccessible(true);
          return localConstructor;
        }
      });
      if (localConstructor == null) {
        throw new Error("Unable to find OptionalDataException constructor");
      }
      return localConstructor;
    }
    catch (Exception localException)
    {
      throw new ExceptionInInitializerError(localException);
    }
  }
  
  private OptionalDataException createOptionalDataException()
  {
    try
    {
      OptionalDataException localOptionalDataException = (OptionalDataException)OPT_DATA_EXCEPTION_CTOR.newInstance(new Object[] { Boolean.TRUE });
      if (localOptionalDataException == null) {
        throw new Error("Created null OptionalDataException");
      }
      return localOptionalDataException;
    }
    catch (Exception localException)
    {
      throw new Error("Couldn't create OptionalDataException", localException);
    }
  }
  
  protected byte getStreamFormatVersion()
  {
    return streamFormatVersion;
  }
  
  private void readFormatVersion()
    throws IOException
  {
    streamFormatVersion = orbStream.read_octet();
    Object localObject;
    IOException localIOException;
    if ((streamFormatVersion < 1) || (streamFormatVersion > vhandler.getMaximumStreamFormatVersion()))
    {
      localObject = omgWrapper.unsupportedFormatVersion(CompletionStatus.COMPLETED_MAYBE);
      localIOException = new IOException("Unsupported format version: " + streamFormatVersion);
      localIOException.initCause((Throwable)localObject);
      throw localIOException;
    }
    if ((streamFormatVersion == 2) && (!(orbStream instanceof ValueInputStream)))
    {
      localObject = omgWrapper.notAValueinputstream(CompletionStatus.COMPLETED_MAYBE);
      localIOException = new IOException("Not a ValueInputStream");
      localIOException.initCause((Throwable)localObject);
      throw localIOException;
    }
  }
  
  public static void setTestFVDFlag(boolean paramBoolean) {}
  
  public IIOPInputStream()
    throws IOException
  {
    resetStream();
  }
  
  final void setOrbStream(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
  {
    orbStream = paramInputStream;
  }
  
  final org.omg.CORBA_2_3.portable.InputStream getOrbStream()
  {
    return orbStream;
  }
  
  public final void setSender(CodeBase paramCodeBase)
  {
    cbSender = paramCodeBase;
  }
  
  public final CodeBase getSender()
  {
    return cbSender;
  }
  
  public final void setValueHandler(ValueHandler paramValueHandler)
  {
    vhandler = ((ValueHandlerImpl)paramValueHandler);
  }
  
  public final ValueHandler getValueHandler()
  {
    return vhandler;
  }
  
  final void increaseRecursionDepth()
  {
    recursionDepth += 1;
  }
  
  final int decreaseRecursionDepth()
  {
    return --recursionDepth;
  }
  
  public final synchronized Object readObjectDelegate()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_abstract_interface();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, true);
      throw localMARSHAL;
    }
    catch (IndirectionException localIndirectionException)
    {
      return activeRecursionMgr.getObject(offset);
    }
  }
  
  final synchronized Object simpleReadObject(Class paramClass, String paramString, CodeBase paramCodeBase, int paramInt)
  {
    Object localObject1 = currentObject;
    ObjectStreamClass localObjectStreamClass = currentClassDesc;
    Class localClass = currentClass;
    byte b = streamFormatVersion;
    simpleReadDepth += 1;
    Object localObject2 = null;
    try
    {
      if (vhandler.useFullValueDescription(paramClass, paramString)) {
        localObject2 = inputObjectUsingFVD(paramClass, paramString, paramCodeBase, paramInt);
      } else {
        localObject2 = inputObject(paramClass, paramString, paramCodeBase, paramInt);
      }
      localObject2 = currentClassDesc.readResolve(localObject2);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      bridge.throwException(localClassNotFoundException);
      localObject3 = null;
      return localObject3;
    }
    catch (IOException localIOException1)
    {
      bridge.throwException(localIOException1);
      localObject3 = null;
      return localObject3;
    }
    finally
    {
      simpleReadDepth -= 1;
      currentObject = localObject1;
      currentClassDesc = localObjectStreamClass;
      currentClass = localClass;
      streamFormatVersion = b;
    }
    IOException localIOException2 = abortIOException;
    if (simpleReadDepth == 0) {
      abortIOException = null;
    }
    if (localIOException2 != null)
    {
      bridge.throwException(localIOException2);
      return null;
    }
    Object localObject3 = abortClassNotFoundException;
    if (simpleReadDepth == 0) {
      abortClassNotFoundException = null;
    }
    if (localObject3 != null)
    {
      bridge.throwException((Throwable)localObject3);
      return null;
    }
    return localObject2;
  }
  
  public final synchronized void simpleSkipObject(String paramString, CodeBase paramCodeBase)
  {
    Object localObject1 = currentObject;
    ObjectStreamClass localObjectStreamClass = currentClassDesc;
    Class localClass = currentClass;
    byte b = streamFormatVersion;
    simpleReadDepth += 1;
    Object localObject2 = null;
    try
    {
      skipObjectUsingFVD(paramString, paramCodeBase);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      bridge.throwException(localClassNotFoundException1);
      return;
    }
    catch (IOException localIOException1)
    {
      bridge.throwException(localIOException1);
      return;
    }
    finally
    {
      simpleReadDepth -= 1;
      streamFormatVersion = b;
      currentObject = localObject1;
      currentClassDesc = localObjectStreamClass;
      currentClass = localClass;
    }
    IOException localIOException2 = abortIOException;
    if (simpleReadDepth == 0) {
      abortIOException = null;
    }
    if (localIOException2 != null)
    {
      bridge.throwException(localIOException2);
      return;
    }
    ClassNotFoundException localClassNotFoundException2 = abortClassNotFoundException;
    if (simpleReadDepth == 0) {
      abortClassNotFoundException = null;
    }
    if (localClassNotFoundException2 != null)
    {
      bridge.throwException(localClassNotFoundException2);
      return;
    }
  }
  
  protected final Object readObjectOverride()
    throws OptionalDataException, ClassNotFoundException, IOException
  {
    return readObjectDelegate();
  }
  
  final synchronized void defaultReadObjectDelegate()
  {
    try
    {
      if ((currentObject == null) || (currentClassDesc == null)) {
        throw new NotActiveException("defaultReadObjectDelegate");
      }
      if (!currentClassDesc.forClass().isAssignableFrom(currentObject.getClass())) {
        throw new IOException("Object Type mismatch");
      }
      if ((defaultReadObjectFVDMembers != null) && (defaultReadObjectFVDMembers.length > 0))
      {
        inputClassFields(currentObject, currentClass, currentClassDesc, defaultReadObjectFVDMembers, cbSender);
      }
      else
      {
        ObjectStreamField[] arrayOfObjectStreamField = currentClassDesc.getFieldsNoCopy();
        if (arrayOfObjectStreamField.length > 0) {
          inputClassFields(currentObject, currentClass, arrayOfObjectStreamField, cbSender);
        }
      }
    }
    catch (NotActiveException localNotActiveException)
    {
      bridge.throwException(localNotActiveException);
    }
    catch (IOException localIOException)
    {
      bridge.throwException(localIOException);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      bridge.throwException(localClassNotFoundException);
    }
  }
  
  public final boolean enableResolveObjectDelegate(boolean paramBoolean)
  {
    return false;
  }
  
  public final void mark(int paramInt)
  {
    orbStream.mark(paramInt);
  }
  
  public final boolean markSupported()
  {
    return orbStream.markSupported();
  }
  
  public final void reset()
    throws IOException
  {
    try
    {
      orbStream.reset();
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final int available()
    throws IOException
  {
    return 0;
  }
  
  public final void close()
    throws IOException
  {}
  
  public final int read()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_octet() << 0 & 0xFF;
    }
    catch (MARSHAL localMARSHAL)
    {
      if (minor == 1330446344)
      {
        setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
        return -1;
      }
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      orbStream.read_octet_array(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt2;
    }
    catch (MARSHAL localMARSHAL)
    {
      if (minor == 1330446344)
      {
        setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
        return -1;
      }
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final boolean readBoolean()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_boolean();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final byte readByte()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_octet();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final char readChar()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_wchar();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final double readDouble()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_double();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final float readFloat()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_float();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void readFully(byte[] paramArrayOfByte)
    throws IOException
  {
    readFully(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      orbStream.read_octet_array(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final int readInt()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_long();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final String readLine()
    throws IOException
  {
    throw new IOException("Method readLine not supported");
  }
  
  public final long readLong()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_longlong();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final short readShort()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_short();
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  protected final void readStreamHeader()
    throws IOException, StreamCorruptedException
  {}
  
  public final int readUnsignedByte()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_octet() << 0 & 0xFF;
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final int readUnsignedShort()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return orbStream.read_ushort() << 0 & 0xFFFF;
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  protected String internalReadUTF(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    return paramInputStream.read_wstring();
  }
  
  public final String readUTF()
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      return internalReadUTF(orbStream);
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  private void handleOptionalDataMarshalException(MARSHAL paramMARSHAL, boolean paramBoolean)
    throws IOException
  {
    if (minor == 1330446344)
    {
      Object localObject;
      if (!paramBoolean) {
        localObject = new EOFException("No more optional data");
      } else {
        localObject = createOptionalDataException();
      }
      ((IOException)localObject).initCause(paramMARSHAL);
      setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
      throw ((Throwable)localObject);
    }
  }
  
  public final synchronized void registerValidation(ObjectInputValidation paramObjectInputValidation, int paramInt)
    throws NotActiveException, InvalidObjectException
  {
    throw new Error("Method registerValidation not supported");
  }
  
  protected final Class resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    throw new IOException("Method resolveClass not supported");
  }
  
  protected final Object resolveObject(Object paramObject)
    throws IOException
  {
    throw new IOException("Method resolveObject not supported");
  }
  
  public final int skipBytes(int paramInt)
    throws IOException
  {
    try
    {
      readObjectState.readData(this);
      byte[] arrayOfByte = new byte[paramInt];
      orbStream.read_octet_array(arrayOfByte, 0, paramInt);
      return paramInt;
    }
    catch (MARSHAL localMARSHAL)
    {
      handleOptionalDataMarshalException(localMARSHAL, false);
      throw localMARSHAL;
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  private synchronized Object inputObject(Class paramClass, String paramString, CodeBase paramCodeBase, int paramInt)
    throws IOException, ClassNotFoundException
  {
    currentClassDesc = ObjectStreamClass.lookup(paramClass);
    currentClass = currentClassDesc.forClass();
    if (currentClass == null) {
      throw new ClassNotFoundException(currentClassDesc.getName());
    }
    try
    {
      Object localObject1;
      if (Enum.class.isAssignableFrom(paramClass))
      {
        int i = orbStream.read_long();
        localObject1 = (String)orbStream.read_value(String.class);
        Enum localEnum = Enum.valueOf(paramClass, (String)localObject1);
        return localEnum;
      }
      if (currentClassDesc.isExternalizable())
      {
        try
        {
          currentObject = (currentClass == null ? null : currentClassDesc.newInstance());
          if (currentObject != null)
          {
            activeRecursionMgr.addObject(paramInt, currentObject);
            readFormatVersion();
            Externalizable localExternalizable = (Externalizable)currentObject;
            localExternalizable.readExternal(this);
          }
        }
        catch (InvocationTargetException localInvocationTargetException1)
        {
          localObject1 = new InvalidClassException(currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          ((InvalidClassException)localObject1).initCause(localInvocationTargetException1);
          throw ((Throwable)localObject1);
        }
        catch (UnsupportedOperationException localUnsupportedOperationException1)
        {
          localObject1 = new InvalidClassException(currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          ((InvalidClassException)localObject1).initCause(localUnsupportedOperationException1);
          throw ((Throwable)localObject1);
        }
        catch (InstantiationException localInstantiationException1)
        {
          localObject1 = new InvalidClassException(currentClass.getName(), "InstantiationException accessing no-arg constructor");
          ((InvalidClassException)localObject1).initCause(localInstantiationException1);
          throw ((Throwable)localObject1);
        }
      }
      else
      {
        ObjectStreamClass localObjectStreamClass = currentClassDesc;
        localObject1 = currentClass;
        int j = spClass;
        Object localObject2;
        if (currentClass.getName().equals("java.lang.String"))
        {
          localObject2 = readUTF();
          return localObject2;
        }
        localObjectStreamClass = currentClassDesc;
        localObject1 = currentClass;
        Object localObject4;
        while ((localObjectStreamClass != null) && (localObjectStreamClass.isSerializable()))
        {
          localObject2 = localObjectStreamClass.forClass();
          for (localObject4 = localObject1; (localObject4 != null) && (localObject2 != localObject4); localObject4 = ((Class)localObject4).getSuperclass()) {}
          spClass += 1;
          if (spClass >= classes.length)
          {
            int k = classes.length * 2;
            Class[] arrayOfClass = new Class[k];
            ObjectStreamClass[] arrayOfObjectStreamClass = new ObjectStreamClass[k];
            System.arraycopy(classes, 0, arrayOfClass, 0, classes.length);
            System.arraycopy(classdesc, 0, arrayOfObjectStreamClass, 0, classes.length);
            classes = arrayOfClass;
            classdesc = arrayOfObjectStreamClass;
          }
          if (localObject4 == null)
          {
            classdesc[spClass] = localObjectStreamClass;
            classes[spClass] = null;
          }
          else
          {
            classdesc[spClass] = localObjectStreamClass;
            classes[spClass] = localObject4;
            localObject1 = ((Class)localObject4).getSuperclass();
          }
          localObjectStreamClass = localObjectStreamClass.getSuperclass();
        }
        try
        {
          currentObject = (currentClass == null ? null : currentClassDesc.newInstance());
          activeRecursionMgr.addObject(paramInt, currentObject);
        }
        catch (InvocationTargetException localInvocationTargetException2)
        {
          localObject4 = new InvalidClassException(currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          ((InvalidClassException)localObject4).initCause(localInvocationTargetException2);
          throw ((Throwable)localObject4);
        }
        catch (UnsupportedOperationException localUnsupportedOperationException2)
        {
          localObject4 = new InvalidClassException(currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          ((InvalidClassException)localObject4).initCause(localUnsupportedOperationException2);
          throw ((Throwable)localObject4);
        }
        catch (InstantiationException localInstantiationException2)
        {
          localObject4 = new InvalidClassException(currentClass.getName(), "InstantiationException accessing no-arg constructor");
          ((InvalidClassException)localObject4).initCause(localInstantiationException2);
          throw ((Throwable)localObject4);
        }
        try
        {
          for (spClass = spClass; spClass > j; spClass -= 1)
          {
            currentClassDesc = classdesc[spClass];
            currentClass = classes[spClass];
            Object localObject3;
            if (classes[spClass] != null)
            {
              localObject3 = readObjectState;
              setState(DEFAULT_STATE);
              try
              {
                if (currentClassDesc.hasWriteObject())
                {
                  readFormatVersion();
                  boolean bool = readBoolean();
                  readObjectState.beginUnmarshalCustomValue(this, bool, currentClassDesc.readObjectMethod != null);
                }
                else if (currentClassDesc.hasReadObject())
                {
                  setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                }
                if ((!invokeObjectReader(currentClassDesc, currentObject, currentClass)) || (readObjectState == IN_READ_OBJECT_DEFAULTS_SENT))
                {
                  ObjectStreamField[] arrayOfObjectStreamField = currentClassDesc.getFieldsNoCopy();
                  if (arrayOfObjectStreamField.length > 0) {
                    inputClassFields(currentObject, currentClass, arrayOfObjectStreamField, paramCodeBase);
                  }
                }
                if (currentClassDesc.hasWriteObject()) {
                  readObjectState.endUnmarshalCustomValue(this);
                }
              }
              finally
              {
                setState((InputStreamHook.ReadObjectState)localObject3);
              }
            }
            else
            {
              localObject3 = currentClassDesc.getFieldsNoCopy();
              if (localObject3.length > 0) {
                inputClassFields(null, currentClass, (ObjectStreamField[])localObject3, paramCodeBase);
              }
            }
          }
        }
        finally
        {
          spClass = j;
        }
      }
    }
    finally
    {
      activeRecursionMgr.removeObject(paramInt);
    }
    return currentObject;
  }
  
  private Vector getOrderedDescriptions(String paramString, CodeBase paramCodeBase)
  {
    Vector localVector = new Vector();
    if (paramCodeBase == null) {
      return localVector;
    }
    FullValueDescription localFullValueDescription = paramCodeBase.meta(paramString);
    while (localFullValueDescription != null)
    {
      localVector.insertElementAt(localFullValueDescription, 0);
      if ((base_value != null) && (!"".equals(base_value))) {
        localFullValueDescription = paramCodeBase.meta(base_value);
      } else {
        return localVector;
      }
    }
    return localVector;
  }
  
  private synchronized Object inputObjectUsingFVD(Class paramClass, String paramString, CodeBase paramCodeBase, int paramInt)
    throws IOException, ClassNotFoundException
  {
    int i = spClass;
    try
    {
      ObjectStreamClass localObjectStreamClass = currentClassDesc = ObjectStreamClass.lookup(paramClass);
      Class localClass1 = currentClass = paramClass;
      Object localObject2;
      if (currentClassDesc.isExternalizable())
      {
        try
        {
          currentObject = (currentClass == null ? null : currentClassDesc.newInstance());
          if (currentObject != null)
          {
            activeRecursionMgr.addObject(paramInt, currentObject);
            readFormatVersion();
            Externalizable localExternalizable = (Externalizable)currentObject;
            localExternalizable.readExternal(this);
          }
        }
        catch (InvocationTargetException localInvocationTargetException1)
        {
          localObject2 = new InvalidClassException(currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          ((InvalidClassException)localObject2).initCause(localInvocationTargetException1);
          throw ((Throwable)localObject2);
        }
        catch (UnsupportedOperationException localUnsupportedOperationException1)
        {
          localObject2 = new InvalidClassException(currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          ((InvalidClassException)localObject2).initCause(localUnsupportedOperationException1);
          throw ((Throwable)localObject2);
        }
        catch (InstantiationException localInstantiationException1)
        {
          localObject2 = new InvalidClassException(currentClass.getName(), "InstantiationException accessing no-arg constructor");
          ((InvalidClassException)localObject2).initCause(localInstantiationException1);
          throw ((Throwable)localObject2);
        }
      }
      else
      {
        localObjectStreamClass = currentClassDesc;
        localClass1 = currentClass;
        Object localObject3;
        while ((localObjectStreamClass != null) && (localObjectStreamClass.isSerializable()))
        {
          Class localClass2 = localObjectStreamClass.forClass();
          for (localObject2 = localClass1; (localObject2 != null) && (localClass2 != localObject2); localObject2 = ((Class)localObject2).getSuperclass()) {}
          spClass += 1;
          if (spClass >= classes.length)
          {
            int j = classes.length * 2;
            localObject3 = new Class[j];
            ObjectStreamClass[] arrayOfObjectStreamClass = new ObjectStreamClass[j];
            System.arraycopy(classes, 0, localObject3, 0, classes.length);
            System.arraycopy(classdesc, 0, arrayOfObjectStreamClass, 0, classes.length);
            classes = ((Class[])localObject3);
            classdesc = arrayOfObjectStreamClass;
          }
          if (localObject2 == null)
          {
            classdesc[spClass] = localObjectStreamClass;
            classes[spClass] = null;
          }
          else
          {
            classdesc[spClass] = localObjectStreamClass;
            classes[spClass] = localObject2;
            localClass1 = ((Class)localObject2).getSuperclass();
          }
          localObjectStreamClass = localObjectStreamClass.getSuperclass();
        }
        try
        {
          currentObject = (currentClass == null ? null : currentClassDesc.newInstance());
          activeRecursionMgr.addObject(paramInt, currentObject);
        }
        catch (InvocationTargetException localInvocationTargetException2)
        {
          localObject2 = new InvalidClassException(currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          ((InvalidClassException)localObject2).initCause(localInvocationTargetException2);
          throw ((Throwable)localObject2);
        }
        catch (UnsupportedOperationException localUnsupportedOperationException2)
        {
          localObject2 = new InvalidClassException(currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          ((InvalidClassException)localObject2).initCause(localUnsupportedOperationException2);
          throw ((Throwable)localObject2);
        }
        catch (InstantiationException localInstantiationException2)
        {
          localObject2 = new InvalidClassException(currentClass.getName(), "InstantiationException accessing no-arg constructor");
          ((InvalidClassException)localObject2).initCause(localInstantiationException2);
          throw ((Throwable)localObject2);
        }
        localObject1 = getOrderedDescriptions(paramString, paramCodeBase).elements();
        while ((((Enumeration)localObject1).hasMoreElements()) && (spClass > i))
        {
          localObject2 = (FullValueDescription)((Enumeration)localObject1).nextElement();
          String str = vhandler.getClassName(id);
          localObject3 = vhandler.getClassName(vhandler.getRMIRepositoryID(currentClass));
          while ((spClass > i) && (!str.equals(localObject3)))
          {
            int k = findNextClass(str, classes, spClass, i);
            if (k != -1)
            {
              spClass = k;
              localClass1 = currentClass = classes[spClass];
              localObject3 = vhandler.getClassName(vhandler.getRMIRepositoryID(currentClass));
            }
            else
            {
              if (is_custom)
              {
                readFormatVersion();
                boolean bool1 = readBoolean();
                if (bool1) {
                  inputClassFields(null, null, null, members, paramCodeBase);
                }
                if (getStreamFormatVersion() == 2)
                {
                  ((ValueInputStream)getOrbStream()).start_value();
                  ((ValueInputStream)getOrbStream()).end_value();
                }
              }
              else
              {
                inputClassFields(null, currentClass, null, members, paramCodeBase);
              }
              if (((Enumeration)localObject1).hasMoreElements())
              {
                localObject2 = (FullValueDescription)((Enumeration)localObject1).nextElement();
                str = vhandler.getClassName(id);
              }
              else
              {
                Object localObject4 = currentObject;
                return localObject4;
              }
            }
          }
          localObjectStreamClass = currentClassDesc = ObjectStreamClass.lookup(currentClass);
          if (!((String)localObject3).equals("java.lang.Object"))
          {
            InputStreamHook.ReadObjectState localReadObjectState = readObjectState;
            setState(DEFAULT_STATE);
            try
            {
              if (is_custom)
              {
                readFormatVersion();
                bool2 = readBoolean();
                readObjectState.beginUnmarshalCustomValue(this, bool2, currentClassDesc.readObjectMethod != null);
              }
              boolean bool2 = false;
              try
              {
                if ((!is_custom) && (currentClassDesc.hasReadObject())) {
                  setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                }
                defaultReadObjectFVDMembers = members;
                bool2 = invokeObjectReader(currentClassDesc, currentObject, currentClass);
              }
              finally
              {
                defaultReadObjectFVDMembers = null;
              }
              if ((!bool2) || (readObjectState == IN_READ_OBJECT_DEFAULTS_SENT)) {
                inputClassFields(currentObject, currentClass, localObjectStreamClass, members, paramCodeBase);
              }
              if (is_custom) {
                readObjectState.endUnmarshalCustomValue(this);
              }
            }
            finally
            {
              setState(localReadObjectState);
            }
            localClass1 = currentClass = classes[(--spClass)];
          }
          else
          {
            inputClassFields(null, currentClass, null, members, paramCodeBase);
            while (((Enumeration)localObject1).hasMoreElements())
            {
              localObject2 = (FullValueDescription)((Enumeration)localObject1).nextElement();
              if (is_custom) {
                skipCustomUsingFVD(members, paramCodeBase);
              } else {
                inputClassFields(null, currentClass, null, members, paramCodeBase);
              }
            }
          }
        }
        while (((Enumeration)localObject1).hasMoreElements())
        {
          localObject2 = (FullValueDescription)((Enumeration)localObject1).nextElement();
          if (is_custom) {
            skipCustomUsingFVD(members, paramCodeBase);
          } else {
            throwAwayData(members, paramCodeBase);
          }
        }
      }
      Object localObject1 = currentObject;
      return localObject1;
    }
    finally
    {
      spClass = i;
      activeRecursionMgr.removeObject(paramInt);
    }
  }
  
  private Object skipObjectUsingFVD(String paramString, CodeBase paramCodeBase)
    throws IOException, ClassNotFoundException
  {
    Enumeration localEnumeration = getOrderedDescriptions(paramString, paramCodeBase).elements();
    while (localEnumeration.hasMoreElements())
    {
      FullValueDescription localFullValueDescription = (FullValueDescription)localEnumeration.nextElement();
      String str = vhandler.getClassName(id);
      if (!str.equals("java.lang.Object")) {
        if (is_custom)
        {
          readFormatVersion();
          boolean bool = readBoolean();
          if (bool) {
            inputClassFields(null, null, null, members, paramCodeBase);
          }
          if (getStreamFormatVersion() == 2)
          {
            ((ValueInputStream)getOrbStream()).start_value();
            ((ValueInputStream)getOrbStream()).end_value();
          }
        }
        else
        {
          inputClassFields(null, null, null, members, paramCodeBase);
        }
      }
    }
    return null;
  }
  
  private int findNextClass(String paramString, Class[] paramArrayOfClass, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i > paramInt2; i--) {
      if (paramString.equals(paramArrayOfClass[i].getName())) {
        return i;
      }
    }
    return -1;
  }
  
  private boolean invokeObjectReader(ObjectStreamClass paramObjectStreamClass, Object paramObject, Class paramClass)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    if (readObjectMethod == null) {
      return false;
    }
    try
    {
      readObjectMethod.invoke(paramObject, readObjectArgList);
      return true;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getTargetException();
      if ((localThrowable instanceof ClassNotFoundException)) {
        throw ((ClassNotFoundException)localThrowable);
      }
      if ((localThrowable instanceof IOException)) {
        throw ((IOException)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new Error("internal error");
    }
    catch (IllegalAccessException localIllegalAccessException) {}
    return false;
  }
  
  private void resetStream()
    throws IOException
  {
    int i;
    if (classes == null) {
      classes = new Class[20];
    } else {
      for (i = 0; i < classes.length; i++) {
        classes[i] = null;
      }
    }
    if (classdesc == null) {
      classdesc = new ObjectStreamClass[20];
    } else {
      for (i = 0; i < classdesc.length; i++) {
        classdesc[i] = null;
      }
    }
    spClass = 0;
    if (callbacks != null) {
      callbacks.setSize(0);
    }
  }
  
  private void inputPrimitiveField(Object paramObject, Class paramClass, ObjectStreamField paramObjectStreamField)
    throws InvalidClassException, IOException
  {
    try
    {
      switch (paramObjectStreamField.getTypeCode())
      {
      case 'B': 
        byte b = orbStream.read_octet();
        if (paramObjectStreamField.getField() != null) {
          bridge.putByte(paramObject, paramObjectStreamField.getFieldID(), b);
        }
        break;
      case 'Z': 
        boolean bool = orbStream.read_boolean();
        if (paramObjectStreamField.getField() != null) {
          bridge.putBoolean(paramObject, paramObjectStreamField.getFieldID(), bool);
        }
        break;
      case 'C': 
        char c = orbStream.read_wchar();
        if (paramObjectStreamField.getField() != null) {
          bridge.putChar(paramObject, paramObjectStreamField.getFieldID(), c);
        }
        break;
      case 'S': 
        short s = orbStream.read_short();
        if (paramObjectStreamField.getField() != null) {
          bridge.putShort(paramObject, paramObjectStreamField.getFieldID(), s);
        }
        break;
      case 'I': 
        int i = orbStream.read_long();
        if (paramObjectStreamField.getField() != null) {
          bridge.putInt(paramObject, paramObjectStreamField.getFieldID(), i);
        }
        break;
      case 'J': 
        long l = orbStream.read_longlong();
        if (paramObjectStreamField.getField() != null) {
          bridge.putLong(paramObject, paramObjectStreamField.getFieldID(), l);
        }
        break;
      case 'F': 
        float f = orbStream.read_float();
        if (paramObjectStreamField.getField() != null) {
          bridge.putFloat(paramObject, paramObjectStreamField.getFieldID(), f);
        }
        break;
      case 'D': 
        double d = orbStream.read_double();
        if (paramObjectStreamField.getField() != null) {
          bridge.putDouble(paramObject, paramObjectStreamField.getFieldID(), d);
        }
        break;
      case 'E': 
      case 'G': 
      case 'H': 
      case 'K': 
      case 'L': 
      case 'M': 
      case 'N': 
      case 'O': 
      case 'P': 
      case 'Q': 
      case 'R': 
      case 'T': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case 'Y': 
      default: 
        throw new InvalidClassException(paramClass.getName());
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      ClassCastException localClassCastException = new ClassCastException("Assigning instance of class " + paramObjectStreamField.getType().getName() + " to field " + currentClassDesc.getName() + '#' + paramObjectStreamField.getField().getName());
      localClassCastException.initCause(localIllegalArgumentException);
      throw localClassCastException;
    }
  }
  
  private Object inputObjectField(ValueMember paramValueMember, CodeBase paramCodeBase)
    throws IndirectionException, ClassNotFoundException, IOException, StreamCorruptedException
  {
    Object localObject = null;
    Class localClass = null;
    String str1 = id;
    try
    {
      localClass = vhandler.getClassFromType(str1);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localClass = null;
    }
    String str2 = null;
    if (localClass != null) {
      str2 = ValueUtility.getSignature(paramValueMember);
    }
    if ((str2 != null) && ((str2.equals("Ljava/lang/Object;")) || (str2.equals("Ljava/io/Serializable;")) || (str2.equals("Ljava/io/Externalizable;"))))
    {
      localObject = Util.readAny(orbStream);
    }
    else
    {
      int i = 2;
      if (!vhandler.isSequence(str1)) {
        if (type.kind().value() == kRemoteTypeCode.kind().value()) {
          i = 0;
        } else if ((localClass != null) && (localClass.isInterface()) && ((vhandler.isAbstractBase(localClass)) || (ObjectStreamClassCorbaExt.isAbstractInterface(localClass)))) {
          i = 1;
        }
      }
      switch (i)
      {
      case 0: 
        if (localClass != null) {
          localObject = Utility.readObjectAndNarrow(orbStream, localClass);
        } else {
          localObject = orbStream.read_Object();
        }
        break;
      case 1: 
        if (localClass != null) {
          localObject = Utility.readAbstractAndNarrow(orbStream, localClass);
        } else {
          localObject = orbStream.read_abstract_interface();
        }
        break;
      case 2: 
        if (localClass != null) {
          localObject = orbStream.read_value(localClass);
        } else {
          localObject = orbStream.read_value();
        }
        break;
      default: 
        throw new StreamCorruptedException("Unknown callType: " + i);
      }
    }
    return localObject;
  }
  
  private Object inputObjectField(ObjectStreamField paramObjectStreamField)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IndirectionException, IOException
  {
    if (ObjectStreamClassCorbaExt.isAny(paramObjectStreamField.getTypeString())) {
      return Util.readAny(orbStream);
    }
    Object localObject1 = null;
    Class localClass1 = paramObjectStreamField.getType();
    Object localObject2 = localClass1;
    int i = 2;
    int j = 0;
    if (localClass1.isInterface())
    {
      int k = 0;
      if (Remote.class.isAssignableFrom(localClass1))
      {
        i = 0;
      }
      else if (org.omg.CORBA.Object.class.isAssignableFrom(localClass1))
      {
        i = 0;
        k = 1;
      }
      else if (vhandler.isAbstractBase(localClass1))
      {
        i = 1;
        k = 1;
      }
      else if (ObjectStreamClassCorbaExt.isAbstractInterface(localClass1))
      {
        i = 1;
      }
      if (k != 0) {
        try
        {
          String str1 = Util.getCodebase(localClass1);
          String str2 = vhandler.createForAnyType(localClass1);
          Class localClass2 = Utility.loadStubClass(str2, str1, localClass1);
          localObject2 = localClass2;
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          j = 1;
        }
      } else {
        j = 1;
      }
    }
    switch (i)
    {
    case 0: 
      if (j == 0) {
        localObject1 = orbStream.read_Object((Class)localObject2);
      } else {
        localObject1 = Utility.readObjectAndNarrow(orbStream, (Class)localObject2);
      }
      break;
    case 1: 
      if (j == 0) {
        localObject1 = orbStream.read_abstract_interface((Class)localObject2);
      } else {
        localObject1 = Utility.readAbstractAndNarrow(orbStream, (Class)localObject2);
      }
      break;
    case 2: 
      localObject1 = orbStream.read_value((Class)localObject2);
      break;
    default: 
      throw new StreamCorruptedException("Unknown callType: " + i);
    }
    return localObject1;
  }
  
  private final boolean mustUseRemoteValueMembers()
  {
    return defaultReadObjectFVDMembers != null;
  }
  
  void readFields(Map paramMap)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    if (mustUseRemoteValueMembers()) {
      inputRemoteMembersForReadFields(paramMap);
    } else {
      inputCurrentClassFieldsForReadFields(paramMap);
    }
  }
  
  private final void inputRemoteMembersForReadFields(Map paramMap)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    ValueMember[] arrayOfValueMember = defaultReadObjectFVDMembers;
    try
    {
      for (int i = 0; i < arrayOfValueMember.length; i++) {
        switch (type.kind().value())
        {
        case 10: 
          byte b = orbStream.read_octet();
          paramMap.put(name, new Byte(b));
          break;
        case 8: 
          boolean bool = orbStream.read_boolean();
          paramMap.put(name, new Boolean(bool));
          break;
        case 9: 
        case 26: 
          char c = orbStream.read_wchar();
          paramMap.put(name, new Character(c));
          break;
        case 2: 
          short s = orbStream.read_short();
          paramMap.put(name, new Short(s));
          break;
        case 3: 
          int j = orbStream.read_long();
          paramMap.put(name, new Integer(j));
          break;
        case 23: 
          long l = orbStream.read_longlong();
          paramMap.put(name, new Long(l));
          break;
        case 6: 
          float f = orbStream.read_float();
          paramMap.put(name, new Float(f));
          break;
        case 7: 
          double d = orbStream.read_double();
          paramMap.put(name, new Double(d));
          break;
        case 14: 
        case 29: 
        case 30: 
          Object localObject = null;
          try
          {
            localObject = inputObjectField(arrayOfValueMember[i], cbSender);
          }
          catch (IndirectionException localIndirectionException)
          {
            localObject = activeRecursionMgr.getObject(offset);
          }
          paramMap.put(name, localObject);
          break;
        case 4: 
        case 5: 
        case 11: 
        case 12: 
        case 13: 
        case 15: 
        case 16: 
        case 17: 
        case 18: 
        case 19: 
        case 20: 
        case 21: 
        case 22: 
        case 24: 
        case 25: 
        case 27: 
        case 28: 
        default: 
          throw new StreamCorruptedException("Unknown kind: " + type.kind().value());
        }
      }
    }
    catch (Throwable localThrowable)
    {
      StreamCorruptedException localStreamCorruptedException = new StreamCorruptedException(localThrowable.getMessage());
      localStreamCorruptedException.initCause(localThrowable);
      throw localStreamCorruptedException;
    }
  }
  
  private final void inputCurrentClassFieldsForReadFields(Map paramMap)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    ObjectStreamField[] arrayOfObjectStreamField = currentClassDesc.getFieldsNoCopy();
    int i = arrayOfObjectStreamField.length - currentClassDesc.objFields;
    for (int j = 0; j < i; j++) {
      switch (arrayOfObjectStreamField[j].getTypeCode())
      {
      case 'B': 
        byte b = orbStream.read_octet();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Byte(b));
        break;
      case 'Z': 
        boolean bool = orbStream.read_boolean();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Boolean(bool));
        break;
      case 'C': 
        char c = orbStream.read_wchar();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Character(c));
        break;
      case 'S': 
        short s = orbStream.read_short();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Short(s));
        break;
      case 'I': 
        int k = orbStream.read_long();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Integer(k));
        break;
      case 'J': 
        long l = orbStream.read_longlong();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Long(l));
        break;
      case 'F': 
        float f = orbStream.read_float();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Float(f));
        break;
      case 'D': 
        double d = orbStream.read_double();
        paramMap.put(arrayOfObjectStreamField[j].getName(), new Double(d));
        break;
      case 'E': 
      case 'G': 
      case 'H': 
      case 'K': 
      case 'L': 
      case 'M': 
      case 'N': 
      case 'O': 
      case 'P': 
      case 'Q': 
      case 'R': 
      case 'T': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case 'Y': 
      default: 
        throw new InvalidClassException(currentClassDesc.getName());
      }
    }
    if (currentClassDesc.objFields > 0) {
      for (j = i; j < arrayOfObjectStreamField.length; j++)
      {
        Object localObject = null;
        try
        {
          localObject = inputObjectField(arrayOfObjectStreamField[j]);
        }
        catch (IndirectionException localIndirectionException)
        {
          localObject = activeRecursionMgr.getObject(offset);
        }
        paramMap.put(arrayOfObjectStreamField[j].getName(), localObject);
      }
    }
  }
  
  private void inputClassFields(Object paramObject, Class<?> paramClass, ObjectStreamField[] paramArrayOfObjectStreamField, CodeBase paramCodeBase)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    int i = paramArrayOfObjectStreamField.length - currentClassDesc.objFields;
    int j;
    if (paramObject != null) {
      for (j = 0; j < i; j++) {
        inputPrimitiveField(paramObject, paramClass, paramArrayOfObjectStreamField[j]);
      }
    }
    if (currentClassDesc.objFields > 0) {
      for (j = i; j < paramArrayOfObjectStreamField.length; j++)
      {
        Object localObject1 = null;
        try
        {
          localObject1 = inputObjectField(paramArrayOfObjectStreamField[j]);
        }
        catch (IndirectionException localIndirectionException)
        {
          localObject1 = activeRecursionMgr.getObject(offset);
        }
        if ((paramObject != null) && (paramArrayOfObjectStreamField[j].getField() != null)) {
          try
          {
            Class localClass = paramArrayOfObjectStreamField[j].getClazz();
            if ((localObject1 != null) && (!localClass.isAssignableFrom(localObject1.getClass()))) {
              throw new IllegalArgumentException("Field mismatch");
            }
            localObject2 = null;
            str = paramArrayOfObjectStreamField[j].getName();
            try
            {
              localObject2 = getDeclaredField(paramClass, str);
            }
            catch (PrivilegedActionException localPrivilegedActionException)
            {
              throw new IllegalArgumentException((NoSuchFieldException)localPrivilegedActionException.getException());
            }
            catch (SecurityException localSecurityException)
            {
              throw new IllegalArgumentException(localSecurityException);
            }
            catch (NullPointerException localNullPointerException)
            {
              continue;
            }
            catch (NoSuchFieldException localNoSuchFieldException)
            {
              continue;
            }
            if (localObject2 != null)
            {
              localObject3 = ((Field)localObject2).getType();
              if (!((Class)localObject3).isAssignableFrom(localClass)) {
                throw new IllegalArgumentException("Field Type mismatch");
              }
              if ((localObject1 != null) && (!localClass.isInstance(localObject1))) {
                throw new IllegalArgumentException();
              }
              bridge.putObject(paramObject, paramArrayOfObjectStreamField[j].getFieldID(), localObject1);
            }
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            Object localObject2 = "null";
            String str = "null";
            Object localObject3 = "null";
            if (localObject1 != null) {
              localObject2 = localObject1.getClass().getName();
            }
            if (currentClassDesc != null) {
              str = currentClassDesc.getName();
            }
            if ((paramArrayOfObjectStreamField[j] != null) && (paramArrayOfObjectStreamField[j].getField() != null)) {
              localObject3 = paramArrayOfObjectStreamField[j].getField().getName();
            }
            ClassCastException localClassCastException = new ClassCastException("Assigning instance of class " + (String)localObject2 + " to field " + str + '#' + (String)localObject3);
            localClassCastException.initCause(localIllegalArgumentException);
            throw localClassCastException;
          }
        }
      }
    }
  }
  
  private void inputClassFields(Object paramObject, Class paramClass, ObjectStreamClass paramObjectStreamClass, ValueMember[] paramArrayOfValueMember, CodeBase paramCodeBase)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    try
    {
      for (int i = 0; i < paramArrayOfValueMember.length; i++) {
        try
        {
          switch (type.kind().value())
          {
          case 10: 
            byte b = orbStream.read_octet();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setByteField(paramObject, paramClass, name, b);
            }
            break;
          case 8: 
            boolean bool = orbStream.read_boolean();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setBooleanField(paramObject, paramClass, name, bool);
            }
            break;
          case 9: 
          case 26: 
            char c = orbStream.read_wchar();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setCharField(paramObject, paramClass, name, c);
            }
            break;
          case 2: 
            short s = orbStream.read_short();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setShortField(paramObject, paramClass, name, s);
            }
            break;
          case 3: 
            int j = orbStream.read_long();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setIntField(paramObject, paramClass, name, j);
            }
            break;
          case 23: 
            long l = orbStream.read_longlong();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setLongField(paramObject, paramClass, name, l);
            }
            break;
          case 6: 
            float f = orbStream.read_float();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setFloatField(paramObject, paramClass, name, f);
            }
            break;
          case 7: 
            double d = orbStream.read_double();
            if ((paramObject != null) && (paramObjectStreamClass.hasField(paramArrayOfValueMember[i]))) {
              setDoubleField(paramObject, paramClass, name, d);
            }
            break;
          case 14: 
          case 29: 
          case 30: 
            Object localObject = null;
            try
            {
              localObject = inputObjectField(paramArrayOfValueMember[i], paramCodeBase);
            }
            catch (IndirectionException localIndirectionException)
            {
              localObject = activeRecursionMgr.getObject(offset);
            }
            if (paramObject != null) {
              try
              {
                if (paramObjectStreamClass.hasField(paramArrayOfValueMember[i])) {
                  setObjectField(paramObject, paramClass, name, localObject);
                }
              }
              catch (IllegalArgumentException localIllegalArgumentException2)
              {
                ClassCastException localClassCastException2 = new ClassCastException("Assigning instance of class " + localObject.getClass().getName() + " to field " + name);
                localClassCastException2.initCause(localIllegalArgumentException2);
                throw localClassCastException2;
              }
            }
            break;
          case 4: 
          case 5: 
          case 11: 
          case 12: 
          case 13: 
          case 15: 
          case 16: 
          case 17: 
          case 18: 
          case 19: 
          case 20: 
          case 21: 
          case 22: 
          case 24: 
          case 25: 
          case 27: 
          case 28: 
          default: 
            throw new StreamCorruptedException("Unknown kind: " + type.kind().value());
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          ClassCastException localClassCastException1 = new ClassCastException("Assigning instance of class " + id + " to field " + currentClassDesc.getName() + '#' + name);
          localClassCastException1.initCause(localIllegalArgumentException1);
          throw localClassCastException1;
        }
      }
    }
    catch (Throwable localThrowable)
    {
      StreamCorruptedException localStreamCorruptedException = new StreamCorruptedException(localThrowable.getMessage());
      localStreamCorruptedException.initCause(localThrowable);
      throw localStreamCorruptedException;
    }
  }
  
  private void skipCustomUsingFVD(ValueMember[] paramArrayOfValueMember, CodeBase paramCodeBase)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    readFormatVersion();
    boolean bool = readBoolean();
    if (bool) {
      throwAwayData(paramArrayOfValueMember, paramCodeBase);
    }
    if (getStreamFormatVersion() == 2)
    {
      ((ValueInputStream)getOrbStream()).start_value();
      ((ValueInputStream)getOrbStream()).end_value();
    }
  }
  
  private void throwAwayData(ValueMember[] paramArrayOfValueMember, CodeBase paramCodeBase)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException
  {
    for (int i = 0; i < paramArrayOfValueMember.length; i++) {
      try
      {
        switch (type.kind().value())
        {
        case 10: 
          orbStream.read_octet();
          break;
        case 8: 
          orbStream.read_boolean();
          break;
        case 9: 
        case 26: 
          orbStream.read_wchar();
          break;
        case 2: 
          orbStream.read_short();
          break;
        case 3: 
          orbStream.read_long();
          break;
        case 23: 
          orbStream.read_longlong();
          break;
        case 6: 
          orbStream.read_float();
          break;
        case 7: 
          orbStream.read_double();
          break;
        case 14: 
        case 29: 
        case 30: 
          Class localClass = null;
          localObject = id;
          try
          {
            localClass = vhandler.getClassFromType((String)localObject);
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            localClass = null;
          }
          String str = null;
          if (localClass != null) {
            str = ValueUtility.getSignature(paramArrayOfValueMember[i]);
          }
          try
          {
            if ((str != null) && ((str.equals("Ljava/lang/Object;")) || (str.equals("Ljava/io/Serializable;")) || (str.equals("Ljava/io/Externalizable;"))))
            {
              Util.readAny(orbStream);
            }
            else
            {
              int j = 2;
              if (!vhandler.isSequence((String)localObject))
              {
                FullValueDescription localFullValueDescription = paramCodeBase.meta(id);
                if (kRemoteTypeCode == type) {
                  j = 0;
                } else if (is_abstract) {
                  j = 1;
                }
              }
              switch (j)
              {
              case 0: 
                orbStream.read_Object();
                break;
              case 1: 
                orbStream.read_abstract_interface();
                break;
              case 2: 
                if (localClass != null) {
                  orbStream.read_value(localClass);
                } else {
                  orbStream.read_value();
                }
                break;
              default: 
                throw new StreamCorruptedException("Unknown callType: " + j);
              }
            }
          }
          catch (IndirectionException localIndirectionException) {}
        case 4: 
        case 5: 
        case 11: 
        case 12: 
        case 13: 
        case 15: 
        case 16: 
        case 17: 
        case 18: 
        case 19: 
        case 20: 
        case 21: 
        case 22: 
        case 24: 
        case 25: 
        case 27: 
        case 28: 
        default: 
          throw new StreamCorruptedException("Unknown kind: " + type.kind().value());
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        Object localObject = new ClassCastException("Assigning instance of class " + id + " to field " + currentClassDesc.getName() + '#' + name);
        ((ClassCastException)localObject).initCause(localIllegalArgumentException);
        throw ((Throwable)localObject);
      }
    }
  }
  
  private static void setObjectField(Object paramObject1, Class<?> paramClass, String paramString, Object paramObject2)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      Class localClass = localField.getType();
      if ((paramObject2 != null) && (!localClass.isInstance(paramObject2))) {
        throw new Exception();
      }
      long l = bridge.objectFieldOffset(localField);
      bridge.putObject(paramObject1, l, paramObject2);
    }
    catch (Exception localException)
    {
      if (paramObject1 != null) {
        throw utilWrapper.errorSetObjectField(localException, paramString, paramObject1.toString(), paramObject2.toString());
      }
      throw utilWrapper.errorSetObjectField(localException, paramString, "null " + paramClass.getName() + " object", paramObject2.toString());
    }
  }
  
  private static void setBooleanField(Object paramObject, Class<?> paramClass, String paramString, boolean paramBoolean)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Boolean.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putBoolean(paramObject, l, paramBoolean);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetBooleanField(localException, paramString, paramObject.toString(), new Boolean(paramBoolean));
      }
      throw utilWrapper.errorSetBooleanField(localException, paramString, "null " + paramClass.getName() + " object", new Boolean(paramBoolean));
    }
  }
  
  private static void setByteField(Object paramObject, Class<?> paramClass, String paramString, byte paramByte)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Byte.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putByte(paramObject, l, paramByte);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetByteField(localException, paramString, paramObject.toString(), new Byte(paramByte));
      }
      throw utilWrapper.errorSetByteField(localException, paramString, "null " + paramClass.getName() + " object", new Byte(paramByte));
    }
  }
  
  private static void setCharField(Object paramObject, Class<?> paramClass, String paramString, char paramChar)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Character.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putChar(paramObject, l, paramChar);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetCharField(localException, paramString, paramObject.toString(), new Character(paramChar));
      }
      throw utilWrapper.errorSetCharField(localException, paramString, "null " + paramClass.getName() + " object", new Character(paramChar));
    }
  }
  
  private static void setShortField(Object paramObject, Class<?> paramClass, String paramString, short paramShort)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Short.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putShort(paramObject, l, paramShort);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetShortField(localException, paramString, paramObject.toString(), new Short(paramShort));
      }
      throw utilWrapper.errorSetShortField(localException, paramString, "null " + paramClass.getName() + " object", new Short(paramShort));
    }
  }
  
  private static void setIntField(Object paramObject, Class<?> paramClass, String paramString, int paramInt)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Integer.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putInt(paramObject, l, paramInt);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetIntField(localException, paramString, paramObject.toString(), new Integer(paramInt));
      }
      throw utilWrapper.errorSetIntField(localException, paramString, "null " + paramClass.getName() + " object", new Integer(paramInt));
    }
  }
  
  private static void setLongField(Object paramObject, Class<?> paramClass, String paramString, long paramLong)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Long.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putLong(paramObject, l, paramLong);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetLongField(localException, paramString, paramObject.toString(), new Long(paramLong));
      }
      throw utilWrapper.errorSetLongField(localException, paramString, "null " + paramClass.getName() + " object", new Long(paramLong));
    }
  }
  
  private static void setFloatField(Object paramObject, Class<?> paramClass, String paramString, float paramFloat)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Float.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putFloat(paramObject, l, paramFloat);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetFloatField(localException, paramString, paramObject.toString(), new Float(paramFloat));
      }
      throw utilWrapper.errorSetFloatField(localException, paramString, "null " + paramClass.getName() + " object", new Float(paramFloat));
    }
  }
  
  private static void setDoubleField(Object paramObject, Class<?> paramClass, String paramString, double paramDouble)
  {
    try
    {
      Field localField = getDeclaredField(paramClass, paramString);
      if ((localField != null) && (localField.getType() == Double.TYPE))
      {
        long l = bridge.objectFieldOffset(localField);
        bridge.putDouble(paramObject, l, paramDouble);
      }
      else
      {
        throw new InvalidObjectException("Field Type mismatch");
      }
    }
    catch (Exception localException)
    {
      if (paramObject != null) {
        throw utilWrapper.errorSetDoubleField(localException, paramString, paramObject.toString(), new Double(paramDouble));
      }
      throw utilWrapper.errorSetDoubleField(localException, paramString, "null " + paramClass.getName() + " object", new Double(paramDouble));
    }
  }
  
  private static Field getDeclaredField(Class<?> paramClass, final String paramString)
    throws PrivilegedActionException, NoSuchFieldException, SecurityException
  {
    if (System.getSecurityManager() == null) {
      return paramClass.getDeclaredField(paramString);
    }
    (Field)AccessController.doPrivileged(new PrivilegedExceptionAction()
    {
      public Field run()
        throws NoSuchFieldException
      {
        return val$c.getDeclaredField(paramString);
      }
    });
  }
  
  static class ActiveRecursionManager
  {
    private Map<Integer, Object> offsetToObjectMap = new HashMap();
    
    public ActiveRecursionManager() {}
    
    public void addObject(int paramInt, Object paramObject)
    {
      offsetToObjectMap.put(new Integer(paramInt), paramObject);
    }
    
    public Object getObject(int paramInt)
      throws IOException
    {
      Integer localInteger = new Integer(paramInt);
      if (!offsetToObjectMap.containsKey(localInteger)) {
        throw new IOException("Invalid indirection to offset " + paramInt);
      }
      return offsetToObjectMap.get(localInteger);
    }
    
    public void removeObject(int paramInt)
    {
      offsetToObjectMap.remove(new Integer(paramInt));
    }
    
    public boolean containsObject(int paramInt)
    {
      return offsetToObjectMap.containsKey(new Integer(paramInt));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\io\IIOPInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */