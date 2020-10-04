// N Queens Solver -> App
//
//    This is the app wrapper for the N Queens solver.  It parses the command
// line, invokes the solver and prints the results.  See `NQueensBoard.java`
// for the actual solver.

package NQueens;

public class App
{
   /**
    * Print a complaint and end execution.
    *
    * @param msg   the message to print
    */

   public static void die(String msg)
   {
      System.out.print(msg);
      System.exit(1);
   }

   /**
    * Describe how to invoke the program, then exit.
    */

   public static void dieWithHelpText()
   {
      die("Usage: app [args] queens\n  args:\n    no-line-constraint - Don't enforce 3-queens line constaint.\n");
   }

   /**
    * Convert a command line argument to an integer, or die trying.
    *
    * If conversion fails, this terminates the program.
    *
    * @param arg   the argument to convert to an integer
    * @return      `arg` converted to an integer
    */

   static int strToInt(String arg)
   {
      int count = 0;

      try
         {
            count = Integer.parseInt(arg);
         }
      catch(NumberFormatException e)
         {
            die(String.format("Unable to convert argument '%s' to a number.\n", arg));
         }

      return count;
   }

   /**
    * Program entry point.
    *
    * @param args  command line argument array
    */

   public static void main(String[] args)
   {
      boolean      lineConstraint = true; // Line constraint defaults on.
      int          queens         = 8;    // Arbitrary default value.
      NQueensBoard board;

      // Parse Arguments
      //    We only take two arguments, so the parser isn't particularly
      // robust.  If we were to add more arguments it it would be worth
      // pulling in or writing a more capable command line parser.

      switch(args.length)
         {
         case 0:
            System.out.printf("Assuming %d Queens -- rerun with an integer argument to specify.\n", queens);
            break;

         case 1:
            if(args[0].equals("no-line-constraint"))
               {
                  lineConstraint = false;
               }
            else
               {
                  queens = strToInt(args[0]);
               }
            break;

         case 2:
            if(args[0].equals("no-line-constraint"))
               {
                  lineConstraint = false;
               }
            else
               {
                  dieWithHelpText();
               }

            queens = strToInt(args[1]);
            break;

         default:
            dieWithHelpText();
            break;
         }

      // Solve.

      board = new NQueensBoard(queens, lineConstraint);

      if(board.solve())
         {
            board.print();
         }
      else
         {
            System.out.printf("No solution found for %d queens.\n", queens);
         }
   }
}

//
// Copyright © Todd Showalter, 2020.
//
