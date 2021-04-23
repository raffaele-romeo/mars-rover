package marsrover

import cats.data._
import marsrover.Direction._
import marsrover.Command._
import cats.effect.IO

trait Rover[F[_]] {

  def autopilot(from: Position, to: Coordinate2D): F[List[Command]]

  def move(command: Command): StateT[F, Position, Command]

}

final class LiveRover(roverContext: RoverContext) extends Rover[IO] {

  override def autopilot(from: Position, to: Coordinate2D): IO[List[Command]] = {
    IO.pure(
      getDirectionBaseOnCoordinateX(from, to.x) concat getDirectionBaseOnCoordinateY(
        from.coordinate2D.x,
        to.x,
        from.coordinate2D.y,
        to.y
      )
    )
  }

  override def move(command: Command): StateT[IO, Position, Command] = StateT { s =>
    command match {
      case Command.Forward =>
        s.direction match {
          case Direction.North =>
            IO.println(s.direction) *>
              IO(
                s.copy(coordinate2D =
                  Coordinate2D(s.coordinate2D.x, Math.floorMod(s.coordinate2D.y + 1, roverContext.gridDimensions))
                ),
                command
              )

          case Direction.South =>
            IO.println(s.direction) *>
              IO.pure(
                s.copy(coordinate2D =
                  Coordinate2D(s.coordinate2D.x, Math.floorMod(s.coordinate2D.y - 1, roverContext.gridDimensions))
                ),
                command
              )

          case Direction.East =>
            IO.println(s.direction) *>
              IO(
                s.copy(coordinate2D =
                  Coordinate2D(Math.floorMod(s.coordinate2D.x + 1, roverContext.gridDimensions), s.coordinate2D.y)
                ),
                command
              )

          case Direction.West =>
            IO.println(s.direction) *>
              IO.pure(
                s.copy(coordinate2D =
                  Coordinate2D(Math.floorMod(s.coordinate2D.x - 1, roverContext.gridDimensions), s.coordinate2D.y)
                ),
                command
              )

        }
      case _ =>
        IO.println(s.direction) *> IO(s.copy(direction = s.direction.changeDirectionBasedOn(command)), command)
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
