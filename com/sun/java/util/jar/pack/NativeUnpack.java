package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

class NativeUnpack
{
  private long unpackerPtr;
  private BufferedInputStream in;
  private int _verbose;
  private long _byteCount;
  private int _segCount;
  private int _fileCount;
  private long _estByteLimit;
  private int _estSegLimit;
  private int _estFileLimit;
  private int _prevPercent = -1;
  private final CRC32 _crc32 = new CRC32();
  private byte[] _buf = new byte['䀀'];
  private UnpackerImpl _p200;
  private PropMap _props;
  
  private static synchronized native void initIDs();
  
  private synchronized native long start(ByteBuffer paramByteBuffer, long paramLong);
  
  private synchronized native boolean getNextFile(Object[] paramArrayOfObject);
  
  private synchronized native ByteBuffer getUnusedInput();
  
  private synchronized native long finish();
  
  protected synchronized native boolean setOption(String paramString1, String paramString2);
  
  protected synchronized native String getOption(String paramString);
  
  NativeUnpack(UnpackerImpl paramUnpackerImpl)
  {
    _p200 = paramUnpackerImpl;
    _props = props;
    _nunp = this;
  }
  
  private static Object currentInstance()
  {
    UnpackerImpl localUnpackerImpl = (UnpackerImpl)Utils.getTLGlobals();
    return localUnpackerImpl == null ? null : _nunp;
  }
  
  private synchronized long getUnpackerPtr()
  {
    return unpackerPtr;
  }
  
  private long readInputFn(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    if (in == null) {
      return 0L;
    }
    long l1 = paramByteBuffer.capacity() - paramByteBuffer.position();
    assert (paramLong <= l1);
    long l2 = 0L;
    int i = 0;
    while (l2 < paramLong)
    {
      i++;
      int j = _buf.length;
      if (j > l1 - l2) {
        j = (int)(l1 - l2);
      }
      int k = in.read(_buf, 0, j);
      if (k <= 0) {
        break;
      }
      l2 += k;
      assert (l2 <= l1);
      paramByteBuffer.put(_buf, 0, k);
    }
    if (_verbose > 1) {
      Utils.log.fine("readInputFn(" + paramLong + "," + l1 + ") => " + l2 + " steps=" + i);
    }
    if (l1 > 100L) {
      _estByteLimit = (_byteCount + l1);
    } else {
      _estByteLimit = ((_byteCount + l2) * 20L);
    }
    _byteCount += l2;
    updateProgress();
    return l2;
  }
  
  private void updateProgress()
  {
    double d1 = _segCount;
    if ((_estByteLimit > 0L) && (_byteCount > 0L)) {
      d1 += _byteCount / _estByteLimit;
    }
    double d2 = _fileCount;
    double d3 = 0.33D * d1 / Math.max(_estSegLimit, 1) + 0.67D * d2 / Math.max(_estFileLimit, 1);
    int i = (int)Math.round(100.0D * d3);
    if (i > 100) {
      i = 100;
    }
    if (i > _prevPercent)
    {
      _prevPercent = i;
      _props.setInteger("unpack.progress", i);
      if (_verbose > 0) {
        Utils.log.info("progress = " + i);
      }
    }
  }
  
  private void copyInOption(String paramString)
  {
    String str = _props.getProperty(paramString);
    if (_verbose > 0) {
      Utils.log.info("set " + paramString + "=" + str);
    }
    if (str != null)
    {
      boolean bool = setOption(paramString, str);
      if (!bool) {
        Utils.log.warning("Invalid option " + paramString + "=" + str);
      }
    }
  }
  
  void run(InputStream paramInputStream, JarOutputStream paramJarOutputStream, ByteBuffer paramByteBuffer)
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
    in = localBufferedInputStream;
    _verbose = _props.getInteger("com.sun.java.util.jar.pack.verbose");
    int i = "keep".equals(_props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "0")) ? 0 : _props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
    copyInOption("com.sun.java.util.jar.pack.verbose");
    copyInOption("unpack.deflate.hint");
    if (i == 0) {
      copyInOption("com.sun.java.util.jar.pack.unpack.modification.time");
    }
    updateProgress();
    for (;;)
    {
      long l1 = start(paramByteBuffer, 0L);
      _byteCount = (_estByteLimit = 0L);
      _segCount += 1;
      int j = (int)(l1 >>> 32);
      int k = (int)(l1 >>> 0);
      _estSegLimit = (_segCount + j);
      double d = _fileCount + k;
      _estFileLimit = ((int)(d * _estSegLimit / _segCount));
      int[] arrayOfInt = { 0, 0, 0, 0 };
      Object[] arrayOfObject = { arrayOfInt, null, null, null };
      while (getNextFile(arrayOfObject))
      {
        String str = (String)arrayOfObject[1];
        long l3 = (arrayOfInt[0] << 32) + (arrayOfInt[1] << 32 >>> 32);
        long l4 = i != 0 ? i : arrayOfInt[2];
        boolean bool = arrayOfInt[3] != 0;
        ByteBuffer localByteBuffer1 = (ByteBuffer)arrayOfObject[2];
        ByteBuffer localByteBuffer2 = (ByteBuffer)arrayOfObject[3];
        writeEntry(paramJarOutputStream, str, l4, l3, bool, localByteBuffer1, localByteBuffer2);
        _fileCount += 1;
        updateProgress();
      }
      paramByteBuffer = getUnusedInput();
      long l2 = finish();
      if (_verbose > 0) {
        Utils.log.info("bytes consumed = " + l2);
      }
      if ((paramByteBuffer == null) && (!Utils.isPackMagic(Utils.readMagic(localBufferedInputStream)))) {
        break;
      }
      if ((_verbose > 0) && (paramByteBuffer != null)) {
        Utils.log.info("unused input = " + paramByteBuffer);
      }
    }
  }
  
  void run(InputStream paramInputStream, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    run(paramInputStream, paramJarOutputStream, null);
  }
  
  void run(File paramFile, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    ByteBuffer localByteBuffer = null;
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    Object localObject1 = null;
    try
    {
      run(localFileInputStream, paramJarOutputStream, localByteBuffer);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileInputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localFileInputStream.close();
        }
      }
    }
  }
  
  private void writeEntry(JarOutputStream paramJarOutputStream, String paramString, long paramLong1, long paramLong2, boolean paramBoolean, ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
    throws IOException
  {
    int i = (int)paramLong2;
    if (i != paramLong2) {
      throw new IOException("file too large: " + paramLong2);
    }
    CRC32 localCRC32 = _crc32;
    if (_verbose > 1) {
      Utils.log.fine("Writing entry: " + paramString + " size=" + i + (paramBoolean ? " deflated" : ""));
    }
    if (_buf.length < i)
    {
      j = i;
      while (j < _buf.length)
      {
        j <<= 1;
        if (j <= 0) {
          j = i;
        }
      }
      _buf = new byte[j];
    }
    assert (_buf.length >= i);
    int j = 0;
    int k;
    if (paramByteBuffer1 != null)
    {
      k = paramByteBuffer1.capacity();
      paramByteBuffer1.get(_buf, j, k);
      j += k;
    }
    if (paramByteBuffer2 != null)
    {
      k = paramByteBuffer2.capacity();
      paramByteBuffer2.get(_buf, j, k);
      j += k;
    }
    while (j < i)
    {
      k = in.read(_buf, j, i - j);
      if (k <= 0) {
        throw new IOException("EOF at end of archive");
      }
      j += k;
    }
    ZipEntry localZipEntry = new ZipEntry(paramString);
    localZipEntry.setTime(paramLong1 * 1000L);
    if (i == 0)
    {
      localZipEntry.setMethod(0);
      localZipEntry.setSize(0L);
      localZipEntry.setCrc(0L);
      localZipEntry.setCompressedSize(0L);
    }
    else if (!paramBoolean)
    {
      localZipEntry.setMethod(0);
      localZipEntry.setSize(i);
      localZipEntry.setCompressedSize(i);
      localCRC32.reset();
      localCRC32.update(_buf, 0, i);
      localZipEntry.setCrc(localCRC32.getValue());
    }
    else
    {
      localZipEntry.setMethod(8);
      localZipEntry.setSize(i);
    }
    paramJarOutputStream.putNextEntry(localZipEntry);
    if (i > 0) {
      paramJarOutputStream.write(_buf, 0, i);
    }
    paramJarOutputStream.closeEntry();
    if (_verbose > 0) {
      Utils.log.info("Writing " + Utils.zeString(localZipEntry));
    }
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("unpack");
        return null;
      }
    });
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\NativeUnpack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */