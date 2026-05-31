package com.example.spiele_statistiken.network

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST(".")
    suspend fun request(@Body body: ApiRequest): NachrichtResponse

    @POST(".")
    suspend fun gruppeRequest(@Body body: ApiRequest): GruppeResponse

    @POST(".")
    suspend fun spielerListeRequest(@Body body: ApiRequest): List<SpielerResponse>

    @POST(".")
    suspend fun spielTypListeRequest(@Body body: ApiRequest): List<SpielTypResponse>

    @POST(".")
    suspend fun eventsRequest(@Body body: ApiRequest): List<EventResponse>
}