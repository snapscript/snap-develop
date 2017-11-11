package org.snapscript.index;

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
      int leftLine = left.getIndex().getLine();
      int rightLine = right.getIndex().getLine();
      
      if(reverse) {
         return Integer.compare(rightLine, leftLine);
      }
      return Integer.compare(leftLine, rightLine);
   }

}
