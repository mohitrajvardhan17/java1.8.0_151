package javax.sql.rowset.spi;

import java.sql.SQLException;

public class SyncFactoryException
  extends SQLException
{
  static final long serialVersionUID = -4354595476433200352L;
  
  public SyncFactoryException() {}
  
  public SyncFactoryException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\spi\SyncFactoryException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */