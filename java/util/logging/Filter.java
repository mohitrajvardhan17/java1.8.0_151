package java.util.logging;

@FunctionalInterface
public abstract interface Filter
{
  public abstract boolean isLoggable(LogRecord paramLogRecord);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */