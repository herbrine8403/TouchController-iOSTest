pub mod jni;
pub mod poller;

use log::LevelFilter;

#[cfg(target_os = "ios")]
use oslog::OsLogger;

pub fn init_logger() {
    #[cfg(target_os = "ios")]
    {
        OsLogger::new("top.fifthlight.touchcontroller")
            .level_filter(LevelFilter::Info)
            .init()
            .expect("Failed to initialize iOS logger");
    }
    #[cfg(not(target_os = "ios"))]
    {
        env_logger::Builder::new()
            .filter_level(LevelFilter::Info)
            .init();
    }
}