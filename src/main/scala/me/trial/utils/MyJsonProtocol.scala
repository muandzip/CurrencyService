package me.trial.utils

import java.sql.Timestamp
import spray.json._

object MyJsonProtocol extends DefaultJsonProtocol {
  import me.trial.model._

  implicit object currencyRateFormat extends JsonFormat[CurrencyRate] {
    def write(c: CurrencyRate) = JsObject(
      "code" -> JsString(c.code),
      "name" -> JsString(c.name),
      "rate" -> JsNumber(c.rate),
      "tstamp" -> JsString(c.tstamp.toString)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("code", "name", "rate") match {
        case Seq(JsString(code), JsString(name), JsNumber(rate)) =>
          new CurrencyRate(code, name, rate.toDouble, new Timestamp(System.currentTimeMillis))
        case _ => throw new DeserializationException("Currency record expected")
      }
    }
  }
}