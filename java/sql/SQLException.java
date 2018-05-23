package java.sql;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class SQLException
  extends Exception
  implements Iterable<Throwable>
{
  private String SQLState;
  private int vendorCode;
  private volatile SQLException next;
  private static final AtomicReferenceFieldUpdater<SQLException, SQLException> nextUpdater = AtomicReferenceFieldUpdater.newUpdater(SQLException.class, SQLException.class, "next");
  private static final long serialVersionUID = 2135244094396331484L;
  
  public SQLException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1);
    SQLState = paramString2;
    vendorCode = paramInt;
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null))
    {
      DriverManager.println("SQLState(" + paramString2 + ") vendor code(" + paramInt + ")");
      printStackTrace(DriverManager.getLogWriter());
    }
  }
  
  public SQLException(String paramString1, String paramString2)
  {
    super(paramString1);
    SQLState = paramString2;
    vendorCode = 0;
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null))
    {
      printStackTrace(DriverManager.getLogWriter());
      DriverManager.println("SQLException: SQLState(" + paramString2 + ")");
    }
  }
  
  public SQLException(String paramString)
  {
    super(paramString);
    SQLState = null;
    vendorCode = 0;
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null)) {
      printStackTrace(DriverManager.getLogWriter());
    }
  }
  
  public SQLException()
  {
    SQLState = null;
    vendorCode = 0;
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null)) {
      printStackTrace(DriverManager.getLogWriter());
    }
  }
  
  public SQLException(Throwable paramThrowable)
  {
    super(paramThrowable);
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null)) {
      printStackTrace(DriverManager.getLogWriter());
    }
  }
  
  public SQLException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null)) {
      printStackTrace(DriverManager.getLogWriter());
    }
  }
  
  public SQLException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramThrowable);
    SQLState = paramString2;
    vendorCode = 0;
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null))
    {
      printStackTrace(DriverManager.getLogWriter());
      DriverManager.println("SQLState(" + SQLState + ")");
    }
  }
  
  public SQLException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramThrowable);
    SQLState = paramString2;
    vendorCode = paramInt;
    if ((!(this instanceof SQLWarning)) && (DriverManager.getLogWriter() != null))
    {
      DriverManager.println("SQLState(" + SQLState + ") vendor code(" + paramInt + ")");
      printStackTrace(DriverManager.getLogWriter());
    }
  }
  
  public String getSQLState()
  {
    return SQLState;
  }
  
  public int getErrorCode()
  {
    return vendorCode;
  }
  
  public SQLException getNextException()
  {
    return next;
  }
  
  public void setNextException(SQLException paramSQLException)
  {
    Object localObject = this;
    for (;;)
    {
      SQLException localSQLException = next;
      if (localSQLException != null)
      {
        localObject = localSQLException;
      }
      else
      {
        if (nextUpdater.compareAndSet(localObject, null, paramSQLException)) {
          return;
        }
        localObject = next;
      }
    }
  }
  
  public Iterator<Throwable> iterator()
  {
    new Iterator()
    {
      SQLException firstException = SQLException.this;
      SQLException nextException = firstException.getNextException();
      Throwable cause = firstException.getCause();
      
      public boolean hasNext()
      {
        return (firstException != null) || (nextException != null) || (cause != null);
      }
      
      public Throwable next()
      {
        Object localObject = null;
        if (firstException != null)
        {
          localObject = firstException;
          firstException = null;
        }
        else if (cause != null)
        {
          localObject = cause;
          cause = cause.getCause();
        }
        else if (nextException != null)
        {
          localObject = nextException;
          cause = nextException.getCause();
          nextException = nextException.getNextException();
        }
        else
        {
          throw new NoSuchElementException();
        }
        return (Throwable)localObject;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */