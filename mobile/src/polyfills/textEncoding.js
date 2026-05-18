/**
 * Hermes does not expose TextEncoder; qrcode (used by react-native-qrcode-svg) requires it.
 */
if (typeof global.TextEncoder === 'undefined') {
  global.TextEncoder = class TextEncoder {
    encode(input = '') {
      const str = String(input);
      const utf8 = unescape(encodeURIComponent(str));
      const bytes = new Uint8Array(utf8.length);
      for (let i = 0; i < utf8.length; i++) {
        bytes[i] = utf8.charCodeAt(i);
      }
      return bytes;
    }
  };
}

if (typeof global.TextDecoder === 'undefined') {
  global.TextDecoder = class TextDecoder {
    decode(input) {
      const bytes = input instanceof Uint8Array ? input : new Uint8Array(input);
      let str = '';
      for (let i = 0; i < bytes.length; i++) {
        str += String.fromCharCode(bytes[i]);
      }
      return decodeURIComponent(escape(str));
    }
  };
}
