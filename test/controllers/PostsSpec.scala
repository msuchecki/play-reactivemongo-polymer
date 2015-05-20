package controllers

import backend.PostRepo
import org.specs2.mock.Mockito
import play.api.libs.json._
import play.api.mvc._
import play.api.test._
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError

import scala.concurrent.Future

object PostsSpec extends PlaySpecification with Results with Mockito {

  val mockPostRepo = mock[PostRepo]

  val wojciechPost = Json.obj(
    "uid" -> 1,
    "text" -> "Have you heard about the Javeo?",
    "username" -> "Wojciech",
    "avatar" -> "../images/avatar-01.svg",
    "favorite" -> true
  )
  val arunPost = Json.obj(
    "uid" -> 2,
    "text" -> "Microservices: the good, the bad, and the ugly",
    "username" -> "Arun",
    "avatar" -> "../images/avatar-03.svg",
    "favorite" -> false
  )

  class TestController() extends Controller with Posts {
    override def postRepo: PostRepo = mockPostRepo
  }

  val controller = new TestController()
  val nothingHappenedLastError = new LastError(true, None, None, None, None, 0, false)

  "Posts Page#list" should {
    "list posts" in new WithApplication {
      mockPostRepo.find() returns Future(List(wojciechPost, arunPost))

      val result: Future[Result] = controller.list().apply(FakeRequest())

      contentAsJson(result) must be equalTo JsArray(List(wojciechPost, arunPost))
    }
  }

  "Posts Page#delete" should {
    "remove post" in {
      mockPostRepo.remove(any[BSONDocument]) returns Future(nothingHappenedLastError)

      val result: Future[Result] = controller.delete(1).apply(FakeRequest())

      status(result) must be equalTo SEE_OTHER
      redirectLocation(result) must beSome(routes.Posts.list().url)
      there was one(mockPostRepo).remove(any[BSONDocument])
    }
  }

  "Posts Page#add" should {
    "create post" in {
      mockPostRepo.save(any[BSONDocument]) returns Future(nothingHappenedLastError)

      val request = FakeRequest().withBody(JsNull)
      val result: Future[Result] = controller.add()(request)

      status(result) must be equalTo SEE_OTHER
      redirectLocation(result) must beSome(routes.Posts.list().url)
      there was one(mockPostRepo).save(any[BSONDocument])
    }
  }

  "Posts Page#like" should {
    "like post" in {
      mockPostRepo.update(any[BSONDocument], any[BSONDocument]) returns Future(nothingHappenedLastError)

      val request = FakeRequest().withBody(Json.obj("favorite" -> true))
      val result: Future[Result] = controller.like(1)(request)

      status(result) must be equalTo OK
      contentAsJson(result) must be equalTo Json.obj("success" -> true)
      there was one(mockPostRepo).update(any[BSONDocument], any[BSONDocument])
    }
  }
}
