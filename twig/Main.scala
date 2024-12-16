import dev.wishingtree.branch.ursula.UrsulaApp
import dev.wishingtree.branch.ursula.command.Command
import serve.Serve

object Main extends UrsulaApp {

  override val commands: Seq[Command] = Seq(
    Serve
  )
}
