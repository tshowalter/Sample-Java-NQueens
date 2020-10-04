// N Queens Solver -> Tests
//
// Unit tests

package NQueens;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest
{
   /**
    * Ensure we get an exception if we try to get results without solving.
    */

   @Test public void testNoResultsIfNotSolved()
   {
      NQueensBoard testBoard = new NQueensBoard(8, false);

      try
         {
            testBoard.queenPositions();
            fail("Expected IllegalStateException reading result from unsolved board.\n");
         }
      catch(IllegalStateException e)
         {
            // pass.
         }
   }

   /**
    * Ensure we get an exception if we try to get results after a failed solve.
    */

   @Test public void testNoResultsIfNotSolvable()
   {
      NQueensBoard testBoard = new NQueensBoard(2, false);

      testBoard.solve();

      try
         {
            testBoard.queenPositions();
            fail("Expected IllegalStateException reading result from unsolved board.\n");
         }
      catch(IllegalStateException e)
         {
            // pass.
         }
   }

   /**
    * Check that there is a solution for N=1.
    */

   @Test public void testSolutionFor1Queen()
   {
      NQueensBoard testBoard = new NQueensBoard(1, false);

      if(!testBoard.solve())
         {
            fail("Expected a solution for 1 queen.");
         }
   }

   /**
    * Check that there's no solution for N=6 with the line constraint.
    */

   @Test public void testNoSolutionFor6QueensWithLineConstraint()
   {
      NQueensBoard testBoard = new NQueensBoard(6, true);

      if(testBoard.solve())
         {
            fail("Expected no solution for 6 queens with the line constraint active.");
         }

      if(testBoard.validate())
         {
            fail("Validation succeeded on failed 6 queens solution attempt.\n");
         }
   }

   /**
    * Check that there is a solution for N=6 without the line constraint.
    */

   @Test public void testSolutionFor6QueensWithoutLineConstraint()
   {
      NQueensBoard testBoard = new NQueensBoard(6, false);

      if(!testBoard.solve())
         {
            fail("Expected a solution for 6 queens with the line constraint active.");
         }

      if(!testBoard.validate())
         {
            fail("Validation failed on 6 queens solution.\n");
         }
   }

   /**
    * Check that the N=8 solution validates without the line constraint.
    */

   @Test public void testSolutionFor8QueensWithoutLineConstraint()
   {
      NQueensBoard testBoard = new NQueensBoard(8, false);

      if(!testBoard.solve())
         {
            fail("Expected a solution for 8 queens.");
         }

      if(!testBoard.validate())
         {
            fail("Validation failed on 8 queen solution.\n");
         }
   }

   /**
    * Check that the N=8 solution validates with the line constraint.
    */

   @Test public void testSolutionFor8QueensWithLineConstraint()
   {
      NQueensBoard testBoard = new NQueensBoard(8, true);

      if(!testBoard.solve())
         {
            fail("Expected a solution for 8 queens.");
         }

      if(!testBoard.validate())
         {
            fail("Validation failed on 8 queen solution.\n");
         }
   }
}

//
// Copyright © Todd Showalter, 2020.
//
