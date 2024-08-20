const { app, BrowserWindow } = require('electron');
const path = require('path');
const { exec } = require('child_process');
const fs = require('fs');
const { dialog, ipcMain } = require('electron');
const { spawn } = require('child_process');
let mainWindow = null;

// Handle creating/removing shortcuts on Windows when installing/uninstalling.
if (require('electron-squirrel-startup')) {
  app.quit();
}

const createWindow = () => {
  // Create the browser window.
  const main = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js'),
      enableRemoteModule: false,
    },
  });
  mainWindow = main;

  // and load the index.html of the app.
  mainWindow.loadFile(path.join(__dirname, 'index.html'));
};

const startBackend = () => {
  // Determine the correct path to the backend script
  const isPackaged = app.isPackaged;
  const backendScriptPath = isPackaged
    ? path.join(process.resourcesPath, 'src', 'start-backend.js')
    : path.join(__dirname, 'start-backend.js');
  
  console.log('Launching backend...');
  const backendProcess = spawn('node', [backendScriptPath], { cwd: path.dirname(backendScriptPath) });

  backendProcess.stdout.on('data', (data) => {
    console.log(`Backend stdout: ${data}`);
  });

  backendProcess.stderr.on('data', (data) => {
    console.error(`Backend stderr: ${data}`);
  });

  backendProcess.on('exit', (code) => {
    console.log(`Backend process exited with code ${code}`);
  });

  backendProcess.on('error', (error) => {
    console.error(`Failed to start backend process: ${error.message}`);
  });
};

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.whenReady().then(() => {
  startBackend();
  createWindow();

  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

// Quit when all windows are closed, except on macOS. There, it's common
// for applications and their menu bar to stay active until the user quits
// explicitly with Cmd + Q.
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and import them here.
ipcMain.handle('show-open-dialog', async (event, options) => {
  const result = await dialog.showOpenDialog(options);
  return result;
});

function parseTypstError(stderr) {
  const lineMatch = stderr.match(/input\.typ:(\d+):\d+/);
  return lineMatch ? parseInt(lineMatch[1], 10) : null;
}

function compileTypst(code) {
  return new Promise((resolve, reject) => {
    const inputFilePath = path.join(__dirname, 'input.typ');
    const outputFilePath = path.join(__dirname, 'output.pdf');

    fs.writeFileSync(inputFilePath, code);

    const compileCommand = `typst compile ${inputFilePath} ${outputFilePath}`;

    exec(compileCommand, (error, stdout, stderr) => {
      if (error) {
        console.error(`Compilation error: ${stderr}`);
        const errorLine = parseTypstError(stderr);
        return reject({ message: `Error compiling Typst code: ${stderr}`, line: errorLine });
      }
      console.log(`PDF generated at: ${outputFilePath}`);
      resolve(outputFilePath);
    });
  });
}

ipcMain.handle('compile-typst', async (event, code) => {
  try {
    const pdfPath = await compileTypst(code);
    return { pdfPath };
  } catch (error) {
    console.error('Error in compile-typst handler:', error);
    return { error: error.message, line: error.line };
  }
});

ipcMain.handle('save-file', async (event, content, filePath) => {
  return new Promise((resolve, reject) => {
    fs.writeFile(filePath, content, (err) => {
      if (err) {
        reject('Error saving file: ' + err);
      } else {
        resolve('File saved successfully');
      }
    });
  });
});
