package com.sun.rowset.providers;

import com.sun.rowset.JdbcRowSetResourceBundle;
import com.sun.rowset.internal.CachedRowSetReader;
import com.sun.rowset.internal.CachedRowSetWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

public final class RIOptimisticProvider
  extends SyncProvider
  implements Serializable
{
  private CachedRowSetReader reader = new CachedRowSetReader();
  private CachedRowSetWriter writer = new CachedRowSetWriter();
  private String providerID = "com.sun.rowset.providers.RIOptimisticProvider";
  private String vendorName = "Oracle Corporation";
  private String versionNumber = "1.0";
  private JdbcRowSetResourceBundle resBundle;
  static final long serialVersionUID = -3143367176751761936L;
  
  public RIOptimisticProvider()
  {
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public String getProviderID()
  {
    return providerID;
  }
  
  public RowSetWriter getRowSetWriter()
  {
    try
    {
      writer.setReader(reader);
    }
    catch (SQLException localSQLException) {}
    return writer;
  }
  
  public RowSetReader getRowSetReader()
  {
    return reader;
  }
  
  public int getProviderGrade()
  {
    return 2;
  }
  
  public void setDataSourceLock(int paramInt)
    throws SyncProviderException
  {
    if (paramInt != 1) {
      throw new SyncProviderException(resBundle.handleGetObject("riop.locking").toString());
    }
  }
  
  public int getDataSourceLock()
    throws SyncProviderException
  {
    return 1;
  }
  
  public int supportsUpdatableView()
  {
    return 6;
  }
  
  public String getVersion()
  {
    return versionNumber;
  }
  
  public String getVendor()
  {
    return vendorName;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\providers\RIOptimisticProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */