package sun.nio.cs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;

public class StreamDecoder
  extends Reader
{
  private static final int MIN_BYTE_BUFFER_SIZE = 32;
  private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
  private volatile boolean isOpen = true;
  private boolean haveLeftoverChar = false;
  private char leftoverChar;
  private static volatile boolean channelsAvailable = true;
  private Charset cs;
  private CharsetDecoder decoder;
  private ByteBuffer bb;
  private InputStream in;
  private ReadableByteChannel ch;
  
  private void ensureOpen()
    throws IOException
  {
    if (!isOpen) {
      throw new IOException("Stream closed");
    }
  }
  
  public static StreamDecoder forInputStreamReader(InputStream paramInputStream, Object paramObject, String paramString)
    throws UnsupportedEncodingException
  {
    String str = paramString;
    if (str == null) {
      str = Charset.defaultCharset().name();
    }
    try
    {
      if (Charset.isSupported(str)) {
        return new StreamDecoder(paramInputStream, paramObject, Charset.forName(str));
      }
    }
    catch (IllegalCharsetNameException localIllegalCharsetNameException) {}
    throw new UnsupportedEncodingException(str);
  }
  
  public static StreamDecoder forInputStreamReader(InputStream paramInputStream, Object paramObject, Charset paramCharset)
  {
    return new StreamDecoder(paramInputStream, paramObject, paramCharset);
  }
  
  public static StreamDecoder forInputStreamReader(InputStream paramInputStream, Object paramObject, CharsetDecoder paramCharsetDecoder)
  {
    return new StreamDecoder(paramInputStream, paramObject, paramCharsetDecoder);
  }
  
  public static StreamDecoder forDecoder(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder, int paramInt)
  {
    return new StreamDecoder(paramReadableByteChannel, paramCharsetDecoder, paramInt);
  }
  
  public String getEncoding()
  {
    if (isOpen()) {
      return encodingName();
    }
    return null;
  }
  
  public int read()
    throws IOException
  {
    return read0();
  }
  
  private int read0()
    throws IOException
  {
    synchronized (lock)
    {
      if (haveLeftoverChar)
      {
        haveLeftoverChar = false;
        return leftoverChar;
      }
      char[] arrayOfChar = new char[2];
      int i = read(arrayOfChar, 0, 2);
      switch (i)
      {
      case -1: 
        return -1;
      case 2: 
        leftoverChar = arrayOfChar[1];
        haveLeftoverChar = true;
      case 1: 
        return arrayOfChar[0];
      }
      if (!$assertionsDisabled) {
        throw new AssertionError(i);
      }
      return -1;
    }
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1;
    int j = paramInt2;
    synchronized (lock)
    {
      ensureOpen();
      if ((i < 0) || (i > paramArrayOfChar.length) || (j < 0) || (i + j > paramArrayOfChar.length) || (i + j < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (j == 0) {
        return 0;
      }
      int k = 0;
      if (haveLeftoverChar)
      {
        paramArrayOfChar[i] = leftoverChar;
        i++;
        j--;
        haveLeftoverChar = false;
        k = 1;
        if ((j == 0) || (!implReady())) {
          return k;
        }
      }
      if (j == 1)
      {
        int m = read0();
        if (m == -1) {
          return k == 0 ? -1 : k;
        }
        paramArrayOfChar[i] = ((char)m);
        return k + 1;
      }
      return k + implRead(paramArrayOfChar, i, i + j);
    }
  }
  
  public boolean ready()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      return (haveLeftoverChar) || (implReady());
    }
  }
  
  public void close()
    throws IOException
  {
    synchronized (lock)
    {
      if (!isOpen) {
        return;
      }
      implClose();
      isOpen = false;
    }
  }
  
  private boolean isOpen()
  {
    return isOpen;
  }
  
  private static FileChannel getChannel(FileInputStream paramFileInputStream)
  {
    if (!channelsAvailable) {
      return null;
    }
    try
    {
      return paramFileInputStream.getChannel();
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      channelsAvailable = false;
    }
    return null;
  }
  
  StreamDecoder(InputStream paramInputStream, Object paramObject, Charset paramCharset)
  {
    this(paramInputStream, paramObject, paramCharset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
  }
  
  StreamDecoder(InputStream paramInputStream, Object paramObject, CharsetDecoder paramCharsetDecoder)
  {
    super(paramObject);
    cs = paramCharsetDecoder.charset();
    decoder = paramCharsetDecoder;
    if (ch == null)
    {
      in = paramInputStream;
      ch = null;
      bb = ByteBuffer.allocate(8192);
    }
    bb.flip();
  }
  
  StreamDecoder(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder, int paramInt)
  {
    in = null;
    ch = paramReadableByteChannel;
    decoder = paramCharsetDecoder;
    cs = paramCharsetDecoder.charset();
    bb = ByteBuffer.allocate(paramInt < 32 ? 32 : paramInt < 0 ? 8192 : paramInt);
    bb.flip();
  }
  
  private int readBytes()
    throws IOException
  {
    bb.compact();
    try
    {
      int j;
      if (ch != null)
      {
        i = ch.read(bb);
        if (i < 0)
        {
          j = i;
          return j;
        }
      }
      else
      {
        i = bb.limit();
        j = bb.position();
        assert (j <= i);
        int k = j <= i ? i - j : 0;
        assert (k > 0);
        int m = in.read(bb.array(), bb.arrayOffset() + j, k);
        if (m < 0)
        {
          int n = m;
          return n;
        }
        if (m == 0) {
          throw new IOException("Underlying input stream returned zero bytes");
        }
        assert (m <= k) : ("n = " + m + ", rem = " + k);
        bb.position(j + m);
      }
    }
    finally
    {
      bb.flip();
    }
    int i = bb.remaining();
    assert (i != 0) : i;
    return i;
  }
  
  int implRead(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    assert (paramInt2 - paramInt1 > 1);
    CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2 - paramInt1);
    if (localCharBuffer.position() != 0) {
      localCharBuffer = localCharBuffer.slice();
    }
    boolean bool = false;
    for (;;)
    {
      CoderResult localCoderResult = decoder.decode(bb, localCharBuffer, bool);
      if (localCoderResult.isUnderflow())
      {
        if ((bool) || (!localCharBuffer.hasRemaining()) || ((localCharBuffer.position() > 0) && (!inReady()))) {
          break;
        }
        int i = readBytes();
        if (i < 0)
        {
          bool = true;
          if ((localCharBuffer.position() == 0) && (!bb.hasRemaining())) {
            break;
          }
          decoder.reset();
        }
      }
      else
      {
        if (localCoderResult.isOverflow())
        {
          if (($assertionsDisabled) || (localCharBuffer.position() > 0)) {
            break;
          }
          throw new AssertionError();
        }
        localCoderResult.throwException();
      }
    }
    if (bool) {
      decoder.reset();
    }
    if (localCharBuffer.position() == 0)
    {
      if (bool) {
        return -1;
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return localCharBuffer.position();
  }
  
  String encodingName()
  {
    return (cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)cs).historicalName() : cs.name();
  }
  
  private boolean inReady()
  {
    try
    {
      return ((in != null) && (in.available() > 0)) || ((ch instanceof FileChannel));
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  boolean implReady()
  {
    return (bb.hasRemaining()) || (inReady());
  }
  
  void implClose()
    throws IOException
  {
    if (ch != null) {
      ch.close();
    } else {
      in.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\StreamDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */