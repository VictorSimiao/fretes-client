package br.com.zup.edu

import com.google.protobuf.Any
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class CalculadoraFretesController(@Inject val grpcClient: FretesServiceGrpc.FretesServiceBlockingStub) {

    @Get("api/fretes")
    fun calcula(@QueryValue cep: String): FreteDtoResponse {


        val request = FreteRequest.newBuilder()
            .setCep(cep)
            .build()


        try {
            val response = grpcClient.calculaFrete(request)
            return FreteDtoResponse(cep = response.cep, valor = response.valor)
        } catch (e: StatusRuntimeException) {
            val statusCode = e.status.code
            val descricaoMensagem = e.status.description

            if (statusCode == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, descricaoMensagem)
            }
            //ErrorDetails
            if (statusCode == Status.Code.PERMISSION_DENIED) {
                val statusProto = StatusProto.fromThrowable(e)
                if (statusProto == null) {
                    throw HttpStatusException(HttpStatus.FORBIDDEN, descricaoMensagem)
                }
                val anydetalhes: Any = statusProto.detailsList.get(0)
                val errorDetails = anydetalhes.unpack(ErrorDetails::class.java)

                throw HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)

        }


    }
}

data class FreteDtoResponse(val cep: String, val valor: Double) {

}