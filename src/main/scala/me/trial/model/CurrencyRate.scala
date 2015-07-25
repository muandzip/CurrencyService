package me.trial.model

import java.sql.Timestamp

case class CurrencyRate(code: String, name: String, rate: Double, tstamp: Timestamp)
