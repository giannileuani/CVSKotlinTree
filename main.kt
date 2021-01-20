
import java.io.File

fun main(args: Array<String>) {
    if (args.size >= 1) {
        val f = File(args[0])
        if (f.exists() && f.isFile) {
            var ip = InputParser()
            ip.process(f)
        } else {
            System.err.println("Need a valid input file")
        }
    } else {
        System.err.println("Need an input file")
    }
}
