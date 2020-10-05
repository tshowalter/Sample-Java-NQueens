// N Queens Solver -> Solver
//
//    Find a solution for the N Queens problem for a given N, if possible.  Not
// all values of N have solutions (N = 2, for example), while any value of N
// that has a solution has multiple solutions; through symmetry if not
// otherwise.  This solver returns the first solution it finds.
//
//    This is a fairly standard backtracking solver.  Since there can only be
// one queen per row, we recurse over rows, and for each row we iterate over
// possible queen positons.  A bitmap is used to keep track of which grid cells
// are available for queen placement and which have been interdicted by queens
// already placed.  The bitmap is copied on recursion so markup is easy to
// unwind while backtracking.  `true` bits are positions where a queen might
// be placed, while `false` bits are positions that are interdicted by earlier
// queens.
//
//    The additional constraint (3 queens may not be in a direct line) is
// enforced alongside the standard constraints; all of the attack moves are
// treated as vectors and marched across the bitmap board marking up cells. The
// line constraint is simply another vector march for each pair of queens
// placed; during recursion this means at each level we only need to consider
// the current row's queen paired with each previous row's queens.
//
//    Note that the bitmap is only accurate for the current row down to the end
// of the board at any given level of recursion; we don't attempt to push
// constraints back up into the parts of the board that have queens already
// placed.  Back-propagating the constraints would allow for printing
// incremental results, but would slow down the solve significantly.
//
//    Whether the initial constraint is active or not is controlled by the
// `lines` argument to the constructor.  If the argument is `true`, the
// constraint is enforced.
//
//    This solver has been written for correctness and readability; it could be
// optimized significantly (by moving to a true bitmap rather than a boolean
// array, for instance, or bringing in monte carlo techniques to increase the
// likelihood of hitting a soluton earlier), but readability would suffer.

package NQueens;

import java.util.Arrays; // For fill()

public class NQueensBoard
{
   int[]     Queens;         // The index of the queen's position in each row.
   boolean[] Board;          // The chessboard.
   int       BoardWidth;     // Width of the board (and height, queen count...)
   boolean   LineConstraint; // Whether to enforce the 3 queen line constraint.
   boolean   Solved;         // Has this board been solved?
   boolean   Attempted;      // Have we attempted to solve it?

   /**
    * Construct an N Queens solver.
    *
    * @param num     the number of queens and board size for which to solve
    * @param lines   require that no 3 queens lie along a straight line
    */

   NQueensBoard(int num, boolean lines)
   {
      BoardWidth     = num;
      Queens         = new int[BoardWidth];
      Board          = new boolean[num * num];
      LineConstraint = lines;
      Solved         = false;
      Attempted      = false;

      //   The board is a bitmap; cells where a queen may be placed are `true`,
      // while places where a queen cannot be placed are `false`.  If this was
      // to be a high performance solver, I'd probably use an actual bitmap
      // built from `unsigned long` or the like, to pack the memory in tighter
      // both to make better use of cache and to facilitate solving larger
      // boards.

      Arrays.fill(Board,  true);
      Arrays.fill(Queens, 0);
   }

   /**
    * Mark a single cell as not containing a queen.
    *
    * Mark a cell as interdicted.  This returns `false` if the location to be
    * set lies outside the board, so that `setVec()` has a termination
    * condition.
    *
    * @param board  the bitmap in which to set the value
    * @param x      the x position of the cell to be set
    * @param y      the y position of the cell to be set
    * @return       true if the given position was on the board, false otherwise
    */

   boolean set(boolean[] board, int x, int y)
   {
      if((x < 0) || (x >= BoardWidth) ||
         (y < 0) || (y >= BoardWidth))
         {
            return false; // Looks like we walked off the board somehow.
         }

      board[x + (BoardWidth * y)] = false;
      return true;
   }

   /**
    * Mark a linear sequence of cells as not containing a queen.
    *
    * This takes a start position and a vector, and marches across the board
    * setting cells as interdicted until an edge of the board is crossed.
    *
    * @param board   the bitmap in which to set the value
    * @param x       the initial x position on the board
    * @param y       the initial y position on the board
    * @param vx      the x component of the vector
    * @param vy      the y component of the vector
    */

   void setVec(boolean[] board, int x, int y, int vx, int vy)
   {
      do // Skip the initial position.
         {
            x += vx;
            y += vy;
         } while(set(board, x, y));
   }

   /**
    * Calculate the GCD of a pair of integers.
    *
    * Use Euclid's algorithm to calculate the GCD of two integers.  Inputs are
    * assumed to be positive; this counts on the wrapper function `gcd()` to
    * massage the values.
    *
    * @param a  one of the integer pair for which the gcd is desired
    * @param b  the other of the integer pair for which the gcd is desired
    * @return   the GCD of `a` and `b`
    */

   int gcd_r(int a, int b)
   {
      return (b != 0) ? gcd(b, a % b) : a;
   }

   /**
    * Calculate the GCD of a pair of integers.
    *
    * This wraps the recursive `gcd_r()` function which requires positive
    * integers.
    *
    * @param a  one of the integer pair for which the gcd is desired
    * @param b  the other of the integer pair for which the gcd is desired
    * @return   the GCD of `a` and `b`
    */

   int gcd(int a, int b)
   {
      return gcd_r(Math.abs(a), Math.abs(b));
   }

   /**
    * Mark up the board for a given row.
    *
    * @param board   the board to mark up
    * @param row     the row containing the queen used for markup
    */

