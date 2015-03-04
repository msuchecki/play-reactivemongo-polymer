package controllers

import models.author.{AuthorMongoRepo, AuthorRepo}
import play.api.mvc.Controller

trait Authors extends Controller {
  def authorRepo: AuthorRepo
}

object Authors extends Authors {
  def authorRepo: AuthorRepo = AuthorMongoRepo
}