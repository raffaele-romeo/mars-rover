package marsrover

import marsrover.Direction.South
import marsrover.Direction._
import marsrover.Command._
import cats.implicits._
import cats.effect.IO
import munit.CatsEffectSuite
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger

class RoverTest extends CatsEffectSuite {

  val roverContext: RoverContext = RoverContext(3, List.empty)

  implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = NoOpLogger.apply[IO]

  val rover: IO[LiveRover[IO]] = LiveRover.make[IO](roverContext)

  test("Rover should be able to move forward within the Grid") {

    val initialPosition = Position(Coordinate2D(1, 1), South)
    val finalPosition = Position(Coordinate2D(1, 0), South)

    rover.flatMap(_.move(Forward).run(initialPosition) flatMap (result => IO(assertEquals(result._1, finalPosition))))
  }

  test("it should be able to rotate clockwise") {

    val initialPosition = Position(Coordinate2D(1, 1), South)
    val finalPosition = Position(Coordinate2D(1, 1), West)

    rover.flatMap(
      _.move(RotateClockwise).run(initialPosition) flatMap (result => IO(assertEquals(result._1, finalPosition)))
    )
  }

  test("it should be able to rotate anticlockwise") {

    val initialPosition = Position(Coordinate2D(1, 1), South)
    val finalPosition = Position(Coordinate2D(1, 1), East)

    rover.flatMap(
      _.move(RotateAnticlockwise).run(initialPosition) flatMap (result => IO(assertEquals(result._1, finalPosition)))
    )
  }

  test("it should reappears on the opposite side of the grid if the rover moves off the grid") {
    val initialPosition = Position(Coordinate2D(2, 2), North)
    val finalPosition = Position(Coordinate2D(2, 0), North)

    rover.flatMap(_.move(Forward).run(initialPosition) flatMap (result => IO(assertEquals(result._1, finalPosition))))
  }

  test("it should reappears on the opposite side of the grid if the rover moves off the grid test 2") {
    val initialPosition = Position(Coordinate2D(2, 0), South)
    val finalPosition = Position(Coordinate2D(2, 2), South)

    rover.flatMap(_.move(Forward).run(initialPosition) flatMap (result => IO(assertEquals(result._1, finalPosition))))
  }

  test("it should get instructions from initial position to final position") {
    val initialPosition = Position(Coordinate2D(2, 2), North)
    val finalPosition = Coordinate2D(2, 0)

    val expectedOutput = List(RotateClockwise, RotateClockwise, Forward, Forward)

    rover.flatMap(
      _.autopilot(initialPosition, finalPosition) flatMap (result => IO(assertEquals(result, expectedOutput)))
    )
  }

  test("it should throw IllegalArgumentException if coordinate are out of the grid") {
    val initialPosition = Position(Coordinate2D(3, 2), North)
    val finalPosition = Coordinate2D(2, 0)

    intercept[IllegalArgumentException] {
      rover.flatMap(_.autopilot(initialPosition, finalPosition)).unsafeRunSync()
    }

  }

  test("it should be able to perform multiple movements") {
    val initialPosition = Position(Coordinate2D(2, 2), North)
    val finalPosition = Position(Coordinate2D(2, 2), West)

    val commands = List(Forward, RotateAnticlockwise, RotateAnticlockwise, Forward, RotateClockwise)

    for {
      rover_ <- rover
      output <- commands.traverse(rover_.move).run(initialPosition)
    } yield assertEquals(output._1, finalPosition)

  }
}
