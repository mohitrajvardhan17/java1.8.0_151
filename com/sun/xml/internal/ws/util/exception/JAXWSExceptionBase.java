package com.sun.xml.internal.ws.util.exception;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessage;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.NullLocalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;

public abstract class JAXWSExceptionBase
  extends WebServiceException
  implements Localizable
{
  private static final long serialVersionUID = 1L;
  private transient Localizable msg;
  
  /**
   * @deprecated
   */
  protected JAXWSExceptionBase(String paramString, Object... paramVarArgs)
  {
    super(findNestedException(paramVarArgs));
    msg = new LocalizableMessage(getDefaultResourceBundleName(), paramString, paramVarArgs);
  }
  
  protected JAXWSExceptionBase(String paramString)
  {
    this(new NullLocalizable(paramString));
  }
  
  protected JAXWSExceptionBase(Throwable paramThrowable)
  {
    this(new NullLocalizable(paramThrowable.toString()), paramThrowable);
  }
  
  protected JAXWSExceptionBase(Localizable paramLocalizable)
  {
    msg = paramLocalizable;
  }
  
  protected JAXWSExceptionBase(Localizable paramLocalizable, Throwable paramThrowable)
  {
    super(paramThrowable);
    msg = paramLocalizable;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(msg.getResourceBundleName());
    paramObjectOutputStream.writeObject(msg.getKey());
    Object[] arrayOfObject = msg.getArguments();
    if (arrayOfObject == null)
    {
      paramObjectOutputStream.writeInt(-1);
      return;
    }
    paramObjectOutputStream.writeInt(arrayOfObject.length);
    for (int i = 0; i < arrayOfObject.length; i++) {
      if ((arrayOfObject[i] == null) || ((arrayOfObject[i] instanceof Serializable))) {
        paramObjectOutputStream.writeObject(arrayOfObject[i]);
      } else {
        paramObjectOutputStream.writeObject(arrayOfObject[i].toString());
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    String str1 = (String)paramObjectInputStream.readObject();
    String str2 = (String)paramObjectInputStream.readObject();
    int i = paramObjectInputStream.readInt();
    if (i < -1) {
      throw new NegativeArraySizeException();
    }
    Object[] arrayOfObject;
    if (i == -1)
    {
      arrayOfObject = null;
    }
    else if (i < 255)
    {
      arrayOfObject = new Object[i];
      for (int j = 0; j < arrayOfObject.length; j++) {
        arrayOfObject[j] = paramObjectInputStream.readObject();
      }
    }
    else
    {
      ArrayList localArrayList = new ArrayList(Math.min(i, 1024));
      for (int k = 0; k < i; k++) {
        localArrayList.add(paramObjectInputStream.readObject());
      }
      arrayOfObject = localArrayList.toArray(new Object[localArrayList.size()]);
    }
    msg = new LocalizableMessageFactory(str1).getMessage(str2, arrayOfObject);
  }
  
  private static Throwable findNestedException(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    for (Object localObject : paramArrayOfObject) {
      if ((localObject instanceof Throwable)) {
        return (Throwable)localObject;
      }
    }
    return null;
  }
  
  public String getMessage()
  {
    Localizer localLocalizer = new Localizer();
    return localLocalizer.localize(this);
  }
  
  protected abstract String getDefaultResourceBundleName();
  
  public final String getKey()
  {
    return msg.getKey();
  }
  
  public final Object[] getArguments()
  {
    return msg.getArguments();
  }
  
  public final String getResourceBundleName()
  {
    return msg.getResourceBundleName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\exception\JAXWSExceptionBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */