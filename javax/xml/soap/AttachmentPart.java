package javax.xml.soap;

import java.io.InputStream;
import java.util.Iterator;
import javax.activation.DataHandler;

public abstract class AttachmentPart
{
  public AttachmentPart() {}
  
  public abstract int getSize()
    throws SOAPException;
  
  public abstract void clearContent();
  
  public abstract Object getContent()
    throws SOAPException;
  
  public abstract InputStream getRawContent()
    throws SOAPException;
  
  public abstract byte[] getRawContentBytes()
    throws SOAPException;
  
  public abstract InputStream getBase64Content()
    throws SOAPException;
  
  public abstract void setContent(Object paramObject, String paramString);
  
  public abstract void setRawContent(InputStream paramInputStream, String paramString)
    throws SOAPException;
  
  public abstract void setRawContentBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString)
    throws SOAPException;
  
  public abstract void setBase64Content(InputStream paramInputStream, String paramString)
    throws SOAPException;
  
  public abstract DataHandler getDataHandler()
    throws SOAPException;
  
  public abstract void setDataHandler(DataHandler paramDataHandler);
  
  public String getContentId()
  {
    String[] arrayOfString = getMimeHeader("Content-ID");
    if ((arrayOfString != null) && (arrayOfString.length > 0)) {
      return arrayOfString[0];
    }
    return null;
  }
  
  public String getContentLocation()
  {
    String[] arrayOfString = getMimeHeader("Content-Location");
    if ((arrayOfString != null) && (arrayOfString.length > 0)) {
      return arrayOfString[0];
    }
    return null;
  }
  
  public String getContentType()
  {
    String[] arrayOfString = getMimeHeader("Content-Type");
    if ((arrayOfString != null) && (arrayOfString.length > 0)) {
      return arrayOfString[0];
    }
    return null;
  }
  
  public void setContentId(String paramString)
  {
    setMimeHeader("Content-ID", paramString);
  }
  
  public void setContentLocation(String paramString)
  {
    setMimeHeader("Content-Location", paramString);
  }
  
  public void setContentType(String paramString)
  {
    setMimeHeader("Content-Type", paramString);
  }
  
  public abstract void removeMimeHeader(String paramString);
  
  public abstract void removeAllMimeHeaders();
  
  public abstract String[] getMimeHeader(String paramString);
  
  public abstract void setMimeHeader(String paramString1, String paramString2);
  
  public abstract void addMimeHeader(String paramString1, String paramString2);
  
  public abstract Iterator getAllMimeHeaders();
  
  public abstract Iterator getMatchingMimeHeaders(String[] paramArrayOfString);
  
  public abstract Iterator getNonMatchingMimeHeaders(String[] paramArrayOfString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\AttachmentPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */