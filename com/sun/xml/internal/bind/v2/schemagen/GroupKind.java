package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ContentModelContainer;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;

 enum GroupKind
{
  ALL("all"),  SEQUENCE("sequence"),  CHOICE("choice");
  
  private final String name;
  
  private GroupKind(String paramString)
  {
    name = paramString;
  }
  
  Particle write(ContentModelContainer paramContentModelContainer)
  {
    return (Particle)paramContentModelContainer._element(name, Particle.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\GroupKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */