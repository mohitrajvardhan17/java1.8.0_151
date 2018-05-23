package java.sql;

public enum RowIdLifetime
{
  ROWID_UNSUPPORTED,  ROWID_VALID_OTHER,  ROWID_VALID_SESSION,  ROWID_VALID_TRANSACTION,  ROWID_VALID_FOREVER;
  
  private RowIdLifetime() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\RowIdLifetime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */