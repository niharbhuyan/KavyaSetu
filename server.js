const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const PUBLIC_DIR = path.join(__dirname, 'public');

// In-memory cache for GitHub build status
let buildStatusCache = {
  data: null,
  timestamp: 0,
  ttlMs: 20000 // 20 seconds cache to avoid GitHub rate limits
};

function fetchGitHubBuildStatus(callback) {
  const now = Date.now();
  if (buildStatusCache.data && (now - buildStatusCache.timestamp < buildStatusCache.ttlMs)) {
    return callback(null, buildStatusCache.data);
  }

  const https = require('https');
  const options = {
    hostname: 'api.github.com',
    path: '/repos/niharbhuyan/KavyaSetu/actions/runs?per_page=10',
    headers: {
      'User-Agent': 'KavyaSetu-Status-Monitor',
      'Accept': 'application/vnd.github.v3+json'
    }
  };

  const req = https.get(options, (res) => {
    let rawData = '';
    res.on('data', chunk => { rawData += chunk; });
    res.on('end', () => {
      try {
        if (res.statusCode !== 200) {
          throw new Error(`GitHub API returned status code ${res.statusCode}`);
        }
        const json = JSON.parse(rawData);
        const runs = json.workflow_runs || [];
        if (runs.length === 0) {
          const fallback = {
            status: 'Unknown',
            health: 'unknown',
            message: 'No GitHub Actions workflow runs found',
            runs: []
          };
          buildStatusCache = { data: fallback, timestamp: now, ttlMs: 20000 };
          return callback(null, fallback);
        }

        const latest = runs[0];
        let health = 'Unknown';
        if (latest.status === 'in_progress') {
          health = 'Running';
        } else if (latest.status === 'queued' || latest.status === 'waiting' || latest.status === 'requested') {
          health = 'Queued';
        } else if (latest.status === 'completed') {
          if (latest.conclusion === 'success') {
            health = 'Passed';
          } else if (latest.conclusion === 'failure' || latest.conclusion === 'timed_out' || latest.conclusion === 'startup_failure') {
            health = 'Failed';
          } else if (latest.conclusion === 'cancelled') {
            health = 'Cancelled';
          } else {
            health = latest.conclusion || 'Completed';
          }
        }

        const formattedRuns = runs.map(r => {
          let runHealth = 'Unknown';
          if (r.status === 'in_progress') runHealth = 'Running';
          else if (r.status === 'queued') runHealth = 'Queued';
          else if (r.status === 'completed') {
            if (r.conclusion === 'success') runHealth = 'Passed';
            else if (r.conclusion === 'failure') runHealth = 'Failed';
            else runHealth = r.conclusion || 'Completed';
          }

          return {
            id: r.id,
            name: r.name,
            health: runHealth,
            status: r.status,
            conclusion: r.conclusion,
            branch: r.head_branch,
            commit: r.head_sha ? r.head_sha.substring(0, 7) : '',
            commitMessage: r.head_commit ? r.head_commit.message.split('\n')[0] : (r.display_title || ''),
            author: r.head_commit?.author?.name || r.actor?.login || 'niharbhuyan',
            htmlUrl: r.html_url,
            logsUrl: `${r.html_url}`,
            createdAt: r.created_at,
            updatedAt: r.updated_at,
            runNumber: r.run_number
          };
        });

        const result = {
          health, // 'Passed', 'Running', 'Failed', 'Queued', etc.
          latestRun: formattedRuns[0],
          repo: 'niharbhuyan/KavyaSetu',
          repoUrl: 'https://github.com/niharbhuyan/KavyaSetu',
          totalRuns: json.total_count || formattedRuns.length,
          runs: formattedRuns.slice(0, 5),
          serverTime: new Date().toISOString()
        };

        buildStatusCache = { data: result, timestamp: now, ttlMs: 20000 };
        callback(null, result);
      } catch (err) {
        callback(err, null);
      }
    });
  });

  req.on('error', (e) => {
    callback(e, null);
  });
  req.setTimeout(5000, () => {
    req.abort();
    callback(new Error('GitHub API request timed out'), null);
  });
}

process.on('uncaughtException', (err) => {
  console.error('Unhandled exception caught:', err.message);
});
process.on('unhandledRejection', (reason, promise) => {
  console.error('Unhandled rejection caught:', reason);
});

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.md': 'text/markdown; charset=utf-8',
  '.txt': 'text/plain; charset=utf-8',
  '.pem': 'application/x-pem-file',
  '.aab': 'application/octet-stream',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.apk': 'application/vnd.android.package-archive',
  '.jks': 'application/octet-stream',
};

