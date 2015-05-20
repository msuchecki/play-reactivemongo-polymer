package controllers

import backend.{PostMongoRepo, PostRepo}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import reactivemongo.bson.BSONDocument
import reactivemongo.core.actors.Exceptions.PrimaryUnavailableException
import reactivemongo.core.commands.LastError

trait Posts {
  this: Controller =>

  def postRepo: PostRepo = PostMongoRepo

  def list = Action.async {implicit request =>
    postRepo.find()
      .map(posts => Ok(Json.toJson(posts)))
      .recover {case PrimaryUnavailableException => InternalServerError("Please install MongoDB")}
  }

  def like(id: Int) = Action.async(BodyParsers.parse.json) { implicit request =>
    val value = (request.body \ "favorite").as[Boolean]
    postRepo.update(BSONDocument("uid" -> id), BSONDocument("$set" -> BSONDocument("favorite" -> value)))
      .map(le => Ok(Json.obj("success" -> le.ok)))
  }

  def delete(id: Int) = Action.async {
    postRepo.remove(BSONDocument("uid" -> id))
      .map(le => RedirectAfterPost(le, routes.Posts.list()))
  }

  private def RedirectAfterPost(lastError: LastError, call: Call): Result =
    if (lastError.inError) InternalServerError("%s".format(lastError))
    else Redirect(call)

  def add = Action.async(BodyParsers.parse.json) { implicit request =>
    postRepo.save(BSONDocument(
      "uid" -> 123,
      "text" -> "Elo elo",
      "username" -> "Cat",
      "avatar" -> "../images/avatar-12.svg",
      "favorite" -> false
    )).map(le => Redirect(routes.Posts.list()))
  }
}

object Posts extends Controller with Posts
