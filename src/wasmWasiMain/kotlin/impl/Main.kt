package impl

import bindings.ComponentException
import bindings.IncomingHandlerExports
import bindings.Types

// impl based on https://github.com/bytecodealliance/sample-wasi-http-rust/blob/53279f531cfa9c8e88c172f3c98a21e002246bbb/src/lib.rs

object IncomingHandlerExportsImpl : IncomingHandlerExports {
    override fun handle(request: Types.IncomingRequest, responseOut: Types.ResponseOutparam) {
        when(request.pathWithQuery()) {
            // right now, just respond to everything with home
            "/" -> httpResponse(request, responseOut, 200){
                "Hello, wasi:http from Kotlin!"
            }
            else -> httpResponse(request, responseOut, 404, "<h1>Sad 404 noises</h1>")
        }
    }

    fun httpResponse(request: Types.IncomingRequest, outparam: Types.ResponseOutparam, statusCode:Int, responseBuiler: () -> String){
        val headers = Types.Fields()
        val response = Types.OutgoingResponse(headers)
        response.setStatusCode(statusCode.toUShort())
        val body = response.body().getOrThrow()

        try{
            Types.ResponseOutparam.set(outparam, Result.success(response))

            val out = body.write().getOrThrow()
            out.use {
                out.blockingWriteAndFlush(responseBuiler().toList().map { it.code.toUByte() })
            }

            Types.OutgoingBody.finish(body, null)
        }catch (e:Throwable){
            // TODO exception catching doesn't seem to work yet, even if we just `throw Exception("lala") above
            val errorCode = if(e is ComponentException && e.value is Types.ErrorCode) {
                e.value
            }else{
                Types.ErrorCode.InternalError("Let's leak the stack trace yaaaay: ${e.stackTraceToString()}")
            }
            Types.ResponseOutparam.set(outparam, Result.failure(ComponentException(errorCode)))
        }
    }

    fun httpResponse(request: Types.IncomingRequest, outparam: Types.ResponseOutparam, statusCode:Int, responseBody: String) =
        httpResponse(request, outparam, statusCode) { responseBody }

}

fun main() {

}
