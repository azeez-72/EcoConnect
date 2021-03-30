package io.realworld.ecoconnect.ui.detect

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.call
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Math.abs
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ImageClassifier(private val context: CameraFragment) {
    private var interpreter: Interpreter? = null
    var isInitialized = false
        private set

    /** Executor to run inference task in the background */
    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    private var inputImageWidth: Int = 224
    private var inputImageHeight: Int = 224
    private var modelInputSize: Int = 0 // will be inferred from TF Lite model

    fun initialize(model: Any): Task<Void> {
        return call(
            executorService,
            Callable<Void> {
                initializeInterpreter(model)
                null
            }
        )
    }

    private fun initializeInterpreter(model: Any) {
        // Initialize TF Lite Interpreter with NNAPI enabled
        val options = Interpreter.Options()
        options.setUseNNAPI(true)
        val interpreter: Interpreter
        if (model is ByteBuffer) {
            interpreter = Interpreter(model, options)
        } else {
            interpreter = Interpreter(model as File, options)
        }
        // Read input shape from model file
        val inputShape = interpreter.getInputTensor(0).shape()
        modelInputSize = inputImageWidth * inputImageHeight * PIXEL_SIZE

        // Finish interpreter initialization
        this.interpreter = interpreter
        isInitialized = true
        Log.d(TAG, "Initialized TFLite interpreter.")
    }

    fun classify(bitmap: Bitmap): String {
        if (!isInitialized) {
            Log.e(TAG, "Could not classify image")
            throw IllegalStateException("TF Lite Interpreter is not initialized yet.")
        }

        var elapsedTime: Long

        // Preprocessing: resize the input
        var startTime: Long = System.nanoTime()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        val batchNum = 0
        val input = Array(1) { Array(224) { Array(224) { ByteArray(3) } } }
        for (x in 0..223) {
            for (y in 0..223) {
                val pixel = scaledBitmap.getPixel(x, y)
                input[batchNum][x][y][0] = ((Color.red(pixel) shr 16) and 0xff).toByte()
                input[batchNum][x][y][1] = ((Color.green(pixel) shr 8) and 0xff).toByte()
                input[batchNum][x][y][2] = (Color.blue(pixel) and 0xff).toByte()
            }
        }
        var bufferSize = OUTPUT_CLASSES_COUNT * java.lang.Integer.SIZE / java.lang.Byte.SIZE
        bufferSize /= 4
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
        elapsedTime = (System.nanoTime() - startTime) / 1000000
        Log.d(TAG, "Preprocessing time = " + elapsedTime + "ms")

        startTime = System.nanoTime()
        interpreter?.run(input, modelOutput)
//        modelOutput.rewind()
//        val probabilities = modelOutput.asIntBuffer()
//        try {
//            val reader = BufferedReader(
//                InputStreamReader(assets.open("dict.txt"))
//            )
//            for (i in probabilities.capacity()!!) {
//                val label: String = reader.readLine()
//                val probability = probabilities.get(i)
//                println("$label: $probability")
//            }
//        } catch (e: IOException) {
//            // File not found?
//        }
        elapsedTime = (System.nanoTime() - startTime) / 1000000
        Log.d(TAG, "Inference time = " + elapsedTime + "ms")

        return getOutputString(IntArray(1) {modelOutput[0].toInt()})
    }

    fun classifyAsync(bitmap: Bitmap): Task<String> {
        return call(executorService, Callable<String> { classify(bitmap) })
    }

    fun close() {
        call(
            executorService,
            Callable<String> {
                interpreter?.close()
                Log.d(TAG, "Closed TFLite interpreter.")
                null
            }
        )
    }

    private fun getOutputString(output: IntArray): String {
        val iter = output.iterator()
        for (i in iter) {
            Log.i(TAG, i.toString())
        }
        val maxIndex = output.indices.maxBy { output[it] } ?: -1
        return "Prediction Result: %d\nConfidence: %d".format(maxIndex, output[maxIndex])
    }

    companion object {
        private const val TAG = "ImageClassifier"

        private const val PIXEL_SIZE = 1

        private const val OUTPUT_CLASSES_COUNT = 6
        private const val MODEL_NAME_KEY = "model_name"
    }
}
