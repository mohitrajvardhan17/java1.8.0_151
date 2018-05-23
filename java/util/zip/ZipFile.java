package java.util.zip;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterators;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.misc.JavaUtilZipFileAccess;
import sun.misc.PerfCounter;
import sun.misc.SharedSecrets;
import sun.misc.VM;

public class ZipFile
  implements ZipConstants, Closeable
{
  private long jzfile;
  private final String name;
  private final int total;
  private final boolean locsig;
  private volatile boolean closeRequested = false;
  private static final int STORED = 0;
  private static final int DEFLATED = 8;
  public static final int OPEN_READ = 1;
  public static final int OPEN_DELETE = 4;
  private static final boolean usemmap;
  private static final boolean ensuretrailingslash;
  private ZipCoder zc;
  private final Map<InputStream, Inflater> streams = new WeakHashMap();
  private Deque<Inflater> inflaterCache = new ArrayDeque();
  private static final int JZENTRY_NAME = 0;
  private static final int JZENTRY_EXTRA = 1;
  private static final int JZENTRY_COMMENT = 2;
  
  private static native void initIDs();
  
  public ZipFile(String paramString)
    throws IOException
  {
    this(new File(paramString), 1);
  }
  
  public ZipFile(File paramFile, int paramInt)
    throws IOException
  {
    this(paramFile, paramInt, StandardCharsets.UTF_8);
  }
  
  public ZipFile(File paramFile)
    throws ZipException, IOException
  {
    this(paramFile, 1);
  }
  
  public ZipFile(File paramFile, int paramInt, Charset paramCharset)
    throws IOException
  {
    if (((paramInt & 0x1) == 0) || ((paramInt & 0xFFFFFFFA) != 0)) {
      throw new IllegalArgumentException("Illegal mode: 0x" + Integer.toHexString(paramInt));
    }
    String str = paramFile.getPath();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkRead(str);
      if ((paramInt & 0x4) != 0) {
        localSecurityManager.checkDelete(str);
      }
    }
    if (paramCharset == null) {
      throw new NullPointerException("charset is null");
    }
    zc = ZipCoder.get(paramCharset);
    long l = System.nanoTime();
    jzfile = open(str, paramInt, paramFile.lastModified(), usemmap);
    PerfCounter.getZipFileOpenTime().addElapsedTimeFrom(l);
    PerfCounter.getZipFileCount().increment();
    name = str;
    total = getTotal(jzfile);
    locsig = startsWithLOC(jzfile);
  }
  
  public ZipFile(String paramString, Charset paramCharset)
    throws IOException
  {
    this(new File(paramString), 1, paramCharset);
  }
  
  public ZipFile(File paramFile, Charset paramCharset)
    throws IOException
  {
    this(paramFile, 1, paramCharset);
  }
  
  public String getComment()
  {
    synchronized (this)
    {
      ensureOpen();
      byte[] arrayOfByte = getCommentBytes(jzfile);
      if (arrayOfByte == null) {
        return null;
      }
      return zc.toString(arrayOfByte, arrayOfByte.length);
    }
  }
  
  public ZipEntry getEntry(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name");
    }
    long l = 0L;
    synchronized (this)
    {
      ensureOpen();
      l = getEntry(jzfile, zc.getBytes(paramString), true);
      if (l != 0L)
      {
        ZipEntry localZipEntry = ensuretrailingslash ? getZipEntry(null, l) : getZipEntry(paramString, l);
        freeEntry(jzfile, l);
        return localZipEntry;
      }
    }
    return null;
  }
  
  private static native long getEntry(long paramLong, byte[] paramArrayOfByte, boolean paramBoolean);
  
  private static native void freeEntry(long paramLong1, long paramLong2);
  
  public InputStream getInputStream(ZipEntry paramZipEntry)
    throws IOException
  {
    if (paramZipEntry == null) {
      throw new NullPointerException("entry");
    }
    long l1 = 0L;
    ZipFileInputStream localZipFileInputStream = null;
    synchronized (this)
    {
      ensureOpen();
      if ((!zc.isUTF8()) && ((flag & 0x800) != 0)) {
        l1 = getEntry(jzfile, zc.getBytesUTF8(name), false);
      } else {
        l1 = getEntry(jzfile, zc.getBytes(name), false);
      }
      if (l1 == 0L) {
        return null;
      }
      localZipFileInputStream = new ZipFileInputStream(l1);
      switch (getEntryMethod(l1))
      {
      case 0: 
        synchronized (streams)
        {
          streams.put(localZipFileInputStream, null);
        }
        return localZipFileInputStream;
      case 8: 
        long l2 = getEntrySize(l1) + 2L;
        if (l2 > 65536L) {
          l2 = 8192L;
        }
        if (l2 <= 0L) {
          l2 = 4096L;
        }
        Inflater localInflater = getInflater();
        ZipFileInflaterInputStream localZipFileInflaterInputStream = new ZipFileInflaterInputStream(localZipFileInputStream, localInflater, (int)l2);
        synchronized (streams)
        {
          streams.put(localZipFileInflaterInputStream, localInflater);
        }
        return localZipFileInflaterInputStream;
      }
      throw new ZipException("invalid compression method");
    }
  }
  
  private Inflater getInflater()
  {
    synchronized (inflaterCache)
    {
      Inflater localInflater;
      while (null != (localInflater = (Inflater)inflaterCache.poll())) {
        if (false == localInflater.ended()) {
          return localInflater;
        }
      }
    }
    return new Inflater(true);
  }
  
  private void releaseInflater(Inflater paramInflater)
  {
    if (false == paramInflater.ended())
    {
      paramInflater.reset();
      synchronized (inflaterCache)
      {
        inflaterCache.add(paramInflater);
      }
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public Enumeration<? extends ZipEntry> entries()
  {
    return new ZipEntryIterator();
  }
  
  public Stream<? extends ZipEntry> stream()
  {
    return StreamSupport.stream(Spliterators.spliterator(new ZipEntryIterator(), size(), 1297), false);
  }
  
  private ZipEntry getZipEntry(String paramString, long paramLong)
  {
    ZipEntry localZipEntry = new ZipEntry();
    flag = getEntryFlag(paramLong);
    if (paramString != null)
    {
      name = paramString;
    }
    else
    {
      arrayOfByte = getEntryBytes(paramLong, 0);
      if (arrayOfByte == null) {
        name = "";
      } else if ((!zc.isUTF8()) && ((flag & 0x800) != 0)) {
        name = zc.toStringUTF8(arrayOfByte, arrayOfByte.length);
      } else {
        name = zc.toString(arrayOfByte, arrayOfByte.length);
      }
    }
    xdostime = getEntryTime(paramLong);
    crc = getEntryCrc(paramLong);
    size = getEntrySize(paramLong);
    csize = getEntryCSize(paramLong);
    method = getEntryMethod(paramLong);
    localZipEntry.setExtra0(getEntryBytes(paramLong, 1), false);
    byte[] arrayOfByte = getEntryBytes(paramLong, 2);
    if (arrayOfByte == null) {
      comment = null;
    } else if ((!zc.isUTF8()) && ((flag & 0x800) != 0)) {
      comment = zc.toStringUTF8(arrayOfByte, arrayOfByte.length);
    } else {
      comment = zc.toString(arrayOfByte, arrayOfByte.length);
    }
    return localZipEntry;
  }
  
  private static native long getNextEntry(long paramLong, int paramInt);
  
  public int size()
  {
    ensureOpen();
    return total;
  }
  
  public void close()
    throws IOException
  {
    if (closeRequested) {
      return;
    }
    closeRequested = true;
    synchronized (this)
    {
      synchronized (streams)
      {
        if (false == streams.isEmpty())
        {
          HashMap localHashMap = new HashMap(streams);
          streams.clear();
          Iterator localIterator = localHashMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            ((InputStream)localEntry.getKey()).close();
            Inflater localInflater = (Inflater)localEntry.getValue();
            if (localInflater != null) {
              localInflater.end();
            }
          }
        }
      }
      synchronized (inflaterCache)
      {
        while (null != (??? = (Inflater)inflaterCache.poll())) {
          ((Inflater)???).end();
        }
      }
      if (jzfile != 0L)
      {
        long l = jzfile;
        jzfile = 0L;
        close(l);
      }
    }
  }
  
  protected void finalize()
    throws IOException
  {
    close();
  }
  
  private static native void close(long paramLong);
  
  private void ensureOpen()
  {
    if (closeRequested) {
      throw new IllegalStateException("zip file closed");
    }
    if (jzfile == 0L) {
      throw new IllegalStateException("The object is not initialized.");
    }
  }
  
  private void ensureOpenOrZipException()
    throws IOException
  {
    if (closeRequested) {
      throw new ZipException("ZipFile closed");
    }
  }
  
  private boolean startsWithLocHeader()
  {
    return locsig;
  }
  
  private static native long open(String paramString, int paramInt, long paramLong, boolean paramBoolean)
    throws IOException;
  
  private static native int getTotal(long paramLong);
  
  private static native boolean startsWithLOC(long paramLong);
  
  private static native int read(long paramLong1, long paramLong2, long paramLong3, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native long getEntryTime(long paramLong);
  
  private static native long getEntryCrc(long paramLong);
  
  private static native long getEntryCSize(long paramLong);
  
  private static native long getEntrySize(long paramLong);
  
  private static native int getEntryMethod(long paramLong);
  
  private static native int getEntryFlag(long paramLong);
  
  private static native byte[] getCommentBytes(long paramLong);
  
  private static native byte[] getEntryBytes(long paramLong, int paramInt);
  
  private static native String getZipMessage(long paramLong);
  
  static
  {
    initIDs();
    String str = VM.getSavedProperty("sun.zip.disableMemoryMapping");
    usemmap = (str == null) || ((str.length() != 0) && (!str.equalsIgnoreCase("true")));
    str = VM.getSavedProperty("jdk.util.zip.ensureTrailingSlash");
    ensuretrailingslash = (str == null) || (!str.equalsIgnoreCase("false"));
    SharedSecrets.setJavaUtilZipFileAccess(new JavaUtilZipFileAccess()
    {
      public boolean startsWithLocHeader(ZipFile paramAnonymousZipFile)
      {
        return paramAnonymousZipFile.startsWithLocHeader();
      }
    });
  }
  
  private class ZipEntryIterator
    implements Enumeration<ZipEntry>, Iterator<ZipEntry>
  {
    private int i = 0;
    
    public ZipEntryIterator()
    {
      ZipFile.this.ensureOpen();
    }
    
    public boolean hasMoreElements()
    {
      return hasNext();
    }
    
    public boolean hasNext()
    {
      synchronized (ZipFile.this)
      {
        ZipFile.this.ensureOpen();
        return i < total;
      }
    }
    
    public ZipEntry nextElement()
    {
      return next();
    }
    
    public ZipEntry next()
    {
      synchronized (ZipFile.this)
      {
        ZipFile.this.ensureOpen();
        if (i >= total) {
          throw new NoSuchElementException();
        }
        long l = ZipFile.getNextEntry(jzfile, i++);
        if (l == 0L)
        {
          if (closeRequested) {
            localObject1 = "ZipFile concurrently closed";
          } else {
            localObject1 = ZipFile.getZipMessage(jzfile);
          }
          throw new ZipError("jzentry == 0,\n jzfile = " + jzfile + ",\n total = " + total + ",\n name = " + name + ",\n i = " + i + ",\n message = " + (String)localObject1);
        }
        Object localObject1 = ZipFile.this.getZipEntry(null, l);
        ZipFile.freeEntry(jzfile, l);
        return (ZipEntry)localObject1;
      }
    }
  }
  
  private class ZipFileInflaterInputStream
    extends InflaterInputStream
  {
    private volatile boolean closeRequested = false;
    private boolean eof = false;
    private final ZipFile.ZipFileInputStream zfin;
    
    ZipFileInflaterInputStream(ZipFile.ZipFileInputStream paramZipFileInputStream, Inflater paramInflater, int paramInt)
    {
      super(paramInflater, paramInt);
      zfin = paramZipFileInputStream;
    }
    
    public void close()
      throws IOException
    {
      if (closeRequested) {
        return;
      }
      closeRequested = true;
      super.close();
      Inflater localInflater;
      synchronized (streams)
      {
        localInflater = (Inflater)streams.remove(this);
      }
      if (localInflater != null) {
        ZipFile.this.releaseInflater(localInflater);
      }
    }
    
    protected void fill()
      throws IOException
    {
      if (eof) {
        throw new EOFException("Unexpected end of ZLIB input stream");
      }
      len = in.read(buf, 0, buf.length);
      if (len == -1)
      {
        buf[0] = 0;
        len = 1;
        eof = true;
      }
      inf.setInput(buf, 0, len);
    }
    
    public int available()
      throws IOException
    {
      if (closeRequested) {
        return 0;
      }
      long l = zfin.size() - inf.getBytesWritten();
      return l > 2147483647L ? Integer.MAX_VALUE : (int)l;
    }
    
    protected void finalize()
      throws Throwable
    {
      close();
    }
  }
  
  private class ZipFileInputStream
    extends InputStream
  {
    private volatile boolean zfisCloseRequested = false;
    protected long jzentry;
    private long pos = 0L;
    protected long rem;
    protected long size;
    
    ZipFileInputStream(long paramLong)
    {
      rem = ZipFile.getEntryCSize(paramLong);
      size = ZipFile.getEntrySize(paramLong);
      jzentry = paramLong;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      synchronized (ZipFile.this)
      {
        long l1 = rem;
        long l2 = pos;
        if (l1 == 0L) {
          return -1;
        }
        if (paramInt2 <= 0) {
          return 0;
        }
        if (paramInt2 > l1) {
          paramInt2 = (int)l1;
        }
        ZipFile.this.ensureOpenOrZipException();
        paramInt2 = ZipFile.read(jzfile, jzentry, l2, paramArrayOfByte, paramInt1, paramInt2);
        if (paramInt2 > 0)
        {
          pos = (l2 + paramInt2);
          rem = (l1 - paramInt2);
        }
      }
      if (rem == 0L) {
        close();
      }
      return paramInt2;
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      if (read(arrayOfByte, 0, 1) == 1) {
        return arrayOfByte[0] & 0xFF;
      }
      return -1;
    }
    
    public long skip(long paramLong)
    {
      if (paramLong > rem) {
        paramLong = rem;
      }
      pos += paramLong;
      rem -= paramLong;
      if (rem == 0L) {
        close();
      }
      return paramLong;
    }
    
    public int available()
    {
      return rem > 2147483647L ? Integer.MAX_VALUE : (int)rem;
    }
    
    public long size()
    {
      return size;
    }
    
    public void close()
    {
      if (zfisCloseRequested) {
        return;
      }
      zfisCloseRequested = true;
      rem = 0L;
      synchronized (ZipFile.this)
      {
        if ((jzentry != 0L) && (jzfile != 0L))
        {
          ZipFile.freeEntry(jzfile, jzentry);
          jzentry = 0L;
        }
      }
      synchronized (streams)
      {
        streams.remove(this);
      }
    }
    
    protected void finalize()
    {
      close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\ZipFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */