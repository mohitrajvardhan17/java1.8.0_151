package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

public class UnpackerImpl
  extends TLGlobals
  implements Pack200.Unpacker
{
  Object _nunp;
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    props.addListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    props.removeListener(paramPropertyChangeListener);
  }
  
  public UnpackerImpl() {}
  
  public SortedMap<String, String> properties()
  {
    return props;
  }
  
  public String toString()
  {
    return Utils.getVersionString();
  }
  
  public synchronized void unpack(InputStream paramInputStream, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    if (paramInputStream == null) {
      throw new NullPointerException("null input");
    }
    if (paramJarOutputStream == null) {
      throw new NullPointerException("null output");
    }
    assert (Utils.currentInstance.get() == null);
    int i = !props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try
    {
      Utils.currentInstance.set(this);
      if (i != 0) {
        Utils.changeDefaultTimeZoneToUtc();
      }
      int j = props.getInteger("com.sun.java.util.jar.pack.verbose");
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
      if (Utils.isJarMagic(Utils.readMagic(localBufferedInputStream)))
      {
        if (j > 0) {
          Utils.log.info("Copying unpacked JAR file...");
        }
        Utils.copyJarFile(new JarInputStream(localBufferedInputStream), paramJarOutputStream);
      }
      else if (props.getBoolean("com.sun.java.util.jar.pack.disable.native"))
      {
        new DoUnpack(null).run(localBufferedInputStream, paramJarOutputStream);
        localBufferedInputStream.close();
        Utils.markJarFile(paramJarOutputStream);
      }
      else
      {
        try
        {
          new NativeUnpack(this).run(localBufferedInputStream, paramJarOutputStream);
        }
        catch (UnsatisfiedLinkError|NoClassDefFoundError localUnsatisfiedLinkError)
        {
          new DoUnpack(null).run(localBufferedInputStream, paramJarOutputStream);
        }
        localBufferedInputStream.close();
        Utils.markJarFile(paramJarOutputStream);
      }
    }
    finally
    {
      _nunp = null;
      Utils.currentInstance.set(null);
      if (i != 0) {
        Utils.restoreDefaultTimeZone();
      }
    }
  }
  
  public synchronized void unpack(File paramFile, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    if (paramFile == null) {
      throw new NullPointerException("null input");
    }
    if (paramJarOutputStream == null) {
      throw new NullPointerException("null output");
    }
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    Object localObject1 = null;
    try
    {
      unpack(localFileInputStream, paramJarOutputStream);
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
    if (props.getBoolean("com.sun.java.util.jar.pack.unpack.remove.packfile")) {
      paramFile.delete();
    }
  }
  
  private class DoUnpack
  {
    final int verbose = props.getInteger("com.sun.java.util.jar.pack.verbose");
    final Package pkg;
    final boolean keepModtime;
    final boolean keepDeflateHint;
    final int modtime;
    final boolean deflateHint;
    final CRC32 crc;
    final ByteArrayOutputStream bufOut;
    final OutputStream crcOut;
    
    private DoUnpack()
    {
      props.setInteger("unpack.progress", 0);
      pkg = new Package();
      keepModtime = "keep".equals(props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "keep"));
      keepDeflateHint = "keep".equals(props.getProperty("unpack.deflate.hint", "keep"));
      if (!keepModtime) {
        modtime = props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
      } else {
        modtime = pkg.default_modtime;
      }
      deflateHint = (keepDeflateHint ? false : props.getBoolean("unpack.deflate.hint"));
      crc = new CRC32();
      bufOut = new ByteArrayOutputStream();
      crcOut = new CheckedOutputStream(bufOut, crc);
    }
    
    public void run(BufferedInputStream paramBufferedInputStream, JarOutputStream paramJarOutputStream)
      throws IOException
    {
      if (verbose > 0) {
        props.list(System.out);
      }
      for (int i = 1;; i++)
      {
        unpackSegment(paramBufferedInputStream, paramJarOutputStream);
        if (!Utils.isPackMagic(Utils.readMagic(paramBufferedInputStream))) {
          break;
        }
        if (verbose > 0) {
          Utils.log.info("Finished segment #" + i);
        }
      }
    }
    
    private void unpackSegment(InputStream paramInputStream, JarOutputStream paramJarOutputStream)
      throws IOException
    {
      props.setProperty("unpack.progress", "0");
      new PackageReader(pkg, paramInputStream).read();
      if (props.getBoolean("unpack.strip.debug")) {
        pkg.stripAttributeKind("Debug");
      }
      if (props.getBoolean("unpack.strip.compile")) {
        pkg.stripAttributeKind("Compile");
      }
      props.setProperty("unpack.progress", "50");
      pkg.ensureAllClassFiles();
      HashSet localHashSet = new HashSet(pkg.getClasses());
      Iterator localIterator = pkg.getFiles().iterator();
      while (localIterator.hasNext())
      {
        Package.File localFile = (Package.File)localIterator.next();
        String str = nameString;
        JarEntry localJarEntry = new JarEntry(Utils.getJarEntryName(str));
        boolean bool = keepDeflateHint ? false : ((options & 0x1) != 0) || ((pkg.default_options & 0x20) != 0) ? true : deflateHint;
        int i = !bool ? 1 : 0;
        if (i != 0) {
          crc.reset();
        }
        bufOut.reset();
        if (localFile.isClassStub())
        {
          Package.Class localClass = localFile.getStubClass();
          assert (localClass != null);
          new ClassWriter(localClass, i != 0 ? crcOut : bufOut).write();
          localHashSet.remove(localClass);
        }
        else
        {
          localFile.writeTo(i != 0 ? crcOut : bufOut);
        }
        localJarEntry.setMethod(bool ? 8 : 0);
        if (i != 0)
        {
          if (verbose > 0) {
            Utils.log.info("stored size=" + bufOut.size() + " and crc=" + crc.getValue());
          }
          localJarEntry.setMethod(0);
          localJarEntry.setSize(bufOut.size());
          localJarEntry.setCrc(crc.getValue());
        }
        if (keepModtime)
        {
          localJarEntry.setTime(modtime);
          localJarEntry.setTime(modtime * 1000L);
        }
        else
        {
          localJarEntry.setTime(modtime * 1000L);
        }
        paramJarOutputStream.putNextEntry(localJarEntry);
        bufOut.writeTo(paramJarOutputStream);
        paramJarOutputStream.closeEntry();
        if (verbose > 0) {
          Utils.log.info("Writing " + Utils.zeString(localJarEntry));
        }
      }
      assert (localHashSet.isEmpty());
      props.setProperty("unpack.progress", "100");
      pkg.reset();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\UnpackerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */