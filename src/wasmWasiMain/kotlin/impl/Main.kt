package impl

import bindings.*

// impl based on https://github.com/bytecodealliance/sample-wasi-http-rust/blob/53279f531cfa9c8e88c172f3c98a21e002246bbb/src/lib.rs

object IncomingHandlerExportsImpl : IncomingHandlerExports {
    override fun handle(request: Types.IncomingRequest, responseOut: Types.ResponseOutparam) {
        when(request.pathWithQuery()) {
            "/" -> httpHome(request, responseOut)
        }
    }

    fun httpHome(request: Types.IncomingRequest, outparam: Types.ResponseOutparam) {
        val headers = Types.Fields()
        val response = Types.OutgoingResponse(headers)
        val body = response.body().getOrThrow()

        Types.ResponseOutparam.set(outparam, Result.success(response))

        val out = body.write().getOrThrow()
        out.use {
            out.blockingWriteAndFlush("Hello, wasi:http/proxy world from kotlin!\n".toList().map { it.code.toUByte() })
        }

        Types.OutgoingBody.finish(body, null)
    }

}

fun main() {

}
