#![cfg(target_os = "ios")]
#![crate_type = "staticlib"]

pub mod jni;
pub mod poller;

// 链接系统库以确保静态库能被正确加载
#[link(name = "c")]
extern {}
