package ergonames.Sdk

import scalaj.http._
import spray.json._
import DefaultJsonProtocol._

object ErgoNamesSdk {

  val ergonamesMainnetAPIUrl: String = "https://api.ergonames.com"
  val ergonamesTestnetAPIUrl: String = "https://testnet-api.ergonames.com"

  def createUrl(name: String): String = {
    val url = ergonamesTestnetAPIUrl + "/ergonames/resolve/" + name
    url
  }

  def makeRequest(url: String): String = {
    val response = Http(url).asString
    val body = response.body
    body
  }

  def parseResponse(response: String): Map[String, Option[String]] = {
    val json = response.parseJson
    val mappedData = json.convertTo[Map[String, Option[String]]]
    mappedData
  }

  def parseOption(raw: Option[String]): String = {
    if (raw == None) {
      "None"
    } else {
      raw.get
    }
  }

  def getOwnerAddress(name: String): String = {
    val url = createUrl(name)
    val response = makeRequest(url)
    val json = parseResponse(response)
    val addressRaw = json.get("ergo").get
    val address = parseOption(addressRaw)
    address
  }

  def checkNameExists(name: String): Boolean = {
    val address = getOwnerAddress(name)
    if (address == "None") {
      false
    } else {
      true
    }
  }
}