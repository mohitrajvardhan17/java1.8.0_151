package javax.management.relation;

import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RoleList
  extends ArrayList<Object>
{
  private transient boolean typeSafe;
  private transient boolean tainted;
  private static final long serialVersionUID = 5568344346499649313L;
  
  public RoleList() {}
  
  public RoleList(int paramInt)
  {
    super(paramInt);
  }
  
  public RoleList(List<Role> paramList)
    throws IllegalArgumentException
  {
    if (paramList == null) {
      throw new IllegalArgumentException("Null parameter");
    }
    checkTypeSafe(paramList);
    super.addAll(paramList);
  }
  
  public List<Role> asList()
  {
    if (!typeSafe)
    {
      if (tainted) {
        checkTypeSafe(this);
      }
      typeSafe = true;
    }
    return (List)Util.cast(this);
  }
  
  public void add(Role paramRole)
    throws IllegalArgumentException
  {
    if (paramRole == null)
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    super.add(paramRole);
  }
  
  public void add(int paramInt, Role paramRole)
    throws IllegalArgumentException, IndexOutOfBoundsException
  {
    if (paramRole == null)
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    super.add(paramInt, paramRole);
  }
  
  public void set(int paramInt, Role paramRole)
    throws IllegalArgumentException, IndexOutOfBoundsException
  {
    if (paramRole == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    super.set(paramInt, paramRole);
  }
  
  public boolean addAll(RoleList paramRoleList)
    throws IndexOutOfBoundsException
  {
    if (paramRoleList == null) {
      return true;
    }
    return super.addAll(paramRoleList);
  }
  
  public boolean addAll(int paramInt, RoleList paramRoleList)
    throws IllegalArgumentException, IndexOutOfBoundsException
  {
    if (paramRoleList == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    return super.addAll(paramInt, paramRoleList);
  }
  
  public boolean add(Object paramObject)
  {
    if (!tainted) {
      tainted = isTainted(paramObject);
    }
    if (typeSafe) {
      checkTypeSafe(paramObject);
    }
    return super.add(paramObject);
  }
  
  public void add(int paramInt, Object paramObject)
  {
    if (!tainted) {
      tainted = isTainted(paramObject);
    }
    if (typeSafe) {
      checkTypeSafe(paramObject);
    }
    super.add(paramInt, paramObject);
  }
  
  public boolean addAll(Collection<?> paramCollection)
  {
    if (!tainted) {
      tainted = isTainted(paramCollection);
    }
    if (typeSafe) {
      checkTypeSafe(paramCollection);
    }
    return super.addAll(paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<?> paramCollection)
  {
    if (!tainted) {
      tainted = isTainted(paramCollection);
    }
    if (typeSafe) {
      checkTypeSafe(paramCollection);
    }
    return super.addAll(paramInt, paramCollection);
  }
  
  public Object set(int paramInt, Object paramObject)
  {
    if (!tainted) {
      tainted = isTainted(paramObject);
    }
    if (typeSafe) {
      checkTypeSafe(paramObject);
    }
    return super.set(paramInt, paramObject);
  }
  
  private static void checkTypeSafe(Object paramObject)
  {
    try
    {
      paramObject = (Role)paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException(localClassCastException);
    }
  }
  
  private static void checkTypeSafe(Collection<?> paramCollection)
  {
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        Role localRole = (Role)localObject;
      }
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException(localClassCastException);
    }
  }
  
  private static boolean isTainted(Object paramObject)
  {
    try
    {
      checkTypeSafe(paramObject);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return true;
    }
    return false;
  }
  
  private static boolean isTainted(Collection<?> paramCollection)
  {
    try
    {
      checkTypeSafe(paramCollection);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\RoleList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */