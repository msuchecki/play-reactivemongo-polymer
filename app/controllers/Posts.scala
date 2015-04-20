package controllers

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{BodyParsers, Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.ImplicitBSONHandlers._
import reactivemongo.core.actors.Exceptions.PrimaryUnavailableException

object Posts extends Controller with MongoController {

  def collection = db.collection[JSONCollection]("posts")

  def list = Action.async {implicit request =>
    collection.find(Json.obj())
      .cursor[JsObject]
      .collect[List]()
      .map(posts => Ok(Json.toJson(posts)))
      .recover {case PrimaryUnavailableException => InternalServerError("Pleas install MongoDB")}
  }

  def like(id: Int) = Action.async(BodyParsers.parse.json) { implicit request =>
    val value = (request.body \ "favorite").as[Boolean]
    collection.update(BSONDocument("uid" -> id), BSONDocument("$set" -> BSONDocument("favorite" -> value)))
      .map(le => Ok(Json.obj("success" -> le.ok)))
  }
}
