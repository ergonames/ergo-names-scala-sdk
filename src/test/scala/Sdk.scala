import ergonames.Sdk.ErgoNamesSdk._
import org.scalatest.funsuite.AnyFunSuite
import ergonames.Sdk.BalanceToken


class ErgoNamesSdkTest extends AnyFunSuite {

    val name = "~balb"
    val address = "3WwKzFjZGrtKAV7qSCoJsZK9iJhLLrUa3uwd4yw52bVtDVv6j5TL"

    test("ResolveErgoname") {
        assert(resolveErgoname(name).get === "3WwKzFjZGrtKAV7qSCoJsZK9iJhLLrUa3uwd4yw52bVtDVv6j5TL")
    }

    test("CheckCheckNameValid") {
        assert(check_name_valid(name) === true)
    }

    test("CheckCheckAlreadyRegistered") {
        assert(check_already_registered(name) === true)
    }

    test("CheckCheckNamePrice") {
        assert(check_name_price(name) === 0)
    }

    test("CheckGetBlockIdRegistered") {
        assert(get_block_id_registered(name).get === "a5e0ab7f95142ceee7f3b6b5a5318153b345292e9aaae7c56825da115e196d08")
    }

    test("CheckGetBlockRegistered") {
        assert(get_block_registered(name).get === 60761)
    }

    test("CheckGetTimeStampRegistered") {
        assert(get_timestamp_registered(name).get === 1656968987794L)
    }

    test("CheckDateRegistered") {
        assert(get_date_registered(name).get === "07/04/2022")
    }

    test("CheckTotalAmountOwned") {
        assert(get_total_amount_owned(address).get === 1)
    }

    test("CheckReverseSearch") {
        val tk = BalanceToken("2b41b93d22a46de0b0ed9c8b814b766298adbf2ff304f83ee2426f47ac33d9b8", 1, "~balb")
        val arr = Array(tk)
        assert(reverse_search(address).get === arr)
    }

}