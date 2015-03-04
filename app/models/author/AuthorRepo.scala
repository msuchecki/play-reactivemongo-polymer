package models.author

import backend.{MongoRepo, Repo}
import reactivemongo.bson.BSONDocumentReader

trait AuthorRepo extends Repo[AuthorId, Author]

object AuthorMongoRepo extends MongoRepo[AuthorId, Author] with AuthorRepo {
  override protected def collectionName: String = "users"

  override protected implicit def reader: BSONDocumentReader[Author] = ???
}
