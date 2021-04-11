# Mars Rover Exercise 

## Part 1: Basic Movement
1. The Mars Rover operates on a grid of arbitrary size.
2. You can only issue three commands: Move forward, rotate clockwise, and rotate
   anticlockwise.
3. If the rover moves off the grid, it reappears on the opposite side of the grid. 
   
## Part 2: Autopilot
1. Devise a simple process for determining the shortest possible path from one position
   on the grid to another.
2. Improve the solution so that it can avoid mountain ranges that occupy a number of
   inconvenient grid squares scattered around the map. 
   
## Part 3: Putting it all together
   Output all the instructions and moves carried out by the rover to get from one grid square to
   another. The output can take any form e.g rows of text, JSON data, or something graphical.
   

# Solution
In the solution, I assumed that the rover can rotate only by 90 degrees and so, I created a Direction ADT to implement that with North, South, East, West. Also, I assumed that the coordinates of the grid start from (0, 0) and end to (gridDimension - 1, gridDimension - 1).