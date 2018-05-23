package org.omg.CosNaming;

import java.util.Dictionary;
import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;

public abstract class _BindingIteratorImplBase
  extends DynamicImplementation
  implements BindingIterator
{
  private static final String[] _type_ids = { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
  private static Dictionary _methods = new Hashtable();
  
  public _BindingIteratorImplBase() {}
  
  public String[] _ids()
  {
    return (String[])_type_ids.clone();
  }
  
  public void invoke(ServerRequest paramServerRequest)
  {
    NVList localNVList;
    Any localAny1;
    Object localObject1;
    Object localObject2;
    switch (((Integer)_methods.get(paramServerRequest.op_name())).intValue())
    {
    case 0: 
      localNVList = _orb().create_list(0);
      localAny1 = _orb().create_any();
      localAny1.type(BindingHelper.type());
      localNVList.add_value("b", localAny1, 2);
      paramServerRequest.params(localNVList);
      localObject1 = new BindingHolder();
      boolean bool1 = next_one((BindingHolder)localObject1);
      BindingHelper.insert(localAny1, value);
      localObject2 = _orb().create_any();
      ((Any)localObject2).insert_boolean(bool1);
      paramServerRequest.result((Any)localObject2);
      break;
    case 1: 
      localNVList = _orb().create_list(0);
      localAny1 = _orb().create_any();
      localAny1.type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
      localNVList.add_value("how_many", localAny1, 1);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(BindingListHelper.type());
      localNVList.add_value("bl", (Any)localObject1, 2);
      paramServerRequest.params(localNVList);
      int i = localAny1.extract_ulong();
      localObject2 = new BindingListHolder();
      boolean bool2 = next_n(i, (BindingListHolder)localObject2);
      BindingListHelper.insert((Any)localObject1, value);
      Any localAny2 = _orb().create_any();
      localAny2.insert_boolean(bool2);
      paramServerRequest.result(localAny2);
      break;
    case 2: 
      localNVList = _orb().create_list(0);
      paramServerRequest.params(localNVList);
      destroy();
      localAny1 = _orb().create_any();
      localAny1.type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result(localAny1);
      break;
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
  }
  
  static
  {
    _methods.put("next_one", new Integer(0));
    _methods.put("next_n", new Integer(1));
    _methods.put("destroy", new Integer(2));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\_BindingIteratorImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */