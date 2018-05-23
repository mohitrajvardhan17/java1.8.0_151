package javax.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DiagnosticCollector<S>
  implements DiagnosticListener<S>
{
  private List<Diagnostic<? extends S>> diagnostics = Collections.synchronizedList(new ArrayList());
  
  public DiagnosticCollector() {}
  
  public void report(Diagnostic<? extends S> paramDiagnostic)
  {
    paramDiagnostic.getClass();
    diagnostics.add(paramDiagnostic);
  }
  
  public List<Diagnostic<? extends S>> getDiagnostics()
  {
    return Collections.unmodifiableList(diagnostics);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\DiagnosticCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */