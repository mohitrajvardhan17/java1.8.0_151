package javax.sql.rowset.serial;

import java.sql.SQLException;

public class SerialException
  extends SQLException
{
  static final long serialVersionUID = -489794565168592690L;
  
  public SerialException() {}
  
  public SerialException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */