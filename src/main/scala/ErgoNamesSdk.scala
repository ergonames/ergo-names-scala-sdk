package ergonames.Sdk

import scalaj.http._
import spray.json._

import java.util.Date
import java.text.SimpleDateFormat

case class Token (
  id: String,
  boxId: String,
  emissionAmount: Int,
  name: String,
  description: String,
  decimals: Int,
)

case class BalanceToken (
  tokenId: String,
  amount: BigInt,
  name: String,
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

case class BoxByBoxId (
  boxId: String,
  blockId: String,
  address: String,
  creationHeight: Int,
)

case class BoxByTokenId (
  id: String,
  boxId: String,
)

case class Transaction (
  headerId: String,
  boxId: String,
)

case class TransactionsResponse (
  items: List[Transaction],
  total: Int,
)

case class BlockHeader (
  id: String,
  height: Int,
  timestamp: BigInt,
)

case class BlockMain (
  header: BlockHeader,
)

case class Block (
  block: BlockMain,
)

case class Address (
  nanoErgs: BigInt,
  tokens: List[BalanceToken],
)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val tokenFormat: RootJsonFormat[Token] = jsonFormat6(Token)
  implicit val balanceTokenFormat: RootJsonFormat[BalanceToken] = jsonFormat3(BalanceToken)
  implicit val tokensResponseFormat: RootJsonFormat[TokensResponse] = jsonFormat2(TokensResponse.apply)
  implicit val boxFormat: RootJsonFormat[Box] = jsonFormat4(Box)
  implicit val boxByBoxIdFormat: RootJsonFormat[BoxByBoxId] = jsonFormat4(BoxByBoxId)
  implicit val blockHeaderFormat: RootJsonFormat[BlockHeader] = jsonFormat3(BlockHeader)
  implicit val blockMainFormat: RootJsonFormat[BlockMain] = jsonFormat1(BlockMain)
  implicit val blockFormat: RootJsonFormat[Block] = jsonFormat1(Block)
  implicit val boxByTokenIdFormat: RootJsonFormat[BoxByTokenId] = jsonFormat2(BoxByTokenId)
  implicit val transactionFormat: RootJsonFormat[Transaction] = jsonFormat2(Transaction)
  implicit val transactionsResponseFormat: RootJsonFormat[TransactionsResponse] = jsonFormat2(TransactionsResponse)
  implicit val addressFormat: RootJsonFormat[Address] = jsonFormat2(Address)
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

  def reverse_search(address: String): Array[BalanceToken] = {
    val address_data = convert_address_tokens_to_array(address)
    val correct_names = remove_wrong_named_tokens(address_data)
    val correct_mint = remove_wrong_address_tokens(correct_names)
    correct_mint
  }

  def get_total_amount_owned(address: String): Int = {
    val array = reverse_search(address)
    array.length
  }

  def check_name_price(name: String): Int = {
    0
  }

  def get_block_id_registered(name: String): String = {
    val token_array = convert_token_info_to_array(name)
    val token_id = get_asset_minted_at_address(token_array)
    val minting_box_id = get_minting_box_id_by_token_id(token_id)
    val block_id = get_block_id_for_box_by_id(minting_box_id)
    block_id
  }

  def get_block_registered(name: String): Int = {
    val token_array = convert_token_info_to_array(name)
    val token_id = get_asset_minted_at_address(token_array)
    val minting_box_id = get_minting_box_id_by_token_id(token_id)
    val height = get_block_for_box_by_id(minting_box_id)
    height
  }

  def get_timestamp_registered(name: String): BigInt = {
    val token_array = convert_token_info_to_array(name)
    val token_id = get_asset_minted_at_address(token_array)
    val minting_box_id = get_minting_box_id_by_token_id(token_id)
    val block_id = get_block_id_for_box_by_id(minting_box_id)
    val timestamp = get_timestamp_for_block_by_id(block_id)
    timestamp
  }

  def get_date_registered(name: String): String = {
    val timestamp = get_timestamp_registered(name)
    val date_format = new SimpleDateFormat("MM/dd/yyyy")
    val formatted_date = date_format.format(timestamp)
    formatted_date
  }

  def reformat_name(name: String): String = {
    name.toLowerCase()
  }

  def check_name_valid(name: String): Boolean = {
    for (c <- name) {
      val code = c.toInt
      if (code <= 44) {
        return false
      } else if (code == 47) {
        return false
      } else if (code >= 58 && code <= 94) {
        return false
      } else if (code == 96) {
        return false
      } else if (code >= 123 && code <= 125) {
        return false
      } else if (code >= 127) {
        return false
      }
    }
    true
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

  def get_minting_box_id_by_token_id(token_id: String): String = {
    val url: String = EXPLORER_URL + "api/v1/tokens/" + token_id
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val box = json.convertTo[BoxByTokenId]
    box.boxId
  }

  def get_block_id_for_box_by_id(box_id: String): String = {
    val url: String = EXPLORER_URL + "api/v1/boxes/" + box_id
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val box = json.convertTo[BoxByBoxId]
    box.blockId
  }

  def get_block_for_box_by_id(box_id: String): Int = {
    val url: String = EXPLORER_URL + "api/v1/boxes/" + box_id
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val box = json.convertTo[BoxByBoxId]
    box.creationHeight
  }

  def get_timestamp_for_block_by_id(block_id: String): BigInt = {
    val url: String = EXPLORER_URL + "api/v1/blocks/" + block_id
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val timestamp = json.convertTo[Block].block.header.timestamp
    timestamp
  }

  def get_address_for_box_id(box_id: String): String = {
    val url: String = EXPLORER_URL + "api/v1/boxes/" + box_id
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val boxJson = json.convertTo[Box]
    boxJson.address
  }

  def create_address_data(address: String): List[BalanceToken] = {
    val url: String = EXPLORER_URL + "api/v1/addresses/" + address + "/balance/confirmed"
    val response = Http(url).asString
    val body = response.body
    val json = body.parseJson
    val addressJson = json.convertTo[Address]
    val tokens = addressJson.tokens
    tokens
  }

  def convert_address_tokens_to_array(address: String): Array[BalanceToken] = {
    val tokens = create_address_data(address)
    val token_array = new Array[BalanceToken](tokens.length)
    for (i <- 0 until tokens.length) {
      token_array(i) = tokens(i)
    }
    token_array
  }

  def remove_wrong_named_tokens(token_array: Array[BalanceToken]): Array[BalanceToken] = {
    val token_array_new = new Array[BalanceToken](token_array.length)
    var i = 0
    for (j <- 0 until token_array.length) {
      if (check_name_valid(token_array(j).name)) {
        token_array_new(i) = token_array(j)
        i = i + 1
      }
    }
    val token_array_new_new = new Array[BalanceToken](i)
    for (j <- 0 until i) {
      token_array_new_new(j) = token_array_new(j)
    }
    token_array_new_new
  }

  def check_correct_minting_address(token_array: Array[BalanceToken]): Boolean = {
    for (i <- token_array.indices) {
      val token_id = token_array(i).tokenId
      val box_id = get_minting_box_id_by_token_id(token_id)
      val address = get_address_for_box_id(box_id)
      if (address == MINT_ADDRESS) {
        return true
      }
    }
    false
  }

  def remove_wrong_address_tokens(token_array: Array[BalanceToken]): Array[BalanceToken] = {
    val token_array_new = new Array[BalanceToken](token_array.length)
    var i = 0
    for (j <- 0 until token_array.length) {
      if (check_correct_minting_address(token_array)) {
        token_array_new(i) = token_array(j)
        i = i + 1
      }
    }
    val token_array_new_new = new Array[BalanceToken](i)
    for (j <- 0 until i) {
      token_array_new_new(j) = token_array_new(j)
    }
    token_array_new_new
  }
}