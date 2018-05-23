package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.util.JAXWSUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;

final class SCAnnotations
{
  final ArrayList<QName> portQNames = new ArrayList();
  final ArrayList<Class> classes = new ArrayList();
  
  SCAnnotations(final Class<?> paramClass)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        WebServiceClient localWebServiceClient = (WebServiceClient)paramClass.getAnnotation(WebServiceClient.class);
        if (localWebServiceClient == null) {
          throw new WebServiceException("Service Interface Annotations required, exiting...");
        }
        String str = localWebServiceClient.targetNamespace();
        try
        {
          JAXWSUtils.getFileOrURL(localWebServiceClient.wsdlLocation());
        }
        catch (IOException localIOException)
        {
          throw new WebServiceException(localIOException);
        }
        for (Method localMethod : paramClass.getDeclaredMethods())
        {
          WebEndpoint localWebEndpoint = (WebEndpoint)localMethod.getAnnotation(WebEndpoint.class);
          if (localWebEndpoint != null)
          {
            localObject = localWebEndpoint.name();
            QName localQName = new QName(str, (String)localObject);
            portQNames.add(localQName);
          }
          Object localObject = localMethod.getReturnType();
          if (localObject != Void.TYPE) {
            classes.add(localObject);
          }
        }
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\SCAnnotations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */