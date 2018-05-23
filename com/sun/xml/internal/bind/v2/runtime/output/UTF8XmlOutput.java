package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class UTF8XmlOutput
  extends XmlOutputAbstractImpl
{
  protected final OutputStream out;
  private Encoded[] prefixes = new Encoded[8];
  private int prefixCount;
  private final Encoded[] localNames;
  private final Encoded textBuffer = new Encoded();
  protected final byte[] octetBuffer = new byte['Ѐ'];
  protected int octetBufferIndex;
  protected boolean closeStartTagPending = false;
  private String header;
  private CharacterEscapeHandler escapeHandler = null;
  private final byte[] XMLNS_EQUALS = (byte[])_XMLNS_EQUALS.clone();
  private final byte[] XMLNS_COLON = (byte[])_XMLNS_COLON.clone();
  private final byte[] EQUALS = (byte[])_EQUALS.clone();
  private final byte[] CLOSE_TAG = (byte[])_CLOSE_TAG.clone();
  private final byte[] EMPTY_TAG = (byte[])_EMPTY_TAG.clone();
  private final byte[] XML_DECL = (byte[])_XML_DECL.clone();
  private static final byte[] _XMLNS_EQUALS = toBytes(" xmlns=\"");
  private static final byte[] _XMLNS_COLON = toBytes(" xmlns:");
  private static final byte[] _EQUALS = toBytes("=\"");
  private static final byte[] _CLOSE_TAG = toBytes("</");
  private static final byte[] _EMPTY_TAG = toBytes("/>");
  private static final byte[] _XML_DECL = toBytes("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  
  public UTF8XmlOutput(OutputStream paramOutputStream, Encoded[] paramArrayOfEncoded, CharacterEscapeHandler paramCharacterEscapeHandler)
  {
    out = paramOutputStream;
    localNames = paramArrayOfEncoded;
    for (int i = 0; i < prefixes.length; i++) {
      prefixes[i] = new Encoded();
    }
    escapeHandler = paramCharacterEscapeHandler;
  }
  
  public void setHeader(String paramString)
  {
    header = paramString;
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    octetBufferIndex = 0;
    if (!paramBoolean) {
      write(XML_DECL);
    }
    if (header != null)
    {
      textBuffer.set(header);
      textBuffer.write(this);
    }
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    flushBuffer();
    super.endDocument(paramBoolean);
  }
  
  protected final void closeStartTag()
    throws IOException
  {
    if (closeStartTagPending)
    {
      write(62);
      closeStartTagPending = false;
    }
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException
  {
    closeStartTag();
    int i = pushNsDecls();
    write(60);
    writeName(paramInt, paramString);
    writeNsDecls(i);
  }
  
  public void beginStartTag(Name paramName)
    throws IOException
  {
    closeStartTag();
    int i = pushNsDecls();
    write(60);
    writeName(paramName);
    writeNsDecls(i);
  }
  
  private int pushNsDecls()
  {
    int i = nsContext.count();
    NamespaceContextImpl.Element localElement = nsContext.getCurrent();
    if (i > prefixes.length)
    {
      j = Math.max(i, prefixes.length * 2);
      Encoded[] arrayOfEncoded = new Encoded[j];
      System.arraycopy(prefixes, 0, arrayOfEncoded, 0, prefixes.length);
      for (m = prefixes.length; m < arrayOfEncoded.length; m++) {
        arrayOfEncoded[m] = new Encoded();
      }
      prefixes = arrayOfEncoded;
    }
    int j = Math.min(prefixCount, localElement.getBase());
    int k = nsContext.count();
    for (int m = j; m < k; m++)
    {
      String str = nsContext.getPrefix(m);
      Encoded localEncoded = prefixes[m];
      if (str.length() == 0)
      {
        buf = EMPTY_BYTE_ARRAY;
        len = 0;
      }
      else
      {
        localEncoded.set(str);
        localEncoded.append(':');
      }
    }
    prefixCount = k;
    return j;
  }
  
  protected void writeNsDecls(int paramInt)
    throws IOException
  {
    NamespaceContextImpl.Element localElement = nsContext.getCurrent();
    int i = nsContext.count();
    for (int j = localElement.getBase(); j < i; j++) {
      writeNsDecl(j);
    }
  }
  
  protected final void writeNsDecl(int paramInt)
    throws IOException
  {
    String str = nsContext.getPrefix(paramInt);
    if (str.length() == 0)
    {
      if ((nsContext.getCurrent().isRootElement()) && (nsContext.getNamespaceURI(paramInt).length() == 0)) {
        return;
      }
      write(XMLNS_EQUALS);
    }
    else
    {
      Encoded localEncoded = prefixes[paramInt];
      write(XMLNS_COLON);
      write(buf, 0, len - 1);
      write(EQUALS);
    }
    doText(nsContext.getNamespaceURI(paramInt), true);
    write(34);
  }
  
  private void writePrefix(int paramInt)
    throws IOException
  {
    prefixes[paramInt].write(this);
  }
  
  private void writeName(Name paramName)
    throws IOException
  {
    writePrefix(nsUriIndex2prefixIndex[nsUriIndex]);
    localNames[localNameIndex].write(this);
  }
  
  private void writeName(int paramInt, String paramString)
    throws IOException
  {
    writePrefix(paramInt);
    textBuffer.set(paramString);
    textBuffer.write(this);
  }
  
  public void attribute(Name paramName, String paramString)
    throws IOException
  {
    write(32);
    if (nsUriIndex == -1) {
      localNames[localNameIndex].write(this);
    } else {
      writeName(paramName);
    }
    write(EQUALS);
    doText(paramString, true);
    write(34);
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException
  {
    write(32);
    if (paramInt == -1)
    {
      textBuffer.set(paramString1);
      textBuffer.write(this);
    }
    else
    {
      writeName(paramInt, paramString1);
    }
    write(EQUALS);
    doText(paramString2, true);
    write(34);
  }
  
  public void endStartTag()
    throws IOException
  {
    closeStartTagPending = true;
  }
  
  public void endTag(Name paramName)
    throws IOException
  {
    if (closeStartTagPending)
    {
      write(EMPTY_TAG);
      closeStartTagPending = false;
    }
    else
    {
      write(CLOSE_TAG);
      writeName(paramName);
      write(62);
    }
  }
  
  public void endTag(int paramInt, String paramString)
    throws IOException
  {
    if (closeStartTagPending)
    {
      write(EMPTY_TAG);
      closeStartTagPending = false;
    }
    else
    {
      write(CLOSE_TAG);
      writeName(paramInt, paramString);
      write(62);
    }
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException
  {
    closeStartTag();
    if (paramBoolean) {
      write(32);
    }
    doText(paramString, false);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException
  {
    closeStartTag();
    if (paramBoolean) {
      write(32);
    }
    paramPcdata.writeTo(this);
  }
  
  private void doText(String paramString, boolean paramBoolean)
    throws IOException
  {
    if (escapeHandler != null)
    {
      StringWriter localStringWriter = new StringWriter();
      escapeHandler.escape(paramString.toCharArray(), 0, paramString.length(), paramBoolean, localStringWriter);
      textBuffer.set(localStringWriter.toString());
    }
    else
    {
      textBuffer.setEscape(paramString, paramBoolean);
    }
    textBuffer.write(this);
  }
  
  public final void text(int paramInt)
    throws IOException
  {
    closeStartTag();
    int i = paramInt < 0 ? 1 : 0;
    textBuffer.ensureSize(11);
    byte[] arrayOfByte = textBuffer.buf;
    int j = 11;
    do
    {
      int k = paramInt % 10;
      if (k < 0) {
        k = -k;
      }
      arrayOfByte[(--j)] = ((byte)(0x30 | k));
      paramInt /= 10;
    } while (paramInt != 0);
    if (i != 0) {
      arrayOfByte[(--j)] = 45;
    }
    write(arrayOfByte, j, 11 - j);
  }
  
  public void text(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    closeStartTag();
    int i = 0;
    while (paramInt > 0)
    {
      int j = Math.min((octetBuffer.length - octetBufferIndex) / 4 * 3, paramInt);
      octetBufferIndex = DatatypeConverterImpl._printBase64Binary(paramArrayOfByte, i, j, octetBuffer, octetBufferIndex);
      if (j < paramInt) {
        flushBuffer();
      }
      i += j;
      paramInt -= j;
    }
  }
  
  public final void write(int paramInt)
    throws IOException
  {
    if (octetBufferIndex < octetBuffer.length)
    {
      octetBuffer[(octetBufferIndex++)] = ((byte)paramInt);
    }
    else
    {
      out.write(octetBuffer);
      octetBufferIndex = 1;
      octetBuffer[0] = ((byte)paramInt);
    }
  }
  
  protected final void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  protected final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (octetBufferIndex + paramInt2 < octetBuffer.length)
    {
      System.arraycopy(paramArrayOfByte, paramInt1, octetBuffer, octetBufferIndex, paramInt2);
      octetBufferIndex += paramInt2;
    }
    else
    {
      out.write(octetBuffer, 0, octetBufferIndex);
      out.write(paramArrayOfByte, paramInt1, paramInt2);
      octetBufferIndex = 0;
    }
  }
  
  protected final void flushBuffer()
    throws IOException
  {
    out.write(octetBuffer, 0, octetBufferIndex);
    octetBufferIndex = 0;
  }
  
  static byte[] toBytes(String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length()];
    for (int i = paramString.length() - 1; i >= 0; i--) {
      arrayOfByte[i] = ((byte)paramString.charAt(i));
    }
    return arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\UTF8XmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */