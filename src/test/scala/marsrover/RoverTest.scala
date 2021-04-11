package marsrover

import marsrover.Direction.South
import org.scalatest.flatspec.AnyFlatSpec
import marsrover.Direction._
import marsrover.Command._
import cats.implicits._

class RoverTest extends AnyFlatSpec {

  val roverContext: RoverContext = RoverContext(3, List.empty)

  val rover = new LiveRover(roverContext)

  "Rover" should "be able to move forward within the Grid" in {

    val initialPosition = Position(Coordinate2D(1, 1), South)
    val finalPosition = Position(Coordinate2D(1, 0), South)

    assert(rover.move(Forward).run(initialPosition).value._1 === finalPosition)
  }

  it should "be able to rotate clockwise" in {

    val initialPosition = Position(Coordinate2D(1, 1), South)
    val finalPosition = Position(Coordinate2D(1, 1), West)

    assert(rover.move(RotateClockwise).run(initialPosition).value._1 === finalPosition)
  }

  it should "be able to rotate anticlockwise" in {

    val initialPosition = Position(Coordinate2D(1, 1), South)
    val finalPosition = Position(Coordinate2D(1, 1), East)

    assert(rover.move(RotateAnticlockwise).run(initialPosition).value._1 === finalPosition)
  }

  it should "reappears on the opposite side of the grid if the rover moves off the grid" in {
    val initialPosition = Position(Coordinate2D(2, 2), North)
    val finalPosition = Position(Coordinate2D(2, 0), North)

    assert(rover.move(Forward).run(initialPosition).value._1 === finalPosition)
  }

  it should "get instructions from initial position to final position" in {
    val initialPosition = Position(Coordinate2D(2, 2), North)
    val finalPosition = Coordinate2D(2, 0)

    val expectedOutput = List(RotateClockwise, RotateClockwise, Forward, Forward)

    assert (rover.autopilot(initialPosition, finalPosition) === expectedOutput)
  }

  it should "throw IllegalArgumentException if coordinate are out of the grid" in {
    val initialPosition = Position(Coordinate2D(3, 2), North)
    val finalPosition = Coordinate2D(2, 0)

    assertThrows[IllegalArgumentException] {
      rover.autopilot(initialPosition, finalPosition)
    }
  }

  it should "be able to perform multiple movements" in {
    val initialPosition = Position(Coordinate2D(2, 2), North)
    val finalPosition = Position(Coordinate2D(2, 2), West)

    val commands = List(Forward, RotateAnticlockwise, RotateAnticlockwise, Forward, RotateClockwise)

    val chainCommands = commands.traverse(rover.move)
    val output = chainCommands.run(initialPosition).value

    assert(output._1 === finalPosition)
  }
}
