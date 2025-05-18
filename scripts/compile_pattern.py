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
    os.makedirs("patterns", exist_ok=True)
    os.makedirs("dist/patterns", exist_ok=True)
    os.makedirs("target/pattern-build", exist_ok=True)

def compile_pattern(pattern_file, pattern_name):
    """Compile a pattern file into a standalone JS bundle"""
    # Ensure directories exist
    ensure_dirs()
    
    # Create pattern directory if it doesn't exist
    pattern_dir = Path("patterns")
    pattern_dir.mkdir(exist_ok=True)
    
    # Get the source and destination paths
    source_path = Path(pattern_file)
    if not source_path.suffix:
        source_path = source_path.with_suffix('.cljs')
    
    dest_path = pattern_dir / f"{pattern_name}.cljs"
    
    print(f"Source path: {source_path.absolute()}")
    print(f"Destination path: {dest_path.absolute()}")
    print(f"Source exists: {source_path.exists()}")
    print(f"Destination exists: {dest_path.exists()}")
    
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
        
        print(f"Environment PATTERN_NAME: {env['PATTERN_NAME']}")
        
        # Run lein cljsbuild
        try:
            # First clean any previous builds
            subprocess.run(["lein", "clean"], check=True, env=env)
            
            # Then run the build
            result = subprocess.run(["lein", "cljsbuild", "once", "pattern"], 
                                  check=True,
                                  capture_output=True,
                                  text=True,
                                  env=env)
            
            print(f"Successfully compiled pattern to dist/patterns/{pattern_name}.js")
            
            # If compilation succeeded, copy the JS file to dist/patterns directory
            js_file = os.path.join("dist", "patterns", f"{pattern_name}.js")
            print(f"Looking for JS file at: {js_file}")
            print(f"JS file exists: {os.path.exists(js_file)}")
            
            if os.path.exists(js_file):
                # Copy the pattern renderer
                renderer_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "pattern_renderer.js")
                if os.path.exists(renderer_path):
                    shutil.copy2(renderer_path, os.path.join("dist", "patterns", "pattern_renderer.js"))
                else:
                    print(f"Warning: pattern_renderer.js not found at {renderer_path}")
                
                # Read the HTML template
                template_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "scripts", "pattern_template.html")
                print(f"Looking for HTML template at: {template_path}")
                print(f"Template exists: {os.path.exists(template_path)}")
                
                if not os.path.exists(template_path):
                    print(f"Error: HTML template not found at {template_path}")
                    return False
                
                with open(template_path, 'r') as f:
                    html_content = f.read()
                
                # Update the script src in the HTML to point to the correct JS file
                html_content = html_content.replace('src="{{pattern_name}}.js"', f'src="{pattern_name}.js"')
                
                # Update the script block to use the correct pattern name
                html_content = html_content.replace('window["{{pattern_name}}"]', f'window["{pattern_name}"]')
                
                # Write the HTML file to dist/patterns
                html_file = os.path.join("dist", "patterns", f"{pattern_name}.html")
                print(f"Writing HTML file to: {html_file}")
                
                try:
                    with open(html_file, 'w') as f:
                        f.write(html_content)
                    print(f"Successfully wrote HTML file to {html_file}")
                except Exception as e:
                    print(f"Error writing HTML file: {str(e)}")
                    return False
                
                print(f"\nPattern compiled successfully!")
                print(f"Output files created in: dist/patterns/")
                print(f"Open dist/patterns/{pattern_name}.html in your browser to view the pattern.")
                return True
            else:
                print(f"Error: Compiled JS file not found at {js_file}")
                return False
            
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
    parser = argparse.ArgumentParser(description="Compile a Patterning pattern into a standalone JS bundle")
    parser.add_argument("pattern_file", help="Path to the pattern file")
    parser.add_argument("--name", help="Name for the pattern (defaults to filename without extension)")
    
    args = parser.parse_args()
    
    # Get pattern name from filename if not provided
    pattern_name = args.name or Path(args.pattern_file).stem
    
    success = compile_pattern(args.pattern_file, pattern_name)
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main() 