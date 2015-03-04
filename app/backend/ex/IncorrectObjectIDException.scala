package backend.ex

case class IncorrectObjectIDException(id: String, cause: Throwable)
  extends RuntimeException(s"Incorrect ObjectId: $id", cause)
