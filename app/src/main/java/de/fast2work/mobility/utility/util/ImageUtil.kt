package de.fast2work.mobility.utility.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import de.fast2work.mobility.utility.util.LocalConfig.IMAGE_WIDTH_SIZE
import de.fast2work.mobility.utility.util.LocalConfig.PROFILE_IMAGE_QUALITY
import de.fast2work.mobility.utility.util.LocalConfig.PROFILE_IMAGE_SIZE
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*


/**
 * ImageUtil class.
 *
 * This class is used to render images across the app.
 */
object ImageUtil {

    fun getPhotoFileUri(context: Context, fileName: String): File? {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            val mediaStorageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Images")

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {

            }

            // Return the file target for the photo based on filename
            return File(mediaStorageDir.path + File.separator + fileName)
        }
        return null
    }

    fun getVideoFileUri(context: Context, fileName: String): File? {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            val mediaStorageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), "Videos")

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            }

            // Return the file target for the video based on filename
            return File(mediaStorageDir.path + File.separator + fileName)
        }
        return null
    }

    // Checks if a volume containing external storage is available
// for read and write.
    // Returns true if external storage for photos is available
    private val isExternalStorageAvailable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return state == Environment.MEDIA_MOUNTED
        }


    /**
     * Function to get rotated bitmap
     * @param bitmap input bitmap
     * @param rotate rotate angle
     * @return rotated bitmap
     */
    private fun getRotatedBitmap(bitmap: Bitmap, rotate: Int): Bitmap {
        return if (rotate == 0) {
            bitmap
        } else {
            val mat = Matrix()
            mat.postRotate(rotate.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mat, true)
        }
    }


    /**
     * This method is used get orientation of camera photo
     *
     * @param context
     * @param imageUri  This parameter is Uri type
     * @param imagePath This parameter is String type
     * @return rotate
     */
    private fun getCameraPhotoOrientation(context: Context, imageUri: Uri?, imagePath: String): Int {
        var rotate = 0
        try {
//        try {
//            if (imageUri != null) context.contentResolver.notifyChange(imageUri, null)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                ExifInterface.ORIENTATION_NORMAL -> rotate = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rotate
    }

    /**
     * Function to set selected image
     * @param originalBitmap original bitmap
     * @param context instance of [Context]
     * @param imagePath image path
     * @param IMAGE_CAPTURE_URI image capture uri
     * @return instance of [Bitmap]
     */
    private fun setSelectedImage(originalBitmap: Bitmap, context: Context, imagePath: String, IMAGE_CAPTURE_URI: Uri): Bitmap {
        return try {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            if (manufacturer.equals("samsung", ignoreCase = true) || model.equals("samsung", ignoreCase = true)) {
                rotateBitmap(context, originalBitmap, imagePath, IMAGE_CAPTURE_URI)
            } else {
                originalBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            originalBitmap
        }

    }

    /**
     * Function to rotate bitmap
     * @param context instance of [Context]
     * @param bit input bitmap
     * @param imagePath image path
     * @param IMAGE_CAPTURE_URI image capture uri
     * @return instance of [Bitmap]
     */
    private fun rotateBitmap(context: Context, bit: Bitmap, imagePath: String, IMAGE_CAPTURE_URI: Uri): Bitmap {

        val rotation = getCameraPhotoOrientation(context, IMAGE_CAPTURE_URI, imagePath)
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(bit, 0, 0, bit.width, bit.height, matrix, true)
    }

    /**
     * Utility function for decoding an image resource. The decoded bitmap will
     * be optimized for further scaling to the requested destination dimensions
     * and scaling logic.
     *
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Decoded bitmap
     */
    private fun decodeResource(filePath: String, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Bitmap {

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, bmOptions)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = calculateSampleSize(bmOptions.outWidth, bmOptions.outHeight, dstWidth, dstHeight, scalingLogic)

        return BitmapFactory.decodeFile(filePath, bmOptions)
    }

    /**
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap
     * Bitmap to scale
     * @param dstWidth
     * Wanted width of destination bitmap
     * @param dstHeight
     * Wanted height of destination bitmap
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    private fun createScaledBitmap(unscaledBitmap: Bitmap, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Bitmap {
        val srcRect = calculateSrcRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scalingLogic)
        val dstRect = calculateDstRect(unscaledBitmap.width, unscaledBitmap.height, dstWidth, dstHeight, scalingLogic)
        val scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(scaledBitmap)
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))

        return scaledBitmap
    }

    /**
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     *
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     *
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
    enum class ScalingLogic {
        CROP, FIT
    }

    /**
     * Calculate optimal down-sampling factor given the dimensions of a source
     * image, the dimensions of a destination area and a scaling logic.
     *
     * @param srcWidth
     * Width of source image
     * @param srcHeight
     * Height of source image
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Optimal down scaling sample size for decoding
     */
    private fun calculateSampleSize(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Int {
        if (scalingLogic == ScalingLogic.FIT) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            return if (srcAspect > dstAspect) {
                srcWidth / dstWidth
            } else {
                srcHeight / dstHeight
            }
        } else {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            return if (srcAspect > dstAspect) {
                srcHeight / dstHeight
            } else {
                srcWidth / dstWidth
            }
        }
    }

    /**
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth
     * Width of source image
     * @param srcHeight
     * Height of source image
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    private fun calculateSrcRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Rect {
        if (scalingLogic == ScalingLogic.CROP) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            return if (srcAspect > dstAspect) {
                val srcRectWidth = (srcHeight * dstAspect).toInt()
                val srcRectLeft = (srcWidth - srcRectWidth) / 2
                Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight)
            } else {
                val srcRectHeight = (srcWidth / dstAspect).toInt()
                val scrRectTop = (srcHeight - srcRectHeight) / 2
                Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight)
            }
        } else {
            return Rect(0, 0, srcWidth, srcHeight)
        }
    }

    /**
     * Calculates destination rectangle for scaling bitmap
     *
     * @param srcWidth
     * Width of source image
     * @param srcHeight
     * Height of source image
     * @param dstWidth
     * Width of destination area
     * @param dstHeight
     * Height of destination area
     * @param scalingLogic
     * Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    private fun calculateDstRect(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, scalingLogic: ScalingLogic): Rect {
        return if (scalingLogic == ScalingLogic.FIT) {
            val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
            val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

            if (srcAspect > dstAspect) {
                Rect(0, 0, dstWidth, (dstWidth / srcAspect).toInt())
            } else {
                Rect(0, 0, (dstHeight * srcAspect).toInt(), dstHeight)
            }
        } else {
            Rect(0, 0, dstWidth, dstHeight)
        }
    }

    /**
     * This method is used to resize image
     *
     * @param context
     * @param dstWidth
     * @param dstHeight
     * @param scalingLogic
     * @param currentPhotoPath
     * @return scaledBitmap
     */
    fun getResizeProfileImage(context: Context, scalingLogic: ScalingLogic, currentPhotoPath: String, IMAGE_CAPTURE_URI: Uri?): Bitmap? {
        var rotate = 0
        try {
            val imageFile = File(currentPhotoPath)

            val exif = ExifInterface(imageFile.absolutePath)

            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            bmOptions.inJustDecodeBounds = false
            return if (bmOptions.outWidth < PROFILE_IMAGE_SIZE) {
                val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
                getRotatedBitmap(setSelectedImage(bitmap, context, currentPhotoPath, IMAGE_CAPTURE_URI!!), rotate)
            } else {
                val dstHeight = PROFILE_IMAGE_SIZE
                val dstWidth = PROFILE_IMAGE_SIZE
                val unscaledBitmap = decodeResource(currentPhotoPath, dstWidth, dstHeight, scalingLogic)
                val scaledBitmap = createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, scalingLogic)
                unscaledBitmap.recycle()
                getRotatedBitmap(scaledBitmap, rotate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * This method is used to resize image
     *
     * @param context
     * @param dstWidth
     * @param dstHeight
     * @param scalingLogic
     * @param currentPhotoPath
     * @return scaledBitmap
     */
    fun getResizeImage(context: Context, scalingLogic: ScalingLogic, currentPhotoPath: String, IMAGE_CAPTURE_URI: Uri?): Bitmap? {
        var rotate = 0
        try {
            val imageFile = File(currentPhotoPath)

            val exif = ExifInterface(imageFile.absolutePath)

            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            bmOptions.inJustDecodeBounds = false
            return if (bmOptions.outWidth < IMAGE_WIDTH_SIZE) {
                val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
                getRotatedBitmap(setSelectedImage(bitmap, context, currentPhotoPath, IMAGE_CAPTURE_URI!!), rotate)
            } else {
                val dstWidth: Int = IMAGE_WIDTH_SIZE
                val dstHeight: Int = (bmOptions.outHeight * dstWidth) / bmOptions.outWidth
                val unscaledBitmap = decodeResource(currentPhotoPath, dstWidth, dstHeight, scalingLogic)
                val scaledBitmap = createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, scalingLogic)
                unscaledBitmap.recycle()
                getRotatedBitmap(scaledBitmap, rotate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    fun getBitmapToFile(context: Context, bitmap: Bitmap,prefix:String="image_"): File? {
        try {
            return if (isExternalStorageAvailable) {

                val pictureFile = File.createTempFile(prefix, ".${Media.EXTENSION_JPEG}", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                if (pictureFile.exists()) {
                    pictureFile.delete()
                }

                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, PROFILE_IMAGE_QUALITY, bos)
                val pic = bos.toByteArray()
                val fileOutputStream = FileOutputStream(pictureFile)
                fileOutputStream.write(pic)
                fileOutputStream.close()
                fileOutputStream.flush()

                pictureFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    fun getBitmapToGIFFile(context: Context, bitmap: String,prefix:String="image_"): File? {
        try {
            return if (isExternalStorageAvailable) {

                val pictureFile = File.createTempFile(prefix, ".${Media.PREFIX_GIF}", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                if (pictureFile.exists()) {
                    pictureFile.delete()
                }

                try {
                    val output = FileOutputStream(pictureFile)
                    val input = FileInputStream(bitmap)
                    val inputChannel: FileChannel = input.channel
                    val outputChannel: FileChannel = output.channel
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel)
                    output.close()
                    input.close()
                    //Toast.makeText(this@MainActivity, "Image Downloaded", Toast.LENGTH_SHORT).show()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                pictureFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqheight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqheight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize > reqheight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    fun bitmapToFile(context: Context, bitmap: Bitmap, prefix: String? = "IMG_"): File? {
        try {
            return if (isExternalStorageAvailable) {
                val sdcard = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileNameString = "$prefix${timeStamp}.jpg"
                val pictureFile = File(sdcard, fileNameString)

                val fOut = FileOutputStream(pictureFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
                fOut.flush()
                fOut.close()
                pictureFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}