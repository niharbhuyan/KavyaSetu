import http.server
import socketserver
import os

PORT = 3000
DIRECTORY = "/app/applet/downloads"

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        # Enable CORS and byte range requests for large file downloads
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Cache-Control', 'no-cache')
        super().end_headers()

class ReusableTCPServer(socketserver.TCPServer):
    allow_reuse_address = True

if __name__ == '__main__':
    os.chdir(DIRECTORY)
    with ReusableTCPServer(("0.0.0.0", PORT), Handler) as httpd:
        print(f"Serving downloads at http://0.0.0.0:{PORT}")
        httpd.serve_forever()
