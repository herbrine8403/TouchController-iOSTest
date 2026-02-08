use std::io::{ErrorKind, Result};
use std::os::unix::net::UnixDatagram;
use std::path::Path;

pub struct Poller {
    socket: UnixDatagram,
}

impl Poller {
    pub fn new(address: &str) -> Result<Self> {
        let path = Path::new(address);
        
        // Create unbound socket
        let socket = UnixDatagram::unbound()?;
        
        // Try to connect to existing socket
        if socket.connect(path).is_err() {
            // Connection failed, but we still have an unbound socket
            // This is expected when the socket doesn't exist yet
        }
        
        Ok(Poller { socket })
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