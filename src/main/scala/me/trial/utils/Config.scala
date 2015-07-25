package me.trial.utils

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()
  val dataProviderConfig = config.getConfig("externalWS")
  val serviceConfig = config.getConfig("service")
}
