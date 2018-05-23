package javax.sql.rowset.spi;

import com.sun.rowset.internal.SyncResolverImpl;
import java.sql.SQLException;

public class SyncProviderException
  extends SQLException
{
  private SyncResolver syncResolver = null;
  static final long serialVersionUID = -939908523620640692L;
  
  public SyncProviderException() {}
  
  public SyncProviderException(String paramString)
  {
    super(paramString);
  }
  
  public SyncProviderException(SyncResolver paramSyncResolver)
  {
    if (paramSyncResolver == null) {
      throw new IllegalArgumentException("Cannot instantiate a SyncProviderException with a null SyncResolver object");
    }
    syncResolver = paramSyncResolver;
  }
  
  public SyncResolver getSyncResolver()
  {
    if (syncResolver != null) {
      return syncResolver;
    }
    try
    {
      syncResolver = new SyncResolverImpl();
    }
    catch (SQLException localSQLException) {}
    return syncResolver;
  }
  
  public void setSyncResolver(SyncResolver paramSyncResolver)
  {
    if (paramSyncResolver == null) {
      throw new IllegalArgumentException("Cannot set a null SyncResolver object");
    }
    syncResolver = paramSyncResolver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\spi\SyncProviderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */