package ergonames.Sdk

import scalaj.http._
import spray.json._

case class Token (
  id: String,
  boxId: String,
  emissionAmount: Int,
  name: String,
  description: String,
  decimals: Int,
)

case class TokensResponse (
  items: List[Token],
  total: Int,
)
object TokensResponse

case class Box (
  boxId: String,
  transactionId: String,
  blockId: String,
  address: String,
)

case class Transaction (
  headerId: String,
  boxId: String,
)

case class TransactionsResponse (
  items: List[Transaction],
  total: Int,
)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val tokenFormat: RootJsonFormat[Token] = jsonFormat6(Token)
  implicit val tokensResponseFormat: RootJsonFormat[TokensResponse] = jsonFormat2(TokensResponse.apply)
  implicit val boxFormat: RootJsonFormat[Box] = jsonFormat4(Box)
  implicit val transactionFormat: RootJsonFormat[Transaction] = jsonFormat2(Transaction)
  implicit val transactionsResponseFormat: RootJsonFormat[TransactionsResponse] = jsonFormat2(TransactionsResponse)
}

import MyJsonProtocol._

object ErgoNamesSdk {

  val EXPLORER_URL: String = "https://api-testnet.ergoplatform.com/"
  val MINT_ADDRESS: String = "3WwKzFjZGrtKAV7qSCoJsZK9iJhLLrUa3uwd4yw52bVtDVv6j5TL"

  def resolveErgoname(name: String): String = {
    val token_array = convert_token_info_to_array(name)
    val token_id = get_asset_minted_at_address(token_array)
    val last_transaction = get_last_transaction_for_token_by_id(token_id)
    val box_id = last_transaction.boxId
    val address = get_address_for_box_id(box_id)
    address
  }

  def check_already_registered(name: String): Boolean = {
    val resolved = resolveErgoname(name)
    if (resolved == "None") {
      return false
    } else {
      return true
    }
  }

  def reverse_search(address: String): String = {
    "todo"
  }

  def get_total_amount_owned(address: String): String = {
    "todo"
  }

  def check_name_price(name: String): String = {
    "todo"
  }

  def get_block_id_registered(name: String): String = {
    "todo"
  }

  def get_block_registered(name: String): String = {
    "todo"
  }

  def get_timestamp_registered(name: String): String = {
    "todo"
  }

  def get_date_registered(name: String): String = {
    "todo"
  }

  def reformat_name(name: String): String = {
    "todo"
  }

  def check_name_valid(name: String): String = {
    "todo"
  }

  def get_token_info(name: String): TokensResponse = {
    val url: String = EXPLORER_URL + "api/v1/tokens/search?query=" + name
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val tokensResponseJson = json.convertTo[TokensResponse]
    tokensResponseJson
  }

  def convert_token_info_to_array(name: String): Array[Token] = {
    val token_info = get_token_info(name)
    val token_array = new Array[Token](token_info.total)
    for (i <- 0 until token_info.total) {
      token_array(i) = token_info.items(i)
    }
    token_array
  }

  def get_asset_minted_at_address(token_array: Array[Token]): String = {
    for (i <- token_array.indices) {
      val box_address = get_address_for_box_id(token_array(i).boxId)
      if (box_address == MINT_ADDRESS) {
        return token_array(i).id
      }
    }
    "None"
  }

  def get_last_transaction_for_token_by_id(token_id: String): Transaction = {
    val url: String = EXPLORER_URL + "api/v1/assets/search/byTokenId?query=" + token_id + "&limit=1"
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val transactionsResponseJson = json.convertTo[TransactionsResponse]
    transactionsResponseJson.items.head
  }

  def get_address_for_box_id(box_id: String): String = {
    val url: String = EXPLORER_URL + "api/v1/boxes/" + box_id
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val boxJson = json.convertTo[Box]
    boxJson.address
  }
}