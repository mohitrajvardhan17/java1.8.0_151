package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Header;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.Source;

public final class EPRRecipe
{
  private final List<Header> referenceParameters = new ArrayList();
  private final List<Source> metadata = new ArrayList();
  
  public EPRRecipe() {}
  
  @NotNull
  public List<Header> getReferenceParameters()
  {
    return referenceParameters;
  }
  
  @NotNull
  public List<Source> getMetadata()
  {
    return metadata;
  }
  
  public EPRRecipe addReferenceParameter(Header paramHeader)
  {
    if (paramHeader == null) {
      throw new IllegalArgumentException();
    }
    referenceParameters.add(paramHeader);
    return this;
  }
  
  public EPRRecipe addReferenceParameters(Header... paramVarArgs)
  {
    for (Header localHeader : paramVarArgs) {
      addReferenceParameter(localHeader);
    }
    return this;
  }
  
  public EPRRecipe addReferenceParameters(Iterable<? extends Header> paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Header localHeader = (Header)localIterator.next();
      addReferenceParameter(localHeader);
    }
    return this;
  }
  
  public EPRRecipe addMetadata(Source paramSource)
  {
    if (paramSource == null) {
      throw new IllegalArgumentException();
    }
    metadata.add(paramSource);
    return this;
  }
  
  public EPRRecipe addMetadata(Source... paramVarArgs)
  {
    for (Source localSource : paramVarArgs) {
      addMetadata(localSource);
    }
    return this;
  }
  
  public EPRRecipe addMetadata(Iterable<? extends Source> paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Source localSource = (Source)localIterator.next();
      addMetadata(localSource);
    }
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\EPRRecipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */