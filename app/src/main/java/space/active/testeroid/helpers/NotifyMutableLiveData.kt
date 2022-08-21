package space.active.testeroid.helpers

import androidx.lifecycle.MutableLiveData

// If you use Collections or Class you need to notify observer after operations with Array
fun <T> MutableLiveData<T>.notifyObserver() {
    this.postValue(this.value)
}
