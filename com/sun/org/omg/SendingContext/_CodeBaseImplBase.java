package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.RepositoryHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _CodeBaseImplBase
  extends ObjectImpl
  implements CodeBase, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0" };
  
  public _CodeBaseImplBase() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = paramResponseHandler.createReply();
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    Object localObject1;
    Object localObject2;
    switch (localInteger.intValue())
    {
    case 0: 
      localObject1 = null;
      localObject1 = get_ir();
      RepositoryHelper.write(localOutputStream, (Repository)localObject1);
      break;
    case 1: 
      localObject1 = RepositoryIdHelper.read(paramInputStream);
      localObject2 = null;
      localObject2 = implementation((String)localObject1);
      localOutputStream.write_string((String)localObject2);
      break;
    case 2: 
      localObject1 = RepositoryIdSeqHelper.read(paramInputStream);
      localObject2 = null;
      localObject2 = implementations((String[])localObject1);
      URLSeqHelper.write(localOutputStream, (String[])localObject2);
      break;
    case 3: 
      localObject1 = RepositoryIdHelper.read(paramInputStream);
      localObject2 = null;
      localObject2 = meta((String)localObject1);
      FullValueDescriptionHelper.write(localOutputStream, (FullValueDescription)localObject2);
      break;
    case 4: 
      localObject1 = RepositoryIdSeqHelper.read(paramInputStream);
      localObject2 = null;
      localObject2 = metas((String[])localObject1);
      ValueDescSeqHelper.write(localOutputStream, (FullValueDescription[])localObject2);
      break;
    case 5: 
      localObject1 = RepositoryIdHelper.read(paramInputStream);
      localObject2 = null;
      localObject2 = bases((String)localObject1);
      RepositoryIdSeqHelper.write(localOutputStream, (String[])localObject2);
      break;
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    return localOutputStream;
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  static
  {
    _methods.put("get_ir", new Integer(0));
    _methods.put("implementation", new Integer(1));
    _methods.put("implementations", new Integer(2));
    _methods.put("meta", new Integer(3));
    _methods.put("metas", new Integer(4));
    _methods.put("bases", new Integer(5));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\SendingContext\_CodeBaseImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */