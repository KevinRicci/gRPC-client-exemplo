package br.com.zupacademy.frete

import br.com.zupacademy.CepRequest
import br.com.zupacademy.ErrorDetails
import br.com.zupacademy.FreteServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.inject.Inject

@Controller("/fretes")
class FreteController(@Inject val clientFrete : FreteServiceGrpc.FreteServiceBlockingStub) {

    @Get
    fun buscaCep(@QueryValue("cep") cep: String) : HttpResponse<Any> {
        try {
            val response = clientFrete.consultaCep(CepRequest.newBuilder().setCep(cep).build())
            return HttpResponse.ok(FreteResponse(response.cep, response.valor))
        }
        catch(e: StatusRuntimeException){
            val status = e.status
            val message = e.message

            if(status.code.equals(Status.Code.INVALID_ARGUMENT)){
                return HttpResponse.badRequest(message)
            }

            if(status.code.equals(Status.Code.PERMISSION_DENIED)){
                val statusProto = io.grpc.protobuf.StatusProto.fromThrowable(e) ?: return HttpResponse.status(HttpStatus.FORBIDDEN)
                val error = statusProto.getDetails(0).unpack(ErrorDetails::class.java)
                return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN).body("${statusProto.message} : ${error.message}")
            }

            return HttpResponse.status<Any?>(HttpStatus.INTERNAL_SERVER_ERROR).body(message)
        }
    }
}

data class FreteResponse(val cep : String, val valor : Double){}