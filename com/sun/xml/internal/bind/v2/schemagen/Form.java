package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.namespace.QName;

 enum Form
{
  QUALIFIED(XmlNsForm.QUALIFIED, true),  UNQUALIFIED(XmlNsForm.UNQUALIFIED, false),  UNSET(XmlNsForm.UNSET, false);
  
  private final XmlNsForm xnf;
  public final boolean isEffectivelyQualified;
  
  private Form(XmlNsForm paramXmlNsForm, boolean paramBoolean)
  {
    xnf = paramXmlNsForm;
    isEffectivelyQualified = paramBoolean;
  }
  
  abstract void declare(String paramString, Schema paramSchema);
  
  public void writeForm(LocalElement paramLocalElement, QName paramQName)
  {
    _writeForm(paramLocalElement, paramQName);
  }
  
  public void writeForm(LocalAttribute paramLocalAttribute, QName paramQName)
  {
    _writeForm(paramLocalAttribute, paramQName);
  }
  
  private void _writeForm(TypedXmlWriter paramTypedXmlWriter, QName paramQName)
  {
    int i = paramQName.getNamespaceURI().length() > 0 ? 1 : 0;
    if ((i != 0) && (this != QUALIFIED)) {
      paramTypedXmlWriter._attribute("form", "qualified");
    } else if ((i == 0) && (this == QUALIFIED)) {
      paramTypedXmlWriter._attribute("form", "unqualified");
    }
  }
  
  public static Form get(XmlNsForm paramXmlNsForm)
  {
    for (Form localForm : ) {
      if (xnf == paramXmlNsForm) {
        return localForm;
      }
    }
    throw new IllegalArgumentException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\Form.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */