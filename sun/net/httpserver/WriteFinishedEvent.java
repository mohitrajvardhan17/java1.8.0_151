package sun.net.httpserver;

class WriteFinishedEvent
  extends Event
{
  WriteFinishedEvent(ExchangeImpl paramExchangeImpl)
  {
    super(paramExchangeImpl);
    assert (!writefinished);
    writefinished = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\WriteFinishedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */