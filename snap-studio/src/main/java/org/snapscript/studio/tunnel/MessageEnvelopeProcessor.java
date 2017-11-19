package org.snapscript.studio.tunnel;

import org.snapscript.studio.agent.event.MessageEnvelope;
import org.snapscript.studio.agent.event.ProcessEventChannel;

public interface MessageEnvelopeProcessor {
   void process(ProcessEventChannel channel, MessageEnvelope message) throws Exception;
}