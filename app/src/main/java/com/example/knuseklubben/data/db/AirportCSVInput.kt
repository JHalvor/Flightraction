package com.example.knuseklubben.data.db

import android.annotation.SuppressLint
import android.content.Context
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.AirportType
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/*
    Aiport.id and Airport.ident (Airport ICAO code) is unique and never NULL

    The function recieves a fileName and opens a csv in a inputStream that
    takes input from the csv and converts it to Airport objects in a series of functions
 */

/** Singleton object to retrieve Airport data from CSV
 */
@SuppressLint("StaticFieldLeak")
object AirportCSVInput {
    private var context: Context? = null
    private var airports: List<Airport>? = null
    private const val NUMBER_OF_REGEX_ELEMENT_TO_SCAN: Int = 14

    /** Sets the conext to be used for reading the airport csv
     * @param newContext context of the application
     */
    fun setContext(newContext: Context) {
        context = newContext
    }

    /** Private, sets airport that is read to the list of airports to return in readAirportCSV
     * @param newAirports list of airports to be returned in readAirportCsv
     */
    private fun setAirports(newAirports: List<Airport>) {
        airports = newAirports
    }

    /** Returns airports read from csv
     */
    internal fun getAirports(): List<Airport>? {
        if (airports.isNullOrEmpty()) {
            readAirportCsv()
        }
        return airports
    }

    /** Reads airports from the airport csv and places it in a private airports list that can be
     * retrieved by getAirports
     */
    internal fun readAirportCsv() {
        val context = context ?: throw IllegalStateException("Context for AirportCsvReader not set")
        val airports: List<Airport>

        // Getting inputStream from csv file in resources
        val inputStream = try {
            context.resources.openRawResource(R.raw.airports)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }

        // Getting buffered reader from inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))

        try {
            reader.readLine()
            airports = reader.lineSequence()
                .filter { it.isNotBlank() }
                .map {
                    /**
                     * DESCRIPTION OF CSV FORMATTING TO AIRPORT OBJECTS
                     * The split function splits a line of the CSV data by commas, as long as the comma is not wrapped in double quotes.
                     *
                     * The regex used to achieve this is ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".
                     *
                     * This regex uses a positive lookahead to match commas
                     * only if they are followed by an even number of quotes
                     * (which means the comma is not within quotes),
                     * up until the end of the line.
                     *
                     * The take function is used to limit the number of fields parsed from each
                     * line, since the Airport class only requires 14 of the 18 available fields.
                     *
                     * The map function is then used to remove the double quotes that
                     * surround some of the fields.
                     *
                     * The chunked function is used to group the fields into lists of 5,
                     * which are then destructured into separate variables for use in
                     * creating Airport objects,
                     * because kotlin allows max 5 elements in destructuring declarations.
                     *
                     * The filter function takes only lines that is not blank lines.
                     *
                     * The lineSqeuence function reads a line by line of the csv.
                     */

                    val list = it.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                        .take(NUMBER_OF_REGEX_ELEMENT_TO_SCAN)
                        .map { item -> item.removeSurrounding("\"") }
                        .chunked(5)

                    val (id, ident, type, name, latDeg) = list[0]

                    val (longDeg, elevationFeet, continent, isoCountry, isoRegion) = list[1]

                    val (municipality, scheduledService, gpsCode, iataCode) = list[2]
                    Airport(
                        id.toIntOrNull() ?: 0,
                        ident.trimStart().trimEnd(),
                        AirportType.valueOf(type.uppercase(Locale.getDefault())),
                        name,
                        latDeg.toDoubleOrNull() ?: 0.0,
                        longDeg.toDoubleOrNull() ?: 0.0,
                        elevationFeet.toIntOrNull() ?: 0,
                        continent,
                        isoCountry,
                        isoRegion,
                        municipality,
                        scheduledService == "yes",
                        gpsCode,
                        iataCode.trimStart().trimEnd()
                    )
                }.toList()

            // Setting the airports to airport list for return
            setAirports(airports)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Closing the reader and inputStream
        finally {
            try {
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
