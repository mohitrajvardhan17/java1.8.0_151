package java.util.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ZipInputStream
  extends InflaterInputStream
  implements ZipConstants
{
  private ZipEntry entry;
  private int flag;
  private CRC32 crc = new CRC32();
  private long remaining;
  private byte[] tmpbuf = new byte['Ȁ'];
  private static final int STORED = 0;
  private static final int DEFLATED = 8;
  private boolean closed = false;
  private boolean entryEOF = false;
  private ZipCoder zc;
  private byte[] b = new byte['Ā'];
  
  private void ensureOpen()
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream closed");
    }
  }
  
  public ZipInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, StandardCharsets.UTF_8);
  }
  
  public ZipInputStream(InputStream paramInputStream, Charset paramCharset)
  {
    super(new PushbackInputStream(paramInputStream, 512), new Inflater(true), 512);
    usesDefaultInflater = true;
    if (paramInputStream == null) {
      throw new NullPointerException("in is null");
    }
    if (paramCharset == null) {
      throw new NullPointerException("charset is null");
    }
    zc = ZipCoder.get(paramCharset);
  }
  
  public ZipEntry getNextEntry()
    throws IOException
  {
    ensureOpen();
    if (entry != null) {
      closeEntry();
    }
    crc.reset();
    inf.reset();
    if ((entry = readLOC()) == null) {
      return null;
    }
    if (entry.method == 0) {
      remaining = entry.size;
    }
    entryEOF = false;
    return entry;
  }
  
  public void closeEntry()
    throws IOException
  {
    ensureOpen();
    while (read(tmpbuf, 0, tmpbuf.length) != -1) {}
    entryEOF = true;
  }
  
  public int available()
    throws IOException
  {
    ensureOpen();
    if (entryEOF) {
      return 0;
    }
    return 1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    if (entry == null) {
      return -1;
    }
    switch (entry.method)
    {
    case 8: 
      paramInt2 = super.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt2 == -1)
      {
        readEnd(entry);
        entryEOF = true;
        entry = null;
      }
      else
      {
        crc.update(paramArrayOfByte, paramInt1, paramInt2);
      }
      return paramInt2;
    case 0: 
      if (remaining <= 0L)
      {
        entryEOF = true;
        entry = null;
        return -1;
      }
      if (paramInt2 > remaining) {
        paramInt2 = (int)remaining;
      }
      paramInt2 = in.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt2 == -1) {
        throw new ZipException("unexpected EOF");
      }
      crc.update(paramArrayOfByte, paramInt1, paramInt2);
      remaining -= paramInt2;
      if ((remaining == 0L) && (entry.crc != crc.getValue())) {
        throw new ZipException("invalid entry CRC (expected 0x" + Long.toHexString(entry.crc) + " but got 0x" + Long.toHexString(crc.getValue()) + ")");
      }
      return paramInt2;
    }
    throw new ZipException("invalid compression method");
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("negative skip length");
    }
    ensureOpen();
    int i = (int)Math.min(paramLong, 2147483647L);
    int j = 0;
    while (j < i)
    {
      int k = i - j;
      if (k > tmpbuf.length) {
        k = tmpbuf.length;
      }
      k = read(tmpbuf, 0, k);
      if (k == -1)
      {
        entryEOF = true;
        break;
      }
      j += k;
    }
    return j;
  }
  
  public void close()
    throws IOException
  {
    if (!closed)
    {
      super.close();
      closed = true;
    }
  }
  
  private ZipEntry readLOC()
    throws IOException
  {
    try
    {
      readFully(tmpbuf, 0, 30);
    }
    catch (EOFException localEOFException)
    {
      return null;
    }
    if (ZipUtils.get32(tmpbuf, 0) != 67324752L) {
      return null;
    }
    flag = ZipUtils.get16(tmpbuf, 6);
    int i = ZipUtils.get16(tmpbuf, 26);
    int j = b.length;
    if (i > j)
    {
      do
      {
        j *= 2;
      } while (i > j);
      b = new byte[j];
    }
    readFully(b, 0, i);
    ZipEntry localZipEntry = createZipEntry((flag & 0x800) != 0 ? zc.toStringUTF8(b, i) : zc.toString(b, i));
    if ((flag & 0x1) == 1) {
      throw new ZipException("encrypted ZIP entry not supported");
    }
    method = ZipUtils.get16(tmpbuf, 8);
    xdostime = ZipUtils.get32(tmpbuf, 10);
    if ((flag & 0x8) == 8)
    {
      if (method != 8) {
        throw new ZipException("only DEFLATED entries can have EXT descriptor");
      }
    }
    else
    {
      crc = ZipUtils.get32(tmpbuf, 14);
      csize = ZipUtils.get32(tmpbuf, 18);
      size = ZipUtils.get32(tmpbuf, 22);
    }
    i = ZipUtils.get16(tmpbuf, 28);
    if (i > 0)
    {
      byte[] arrayOfByte = new byte[i];
      readFully(arrayOfByte, 0, i);
      localZipEntry.setExtra0(arrayOfByte, (csize == 4294967295L) || (size == 4294967295L));
    }
    return localZipEntry;
  }
  
  protected ZipEntry createZipEntry(String paramString)
  {
    return new ZipEntry(paramString);
  }
  
  private void readEnd(ZipEntry paramZipEntry)
    throws IOException
  {
    int i = inf.getRemaining();
    if (i > 0) {
      ((PushbackInputStream)in).unread(buf, len - i, i);
    }
    if ((flag & 0x8) == 8)
    {
      long l;
      if ((inf.getBytesWritten() > 4294967295L) || (inf.getBytesRead() > 4294967295L))
      {
        readFully(tmpbuf, 0, 24);
        l = ZipUtils.get32(tmpbuf, 0);
        if (l != 134695760L)
        {
          crc = l;
          csize = ZipUtils.get64(tmpbuf, 4);
          size = ZipUtils.get64(tmpbuf, 12);
          ((PushbackInputStream)in).unread(tmpbuf, 19, 4);
        }
        else
        {
          crc = ZipUtils.get32(tmpbuf, 4);
          csize = ZipUtils.get64(tmpbuf, 8);
          size = ZipUtils.get64(tmpbuf, 16);
        }
      }
      else
      {
        readFully(tmpbuf, 0, 16);
        l = ZipUtils.get32(tmpbuf, 0);
        if (l != 134695760L)
        {
          crc = l;
          csize = ZipUtils.get32(tmpbuf, 4);
          size = ZipUtils.get32(tmpbuf, 8);
          ((PushbackInputStream)in).unread(tmpbuf, 11, 4);
        }
        else
        {
          crc = ZipUtils.get32(tmpbuf, 4);
          csize = ZipUtils.get32(tmpbuf, 8);
          size = ZipUtils.get32(tmpbuf, 12);
        }
      }
    }
    if (size != inf.getBytesWritten()) {
      throw new ZipException("invalid entry size (expected " + size + " but got " + inf.getBytesWritten() + " bytes)");
    }
    if (csize != inf.getBytesRead()) {
      throw new ZipException("invalid entry compressed size (expected " + csize + " but got " + inf.getBytesRead() + " bytes)");
    }
    if (crc != crc.getValue()) {
      throw new ZipException("invalid entry CRC (expected 0x" + Long.toHexString(crc) + " but got 0x" + Long.toHexString(crc.getValue()) + ")");
    }
  }
  
  private void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    while (paramInt2 > 0)
    {
      int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i == -1) {
        throw new EOFException();
      }
      paramInt1 += i;
      paramInt2 -= i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\ZipInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */