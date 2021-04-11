package marsrover

import marsrover.Direction._
import java.io._
import cats.implicits._

object Main {

  def main(args: Array[String]): Unit = {

    val roverContext = RoverContext(20, List.empty)

    val rover = new LiveRover(roverContext)

    // Write instructions to go from point A to B
    val commands = rover.autopilot(Position(Coordinate2D(17, 16), South), Coordinate2D(5, 19))
    writeFile("output/instructions.txt", commands.map(_.toString))

    // Get destination point given a list of instructions
    val initialPosition = Position(Coordinate2D(0, 0), South)
    val chainCommands = commands.traverse(rover.move)
    val output = chainCommands.run(initialPosition).value

    writeFile("output/destinationPoint.txt",
      Seq(s"Destination point is: ${(output._1.coordinate2D.x, output._1.coordinate2D.y)}"))
  }

  def writeFile(filename: String, lines: Seq[String]): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines) {
      bw.write(line + "\n")
    }
    bw.close()
  }

}
