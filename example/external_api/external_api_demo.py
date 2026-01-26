import time
import uuid
import hmac
import hashlib
import requests
import base64


def sha256_base64(data: str) -> str:
    """
    计算 sha256 base64
    """
    hash_bytes = hashlib.sha256(data.encode("utf-8")).digest()
    return base64.b64encode(hash_bytes).decode("utf-8")


def sign_hmac_sha256_hex(data: str, secret: str) -> str:
    """
    HmacSHA256 签名, hex 小写
    """
    mac = hmac.new(
        secret.encode("utf-8"),
        data.encode("utf-8"),
        digestmod=hashlib.sha256
    )
    return mac.hexdigest()


def build_sign_content(
        uri: str,
        app_key: str,
        timestamp: str,
        nonce: str,
        body: str
) -> str:
    body_hash = sha256_base64(body)

    return "\n".join([
        uri,
        app_key,
        timestamp,
        nonce,
        body_hash
    ])


def call_external_api():
    url = "http://127.0.0.1:19420/test/external"
    uri = "/test/external"

    app_key = "app_key"
    api_secret = "api_secret"

    body = '{"orderId":123,"amount":99.9}'

    timestamp = str(int(time.time() * 1000))
    nonce = uuid.uuid4().hex

    sign_content = build_sign_content(
        uri,
        app_key,
        timestamp,
        nonce,
        body
    )

    signature = sign_hmac_sha256_hex(sign_content, api_secret)

    headers = {
        "Content-Type": "application/json",
        "X-App-Key": app_key,
        "X-Timestamp": timestamp,
        "X-Nonce": nonce,
        "X-Signature": signature
    }

    resp = requests.post(url, data=body, headers=headers, timeout=500)

    print("status:", resp.status_code)
    print("response:", resp.text)


if __name__ == "__main__":
    call_external_api()
