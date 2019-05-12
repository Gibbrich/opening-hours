package OpeningHours

import OpeningHours.data.ExtRestaurantData
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

const val NO_RESTAURANT_DATA = "You should specify file with restaurant data"
const val TOO_MANY_ARGUMENTS = "You should specify at most 2 arguments: input file path and output file path"
const val INCORRECT_INPUT_DATA_FORMAT = "Incorrect input data format"
const val INPUT_FILE_DOES_NOT_EXIST = "Input file does not exists"
const val CANT_READ_INPUT_FILE = "Can't read input file"

internal data class Arguments(
    val inputFilePath: String,
    val outputFilePath: String?
)

internal fun writeOutput(outputFilePath: String, output: String) = File(outputFilePath).writeText(output)

internal fun getArguments(args: Array<String>): Arguments {
    if (args.isEmpty()) {
        throw IllegalArgumentException(NO_RESTAURANT_DATA)
    }

    if (args.size > 2) {
        throw IllegalArgumentException(TOO_MANY_ARGUMENTS)
    }

    val inputFilePath = args[0]

    val outputFilePath = if (args.size == 2) {
        args[1]
    } else {
        null
    }

    return Arguments(inputFilePath, outputFilePath)
}

internal fun getInputFile(inputFilePath: String): File {
    val input = File(inputFilePath)
    if (input.exists().not()) {
        throw IllegalStateException(INPUT_FILE_DOES_NOT_EXIST)
    }

    if (input.canRead().not()) {
        throw IllegalStateException(CANT_READ_INPUT_FILE)
    }

    return input
}

internal fun parseData(data: File): ExtRestaurantData = try {
    Gson().fromJson(FileReader(data), ExtRestaurantData::class.java)
} catch (e: Exception) {
    throw IllegalArgumentException(INCORRECT_INPUT_DATA_FORMAT)
}