use std::io::{Error, ErrorKind, Result};
use std::os::unix::net::UnixDatagram;
use std::path::Path;

pub struct Poller {
    socket: UnixDatagram,
}

impl Poller {
    pub fn new(address: &str) -> Result<Self> {
        let path = Path::new(address);
        
        // Try to connect to existing socket
        match UnixDatagram::unbound()?.connect(path) {
            Ok(socket) => Ok(Poller { socket }),
            Err(_) => {
                // If connection fails, create new socket
                let socket = UnixDatagram::unbound()?;
                Ok(Poller { socket })
            }
        }
    }

    pub fn receive(&self) -> Result<Option<Vec<u8>>> {
        let mut buffer = vec![0u8; 4096];
        
        match self.socket.recv(&mut buffer) {
            Ok(size) => {
                buffer.truncate(size);
                Ok(Some(buffer))
            }
            Err(e) if e.kind() == ErrorKind::WouldBlock => {
                Ok(None)
            }
            Err(e) => Err(e),
        }
    }

    pub fn send(&self, data: &[u8]) {
        let _ = self.socket.send(data);
    }
}

impl Drop for Poller {
    fn drop(&mut self) {
        let _ = self.socket.shutdown(std::net::Shutdown::Both);
    }
}