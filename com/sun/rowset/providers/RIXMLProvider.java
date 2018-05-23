package com.sun.rowset.providers;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.XmlReader;
import javax.sql.rowset.spi.XmlWriter;

public final class RIXMLProvider
  extends SyncProvider
{
  private String providerID = "com.sun.rowset.providers.RIXMLProvider";
  private String vendorName = "Oracle Corporation";
  private String versionNumber = "1.0";
  private JdbcRowSetResourceBundle resBundle;
  private XmlReader xmlReader;
  private XmlWriter xmlWriter;
  
  public RIXMLProvider()
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
  
  public void setXmlReader(XmlReader paramXmlReader)
    throws SQLException
  {
    xmlReader = paramXmlReader;
  }
  
  public void setXmlWriter(XmlWriter paramXmlWriter)
    throws SQLException
  {
    xmlWriter = paramXmlWriter;
  }
  
  public XmlReader getXmlReader()
    throws SQLException
  {
    return xmlReader;
  }
  
  public XmlWriter getXmlWriter()
    throws SQLException
  {
    return xmlWriter;
  }
  
  public int getProviderGrade()
  {
    return 1;
  }
  
  public int supportsUpdatableView()
  {
    return 6;
  }
  
  public int getDataSourceLock()
    throws SyncProviderException
  {
    return 1;
  }
  
  public void setDataSourceLock(int paramInt)
    throws SyncProviderException
  {
    throw new UnsupportedOperationException(resBundle.handleGetObject("rixml.unsupp").toString());
  }
  
  public RowSetWriter getRowSetWriter()
  {
    return null;
  }
  
  public RowSetReader getRowSetReader()
  {
    return null;
  }
  
  public String getVersion()
  {
    return versionNumber;
  }
  
  public String getVendor()
  {
    return vendorName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\providers\RIXMLProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */