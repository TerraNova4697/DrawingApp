package com.example.drawingapp.baseinterfaces

interface BaseView<T> {
    fun setPresenter(presenter: T)
}