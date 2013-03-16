package com.etrium.backup.system;

public interface EventListener
{
  public boolean ReceiveEvent(EtriumEvent p_event);
  void StartListening();
  void StopListening();
}
