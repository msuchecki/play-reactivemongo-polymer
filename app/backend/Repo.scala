package backend

import backend.ex.EntityNotFoundInDBException
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

trait Repo[IdType <: Id, T] {
  def findOne(id: IdType): Future[T]
}

trait MongoRepo[IdType <: Id, T] extends Repo[IdType, T] {
  private def db = ReactiveMongoPlugin.db
  private def collection: BSONCollection = db.collection(collectionName)
  protected def collectionName: String
  protected implicit def reader: BSONDocumentReader[T]

  def findOne(id: IdType): Future[T] =
    collection.find(BSONDocument("_id" -> id.asBSONId)).one[T]
      .map(_.getOrElse(throw new EntityNotFoundInDBException(id.value, collectionName)))
}