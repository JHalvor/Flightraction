package com.example.knuseklubben.ui.screens.sigcharts

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.knuseklubben.data.repository.SigchartsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SigchartsViewModel(
    private val sigchartsRepository: SigchartsRepository = SigchartsRepository()
) : ViewModel() {
    private val _sigchart = MutableStateFlow<ImageBitmap?>(null)
    val sigchart = _sigchart.asStateFlow()

    fun onClickedSigchartButton() {
        viewModelScope.launch {
            _sigchart.value = sigchartsRepository.getSigchartsImageBitmap()
        }
    }
}
