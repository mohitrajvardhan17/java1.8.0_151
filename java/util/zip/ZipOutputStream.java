package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import sun.security.action.GetPropertyAction;

public class ZipOutputStream
  extends DeflaterOutputStream
  implements ZipConstants
{
  private static final boolean inhibitZip64 = Boolean.parseBoolean((String)AccessController.doPrivileged(new GetPropertyAction("jdk.util.zip.inhibitZip64", "false")));
  private XEntry current;
  private Vector<XEntry> xentries = new Vector();
  private HashSet<String> names = new HashSet();
  private CRC32 crc = new CRC32();
  private long written = 0L;
  private long locoff = 0L;
  private byte[] comment;
  private int method = 8;
  private boolean finished;
  private boolean closed = false;
  private final ZipCoder zc;
  public static final int STORED = 0;
  public static final int DEFLATED = 8;
  
  private static int version(ZipEntry paramZipEntry)
    throws ZipException
  {
    switch (method)
    {
    case 8: 
      return 20;
    case 0: 
      return 10;
    }
    throw new ZipException("unsupported compression method");
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream closed");
    }
  }
  
  public ZipOutputStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, StandardCharsets.UTF_8);
  }
  
  public ZipOutputStream(OutputStream paramOutputStream, Charset paramCharset)
  {
    super(paramOutputStream, new Deflater(-1, true));
    if (paramCharset == null) {
      throw new NullPointerException("charset is null");
    }
    zc = ZipCoder.get(paramCharset);
    usesDefaultDeflater = true;
  }
  
  public void setComment(String paramString)
  {
    if (paramString != null)
    {
      comment = zc.getBytes(paramString);
      if (comment.length > 65535) {
        throw new IllegalArgumentException("ZIP file comment too long.");
      }
    }
  }
  
  public void setMethod(int paramInt)
  {
    if ((paramInt != 8) && (paramInt != 0)) {
      throw new IllegalArgumentException("invalid compression method");
    }
    method = paramInt;
  }
  
  public void setLevel(int paramInt)
  {
    def.setLevel(paramInt);
  }
  
  public void putNextEntry(ZipEntry paramZipEntry)
    throws IOException
  {
    ensureOpen();
    if (current != null) {
      closeEntry();
    }
    if (xdostime == -1L) {
      paramZipEntry.setTime(System.currentTimeMillis());
    }
    if (method == -1) {
      method = method;
    }
    flag = 0;
    switch (method)
    {
    case 8: 
      if ((size == -1L) || (csize == -1L) || (crc == -1L)) {
        flag = 8;
      }
      break;
    case 0: 
      if (size == -1L) {
        size = csize;
      } else if (csize == -1L) {
        csize = size;
      } else if (size != csize) {
        throw new ZipException("STORED entry where compressed != uncompressed size");
      }
      if ((size == -1L) || (crc == -1L)) {
        throw new ZipException("STORED entry missing size, compressed size, or crc-32");
      }
      break;
    default: 
      throw new ZipException("unsupported compression method");
    }
    if (!names.add(name)) {
      throw new ZipException("duplicate entry: " + name);
    }
    if (zc.isUTF8()) {
      flag |= 0x800;
    }
    current = new XEntry(paramZipEntry, written);
    xentries.add(current);
    writeLOC(current);
  }
  
  public void closeEntry()
    throws IOException
  {
    ensureOpen();
    if (current != null)
    {
      ZipEntry localZipEntry = current.entry;
      switch (method)
      {
      case 8: 
        def.finish();
        while (!def.finished()) {
          deflate();
        }
        if ((flag & 0x8) == 0)
        {
          if (size != def.getBytesRead()) {
            throw new ZipException("invalid entry size (expected " + size + " but got " + def.getBytesRead() + " bytes)");
          }
          if (csize != def.getBytesWritten()) {
            throw new ZipException("invalid entry compressed size (expected " + csize + " but got " + def.getBytesWritten() + " bytes)");
          }
          if (crc != crc.getValue()) {
            throw new ZipException("invalid entry CRC-32 (expected 0x" + Long.toHexString(crc) + " but got 0x" + Long.toHexString(crc.getValue()) + ")");
          }
        }
        else
        {
          size = def.getBytesRead();
          csize = def.getBytesWritten();
          crc = crc.getValue();
          writeEXT(localZipEntry);
        }
        def.reset();
        written += csize;
        break;
      case 0: 
        if (size != written - locoff) {
          throw new ZipException("invalid entry size (expected " + size + " but got " + (written - locoff) + " bytes)");
        }
        if (crc != crc.getValue()) {
          throw new ZipException("invalid entry crc-32 (expected 0x" + Long.toHexString(crc) + " but got 0x" + Long.toHexString(crc.getValue()) + ")");
        }
        break;
      default: 
        throw new ZipException("invalid compression method");
      }
      crc.reset();
      current = null;
    }
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return;
    }
    if (current == null) {
      throw new ZipException("no current ZIP entry");
    }
    ZipEntry localZipEntry = current.entry;
    switch (method)
    {
    case 8: 
      super.write(paramArrayOfByte, paramInt1, paramInt2);
      break;
    case 0: 
      written += paramInt2;
      if (written - locoff > size) {
        throw new ZipException("attempt to write past end of STORED entry");
      }
      out.write(paramArrayOfByte, paramInt1, paramInt2);
      break;
    default: 
      throw new ZipException("invalid compression method");
    }
    crc.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void finish()
    throws IOException
  {
    ensureOpen();
    if (finished) {
      return;
    }
    if (current != null) {
      closeEntry();
    }
    long l = written;
    Iterator localIterator = xentries.iterator();
    while (localIterator.hasNext())
    {
      XEntry localXEntry = (XEntry)localIterator.next();
      writeCEN(localXEntry);
    }
    writeEND(l, written - l);
    finished = true;
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
  
  private void writeLOC(XEntry paramXEntry)
    throws IOException
  {
    ZipEntry localZipEntry = entry;
    int i = flag;
    int j = 0;
    int k = getExtraLen(extra);
    writeInt(67324752L);
    if ((i & 0x8) == 8)
    {
      writeShort(version(localZipEntry));
      writeShort(i);
      writeShort(method);
      writeInt(xdostime);
      writeInt(0L);
      writeInt(0L);
      writeInt(0L);
    }
    else
    {
      if ((csize >= 4294967295L) || (size >= 4294967295L))
      {
        j = 1;
        writeShort(45);
      }
      else
      {
        writeShort(version(localZipEntry));
      }
      writeShort(i);
      writeShort(method);
      writeInt(xdostime);
      writeInt(crc);
      if (j != 0)
      {
        writeInt(4294967295L);
        writeInt(4294967295L);
        k += 20;
      }
      else
      {
        writeInt(csize);
        writeInt(size);
      }
    }
    byte[] arrayOfByte = zc.getBytes(name);
    writeShort(arrayOfByte.length);
    int m = 0;
    int n = 0;
    if (mtime != null)
    {
      m += 4;
      n |= 0x1;
    }
    if (atime != null)
    {
      m += 4;
      n |= 0x2;
    }
    if (ctime != null)
    {
      m += 4;
      n |= 0x4;
    }
    if (n != 0) {
      k += m + 5;
    }
    writeShort(k);
    writeBytes(arrayOfByte, 0, arrayOfByte.length);
    if (j != 0)
    {
      writeShort(1);
      writeShort(16);
      writeLong(size);
      writeLong(csize);
    }
    if (n != 0)
    {
      writeShort(21589);
      writeShort(m + 1);
      writeByte(n);
      if (mtime != null) {
        writeInt(ZipUtils.fileTimeToUnixTime(mtime));
      }
      if (atime != null) {
        writeInt(ZipUtils.fileTimeToUnixTime(atime));
      }
      if (ctime != null) {
        writeInt(ZipUtils.fileTimeToUnixTime(ctime));
      }
    }
    writeExtra(extra);
    locoff = written;
  }
  
  private void writeEXT(ZipEntry paramZipEntry)
    throws IOException
  {
    writeInt(134695760L);
    writeInt(crc);
    if ((csize >= 4294967295L) || (size >= 4294967295L))
    {
      writeLong(csize);
      writeLong(size);
    }
    else
    {
      writeInt(csize);
      writeInt(size);
    }
  }
  
  private void writeCEN(XEntry paramXEntry)
    throws IOException
  {
    ZipEntry localZipEntry = entry;
    int i = flag;
    int j = version(localZipEntry);
    long l1 = csize;
    long l2 = size;
    long l3 = offset;
    int k = 0;
    int m = 0;
    if (csize >= 4294967295L)
    {
      l1 = 4294967295L;
      k += 8;
      m = 1;
    }
    if (size >= 4294967295L)
    {
      l2 = 4294967295L;
      k += 8;
      m = 1;
    }
    if (offset >= 4294967295L)
    {
      l3 = 4294967295L;
      k += 8;
      m = 1;
    }
    writeInt(33639248L);
    if (m != 0)
    {
      writeShort(45);
      writeShort(45);
    }
    else
    {
      writeShort(j);
      writeShort(j);
    }
    writeShort(i);
    writeShort(method);
    writeInt(xdostime);
    writeInt(crc);
    writeInt(l1);
    writeInt(l2);
    byte[] arrayOfByte1 = zc.getBytes(name);
    writeShort(arrayOfByte1.length);
    int n = getExtraLen(extra);
    if (m != 0) {
      n += k + 4;
    }
    int i1 = 0;
    if (mtime != null)
    {
      n += 4;
      i1 |= 0x1;
    }
    if (atime != null) {
      i1 |= 0x2;
    }
    if (ctime != null) {
      i1 |= 0x4;
    }
    if (i1 != 0) {
      n += 5;
    }
    writeShort(n);
    byte[] arrayOfByte2;
    if (comment != null)
    {
      arrayOfByte2 = zc.getBytes(comment);
      writeShort(Math.min(arrayOfByte2.length, 65535));
    }
    else
    {
      arrayOfByte2 = null;
      writeShort(0);
    }
    writeShort(0);
    writeShort(0);
    writeInt(0L);
    writeInt(l3);
    writeBytes(arrayOfByte1, 0, arrayOfByte1.length);
    if (m != 0)
    {
      writeShort(1);
      writeShort(k);
      if (l2 == 4294967295L) {
        writeLong(size);
      }
      if (l1 == 4294967295L) {
        writeLong(csize);
      }
      if (l3 == 4294967295L) {
        writeLong(offset);
      }
    }
    if (i1 != 0)
    {
      writeShort(21589);
      if (mtime != null)
      {
        writeShort(5);
        writeByte(i1);
        writeInt(ZipUtils.fileTimeToUnixTime(mtime));
      }
      else
      {
        writeShort(1);
        writeByte(i1);
      }
    }
    writeExtra(extra);
    if (arrayOfByte2 != null) {
      writeBytes(arrayOfByte2, 0, Math.min(arrayOfByte2.length, 65535));
    }
  }
  
  private void writeEND(long paramLong1, long paramLong2)
    throws IOException
  {
    int i = 0;
    long l1 = paramLong2;
    long l2 = paramLong1;
    if (l1 >= 4294967295L)
    {
      l1 = 4294967295L;
      i = 1;
    }
    if (l2 >= 4294967295L)
    {
      l2 = 4294967295L;
      i = 1;
    }
    int j = xentries.size();
    if (j >= 65535)
    {
      i |= (!inhibitZip64 ? 1 : 0);
      if (i != 0) {
        j = 65535;
      }
    }
    if (i != 0)
    {
      long l3 = written;
      writeInt(101075792L);
      writeLong(44L);
      writeShort(45);
      writeShort(45);
      writeInt(0L);
      writeInt(0L);
      writeLong(xentries.size());
      writeLong(xentries.size());
      writeLong(paramLong2);
      writeLong(paramLong1);
      writeInt(117853008L);
      writeInt(0L);
      writeLong(l3);
      writeInt(1L);
    }
    writeInt(101010256L);
    writeShort(0);
    writeShort(0);
    writeShort(j);
    writeShort(j);
    writeInt(l1);
    writeInt(l2);
    if (comment != null)
    {
      writeShort(comment.length);
      writeBytes(comment, 0, comment.length);
    }
    else
    {
      writeShort(0);
    }
  }
  
  private int getExtraLen(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return 0;
    }
    int i = 0;
    int j = paramArrayOfByte.length;
    int k = 0;
    while (k + 4 <= j)
    {
      int m = ZipUtils.get16(paramArrayOfByte, k);
      int n = ZipUtils.get16(paramArrayOfByte, k + 2);
      if ((n < 0) || (k + 4 + n > j)) {
        break;
      }
      if ((m == 21589) || (m == 1)) {
        i += n + 4;
      }
      k += n + 4;
    }
    return j - i;
  }
  
  private void writeExtra(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte != null)
    {
      int i = paramArrayOfByte.length;
      int j = 0;
      while (j + 4 <= i)
      {
        int k = ZipUtils.get16(paramArrayOfByte, j);
        int m = ZipUtils.get16(paramArrayOfByte, j + 2);
        if ((m < 0) || (j + 4 + m > i))
        {
          writeBytes(paramArrayOfByte, j, i - j);
          return;
        }
        if ((k != 21589) && (k != 1)) {
          writeBytes(paramArrayOfByte, j, m + 4);
        }
        j += m + 4;
      }
      if (j < i) {
        writeBytes(paramArrayOfByte, j, i - j);
      }
    }
  }
  
  private void writeByte(int paramInt)
    throws IOException
  {
    OutputStream localOutputStream = out;
    localOutputStream.write(paramInt & 0xFF);
    written += 1L;
  }
  
  private void writeShort(int paramInt)
    throws IOException
  {
    OutputStream localOutputStream = out;
    localOutputStream.write(paramInt >>> 0 & 0xFF);
    localOutputStream.write(paramInt >>> 8 & 0xFF);
    written += 2L;
  }
  
  private void writeInt(long paramLong)
    throws IOException
  {
    OutputStream localOutputStream = out;
    localOutputStream.write((int)(paramLong >>> 0 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 8 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 16 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 24 & 0xFF));
    written += 4L;
  }
  
  private void writeLong(long paramLong)
    throws IOException
  {
    OutputStream localOutputStream = out;
    localOutputStream.write((int)(paramLong >>> 0 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 8 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 16 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 24 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 32 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 40 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 48 & 0xFF));
    localOutputStream.write((int)(paramLong >>> 56 & 0xFF));
    written += 8L;
  }
  
  private void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    out.write(paramArrayOfByte, paramInt1, paramInt2);
    written += paramInt2;
  }
  
  private static class XEntry
  {
    final ZipEntry entry;
    final long offset;
    
    public XEntry(ZipEntry paramZipEntry, long paramLong)
    {
      entry = paramZipEntry;
      offset = paramLong;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\ZipOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */