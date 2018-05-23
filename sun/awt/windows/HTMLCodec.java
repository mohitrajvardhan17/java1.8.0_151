package sun.awt.windows;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

class HTMLCodec
  extends InputStream
{
  public static final String ENCODING = "UTF-8";
  public static final String VERSION = "Version:";
  public static final String START_HTML = "StartHTML:";
  public static final String END_HTML = "EndHTML:";
  public static final String START_FRAGMENT = "StartFragment:";
  public static final String END_FRAGMENT = "EndFragment:";
  public static final String START_SELECTION = "StartSelection:";
  public static final String END_SELECTION = "EndSelection:";
  public static final String START_FRAGMENT_CMT = "<!--StartFragment-->";
  public static final String END_FRAGMENT_CMT = "<!--EndFragment-->";
  public static final String SOURCE_URL = "SourceURL:";
  public static final String DEF_SOURCE_URL = "about:blank";
  public static final String EOLN = "\r\n";
  private static final String VERSION_NUM = "1.0";
  private static final int PADDED_WIDTH = 10;
  private final BufferedInputStream bufferedStream;
  private boolean descriptionParsed = false;
  private boolean closed = false;
  public static final int BYTE_BUFFER_LEN = 8192;
  public static final int CHAR_BUFFER_LEN = 2730;
  private static final String FAILURE_MSG = "Unable to parse HTML description: ";
  private static final String INVALID_MSG = " invalid";
  private long iHTMLStart;
  private long iHTMLEnd;
  private long iFragStart;
  private long iFragEnd;
  private long iSelStart;
  private long iSelEnd;
  private String stBaseURL;
  private String stVersion;
  private long iStartOffset;
  private long iEndOffset;
  private long iReadCount;
  private EHTMLReadMode readMode;
  
  private static String toPaddedString(int paramInt1, int paramInt2)
  {
    String str = "" + paramInt1;
    int i = str.length();
    if ((paramInt1 >= 0) && (i < paramInt2))
    {
      char[] arrayOfChar = new char[paramInt2 - i];
      Arrays.fill(arrayOfChar, '0');
      StringBuffer localStringBuffer = new StringBuffer(paramInt2);
      localStringBuffer.append(arrayOfChar);
      localStringBuffer.append(str);
      str = localStringBuffer.toString();
    }
    return str;
  }
  
  public static byte[] convertToHTMLFormat(byte[] paramArrayOfByte)
  {
    String str1 = "";
    String str2 = "";
    String str3 = new String(paramArrayOfByte);
    String str4 = str3.toUpperCase();
    if (-1 == str4.indexOf("<HTML"))
    {
      str1 = "<HTML>";
      str2 = "</HTML>";
      if (-1 == str4.indexOf("<BODY"))
      {
        str1 = str1 + "<BODY>";
        str2 = "</BODY>" + str2;
      }
    }
    str3 = "about:blank";
    int i = "Version:".length() + "1.0".length() + "\r\n".length() + "StartHTML:".length() + 10 + "\r\n".length() + "EndHTML:".length() + 10 + "\r\n".length() + "StartFragment:".length() + 10 + "\r\n".length() + "EndFragment:".length() + 10 + "\r\n".length() + "SourceURL:".length() + str3.length() + "\r\n".length();
    int j = i + str1.length();
    int k = j + paramArrayOfByte.length - 1;
    int m = k + str2.length();
    StringBuilder localStringBuilder = new StringBuilder(j + "<!--StartFragment-->".length());
    localStringBuilder.append("Version:");
    localStringBuilder.append("1.0");
    localStringBuilder.append("\r\n");
    localStringBuilder.append("StartHTML:");
    localStringBuilder.append(toPaddedString(i, 10));
    localStringBuilder.append("\r\n");
    localStringBuilder.append("EndHTML:");
    localStringBuilder.append(toPaddedString(m, 10));
    localStringBuilder.append("\r\n");
    localStringBuilder.append("StartFragment:");
    localStringBuilder.append(toPaddedString(j, 10));
    localStringBuilder.append("\r\n");
    localStringBuilder.append("EndFragment:");
    localStringBuilder.append(toPaddedString(k, 10));
    localStringBuilder.append("\r\n");
    localStringBuilder.append("SourceURL:");
    localStringBuilder.append(str3);
    localStringBuilder.append("\r\n");
    localStringBuilder.append(str1);
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    try
    {
      arrayOfByte1 = localStringBuilder.toString().getBytes("UTF-8");
      arrayOfByte2 = str2.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length + paramArrayOfByte.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte3, arrayOfByte1.length, paramArrayOfByte.length - 1);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length + paramArrayOfByte.length - 1, arrayOfByte2.length);
    arrayOfByte3[(arrayOfByte3.length - 1)] = 0;
    return arrayOfByte3;
  }
  
  public HTMLCodec(InputStream paramInputStream, EHTMLReadMode paramEHTMLReadMode)
    throws IOException
  {
    bufferedStream = new BufferedInputStream(paramInputStream, 8192);
    readMode = paramEHTMLReadMode;
  }
  
  public synchronized String getBaseURL()
    throws IOException
  {
    if (!descriptionParsed) {
      parseDescription();
    }
    return stBaseURL;
  }
  
  public synchronized String getVersion()
    throws IOException
  {
    if (!descriptionParsed) {
      parseDescription();
    }
    return stVersion;
  }
  
  private void parseDescription()
    throws IOException
  {
    stBaseURL = null;
    stVersion = null;
    iHTMLEnd = (iHTMLStart = iFragEnd = iFragStart = iSelEnd = iSelStart = -1L);
    bufferedStream.mark(8192);
    String[] arrayOfString = { "Version:", "StartHTML:", "EndHTML:", "StartFragment:", "EndFragment:", "StartSelection:", "EndSelection:", "SourceURL:" };
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(bufferedStream, "UTF-8"), 2730);
    long l1 = 0L;
    long l2 = "\r\n".length();
    int i = arrayOfString.length;
    int j = 1;
    for (int k = 0; k < i; k++)
    {
      String str1 = localBufferedReader.readLine();
      if (null == str1) {
        break;
      }
      while (k < i)
      {
        if (str1.startsWith(arrayOfString[k]))
        {
          l1 += str1.length() + l2;
          String str2 = str1.substring(arrayOfString[k].length()).trim();
          if (null == str2) {
            break;
          }
          try
          {
            switch (k)
            {
            case 0: 
              stVersion = str2;
              break;
            case 1: 
              iHTMLStart = Integer.parseInt(str2);
              break;
            case 2: 
              iHTMLEnd = Integer.parseInt(str2);
              break;
            case 3: 
              iFragStart = Integer.parseInt(str2);
              break;
            case 4: 
              iFragEnd = Integer.parseInt(str2);
              break;
            case 5: 
              iSelStart = Integer.parseInt(str2);
              break;
            case 6: 
              iSelEnd = Integer.parseInt(str2);
              break;
            case 7: 
              stBaseURL = str2;
            }
          }
          catch (NumberFormatException localNumberFormatException)
          {
            throw new IOException("Unable to parse HTML description: " + arrayOfString[k] + " value " + localNumberFormatException + " invalid");
          }
        }
        k++;
      }
    }
    if (-1L == iHTMLStart) {
      iHTMLStart = l1;
    }
    if (-1L == iFragStart) {
      iFragStart = iHTMLStart;
    }
    if (-1L == iFragEnd) {
      iFragEnd = iHTMLEnd;
    }
    if (-1L == iSelStart) {
      iSelStart = iFragStart;
    }
    if (-1L == iSelEnd) {
      iSelEnd = iFragEnd;
    }
    switch (readMode)
    {
    case HTML_READ_ALL: 
      iStartOffset = iHTMLStart;
      iEndOffset = iHTMLEnd;
      break;
    case HTML_READ_FRAGMENT: 
      iStartOffset = iFragStart;
      iEndOffset = iFragEnd;
      break;
    case HTML_READ_SELECTION: 
    default: 
      iStartOffset = iSelStart;
      iEndOffset = iSelEnd;
    }
    bufferedStream.reset();
    if (-1L == iStartOffset) {
      throw new IOException("Unable to parse HTML description: invalid HTML format.");
    }
    for (k = 0; k < iStartOffset; k = (int)(k + bufferedStream.skip(iStartOffset - k))) {}
    iReadCount = k;
    if (iStartOffset != iReadCount) {
      throw new IOException("Unable to parse HTML description: Byte stream ends in description.");
    }
    descriptionParsed = true;
  }
  
  public synchronized int read()
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream closed");
    }
    if (!descriptionParsed) {
      parseDescription();
    }
    if ((-1L != iEndOffset) && (iReadCount >= iEndOffset)) {
      return -1;
    }
    int i = bufferedStream.read();
    if (i == -1) {
      return -1;
    }
    iReadCount += 1L;
    return i;
  }
  
  public synchronized void close()
    throws IOException
  {
    if (!closed)
    {
      closed = true;
      bufferedStream.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\HTMLCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */