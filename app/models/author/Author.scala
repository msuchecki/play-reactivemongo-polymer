package models.author

import backend.Id

case class AuthorId(value: String) extends Id

case class Author(
  id: String,
  firstName: String,
  lastName: String) {
  def name = firstName + " " + lastName
}