   void markBoard(boolean[] board, int row)
   {
      int x = Queens[row];
      int y = row;

      //    March down the board on the column and diagonals, marking the
      // positions interdicted (false).

      setVec(board, x, y,  0,  1); // Down
      setVec(board, x, y, -1,  1); // Down & Left Diagonal
      setVec(board, x, y,  1,  1); // Down & Right Diagonal

      //    For each queen in rows prior to this one, we calculate a vector
      // from that queen to the current one.  We then divide the components
      // by the gcd of the two, since (4, 6) for example is on the same line
      // as (2, 3), and we want to hit every cell on the line.  We then march
      // that vector across the board, marking cells interdicted (false).

      if(LineConstraint)
         {
            for(int q = 0; q < row; q++)
               {
                  int vx = Queens[row] - Queens[q];
                  int vy = row - q;
                  int d  = gcd(vx, vy);

                  vx /= d;
                  vy /= d;

                  setVec(board, x, y, vx, vy);
               }
         }
   }

   /**
    * Solve a row of the board, then recurse.
    *
    * March through the available options for queen positions within a row,
    * trying each and recursing until we find a solution to the board or run
    * out of possibilities.
    *
    * @param row    the row over which to iterate
    * @param board  the board on which to operate
    */

   boolean solveRest(int row, boolean[] board)
   {
      if(row >= BoardWidth)
         {
            //    We're off the board, and everything fit.  This is the point
            // where we know we've found a valid solution.

            return true;
         }

      //    Currently this iterates linearly across the cells in the row.  This
      // is correct, in that it will hit all possibilities, but it may not be
      // optimal; starting on a random cell and iterating with wrap, for
      // instance, or using a pseudorandom sequence that visits all the cells in
      // an alternate order would tend to reduce the initial set of failed
      // attempts that result when every level of iteration starts at the same
      // cell.

      for(int x = 0; x < BoardWidth; x++)
         {
            if(board[(row * BoardWidth) + x]) // Can a queen go here?
               {
                  //    We could potentially clear the current board instead of
                  // cloning on the second and subsequent iterations of this
                  // loop, but we'd need to regenerate all the markup from
                  // earlier steps.  If we were trying to solve very large
                  // boards, it might be worth doing this for the memory
                  // savings.  As it is, it's probably cheaper to re-clone, and
                  // far more readable.

                  boolean[] b = board.clone();
                  Queens[row] = x;

                  markBoard(b, row);

                  if(solveRest(row + 1, b))
                     {
                        return true; // We must have a valid soluton...
                     }
               }
         }

      return false;
   }

   /**
    * Find a solution to the N Queens problem for the given size of board.
    *
    * @return   `true` if a solution was found, `false` otherwise
    */

   public boolean solve()
   {
      Attempted = true;
      Solved    = solveRest(0, Board);

      return Solved;
   }

   /**
    * Print the board to stdout.
    */

   public void print()
   {
      for(int y = 0; y < BoardWidth; y++)
         {
            System.out.printf("%3d ", Queens[y]);

            for(int x = 0; x < BoardWidth; x++)
               {
                  System.out.print(Queens[y] == x ? "@ " : ". ");
               }

            System.out.print("\n");
         }
   }

   /**
    * Get the list of queens from the solution.
    */

   public int[] queenPositions()
   {
      if(!Attempted)
         {
            throw new IllegalStateException("Board not solved yet.");
         }

      if(!Solved)
         {
            throw new IllegalStateException("No solution found for board.");
         }

      return Queens;
   }

   /**
    * Validate the solved board.
    *
    * @return `true` if validation succeeds, `false` otherwise.
    */

   public boolean validate()
   {
      if(!Attempted || !Solved)
         {
            return false;
         }

      // Check whether the queens are contained within the board boundaries.

      for(int q = 0; q < BoardWidth; q++)
         {
            if(Queens[q] < 0 || Queens[q] >= BoardWidth)
               {
                  return false;
               }
         }

      // Make and clear a board.

      boolean[] board = new boolean[BoardWidth * BoardWidth];

      Arrays.fill(board, true);

      //    For each queen, clear all attack lanes.  Note that setVec() doesn't
      // clear the first cell, so the queen's cell will be left untouched.

      for(int q = 0; q < BoardWidth; q++)
         {
            int x = Queens[q];
            int y = q;

            setVec(board, x, y,  1,  0); // Right
            setVec(board, x, y,  1, -1); // Right & Up
            setVec(board, x, y,  0, -1); // Up
            setVec(board, x, y, -1, -1); // Left & Up
            setVec(board, x, y, -1,  0); // Left
            setVec(board, x, y, -1,  1); // Left & Down
            setVec(board, x, y,  0,  1); // Down
            setVec(board, x, y,  1,  1); // Right & Down
         }

      //    For each pair of queens, clear all lines.  The logic here is similar
      // to the logic enforcing the 3 queens constraint in markBoard(), but we
      // need to iterate across all pairs, and fire the vector marches off in
      // both directions, outwards from the two queens.

      if(LineConstraint)
         {
            for(int qa = 0; qa < BoardWidth; qa++)
               {
                  int xa = Queens[qa];
                  int ya = qa;

                  for(int qb = qa + 1; qb < BoardWidth; qb++)
                     {
                        int xb = Queens[qb];
                        int yb = qb;
                        int vx = xa - xb;
                        int vy = ya - yb;
                        int d  = gcd(vx, vy);

                        vx /= d;
                        vy /= d;

                        setVec(board, xa, ya,  vx,  vy);
                        setVec(board, xb, yb, -vx, -vy);
                     }
               }
         }

      //    Make sure each queen's cell is not interdicted.  At this point, all
      // of the interdicted cells should be marked, and if the board is a valid
      // solution, the queen cells should still be uninterdicted.

      for(int q = 0; q < BoardWidth; q++)
         {
            if(!board[Queens[q] + (BoardWidth * q)])
               {
                  return false;
               }
         }

      // It's good.

      return true;
   }
}

//
// Copyright © Todd Showalter, 2020.
//
