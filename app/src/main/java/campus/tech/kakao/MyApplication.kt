package campus.tech.kakao

import android.app.Application
import campus.tech.kakao.map.R
import com.kakao.vectormap.KakaoMapSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoMapSdk.init(this, getString(R.string.KAKAO_API_KEY))
    }
}
