var http = require('http');
var url = require('url');

HOST = null;

var host = process.env.VCAP_APP_HOST || 'localhost';
var port = process.env.VCAP_APP_PORT || 3000

http.createServer(function (req, res) {
  res.writeHead(200, {'Content-Type': 'text/html'});
  res.end('running version ' + process.version);
}).listen(port, null);

console.log('Server running at http://' + host + ':' + port + '/');
