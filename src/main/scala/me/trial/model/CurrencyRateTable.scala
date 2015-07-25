package me.trial.model

import java.sql.Timestamp

import slick.driver.H2Driver.api._

class CurrencyRateTable(tag: Tag) extends Table[CurrencyRate](tag,"CURRENCY_RATES") {
  def code = column[String]("CODE")
  def name = column[String]("NAME")
  def rate = column[Double]("RATE")
  def tstamp = column[Timestamp]("LAST_DTTM", O.SqlType("timestamp default now()"))
  def * = (code, name, rate, tstamp) <> (CurrencyRate.tupled, CurrencyRate.unapply)
}
object CurrencyRateTable {
  val currencyRateQuery = TableQuery[CurrencyRateTable]
  private def maxTimestampByCode(_code: String) =
    currencyRateQuery
      .filter(_.code === _code)
      .groupBy(_.code)
      .map {
        case (code, ua) =>
          code -> ua.map(_.tstamp).max
      }
  def currencyRateByCode(code: String) = for {
    (c, f) <- currencyRateQuery join maxTimestampByCode(code) on ( (base, lookup) =>
      base.code === lookup._1 && base.tstamp === lookup._2
    )
  } yield c
}
