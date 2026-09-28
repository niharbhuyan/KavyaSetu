import http.server
import socketserver
import os
import sys
import datetime

PORT = 3000
DIRECTORY = "/app/applet/downloads"
LOG_FILE = "/tmp/downloads_access.log"

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def log_message(self, format, *args):
        log_entry = f"[{datetime.datetime.utcnow().isoformat()}] {self.client_address[0]} - {format % args}\n"
        sys.stderr.write(log_entry)
        sys.stderr.flush()
        try:
            with open(LOG_FILE, "a") as f:
                f.write(log_entry)
        except Exception:
            pass

    def end_headers(self):
        # Enable CORS and byte range requests for large file downloads
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, HEAD, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', '*')
        self.send_header('Cache-Control', 'no-store, no-cache, must-revalidate, max-age=0')
        self.send_header('Pragma', 'no-cache')
        self.send_header('Expires', '0')
        self.send_header('Accept-Ranges', 'bytes')

        # Add attachment Content-Disposition for AAB bundles
        path = self.translate_path(self.path)
        if path.endswith('.aab'):
            filename = os.path.basename(path)
            self.send_header('Content-Disposition', f'attachment; filename="{filename}"')
            self.send_header('Content-Type', 'application/octet-stream')

        super().end_headers()

class ReusableTCPServer(socketserver.TCPServer):
    allow_reuse_address = True

if __name__ == '__main__':
    os.chdir(DIRECTORY)
    with ReusableTCPServer(("0.0.0.0", PORT), Handler) as httpd:
        print(f"Serving downloads at http://0.0.0.0:{PORT}")
        httpd.serve_forever()
