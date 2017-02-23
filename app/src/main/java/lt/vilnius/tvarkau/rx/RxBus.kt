package lt.vilnius.tvarkau.rx

import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject


/**
 * @author Martynas Jurkus
 */
object RxBus {

    private val rxBus = SerializedSubject(PublishSubject.create<Any>())

    val observable: Observable<Any>
        get() = rxBus

    fun publish(any: Any) {
        rxBus.onNext(any)
    }
}