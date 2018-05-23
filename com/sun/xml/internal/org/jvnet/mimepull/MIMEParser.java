package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

class MIMEParser
  implements Iterable<MIMEEvent>
{
  private static final Logger LOGGER = Logger.getLogger(MIMEParser.class.getName());
  private static final String HEADER_ENCODING = "ISO8859-1";
  private static final int NO_LWSP = 1000;
  private STATE state = STATE.START_MESSAGE;
  private final InputStream in;
  private final byte[] bndbytes;
  private final int bl;
  private final MIMEConfig config;
  private final int[] bcs = new int[''];
  private final int[] gss;
  private boolean parsed;
  private boolean done = false;
  private boolean eof;
  private final int capacity;
  private byte[] buf;
  private int len;
  private boolean bol;
  
  MIMEParser(InputStream paramInputStream, String paramString, MIMEConfig paramMIMEConfig)
  {
    in = paramInputStream;
    bndbytes = getBytes("--" + paramString);
    bl = bndbytes.length;
    config = paramMIMEConfig;
    gss = new int[bl];
    compileBoundaryPattern();
    capacity = (chunkSize + 2 + bl + 4 + 1000);
    createBuf(capacity);
  }
  
  public Iterator<MIMEEvent> iterator()
  {
    return new MIMEEventIterator();
  }
  
  private InternetHeaders readHeaders()
  {
    if (!eof) {
      fillBuf();
    }
    return new InternetHeaders(new LineInputStream());
  }
  
  private ByteBuffer readBody()
  {
    if (!eof) {
      fillBuf();
    }
    int i = match(buf, 0, len);
    if (i == -1)
    {
      assert ((eof) || (len >= config.chunkSize));
      j = eof ? len : config.chunkSize;
      if (eof)
      {
        done = true;
        throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
      }
      return adjustBuf(j, len - j);
    }
    int j = i;
    if ((!bol) || (i != 0)) {
      if ((i > 0) && ((buf[(i - 1)] == 10) || (buf[(i - 1)] == 13)))
      {
        j--;
        if ((buf[(i - 1)] == 10) && (i > 1) && (buf[(i - 2)] == 13)) {
          j--;
        }
      }
      else
      {
        return adjustBuf(i + 1, len - i - 1);
      }
    }
    if ((i + bl + 1 < len) && (buf[(i + bl)] == 45) && (buf[(i + bl + 1)] == 45))
    {
      state = STATE.END_PART;
      done = true;
      return adjustBuf(j, 0);
    }
    int k = 0;
    for (int m = i + bl; (m < len) && ((buf[m] == 32) || (buf[m] == 9)); m++) {
      k++;
    }
    if ((i + bl + k < len) && (buf[(i + bl + k)] == 10))
    {
      state = STATE.END_PART;
      return adjustBuf(j, len - i - bl - k - 1);
    }
    if ((i + bl + k + 1 < len) && (buf[(i + bl + k)] == 13) && (buf[(i + bl + k + 1)] == 10))
    {
      state = STATE.END_PART;
      return adjustBuf(j, len - i - bl - k - 2);
    }
    if (i + bl + k + 1 < len) {
      return adjustBuf(j + 1, len - j - 1);
    }
    if (eof)
    {
      done = true;
      throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
    }
    return adjustBuf(j, len - j);
  }
  
  private ByteBuffer adjustBuf(int paramInt1, int paramInt2)
  {
    assert (buf != null);
    assert (paramInt1 >= 0);
    assert (paramInt2 >= 0);
    byte[] arrayOfByte = buf;
    createBuf(paramInt2);
    System.arraycopy(arrayOfByte, len - paramInt2, buf, 0, paramInt2);
    len = paramInt2;
    return ByteBuffer.wrap(arrayOfByte, 0, paramInt1);
  }
  
  private void createBuf(int paramInt)
  {
    buf = new byte[paramInt < capacity ? capacity : paramInt];
  }
  
  private void skipPreamble()
  {
    for (;;)
    {
      if (!eof) {
        fillBuf();
      }
      int i = match(buf, 0, len);
      if (i == -1)
      {
        if (eof) {
          throw new MIMEParsingException("Missing start boundary");
        }
        adjustBuf(len - bl + 1, bl - 1);
      }
      else if (i > config.chunkSize)
      {
        adjustBuf(i, len - i);
      }
      else
      {
        int j = 0;
        for (int k = i + bl; (k < len) && ((buf[k] == 32) || (buf[k] == 9)); k++) {
          j++;
        }
        if ((i + bl + j < len) && ((buf[(i + bl + j)] == 10) || (buf[(i + bl + j)] == 13)))
        {
          if (buf[(i + bl + j)] == 10)
          {
            adjustBuf(i + bl + j + 1, len - i - bl - j - 1);
            break;
          }
          if ((i + bl + j + 1 < len) && (buf[(i + bl + j + 1)] == 10))
          {
            adjustBuf(i + bl + j + 2, len - i - bl - j - 2);
            break;
          }
        }
        adjustBuf(i + 1, len - i - 1);
      }
    }
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Skipped the preamble. buffer len={0}", Integer.valueOf(len));
    }
  }
  
  private static byte[] getBytes(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    while (j < i) {
      arrayOfByte[j] = ((byte)arrayOfChar[(j++)]);
    }
    return arrayOfByte;
  }
  
  private void compileBoundaryPattern()
  {
    for (int i = 0; i < bndbytes.length; i++) {
      bcs[(bndbytes[i] & 0x7F)] = (i + 1);
    }
    label106:
    for (i = bndbytes.length; i > 0; i--)
    {
      for (int j = bndbytes.length - 1; j >= i; j--)
      {
        if (bndbytes[j] != bndbytes[(j - i)]) {
          break label106;
        }
        gss[(j - 1)] = i;
      }
      while (j > 0) {
        gss[(--j)] = i;
      }
    }
    gss[(bndbytes.length - 1)] = 1;
  }
  
  private int match(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - bndbytes.length;
    if (paramInt1 <= i)
    {
      for (int j = bndbytes.length - 1;; j--)
      {
        if (j < 0) {
          break label86;
        }
        int k = paramArrayOfByte[(paramInt1 + j)];
        if (k != bndbytes[j])
        {
          paramInt1 += Math.max(j + 1 - bcs[(k & 0x7F)], gss[j]);
          break;
        }
      }
      label86:
      return paramInt1;
    }
    return -1;
  }
  
  private void fillBuf()
  {
    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.log(Level.FINER, "Before fillBuf() buffer len={0}", Integer.valueOf(len));
    }
    assert (!eof);
    while (len < buf.length)
    {
      int i;
      try
      {
        i = in.read(buf, len, buf.length - len);
      }
      catch (IOException localIOException1)
      {
        throw new MIMEParsingException(localIOException1);
      }
      if (i == -1)
      {
        eof = true;
        try
        {
          if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Closing the input stream.");
          }
          in.close();
        }
        catch (IOException localIOException2)
        {
          throw new MIMEParsingException(localIOException2);
        }
      }
      len += i;
    }
    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.log(Level.FINER, "After fillBuf() buffer len={0}", Integer.valueOf(len));
    }
  }
  
  private void doubleBuf()
  {
    byte[] arrayOfByte = new byte[2 * len];
    System.arraycopy(buf, 0, arrayOfByte, 0, len);
    buf = arrayOfByte;
    if (!eof) {
      fillBuf();
    }
  }
  
  class LineInputStream
  {
    private int offset;
    
    LineInputStream() {}
    
    public String readLine()
      throws IOException
    {
      int i = 0;
      int j = 0;
      while (offset + i < len)
      {
        if (buf[(offset + i)] == 10)
        {
          j = 1;
          break;
        }
        if (offset + i + 1 == len) {
          MIMEParser.this.doubleBuf();
        }
        if (offset + i + 1 >= len)
        {
          assert (eof);
          return null;
        }
        if ((buf[(offset + i)] == 13) && (buf[(offset + i + 1)] == 10))
        {
          j = 2;
          break;
        }
        i++;
      }
      if (i == 0)
      {
        MIMEParser.this.adjustBuf(offset + j, len - offset - j);
        return null;
      }
      String str = new String(buf, offset, i, "ISO8859-1");
      offset += i + j;
      return str;
    }
  }
  
  class MIMEEventIterator
    implements Iterator<MIMEEvent>
  {
    MIMEEventIterator() {}
    
    public boolean hasNext()
    {
      return !parsed;
    }
    
    public MIMEEvent next()
    {
      switch (MIMEParser.1.$SwitchMap$com$sun$xml$internal$org$jvnet$mimepull$MIMEParser$STATE[state.ordinal()])
      {
      case 1: 
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
          MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.START_MESSAGE);
        }
        state = MIMEParser.STATE.SKIP_PREAMBLE;
        return MIMEEvent.START_MESSAGE;
      case 2: 
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
          MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.SKIP_PREAMBLE);
        }
        MIMEParser.this.skipPreamble();
      case 3: 
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
          MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.START_PART);
        }
        state = MIMEParser.STATE.HEADERS;
        return MIMEEvent.START_PART;
      case 4: 
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
          MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.HEADERS);
        }
        InternetHeaders localInternetHeaders = MIMEParser.this.readHeaders();
        state = MIMEParser.STATE.BODY;
        bol = true;
        return new MIMEEvent.Headers(localInternetHeaders);
      case 5: 
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
          MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.BODY);
        }
        ByteBuffer localByteBuffer = MIMEParser.this.readBody();
        bol = false;
        return new MIMEEvent.Content(localByteBuffer);
      case 6: 
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
          MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.END_PART);
        }
        if (done) {
          state = MIMEParser.STATE.END_MESSAGE;
        } else {
          state = MIMEParser.STATE.START_PART;
        }
        return MIMEEvent.END_PART;
      case 7: 
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
          MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.END_MESSAGE);
        }
        parsed = true;
        return MIMEEvent.END_MESSAGE;
      }
      throw new MIMEParsingException("Unknown Parser state = " + state);
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static enum STATE
  {
    START_MESSAGE,  SKIP_PREAMBLE,  START_PART,  HEADERS,  BODY,  END_PART,  END_MESSAGE;
    
    private STATE() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */