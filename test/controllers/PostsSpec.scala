package controllers

import backend.PostRepo
import org.specs2.mock.Mockito
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc._
import play.api.test._

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

  "Posts Page#list" should {
    "should be valid" in new WithApplication {
      mockPostRepo.find() returns Future(List(wojciechPost, arunPost))

      val result: Future[Result] = controller.list().apply(FakeRequest())

      val bodyText: JsValue = contentAsJson(result)
      bodyText must be equalTo JsArray(List(wojciechPost, arunPost))
    }
  }
}
