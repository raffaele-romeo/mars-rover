package marsrover

import marsrover.Direction._
import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val roverContext = RoverContext(20, List.empty)

    val rover = new LiveRover(roverContext)

    for {
      commands <- rover.autopilot(Position(Coordinate2D(17, 16), South), Coordinate2D(5, 19))
      _ <- commands.traverse(rover.move).run(Position(Coordinate2D(0, 0), South))
    } yield ExitCode.Success

  }

}
