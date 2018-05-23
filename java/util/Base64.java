package java.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Base64
{
  private Base64() {}
  
  public static Encoder getEncoder()
  {
    return Encoder.RFC4648;
  }
  
  public static Encoder getUrlEncoder()
  {
    return Encoder.RFC4648_URLSAFE;
  }
  
  public static Encoder getMimeEncoder()
  {
    return Encoder.RFC2045;
  }
  
  public static Encoder getMimeEncoder(int paramInt, byte[] paramArrayOfByte)
  {
    Objects.requireNonNull(paramArrayOfByte);
    int[] arrayOfInt = Decoder.fromBase64;
    for (int k : paramArrayOfByte) {
      if (arrayOfInt[(k & 0xFF)] != -1) {
        throw new IllegalArgumentException("Illegal base64 line separator character 0x" + Integer.toString(k, 16));
      }
    }
    if (paramInt <= 0) {
      return Encoder.RFC4648;
    }
    return new Encoder(false, paramArrayOfByte, paramInt >> 2 << 2, true, null);
  }
  
  public static Decoder getDecoder()
  {
    return Decoder.RFC4648;
  }
  
  public static Decoder getUrlDecoder()
  {
    return Decoder.RFC4648_URLSAFE;
  }
  
  public static Decoder getMimeDecoder()
  {
    return Decoder.RFC2045;
  }
  
  private static class DecInputStream
    extends InputStream
  {
    private final InputStream is;
    private final boolean isMIME;
    private final int[] base64;
    private int bits = 0;
    private int nextin = 18;
    private int nextout = -8;
    private boolean eof = false;
    private boolean closed = false;
    private byte[] sbBuf = new byte[1];
    
    DecInputStream(InputStream paramInputStream, int[] paramArrayOfInt, boolean paramBoolean)
    {
      is = paramInputStream;
      base64 = paramArrayOfInt;
      isMIME = paramBoolean;
    }
    
    public int read()
      throws IOException
    {
      return read(sbBuf, 0, 1) == -1 ? -1 : sbBuf[0] & 0xFF;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (closed) {
        throw new IOException("Stream is closed");
      }
      if ((eof) && (nextout < 0)) {
        return -1;
      }
      if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
        throw new IndexOutOfBoundsException();
      }
      int i = paramInt1;
      if (nextout >= 0)
      {
        do
        {
          if (paramInt2 == 0) {
            return paramInt1 - i;
          }
          paramArrayOfByte[(paramInt1++)] = ((byte)(bits >> nextout));
          paramInt2--;
          nextout -= 8;
        } while (nextout >= 0);
        bits = 0;
      }
      while (paramInt2 > 0)
      {
        int j = is.read();
        if (j == -1)
        {
          eof = true;
          if (nextin != 18)
          {
            if (nextin == 12) {
              throw new IOException("Base64 stream has one un-decoded dangling byte.");
            }
            paramArrayOfByte[(paramInt1++)] = ((byte)(bits >> 16));
            paramInt2--;
            if (nextin == 0) {
              if (paramInt2 == 0)
              {
                bits >>= 8;
                nextout = 0;
              }
              else
              {
                paramArrayOfByte[(paramInt1++)] = ((byte)(bits >> 8));
              }
            }
          }
          if (paramInt1 == i) {
            return -1;
          }
          return paramInt1 - i;
        }
        if (j == 61)
        {
          if ((nextin == 18) || (nextin == 12) || ((nextin == 6) && (is.read() != 61))) {
            throw new IOException("Illegal base64 ending sequence:" + nextin);
          }
          paramArrayOfByte[(paramInt1++)] = ((byte)(bits >> 16));
          paramInt2--;
          if (nextin == 0) {
            if (paramInt2 == 0)
            {
              bits >>= 8;
              nextout = 0;
            }
            else
            {
              paramArrayOfByte[(paramInt1++)] = ((byte)(bits >> 8));
            }
          }
          eof = true;
          break;
        }
        if ((j = base64[j]) == -1)
        {
          if (!isMIME) {
            throw new IOException("Illegal base64 character " + Integer.toString(j, 16));
          }
        }
        else
        {
          bits |= j << nextin;
          if (nextin == 0)
          {
            nextin = 18;
            nextout = 16;
            while (nextout >= 0)
            {
              paramArrayOfByte[(paramInt1++)] = ((byte)(bits >> nextout));
              paramInt2--;
              nextout -= 8;
              if ((paramInt2 == 0) && (nextout >= 0)) {
                return paramInt1 - i;
              }
            }
            bits = 0;
          }
          else
          {
            nextin -= 6;
          }
        }
      }
      return paramInt1 - i;
    }
    
    public int available()
      throws IOException
    {
      if (closed) {
        throw new IOException("Stream is closed");
      }
      return is.available();
    }
    
    public void close()
      throws IOException
    {
      if (!closed)
      {
        closed = true;
        is.close();
      }
    }
  }
  
  public static class Decoder
  {
    private final boolean isURL;
    private final boolean isMIME;
    private static final int[] fromBase64 = new int['Ā'];
    private static final int[] fromBase64URL;
    static final Decoder RFC4648 = new Decoder(false, false);
    static final Decoder RFC4648_URLSAFE = new Decoder(true, false);
    static final Decoder RFC2045 = new Decoder(false, true);
    
    private Decoder(boolean paramBoolean1, boolean paramBoolean2)
    {
      isURL = paramBoolean1;
      isMIME = paramBoolean2;
    }
    
    public byte[] decode(byte[] paramArrayOfByte)
    {
      byte[] arrayOfByte = new byte[outLength(paramArrayOfByte, 0, paramArrayOfByte.length)];
      int i = decode0(paramArrayOfByte, 0, paramArrayOfByte.length, arrayOfByte);
      if (i != arrayOfByte.length) {
        arrayOfByte = Arrays.copyOf(arrayOfByte, i);
      }
      return arrayOfByte;
    }
    
    public byte[] decode(String paramString)
    {
      return decode(paramString.getBytes(StandardCharsets.ISO_8859_1));
    }
    
    public int decode(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      int i = outLength(paramArrayOfByte1, 0, paramArrayOfByte1.length);
      if (paramArrayOfByte2.length < i) {
        throw new IllegalArgumentException("Output byte array is too small for decoding all input bytes");
      }
      return decode0(paramArrayOfByte1, 0, paramArrayOfByte1.length, paramArrayOfByte2);
    }
    
    public ByteBuffer decode(ByteBuffer paramByteBuffer)
    {
      int i = paramByteBuffer.position();
      try
      {
        byte[] arrayOfByte1;
        int j;
        int k;
        if (paramByteBuffer.hasArray())
        {
          arrayOfByte1 = paramByteBuffer.array();
          j = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
          k = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
          paramByteBuffer.position(paramByteBuffer.limit());
        }
        else
        {
          arrayOfByte1 = new byte[paramByteBuffer.remaining()];
          paramByteBuffer.get(arrayOfByte1);
          j = 0;
          k = arrayOfByte1.length;
        }
        byte[] arrayOfByte2 = new byte[outLength(arrayOfByte1, j, k)];
        return ByteBuffer.wrap(arrayOfByte2, 0, decode0(arrayOfByte1, j, k, arrayOfByte2));
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        paramByteBuffer.position(i);
        throw localIllegalArgumentException;
      }
    }
    
    public InputStream wrap(InputStream paramInputStream)
    {
      Objects.requireNonNull(paramInputStream);
      return new Base64.DecInputStream(paramInputStream, isURL ? fromBase64URL : fromBase64, isMIME);
    }
    
    private int outLength(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      int[] arrayOfInt = isURL ? fromBase64URL : fromBase64;
      int i = 0;
      int j = paramInt2 - paramInt1;
      if (j == 0) {
        return 0;
      }
      if (j < 2)
      {
        if ((isMIME) && (arrayOfInt[0] == -1)) {
          return 0;
        }
        throw new IllegalArgumentException("Input byte[] should at least have 2 bytes for base64 bytes");
      }
      if (isMIME)
      {
        int k = 0;
        while (paramInt1 < paramInt2)
        {
          int m = paramArrayOfByte[(paramInt1++)] & 0xFF;
          if (m == 61)
          {
            j -= paramInt2 - paramInt1 + 1;
            break;
          }
          if ((m = arrayOfInt[m]) == -1) {
            k++;
          }
        }
        j -= k;
      }
      else if (paramArrayOfByte[(paramInt2 - 1)] == 61)
      {
        i++;
        if (paramArrayOfByte[(paramInt2 - 2)] == 61) {
          i++;
        }
      }
      if ((i == 0) && ((j & 0x3) != 0)) {
        i = 4 - (j & 0x3);
      }
      return 3 * ((j + 3) / 4) - i;
    }
    
    private int decode0(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
    {
      int[] arrayOfInt = isURL ? fromBase64URL : fromBase64;
      int i = 0;
      int j = 0;
      int k = 18;
      while (paramInt1 < paramInt2)
      {
        int m = paramArrayOfByte1[(paramInt1++)] & 0xFF;
        if ((m = arrayOfInt[m]) < 0)
        {
          if (m == -2)
          {
            if (((k != 6) || ((paramInt1 != paramInt2) && (paramArrayOfByte1[(paramInt1++)] == 61))) && (k != 18)) {
              break;
            }
            throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit");
          }
          if (!isMIME) {
            throw new IllegalArgumentException("Illegal base64 character " + Integer.toString(paramArrayOfByte1[(paramInt1 - 1)], 16));
          }
        }
        else
        {
          j |= m << k;
          k -= 6;
          if (k < 0)
          {
            paramArrayOfByte2[(i++)] = ((byte)(j >> 16));
            paramArrayOfByte2[(i++)] = ((byte)(j >> 8));
            paramArrayOfByte2[(i++)] = ((byte)j);
            k = 18;
            j = 0;
          }
        }
      }
      if (k == 6)
      {
        paramArrayOfByte2[(i++)] = ((byte)(j >> 16));
      }
      else if (k == 0)
      {
        paramArrayOfByte2[(i++)] = ((byte)(j >> 16));
        paramArrayOfByte2[(i++)] = ((byte)(j >> 8));
      }
      else if (k == 12)
      {
        throw new IllegalArgumentException("Last unit does not have enough valid bits");
      }
      while (paramInt1 < paramInt2) {
        if ((!isMIME) || (arrayOfInt[paramArrayOfByte1[(paramInt1++)]] >= 0)) {
          throw new IllegalArgumentException("Input byte array has incorrect ending byte at " + paramInt1);
        }
      }
      return i;
    }
    
    static
    {
      Arrays.fill(fromBase64, -1);
      for (int i = 0; i < Base64.Encoder.access$200().length; i++) {
        fromBase64[Base64.Encoder.access$200()[i]] = i;
      }
      fromBase64[61] = -2;
      fromBase64URL = new int['Ā'];
      Arrays.fill(fromBase64URL, -1);
      for (i = 0; i < Base64.Encoder.access$300().length; i++) {
        fromBase64URL[Base64.Encoder.access$300()[i]] = i;
      }
      fromBase64URL[61] = -2;
    }
  }
  
  private static class EncOutputStream
    extends FilterOutputStream
  {
    private int leftover = 0;
    private int b0;
    private int b1;
    private int b2;
    private boolean closed = false;
    private final char[] base64;
    private final byte[] newline;
    private final int linemax;
    private final boolean doPadding;
    private int linepos = 0;
    
    EncOutputStream(OutputStream paramOutputStream, char[] paramArrayOfChar, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    {
      super();
      base64 = paramArrayOfChar;
      newline = paramArrayOfByte;
      linemax = paramInt;
      doPadding = paramBoolean;
    }
    
    public void write(int paramInt)
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      arrayOfByte[0] = ((byte)(paramInt & 0xFF));
      write(arrayOfByte, 0, 1);
    }
    
    private void checkNewline()
      throws IOException
    {
      if (linepos == linemax)
      {
        out.write(newline);
        linepos = 0;
      }
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (closed) {
        throw new IOException("Stream is closed");
      }
      if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
        throw new ArrayIndexOutOfBoundsException();
      }
      if (paramInt2 == 0) {
        return;
      }
      if (leftover != 0)
      {
        if (leftover == 1)
        {
          b1 = (paramArrayOfByte[(paramInt1++)] & 0xFF);
          paramInt2--;
          if (paramInt2 == 0)
          {
            leftover += 1;
            return;
          }
        }
        b2 = (paramArrayOfByte[(paramInt1++)] & 0xFF);
        paramInt2--;
        checkNewline();
        out.write(base64[(b0 >> 2)]);
        out.write(base64[(b0 << 4 & 0x3F | b1 >> 4)]);
        out.write(base64[(b1 << 2 & 0x3F | b2 >> 6)]);
        out.write(base64[(b2 & 0x3F)]);
        linepos += 4;
      }
      int i = paramInt2 / 3;
      leftover = (paramInt2 - i * 3);
      while (i-- > 0)
      {
        checkNewline();
        int j = (paramArrayOfByte[(paramInt1++)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt1++)] & 0xFF) << 8 | paramArrayOfByte[(paramInt1++)] & 0xFF;
        out.write(base64[(j >>> 18 & 0x3F)]);
        out.write(base64[(j >>> 12 & 0x3F)]);
        out.write(base64[(j >>> 6 & 0x3F)]);
        out.write(base64[(j & 0x3F)]);
        linepos += 4;
      }
      if (leftover == 1)
      {
        b0 = (paramArrayOfByte[(paramInt1++)] & 0xFF);
      }
      else if (leftover == 2)
      {
        b0 = (paramArrayOfByte[(paramInt1++)] & 0xFF);
        b1 = (paramArrayOfByte[(paramInt1++)] & 0xFF);
      }
    }
    
    public void close()
      throws IOException
    {
      if (!closed)
      {
        closed = true;
        if (leftover == 1)
        {
          checkNewline();
          out.write(base64[(b0 >> 2)]);
          out.write(base64[(b0 << 4 & 0x3F)]);
          if (doPadding)
          {
            out.write(61);
            out.write(61);
          }
        }
        else if (leftover == 2)
        {
          checkNewline();
          out.write(base64[(b0 >> 2)]);
          out.write(base64[(b0 << 4 & 0x3F | b1 >> 4)]);
          out.write(base64[(b1 << 2 & 0x3F)]);
          if (doPadding) {
            out.write(61);
          }
        }
        leftover = 0;
        out.close();
      }
    }
  }
  
  public static class Encoder
  {
    private final byte[] newline;
    private final int linemax;
    private final boolean isURL;
    private final boolean doPadding;
    private static final char[] toBase64 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    private static final char[] toBase64URL = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
    private static final int MIMELINEMAX = 76;
    private static final byte[] CRLF = { 13, 10 };
    static final Encoder RFC4648 = new Encoder(false, null, -1, true);
    static final Encoder RFC4648_URLSAFE = new Encoder(true, null, -1, true);
    static final Encoder RFC2045 = new Encoder(false, CRLF, 76, true);
    
    private Encoder(boolean paramBoolean1, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean2)
    {
      isURL = paramBoolean1;
      newline = paramArrayOfByte;
      linemax = paramInt;
      doPadding = paramBoolean2;
    }
    
    private final int outLength(int paramInt)
    {
      int i = 0;
      if (doPadding)
      {
        i = 4 * ((paramInt + 2) / 3);
      }
      else
      {
        int j = paramInt % 3;
        i = 4 * (paramInt / 3) + (j == 0 ? 0 : j + 1);
      }
      if (linemax > 0) {
        i += (i - 1) / linemax * newline.length;
      }
      return i;
    }
    
    public byte[] encode(byte[] paramArrayOfByte)
    {
      int i = outLength(paramArrayOfByte.length);
      byte[] arrayOfByte = new byte[i];
      int j = encode0(paramArrayOfByte, 0, paramArrayOfByte.length, arrayOfByte);
      if (j != arrayOfByte.length) {
        return Arrays.copyOf(arrayOfByte, j);
      }
      return arrayOfByte;
    }
    
    public int encode(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      int i = outLength(paramArrayOfByte1.length);
      if (paramArrayOfByte2.length < i) {
        throw new IllegalArgumentException("Output byte array is too small for encoding all input bytes");
      }
      return encode0(paramArrayOfByte1, 0, paramArrayOfByte1.length, paramArrayOfByte2);
    }
    
    public String encodeToString(byte[] paramArrayOfByte)
    {
      byte[] arrayOfByte = encode(paramArrayOfByte);
      return new String(arrayOfByte, 0, 0, arrayOfByte.length);
    }
    
    public ByteBuffer encode(ByteBuffer paramByteBuffer)
    {
      int i = outLength(paramByteBuffer.remaining());
      byte[] arrayOfByte1 = new byte[i];
      int j = 0;
      if (paramByteBuffer.hasArray())
      {
        j = encode0(paramByteBuffer.array(), paramByteBuffer.arrayOffset() + paramByteBuffer.position(), paramByteBuffer.arrayOffset() + paramByteBuffer.limit(), arrayOfByte1);
        paramByteBuffer.position(paramByteBuffer.limit());
      }
      else
      {
        byte[] arrayOfByte2 = new byte[paramByteBuffer.remaining()];
        paramByteBuffer.get(arrayOfByte2);
        j = encode0(arrayOfByte2, 0, arrayOfByte2.length, arrayOfByte1);
      }
      if (j != arrayOfByte1.length) {
        arrayOfByte1 = Arrays.copyOf(arrayOfByte1, j);
      }
      return ByteBuffer.wrap(arrayOfByte1);
    }
    
    public OutputStream wrap(OutputStream paramOutputStream)
    {
      Objects.requireNonNull(paramOutputStream);
      return new Base64.EncOutputStream(paramOutputStream, isURL ? toBase64URL : toBase64, newline, linemax, doPadding);
    }
    
    public Encoder withoutPadding()
    {
      if (!doPadding) {
        return this;
      }
      return new Encoder(isURL, newline, linemax, false);
    }
    
    private int encode0(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
    {
      char[] arrayOfChar = isURL ? toBase64URL : toBase64;
      int i = paramInt1;
      int j = (paramInt2 - paramInt1) / 3 * 3;
      int k = paramInt1 + j;
      if ((linemax > 0) && (j > linemax / 4 * 3)) {
        j = linemax / 4 * 3;
      }
      int m = 0;
      int n;
      int i1;
      while (i < k)
      {
        n = Math.min(i + j, k);
        i1 = i;
        int i2 = m;
        int i3;
        while (i1 < n)
        {
          i3 = (paramArrayOfByte1[(i1++)] & 0xFF) << 16 | (paramArrayOfByte1[(i1++)] & 0xFF) << 8 | paramArrayOfByte1[(i1++)] & 0xFF;
          paramArrayOfByte2[(i2++)] = ((byte)arrayOfChar[(i3 >>> 18 & 0x3F)]);
          paramArrayOfByte2[(i2++)] = ((byte)arrayOfChar[(i3 >>> 12 & 0x3F)]);
          paramArrayOfByte2[(i2++)] = ((byte)arrayOfChar[(i3 >>> 6 & 0x3F)]);
          paramArrayOfByte2[(i2++)] = ((byte)arrayOfChar[(i3 & 0x3F)]);
        }
        i1 = (n - i) / 3 * 4;
        m += i1;
        i = n;
        if ((i1 == linemax) && (i < paramInt2)) {
          for (int i5 : newline) {
            paramArrayOfByte2[(m++)] = i5;
          }
        }
      }
      if (i < paramInt2)
      {
        n = paramArrayOfByte1[(i++)] & 0xFF;
        paramArrayOfByte2[(m++)] = ((byte)arrayOfChar[(n >> 2)]);
        if (i == paramInt2)
        {
          paramArrayOfByte2[(m++)] = ((byte)arrayOfChar[(n << 4 & 0x3F)]);
          if (doPadding)
          {
            paramArrayOfByte2[(m++)] = 61;
            paramArrayOfByte2[(m++)] = 61;
          }
        }
        else
        {
          i1 = paramArrayOfByte1[(i++)] & 0xFF;
          paramArrayOfByte2[(m++)] = ((byte)arrayOfChar[(n << 4 & 0x3F | i1 >> 4)]);
          paramArrayOfByte2[(m++)] = ((byte)arrayOfChar[(i1 << 2 & 0x3F)]);
          if (doPadding) {
            paramArrayOfByte2[(m++)] = 61;
          }
        }
      }
      return m;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */