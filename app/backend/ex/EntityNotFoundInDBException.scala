package backend.ex

case class EntityNotFoundInDBException(query: String, collection: String)
  extends RuntimeException(s"Entity was not found in collection: $collection, by query: $query")
