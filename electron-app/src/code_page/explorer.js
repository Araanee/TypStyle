const express = require('express');
const path = require('path');
const fs = require('fs');
const app = express();

app.use(express.static(__dirname));

app.get('/api/project-structure', (req, res) => {
    const projectPath = req.query.path ? path.resolve(req.query.path) : "";
    if (!projectPath)
        return res.status(400).send('Path is required');
    const directoryTree = readDirectory(projectPath);
    res.json(directoryTree);
});

function readDirectory(dirPath) {
    const result = { name: path.basename(dirPath), type: 'folder', children: [] };
    const files = fs.readdirSync(dirPath);
    files.forEach(file => {
        const filePath = path.join(dirPath, file);
        const stat = fs.statSync(filePath);
        if (stat.isDirectory()) {
        result.children.push(readDirectory(filePath));
        } else {
        result.children.push({ name: file, type: 'file', path: filePath });
        }
    });
    return result;
}

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));