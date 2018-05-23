package org.omg.CosNaming;

import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public abstract interface NamingContextOperations
{
  public abstract void bind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound;
  
  public abstract void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound;
  
  public abstract void rebind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName;
  
  public abstract void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName;
  
  public abstract org.omg.CORBA.Object resolve(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName;
  
  public abstract void unbind(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName;
  
  public abstract void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder);
  
  public abstract NamingContext new_context();
  
  public abstract NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, AlreadyBound, CannotProceed, InvalidName;
  
  public abstract void destroy()
    throws NotEmpty;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */