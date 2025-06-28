package de.fast2work.mobility.data.remote.cit

interface HBSuccessCallback<T> : RetrofitCallback<T> {
    override fun onSuccess(response: T)
    override fun onFailure(code: Int?, message: String?)
}