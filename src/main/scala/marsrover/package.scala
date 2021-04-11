import marsrover.Command._

package object marsrover {

  sealed trait Command extends Product

  object Command {

    final case object Forward extends Command

    final case object RotateClockwise extends Command

    final case object RotateAnticlockwise extends Command

  }

  sealed trait Direction extends Product {
    def getCommandsToMoveTo(direction: Direction): List[Command]

    def changeDirectionBasedOn(command: Command): Direction
  }

  object Direction {

    final case object North extends Direction {
      override def getCommandsToMoveTo(direction: Direction): List[Command] = direction match {
        case North => List.empty
        case South => List(RotateClockwise, RotateClockwise)
        case East => List(RotateClockwise)
        case West => List(RotateAnticlockwise)
      }

      override def changeDirectionBasedOn(command: Command): Direction = command match {
        case Forward => North
        case RotateClockwise => East
        case RotateAnticlockwise => West
      }
    }

    final case object South extends Direction {
      override def getCommandsToMoveTo(direction: Direction): List[Command] = direction match {
        case North => List(RotateClockwise, RotateClockwise)
        case South => List.empty
        case East => List(RotateAnticlockwise)
        case West => List(RotateClockwise)
      }

      override def changeDirectionBasedOn(command: Command): Direction = command match {
        case Forward => South
        case RotateClockwise => West
        case RotateAnticlockwise => East
      }
    }

    final case object East extends Direction {
      override def getCommandsToMoveTo(direction: Direction): List[Command] = direction match {
        case North => List(RotateAnticlockwise)
        case South => List(RotateClockwise)
        case East => List.empty
        case West => List(RotateClockwise, RotateClockwise)
      }

      override def changeDirectionBasedOn(command: Command): Direction = command match {
        case Forward => East
        case RotateClockwise => South
        case RotateAnticlockwise => North
      }
    }

    final case object West extends Direction {
      override def getCommandsToMoveTo(direction: Direction): List[Command] = direction match {
        case North => List(RotateClockwise)
        case South => List(RotateAnticlockwise)
        case East => List(RotateClockwise, RotateClockwise)
        case West => List.empty
      }

      override def changeDirectionBasedOn(command: Command): Direction = command match {
        case Forward => West
        case RotateClockwise => North
        case RotateAnticlockwise => South
      }
    }
  }

  final case class Coordinate2D(x: Int, y: Int)

  final case class Position(coordinate2D: Coordinate2D, direction: Direction)

  final case class RoverContext(gridDimensions: Int, mountainsOnGrid: List[Coordinate2D])
}
