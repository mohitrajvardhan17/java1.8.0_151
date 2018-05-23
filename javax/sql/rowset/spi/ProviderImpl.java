package javax.sql.rowset.spi;

import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;

class ProviderImpl
  extends SyncProvider
{
  private String className = null;
  private String vendorName = null;
  private String ver = null;
  private int index;
  
  ProviderImpl() {}
  
  public void setClassname(String paramString)
  {
    className = paramString;
  }
  
  public String getClassname()
  {
    return className;
  }
  
  public void setVendor(String paramString)
  {
    vendorName = paramString;
  }
  
  public String getVendor()
  {
    return vendorName;
  }
  
  public void setVersion(String paramString)
  {
    ver = paramString;
  }
  
  public String getVersion()
  {
    return ver;
  }
  
  public void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public int getDataSourceLock()
    throws SyncProviderException
  {
    int i = 0;
    try
    {
      i = SyncFactory.getInstance(className).getDataSourceLock();
    }
    catch (SyncFactoryException localSyncFactoryException)
    {
      throw new SyncProviderException(localSyncFactoryException.getMessage());
    }
    return i;
  }
  
  public int getProviderGrade()
  {
    int i = 0;
    try
    {
      i = SyncFactory.getInstance(className).getProviderGrade();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return i;
  }
  
  public String getProviderID()
  {
    return className;
  }
  
  public RowSetReader getRowSetReader()
  {
    RowSetReader localRowSetReader = null;
    try
    {
      localRowSetReader = SyncFactory.getInstance(className).getRowSetReader();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return localRowSetReader;
  }
  
  public RowSetWriter getRowSetWriter()
  {
    RowSetWriter localRowSetWriter = null;
    try
    {
      localRowSetWriter = SyncFactory.getInstance(className).getRowSetWriter();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return localRowSetWriter;
  }
  
  public void setDataSourceLock(int paramInt)
    throws SyncProviderException
  {
    try
    {
      SyncFactory.getInstance(className).setDataSourceLock(paramInt);
    }
    catch (SyncFactoryException localSyncFactoryException)
    {
      throw new SyncProviderException(localSyncFactoryException.getMessage());
    }
  }
  
  public int supportsUpdatableView()
  {
    int i = 0;
    try
    {
      i = SyncFactory.getInstance(className).supportsUpdatableView();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\spi\ProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */