package javax.xml.bind.attachment;

import javax.activation.DataHandler;

public abstract class AttachmentMarshaller
{
  public AttachmentMarshaller() {}
  
  public abstract String addMtomAttachment(DataHandler paramDataHandler, String paramString1, String paramString2);
  
  public abstract String addMtomAttachment(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3);
  
  public boolean isXOPPackage()
  {
    return false;
  }
  
  public abstract String addSwaRefAttachment(DataHandler paramDataHandler);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\attachment\AttachmentMarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */