package org.omg.CORBA_2_3.portable;

public abstract class ObjectImpl
  extends org.omg.CORBA.portable.ObjectImpl
{
  public ObjectImpl() {}
  
  public String _get_codebase()
  {
    org.omg.CORBA.portable.Delegate localDelegate = _get_delegate();
    if ((localDelegate instanceof Delegate)) {
      return ((Delegate)localDelegate).get_codebase(this);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA_2_3\portable\ObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */