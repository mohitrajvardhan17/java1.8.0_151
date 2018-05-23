package com.sun.xml.internal.ws.util.pipe;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DumpTube
  extends AbstractFilterTubeImpl
{
  private final String name;
  private final PrintStream out;
  private final XMLOutputFactory staxOut;
  private static boolean warnStaxUtils;
  
  public DumpTube(String paramString, PrintStream paramPrintStream, Tube paramTube)
  {
    super(paramTube);
    name = paramString;
    out = paramPrintStream;
    staxOut = XMLOutputFactory.newInstance();
  }
  
  protected DumpTube(DumpTube paramDumpTube, TubeCloner paramTubeCloner)
  {
    super(paramDumpTube, paramTubeCloner);
    name = name;
    out = out;
    staxOut = staxOut;
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    dump("request", paramPacket);
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket)
  {
    dump("response", paramPacket);
    return super.processResponse(paramPacket);
  }
  
  protected void dump(String paramString, Packet paramPacket)
  {
    out.println("====[" + name + ":" + paramString + "]====");
    if (paramPacket.getMessage() == null) {
      out.println("(none)");
    } else {
      try
      {
        XMLStreamWriter localXMLStreamWriter = staxOut.createXMLStreamWriter(new PrintStream(out)
        {
          public void close() {}
        });
        localXMLStreamWriter = createIndenter(localXMLStreamWriter);
        paramPacket.getMessage().copy().writeTo(localXMLStreamWriter);
        localXMLStreamWriter.close();
      }
      catch (XMLStreamException localXMLStreamException)
      {
        localXMLStreamException.printStackTrace(out);
      }
    }
    out.println("============");
  }
  
  private XMLStreamWriter createIndenter(XMLStreamWriter paramXMLStreamWriter)
  {
    try
    {
      Class localClass = getClass().getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
      Constructor localConstructor = localClass.getConstructor(new Class[] { XMLStreamWriter.class });
      paramXMLStreamWriter = (XMLStreamWriter)localConstructor.newInstance(new Object[] { paramXMLStreamWriter });
    }
    catch (Exception localException)
    {
      if (!warnStaxUtils)
      {
        warnStaxUtils = true;
        out.println("WARNING: put stax-utils.jar to the classpath to indent the dump output");
      }
    }
    return paramXMLStreamWriter;
  }
  
  public AbstractTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new DumpTube(this, paramTubeCloner);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\pipe\DumpTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */