const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const { exec } = require('child_process');
const fs = require('fs');

function createWindow() {
  console.log("okk");
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      preload: path.join(__dirname, '..','preload.js'),
      nodeIntegration: false,
      contextIsolation: true
    }
  });

  win.loadFile(path.join(__dirname,'index.html'));
}

app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
      document.getElementById("text-editor").innerHTML = localStorage.getItem("lastFile");
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
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

})

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
;
