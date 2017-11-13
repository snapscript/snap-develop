package org.snapscript.studio.index;

import java.util.Comparator;

public class IndexNodeComparator implements Comparator<IndexNode> {

   private final boolean reverse;
   
   public IndexNodeComparator() {
      this(false);
   }
   
   public IndexNodeComparator(boolean reverse) {
      this.reverse = reverse;
   }
   
   @Override
   public int compare(IndexNode left, IndexNode right) {
      int leftLine = left.getLine();
      int rightLine = right.getLine();
      
      if(reverse) {
         return Integer.compare(rightLine, leftLine);
      }
      return Integer.compare(leftLine, rightLine);
   }

}
