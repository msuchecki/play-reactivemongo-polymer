package models.book

import backend.Id
import models.author.AuthorId
import org.joda.time.DateTime

case class BookId(value: String) extends Id

case class AuthorData(
  id: AuthorId,
  name: String)

case class Book(
  id: BookId,
  title: String,
  description: String,
  author: AuthorData,
  state: BookState,
  pages: Int,
  published: DateTime,
  language: String)
