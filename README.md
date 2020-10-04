# NQueens: A Java N Queens Solver

This is an N Queens problem solver; it finds a solution for the N Queens problem for a given N, if possible. Not all values of N have solutions (N = 2, for example), while any value of N that has a solution has multiple solutions; through symmetry if not otherwise. This solver returns the first solution it finds.

This is a fairly standard backtracking solver. Since there can only be one queen per row, we recurse over rows, and for each row we iterate over possible queen positons. A bitmap is used to keep track of which grid cells are available for queen placement and which have been interdicted by queens already placed. The bitmap is copied on recursion so markup is easy to unwind while backtracking. Set bits are positions where a queen might be placed, while unset bits are positions that are interdicted by earlier queens.

An additional constraint (3 queens may not be in a direct line) is enforced alongside the standard constraints, if requested.

This solver has been written for correctness and readability; it could be optimized significantly (by moving to a true bitmap rather than a boolean array, for instance, or bringing in monte carlo techniques to increase the likelihood of hitting a soluton earlier), but readability would suffer.


