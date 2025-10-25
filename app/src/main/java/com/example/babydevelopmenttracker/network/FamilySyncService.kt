package com.example.babydevelopmenttracker.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateFamilyRequest(val secret: String? = null)

data class RegisterFamilyMemberRequest(
    val inviteCode: String,
    val deviceIdentifier: String? = null,
)

data class FamilyRegistrationResponse(val authToken: String)

data class JournalEntryPayload(
    val id: String,
    val timestampEpochMillis: Long,
    val mood: String,
    val body: String,
    val attachments: List<String>? = null,
)

interface FamilySyncService {
    @POST("families/{familyId}")
    suspend fun createFamily(
        @Path("familyId") familyId: String,
        @Body request: CreateFamilyRequest,
    ): FamilyRegistrationResponse

    @POST("families/{familyId}/members")
    suspend fun registerFamilyMember(
        @Path("familyId") familyId: String,
        @Body request: RegisterFamilyMemberRequest,
    ): FamilyRegistrationResponse

    @GET("families/{familyId}/journal")
    suspend fun fetchJournalEntries(
        @Path("familyId") familyId: String,
    ): List<JournalEntryPayload>

    @PUT("families/{familyId}/journal/{entryId}")
    suspend fun upsertJournalEntry(
        @Path("familyId") familyId: String,
        @Path("entryId") entryId: String,
        @Body entry: JournalEntryPayload,
    )

    @DELETE("families/{familyId}/journal/{entryId}")
    suspend fun deleteJournalEntry(
        @Path("familyId") familyId: String,
        @Path("entryId") entryId: String,
    )

    companion object {
        private const val DEFAULT_BASE_URL = "https://example.com/api/"

        fun create(
            baseUrl: String = DEFAULT_BASE_URL,
            client: OkHttpClient = defaultOkHttpClient(),
            moshi: Moshi = defaultMoshi(),
        ): FamilySyncService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client)
                .build()
            return retrofit.create(FamilySyncService::class.java)
        }

        private fun defaultOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }

        private fun defaultMoshi(): Moshi {
            return Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }
    }
}
