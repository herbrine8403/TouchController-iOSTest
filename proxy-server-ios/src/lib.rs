#![cfg(target_os = "ios")]
#![crate_type = "staticlib"]

pub mod jni;
pub mod poller;

// 确保一些基本的 C 兼容性符号
#[no_mangle]
pub extern "C" fn touchcontroller_ensure_symbols() {
    // 空函数，仅用于确保符号表正常
}