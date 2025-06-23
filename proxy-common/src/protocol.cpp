#include "protocol.hpp"

namespace touchcontroller {
namespace protocol {

bool deserialize_event(ProxyMessage& message, const std::vector<uint8_t> data) {
    const uint8_t* ptr = data.data();
    const uint8_t* end = data.data() + data.size();

    if (end - ptr < sizeof(uint32_t)) {
        std::cerr << "Not enough data to read message type" << std::endl;
        return false;
    }

    uint32_t raw_type = *reinterpret_cast<const uint32_t*>(ptr);
    ptr += sizeof(uint32_t);
    ProxyMessage::Type type = static_cast<ProxyMessage::Type>(ntohl(raw_type));

    switch (type) {
        case ProxyMessage::Vibrate: {
            message.type = type;
            return true;
        }
        case ProxyMessage::KeyboardShow: {
            message.type = type;
            if (end - ptr < sizeof(uint8_t)) {
                std::cerr << "Not enough data to read keyboard show flag" << std::endl;
                return false;
            }
            message.keyboard_show.show = (*ptr++ != 0);
            return true;
        }
        default: {
            return false;
        }
    }
}

}
}  // namespace touchcontroller