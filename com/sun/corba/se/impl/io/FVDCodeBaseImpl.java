package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import java.util.Hashtable;
import java.util.Stack;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;

public class FVDCodeBaseImpl
  extends _CodeBaseImplBase
{
  private static Hashtable fvds = new Hashtable();
  private transient ORB orb = null;
  private transient OMGSystemException wrapper = OMGSystemException.get("rpc.encoding");
  private transient ValueHandlerImpl vhandler = null;
  
  public FVDCodeBaseImpl() {}
  
  void setValueHandler(ValueHandler paramValueHandler)
  {
    vhandler = ((ValueHandlerImpl)paramValueHandler);
  }
  
  public Repository get_ir()
  {
    return null;
  }
  
  public String implementation(String paramString)
  {
    try
    {
      if (vhandler == null) {
        vhandler = ValueHandlerImpl.getInstance(false);
      }
      String str = Util.getCodebase(vhandler.getClassFromType(paramString));
      if (str == null) {
        return "";
      }
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
    }
  }
  
  public String[] implementations(String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++) {
      arrayOfString[i] = implementation(paramArrayOfString[i]);
    }
    return arrayOfString;
  }
  
  public FullValueDescription meta(String paramString)
  {
    try
    {
      FullValueDescription localFullValueDescription = (FullValueDescription)fvds.get(paramString);
      if (localFullValueDescription == null)
      {
        if (vhandler == null) {
          vhandler = ValueHandlerImpl.getInstance(false);
        }
        try
        {
          localFullValueDescription = ValueUtility.translate(_orb(), ObjectStreamClass.lookup(vhandler.getAnyClassFromType(paramString)), vhandler);
        }
        catch (Throwable localThrowable2)
        {
          if (orb == null) {
            orb = ORB.init();
          }
          localFullValueDescription = ValueUtility.translate(orb, ObjectStreamClass.lookup(vhandler.getAnyClassFromType(paramString)), vhandler);
        }
        if (localFullValueDescription != null) {
          fvds.put(paramString, localFullValueDescription);
        } else {
          throw wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE);
        }
      }
      return localFullValueDescription;
    }
    catch (Throwable localThrowable1)
    {
      throw wrapper.incompatibleValueImpl(CompletionStatus.COMPLETED_MAYBE, localThrowable1);
    }
  }
  
  public FullValueDescription[] metas(String[] paramArrayOfString)
  {
    FullValueDescription[] arrayOfFullValueDescription = new FullValueDescription[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++) {
      arrayOfFullValueDescription[i] = meta(paramArrayOfString[i]);
    }
    return arrayOfFullValueDescription;
  }
  
  public String[] bases(String paramString)
  {
    try
    {
      if (vhandler == null) {
        vhandler = ValueHandlerImpl.getInstance(false);
      }
      Stack localStack = new Stack();
      for (Class localClass = ObjectStreamClass.lookup(vhandler.getClassFromType(paramString)).forClass().getSuperclass(); !localClass.equals(Object.class); localClass = localClass.getSuperclass()) {
        localStack.push(vhandler.createForAnyType(localClass));
      }
      String[] arrayOfString = new String[localStack.size()];
      for (int i = arrayOfString.length - 1; i >= 0; i++) {
        arrayOfString[i] = ((String)localStack.pop());
      }
      return arrayOfString;
    }
    catch (Throwable localThrowable)
    {
      throw wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, localThrowable);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\io\FVDCodeBaseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */