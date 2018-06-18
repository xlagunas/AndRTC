package cat.xlagunas.data.push

import java.lang.Exception

class TokenNotFoundException : Exception("Push token not found. Firebase provider returned null")