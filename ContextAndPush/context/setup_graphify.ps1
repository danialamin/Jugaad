# Graphify Installation and Execution Script
# This will install Graphify, generate the graph, and move it to this context folder.

# Change to the project root directory
Set-Location -Path "$PSScriptRoot\..\.."

echo "Generating Graphify Knowledge Graph for CampusFlex..."
python -m graphify update .

# Move the output folder inside the context folder for cleanliness
if (Test-Path -Path ".\graphify-out") {
    if (Test-Path -Path ".\ContextAndPush\context\graphify-out") {
        Remove-Item -Recurse -Force ".\ContextAndPush\context\graphify-out"
    }
    Move-Item -Path ".\graphify-out" -Destination ".\ContextAndPush\context\graphify-out" -Force
    echo "Graphify output moved to ContextAndPush/context/graphify-out."
}

echo "Graphify generation complete! The context graph is ready."
