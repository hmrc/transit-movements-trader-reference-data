package config

import com.google.inject.AbstractModule
import scheduler.{DataImportJob, DataImportTrigger}

class SchedulerModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[DataImportJob]).asEagerSingleton()
    bind(classOf[DataImportTrigger]).asEagerSingleton()
  }
}
