package sun.nio.cs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;

public class StreamEncoder
  extends Writer
{
  private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
  private volatile boolean isOpen = true;
  private Charset cs;
  private CharsetEncoder encoder;
  private ByteBuffer bb;
  private final OutputStream out;
  private WritableByteChannel ch;
  private boolean haveLeftoverChar = false;
  private char leftoverChar;
  private CharBuffer lcb = null;
  
  private void ensureOpen()
    throws IOException
  {
    if (!isOpen) {
      throw new IOException("Stream closed");
    }
  }
  
  public static StreamEncoder forOutputStreamWriter(OutputStream paramOutputStream, Object paramObject, String paramString)
    throws UnsupportedEncodingException
  {
    String str = paramString;
    if (str == null) {
      str = Charset.defaultCharset().name();
    }
    try
    {
      if (Charset.isSupported(str)) {
        return new StreamEncoder(paramOutputStream, paramObject, Charset.forName(str));
      }
    }
    catch (IllegalCharsetNameException localIllegalCharsetNameException) {}
    throw new UnsupportedEncodingException(str);
  }
  
  public static StreamEncoder forOutputStreamWriter(OutputStream paramOutputStream, Object paramObject, Charset paramCharset)
  {
    return new StreamEncoder(paramOutputStream, paramObject, paramCharset);
  }
  
  public static StreamEncoder forOutputStreamWriter(OutputStream paramOutputStream, Object paramObject, CharsetEncoder paramCharsetEncoder)
  {
    return new StreamEncoder(paramOutputStream, paramObject, paramCharsetEncoder);
  }
  
  public static StreamEncoder forEncoder(WritableByteChannel paramWritableByteChannel, CharsetEncoder paramCharsetEncoder, int paramInt)
  {
    return new StreamEncoder(paramWritableByteChannel, paramCharsetEncoder, paramInt);
  }
  
  public String getEncoding()
  {
    if (isOpen()) {
      return encodingName();
    }
    return null;
  }
  
  public void flushBuffer()
    throws IOException
  {
    synchronized (lock)
    {
      if (isOpen()) {
        implFlushBuffer();
      } else {
        throw new IOException("Stream closed");
      }
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    char[] arrayOfChar = new char[1];
    arrayOfChar[0] = ((char)paramInt);
    write(arrayOfChar, 0, 1);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (paramInt2 == 0) {
        return;
      }
      implWrite(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0) {
      throw new IndexOutOfBoundsException();
    }
    char[] arrayOfChar = new char[paramInt2];
    paramString.getChars(paramInt1, paramInt1 + paramInt2, arrayOfChar, 0);
    write(arrayOfChar, 0, paramInt2);
  }
  
  public void flush()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      implFlush();
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
  
  private StreamEncoder(OutputStream paramOutputStream, Object paramObject, Charset paramCharset)
  {
    this(paramOutputStream, paramObject, paramCharset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
  }
  
  private StreamEncoder(OutputStream paramOutputStream, Object paramObject, CharsetEncoder paramCharsetEncoder)
  {
    super(paramObject);
    out = paramOutputStream;
    ch = null;
    cs = paramCharsetEncoder.charset();
    encoder = paramCharsetEncoder;
    if (ch == null) {
      bb = ByteBuffer.allocate(8192);
    }
  }
  
  private StreamEncoder(WritableByteChannel paramWritableByteChannel, CharsetEncoder paramCharsetEncoder, int paramInt)
  {
    out = null;
    ch = paramWritableByteChannel;
    cs = paramCharsetEncoder.charset();
    encoder = paramCharsetEncoder;
    bb = ByteBuffer.allocate(paramInt < 0 ? 8192 : paramInt);
  }
  
  private void writeBytes()
    throws IOException
  {
    bb.flip();
    int i = bb.limit();
    int j = bb.position();
    assert (j <= i);
    int k = j <= i ? i - j : 0;
    if (k > 0) {
      if (ch != null)
      {
        if ((ch.write(bb) != k) && (!$assertionsDisabled)) {
          throw new AssertionError(k);
        }
      }
      else {
        out.write(bb.array(), bb.arrayOffset() + j, k);
      }
    }
    bb.clear();
  }
  
  private void flushLeftoverChar(CharBuffer paramCharBuffer, boolean paramBoolean)
    throws IOException
  {
    if ((!haveLeftoverChar) && (!paramBoolean)) {
      return;
    }
    if (lcb == null) {
      lcb = CharBuffer.allocate(2);
    } else {
      lcb.clear();
    }
    if (haveLeftoverChar) {
      lcb.put(leftoverChar);
    }
    if ((paramCharBuffer != null) && (paramCharBuffer.hasRemaining())) {
      lcb.put(paramCharBuffer.get());
    }
    lcb.flip();
    while ((lcb.hasRemaining()) || (paramBoolean))
    {
      CoderResult localCoderResult = encoder.encode(lcb, bb, paramBoolean);
      if (localCoderResult.isUnderflow())
      {
        if (!lcb.hasRemaining()) {
          break;
        }
        leftoverChar = lcb.get();
        if ((paramCharBuffer != null) && (paramCharBuffer.hasRemaining())) {
          flushLeftoverChar(paramCharBuffer, paramBoolean);
        }
        return;
      }
      if (localCoderResult.isOverflow())
      {
        assert (bb.position() > 0);
        writeBytes();
      }
      else
      {
        localCoderResult.throwException();
      }
    }
    haveLeftoverChar = false;
  }
  
  void implWrite(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    if (haveLeftoverChar) {
      flushLeftoverChar(localCharBuffer, false);
    }
    while (localCharBuffer.hasRemaining())
    {
      CoderResult localCoderResult = encoder.encode(localCharBuffer, bb, false);
      if (localCoderResult.isUnderflow())
      {
        assert (localCharBuffer.remaining() <= 1) : localCharBuffer.remaining();
        if (localCharBuffer.remaining() != 1) {
          break;
        }
        haveLeftoverChar = true;
        leftoverChar = localCharBuffer.get();
        break;
      }
      if (localCoderResult.isOverflow())
      {
        assert (bb.position() > 0);
        writeBytes();
      }
      else
      {
        localCoderResult.throwException();
      }
    }
  }
  
  void implFlushBuffer()
    throws IOException
  {
    if (bb.position() > 0) {
      writeBytes();
    }
  }
  
  void implFlush()
    throws IOException
  {
    implFlushBuffer();
    if (out != null) {
      out.flush();
    }
  }
  
  void implClose()
    throws IOException
  {
    flushLeftoverChar(null, true);
    try
    {
      for (;;)
      {
        CoderResult localCoderResult = encoder.flush(bb);
        if (localCoderResult.isUnderflow()) {
          break;
        }
        if (localCoderResult.isOverflow())
        {
          assert (bb.position() > 0);
          writeBytes();
        }
        else
        {
          localCoderResult.throwException();
        }
      }
      if (bb.position() > 0) {
        writeBytes();
      }
      if (ch != null) {
        ch.close();
      } else {
        out.close();
      }
    }
    catch (IOException localIOException)
    {
      encoder.reset();
      throw localIOException;
    }
  }
  
  String encodingName()
  {
    return (cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)cs).historicalName() : cs.name();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\StreamEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */