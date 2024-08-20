const { ipcRenderer } = window.electron;

let openFiles = [];
let ActualFile;

document.addEventListener('DOMContentLoaded', () => {   
  
  const editor = CodeMirror.fromTextArea(document.getElementById('text-editor'), {
    lineNumbers: true,
    mode: 'text/plain', // Utilisez 'text/plain' ou créez un mode personnalisé pour Typst
    theme: 'default'
  });

  const pdfViewer = document.getElementById('pdf-viewer');
  const compileButton = document.querySelector('.buttonCompile');

  const openFilesList = document.getElementById('open-files-list');
  const textColumn = document.querySelector('.text-column');

  const addButton = document.getElementById('add-button');
  const deleteButton = document.getElementById('delete-button');
  const modal = document.getElementById('file-modal');
  const closeModal = document.querySelector('.modal .close');
  const createButton = document.getElementById('create-button');
  const deleteButtonModal = document.getElementById('delete-button-modal');


  const lastFile = localStorage.getItem("lastFile");
  if (lastFile && localStorage.getItem("isFile") == 1) {
    editor.setValue(lastFile);
    updateOpenFiles();
  }
    
  const lastProjectPath = localStorage.getItem("lastProjectPath");
  if (lastProjectPath && localStorage.getItem("isFile") == 0) {
    document.getElementById('project-name').textContent = lastProjectPath.split('/').pop();
    loadProjectStructure(lastProjectPath);
  }

  function highlightLine(lineNumber) {
    const lineHandle = editor.getLineHandle(lineNumber - 1); // Les lignes de CodeMirror sont indexées à partir de 0
    editor.addLineClass(lineHandle, 'background', 'error-line');
  }
  window.changeProjectName = (projectName) => {
    const projectNameElement = document.getElementById('project-name');
    projectNameElement.textContent = projectName;
  };

  window.font_size_button_clicked = () => {
    const fontDropdown = document.getElementById('font-dropdown');
    if (fontDropdown.style.display === 'none' || fontDropdown.style.display === '') {
      fontDropdown.style.display = 'block';
    } else {
      fontDropdown.style.display = 'none';
    }
  };

  window.changeFont = () => {
    const fontSelect = document.getElementById('font-select');
    const selectedFont = fontSelect.value;
    const selectedText = getSelectedText();
    editor.replaceSelection(`#set text(font: "${selectedFont}")${selectedText}`);
    
    // Fermer la liste déroulante après sélection
    const fontDropdown = document.getElementById('font-dropdown');
    fontDropdown.style.display = 'none';
  };

  // Fonction pour récupérer le texte sélectionné
  window.getSelectedText = () => {
      const selectedText = editor.getSelection();
      //alert(`Selected text: ${selectedText}`);
      return selectedText;
  };

  window.bold_button_clicked = () => {
    const selectedText = getSelectedText();
    editor.replaceSelection(`*${selectedText}*`);
  };

  window.italic_button_clicked = () => {
    const selectedText = getSelectedText();
    editor.replaceSelection(`_${selectedText}_`);
  }


  window.underline_button_clicked = () => {
    const selectedText = getSelectedText();
    editor.replaceSelection(`#underline[${selectedText}]`);
  };

  window.math_button_clicked = () => {
    const selectedText = getSelectedText();
    editor.replaceSelection(`$${selectedText}$`); 
  };

  window.home_button_clicked = () => {
   window.location.href = '../index.html';
  };

  // Close all panels when one is opened ! 
  function closeAllPanels() {
    const filePanel = document.getElementById('file-navigation-panel');
    const settingsPanel = document.getElementById('settings-panel');
    if (settingsPanel.classList.contains('open')) {
      settingsPanel.classList.remove('open');
      textColumn.classList.remove('expanded');
    }
    if (filePanel.classList.contains('open')) {
      filePanel.classList.remove('open');
      textColumn.classList.remove('expanded');
    }
  }

  window.view_files_button_clicked = () => {
    const filePanel = document.getElementById('file-navigation-panel');
    const textColumn = document.querySelector('.text-column');
    const settingsPanel = document.getElementById('settings-panel');
    if (settingsPanel.classList.contains('open')) {
      settingsPanel.classList.remove('open');
      textColumn.classList.remove('expanded');
    }
    filePanel.classList.toggle('open');
    textColumn.classList.toggle('expanded');
  };

  window.view_shortcut_button_clicked = () => {
    closeAllPanels()
    const modal = document.getElementById('shortcut-modal');
    modal.style.display = 'block';
  };

  window.view_setting_button_clicked = () => {
    const settingsPanel = document.getElementById('settings-panel');
    const textColumn = document.querySelector('.text-column');
    const filePanel = document.getElementById('file-navigation-panel');
    if (filePanel.classList.contains('open')) {
      filePanel.classList.remove('open');
      textColumn.classList.remove('expanded');
    }
    settingsPanel.classList.toggle('open');
    textColumn.classList.toggle('expanded');
  };

  window.view_plan_button_clicked = () => {
    alert('View Plan button clicked');
  };

  window.compile_button_clicked = async () => {
    document.getElementById('error-container').innerText = '';
    editor.eachLine((lineHandle) => {
      editor.removeLineClass(lineHandle, 'background', 'error-line');
    });
    
    const code = editor.getValue();
    try {
      const response = await window.electron.compileTypst(code);
      if (response.error) {
        console.error('Error compiling Typst code:', response.error);
        document.getElementById('error-container').innerText = response.error;
        console.log(response);
        if (response.line !== null) {
          highlightLine(response.line);
        }
      } else {
        pdfViewer.src = response.pdfPath;
        editor.eachLine((lineHandle) => {
          editor.removeLineClass(lineHandle, 'background', 'error-line');
        });
      }
    } catch (error) {
      console.error('Unexpected error:', error);
      document.getElementById('error-container').innerText = error.message;
    }
  };

  compileButton.addEventListener('click', window.compile_button_clicked);
  
  function loadProjectStructure(path) {
    fetch(`http://localhost:3000/api/project-structure?path=${encodeURIComponent(path)}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => renderProjectStructure(data))
      .catch(error => console.error('Error fetching project structure:', error));
  }

  function addOpenFile(filePath) {
    if (!openFiles.includes(filePath)) {
      openFiles.push(filePath);
    }
  }

  async function openFile(filePath) {
    ActualFile = filePath;
    const requestBody = {
      path: filePath
    };

    fetch('http://localhost:8080/api/open/file', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
    })
    .then(r => {
      if (r.ok) {
        r.text().then(t => {
          editor.setValue(t);
          addOpenFile(filePath);
          updateOpenFiles(filePath);
        });
      } else {
        throw new Error('Failed to fetch the file');
      }
    })
    .catch(e => console.log("ERROR: " + e));
  }

  // Sauvegarde auto
  function saveFile() {
    if (ActualFile === undefined)
      return;
    const code = editor.getValue();
    ipcRenderer.invoke('save-file', code, ActualFile)
      .then((message) => {
        console.log(message);
      })
      .catch((error) => {
        console.error(error);
      });
  }

  setInterval(saveFile, 60000);

  function renderProjectStructure(node, parentElement) {
    const explorer = parentElement || document.getElementById('file-navigation-panel');
    if (parentElement == null) {
      explorer.innerHTML = ''; // Clear existing content if it's the root call
      
      // Create header with buttons
      const header = document.createElement('div');
      header.className = 'explorer-header';
      header.innerHTML = `
        <h2>Explorer</h2>
        <span class="button-container">
          <button id="add-button" class="icon-button">+</button>
          <button id="delete-button" class="icon-button">-</button>
        </span>
      `;
      explorer.appendChild(header);
  
      // Add event listeners for the buttons
      const addButton = header.querySelector('#add-button');
      const deleteButton = header.querySelector('#delete-button');
  
      addButton.addEventListener('click', () => {
        modal.style.display = 'block';
      });
  
      deleteButton.addEventListener('click', () => {
        modal.style.display = 'block';
      });
    }
  
    if (node.type === 'folder') {
      const folderElement = document.createElement('div');
      folderElement.className = 'folder';
      folderElement.innerHTML = `<span class="arrow collapsed"></span>${node.name}`;
      explorer.appendChild(folderElement);
  
      const childrenContainer = document.createElement('div');
      childrenContainer.className = 'children';
      childrenContainer.style.display = 'block';
      explorer.appendChild(childrenContainer);
  
      folderElement.addEventListener('click', () => {
        const arrow = folderElement.querySelector('.arrow');
        const isCollapsed = arrow.classList.contains('collapsed');
        arrow.classList.toggle('collapsed', !isCollapsed);
        arrow.classList.toggle('expanded', isCollapsed);
        childrenContainer.style.display = isCollapsed ? 'none' : 'block';
      });
  
      node.children.forEach(child => renderProjectStructure(child, childrenContainer));
    } else if (node.type === 'file') {
      const fileElement = document.createElement('div');
      fileElement.className = 'file';
      fileElement.textContent = node.name;
      fileElement.addEventListener('click', () => {
        saveFile();
        openFile(node.path);
      });
      explorer.appendChild(fileElement);
    }
  }
  

  /*------------------- SAVE BUTTON -------------------------*/
  // Save settings button
  document.getElementById('save-settings').addEventListener('click', () => {
    const projectName = document.getElementById('project-name-input').value;
    const owner = document.getElementById('owner-input').value;
    localStorage.setItem('projectName', projectName);
    localStorage.setItem('owner', owner);
    window.changeProjectName(projectName);
    alert('Settings saved');
  });

  /*------------------- DELETE BUTTON -------------------------*/
  function removeFromRecentProjects(path=lastProjectPath) {
    let projects = JSON.parse(localStorage.getItem('recentProjects')) || [];
    for (let i = 0; i < projects.length; i++) {
      if (projects[i].path === path) {
        projects.splice(i, 1);
        localStorage.setItem("recentProjects", JSON.stringify(projects.slice(0, 10)));
        return;
      }
    }
  }

  // Delete project button
  document.getElementById('delete-project').addEventListener('click', () => {
    if (confirm('Are you sure you want to delete this project?')) {
      deleteFolder(lastProjectPath).then(() => {
        localStorage.removeItem('projectName');
        localStorage.removeItem('owner');
        localStorage.removeItem('lastFile');
        localStorage.removeItem('lastProjectPath');
        window.changeProjectName('Project Name');
        editor.setValue(''); // clear editor project
        window.location.href = "../index.html";
        removeFromRecentProjects();
      })
      .catch(error => {
        console.error('Error deleting project:', error);
        alert('Failed to delete project');
      });
    }
  });

  /*---------------------- SHORT CUT ---------------------------*/
  // Close modal when the user clicks on <span> (x)
  document.querySelector('.close').addEventListener('click', () => {
    const modal = document.getElementById('shortcut-modal');
    modal.style.display = 'none';
  });

  // Close modal when the user clicks anywhere outside of the modal
  window.onclick = (event) => {
    const modal = document.getElementById('shortcut-modal');
    if (event.target === modal) {
      modal.style.display = 'none';
    }
  };
  /*------------------------------------------------------------*/
  // Implement keyboard shortcuts
  document.addEventListener('keydown', (event) => {
    if (event.ctrlKey) {
      switch (event.key) {
        case 'Enter': 
          event.preventDefault();
          compile_button_clicked();
          break;
        case 's':
          event.preventDefault();
          saveFile();
          break;
        case 'f':
          event.preventDefault();
          editor.execCommand('find');
          break;
      }
    }
  });

  /*------------------------------------------------------------*/

  function closeFile(filePath) {
    const index = openFiles.indexOf(filePath);
    if (index > -1) {
      let newFocusedFile = filePath;
      if (index > 0) {
        // Set focus to the previous file
        newFocusedFile = openFiles[index - 1];
      }
      else {
        if (openFiles.length > 1)
          newFocusedFile = openFiles[1];
      }
      openFiles.splice(index, 1);

      updateOpenFiles(newFocusedFile); // Update list and focus
  
      if (newFocusedFile === filePath) {
        editor.setValue(''); // Clear editor content if closing the current file
        ActualFile = undefined;
      }
      else {
        openFile(newFocusedFile);
        ActualFile = newFocusedFile;
      }
    }
  }

  function updateOpenFiles(focusedFilePath = null) {
    openFilesList.innerHTML = '';

    openFiles.forEach(filePath => {
      const fileName = filePath.split('/').pop();
      const fileSpan = document.createElement('div');
      fileSpan.className = 'file';
      fileSpan.textContent = fileName;

      // Add a close button element
      const closeButton = document.createElement('span');
      closeButton.className = 'close-button';
      closeButton.textContent = '×'; // Close symbol
      fileSpan.appendChild(closeButton);

      if (filePath === focusedFilePath) {
        fileSpan.classList.add('focused');
      }

      fileSpan.addEventListener('click', () => {
        saveFile();
        openFile(filePath)
      });

      // Attach click event listener to the close button
      closeButton.addEventListener('click', () => {
        event.stopPropagation();
        saveFile(); // Save the current file before closing
        closeFile(filePath);
      });

      openFilesList.appendChild(fileSpan);
    });
  }

  // Open modal for creating/deleting file/folder
  addButton.addEventListener('click', () => {
    modal.style.display = 'block';
  });

  deleteButton.addEventListener('click', () => {
    modal.style.display = 'block';
  });

  // Close modal
  closeModal.addEventListener('click', () => {
    modal.style.display = 'none';
  });

  window.onclick = (event) => {
    if (event.target === modal) {
      modal.style.display = 'none';
    }
  };

  async function createFile(path) {
    // Implement your file creation logic here
    return fetch('http://localhost:8080/api/create/file', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ path }),
    });
  }
  
  async function deleteFile(path) {
    // Implement your file deletion logic here
    return fetch('http://localhost:8080/api/delete/file', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ path }),
    });
  }
  
  async function createFolder(path) {
    // Implement your folder creation logic here
    return fetch('http://localhost:8080/api/create/folder', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ path }),
    });
  }
  
  async function deleteFolder(path) {
    // Implement your folder deletion logic here
    return fetch('http://localhost:8080/api/delete/folder', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ path }),
    });
  }  

  // Handle create button click
  createButton.addEventListener('click', () => {
    const type = document.getElementById('file-folder-type').value;
    const path = document.getElementById('file-folder-path').value;
    
    if (type === 'file') {
      createFile(path).then(() => {
        openFile(path);
        loadProjectStructure(lastProjectPath);
      }).catch(error => console.error('Error creating file:', error));
    } else {
      createFolder(path).then(() => {
        loadProjectStructure(lastProjectPath);
      }).catch(error => console.error('Error creating folder:', error));
    }

    modal.style.display = 'none';
  });

  // Handle delete button click
  deleteButtonModal.addEventListener('click', () => {
    const type = document.getElementById('file-folder-type').value;
    const path = document.getElementById('file-folder-path').value;

    if (type === 'file') {
      deleteFile(path).then(() => {
        loadProjectStructure(lastProjectPath);
      }).catch(error => console.error('Error deleting file:', error));
    } else {
      deleteFolder(path).then(() => {
        loadProjectStructure(lastProjectPath);
      }).catch(error => console.error('Error deleting folder:', error));
    }

    modal.style.display = 'none';
  });

});
