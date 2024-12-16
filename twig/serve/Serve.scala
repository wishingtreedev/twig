package serve

import com.sun.net.httpserver.HttpServer
import dev.wishingtree.branch.spider.server.OpaqueSegments.Segments
import dev.wishingtree.branch.spider.server.{
  ContextHandler,
  FileContextHandler,
  OpaqueSegments
}
import dev.wishingtree.branch.ursula.args.{Argument, Flag, IntFlag}
import dev.wishingtree.branch.ursula.command.Command

import java.net.InetSocketAddress

object PortFlag extends IntFlag {

  override val name: String         = "port"
  override val shortKey: String     = "p"
  override val description: String  = "The port to serve on. Defaults to 9000"
  override val default: Option[Int] = Some(9000)

}

object DirFlag extends Flag[Segments] {

  override val name: String                             = "dir"
  override val shortKey: String                         = "d"
  override val description: String                      =
    "The path to serve files from. Defaults to current directory"
  override val default: Option[Segments]                = Some(Segments.wd)
  override def parse: PartialFunction[String, Segments] = { case str =>
    Segments(str)
  }
}

object Serve extends Command {

  override val description: String         = "Start an HTTP server that serves files"
  override val usage: String               = "serve -p 9000 -d ."
  override val examples: Seq[String]       = Seq(
    "serve",
    "serve -p 8080",
    "serve -d ./files"
  )
  override val trigger: String             = "serve"
  override val flags: Seq[Flag[?]]         = Seq(PortFlag, DirFlag)
  override val arguments: Seq[Argument[?]] = Seq.empty

  override def action(args: Seq[String]): Unit = {

    val port = PortFlag.parseFirstArg(args).get
    val dir  = DirFlag.parseFirstArg(args).get

    given server: HttpServer =
      HttpServer.create(new InetSocketAddress(port), 0)

    val fileHandler =
      FileContextHandler(dir, filters = Seq(ContextHandler.timingFilter))

    ContextHandler
      .registerHandler(fileHandler)

    server.start()

    println(s"Server started at http://localhost:$port")
    println(s"Press return to exit")
    scala.io.StdIn.readLine()
    println(s"Shutting down server")

  }
}
