package com.example.googleapi.Modelo

data class DatosParaEnviar(
    val apiKey: String,
    val destinations: String,
    val origins: String,
    val units: String
)

// Modelo para la respuesta JSON que recibirás
data class RespuestaJson(
    val destination_addresses: List<String>,
    val origin_addresses: List<String>,
    val rows: List<Row>,
    val status: String
)

data class Row(
    val elements: List<Element>
)

data class Element(
    val distance: Distance,
    val duration: Duration,
    val status: String
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)