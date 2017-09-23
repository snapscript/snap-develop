package org.snapscript.studio.tunnel;

import org.snapscript.agent.event.MessageEnvelope;
import org.snapscript.agent.event.ProcessEventChannel;

public interface MessageEnvelopeProcessor {
   void process(ProcessEventChannel channel, MessageEnvelope message) throws Exception;
}