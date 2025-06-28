package de.fast2work.mobility.utility.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

val PICK_IMAGE_MIME_TYPES = arrayOf("image/jpeg", "image/png","image/jpg")
val PICK_VIDEO_MIME_TYPES = arrayOf("video/mp4", "video/mpeg","image/jpeg", "image/png","image/jpg")
val PICK_FILE_MIME_TYPES = arrayOf("application/pdf")

class PickMultipleFiles : ActivityResultContract<Array<String>, ArrayList<Uri>>() {
    override fun createIntent(context: Context, input: Array<String>): Intent {
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
//            .setType(input)
            .setType("${input[0].substringBefore('/')}/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, input)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<Uri> {
        val arrayList = arrayListOf<Uri>()
        if (intent?.data != null) {
            arrayList.add(intent.data!!)
        }
        val total = intent?.clipData?.itemCount ?: 0
        for (i in 0 until total) {
            val item = intent?.clipData?.getItemAt(i)?.uri
            item?.let {
                arrayList.add(it)
            }
        }
        return arrayList
    }

    override fun getSynchronousResult(
        context: Context,
        input: Array<String>
    ): ActivityResultContract.SynchronousResult<ArrayList<Uri>>? {
        return null
    }

}

/**
 * This class contains code to pick single file from system
 *
 */
class PickSingleFile : ActivityResultContract<Array<String>, ArrayList<Uri>>() {
    override fun createIntent(context: Context, input: Array<String>): Intent {
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("${input[0].substringBefore('/')}/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<Uri> {
        val arrayList = arrayListOf<Uri>()
        if (intent?.data != null) {
            arrayList.add(intent.data!!)
        }
        return arrayList
    }

    override fun getSynchronousResult(context: Context, input: Array<String>): ActivityResultContract.SynchronousResult<ArrayList<Uri>>? {
        return null
    }
}

/**
 * This class contains code to pick PDF file from system
 *
 */
class PickPDFFile : ActivityResultContract<Array<String>, ArrayList<Uri>>() {
    override fun createIntent(context: Context, input: Array<String>): Intent {
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input[0])
            .putExtra(Intent.EXTRA_MIME_TYPES, input)

    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<Uri> {
        val arrayList = arrayListOf<Uri>()
        if (intent?.data != null) {
            arrayList.add(intent.data!!)
        }
        return arrayList
    }

    override fun getSynchronousResult(context: Context, input: Array<String>): ActivityResultContract.SynchronousResult<ArrayList<Uri>>? {
        return null
    }
}