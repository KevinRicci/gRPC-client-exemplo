package br.com.zupacademy.client

import br.com.zupacademy.FreteServiceGrpc
import io.grpc.Channel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class ClientFactory {

    @Singleton
    fun freteClientStub(@GrpcChannel("frete") channel: Channel) : FreteServiceGrpc.FreteServiceBlockingStub{
        return FreteServiceGrpc.newBlockingStub(channel)
    }
}