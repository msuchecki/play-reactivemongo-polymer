package controllers

import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{BodyParsers, Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.ImplicitBSONHandlers._

object People extends Controller with MongoController {

  def collection = db.collection[JSONCollection]("people")

  def init = Action.async { implicit request =>
    val obj1 = Json.obj(
      "uid" -> 1,
      "text" -> "Have you heard about the Web Components revolution?",
      "username" -> "Eric",
      "avatar" -> "../images/avatar-01.svg",
      "favorite" -> false
    )
    val obj2 = Json.obj(
      "uid" -> 2,
      "text" -> "Loving this Polymer thing.",
      "username" -> "Rob",
      "avatar" -> "../images/avatar-02.svg",
      "favorite" -> false
    )
    val obj3 = Json.obj(
      "uid" -> 3,
      "text" -> "So last year...",
      "username" -> "Dimitri",
      "avatar" -> "../images/avatar-03.svg",
      "favorite" -> false
    )
    val obj4 = Json.obj(
      "uid" -> 4,
      "text" -> "Pretty sure I came up with that first.",
      "username" -> "Ada",
      "avatar" -> "../images/avatar-07.svg",
      "favorite" -> false
    )
    val obj5 = Json.obj(
      "uid" -> 5,
      "text" -> "Yo, I heard you like components, so I put a component in your component.",
      "username" -> "Grace",
      "avatar" -> "../images/avatar-08.svg",
      "favorite" -> false
    )
    val obj6 = Json.obj(
      "uid" -> 6,
      "text" -> "Centralize, centrailize.",
      "username" -> "John",
      "avatar" -> "../images/avatar-04.svg",
      "favorite" -> false
    )
    val obj7 = Json.obj(
      "uid" -> 7,
      "text" -> "Has anyone seen my cat?",
      "username" -> "Zelda",
      "avatar" -> "../images/avatar-06.svg",
      "favorite" -> false
    )
    val obj8 = Json.obj(
      "uid" -> 8,
      "text" -> "Decentralize!",
      "username" -> "Norbert",
      "avatar" -> "../images/avatar-05.svg",
      "favorite" -> false
    )

    collection.bulkInsert(Enumerator.enumerate(List(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8)))
      .map(i => Ok("db was initialized"))
  }

  def list = Action.async {implicit request =>
    collection.find(Json.obj())
      .cursor[JsObject]
      .collect[List]()
      .map(people => Ok(Json.toJson(people)))
  }

  def like(id: Int) = Action.async(BodyParsers.parse.json) { implicit request =>
    val value = (request.body \ "favorite").as[Boolean]
    collection.update(BSONDocument("uid" -> id), BSONDocument("$set" -> BSONDocument("favorite" -> value)))
      .map(le => Ok(Json.obj("success" -> le.ok)))
  }
}
