package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class Base64
{
  public static final int BASE64DEFAULTLENGTH = 76;
  private static final int BASELENGTH = 255;
  private static final int LOOKUPLENGTH = 64;
  private static final int TWENTYFOURBITGROUP = 24;
  private static final int EIGHTBIT = 8;
  private static final int SIXTEENBIT = 16;
  private static final int FOURBYTE = 4;
  private static final int SIGN = -128;
  private static final char PAD = '=';
  private static final byte[] base64Alphabet = new byte['ÿ'];
  private static final char[] lookUpBase64Alphabet = new char[64];
  
  private Base64() {}
  
  static final byte[] getBytes(BigInteger paramBigInteger, int paramInt)
  {
    paramInt = paramInt + 7 >> 3 << 3;
    if (paramInt < paramBigInteger.bitLength()) {
      throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
    }
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if ((paramBigInteger.bitLength() % 8 != 0) && (paramBigInteger.bitLength() / 8 + 1 == paramInt / 8)) {
      return arrayOfByte1;
    }
    int i = 0;
    int j = arrayOfByte1.length;
    if (paramBigInteger.bitLength() % 8 == 0)
    {
      i = 1;
      j--;
    }
    int k = paramInt / 8 - j;
    byte[] arrayOfByte2 = new byte[paramInt / 8];
    System.arraycopy(arrayOfByte1, i, arrayOfByte2, k, j);
    return arrayOfByte2;
  }
  
  public static final String encode(BigInteger paramBigInteger)
  {
    return encode(getBytes(paramBigInteger, paramBigInteger.bitLength()));
  }
  
  public static final byte[] encode(BigInteger paramBigInteger, int paramInt)
  {
    paramInt = paramInt + 7 >> 3 << 3;
    if (paramInt < paramBigInteger.bitLength()) {
      throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
    }
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if ((paramBigInteger.bitLength() % 8 != 0) && (paramBigInteger.bitLength() / 8 + 1 == paramInt / 8)) {
      return arrayOfByte1;
    }
    int i = 0;
    int j = arrayOfByte1.length;
    if (paramBigInteger.bitLength() % 8 == 0)
    {
      i = 1;
      j--;
    }
    int k = paramInt / 8 - j;
    byte[] arrayOfByte2 = new byte[paramInt / 8];
    System.arraycopy(arrayOfByte1, i, arrayOfByte2, k, j);
    return arrayOfByte2;
  }
  
  public static final BigInteger decodeBigIntegerFromElement(Element paramElement)
    throws Base64DecodingException
  {
    return new BigInteger(1, decode(paramElement));
  }
  
  public static final BigInteger decodeBigIntegerFromText(Text paramText)
    throws Base64DecodingException
  {
    return new BigInteger(1, decode(paramText.getData()));
  }
  
  public static final void fillElementWithBigInteger(Element paramElement, BigInteger paramBigInteger)
  {
    String str = encode(paramBigInteger);
    if ((!XMLUtils.ignoreLineBreaks()) && (str.length() > 76)) {
      str = "\n" + str + "\n";
    }
    Document localDocument = paramElement.getOwnerDocument();
    Text localText = localDocument.createTextNode(str);
    paramElement.appendChild(localText);
  }
  
  public static final byte[] decode(Element paramElement)
    throws Base64DecodingException
  {
    Node localNode = paramElement.getFirstChild();
    StringBuffer localStringBuffer = new StringBuffer();
    while (localNode != null)
    {
      if (localNode.getNodeType() == 3)
      {
        Text localText = (Text)localNode;
        localStringBuffer.append(localText.getData());
      }
      localNode = localNode.getNextSibling();
    }
    return decode(localStringBuffer.toString());
  }
  
  public static final Element encodeToElement(Document paramDocument, String paramString, byte[] paramArrayOfByte)
  {
    Element localElement = XMLUtils.createElementInSignatureSpace(paramDocument, paramString);
    Text localText = paramDocument.createTextNode(encode(paramArrayOfByte));
    localElement.appendChild(localText);
    return localElement;
  }
  
  public static final byte[] decode(byte[] paramArrayOfByte)
    throws Base64DecodingException
  {
    return decodeInternal(paramArrayOfByte, -1);
  }
  
  public static final String encode(byte[] paramArrayOfByte)
  {
    return XMLUtils.ignoreLineBreaks() ? encode(paramArrayOfByte, Integer.MAX_VALUE) : encode(paramArrayOfByte, 76);
  }
  
  public static final byte[] decode(BufferedReader paramBufferedReader)
    throws IOException, Base64DecodingException
  {
    byte[] arrayOfByte1 = null;
    UnsyncByteArrayOutputStream localUnsyncByteArrayOutputStream = null;
    try
    {
      localUnsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
      String str;
      while (null != (str = paramBufferedReader.readLine()))
      {
        byte[] arrayOfByte2 = decode(str);
        localUnsyncByteArrayOutputStream.write(arrayOfByte2);
      }
      arrayOfByte1 = localUnsyncByteArrayOutputStream.toByteArray();
    }
    finally
    {
      localUnsyncByteArrayOutputStream.close();
    }
    return arrayOfByte1;
  }
  
  protected static final boolean isWhiteSpace(byte paramByte)
  {
    return (paramByte == 32) || (paramByte == 13) || (paramByte == 10) || (paramByte == 9);
  }
  
  protected static final boolean isPad(byte paramByte)
  {
    return paramByte == 61;
  }
  
  public static final String encode(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt < 4) {
      paramInt = Integer.MAX_VALUE;
    }
    if (paramArrayOfByte == null) {
      return null;
    }
    int i = paramArrayOfByte.length * 8;
    if (i == 0) {
      return "";
    }
    int j = i % 24;
    int k = i / 24;
    int m = j != 0 ? k + 1 : k;
    int n = paramInt / 4;
    int i1 = (m - 1) / n;
    char[] arrayOfChar = null;
    arrayOfChar = new char[m * 4 + i1];
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    int i7 = 0;
    int i8 = 0;
    int i9 = 0;
    int i11;
    int i12;
    for (int i10 = 0; i10 < i1; i10++)
    {
      for (i11 = 0; i11 < 19; i11++)
      {
        i4 = paramArrayOfByte[(i8++)];
        i5 = paramArrayOfByte[(i8++)];
        i6 = paramArrayOfByte[(i8++)];
        i3 = (byte)(i5 & 0xF);
        i2 = (byte)(i4 & 0x3);
        i12 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);
        int i13 = (i5 & 0xFFFFFF80) == 0 ? (byte)(i5 >> 4) : (byte)(i5 >> 4 ^ 0xF0);
        int i14 = (i6 & 0xFFFFFF80) == 0 ? (byte)(i6 >> 6) : (byte)(i6 >> 6 ^ 0xFC);
        arrayOfChar[(i7++)] = lookUpBase64Alphabet[i12];
        arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i13 | i2 << 4)];
        arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i3 << 2 | i14)];
        arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i6 & 0x3F)];
        i9++;
      }
      arrayOfChar[(i7++)] = '\n';
    }
    while (i9 < k)
    {
      i4 = paramArrayOfByte[(i8++)];
      i5 = paramArrayOfByte[(i8++)];
      i6 = paramArrayOfByte[(i8++)];
      i3 = (byte)(i5 & 0xF);
      i2 = (byte)(i4 & 0x3);
      i10 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);
      i11 = (i5 & 0xFFFFFF80) == 0 ? (byte)(i5 >> 4) : (byte)(i5 >> 4 ^ 0xF0);
      i12 = (i6 & 0xFFFFFF80) == 0 ? (byte)(i6 >> 6) : (byte)(i6 >> 6 ^ 0xFC);
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[i10];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i11 | i2 << 4)];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i3 << 2 | i12)];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i6 & 0x3F)];
      i9++;
    }
    if (j == 8)
    {
      i4 = paramArrayOfByte[i8];
      i2 = (byte)(i4 & 0x3);
      i10 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[i10];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i2 << 4)];
      arrayOfChar[(i7++)] = '=';
      arrayOfChar[(i7++)] = '=';
    }
    else if (j == 16)
    {
      i4 = paramArrayOfByte[i8];
      i5 = paramArrayOfByte[(i8 + 1)];
      i3 = (byte)(i5 & 0xF);
      i2 = (byte)(i4 & 0x3);
      i10 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);
      i11 = (i5 & 0xFFFFFF80) == 0 ? (byte)(i5 >> 4) : (byte)(i5 >> 4 ^ 0xF0);
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[i10];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i11 | i2 << 4)];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i3 << 2)];
      arrayOfChar[(i7++)] = '=';
    }
    return new String(arrayOfChar);
  }
  
  public static final byte[] decode(String paramString)
    throws Base64DecodingException
  {
    if (paramString == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[paramString.length()];
    int i = getBytesInternal(paramString, arrayOfByte);
    return decodeInternal(arrayOfByte, i);
  }
  
  protected static final int getBytesInternal(String paramString, byte[] paramArrayOfByte)
  {
    int i = paramString.length();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      byte b = (byte)paramString.charAt(k);
      if (!isWhiteSpace(b)) {
        paramArrayOfByte[(j++)] = b;
      }
    }
    return j;
  }
  
  protected static final byte[] decodeInternal(byte[] paramArrayOfByte, int paramInt)
    throws Base64DecodingException
  {
    if (paramInt == -1) {
      paramInt = removeWhiteSpace(paramArrayOfByte);
    }
    if (paramInt % 4 != 0) {
      throw new Base64DecodingException("decoding.divisible.four");
    }
    int i = paramInt / 4;
    if (i == 0) {
      return new byte[0];
    }
    byte[] arrayOfByte = null;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    i3 = (i - 1) * 4;
    i2 = (i - 1) * 3;
    j = base64Alphabet[paramArrayOfByte[(i3++)]];
    k = base64Alphabet[paramArrayOfByte[(i3++)]];
    if ((j == -1) || (k == -1)) {
      throw new Base64DecodingException("decoding.general");
    }
    byte b1;
    m = base64Alphabet[(b1 = paramArrayOfByte[(i3++)])];
    byte b2;
    n = base64Alphabet[(b2 = paramArrayOfByte[(i3++)])];
    if ((m == -1) || (n == -1))
    {
      if ((isPad(b1)) && (isPad(b2)))
      {
        if ((k & 0xF) != 0) {
          throw new Base64DecodingException("decoding.general");
        }
        arrayOfByte = new byte[i2 + 1];
        arrayOfByte[i2] = ((byte)(j << 2 | k >> 4));
      }
      else if ((!isPad(b1)) && (isPad(b2)))
      {
        if ((m & 0x3) != 0) {
          throw new Base64DecodingException("decoding.general");
        }
        arrayOfByte = new byte[i2 + 2];
        arrayOfByte[(i2++)] = ((byte)(j << 2 | k >> 4));
        arrayOfByte[i2] = ((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
      }
      else
      {
        throw new Base64DecodingException("decoding.general");
      }
    }
    else
    {
      arrayOfByte = new byte[i2 + 3];
      arrayOfByte[(i2++)] = ((byte)(j << 2 | k >> 4));
      arrayOfByte[(i2++)] = ((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
      arrayOfByte[(i2++)] = ((byte)(m << 6 | n));
    }
    i2 = 0;
    i3 = 0;
    for (i1 = i - 1; i1 > 0; i1--)
    {
      j = base64Alphabet[paramArrayOfByte[(i3++)]];
      k = base64Alphabet[paramArrayOfByte[(i3++)]];
      m = base64Alphabet[paramArrayOfByte[(i3++)]];
      n = base64Alphabet[paramArrayOfByte[(i3++)]];
      if ((j == -1) || (k == -1) || (m == -1) || (n == -1)) {
        throw new Base64DecodingException("decoding.general");
      }
      arrayOfByte[(i2++)] = ((byte)(j << 2 | k >> 4));
      arrayOfByte[(i2++)] = ((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
      arrayOfByte[(i2++)] = ((byte)(m << 6 | n));
    }
    return arrayOfByte;
  }
  
  public static final void decode(String paramString, OutputStream paramOutputStream)
    throws Base64DecodingException, IOException
  {
    byte[] arrayOfByte = new byte[paramString.length()];
    int i = getBytesInternal(paramString, arrayOfByte);
    decode(arrayOfByte, paramOutputStream, i);
  }
  
  public static final void decode(byte[] paramArrayOfByte, OutputStream paramOutputStream)
    throws Base64DecodingException, IOException
  {
    decode(paramArrayOfByte, paramOutputStream, -1);
  }
  
  protected static final void decode(byte[] paramArrayOfByte, OutputStream paramOutputStream, int paramInt)
    throws Base64DecodingException, IOException
  {
    if (paramInt == -1) {
      paramInt = removeWhiteSpace(paramArrayOfByte);
    }
    if (paramInt % 4 != 0) {
      throw new Base64DecodingException("decoding.divisible.four");
    }
    int i = paramInt / 4;
    if (i == 0) {
      return;
    }
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    for (i1 = i - 1; i1 > 0; i1--)
    {
      j = base64Alphabet[paramArrayOfByte[(i2++)]];
      k = base64Alphabet[paramArrayOfByte[(i2++)]];
      m = base64Alphabet[paramArrayOfByte[(i2++)]];
      n = base64Alphabet[paramArrayOfByte[(i2++)]];
      if ((j == -1) || (k == -1) || (m == -1) || (n == -1)) {
        throw new Base64DecodingException("decoding.general");
      }
      paramOutputStream.write((byte)(j << 2 | k >> 4));
      paramOutputStream.write((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
      paramOutputStream.write((byte)(m << 6 | n));
    }
    j = base64Alphabet[paramArrayOfByte[(i2++)]];
    k = base64Alphabet[paramArrayOfByte[(i2++)]];
    if ((j == -1) || (k == -1)) {
      throw new Base64DecodingException("decoding.general");
    }
    byte b1;
    m = base64Alphabet[(b1 = paramArrayOfByte[(i2++)])];
    byte b2;
    n = base64Alphabet[(b2 = paramArrayOfByte[(i2++)])];
    if ((m == -1) || (n == -1))
    {
      if ((isPad(b1)) && (isPad(b2)))
      {
        if ((k & 0xF) != 0) {
          throw new Base64DecodingException("decoding.general");
        }
        paramOutputStream.write((byte)(j << 2 | k >> 4));
      }
      else if ((!isPad(b1)) && (isPad(b2)))
      {
        if ((m & 0x3) != 0) {
          throw new Base64DecodingException("decoding.general");
        }
        paramOutputStream.write((byte)(j << 2 | k >> 4));
        paramOutputStream.write((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
      }
      else
      {
        throw new Base64DecodingException("decoding.general");
      }
    }
    else
    {
      paramOutputStream.write((byte)(j << 2 | k >> 4));
      paramOutputStream.write((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
      paramOutputStream.write((byte)(m << 6 | n));
    }
  }
  
  public static final void decode(InputStream paramInputStream, OutputStream paramOutputStream)
    throws Base64DecodingException, IOException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    byte[] arrayOfByte = new byte[4];
    int i1;
    while ((i1 = paramInputStream.read()) > 0)
    {
      b1 = (byte)i1;
      if (!isWhiteSpace(b1))
      {
        if (isPad(b1))
        {
          arrayOfByte[(n++)] = b1;
          if (n != 3) {
            break;
          }
          arrayOfByte[(n++)] = ((byte)paramInputStream.read());
          break;
        }
        if ((arrayOfByte[(n++)] = b1) == -1) {
          throw new Base64DecodingException("decoding.general");
        }
        if (n == 4)
        {
          n = 0;
          i = base64Alphabet[arrayOfByte[0]];
          j = base64Alphabet[arrayOfByte[1]];
          k = base64Alphabet[arrayOfByte[2]];
          m = base64Alphabet[arrayOfByte[3]];
          paramOutputStream.write((byte)(i << 2 | j >> 4));
          paramOutputStream.write((byte)((j & 0xF) << 4 | k >> 2 & 0xF));
          paramOutputStream.write((byte)(k << 6 | m));
        }
      }
    }
    byte b1 = arrayOfByte[0];
    int i2 = arrayOfByte[1];
    byte b2 = arrayOfByte[2];
    byte b3 = arrayOfByte[3];
    i = base64Alphabet[b1];
    j = base64Alphabet[i2];
    k = base64Alphabet[b2];
    m = base64Alphabet[b3];
    if ((k == -1) || (m == -1))
    {
      if ((isPad(b2)) && (isPad(b3)))
      {
        if ((j & 0xF) != 0) {
          throw new Base64DecodingException("decoding.general");
        }
        paramOutputStream.write((byte)(i << 2 | j >> 4));
      }
      else if ((!isPad(b2)) && (isPad(b3)))
      {
        k = base64Alphabet[b2];
        if ((k & 0x3) != 0) {
          throw new Base64DecodingException("decoding.general");
        }
        paramOutputStream.write((byte)(i << 2 | j >> 4));
        paramOutputStream.write((byte)((j & 0xF) << 4 | k >> 2 & 0xF));
      }
      else
      {
        throw new Base64DecodingException("decoding.general");
      }
    }
    else
    {
      paramOutputStream.write((byte)(i << 2 | j >> 4));
      paramOutputStream.write((byte)((j & 0xF) << 4 | k >> 2 & 0xF));
      paramOutputStream.write((byte)(k << 6 | m));
    }
  }
  
  protected static final int removeWhiteSpace(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return 0;
    }
    int i = 0;
    int j = paramArrayOfByte.length;
    for (int k = 0; k < j; k++)
    {
      byte b = paramArrayOfByte[k];
      if (!isWhiteSpace(b)) {
        paramArrayOfByte[(i++)] = b;
      }
    }
    return i;
  }
  
  static
  {
    for (int i = 0; i < 255; i++) {
      base64Alphabet[i] = -1;
    }
    for (i = 90; i >= 65; i--) {
      base64Alphabet[i] = ((byte)(i - 65));
    }
    for (i = 122; i >= 97; i--) {
      base64Alphabet[i] = ((byte)(i - 97 + 26));
    }
    for (i = 57; i >= 48; i--) {
      base64Alphabet[i] = ((byte)(i - 48 + 52));
    }
    base64Alphabet[43] = 62;
    base64Alphabet[47] = 63;
    for (i = 0; i <= 25; i++) {
      lookUpBase64Alphabet[i] = ((char)(65 + i));
    }
    i = 26;
    for (int j = 0; i <= 51; j++)
    {
      lookUpBase64Alphabet[i] = ((char)(97 + j));
      i++;
    }
    i = 52;
    for (j = 0; i <= 61; j++)
    {
      lookUpBase64Alphabet[i] = ((char)(48 + j));
      i++;
    }
    lookUpBase64Alphabet[62] = '+';
    lookUpBase64Alphabet[63] = '/';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */