package javax.sql.rowset;

import java.sql.SQLException;

public class RowSetWarning
  extends SQLException
{
  static final long serialVersionUID = 6678332766434564774L;
  
  public RowSetWarning(String paramString)
  {
    super(paramString);
  }
  
  public RowSetWarning() {}
  
  public RowSetWarning(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public RowSetWarning(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public RowSetWarning getNextWarning()
  {
    SQLException localSQLException = getNextException();
    if ((localSQLException == null) || ((localSQLException instanceof RowSetWarning))) {
      return (RowSetWarning)localSQLException;
    }
    throw new Error("RowSetWarning chain holds value that is not a RowSetWarning: ");
  }
  
  public void setNextWarning(RowSetWarning paramRowSetWarning)
  {
    setNextException(paramRowSetWarning);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\RowSetWarning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */