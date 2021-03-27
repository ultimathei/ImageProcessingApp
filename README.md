## Getting Started

Welcome to my Image Processing Application university project!

This project was done for the Image Processing Modlue at Queenmary university of London in 2021.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies // this folder is not pushed to the repo (see dependencies for more info)

## Dependency Management

This project requires javafx libraries in order to work as expected.
I have included the follwing launch file in .vscode (hidden) folder:

{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch App",
            "request": "launch",
            "vmArgs": "--module-path /Users/ultimathei/macDocs/code_local/ImageProcessingApp/lib --add-modules javafx.controls,javafx.fxml",
            "mainClass": "app.App",
            "projectName": "ImageProcessingApp_10d8e403"
        },
    ]
}

