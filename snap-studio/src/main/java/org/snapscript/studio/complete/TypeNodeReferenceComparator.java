package org.snapscript.studio.complete;

import java.util.Comparator;

public class TypeNodeReferenceComparator implements Comparator<TypeNodeReference> {

   @Override
   public int compare(TypeNodeReference left, TypeNodeReference right) {
      String leftName = left.getName();
      String rightName = right.getName();
      int comparison = leftName.compareTo(rightName);
      
      if(comparison == 0) {
         String leftPath = left.getResource();
         String rightPath = right.getResource();
         
         return leftPath.compareTo(rightPath);
      }
      return comparison;
   }

}
