package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public abstract interface DirStateFactory
  extends StateFactory
{
  public abstract Result getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes)
    throws NamingException;
  
  public static class Result
  {
    private Object obj;
    private Attributes attrs;
    
    public Result(Object paramObject, Attributes paramAttributes)
    {
      obj = paramObject;
      attrs = paramAttributes;
    }
    
    public Object getObject()
    {
      return obj;
    }
    
    public Attributes getAttributes()
    {
      return attrs;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\DirStateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */