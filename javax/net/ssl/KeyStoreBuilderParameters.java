package javax.net.ssl;

import java.security.KeyStore.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KeyStoreBuilderParameters
  implements ManagerFactoryParameters
{
  private final List<KeyStore.Builder> parameters;
  
  public KeyStoreBuilderParameters(KeyStore.Builder paramBuilder)
  {
    parameters = Collections.singletonList(Objects.requireNonNull(paramBuilder));
  }
  
  public KeyStoreBuilderParameters(List<KeyStore.Builder> paramList)
  {
    if (paramList.isEmpty()) {
      throw new IllegalArgumentException();
    }
    parameters = Collections.unmodifiableList(new ArrayList(paramList));
  }
  
  public List<KeyStore.Builder> getParameters()
  {
    return parameters;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\KeyStoreBuilderParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */