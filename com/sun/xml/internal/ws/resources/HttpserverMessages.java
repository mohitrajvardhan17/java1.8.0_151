package com.sun.xml.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class HttpserverMessages
{
  private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.xml.internal.ws.resources.httpserver");
  private static final Localizer localizer = new Localizer();
  
  public HttpserverMessages() {}
  
  public static Localizable localizableUNEXPECTED_HTTP_METHOD(Object paramObject)
  {
    return messageFactory.getMessage("unexpected.http.method", new Object[] { paramObject });
  }
  
  public static String UNEXPECTED_HTTP_METHOD(Object paramObject)
  {
    return localizer.localize(localizableUNEXPECTED_HTTP_METHOD(paramObject));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\resources\HttpserverMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */