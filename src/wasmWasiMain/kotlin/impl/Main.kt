package impl

import bindings.*
import bindings.Types.*

// implementation based on https://github.com/bytecodealliance/sample-wasi-http-rust/blob/53279f531cfa9c8e88c172f3c98a21e002246bbb/src/lib.rs

object IncomingHandlerImpl : IncomingHandler {
    override fun handle(request: IncomingRequest, responseOut: ResponseOutparam) {
        when (val path = request.pathWithQuery()) {
            "/throw-exception" -> responseOut.response {
                throw ComponentException(ErrorCode.InternalError("This is a test exception"))
            }

            "/" -> responseOut.response(200) {
                val W = WallClock.now()

                val startNs = W.seconds.toLong() * 1_000_000_000L + W.nanoseconds.toLong()

                body(
                    """
                    <p>Hello, wasi:http from Kotlin!</p>
                    <p>Assuming our clocks are perfectly in sync, the server took roughly 
                    <script>
                        (function() {
                            // as a js bigint
                            const serverStartNs = ${startNs}n; 
                            
                            // performance.now() is relative, but since we are assuming 
                            // "perfectly in sync" clocks for this exercise, we'll use Date.now() 
                            const clientEndMs = Date.now();
                            
                            const serverStartMs = Number(serverStartNs / 1_000_000n);
                            
                            const diffSeconds = Math.abs((clientEndMs - serverStartMs) / 1000);
                            document.write(diffSeconds.toFixed(4));
                        })();
                    </script> seconds to respond!</p>
                    """.trimIndent()
                )
            }

            "/echo" -> responseOut.response(200) {
                body(
                    request.consume().getOrThrow().use { incomingBody ->
                        incomingBody.stream().getOrThrow().use { stream ->
                            // basically: stop reading if the list if read either returns failure, or an empty list
                            val inputBytes = generateSequence { stream.read(1u).getOrNull()?.singleOrNull() }
                            inputBytes.map { it.toInt().toChar() }.joinToString("")
                        }
                    })
            }

            "/echo-headers" -> responseOut.response(200, request.headers(), "")

            else -> {
                debugPrintLn("Unknown path: $path")
                responseOut.response(404, content = "<h1>Sad 404 noises</h1>")
            }
        }
    }
}

fun ResponseOutparam.response(statusCode: Int = 200, headers: Fields = Fields(), build: OutgoingResponse.() -> Unit) {
    val response = OutgoingResponse(headers)
    try {
        response.setStatusCode(statusCode.toUShort())
        response.build()
        ResponseOutparam.set(this, Result.success(response))
    } catch (e: Throwable) {
        response.close()
        val errorCode = if (e is ComponentException && e.value is ErrorCode) {
            e.value
        } else {
            ErrorCode.InternalError("Unexpected error: $e")
        }
        ResponseOutparam.set(this, Result.failure(ComponentException(errorCode)))
    }
}

fun OutgoingResponse.body(build: Streams.OutputStream.() -> Unit) {
    val body = body().getOrThrow()
    val out: Streams.OutputStream = body.write().getOrThrow()
    out.use(build)
    OutgoingBody.finish(body, null)
}

fun ResponseOutparam.response(statusCode: Int = 200, headers: Fields = Fields(), content: String) {
    response(statusCode, headers) { body(content) }
}

fun OutgoingResponse.body(content: String) {
    body {
        blockingWriteAndFlush(content.map { it.code.toUByte() })
    }
}

fun debugPrint(msg: String) {
    Stdout.getStdout().blockingWriteAndFlush(msg.map { it.code.toUByte() })
}

fun debugPrintLn(msg: String) {
    debugPrint("$msg\n")
}
