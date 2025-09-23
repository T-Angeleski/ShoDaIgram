"""Get IGDB access token"""

import os

import httpx
from dotenv import load_dotenv

load_dotenv()

# Your credentials from Twitch Developer Console
CLIENT_ID = os.getenv("IGDB_CLIENT_ID", "")
CLIENT_SECRET = os.getenv("IGDB_CLIENT_SECRET", "")
URL = "https://id.twitch.tv/oauth2/token"


def get_access_token():
    data = {
        "client_id": CLIENT_ID,
        "client_secret": CLIENT_SECRET,
        "grant_type": "client_credentials",
    }

    print(data)

    client = httpx.Client()
    response = client.post(URL, data=data)

    if response.status_code == 200:
        token_data = response.json()
        print("✅ Success!")
        print(f"Access Token: {token_data['access_token']}")
        expires_in = token_data["expires_in"]
        print(f"Expires in: {expires_in} seconds ({expires_in // 3600} hours)")
        client.close()
        return token_data["access_token"]
    else:
        print(f"❌ Error {response.status_code}: {response.text}")
        client.close()
        return None


if __name__ == "__main__":
    token = get_access_token()
