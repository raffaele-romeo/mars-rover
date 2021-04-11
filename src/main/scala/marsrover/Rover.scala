package marsrover

import cats.data._
import marsrover.Direction._
import marsrover.Command._

trait Rover {
  def autopilot(from: Position, to: Coordinate2D): List[Command]

  def move(command: Command): State[Position, Command]

}

final class LiveRover(roverContext: RoverContext) extends Rover {

  override def autopilot(from: Position, to: Coordinate2D): List[Command] = {

    require(from.coordinate2D.x >= 0 && from.coordinate2D.x < roverContext.gridDimensions)
    require(from.coordinate2D.y >= 0 && from.coordinate2D.y < roverContext.gridDimensions)
    require(to.x >= 0 && to.x < roverContext.gridDimensions)
    require(to.y >= 0 && to.y < roverContext.gridDimensions)

    getDirectionBaseOnCoordinateX(from, to.x) concat getDirectionBaseOnCoordinateY(from.coordinate2D.x, to.x, from.coordinate2D.y, to.y)
  }

  override def move(command: Command): State[Position, Command] = State { s =>
    command match {
      case Command.Forward => s.direction match {

        case Direction.North =>
          if (s.coordinate2D.y + 1 < roverContext.gridDimensions)
            (s.copy(coordinate2D = Coordinate2D(s.coordinate2D.x, s.coordinate2D.y + 1)), command)
          else
            (s.copy(coordinate2D = Coordinate2D(s.coordinate2D.x, 0)), command)

        case Direction.South =>
          if (s.coordinate2D.y - 1 >= 0)
            (s.copy(coordinate2D = Coordinate2D(s.coordinate2D.x, s.coordinate2D.y - 1)), command)
          else
            (s.copy(coordinate2D = Coordinate2D(s.coordinate2D.x, roverContext.gridDimensions - 1)), command)

        case Direction.East =>
          if (s.coordinate2D.x + 1 < roverContext.gridDimensions)
            (s.copy(coordinate2D = Coordinate2D(s.coordinate2D.x + 1, s.coordinate2D.y)), command)
          else
            (s.copy(coordinate2D = Coordinate2D(0, s.coordinate2D.y)), command)

        case Direction.West =>
          if (s.coordinate2D.x - 1 >= 0)
            (s.copy(coordinate2D = Coordinate2D(s.coordinate2D.x - 1, s.coordinate2D.y)), command)
          else
            (s.copy(coordinate2D = Coordinate2D(roverContext.gridDimensions - 1, s.coordinate2D.y)), command)

      }
      case _ => (s.copy(direction = s.direction.changeDirectionBasedOn(command)), command)
    }
  }

  private def getDirectionBaseOnCoordinateX(from: Position, toX: Int): List[Command] = {
    val fromX = from.coordinate2D.x

    if (toX < fromX)
      from.direction.getCommandsToMoveTo(West).concat(getMoveForwards(fromX, toX))
    else
      from.direction.getCommandsToMoveTo(East).concat(getMoveForwards(toX, fromX))
  }

  private def getDirectionBaseOnCoordinateY(fromX: Int, toX: Int, fromY: Int, toY: Int): List[Command] = {
    val initDirection = if (toX < fromX) West else East

    if (toY > fromY)
      initDirection.getCommandsToMoveTo(North).concat(getMoveForwards(toY, fromY))
    else if (toY < fromY)
      initDirection.getCommandsToMoveTo(South).concat(getMoveForwards(toY, fromY))
    else List.empty
  }

  private def getMoveForwards(x: Int, y: Int): List[Command] = List.fill(Math.abs(x - y))(Forward)

  private def isThereAMountain(x: Int, y: Int): Boolean = roverContext.mountainsOnGrid.contains(Coordinate2D(x, y))

}
