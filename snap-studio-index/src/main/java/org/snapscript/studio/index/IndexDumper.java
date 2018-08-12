package org.snapscript.studio.index;

import java.util.Set;

public class IndexDumper {

   public static String dump(IndexNode node) throws Exception {
      StringBuilder builder = new StringBuilder();
      dump(node, builder, "");
      return builder.toString();
   }
   
   private static void dump(IndexNode node, StringBuilder builder, String indent) throws Exception {
      if(node != null) {
         Set<IndexNode> nodes = node.getNodes();
         IndexType type = node.getType();
         String name = node.getName();
         
         if(!type.isRoot()) {
            builder.append(indent);
            
            if(!type.isCompound()) {
               String description = type.getName();
               IndexNode constraint = node.getConstraint();

               builder.append(description);
               builder.append(" ");
               builder.append(name);

               if(constraint != null) {
                  String token = constraint.getName();

                  builder.append(": ");
                  builder.append(token);
               }
               builder.append(" ");
            }
            if(type.isLeaf()) {
               builder.append("\n");
            } else {
               builder.append("{\n");
            }
         }
         for(IndexNode entry : nodes) {
            if(type.isRoot()) {
               dump(entry, builder, "");
            } else {
               dump(entry, builder, indent + "   ");
            }
         }
         if(!type.isRoot() && !type.isLeaf()) {
            builder.append(indent);
            builder.append("}\n");
         }
      }
   }
}
