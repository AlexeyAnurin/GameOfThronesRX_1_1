package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.skillbranch.gameofthrones.data.local.AppDatabase
import ru.skillbranch.gameofthrones.data.remote.res.ApiServerInterface
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.util.concurrent.TimeUnit

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        //AA? Нужен ли здесь контекст как отдельное свойство?
        context = this
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        //AA lateinit var db: AppDatabase. Нужно иметь только один экземпляр, пересоздание - очень тяжёлая операция
        db = Room.databaseBuilder(context, AppDatabase::class.java, "database").build().also { println("db: AppDatabase - $it") }
        // lateinit var api: ApiServerInterface, который interface. Cюда вернулся результат: retrofit.create(ApiServerInterface::class.java)
        api = initApi().also { println("api: ApiServerInterface - $it") }
        //AA class ConnectivityChangeReceiver : BroadcastReceiver()
        connectivityReceiver = ConnectivityChangeReceiver()

        //AA object RootRepository. Этой строкой просто передаю db внутрь RootRepository, т.к.: fun init(database: AppDatabase) { db = database }
        RootRepository.init(db)
    }

    //AA? Возврашает объект ApiServerInterface?
    private fun initApi(): ApiServerInterface {
        //AA Creates a GsonBuilder instance that can be used to build Gson with various configuration settings.
        //AA public Gson create() {...  Creates a GSON instance based on the current configuration.
        val gson = GsonBuilder().create().also { println("val gson - $it") }
        //AA public final class GsonConverterFactory extends Converter.Factory.
        //AA create(gson) создаёт экземпляр GsonConverterFactory, используя объект GSON. Encoding to JSON and decoding from JSON.
        val gsonFactory = GsonConverterFactory.create(gson).also { println("val gsonFactory - $it") }

        //AA HttpLoggingInterceptor - Сетевой аналог LogCat.
        val interceptor = HttpLoggingInterceptor()
        //AA Подключаем перехватчик к веб-клиенту. Существует несколько уровней перехвата данных: NONE, BASIC, HEADERS, BODY.
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS

        //AA Use `new OkHttpClient.Builder()` to create a shared instance with custom settings.
        val okHttpClient = OkHttpClient.Builder()
            //AA private val TIMEOUT_SEC = 30 в companion object. fun connectTimeout(timeout: Long, unit: TimeUnit) - 1й параметр: значение, 2й: размерность.
            .connectTimeout(TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
            //AA Добавляем перехватчик, установленный выше.
            .addInterceptor(interceptor)
            //AA fun build(): OkHttpClient = OkHttpClient(this), где this - Builder
            .build()

        //AA? .Builder() как это работает. Аналогично сделано выше для OkHttpClient.
        val retrofit: Retrofit = Retrofit.Builder()
            //AA const val BASE_URL = "https://www.anapioficeandfire.com/api/"
            .baseUrl(AppConfig.BASE_URL)
            //AA RX неведомая хуйня.
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(gsonFactory)
            .client(okHttpClient)
            //AA Create the class Retrofit instance using the configured values.
            .build().also { println("retrofit - $it") }

        //AA Create an implementation of the API endpoints defined by the interface ApiServerInterface.
        return retrofit.create(ApiServerInterface::class.java)
    }

    companion object {
        private val TIMEOUT_SEC = 30

        ///? Зачем делать контекст lateinit
        lateinit var context: Context
        lateinit var preferences: SharedPreferences
        lateinit var db: AppDatabase
        lateinit var api: ApiServerInterface
        lateinit var connectivityReceiver: ConnectivityChangeReceiver

        fun logThrowable(tag: String, throwable: Throwable) {
            Log.d(tag, throwable.message ?: "")
        }
    }
}