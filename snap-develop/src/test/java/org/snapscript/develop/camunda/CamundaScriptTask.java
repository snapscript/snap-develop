package org.snapscript.develop.camunda;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.snapscript.agent.ProcessAgent;
import org.snapscript.agent.ProcessAgentService;
import org.snapscript.agent.ProcessMode;
import org.snapscript.core.MapModel;

public class CamundaScriptTask {
   
   private static final String RESOURCE = "/rfq.snap";
   private static final String PROJECT = "camunda";
   private static final String URL = "http://192.168.56.1:4457/resource/";

   public static void main(String[] list) throws Exception {
      CamundaScriptTask task = new CamundaScriptTask();
      CamundaContext context = new CamundaContext("ERTF-DBRE-HERH-ERYE", "rfq.bpmn");
      
      task.onScriptTask(context);
   }
   
   public void onScriptTask(CamundaContext context) throws Exception {
      Map<String, Object> state = new HashMap<String, Object>();
      MapModel model = new MapModel(state);
      ProcessAgent agent = new ProcessAgent(
            URI.create(URL), 
            "Camunda 2.0", 
            context.getProcessInstanceId(),
            "DEBUG");
      
      state.put("context", context);
      ProcessAgentService service = agent.start(ProcessMode.ATTACHED, model);
      service.createBreakpoint(RESOURCE, 1);
      service.execute(PROJECT, RESOURCE);
   }
   
   private static class CamundaContext {
      
      private final String processInstanceId;
      private final String processId;
      
      public CamundaContext(String processInstanceId, String processId) {
         this.processInstanceId = processInstanceId;
         this.processId = processId;
      }

      public String getProcessInstanceId() {
         return processInstanceId;
      }

      public String getProcessId() {
         return processId;
      }
      
   }
}
