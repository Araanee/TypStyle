const { exec, fork } = require('child_process');
const path = require('path');

console.log('Starting backend...');

const startBackend = exec('mvn quarkus:dev', { cwd: path.join(__dirname, '../../') });

startBackend.stdout.on('data', (data) => {
  console.log(`stdout: ${data}`);
});

startBackend.stderr.on('data', (data) => {
  console.error(`stderr: ${data}`);
});

startBackend.on('close', (code) => {
  console.log(`child process exited with code ${code}`);
});

startBackend.on('error', (error) => {
  console.error(`Failed to start backend process: ${error.message}`);
});

// Start the file explorer server
console.log('\nStarting file explorer server...');
const startFileExplorerServer = fork(path.join(__dirname, 'code_page', 'explorer.js'));

startFileExplorerServer.on('message', (message) => {
  console.log(`File explorer server message: ${message}`);
});

startFileExplorerServer.on('error', (error) => {
  console.error(`Failed to start file explorer server: ${error.message}`);
});

startFileExplorerServer.on('exit', (code) => {
  console.log(`File explorer server exited with code ${code}`);
});