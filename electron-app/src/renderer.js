const { ipcRenderer, getMainWindow } = window.electron;

const newProjectButton = document.getElementById('newProjectButton');
const openFileButton = document.getElementById('openFileButton');
const openFolderButton = document.getElementById('openFolderButton');
const searchInput = document.getElementById('searchInput');
const projectsList = document.getElementById('projectsList');

document.addEventListener('DOMContentLoaded', () => {
  // update the recent projects on the page
  updateRecentProjectsUI();

  searchInput.addEventListener('input', () => {
    const query = searchInput.value.toLowerCase();
    const projects = projectsList.querySelectorAll('.project-item');
    console.log(query);

    if (query === "") {
      projects.forEach(project => {
        project.style.display = 'block';
      });
    } else {
      for (let i=0; i < projects.length; i++) {
        if (projects[i].textContent.split(" -")[0].toLowerCase().includes(query)) {
          projects[i].style.display = 'block';
        } else {
          projects[i].style.display = 'none';
        }
      }
    }
  });
});

newProjectButton.addEventListener('click', async () => {
  const files = await ipcRenderer.invoke('show-open-dialog', { properties: ['openDirectory'] });
  if (!files.canceled) {
    projectNameModal.style.display = 'block';
    saveProjectNameButton.onclick = async () => {
      const projectName = projectNameInput.value.trim();
      if (projectName) {
        const projectPath = `${files.filePaths[0]}/${projectName}`;
        const requestBody = { path: `${files.filePaths[0]}/${projectName}` };
        fetch('http://localhost:8080/api/create/folder', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(requestBody),
        }).then(r => {
          // save project on recent projects before loading the project
          saveRecentProject({ name: projectName, path: projectPath });
          localStorage.setItem("lastProjectPath", projectPath);
          localStorage.setItem("isFile", 0);
          projectNameModal.style.display = 'none';
          // update the recent project page
          updateRecentProjectsUI();
          openProject(projectPath);
        })
        .catch(e => console.log(e));
      } else {
        alert('Enter a project name.');
      }
    };
  }
});

// button to save project name
saveProjectNameButton.onclick = () => {
  console.log(projectName);
  projectNameModal.style.display = 'none';
};

// quits the project name creation pop up window
window.onclick = (event) => {
  if (event.target === projectNameModal) {
    projectNameModal.style.display = 'none';
  }
};

// quits the project name creation pop up window
closeCross.onclick = (event) => {
  projectNameModal.style.display = 'none';
};

openFileButton.addEventListener('click', async () => {
  const files = await ipcRenderer.invoke('show-open-dialog', { properties: ['openFile'] });
  if (!files.canceled) {
    const requestBody = {
      path: files.filePaths[0]
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
        return r.text();
      }
      throw new Error('Failed to fetch the file');
    })
    .then(async (t) => {
      console.log(t);
      localStorage.setItem("lastFile", t);
      localStorage.setItem("isFile", 1);
      window.location.href = 'code_page/index.html';
    })
    .catch(e => console.log("ERROR: " + e));
  }
});

function openProject(projectPath) {
  const requestBody = { path: projectPath };
    fetch('http://localhost:8080/api/open/project', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody),
    })
    .then(r => {
      if (r.ok) return r.text();
      throw new Error('Failed to fetch the project');
    })
    .then(async (t) => {
      console.log(t);
      saveRecentProject({ name: projectPath.split('/').pop(), path: projectPath });
      console.log('okk');
      localStorage.setItem("lastProjectPath", projectPath);
      localStorage.setItem("isFile", 0);
      updateRecentProjectsUI();
      window.location.href = 'code_page/index.html';
    })
    .catch(e => console.log("ERROR: " + e));
}

openFolderButton.addEventListener('click', async () => {
  const files = await ipcRenderer.invoke('show-open-dialog', { properties: ['openDirectory'] });
  if (!files.canceled) {
    const projectPath = files.filePaths[0];
    openProject(projectPath);
  }
});

function saveRecentProject(project) {
  // get recent projects on storage
  let recentProjects = JSON.parse(localStorage.getItem('recentProjects')) || [];
  recentProjects = recentProjects.filter(p => p.path !== project.path); // Remove duplicates
  recentProjects.unshift(project); // Add the new project to the beginning
  localStorage.setItem('recentProjects', JSON.stringify(recentProjects.slice(0, 10))); // Keep only the last 10 projects
}

function loadRecentProjects() {
  const recent = localStorage.getItem('recentProjects');
  if (recent === "" || !recent)
    return [];
  return JSON.parse(recent);
}

function updateRecentProjectsUI() {
  const recentProjects = loadRecentProjects();
  // get the project list section on the html file
  const projectsList = document.getElementById('projectsList');
  projectsList.innerHTML = '';
  // append each project to the list in a div with project name and path
  recentProjects.forEach(project => {
    const projectItem = document.createElement('div');
    projectItem.className = 'project-item';
    projectItem.textContent = `${project.name} - ${project.path}`;
    // set a function to call on click
    projectItem.onclick = () => openProject(project.path);
    projectsList.appendChild(projectItem);
  });
}