const server = http.createServer((req, res) => {
  console.log('[HTTP_REQ]', new Date().toISOString(), req.method, req.url, 'from', req.headers['user-agent'] || 'unknown');
  res.on('error', (err) => {
    console.error('Response error:', err.message);
  });
  req.on('error', (err) => {
    console.error('Request error:', err.message);
  });

  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, HEAD, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', '*');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  let reqPath = decodeURIComponent(req.url.split('?')[0]);
  const normalizedPath = reqPath.toLowerCase().replace(/\/+$/, '');
  if (normalizedPath === '/app-ads.txt' || normalizedPath === '/ads.txt') {
    const appAdsContent = 'google.com, pub-4880243637225183, DIRECT, f08c47fec0942fa0\n';
    res.writeHead(200, {
      'Content-Type': 'text/plain; charset=utf-8',
      'Content-Length': Buffer.byteLength(appAdsContent, 'utf8'),
      'Cache-Control': 'public, max-age=3600',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, HEAD, OPTIONS',
      'X-Content-Type-Options': 'nosniff'
    });
    if (req.method === 'HEAD') {
      res.end();
      return;
    }
    res.end(appAdsContent);
    return;
  }

  if (normalizedPath === '/api/github/build-status' || normalizedPath === '/api/build-status') {
    fetchGitHubBuildStatus((err, data) => {
      if (err) {
        res.writeHead(500, {
          'Content-Type': 'application/json; charset=utf-8',
          'Access-Control-Allow-Origin': '*'
        });
        res.end(JSON.stringify({
          error: true,
          message: err.message,
          health: 'Failed',
          status: 'Error'
        }));
        return;
      }
      res.writeHead(200, {
        'Content-Type': 'application/json; charset=utf-8',
        'Cache-Control': 'public, max-age=15',
        'Access-Control-Allow-Origin': '*'
      });
      res.end(JSON.stringify(data));
    });
    return;
  }

  if (reqPath === '/' || reqPath === '') {
    reqPath = '/index.html';
  }

  const safePath = path.normalize(reqPath).replace(/^(\.\.[\/\\])+/, '');
  const filePath = path.join(PUBLIC_DIR, safePath);

  if (!filePath.startsWith(PUBLIC_DIR)) {
    res.writeHead(403, { 'Content-Type': 'text/plain' });
    res.end('Forbidden');
    return;
  }

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('File Not Found');
      return;
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';
    const filename = path.basename(filePath);

    res.setHeader('Accept-Ranges', 'bytes');
    res.setHeader('X-Content-Type-Options', 'nosniff');
    res.setHeader('Access-Control-Expose-Headers', 'Content-Length, Content-Disposition, Content-Range');

    const range = req.headers.range;
    if (range) {
      const parts = range.replace(/bytes=/, '').split('-');
      const start = parseInt(parts[0], 10);
      const end = parts[1] ? parseInt(parts[1], 10) : stats.size - 1;
      const chunksize = (end - start) + 1;
      const headers = {
        'Content-Range': `bytes ${start}-${end}/${stats.size}`,
        'Content-Length': chunksize,
        'Content-Type': contentType,
        'Cache-Control': 'public, max-age=3600',
      };
      if (ext === '.aab' || ext === '.apk' || ext === '.jks') {
        headers['Content-Disposition'] = `attachment; filename="${filename}"`;
      }
      res.writeHead(206, headers);
      const stream = fs.createReadStream(filePath, { start, end, highWaterMark: 128 * 1024 });
      stream.on('error', (e) => {
        if (!res.headersSent) res.writeHead(500);
        res.end();
      });
      req.on('close', () => {
        if (!stream.destroyed) stream.destroy();
      });
      stream.pipe(res);
      return;
    }

    const headers = {
      'Content-Type': contentType,
      'Content-Length': stats.size,
      'Cache-Control': 'public, max-age=3600',
    };
    if (ext === '.aab' || ext === '.apk' || ext === '.jks') {
      headers['Content-Disposition'] = `attachment; filename="${filename}"`;
    }
    res.writeHead(200, headers);
    if (req.method === 'HEAD') {
      res.end();
      return;
    }
    const stream = fs.createReadStream(filePath, { highWaterMark: 128 * 1024 });
    stream.on('error', (e) => {
      if (!res.headersSent) res.writeHead(500);
      res.end();
    });
    req.on('close', () => {
      if (!stream.destroyed) stream.destroy();
    });
    stream.pipe(res);
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Robust public download server running on http://0.0.0.0:${PORT}`);
});
