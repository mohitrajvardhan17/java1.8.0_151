package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.InputStream;

final class MimeUtility
{
  private static final boolean ignoreUnknownEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoreunknownencoding", false);
  
  private MimeUtility() {}
  
  public static InputStream decode(InputStream paramInputStream, String paramString)
    throws DecodingException
  {
    if (paramString.equalsIgnoreCase("base64")) {
      return new BASE64DecoderStream(paramInputStream);
    }
    if (paramString.equalsIgnoreCase("quoted-printable")) {
      return new QPDecoderStream(paramInputStream);
    }
    if ((paramString.equalsIgnoreCase("uuencode")) || (paramString.equalsIgnoreCase("x-uuencode")) || (paramString.equalsIgnoreCase("x-uue"))) {
      return new UUDecoderStream(paramInputStream);
    }
    if ((paramString.equalsIgnoreCase("binary")) || (paramString.equalsIgnoreCase("7bit")) || (paramString.equalsIgnoreCase("8bit"))) {
      return paramInputStream;
    }
    if (!ignoreUnknownEncoding) {
      throw new DecodingException("Unknown encoding: " + paramString);
    }
    return paramInputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MimeUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */