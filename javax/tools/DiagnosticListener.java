package javax.tools;

public abstract interface DiagnosticListener<S>
{
  public abstract void report(Diagnostic<? extends S> paramDiagnostic);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\DiagnosticListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */