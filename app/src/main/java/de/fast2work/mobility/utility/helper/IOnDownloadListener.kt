package de.fast2work.mobility.utility.helper

interface IOnDownloadListener {
    fun onSuccess(filePath:String)
    fun onFailure()
    fun onInProgress()
}