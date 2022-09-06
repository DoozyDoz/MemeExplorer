package com.example.memeexplorer.common.domain.model

import java.io.IOException

class NoMoreMemesException(message: String): Exception(message)

class NetworkUnavailableException(message: String = "No network available :(") : IOException(message)

class NetworkException(message: String): Exception(message)