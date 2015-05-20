package backend

import play.api.Play.current
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PostRepo {
  def db = ReactiveMongoPlugin.db

  def collection = db.collection[JSONCollection]("posts")

  def find(): Future[List[JsObject]]
}

object PostMongoRepo extends PostRepo {
  override def find(): Future[List[JsObject]] = {
    collection.find(Json.obj())
      .cursor[JsObject]
      .collect[List]()
  }
}
