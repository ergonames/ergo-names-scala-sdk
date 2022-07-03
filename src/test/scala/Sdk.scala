import ergonames.Sdk.ErgoNamesSdk._
import org.scalatest.funsuite.AnyFunSuite
import ergonames.Sdk.BalanceToken


class ErgoNamesSdkTest extends AnyFunSuite {

    val name = "~balb"
    val address = "3WwKzFjZGrtKAV7qSCoJsZK9iJhLLrUa3uwd4yw52bVtDVv6j5TL"

    test("ResolveErgoname") {
        assert(resolveErgoname(name) === "3WwKzFjZGrtKAV7qSCoJsZK9iJhLLrUa3uwd4yw52bVtDVv6j5TL")
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
        assert(get_block_id_registered(name) === "155b44501f6f45976623ad5b01f207434daef86a35b5efdec36cde70ef55f3c6")
    }

    test("CheckGetBlockRegistered") {
        assert(get_block_registered(name) === 205710)
    }

    test("CheckGetTimeStampRegistered") {
        assert(get_timestamp_registered(name) === 1650222939771L)
    }

    test("CheckDateRegistered") {
        assert(get_date_registered(name) === "04/17/2022")
    }

    test("CheckTotalAmountOwned") {
        assert(get_total_amount_owned(address) === 1)
    }

    test("CheckReverseSearch") {
        val tk = BalanceToken("a22bfbc3545ba99b8a8e4f9fe7841fa7a93e44c6f1e1bcebc65f3ef464b108b1", 1, "~balb")
        val arr = Array(tk)
        assert(reverse_search(address) === arr)
    }

}