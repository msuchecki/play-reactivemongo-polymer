package backend

import backend.ex.IncorrectObjectIDException
import reactivemongo.bson.BSONObjectID

import scala.util.control.NonFatal

trait Id {
  def value: String
  def asBSONId: BSONObjectID =
    try {
      BSONObjectID(value)
    } catch {
      case NonFatal(ex) => throw new IncorrectObjectIDException(value, ex)
    }
}
