package org.snapscript.develop.camunda;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.agent.ProcessAgent;
import org.snapscript.agent.ProcessAgentService;
import org.snapscript.agent.ProcessMode;
import org.snapscript.core.MapModel;

public class CamundaScriptTask {
   
   private static final String RESOURCE = "/task.snap";
   private static final String PROJECT = "camunda";
   private static final String URL = "http://%s:4457/resource/";

   public static void main(String[] list) throws Exception {
      CamundaScriptTask task = new CamundaScriptTask();
      DelegateExecution context = new DelegateExecution("ERTF-DBRE-HERH-ERYE", "rfq.bpmn");
      
      task.onScriptTask(context);
      Thread.sleep(100000000);
   }
   
   public void onScriptTask(DelegateExecution execution) throws Exception {
      String address = InetAddress.getLocalHost().getHostAddress();
      Map<String, Object> state = new HashMap<String, Object>();
      MapModel model = new MapModel(state);
      ProcessAgent agent = new ProcessAgent(
            ProcessMode.TASK,
            URI.create(String.format(URL, address)),
            "Camunda 2.0", 
            execution.getProcessInstanceId(),
            "DEBUG");
      
      state.put("execution", execution);
      ProcessAgentService service = agent.start(model);

      createBreakpoints(service);
      service.execute(PROJECT, RESOURCE);
      service.join(6000000); // wait for script to finish
   }

   public void createBreakpoints(ProcessAgentService service) {
      String source = service.loadScript(PROJECT, RESOURCE);
      Pattern pattern = Pattern.compile(".*\\/\\/\\s*suspend.*");
      String[] list = source.split("\\r?\\n");

      for(int i = 0; i < list.length; i++) {
         String line = list[i];
         Matcher matcher = pattern.matcher(line);

         if(matcher.matches()) {
            service.createBreakpoint(RESOURCE, i+1);
         }
      }
   }

   private static class DelegateExecution {
      
      private final String processInstanceId;
      private final String processId;
      
      public DelegateExecution(String processInstanceId, String processId) {
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
