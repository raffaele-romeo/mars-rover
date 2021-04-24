package marsrover

import marsrover.Direction._
import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    val roverContext = RoverContext(20, List.empty)

    val liveRover = LiveRover.make[IO](roverContext)

    (for {
      rover <- liveRover
      commands <- rover.autopilot(Position(Coordinate2D(17, 17), South), Coordinate2D(5, 19))
      output <- commands.traverse(rover.move).run(Position(Coordinate2D(17, 17), South))
    } yield output._1).flatMap(IO.println).as(ExitCode.Success)

  }

}
