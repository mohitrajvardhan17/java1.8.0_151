package javax.xml.bind.attachment;

import javax.activation.DataHandler;

public abstract class AttachmentUnmarshaller
{
  public AttachmentUnmarshaller() {}
  
  public abstract DataHandler getAttachmentAsDataHandler(String paramString);
  
  public abstract byte[] getAttachmentAsByteArray(String paramString);
  
  public boolean isXOPPackage()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\attachment\AttachmentUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */