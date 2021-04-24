package marsrover

import cats.data._
import cats.effect.Sync
import marsrover.Direction._
import marsrover.Command._
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import cats.implicits._
import effects.MonadThrow

trait Rover[F[_]] {

  def autopilot(from: Position, to: Coordinate2D): F[List[Command]]

  def move(command: Command): StateT[F, Position, Command]

}

object LiveRover {

  def make[F[_]: Sync: SelfAwareStructuredLogger: MonadThrow](roverContext: RoverContext): F[LiveRover[F]] = {
    Sync[F]
      .delay {
        new LiveRover[F](roverContext)
      }
  }
}

final class LiveRover[F[_]: Sync: SelfAwareStructuredLogger: MonadThrow] private (roverContext: RoverContext)
    extends Rover[F] {

  override def autopilot(from: Position, to: Coordinate2D): F[List[Command]] = {

    Sync[F].delay(
      require(
        isInGrid(from.coordinate2D.x, roverContext.gridDimensions) &&
          isInGrid(from.coordinate2D.y, roverContext.gridDimensions) &&
          isInGrid(to.x, roverContext.gridDimensions) &&
          isInGrid(to.y, roverContext.gridDimensions)
      )
    ) *>
      Sync[F]
        .pure(
          getDirectionBaseOnCoordinateX(from, to.x) concat getDirectionBaseOnCoordinateY(
            from.coordinate2D.x,
            to.x,
            from.coordinate2D.y,
            to.y
          )
        )
        .onError(e => Logger[F].error(e)("Coordinates need to be inside the grid!"))
  }

  override def move(command: Command): StateT[F, Position, Command] = StateT { s =>
    command match {
      case Command.Forward =>
        s.direction match {
          case Direction.North =>
            Logger[F].info(s.direction.toString) *>
              Sync[F].pure(
                s.copy(coordinate2D =
                  Coordinate2D(s.coordinate2D.x, Math.floorMod(s.coordinate2D.y + 1, roverContext.gridDimensions))
                ),
                command
              )

          case Direction.South =>
            Logger[F].info(s.direction.toString) *>
              Sync[F].pure(
                s.copy(coordinate2D =
                  Coordinate2D(s.coordinate2D.x, Math.floorMod(s.coordinate2D.y - 1, roverContext.gridDimensions))
                ),
                command
              )

          case Direction.East =>
            Logger[F].info(s.direction.toString) *>
              Sync[F].pure(
                s.copy(coordinate2D =
                  Coordinate2D(Math.floorMod(s.coordinate2D.x + 1, roverContext.gridDimensions), s.coordinate2D.y)
                ),
                command
              )

          case Direction.West =>
            Logger[F].info(s.direction.toString) *>
              Sync[F].pure(
                s.copy(coordinate2D =
                  Coordinate2D(Math.floorMod(s.coordinate2D.x - 1, roverContext.gridDimensions), s.coordinate2D.y)
                ),
                command
              )

        }
      case _ =>
        Logger[F].info(s.direction.toString) *> Sync[F]
          .pure(s.copy(direction = s.direction.changeDirectionBasedOn(command)), command)
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

  private def isInGrid(coordinate: Int, gridDimension: Int): Boolean =
    coordinate >= 0 && coordinate < gridDimension

  private def isThereAMountain(x: Int, y: Int): Boolean = roverContext.mountainsOnGrid.contains(Coordinate2D(x, y))

}
