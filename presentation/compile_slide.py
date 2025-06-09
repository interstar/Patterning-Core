#!/usr/bin/env python3

import os
import sys
import shutil
import subprocess
import argparse
from pathlib import Path
import re

def ensure_dirs():
    """Ensure required directories exist"""
    os.makedirs("presentation/patterns", exist_ok=True)
    os.makedirs("presentation/slides", exist_ok=True)

def copy_main_js():
    """Copy the main Patterning library JS file"""
    main_js = Path("browser-based/js/main.js")
    if not main_js.exists():
        print(f"Error: main.js not found at {main_js}")
        return False
    
    target_dir = Path("presentation/slides")
    
    try:
        shutil.copy2(main_js, target_dir / "main.js")
        return True
    except Exception as e:
        print(f"Error copying main.js: {str(e)}")
        return False

def extract_metadata(pattern_file):
    """Extract metadata from pattern file comments and extract code between PATTERN START/END markers if present."""
    metadata = {
        'title': 'Untitled Pattern',
        'description': '',
        'tags': []
    }
    
    with open(pattern_file, 'r') as f:
        content = f.read()
        
        # Extract title
        title_match = re.search(r';;\s*Title:\s*(.+)$', content, re.MULTILINE)
        if title_match:
            metadata['title'] = title_match.group(1).strip()
            
        # Extract description
        desc_match = re.search(r';;\s*Description:\s*(.+)$', content, re.MULTILINE)
        if desc_match:
            metadata['description'] = desc_match.group(1).strip()
            
        # Extract tags
        tags_match = re.search(r';;\s*Tags:\s*(.+)$', content, re.MULTILINE)
        if tags_match:
            metadata['tags'] = [tag.strip() for tag in tags_match.group(1).split(',')]

        # Extract code between PATTERN START and PATTERN END
        pattern_start = re.search(r'^;;\s*PATTERN START.*$', content, re.MULTILINE)
        pattern_end = re.search(r'^;;\s*PATTERN END.*$', content, re.MULTILINE)
        if pattern_start and pattern_end:
            start_idx = pattern_start.end()
            end_idx = pattern_end.start()
            code = content[start_idx:end_idx].strip('\n')
        else:
            code = content
    
    return metadata, code

def compile_pattern(pattern_file, pattern_name):
    """Compile a pattern file into a slide"""
    # Ensure directories exist
    ensure_dirs()
    
    # Create pattern directory if it doesn't exist
    pattern_dir = Path("presentation/patterns")
    pattern_dir.mkdir(exist_ok=True)
    
    # Get the source and destination paths
    source_path = Path(pattern_file)
    if not source_path.suffix:
        source_path = source_path.with_suffix('.cljs')
    
    dest_path = pattern_dir / f"{pattern_name}.cljs"
    
    # Only copy if the file is not already in the patterns directory
    if source_path.parent != pattern_dir:
        shutil.copy2(source_path, dest_path)
        should_cleanup = True
    else:
        should_cleanup = False
    
    try:
        # Set up environment with pattern name
        env = os.environ.copy()
        env["PATTERN_NAME"] = pattern_name
        
        # Run lein cljsbuild
        try:
            # First clean any previous builds
            subprocess.run(["lein", "clean"], check=True, env=env)
            
            # Create a temporary project.clj with pattern name substituted
            with open("project.clj", "r") as f:
                project_content = f.read()
            
            # Substitute pattern name in project.clj
            project_content = project_content.replace("{{pattern_name}}", pattern_name)
            
            with open("project.clj.tmp", "w") as f:
                f.write(project_content)
            
            try:
                # Run the pattern build with the temporary project.clj
                pattern_result = subprocess.run(["lein", "with-profile", "+dev", "cljsbuild", "once", "presentation-pattern"], 
                                      check=True,
                                      capture_output=True,
                                      text=True,
                                      env=env)
                
                # Move the generated files to their correct locations
                slides_dir = Path("presentation/slides")
                temp_js = slides_dir / "{{pattern_name}}.js"
                temp_map = slides_dir / "{{pattern_name}}.js.map"
                
                if temp_js.exists():
                    # Fix source map URL in JS file
                    with open(temp_js, 'r') as f:
                        js_content = f.read()
                    js_content = js_content.replace("{{pattern_name}}.js.map", f"{pattern_name}.js.map")
                    with open(temp_js, 'w') as f:
                        f.write(js_content)
                    
                    # Move JS file
                    shutil.move(temp_js, slides_dir / f"{pattern_name}.js")
                    # Move source map
                    if temp_map.exists():
                        shutil.move(temp_map, slides_dir / f"{pattern_name}.js.map")
                
                # If compilation succeeded, create the slide HTML
                js_file = slides_dir / f"{pattern_name}.js"
                
                if js_file.exists():
                    # Copy main.js if it doesn't exist
                    if not (slides_dir / "main.js").exists():
                        if not copy_main_js():
                            return False
                    
                    # Extract metadata from pattern file
                    metadata, source_code = extract_metadata(dest_path)
                    
                    # Read the slide template
                    template_path = Path(os.path.dirname(os.path.abspath(__file__))) / "slide_template.html"
                    
                    if not template_path.exists():
                        print(f"Error: Slide template not found at {template_path}")
                        return False
                    
                    with open(template_path, 'r') as f:
                        html_content = f.read()
                    
                    # Update the template with metadata and file paths
                    html_content = html_content.replace('{{title}}', metadata['title'])
                    html_content = html_content.replace('{{description}}', metadata['description'])
                    html_content = html_content.replace('{{pattern_name}}', pattern_name)
                    html_content = html_content.replace('{{source_code}}', source_code)
                    
                    # Write the HTML file to slides directory
                    html_file = slides_dir / f"{pattern_name}.html"
                    
                    try:
                        with open(html_file, 'w') as f:
                            f.write(html_content)
                    except Exception as e:
                        print(f"Error writing HTML file: {str(e)}")
                        return False
                    
                    print(f"\nSlide compiled successfully!")
                    print(f"Output files created in: presentation/slides/")
                    return True
                else:
                    print(f"Error: Compiled JS file not found at {js_file}")
                    return False
                
            finally:
                # Clean up temporary project.clj
                if os.path.exists("project.clj.tmp"):
                    os.remove("project.clj.tmp")
                
        except subprocess.CalledProcessError as e:
            print(f"Error compiling pattern:")
            print(e.stdout)
            print(e.stderr)
            sys.exit(1)
            
    finally:
        # Clean up pattern file only if we copied it
        if should_cleanup and dest_path.exists():
            dest_path.unlink()

def main():
    parser = argparse.ArgumentParser(description="Compile a Patterning pattern into a presentation slide")
    parser.add_argument("pattern_file", help="Path to the pattern file")
    parser.add_argument("--name", help="Name for the pattern (defaults to filename without extension)")
    parser.add_argument("--no-compile", action="store_true", help="Skip ClojureScript compilation step")
    args = parser.parse_args()

    pattern_name = args.name or Path(args.pattern_file).stem

    # Copy main.js
    if not copy_main_js():
        return 1

    # Compile the pattern unless --no-compile is set
    if not args.no_compile:
        success = compile_pattern(args.pattern_file, pattern_name)
        if not success:
            return 1
    else:
        # Still generate the HTML slide using the latest JS and pattern source
        slides_dir = Path("presentation/slides")
        dest_path = Path("presentation/patterns") / f"{pattern_name}.cljs"
        if not dest_path.exists():
            dest_path = Path(args.pattern_file)
        template_path = Path(os.path.dirname(os.path.abspath(__file__))) / "slide_template.html"
        if not template_path.exists():
            print(f"Error: Slide template not found at {template_path}")
            return 1
        with open(template_path, 'r') as f:
            html_content = f.read()
        metadata, source_code = extract_metadata(dest_path)
        html_content = html_content.replace('{{title}}', metadata['title'])
        html_content = html_content.replace('{{description}}', metadata['description'])
        html_content = html_content.replace('{{pattern_name}}', pattern_name)
        html_content = html_content.replace('{{source_code}}', source_code)
        html_file = slides_dir / f"{pattern_name}.html"
        with open(html_file, 'w') as f:
            f.write(html_content)
        print(f"Slide HTML regenerated (no ClojureScript compile): {html_file}")
    return 0

if __name__ == "__main__":
    main() 