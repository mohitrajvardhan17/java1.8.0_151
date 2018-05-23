package java.sql;

import java.util.Properties;
import java.util.logging.Logger;

public abstract interface Driver
{
  public abstract Connection connect(String paramString, Properties paramProperties)
    throws SQLException;
  
  public abstract boolean acceptsURL(String paramString)
    throws SQLException;
  
  public abstract DriverPropertyInfo[] getPropertyInfo(String paramString, Properties paramProperties)
    throws SQLException;
  
  public abstract int getMajorVersion();
  
  public abstract int getMinorVersion();
  
  public abstract boolean jdbcCompliant();
  
  public abstract Logger getParentLogger()
    throws SQLFeatureNotSupportedException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\Driver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */