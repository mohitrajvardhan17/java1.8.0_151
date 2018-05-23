package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotActiveException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Stack;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.portable.ValueOutputStream;
import sun.corba.Bridge;

public class IIOPOutputStream
  extends OutputStreamHook
{
  private UtilSystemException wrapper = UtilSystemException.get("rpc.encoding");
  private static Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Object run()
    {
      return Bridge.get();
    }
  });
  private org.omg.CORBA_2_3.portable.OutputStream orbStream;
  private Object currentObject = null;
  private ObjectStreamClass currentClassDesc = null;
  private int recursionDepth = 0;
  private int simpleWriteDepth = 0;
  private IOException abortIOException = null;
  private Stack classDescStack = new Stack();
  private Object[] writeObjectArgList = { this };
  
  public IIOPOutputStream()
    throws IOException
  {}
  
  protected void beginOptionalCustomData()
  {
    if (streamFormatVersion == 2)
    {
      ValueOutputStream localValueOutputStream = (ValueOutputStream)orbStream;
      localValueOutputStream.start_value(currentClassDesc.getRMIIIOPOptionalDataRepId());
    }
  }
  
  final void setOrbStream(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream)
  {
    orbStream = paramOutputStream;
  }
  
  final org.omg.CORBA_2_3.portable.OutputStream getOrbStream()
  {
    return orbStream;
  }
  
  final void increaseRecursionDepth()
  {
    recursionDepth += 1;
  }
  
  final int decreaseRecursionDepth()
  {
    return --recursionDepth;
  }
  
  public final void writeObjectOverride(Object paramObject)
    throws IOException
  {
    writeObjectState.writeData(this);
    Util.writeAbstractObject(orbStream, paramObject);
  }
  
  public final void simpleWriteObject(Object paramObject, byte paramByte)
  {
    byte b = streamFormatVersion;
    streamFormatVersion = paramByte;
    Object localObject1 = currentObject;
    ObjectStreamClass localObjectStreamClass = currentClassDesc;
    simpleWriteDepth += 1;
    try
    {
      outputObject(paramObject);
    }
    catch (IOException localIOException1)
    {
      if (abortIOException == null) {
        abortIOException = localIOException1;
      }
    }
    finally
    {
      streamFormatVersion = b;
      simpleWriteDepth -= 1;
      currentObject = localObject1;
      currentClassDesc = localObjectStreamClass;
    }
    IOException localIOException2 = abortIOException;
    if (simpleWriteDepth == 0) {
      abortIOException = null;
    }
    if (localIOException2 != null) {
      bridge.throwException(localIOException2);
    }
  }
  
  ObjectStreamField[] getFieldsNoCopy()
  {
    return currentClassDesc.getFieldsNoCopy();
  }
  
  public final void defaultWriteObjectDelegate()
  {
    try
    {
      if ((currentObject == null) || (currentClassDesc == null)) {
        throw new NotActiveException("defaultWriteObjectDelegate");
      }
      ObjectStreamField[] arrayOfObjectStreamField = currentClassDesc.getFieldsNoCopy();
      if (arrayOfObjectStreamField.length > 0) {
        outputClassFields(currentObject, currentClassDesc.forClass(), arrayOfObjectStreamField);
      }
    }
    catch (IOException localIOException)
    {
      bridge.throwException(localIOException);
    }
  }
  
  public final boolean enableReplaceObjectDelegate(boolean paramBoolean)
  {
    return false;
  }
  
  protected final void annotateClass(Class<?> paramClass)
    throws IOException
  {
    throw new IOException("Method annotateClass not supported");
  }
  
  public final void close()
    throws IOException
  {}
  
  protected final void drain()
    throws IOException
  {}
  
  public final void flush()
    throws IOException
  {
    try
    {
      orbStream.flush();
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  protected final Object replaceObject(Object paramObject)
    throws IOException
  {
    throw new IOException("Method replaceObject not supported");
  }
  
  public final void reset()
    throws IOException
  {
    try
    {
      if ((currentObject != null) || (currentClassDesc != null)) {
        throw new IOException("Illegal call to reset");
      }
      abortIOException = null;
      if (classDescStack == null) {
        classDescStack = new Stack();
      } else {
        classDescStack.setSize(0);
      }
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void write(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_octet_array(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_octet_array(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void write(int paramInt)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_octet((byte)(paramInt & 0xFF));
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeBoolean(boolean paramBoolean)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_boolean(paramBoolean);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeByte(int paramInt)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_octet((byte)paramInt);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeBytes(String paramString)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      byte[] arrayOfByte = paramString.getBytes();
      orbStream.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeChar(int paramInt)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_wchar((char)paramInt);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeChars(String paramString)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      char[] arrayOfChar = paramString.toCharArray();
      orbStream.write_wchar_array(arrayOfChar, 0, arrayOfChar.length);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeDouble(double paramDouble)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_double(paramDouble);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeFloat(float paramFloat)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_float(paramFloat);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeInt(int paramInt)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_long(paramInt);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeLong(long paramLong)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_longlong(paramLong);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  public final void writeShort(int paramInt)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      orbStream.write_short((short)paramInt);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  protected final void writeStreamHeader()
    throws IOException
  {}
  
  protected void internalWriteUTF(org.omg.CORBA.portable.OutputStream paramOutputStream, String paramString)
  {
    paramOutputStream.write_wstring(paramString);
  }
  
  public final void writeUTF(String paramString)
    throws IOException
  {
    try
    {
      writeObjectState.writeData(this);
      internalWriteUTF(orbStream, paramString);
    }
    catch (Error localError)
    {
      IOException localIOException = new IOException(localError.getMessage());
      localIOException.initCause(localError);
      throw localIOException;
    }
  }
  
  private boolean checkSpecialClasses(Object paramObject)
    throws IOException
  {
    if ((paramObject instanceof ObjectStreamClass)) {
      throw new IOException("Serialization of ObjectStreamClass not supported");
    }
    return false;
  }
  
  private boolean checkSubstitutableSpecialClasses(Object paramObject)
    throws IOException
  {
    if ((paramObject instanceof String))
    {
      orbStream.write_value((Serializable)paramObject);
      return true;
    }
    return false;
  }
  
  private void outputObject(Object paramObject)
    throws IOException
  {
    currentObject = paramObject;
    Class localClass = paramObject.getClass();
    currentClassDesc = ObjectStreamClass.lookup(localClass);
    if (currentClassDesc == null) {
      throw new NotSerializableException(localClass.getName());
    }
    if (currentClassDesc.isExternalizable())
    {
      orbStream.write_octet(streamFormatVersion);
      Externalizable localExternalizable = (Externalizable)paramObject;
      localExternalizable.writeExternal(this);
    }
    else
    {
      if (currentClassDesc.forClass().getName().equals("java.lang.String"))
      {
        writeUTF((String)paramObject);
        return;
      }
      int i = classDescStack.size();
      try
      {
        ObjectStreamClass localObjectStreamClass;
        while ((localObjectStreamClass = currentClassDesc.getSuperclass()) != null)
        {
          classDescStack.push(currentClassDesc);
          currentClassDesc = localObjectStreamClass;
        }
        do
        {
          OutputStreamHook.WriteObjectState localWriteObjectState = writeObjectState;
          try
          {
            setState(NOT_IN_WRITE_OBJECT);
            if (currentClassDesc.hasWriteObject()) {
              invokeObjectWriter(currentClassDesc, paramObject);
            } else {
              defaultWriteObjectDelegate();
            }
          }
          finally
          {
            setState(localWriteObjectState);
          }
          if (classDescStack.size() <= i) {
            break;
          }
        } while ((currentClassDesc = (ObjectStreamClass)classDescStack.pop()) != null);
      }
      finally
      {
        classDescStack.setSize(i);
      }
    }
  }
  
  private void invokeObjectWriter(ObjectStreamClass paramObjectStreamClass, Object paramObject)
    throws IOException
  {
    Class localClass = paramObjectStreamClass.forClass();
    try
    {
      orbStream.write_octet(streamFormatVersion);
      writeObjectState.enterWriteObject(this);
      writeObjectMethod.invoke(paramObject, writeObjectArgList);
      writeObjectState.exitWriteObject(this);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getTargetException();
      if ((localThrowable instanceof IOException)) {
        throw ((IOException)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new Error("invokeObjectWriter internal error", localInvocationTargetException);
    }
    catch (IllegalAccessException localIllegalAccessException) {}
  }
  
  void writeField(ObjectStreamField paramObjectStreamField, Object paramObject)
    throws IOException
  {
    switch (paramObjectStreamField.getTypeCode())
    {
    case 'B': 
      if (paramObject == null) {
        orbStream.write_octet((byte)0);
      } else {
        orbStream.write_octet(((Byte)paramObject).byteValue());
      }
      break;
    case 'C': 
      if (paramObject == null) {
        orbStream.write_wchar('\000');
      } else {
        orbStream.write_wchar(((Character)paramObject).charValue());
      }
      break;
    case 'F': 
      if (paramObject == null) {
        orbStream.write_float(0.0F);
      } else {
        orbStream.write_float(((Float)paramObject).floatValue());
      }
      break;
    case 'D': 
      if (paramObject == null) {
        orbStream.write_double(0.0D);
      } else {
        orbStream.write_double(((Double)paramObject).doubleValue());
      }
      break;
    case 'I': 
      if (paramObject == null) {
        orbStream.write_long(0);
      } else {
        orbStream.write_long(((Integer)paramObject).intValue());
      }
      break;
    case 'J': 
      if (paramObject == null) {
        orbStream.write_longlong(0L);
      } else {
        orbStream.write_longlong(((Long)paramObject).longValue());
      }
      break;
    case 'S': 
      if (paramObject == null) {
        orbStream.write_short((short)0);
      } else {
        orbStream.write_short(((Short)paramObject).shortValue());
      }
      break;
    case 'Z': 
      if (paramObject == null) {
        orbStream.write_boolean(false);
      } else {
        orbStream.write_boolean(((Boolean)paramObject).booleanValue());
      }
      break;
    case 'L': 
    case '[': 
      writeObjectField(paramObjectStreamField, paramObject);
      break;
    case 'E': 
    case 'G': 
    case 'H': 
    case 'K': 
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
  
  private void writeObjectField(ObjectStreamField paramObjectStreamField, Object paramObject)
    throws IOException
  {
    if (ObjectStreamClassCorbaExt.isAny(paramObjectStreamField.getTypeString()))
    {
      Util.writeAny(orbStream, paramObject);
    }
    else
    {
      Class localClass = paramObjectStreamField.getType();
      int i = 2;
      if (localClass.isInterface())
      {
        String str = localClass.getName();
        if (Remote.class.isAssignableFrom(localClass)) {
          i = 0;
        } else if (org.omg.CORBA.Object.class.isAssignableFrom(localClass)) {
          i = 0;
        } else if (RepositoryId.isAbstractBase(localClass)) {
          i = 1;
        } else if (ObjectStreamClassCorbaExt.isAbstractInterface(localClass)) {
          i = 1;
        }
      }
      switch (i)
      {
      case 0: 
        Util.writeRemoteObject(orbStream, paramObject);
        break;
      case 1: 
        Util.writeAbstractObject(orbStream, paramObject);
        break;
      case 2: 
        try
        {
          orbStream.write_value((Serializable)paramObject, localClass);
        }
        catch (ClassCastException localClassCastException)
        {
          if ((paramObject instanceof Serializable)) {
            throw localClassCastException;
          }
          Utility.throwNotSerializableForCorba(paramObject.getClass().getName());
        }
      }
    }
  }
  
  private void outputClassFields(Object paramObject, Class paramClass, ObjectStreamField[] paramArrayOfObjectStreamField)
    throws IOException, InvalidClassException
  {
    for (int i = 0; i < paramArrayOfObjectStreamField.length; i++)
    {
      if (paramArrayOfObjectStreamField[i].getField() == null) {
        throw new InvalidClassException(paramClass.getName(), "Nonexistent field " + paramArrayOfObjectStreamField[i].getName());
      }
      try
      {
        switch (paramArrayOfObjectStreamField[i].getTypeCode())
        {
        case 'B': 
          byte b = paramArrayOfObjectStreamField[i].getField().getByte(paramObject);
          orbStream.write_octet(b);
          break;
        case 'C': 
          char c = paramArrayOfObjectStreamField[i].getField().getChar(paramObject);
          orbStream.write_wchar(c);
          break;
        case 'F': 
          float f = paramArrayOfObjectStreamField[i].getField().getFloat(paramObject);
          orbStream.write_float(f);
          break;
        case 'D': 
          double d = paramArrayOfObjectStreamField[i].getField().getDouble(paramObject);
          orbStream.write_double(d);
          break;
        case 'I': 
          int j = paramArrayOfObjectStreamField[i].getField().getInt(paramObject);
          orbStream.write_long(j);
          break;
        case 'J': 
          long l = paramArrayOfObjectStreamField[i].getField().getLong(paramObject);
          orbStream.write_longlong(l);
          break;
        case 'S': 
          short s = paramArrayOfObjectStreamField[i].getField().getShort(paramObject);
          orbStream.write_short(s);
          break;
        case 'Z': 
          boolean bool = paramArrayOfObjectStreamField[i].getField().getBoolean(paramObject);
          orbStream.write_boolean(bool);
          break;
        case 'L': 
        case '[': 
          Object localObject = paramArrayOfObjectStreamField[i].getField().get(paramObject);
          writeObjectField(paramArrayOfObjectStreamField[i], localObject);
          break;
        case 'E': 
        case 'G': 
        case 'H': 
        case 'K': 
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
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw wrapper.illegalFieldAccess(localIllegalAccessException, paramArrayOfObjectStreamField[i].getName());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\io\IIOPOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */