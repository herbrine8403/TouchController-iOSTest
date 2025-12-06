use bytemuck::cast_slice;
use jni::{
    objects::{JByteArray, JClass, JString},
    sys::{jint, jlong},
    JNIEnv,
};
use log::LevelFilter;
use oslog::OsLogger;

use crate::poller::Poller;

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_common_platform_ios_Transport_init(
    _env: JNIEnv<'_>,
    _class: JClass,
) {
    OsLogger::new("top.fifthlight.touchcontroller")
        .level_filter(LevelFilter::Info)
        .install()
        .expect("Failed to initialize iOS logger");
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_common_platform_ios_Transport_new(
    mut env: JNIEnv<'_>,
    _class: JClass,
    name: JString,
) -> jlong {
    let Ok(address) = env.get_string(&name) else {
        env.throw_new("java/lang/IllegalArgumentException", "Invalid string")
            .unwrap();
        return -1;
    };
    let Ok(address) = address.to_str() else {
        env.throw_new("java/lang/IllegalArgumentException", "Bad UTF-8 string")
            .unwrap();
        return -1;
    };
    match Poller::new(address) {
        Ok(poller) => Box::into_raw(Box::new(poller)) as jlong,
        Err(err) => {
            env.throw_new("java/io/IOException", err.to_string())
                .unwrap();
            -1
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_common_platform_ios_Transport_receive(
    mut env: JNIEnv<'_>,
    _class: JClass,
    handle: jlong,
    buffer: JByteArray,
) -> jint {
    let poller = unsafe { &mut *(handle as *mut Poller) };

    match poller.receive() {
        Ok(Some(message)) => {
            env.set_byte_array_region(buffer, 0, cast_slice(&message))
                .expect("Failed to set array region");
            message.len() as jint
        }
        Ok(None) => {
            // No data available
            0
        }
        Err(err) => {
            env.throw_new("java/io/IOException", err.to_string())
                .unwrap();
            -1
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_common_platform_ios_Transport_send(
    env: JNIEnv<'_>,
    _class: JClass,
    handle: jlong,
    buffer: JByteArray,
    off: jint,
    len: jint,
) {
    let poller = unsafe { &mut *(handle as *mut Poller) };

    let mut array = vec![0; len as usize];
    if env.get_byte_array_region(buffer, off, &mut array).is_err() {
        return;
    };

    poller.send(cast_slice(&array));
}

#[no_mangle]
pub unsafe extern "system" fn Java_top_fifthlight_touchcontroller_common_platform_ios_Transport_destroy(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) {
    let poller = unsafe { Box::from_raw(handle as *mut Poller) };
    drop(poller)
}