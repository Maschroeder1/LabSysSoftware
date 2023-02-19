package model

class CouldNotParseException : RuntimeException()

class CouldNotGetUfrgsPageException(message: String) : RuntimeException(message)

class OutdatedCookieException : RuntimeException()

class NoPossibilitiesException : RuntimeException()