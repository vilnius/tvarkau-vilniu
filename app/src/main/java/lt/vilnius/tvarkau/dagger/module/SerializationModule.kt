package lt.vilnius.tvarkau.dagger.module

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.data.GsonSerializer
import lt.vilnius.tvarkau.data.GsonSerializerImpl
import org.threeten.bp.LocalDateTime

@Module
class SerializationModule {

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LegacyApiModule.LocalDateTimeSerializer())
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }

    @Provides
    fun provideGsonSerializer(gson: Gson): GsonSerializer {
        return GsonSerializerImpl(gson)
    }
}
