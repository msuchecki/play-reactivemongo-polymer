package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.core.actors.Exceptions.PrimaryUnavailableException
import reactivemongo.core.commands.LastError

object Posts extends Controller with MongoController {

  def collection = db.collection[JSONCollection]("posts")

  def list = Action.async {implicit request =>
    collection.find(Json.obj())
      .cursor[JsObject]
      .collect[List]()
      .map(posts => Ok(Json.toJson(posts)))
      .recover {case PrimaryUnavailableException => InternalServerError("Please install MongoDB")}
  }

  def like(id: Int) = Action.async(BodyParsers.parse.json) { implicit request =>
    val value = (request.body \ "favorite").as[Boolean]
    collection.update(BSONDocument("uid" -> id), BSONDocument("$set" -> BSONDocument("favorite" -> value)))
      .map(le => Ok(Json.obj("success" -> le.ok)))
  }

  def delete(id: String) = Action.async {
    collection.remove(BSONDocument("uid" -> id))
      .map(le => RedirectAfterPost(le, routes.Posts.list()))
  }

  private def RedirectAfterPost(lastError: LastError, call: Call): Result =
    if (lastError.inError) InternalServerError("%s".format(lastError))
    else Redirect(call)

  def add = Action.async(BodyParsers.parse.json) { implicit request =>
    collection.save(BSONDocument(
      "uid" -> 123,
      "text" -> "Elo elo",
      "username" -> "Cat",
      "avatar" -> "../images/avatar-12.svg",
      "favorite" -> false
    )).map(le => Redirect(routes.Posts.list()))
  }
}
