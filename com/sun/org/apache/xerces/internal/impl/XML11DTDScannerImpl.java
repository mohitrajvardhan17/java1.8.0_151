package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public class XML11DTDScannerImpl
  extends XMLDTDScannerImpl
{
  private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  public XML11DTDScannerImpl() {}
  
  public XML11DTDScannerImpl(SymbolTable paramSymbolTable, XMLErrorReporter paramXMLErrorReporter, XMLEntityManager paramXMLEntityManager)
  {
    super(paramSymbolTable, paramXMLErrorReporter, paramXMLEntityManager);
  }
  
  protected boolean scanPubidLiteral(XMLString paramXMLString)
    throws IOException, XNIException
  {
    int i = fEntityScanner.scanChar(null);
    if ((i != 39) && (i != 34))
    {
      reportFatalError("QuoteRequiredInPublicID", null);
      return false;
    }
    fStringBuffer.clear();
    int j = 1;
    boolean bool = true;
    for (;;)
    {
      int k = fEntityScanner.scanChar(null);
      if ((k == 32) || (k == 10) || (k == 13) || (k == 133) || (k == 8232))
      {
        if (j == 0)
        {
          fStringBuffer.append(' ');
          j = 1;
        }
      }
      else
      {
        if (k == i)
        {
          if (j != 0) {
            fStringBuffer.length -= 1;
          }
          paramXMLString.setValues(fStringBuffer);
          break;
        }
        if (XMLChar.isPubid(k))
        {
          fStringBuffer.append((char)k);
          j = 0;
        }
        else
        {
          if (k == -1)
          {
            reportFatalError("PublicIDUnterminated", null);
            return false;
          }
          bool = false;
          reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(k) });
        }
      }
    }
    return bool;
  }
  
  protected void normalizeWhitespace(XMLString paramXMLString)
  {
    int i = offset + length;
    for (int j = offset; j < i; j++)
    {
      int k = ch[j];
      if (XMLChar.isSpace(k)) {
        ch[j] = ' ';
      }
    }
  }
  
  protected void normalizeWhitespace(XMLString paramXMLString, int paramInt)
  {
    int i = offset + length;
    for (int j = offset + paramInt; j < i; j++)
    {
      int k = ch[j];
      if (XMLChar.isSpace(k)) {
        ch[j] = ' ';
      }
    }
  }
  
  protected int isUnchangedByNormalization(XMLString paramXMLString)
  {
    int i = offset + length;
    for (int j = offset; j < i; j++)
    {
      int k = ch[j];
      if (XMLChar.isSpace(k)) {
        return j - offset;
      }
    }
    return -1;
  }
  
  protected boolean isInvalid(int paramInt)
  {
    return !XML11Char.isXML11Valid(paramInt);
  }
  
  protected boolean isInvalidLiteral(int paramInt)
  {
    return !XML11Char.isXML11ValidLiteral(paramInt);
  }
  
  protected boolean isValidNameChar(int paramInt)
  {
    return XML11Char.isXML11Name(paramInt);
  }
  
  protected boolean isValidNameStartChar(int paramInt)
  {
    return XML11Char.isXML11NameStart(paramInt);
  }
  
  protected boolean isValidNCName(int paramInt)
  {
    return XML11Char.isXML11NCName(paramInt);
  }
  
  protected boolean isValidNameStartHighSurrogate(int paramInt)
  {
    return XML11Char.isXML11NameHighSurrogate(paramInt);
  }
  
  protected boolean versionSupported(String paramString)
  {
    return (paramString.equals("1.1")) || (paramString.equals("1.0"));
  }
  
  protected String getVersionNotSupportedKey()
  {
    return "VersionNotSupported11";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11DTDScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */