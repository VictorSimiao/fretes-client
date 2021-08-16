package br.com.zup.edu

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.inject.Inject

@Controller
class CalculadoraFretesController(@Inject val grpcClient:FretesServiceGrpc.FretesServiceBlockingStub) {

    @Get("api/fretes")
    fun calcula(@QueryValue cep:String):FreteDtoResponse{


        val request = FreteRequest.newBuilder()
            .setCep(cep)
            .build()
        //criando a chamada grpc
        val response = grpcClient.calculaFrete(request)

        return FreteDtoResponse(cep=response.cep, valor = response.valor)
    }
}
data class FreteDtoResponse(val cep:String, val valor:Double){

}