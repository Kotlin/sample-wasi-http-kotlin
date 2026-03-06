package impl

import bindings.*

// implementation based on https://github.com/bytecodealliance/sample-wasi-http-rust/blob/53279f531cfa9c8e88c172f3c98a21e002246bbb/src/lib.rs

fun debugPrint(msg: String) {
    Stdout.getStdout().blockingWriteAndFlush(msg.map { it.code.toUByte() })
}

fun debugPrintLn(msg: String) {
    debugPrint("$msg\n")
}

object IncomingHandlerImpl : IncomingHandler {
    override fun handle(request: Types.IncomingRequest, responseOut: Types.ResponseOutparam) {
        when (val path = request.pathWithQuery()) {
            "/" -> httpResponse(request, responseOut) {
                val W = WallClock.now()

                val startNs = W.seconds.toLong() * 1_000_000_000L + W.nanoseconds.toLong()

                Response(
                    statusCode = 200, body =
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
            "/echo" -> httpResponse(request, responseOut) {
                Response(statusCode = 200, body = request.consume().getOrThrow().use { incomingBody ->
                    incomingBody.stream().getOrThrow().use { stream ->
                        // basically: stop reading if the list if read either returns failure, or an empty list
                        val inputBytes = generateSequence { stream.read(1u).getOrNull()?.singleOrNull() }
                        inputBytes.map { it.toInt().toChar() }.joinToString("")
                    }
                })
            }
            "/echo-headers" -> httpResponse(request, responseOut) {
                Response(statusCode = 200, headers = request.headers(), body = "")
            }
            else -> {
                debugPrintLn("Unknown path: $path")
                httpResponse(request, responseOut, 404, "<h1>Sad 404 noises</h1>")
            }
        }
    }

    data class Response(val statusCode: Int, val headers: Types.Fields = Types.Fields(), val body: String)

    fun httpResponse(
        request: Types.IncomingRequest,
        outparam: Types.ResponseOutparam,
        responseBuilder: () -> Response
    ) {
        try {
            val userResponse = responseBuilder()
            val response = Types.OutgoingResponse(userResponse.headers)
            response.setStatusCode(userResponse.statusCode.toUShort())
            val body = response.body().getOrThrow()

            Types.ResponseOutparam.set(outparam, Result.success(response))

            val out = body.write().getOrThrow()
            out.use {
                out.blockingWriteAndFlush(userResponse.body.toList().map { it.code.toUByte() })
            }

            Types.OutgoingBody.finish(body, null)
        } catch (e: Throwable) {
            // TODO exception catching doesn't seem to work yet, even if we just `throw Exception("lala") above
            val errorCode = if (e is ComponentException && e.value is Types.ErrorCode) {
                e.value
            } else {
                Types.ErrorCode.InternalError("Let's leak the stack trace yaaaay: ${e.stackTraceToString()}")
            }
            Types.ResponseOutparam.set(outparam, Result.failure(ComponentException(errorCode)))
        }
    }

    fun httpResponse(
        request: Types.IncomingRequest,
        outparam: Types.ResponseOutparam,
        statusCode: Int,
        responseBody: String
    ) =
        httpResponse(request, outparam) { Response(statusCode, body = responseBody) }

}

fun main() {

}
