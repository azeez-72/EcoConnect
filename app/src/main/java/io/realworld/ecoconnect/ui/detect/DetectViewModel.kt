package io.realworld.ecoconnect.ui.detect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetectViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is detect Fragment"
    }
    val text: LiveData<String> = _text
}