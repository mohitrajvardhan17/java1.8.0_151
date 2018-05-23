package com.sun.xml.internal.messaging.saaj.packaging.mime;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import javax.activation.DataSource;

public abstract interface MultipartDataSource
  extends DataSource
{
  public abstract int getCount();
  
  public abstract MimeBodyPart getBodyPart(int paramInt)
    throws MessagingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\MultipartDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */