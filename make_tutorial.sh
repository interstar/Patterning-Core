#!/bin/bash

# Hardcoded tutorial root path - change this to match your deployment
TUTORIAL_ROOT="http://localhost:8000"

# Create tutorial output directory if it doesn't exist
mkdir -p tutorial/out

# Copy workbench to tutorial output directory
echo "Copying workbench to tutorial output directory..."
cp -r workbench tutorial/out/

# Copy shared tutorial stylesheet
cp tutorial/tutorial.css tutorial/out/tutorial.css

# This is my Virtual Env ... you might have a different one or none at all
source ~/.main_python_venv/bin/activate

# Process each markdown file in the tutorial sources directory
for md_file in tutorial/sources/*.md; do
    # Skip if no markdown files found
    [ -e "$md_file" ] || { echo "No markdown files found in tutorial/sources directory"; exit 1; }
    
    # Get the base name without extension
    base_name=$(basename "$md_file" .md)
    
    # Generate the HTML file directly in tutorial/out directory
    echo "Processing $md_file..."
    python3 tutorial/generate_pattern_page.py "$md_file" "tutorial/out/${base_name}.html" "$TUTORIAL_ROOT"
done

echo "Tutorial build complete!" 
