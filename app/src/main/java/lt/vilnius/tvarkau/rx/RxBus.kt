package lt.vilnius.tvarkau.rx

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


object RxBus {

    private val rxBus = PublishSubject.create<Any>().toSerialized()

    val observable: Observable<Any>
        get() = rxBus

    fun publish(any: Any) {
        rxBus.onNext(any)
    }
}
