package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.RunTimeOperations;

public abstract interface CodeBaseOperations
  extends RunTimeOperations
{
  public abstract Repository get_ir();
  
  public abstract String implementation(String paramString);
  
  public abstract String[] implementations(String[] paramArrayOfString);
  
  public abstract FullValueDescription meta(String paramString);
  
  public abstract FullValueDescription[] metas(String[] paramArrayOfString);
  
  public abstract String[] bases(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\SendingContext\CodeBaseOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */