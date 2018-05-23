package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import java.io.IOException;

public class XML11DocumentScannerImpl
  extends XMLDocumentScannerImpl
{
  private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  private final XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
  
  public XML11DocumentScannerImpl() {}
  
  protected int scanContent(XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    fTempString.length = 0;
    int i = fEntityScanner.scanContent(fTempString);
    paramXMLStringBuffer.append(fTempString);
    if ((i == 13) || (i == 133) || (i == 8232))
    {
      fEntityScanner.scanChar(null);
      paramXMLStringBuffer.append((char)i);
      i = -1;
    }
    if (i == 93)
    {
      paramXMLStringBuffer.append((char)fEntityScanner.scanChar(null));
      fInScanContent = true;
      if (fEntityScanner.skipChar(93, null))
      {
        paramXMLStringBuffer.append(']');
        while (fEntityScanner.skipChar(93, null)) {
          paramXMLStringBuffer.append(']');
        }
        if (fEntityScanner.skipChar(62, null)) {
          reportFatalError("CDEndInContent", null);
        }
      }
      fInScanContent = false;
      i = -1;
    }
    return i;
  }
  
  protected boolean scanAttributeValue(XMLString paramXMLString1, XMLString paramXMLString2, String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
    throws IOException, XNIException
  {
    int i = fEntityScanner.peekChar();
    if ((i != 39) && (i != 34)) {
      reportFatalError("OpenQuoteExpected", new Object[] { paramString2, paramString1 });
    }
    fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
    int j = fEntityDepth;
    int k = fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
    int m = 0;
    int n;
    if ((k == i) && ((m = isUnchangedByNormalization(paramXMLString1)) == -1))
    {
      paramXMLString2.setValues(paramXMLString1);
      n = fEntityScanner.scanChar(XMLScanner.NameType.ATTRIBUTE);
      if (n != i) {
        reportFatalError("CloseQuoteExpected", new Object[] { paramString2, paramString1 });
      }
      return true;
    }
    fStringBuffer2.clear();
    fStringBuffer2.append(paramXMLString1);
    normalizeWhitespace(paramXMLString1, m);
    if (k != i)
    {
      fScanningAttribute = true;
      fStringBuffer.clear();
      do
      {
        fStringBuffer.append(paramXMLString1);
        if (k == 38)
        {
          fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE);
          if (j == fEntityDepth) {
            fStringBuffer2.append('&');
          }
          if (fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE))
          {
            if (j == fEntityDepth) {
              fStringBuffer2.append('#');
            }
            n = scanCharReferenceValue(fStringBuffer, fStringBuffer2);
            if (n == -1) {}
          }
          else
          {
            String str = fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
            if (str == null) {
              reportFatalError("NameRequiredInReference", null);
            } else if (j == fEntityDepth) {
              fStringBuffer2.append(str);
            }
            if (!fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
              reportFatalError("SemicolonRequiredInReference", new Object[] { str });
            } else if (j == fEntityDepth) {
              fStringBuffer2.append(';');
            }
            if (resolveCharacter(str, fStringBuffer))
            {
              checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, 1);
            }
            else if (fEntityManager.isExternalEntity(str))
            {
              reportFatalError("ReferenceToExternalEntity", new Object[] { str });
            }
            else
            {
              if (!fEntityManager.isDeclaredEntity(str)) {
                if (paramBoolean1)
                {
                  if (fValidation) {
                    fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { str }, (short)1);
                  }
                }
                else {
                  reportFatalError("EntityNotDeclared", new Object[] { str });
                }
              }
              fEntityManager.startEntity(true, str, true);
            }
          }
        }
        else if (k == 60)
        {
          reportFatalError("LessthanInAttValue", new Object[] { paramString2, paramString1 });
          fEntityScanner.scanChar(null);
          if (j == fEntityDepth) {
            fStringBuffer2.append((char)k);
          }
        }
        else if ((k == 37) || (k == 93))
        {
          fEntityScanner.scanChar(null);
          fStringBuffer.append((char)k);
          if (j == fEntityDepth) {
            fStringBuffer2.append((char)k);
          }
        }
        else if ((k == 10) || (k == 13) || (k == 133) || (k == 8232))
        {
          fEntityScanner.scanChar(null);
          fStringBuffer.append(' ');
          if (j == fEntityDepth) {
            fStringBuffer2.append('\n');
          }
        }
        else if ((k != -1) && (XMLChar.isHighSurrogate(k)))
        {
          fStringBuffer3.clear();
          if (scanSurrogates(fStringBuffer3))
          {
            fStringBuffer.append(fStringBuffer3);
            if (j == fEntityDepth) {
              fStringBuffer2.append(fStringBuffer3);
            }
          }
        }
        else if ((k != -1) && (isInvalidLiteral(k)))
        {
          reportFatalError("InvalidCharInAttValue", new Object[] { paramString2, paramString1, Integer.toString(k, 16) });
          fEntityScanner.scanChar(null);
          if (j == fEntityDepth) {
            fStringBuffer2.append((char)k);
          }
        }
        k = fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
        if (j == fEntityDepth) {
          fStringBuffer2.append(paramXMLString1);
        }
        normalizeWhitespace(paramXMLString1);
      } while ((k != i) || (j != fEntityDepth));
      fStringBuffer.append(paramXMLString1);
      paramXMLString1.setValues(fStringBuffer);
      fScanningAttribute = false;
    }
    paramXMLString2.setValues(fStringBuffer2);
    int i1 = fEntityScanner.scanChar(null);
    if (i1 != i) {
      reportFatalError("CloseQuoteExpected", new Object[] { paramString2, paramString1 });
    }
    return paramXMLString2.equals(ch, offset, length);
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
    return XML11Char.isXML11Invalid(paramInt);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11DocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */